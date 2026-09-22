package com.codepay.register.sdk.device;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class ECRHubWebSocketServer extends WebSocketServer {
    private static final String TAG = "ECRHubWebSocketServer";

    private final OnServerCallback onServerCallback;

    public ECRHubWebSocketServer(InetSocketAddress address, OnServerCallback callback) {
        super(address);
        onServerCallback = callback;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String remote = conn.getRemoteSocketAddress() != null && conn.getRemoteSocketAddress().getAddress() != null
                ? conn.getRemoteSocketAddress().getAddress().getHostAddress()
                : "unknown";
        Log.d(TAG, "new connection from " + remote);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // A single pairing connection was closed. This is NOT a server shutdown:
        // forwarding this used to make the discovery service drop its server state
        // every time a terminal closed its websocket.
        Log.d(TAG, "connection closed: code=" + code + ", reason=" + reason + ", remote=" + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d(TAG, "received message: " + message);
        onServerCallback.onMessageReceived(conn, message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        // the ECR hub protocol is text only
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        String msg = ex == null ? "unknown error" : String.valueOf(ex.getMessage());
        if (conn == null) {
            // server level failure, e.g. the port could not be bound: let the discovery
            // service retry on another port
            Log.e(TAG, "server error: " + msg);
            onServerCallback.onError(msg);
        } else {
            // connection level error: the server itself keeps running. Previously every
            // such error was forwarded and made the server rebind to a different port,
            // which invalidated the port published via mDNS.
            Log.w(TAG, "connection error: " + msg);
            conn.close();
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "websocket server started");
        // onStart() runs on the selector thread before the accept loop starts: it must
        // return quickly or the server can not accept connections.
        onServerCallback.onServerStart();
    }
}
