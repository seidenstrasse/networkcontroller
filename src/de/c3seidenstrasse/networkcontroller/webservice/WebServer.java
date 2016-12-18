package de.c3seidenstrasse.networkcontroller.webservice;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.states.NetworkState;

@ServerEndpoint("/websocket")
public class WebServer extends GuiUpdater {
	Set<Session> sessions = new HashSet<>();
	Gson gson = new Gson();

	@OnOpen
	synchronized public void onOpen(final Session session) {
		System.out.println("WebSocket opened: " + session.getId());
		this.sessions.add(session);
	}

	@OnMessage
	public void onMessage(final String txt, final Session session) throws IOException {
		System.out.println("Message received: " + txt);
		session.getBasicRemote().sendText(txt.toUpperCase());
	}

	@OnClose
	synchronized public void onClose(final CloseReason reason, final Session session) {
		System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
		this.sessions.remove(session);
	}

	// --------------------------------------------------------------------------------

	synchronized private void sendToAll(final String message) {
		final Iterator<Session> i = this.sessions.iterator();
		while (i.hasNext()) {
			i.next().getAsyncRemote().sendText(message);
		}
	}

	@Override
	public void updateNode(final NetworkComponent nc) {
		final String message = this.gson.toJson(new NodeChanged(nc));
		this.sendToAll(message);
	}

	@Override
	public void updateState(final NetworkState ns) {
		final String message = this.gson.toJson(new StateChanged(ns));
		this.sendToAll(message);
	}
}
