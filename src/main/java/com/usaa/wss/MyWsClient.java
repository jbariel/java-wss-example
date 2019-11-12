package com.usaa.wss;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

final class MyWsClient extends WebSocketClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private int threadId = 0;

    MyWsClient() {
        this(Entry.DEFAULT_URI);
    }

    MyWsClient(int threadId) {
        this();
        this.threadId = threadId;
    }

    MyWsClient(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info(tStr("Client connected!"));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info(tStr("Closed with code '" + code + "' for reason: '" + reason + "'"));
    }

    @Override
    public void onMessage(String msg) {
        log.info(tStr("[MSG] " + msg));
    }

    @Override
    public void onError(Exception e) {
        log.error(e.getLocalizedMessage());
        e.printStackTrace();
    }

    private String tStr(String str) {
        return "[[t"+this.threadId+"]] " + str;
    }


}
