package com.codepay.register.sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Network-related tools
 */

public class NetUtils {

    private static final String tag = "【NetUtils】";

    /**
     * Ipv4 address check.
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    /**
     * Check if valid IPV4 address.
     *
     * @param input the address string to check for validity.
     * @return True if the input parameter is a valid IPv4 address.
     */
    public static boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * Get local Ip address.
     *
     * Prefers addresses of Wi-Fi / Ethernet interfaces: on devices that also have a
     * mobile data connection the interface enumeration may list the cellular interface
     * first, and binding mDNS to that address makes the register unreachable from the
     * LAN (terminals could never connect to the announced address).
     */
    public static InetAddress getLocalIPAddress() {
        InetAddress fallback = null;
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    NetworkInterface nif = enumeration.nextElement();
                    if (!isUsableInterface(nif)) {
                        continue;
                    }
                    String name = nif.getName() == null ? "" : nif.getName().toLowerCase(Locale.ROOT);
                    boolean preferred = name.startsWith("wlan") || name.startsWith("eth") || name.startsWith("usb");
                    boolean skip = name.startsWith("rmnet") || name.startsWith("ccmni") || name.startsWith("tun")
                            || name.startsWith("p2p") || name.startsWith("sw");
                    Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                    if (inetAddresses != null) {
                        while (inetAddresses.hasMoreElements()) {
                            InetAddress inetAddress = inetAddresses.nextElement();
                            if (!inetAddress.isLoopbackAddress() && isIPv4Address(inetAddress.getHostAddress())) {
                                if (preferred) {
                                    return inetAddress;
                                }
                                if (fallback == null && !skip) {
                                    fallback = inetAddress;
                                }
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return fallback;
    }

    private static boolean isUsableInterface(NetworkInterface nif) {
        try {
            return nif != null && !nif.isLoopback() && nif.isUp();
        } catch (SocketException e) {
            return false;
        }
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                Log.e(tag, "The network is connect, netType is "
                        + info.getType() + ", netTypeName is " + info.getTypeName());
                return true;
            }
        }
        return false;
    }

    /**
     * 判断WIFI是否可用
     * - 根据当前有效的网络是否是WIFI，因为如果手机和WIFI同时具备的时候，网络会使用WIFI
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e(tag, "The network is connect, netType is "
                    + networkInfo.getType() + ", netTypeName is " + networkInfo.getTypeName());
            return (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
        } else {
            Log.e(tag, "The network is null or disConnect!");
            return false;
        }
    }

    /**
     * 判断MOBILE是否可用
     * - 如果当前连接WIFI，那么使用的就是WIFI，我们自己默认MOBILE不可用
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        return false;
    }

    public static String getMacAddress(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * 获取网络信息
     */
    public static NetworkInfo getCurrentNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }
}
