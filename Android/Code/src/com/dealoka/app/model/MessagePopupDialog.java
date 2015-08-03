package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

public class MessagePopupDialog {
	public String id;
	public String message_body;
	public String message_title;
	public MessagePopupDialog(final String id, final String json_result) {
		try {
			this.id = id;
			JSONObject json = new JSONObject(json_result);
			message_body = json.getString("message_body");
			message_title = json.getString("message_title");
		}catch(JSONException ex) {}
	}
}