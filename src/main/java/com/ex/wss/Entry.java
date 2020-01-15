/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.ex.wss;

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
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	static class ConsumerThread extends Thread {

		// private final int myThreadId;

		private final MyWsClient client;

		public ConsumerThread(final int threadId) {
			super("Thread " + threadId);
			// this.myThreadId = threadId;
			log.info("Creating client " + threadId + "...");
			client = new MyWsClient(threadId);
		}

		@Override
		public void run() {
			client.connect();
		}

		public void safeStop() {
			client.close();
		}

	}

	public static void main(String[] args) {
		log.info("Time Publisher starting...");

		final MyWsServer publisher = new MyWsServer(WSS_PORT);
		publisher.start();

		log.debug("My WS Server started on port " + WSS_PORT);

		log.info("Starting my publisher");
		executor.scheduleAtFixedRate(() -> publisher.updateTime(), 1, 5, TimeUnit.SECONDS);

		List<ConsumerThread> threads = new ArrayList<>();
		IntStream.range(0, 9).forEach(i -> threads.add(new ConsumerThread(i)));
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

		threads.forEach(t -> t.safeStop());

		System.exit(0);
	}
}
