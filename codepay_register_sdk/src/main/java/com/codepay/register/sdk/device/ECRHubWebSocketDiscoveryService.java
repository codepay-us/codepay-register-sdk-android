package com.codepay.register.sdk.device;

import android.content.Context;
import android.os.Build;

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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class ECRHubWebSocketDiscoveryService implements OnServerCallback {
    private final static String REMOTE_CLIENT_TYPE = "_ecr-hub-client._tcp.local.";
    private static int PORT = 35779;
    private Context context;
    private String deviceName = "";
    ECRHubWebSocketServer socketServer;
    private JmDNS mJmdns;
    private boolean isServerStart = false;
    private ECRHubPairListener pairListener;

    private WebSocket connection;

    public ECRHubWebSocketDiscoveryService(Context context) {
        this.context = context;
        SharePreferenceUtil.init(context);
        InetAddress ip = NetUtils.getLocalIPAddress();
        try {
            mJmdns = JmDNS.create(ip, "ECRHubServerName");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(String name, ECRHubPairListener listener) {
        deviceName = name;
        pairListener = listener;
        if (!isServerStart) {
            if (null == socketServer) {
                InetSocketAddress address = new InetSocketAddress(PORT);
                socketServer = new ECRHubWebSocketServer(address, this);
            }
            socketServer.start();
        }
    }

    public void start(ECRHubPairListener listener) {
        deviceName = Build.MODEL;
        pairListener = listener;
        if (!isServerStart) {
            if (null == socketServer) {
                InetSocketAddress address = new InetSocketAddress(PORT);
                socketServer = new ECRHubWebSocketServer(address, this);
            }
            socketServer.start();
        }
    }

    public void stop() {
        try {
            if (null != mJmdns) {
                mJmdns.close();
                mJmdns.unregisterAllServices();
                mJmdns = null;
            }
            socketServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unPair(ECRHubDevice device, ECRHubResponseCallBack callBack) {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        if (!deviceList.isEmpty()) {
            JSONArray array = JSON.parseArray(deviceList);
            for (int i = 0; i < array.size(); i++) {
                ECRHubDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), ECRHubDevice.class);
                if (deviceData.getTerminal_sn().equals(device.getTerminal_sn())) {
                    array.remove(i);
                    break;
                }
            }
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
        if (deviceName.isEmpty()) {
            deviceName = Build.MODEL;
        }
        device.setName(deviceName);
        device.setWs_address(NetUtils.getMacAddress(context));
        device.setPort("" + PORT);
        device.setIp_address(NetUtils.getLocalIPAddress().getHostAddress());
        ECRHubClient.getInstance().requestUnPair(device, callBack);
    }


    @Override
    public void onServerStart() {
        isServerStart = true;
        final JSONObject clientInfo = new JSONObject();
        clientInfo.put("mac_address", NetUtils.getMacAddress(context));
        ServiceInfo mServiceInfo = ServiceInfo.create(REMOTE_CLIENT_TYPE, deviceName, PORT, 0, 0, clientInfo.toJSONString());
        try {
            mJmdns.registerService(mServiceInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(String errorMsg) {
        isServerStart = false;
        PORT += 1;
        InetSocketAddress address = new InetSocketAddress(PORT);
        socketServer = new ECRHubWebSocketServer(address, this);
        socketServer.start();
    }

    @Override
    public void onClose() {
        isServerStart = false;
        socketServer = null;
    }

    /**
     * get paired list
     *
     * @return paired list
     */
    public List<ECRHubDevice> getPairedDeviceList() {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        List<ECRHubDevice> pairedDeviceList = new ArrayList();
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
        device.setTerminal_sn(data.getDevice_data().getAlias_name());
        device.setWs_address(data.getDevice_data().getMac_address());
        device.setName(data.getDevice_data().getDevice_name());
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        if (!deviceList.isEmpty()) {
            array = JSON.parseArray(deviceList);
            boolean isHas = false;
            for (int i = 0; i < array.size(); i++) {
                ECRHubDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), ECRHubDevice.class);
                if (deviceData.getTerminal_sn().equals(data.getDevice_data().getAlias_name())) {
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

    public void confirmPair(ECRHubMessageData data) {
        if (null != connection) {
            addPairedDevice(data);
            if (data.getTopic().equals(Constants.ECR_HUB_TOPIC_PAIR)) {
                data.setResponse_code("000");
                connection.send(JSON.toJSON(data).toString());
            }
        }
    }

    public void cancelPair(ECRHubMessageData data) {
        if (null != connection) {
            if (data.getTopic().equals(Constants.ECR_HUB_TOPIC_PAIR)) {
                data.setResponse_code("001");
                connection.send(JSON.toJSON(data).toString());
            }
        }
    }

    @Override
    public void onMessageReceived(WebSocket connection, String message) {
        this.connection = connection;
        ECRHubMessageData data = JSON.parseObject(message, ECRHubMessageData.class);
        if (data.getTopic().equals(Constants.ECR_HUB_TOPIC_UNPAIR)) {
            data.setResponse_code("000");
            connection.send(JSON.toJSON(data).toString());
        } else {
            pairListener.onDevicePair(data, "ws://" + data.getDevice_data().getIp_address() + ":" + data.getDevice_data().getPort());
        }
    }
}
