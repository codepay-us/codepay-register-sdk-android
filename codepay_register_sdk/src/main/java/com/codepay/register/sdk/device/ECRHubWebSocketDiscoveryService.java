package com.codepay.register.sdk.device;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codepay.register.sdk.client.ECRHubClient;
import com.codepay.register.sdk.listener.ECRHubPairListener;
import com.codepay.register.sdk.listener.ECRHubResponseCallBack;
import com.codepay.register.sdk.util.Constants;
import com.codepay.register.sdk.util.ECRHubMessageData;
import com.codepay.register.sdk.util.NetUtils;
import com.codepay.register.sdk.util.SharePreferenceUtil;

import org.java_websocket.WebSocket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * WLAN discovery for the register (ECR) side.
 *
 * <p>The register announces itself on the LAN as {@code _ecr-hub-client._tcp.local.} via
 * JmDNS and runs a local websocket server that terminals connect to for pairing.</p>
 *
 * <p>Notes on the implementation, all of them learned the hard way:</p>
 * <ul>
 *   <li>An Android Wi-Fi driver silently drops incoming multicast (mDNS) packets unless a
 *       {@link WifiManager.MulticastLock} is held. Without the lock this device never sees
 *       the terminal's browse queries and can therefore never answer them - the terminal
 *       "almost never" receives our broadcast.</li>
 *   <li>JmDNS announces a registered service only 3 times (~4 seconds after registration)
 *       and then stays silent for half an hour (renewal = 50% of the 3600s TTL). A terminal
 *       that starts browsing later has to rely on its query being answered. To make
 *       discovery robust the service is re-announced every few seconds with a stable
 *       instance name through an unregister/register cycle (JmDNS has no public
 *       "announce now" API).</li>
 *   <li>{@code JmDNS.list()} blocks for its full timeout (6s by default). It must never be
 *       called on the websocket server thread ({@code onStart()} runs there before the
 *       accept loop starts) nor on the main thread - blocking it made the server refuse
 *       terminal connections for seconds.</li>
 * </ul>
 */
public class ECRHubWebSocketDiscoveryService implements OnServerCallback, ServiceListener {

    private static final String TAG = "ECRHubDiscoveryService";

    private final static String REMOTE_CLIENT_TYPE = "_ecr-hub-client._tcp.local.";

    public final static String REMOTE_SERVER_TYPE = "_ecr-hub-server._tcp.local.";

    /**
     * Interval of the mDNS announce cycle. Every cycle emits a burst of 3 announcements
     * for our service, so a terminal listening at any point in time sees us within one
     * interval even if it missed the initial announcements or its browse queries are lost.
     */
    private static final long ANNOUNCE_INTERVAL_MS = 5000L;

    /**
     * Interval of the terminal discovery poll. The poll re-queries {@code _ecr-hub-server}
     * so that {@link #serviceResolved(ServiceEvent)} fires even when the terminal's own
     * announcement was missed, refreshing the stored address of paired terminals.
     */
    private static final long DISCOVERY_POLL_INTERVAL_MS = 15000L;
    private static final long DISCOVERY_POLL_TIMEOUT_MS = 1500L;

    /**
     * Timeout of the initial {@code _ecr-hub-server} scan. Runs on the worker thread,
     * never on a thread that must stay responsive.
     */
    private static final long INITIAL_LIST_TIMEOUT_MS = 3000L;

    private static final int PORT_RETRY_MAX = 10;
    private static final int IP_WAIT_RETRIES = 10;
    private static final long IP_WAIT_INTERVAL_MS = 1000L;

    private static volatile int PORT = 35779;

    /**
     * Single worker for all mDNS setup/teardown work. All blocking JmDNS calls happen
     * here so they can never block the UI or websocket threads.
     */
    private static final ExecutorService MDNS_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "ECRHub-MDNS-" + count.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    });

    private final Context context;
    private String deviceName = "";
    ECRHubWebSocketServer socketServer;
    private JmDNS mJmdns;
    private volatile boolean isServerStart = false;
    private volatile boolean isServerStarting = false;
    private ECRHubPairListener pairListener;

    private WifiManager.MulticastLock multicastLock;

    private WebSocket connection;

    /**
     * Bumped whenever a websocket server is created or stopped. Callbacks carry the
     * generation they belong to, so events from a replaced or stopped server are ignored
     * instead of clobbering the state of the current one.
     */
    private final AtomicInteger serverGeneration = new AtomicInteger(0);
    private volatile int portRetryCount = 0;

    /**
     * Stable suffix of the mDNS instance name, generated once per run. Re-registering
     * with a new name every time (the old behaviour used a timestamp suffix) forced
     * terminals to forget the previous instance and resolve a brand new one over and
     * over again.
     */
    private String serviceInstanceSuffix;

    private volatile boolean announceRunning = false;
    private Thread announceThread;

    private volatile boolean discoveryPollRunning = false;
    private Thread discoveryPollThread;

    public ECRHubWebSocketDiscoveryService(Context context) {
        // keep the application context: this object usually outlives activities
        Context appContext = context.getApplicationContext();
        this.context = appContext != null ? appContext : context;
        SharePreferenceUtil.init(context);
    }

    public void start(String name, ECRHubPairListener listener) {
        if (name == null || name.isEmpty()) {
            name = Build.MODEL;
        }
        deviceName = name;
        pairListener = listener;
        startSocketServer();
    }

    public void start(ECRHubPairListener listener) {
        start(Build.MODEL, listener);
    }

    private synchronized void startSocketServer() {
        if (isServerStart || isServerStarting) {
            return;
        }
        acquireMulticastLock();
        isServerStarting = true;
        createAndStartServer();
    }

    private void createAndStartServer() {
        final int generation = serverGeneration.incrementAndGet();
        ECRHubWebSocketServer server = new ECRHubWebSocketServer(new InetSocketAddress(PORT), new OnServerCallback() {
            @Override
            public void onServerStart() {
                if (generation == serverGeneration.get()) {
                    handleServerStarted(generation);
                }
            }

            @Override
            public void onError(String errorMsg) {
                if (generation == serverGeneration.get()) {
                    handleServerError(generation, errorMsg);
                }
            }

            @Override
            public void onClose() {
                if (generation == serverGeneration.get()) {
                    Log.d(TAG, "websocket server closed");
                }
            }

            @Override
            public void onMessageReceived(WebSocket connection, String message) {
                if (generation == serverGeneration.get()) {
                    ECRHubWebSocketDiscoveryService.this.onMessageReceived(connection, message);
                }
            }
        });
        socketServer = server;
        server.start();
    }

    public void stop() {
        isServerStart = false;
        isServerStarting = false;
        serverGeneration.incrementAndGet();
        final ECRHubWebSocketServer server = socketServer;
        socketServer = null;
        connection = null;
        if (server != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (Exception e) {
                        Log.w(TAG, "websocket server stop failed: " + e);
                    }
                }
            }, "ECRHub-ServerStop");
            thread.setDaemon(true);
            thread.start();
        }
        MDNS_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                teardownJmDNS();
            }
        });
    }

    public void unPair(ECRHubDevice device, ECRHubResponseCallBack callBack) {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        if (!deviceList.isEmpty()) {
            JSONArray array = JSON.parseArray(deviceList);
            for (int i = 0; i < array.size(); i++) {
                ECRHubDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), ECRHubDevice.class);
                if (deviceData.getName() != null && deviceData.getName().equals(device.getName())) {
                    array.remove(i);
                    break;
                }
            }
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
        if (ECRHubClient.getInstance().isConnected()) {
            if (deviceName == null || deviceName.isEmpty()) {
                deviceName = Build.MODEL;
            }
            InetAddress ip = NetUtils.getLocalIPAddress();
            if (ip == null) {
                Log.w(TAG, "unPair: no local IPv4 address, cannot notify terminal");
                return;
            }
            device.setName(deviceName);
            device.setWs_address(NetUtils.getMacAddress(context));
            device.setPort(String.valueOf(PORT));
            device.setIp_address(ip.getHostAddress());
            ECRHubClient.getInstance().requestUnPair(device, callBack);
        }
    }

    public void deletePairList(ECRHubMessageData data) {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        ECRHubDevice device = new ECRHubDevice();
        device.setPort(data.getDevice_data().getPort());
        device.setIp_address(data.getDevice_data().getIp_address());
        if (null != data.getDevice_data().getAlias_name() && !"".equals(data.getDevice_data().getAlias_name())) {
            device.setTerminal_sn(data.getDevice_data().getAlias_name());
        } else {
            device.setTerminal_sn(data.getDevice_data().getDevice_name());
        }
        device.setWs_address(data.getDevice_data().getMac_address());
        device.setName(data.getDevice_data().getDevice_name());
        if (!deviceList.isEmpty()) {
            JSONArray array = JSON.parseArray(deviceList);
            for (int i = 0; i < array.size(); i++) {
                ECRHubDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), ECRHubDevice.class);
                if (deviceData.getName() != null && deviceData.getName().equals(device.getName())) {
                    array.remove(i);
                    break;
                }
            }
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
    }

    // ---------------------------------------------------------------------
    // websocket server lifecycle
    // ---------------------------------------------------------------------

    @Override
    public void onServerStart() {
        // invoked through the per-server wrapper (with generation check); kept public
        // for OnServerCallback compatibility
        handleServerStarted(serverGeneration.get());
    }

    private void handleServerStarted(final int generation) {
        // stop() may have run between the wrapper's check and this call
        if (generation != serverGeneration.get()) {
            return;
        }
        Log.i(TAG, "websocket server started on port " + PORT);
        isServerStart = true;
        isServerStarting = false;
        portRetryCount = 0;
        // Never do blocking work here: this callback runs on the websocket selector
        // thread before its accept loop starts, and while it is busy the server can
        // not accept any terminal connection. All mDNS work goes to the worker.
        MDNS_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (generation == serverGeneration.get()) {
                    setupJmDNS();
                }
            }
        });
    }

    private void handleServerError(int generation, String errorMsg) {
        if (generation != serverGeneration.get()) {
            return;
        }
        Log.e(TAG, "websocket server error: " + errorMsg);
        isServerStart = false;
        isServerStarting = false;
        if (portRetryCount >= PORT_RETRY_MAX) {
            Log.e(TAG, "websocket server: port retries exhausted, giving up");
            return;
        }
        portRetryCount++;
        PORT += 1;
        isServerStarting = true;
        createAndStartServer();
    }

    @Override
    public void onError(String errorMsg) {
        // invoked through the per-server wrapper (with generation check); kept public
        // for OnServerCallback compatibility
        handleServerError(serverGeneration.get(), errorMsg);
    }

    @Override
    public void onClose() {
        // A closed pairing connection is not a server shutdown. Previously this reset
        // the server state whenever a terminal closed its websocket, leaving the
        // discovery service inconsistent while the server kept running.
    }

    @Override
    public void onMessageReceived(WebSocket connection, String message) {
        Log.i(TAG, "message received: " + message);
        this.connection = connection;
        ECRHubMessageData data = JSON.parseObject(message, ECRHubMessageData.class);
        if (data == null || data.getTopic() == null || data.getDevice_data() == null) {
            return;
        }
        if (Constants.ECR_HUB_TOPIC_UNPAIR.equals(data.getTopic())) {
            data.setResponse_code("000");
            connection.send(JSON.toJSON(data).toString());
            if (null != pairListener) {
                pairListener.onDeviceUnpair(data);
            }
            deletePairList(data);
        } else {
            if (null != pairListener) {
                pairListener.onDevicePair(data, "ws://" + data.getDevice_data().getIp_address() + ":" + data.getDevice_data().getPort());
            }
        }
    }

    // ---------------------------------------------------------------------
    // mDNS: announcing ourselves and discovering terminals
    // ---------------------------------------------------------------------

    private void setupJmDNS() {
        acquireMulticastLock();
        if (mJmdns != null) {
            // restarted after stop(): announcements are driven by the announce thread
            startAnnounceThread();
            startDiscoveryPollThread();
            return;
        }
        InetAddress ip = waitForLocalIPAddress();
        if (ip == null) {
            Log.e(TAG, "no local IPv4 address available, mDNS not started");
            return;
        }
        try {
            long begin = SystemClock.elapsedRealtime();
            mJmdns = JmDNS.create(ip, "ECRHubServerName");
            Log.i(TAG, "JmDNS created in " + (SystemClock.elapsedRealtime() - begin) + " ms");
        } catch (Exception e) {
            Log.e(TAG, "JmDNS.create failed", e);
            mJmdns = null;
            return;
        }
        try {
            mJmdns.addServiceListener(REMOTE_SERVER_TYPE, this);
        } catch (Exception e) {
            Log.e(TAG, "addServiceListener failed", e);
        }
        announceOnce();
        startAnnounceThread();
        startDiscoveryPollThread();
        try {
            // Seed the discovery of terminals that are already running. list() blocks,
            // which is fine here: this runs on the worker thread.
            ServiceInfo[] services = mJmdns.list(REMOTE_SERVER_TYPE, INITIAL_LIST_TIMEOUT_MS);
            Log.i(TAG, "initial mDNS scan found " + (services == null ? 0 : services.length) + " terminal(s)");
        } catch (Exception e) {
            Log.w(TAG, "initial mDNS scan failed: " + e);
        }
    }

    private void teardownJmDNS() {
        stopAnnounceThread();
        stopDiscoveryPollThread();
        releaseMulticastLock();
        JmDNS jmdns = mJmdns;
        mJmdns = null;
        if (jmdns == null) {
            return;
        }
        try {
            jmdns.removeServiceListener(REMOTE_SERVER_TYPE, this);
        } catch (Exception e) {
            Log.w(TAG, "removeServiceListener failed: " + e);
        }
        try {
            // sends the goodbye packets so terminals drop us immediately
            jmdns.unregisterAllServices();
        } catch (Exception e) {
            Log.w(TAG, "unregisterAllServices failed: " + e);
        }
        try {
            jmdns.close();
        } catch (Exception e) {
            Log.w(TAG, "JmDNS close failed: " + e);
        }
    }

    /**
     * One mDNS announce cycle for our {@code _ecr-hub-client} service.
     *
     * <p>JmDNS announces a newly registered service 3 times and then goes quiet for half
     * an hour, and it has no public "announce now" API. Re-registering the service is the
     * only way to force a fresh burst of announcements. The instance name is kept stable
     * so that terminals refresh their cache entry instead of having to forget the old
     * instance and resolve a brand new one.</p>
     */
    private boolean announceOnce() {
        JmDNS jmdns = mJmdns;
        if (jmdns == null) {
            return false;
        }
        ServiceInfo serviceInfo = buildServiceInfo();
        if (serviceInfo == null) {
            return false;
        }
        try {
            jmdns.unregisterAllServices();
        } catch (Exception e) {
            Log.w(TAG, "unregisterAllServices failed: " + e);
        }
        try {
            jmdns.registerService(serviceInfo);
            Log.d(TAG, "announced mDNS service " + serviceInfo.getName() + " on port " + PORT);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "registerService failed", e);
            return false;
        }
    }

    private ServiceInfo buildServiceInfo() {
        InetAddress ip = NetUtils.getLocalIPAddress();
        if (ip == null) {
            Log.w(TAG, "no local IPv4 address, announce skipped");
            return null;
        }
        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = Build.MODEL;
        }
        if (serviceInstanceSuffix == null || serviceInstanceSuffix.isEmpty()) {
            serviceInstanceSuffix = Integer.toHexString((int) (SystemClock.elapsedRealtime() & 0xFFFFFF))
                    + "-" + PORT;
        }
        JSONObject clientInfo = new JSONObject();
        clientInfo.put("mac_address", NetUtils.getMacAddress(context));
        clientInfo.put("ip_address", ip.getHostAddress() + ":" + PORT);
        clientInfo.put("name", deviceName);
        return ServiceInfo.create(REMOTE_CLIENT_TYPE, deviceName + "_" + serviceInstanceSuffix, PORT, 0, 0, clientInfo.toJSONString());
    }

    private void startAnnounceThread() {
        if (announceRunning) {
            return;
        }
        announceRunning = true;
        announceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (announceRunning) {
                    try {
                        Thread.sleep(ANNOUNCE_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (!announceRunning) {
                        break;
                    }
                    announceOnce();
                }
                Log.i(TAG, "announce thread stopped");
            }
        }, "ECRHub-Announce");
        announceThread.setDaemon(true);
        announceThread.start();
    }

    private void stopAnnounceThread() {
        announceRunning = false;
        if (announceThread != null) {
            announceThread.interrupt();
            announceThread = null;
        }
    }

    private void startDiscoveryPollThread() {
        if (discoveryPollRunning) {
            return;
        }
        discoveryPollRunning = true;
        discoveryPollThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (discoveryPollRunning) {
                    try {
                        Thread.sleep(DISCOVERY_POLL_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
                    JmDNS jmdns = mJmdns;
                    if (!discoveryPollRunning || jmdns == null) {
                        break;
                    }
                    try {
                        // re-queries _ecr-hub-server on the LAN; every answer triggers
                        // serviceResolved and refreshes paired terminals
                        jmdns.list(REMOTE_SERVER_TYPE, DISCOVERY_POLL_TIMEOUT_MS);
                    } catch (Exception e) {
                        Log.w(TAG, "discovery poll failed: " + e);
                    }
                }
                Log.i(TAG, "discovery poll thread stopped");
            }
        }, "ECRHub-DiscoveryPoll");
        discoveryPollThread.setDaemon(true);
        discoveryPollThread.start();
    }

    private void stopDiscoveryPollThread() {
        discoveryPollRunning = false;
        if (discoveryPollThread != null) {
            discoveryPollThread.interrupt();
            discoveryPollThread = null;
        }
    }

    /**
     * Waits briefly for a local IPv4 address (Wi-Fi may still be coming up when the
     * server starts). Runs on the worker thread, so sleeping here is fine.
     */
    private InetAddress waitForLocalIPAddress() {
        InetAddress ip = NetUtils.getLocalIPAddress();
        for (int i = 0; i < IP_WAIT_RETRIES && ip == null; i++) {
            try {
                Thread.sleep(IP_WAIT_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            ip = NetUtils.getLocalIPAddress();
        }
        return ip;
    }

    // ---------------------------------------------------------------------
    // mDNS: terminal discovery callbacks (called on JmDNS threads)
    // ---------------------------------------------------------------------

    @Override
    public void serviceAdded(ServiceEvent event) {
        Log.d(TAG, "mDNS service added: " + event.getName());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        Log.d(TAG, "mDNS service removed: " + event.getName());
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        Log.i(TAG, "mDNS service resolved: " + event.getName());
        JSONObject info = toJsonObject(event.getInfo());
        if (info == null) {
            return;
        }
        refreshPairedDevices(info);
    }

    /**
     * Updates the stored address of paired terminals when their mDNS record changes
     * (e.g. after a DHCP renewal) and notifies {@link ECRHubPairListener#onServerFind}.
     */
    private void refreshPairedDevices(JSONObject info) {
        List<ECRHubDevice> list = getPairedDeviceList();
        if (list.isEmpty()) {
            return;
        }
        String resolvedMac = emptyToNull(info.getString("mac_address"));
        String resolvedName = emptyToNull(info.getString("name"));
        String resolvedIp = emptyToNull(info.getString("ip_address"));
        String resolvedPort = emptyToNull(info.getString("port"));
        JSONArray array = new JSONArray();
        boolean changed = false;
        for (ECRHubDevice device : list) {
            // ws_address holds the terminal's mac_address saved during pairing. Name
            // matching is kept as a fallback for records saved by older SDK versions.
            boolean matched = textEquals(device.getWs_address(), resolvedMac)
                    || textEquals(device.getName(), resolvedName)
                    || textEquals(device.getTerminal_sn(), resolvedName);
            if (matched) {
                if (resolvedIp != null && !resolvedIp.equals(device.getIp_address())) {
                    device.setIp_address(resolvedIp);
                    changed = true;
                }
                if (resolvedPort != null && !resolvedPort.equals(device.getPort())) {
                    device.setPort(resolvedPort);
                    changed = true;
                }
                if (null != pairListener) {
                    pairListener.onServerFind(device);
                }
            }
            array.add(JSON.toJSON(device));
        }
        if (changed && !array.isEmpty()) {
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
    }

    // ---------------------------------------------------------------------
    // pairing
    // ---------------------------------------------------------------------

    /**
     * get paired list
     *
     * @return paired list
     */
    public List<ECRHubDevice> getPairedDeviceList() {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        List<ECRHubDevice> pairedDeviceList = new ArrayList<>();
        if (!deviceList.isEmpty()) {
            JSONArray array = JSON.parseArray(deviceList);
            for (int i = 0; i < array.size(); i++) {
                ECRHubDevice data = JSON.parseObject(array.getJSONObject(i).toString(), ECRHubDevice.class);
                pairedDeviceList.add(data);
            }
        }
        return pairedDeviceList;
    }

    private void addPairedDevice(ECRHubMessageData data) {
        JSONArray array = new JSONArray();
        ECRHubDevice device = new ECRHubDevice();
        device.setPort(data.getDevice_data().getPort());
        device.setIp_address(data.getDevice_data().getIp_address());
        if (null != data.getDevice_data().getAlias_name() && !"".equals(data.getDevice_data().getAlias_name())) {
            device.setTerminal_sn(data.getDevice_data().getAlias_name());
        } else {
            device.setTerminal_sn(data.getDevice_data().getDevice_name());
        }
        device.setWs_address(data.getDevice_data().getMac_address());
        device.setName(data.getDevice_data().getDevice_name());
        if (null == device.getName() || "".equals(device.getName())) {
            return;
        }
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        if (!deviceList.isEmpty()) {
            array = JSON.parseArray(deviceList);
            boolean isHas = false;
            for (int i = 0; i < array.size(); i++) {
                ECRHubDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), ECRHubDevice.class);
                String mac = data.getDevice_data().getMac_address();
                String ip = data.getDevice_data().getIp_address();
                String name = data.getDevice_data().getDevice_name();
                if (textEquals(deviceData.getWs_address(), mac) || textEquals(deviceData.getIp_address(), ip) || textEquals(deviceData.getName(), name)) {
                    isHas = true;
                    break;
                }
            }
            if (!isHas) {
                array.add(JSON.toJSON(device));
            }
        } else {
            array.add(JSON.toJSON(device));
        }
        if (!array.isEmpty()) {
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
    }

    public boolean confirmPair(ECRHubMessageData data) {
        if (null != connection && connection.isOpen()) {
            if (Constants.ECR_HUB_TOPIC_PAIR.equals(data.getTopic())) {
                data.setResponse_code("000");
                connection.send(JSON.toJSON(data).toString());
                addPairedDevice(data);
                Log.i(TAG, "paired device list: " + getPairedDeviceList());
            }
            return true;
        }
        return false;
    }

    public void cancelPair(ECRHubMessageData data) {
        if (null != connection && connection.isOpen()) {
            if (Constants.ECR_HUB_TOPIC_PAIR.equals(data.getTopic())) {
                data.setResponse_code("001");
                connection.send(JSON.toJSON(data).toString());
            }
        }
    }

    // ---------------------------------------------------------------------
    // multicast lock
    // ---------------------------------------------------------------------

    /**
     * Android Wi-Fi drivers filter multicast packets unless a lock is held. Without it
     * this device never receives mDNS traffic: no terminal announcements, and no browse
     * queries to answer. The permission (CHANGE_WIFI_MULTICAST_STATE) is declared in the
     * manifest; the lock still has to be acquired at runtime.
     */
    private void acquireMulticastLock() {
        try {
            if (multicastLock != null && multicastLock.isHeld()) {
                return;
            }
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                multicastLock = wifiManager.createMulticastLock(TAG);
                multicastLock.setReferenceCounted(false);
                multicastLock.acquire();
                Log.i(TAG, "multicast lock acquired");
            }
        } catch (Exception e) {
            Log.w(TAG, "acquire multicast lock failed: " + e);
        }
    }

    private void releaseMulticastLock() {
        try {
            if (multicastLock != null && multicastLock.isHeld()) {
                multicastLock.release();
                Log.i(TAG, "multicast lock released");
            }
            multicastLock = null;
        } catch (Exception e) {
            Log.w(TAG, "release multicast lock failed: " + e);
        }
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static boolean textEquals(String a, String b) {
        return a != null && b != null && a.equals(b);
    }

    /**
     * 解析获取到的客户端的信息
     *
     * @param sInfo 客户端信息
     * @return json格式信息
     */
    private JSONObject toJsonObject(ServiceInfo sInfo) {
        JSONObject jsonObj = new JSONObject();
        try {
            String ipv4 = "";
            if (sInfo.getInet4Addresses().length > 0) {
                ipv4 = sInfo.getInet4Addresses()[0].getHostAddress();
            }
            String name = sInfo.getName();
            if (name != null && name.contains("_")) {
                name = name.split("_")[0];
            }
            jsonObj.put("name", name);
            jsonObj.put("ip_address", ipv4);
            jsonObj.put("port", sInfo.getPort());
            mergeTxtRecords(sInfo, jsonObj);
        } catch (Exception e) {
            Log.w(TAG, "toJsonObject failed: " + e);
        }
        return jsonObj;
    }

    /**
     * Merges the TXT records of the resolved service into the json object. The TXT
     * payload of an ECR hub device is a json blob (mac_address / ip_address / name).
     * The ip and port from the SRV/A records stay authoritative.
     */
    private static void mergeTxtRecords(ServiceInfo sInfo, JSONObject jsonObj) {
        byte[] allInfo = sInfo.getTextBytes();
        if (allInfo == null || allInfo.length == 0) {
            return;
        }
        int index = 0;
        while (index < allInfo.length) {
            int fLen = allInfo[index++] & 0xFF;
            if (fLen == 0) {
                break;
            }
            int len = Math.min(fLen, allInfo.length - index);
            if (len <= 0) {
                break;
            }
            String fInfo = new String(allInfo, index, len, StandardCharsets.UTF_8);
            index += len;
            try {
                JSONObject jsonInfo = JSON.parseObject(fInfo);
                if (jsonInfo != null && !jsonInfo.isEmpty()) {
                    for (String key : jsonInfo.keySet()) {
                        String value = jsonInfo.getString(key);
                        if ("ip_address".equals(key)) {
                            if (emptyToNull(jsonObj.getString("ip_address")) == null && emptyToNull(value) != null) {
                                jsonObj.put(key, value);
                            }
                        } else if ("port".equals(key)) {
                            if (jsonObj.getIntValue("port") == 0 && emptyToNull(value) != null) {
                                jsonObj.put(key, value);
                            }
                        } else {
                            jsonObj.put(key, value);
                        }
                    }
                }
            } catch (Exception ignore) {
                // not a json TXT entry (e.g. "key=value"), skip it
            }
        }
    }
}
