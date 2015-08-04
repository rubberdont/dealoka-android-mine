package com.dealoka.app.general;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.general.DDPMessage.DdpMessageField;
import com.dealoka.app.general.DDPMessage.DdpMessageType;
import com.dealoka.app.subs.SubsController.SubsListener;
import com.google.gson.Gson;
import com.dealoka.lib.General;
import com.dealoka.lib.Logger;
import com.dealoka.lib.websocket.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

public class DDPController {
	public static int unique_id = 0;
	private final Gson gson = new Gson();
	private CallListener call_listener;
	private SubsListener subs_listener;
	private WebSocketClient client;
	private boolean connecting = false;
	private boolean connected = false;
	private boolean auto_reconnect = false;
	private String saved_json = General.TEXT_BLANK;
	private String saved_subs_id = General.TEXT_BLANK;
	private boolean saved = false;
	private setAutoConnectTimer set_auto_connect_timer;
	public DDPController() {}
	public DDPController(final String subs_id) {
		this.saved_subs_id = subs_id;
	}
	public void init(CallListener call_listener) {
		this.call_listener = call_listener;
		auto_reconnect = false;
		saved = false;
		connect();
	}
	public void init(SubsListener subs_listener, final boolean auto_reconnect) {
		this.subs_listener = subs_listener;
		this.auto_reconnect = auto_reconnect;
		saved = false;
		connect();
	}
	public void setAutoReconnect(final boolean auto_reconnect) {
		this.auto_reconnect = auto_reconnect;
	}
	public WebSocketClient SocketClient() {
		return client;
	}
	public boolean isConnecting() {
		return connecting;
	}
	public boolean isConnected() {
		return connected;
	}
	public void do_call(final String name, final String[] params) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put(DdpMessageField.MSG, DdpMessageType.METHOD);
		message.put(DdpMessageField.METHOD, name);
		message.put(DdpMessageField.PARAMS, params);
		message.put(DdpMessageField.ID, String.valueOf(next_id()));
		saved_json = gson.toJson(message);
		if(!isConnected()) {
			saved = true;
			return;
		}
		saved = false;
		client.send(saved_json);
	}
	public void do_call(final String name, final String params) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put(DdpMessageField.MSG, DdpMessageType.METHOD);
		message.put(DdpMessageField.METHOD, name);
		message.put(DdpMessageField.PARAMS, params);
		message.put(DdpMessageField.ID, String.valueOf(next_id()));
		saved_json = gson.toJson(message);
		if(!isConnected()){
			saved = true;
			return;
		}
		saved = false;
		client.send(saved_json);
	}
	public void do_subs(final String name) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put(DdpMessageField.MSG, DdpMessageType.SUB);
		message.put(DdpMessageField.NAME, name);
		message.put(DdpMessageField.PARAMS, null);
		message.put(DdpMessageField.ID, String.valueOf(next_id()));
		saved_json = gson.toJson(message);
		if(!isConnected()) {
			saved = true;
			return;
		}
		saved = false;
		client.send(saved_json);
	}
	public void do_subs(final String name, final Object params[]) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put(DdpMessageField.MSG, DdpMessageType.SUB);
		message.put(DdpMessageField.NAME, name);
		message.put(DdpMessageField.PARAMS, params);
		message.put(DdpMessageField.ID, String.valueOf(next_id()));
		saved_json = gson.toJson(message);
		if(!isConnected()) {
			saved = true;
			return;
		}
		saved = true;
		client.send(saved_json);
	}
	public void do_unsubs(final String id) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put(DdpMessageField.MSG, DdpMessageType.UNSUB);
		message.put(DdpMessageField.ID, id);
		saved_json = gson.toJson(message);
		if(!isConnected()) {
			saved = true;
			return;
		}
		saved = true;
		client.send(saved_json);
	}
	public void connect() {
		List<BasicNameValuePair> extra_headers = Arrays.asList(new BasicNameValuePair("Cookie", "session=dealoka"));
		try {
			client = new WebSocketClient(new URI(Config.socket_server), listener, extra_headers);
			client.connect();
		}catch(URISyntaxException ex) {}
	}
	public void disconnect() {
		if(client == null) {
			return;
		}
		auto_reconnect = false;
		client.disconnect();
		connecting = false;
		connected = false;
	}
	private int next_id() {
		unique_id++;
		return unique_id;
	}
	private void setAutoConnect() {
		if(set_auto_connect_timer == null) {
			set_auto_connect_timer = new setAutoConnectTimer();
		}
		if(set_auto_connect_timer.isRun()) {
			return;
		}
		set_auto_connect_timer = new setAutoConnectTimer();
		General.executeAsync(set_auto_connect_timer);
	}
	private WebSocketClient.Listener listener = new WebSocketClient.Listener() {
		@Override
		public void onConnect() {
			connecting = true;
			connected = false;
			Map<String, Object> connect_message = new HashMap<String, Object>();
			connect_message.put(DdpMessageField.MSG, DdpMessageType.CONNECT);
			connect_message.put(DdpMessageField.VERSION, DDPMessage.DDP_PROTOCOL_VERSION);
			connect_message.put(DdpMessageField.SUPPORT, new String[] { DDPMessage.DDP_PROTOCOL_VERSION });
			if(General.isNotNull(GlobalVariables.session)) {
				connect_message.put(DdpMessageField.SESSION, GlobalVariables.session);
			}
	        String json = gson.toJson(connect_message);
	        client.send(json);
	        if(saved) {
	        	saved = false;
	        	client.send(saved_json);
	        }
		}
		@Override
		public void onMessage(String message) {
			parseMessage(message);
		}
		@Override
		public void onMessage(byte[] data) {}
		@Override
		public void onDisconnect(int code, String reason) {
			connecting = false;
			connected = false;
			if(code == 1) {
				if(subs_listener != null) {
					subs_listener.onSubEOF();
				}
			}
			if(auto_reconnect) {
				setAutoConnect();
			}
		}
		@Override
		public void onSocketError() {
			connecting = false;
			connected = false;
			if(call_listener != null) {
				call_listener.onCallFailed();
			}
			if(subs_listener != null) {
				subs_listener.onSubFailed();
			}
			if(auto_reconnect) {
				setAutoConnect();
			}
		}
		@Override
		public void onUnknownHost() {
			connecting = false;
			connected = false;
			if(call_listener != null) {
				call_listener.onCallFailed();
			}
			if(subs_listener != null) {
				subs_listener.onSubFailed();
			}
			if(auto_reconnect) {
				setAutoConnect();
			}
		}
		@Override
		public void onError(Exception error) {
			connecting = false;
			connected = false;
			if(call_listener != null) {
				call_listener.onCallFailed();
			}
			if(subs_listener != null) {
				subs_listener.onSubFailed();
			}
		}
	};
	private class setAutoConnectTimer extends AsyncTask<Void, Void, Void> {
		private boolean run = false;
		public boolean isRun() {
			return run;
		}
		@Override
		protected void onPostExecute(Void result) {
			if(auto_reconnect) {
				connect();
			}
			run = false;
		}
		@Override
		protected void onPreExecute() {
			run = true;
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(Config.idle_timer);
			}catch(InterruptedException ex) {}
			return null;
		}
		@Override
		protected void onCancelled() {
			run = false;
		}
	}
	private void parseMessage(final String message) {
		Logger.d(message);
		try {
			JSONObject json = new JSONObject(message);
			if(json.isNull(DdpMessageField.MSG)) {
				return;
			}
			if(json.getString(DdpMessageField.MSG).equals(DdpMessageType.CONNECTED)) {
				if(!json.isNull(DdpMessageField.SESSION)) {
					connecting = false;
					connected = true;
					GlobalVariables.session = json.getString(DdpMessageField.SESSION);
					if(call_listener != null) {
						call_listener.onCallConnected();
					}
					if(subs_listener != null) {
						subs_listener.onSubConnected(saved_subs_id);
					}
				}
			}else if(json.getString(DdpMessageField.MSG).equals(DdpMessageField.SUBS)) {
			}else if(json.getString(DdpMessageField.MSG).equals(DdpMessageType.ERROR)) {
			}else if(json.getString(DdpMessageField.MSG).equals(DdpMessageType.ADDED)) {
				parseAddSubscribe(message);
			}else if(json.getString(DdpMessageField.MSG).equals(DdpMessageType.CHANGED)) {
				parseChangeSubscribe(message);
			}else if(json.getString(DdpMessageField.MSG).equals(DdpMessageType.REMOVED)) {
				parseRemoveSubscribe(message);
			}else if(json.getString(DdpMessageField.MSG).equals(DdpMessageType.RESULT)) {
				if(call_listener != null) {
					if(json.isNull(DdpMessageField.ERROR)) {
						call_listener.onCallReturned(json.getString(DdpMessageField.RESULT));
					}else {
						call_listener.onCallFailed();
					}
				}
			}
		}catch(JSONException ex) {}
	}
	private void parseAddSubscribe(final String message) {
		try {
			JSONObject json = new JSONObject(message);
			if(json.isNull(DdpMessageField.COLLECTION)) {
				return;
			}
			if(subs_listener != null) {
				subs_listener.onSubAdded(
						json.getString(DdpMessageField.COLLECTION),
						json.getString(DdpMessageField.ID),
						json.getString(DdpMessageField.FIELDS));
			}
		}catch(JSONException ex) {}
	}
	private void parseChangeSubscribe(final String message) {
		try {
			JSONObject json = new JSONObject(message);
			if(json.isNull(DdpMessageField.COLLECTION)) {
				return;
			}
			if(subs_listener != null) {
				subs_listener.onSubChanged(
						json.getString(DdpMessageField.COLLECTION),
						json.getString(DdpMessageField.ID),
						json.getString(DdpMessageField.FIELDS));
			}
		}catch(JSONException ex) {}
	}
	private void parseRemoveSubscribe(final String message) {
		try {
			JSONObject json = new JSONObject(message);
			if(json.isNull(DdpMessageField.COLLECTION)) {
				return;
			}
			if(subs_listener != null) {
				subs_listener.onSubRemoved(
						json.getString(DdpMessageField.COLLECTION),
						json.getString(DdpMessageField.ID));
			}
		}catch(JSONException ex) {}
	}
}