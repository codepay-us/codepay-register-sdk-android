package com.codepay.register.sdk.device;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class ECRHubWebSocketServer extends WebSocketServer {
    private final OnServerCallback onServerCallback;

    public ECRHubWebSocketServer(InetSocketAddress address, OnServerCallback callback) {
        super(address);
        onServerCallback = callback;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println(
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        System.out.println("New connection opened");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed");
        onServerCallback.onClose();
        conn.close();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        onServerCallback.onMessageReceived(conn, message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        super.onMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (null != conn) {
            conn.close();
        }
        System.err.println("Error occurred: " + ex.getMessage());
        onServerCallback.onError(ex.getMessage());
    }

    @Override
    public void onStart() {
        onServerCallback.onServerStart();
        System.out.println("WebSocket server started");
    }
}
