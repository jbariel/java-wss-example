package com.usaa.wss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Entry {

    private final static Logger log = LoggerFactory.getLogger(Entry.class);

    public final static int WSS_PORT = 9001;

    public static URI DEFAULT_URI = null;

    static {
        try {
            DEFAULT_URI = new URI("ws://localhost:" + Entry.WSS_PORT);
        } catch (URISyntaxException e) {
            log.error("Why could we not setup the correct default URI?!?!?");
        }
    }

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);


    public static void main(String[] args) {
        log.info("Time Publisher starting...");

        final MyWsServer publisher = new MyWsServer(WSS_PORT);
        publisher.start();

        log.debug("My WS Server started on port " + WSS_PORT);

        log.info("Starting my publisher");
        executor.scheduleAtFixedRate(() -> publisher.updateTime(), 1, 5, TimeUnit.SECONDS);

        List<Thread> threads = new ArrayList<>();
        for (int i=0; i<10; i++) {
            // used so we can oneline in the lambda
            final int tId = i;
            threads.add(new Thread(() -> {
                log.info("Creating client " + tId + "...");
                MyWsClient client = new MyWsClient(tId);
                client.connect();
            }, "Thread " + tId));
        }
        threads.forEach(t -> t.start());


        log.info("Reading console...");
        log.info("\tType 'exit' to quit");
        log.info("\tOther typed messages will broadcast");
        log.info("What would you like to say?");
        try (BufferedReader in = new BufferedReader(new InputStreamReader((System.in)))) {
            while (true) {
                String i = in.readLine();
                publisher.broadcast(i);
                if ("exit".equalsIgnoreCase(i)) {
                    publisher.stop(1000);
                    break;
                }
            }
        } catch (IOException | InterruptedException ex) {
            log.error(ex.getLocalizedMessage());
            ex.printStackTrace();
        }

        threads.forEach(t -> t.stop());

        System.exit(0);
    }
}
