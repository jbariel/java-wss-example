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

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		return "[[t" + this.threadId + "]] " + str;
	}

}
