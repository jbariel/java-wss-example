package com.ex.wss;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

final class MyWsServer extends WebSocketServer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String currentTime = "1";

    private MyWsServer() {
        super();
    }

    MyWsServer(int port) {
        this(new InetSocketAddress(port));
    }

    MyWsServer(InetSocketAddress inetAddr) {
        super(inetAddr);
    }


    @Override
    public void onStart() {
        log.debug("Started WSS...");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send(currentTime);
        log.debug(conn + " has joined");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left");
    }

    @Override
    public void onMessage(WebSocket conn, String msg) {
        broadcast(msg);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void broadcast(String text) {
        log.trace("[BROADCAST] " + text);
        super.broadcast(text);
    }

    public void updateTime() {
        this.currentTime = String.valueOf(System.currentTimeMillis());
        broadcast(this.currentTime);
    }

}
