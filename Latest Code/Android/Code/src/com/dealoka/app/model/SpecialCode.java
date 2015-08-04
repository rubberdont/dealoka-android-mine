package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SpecialCode {
	public String message_desc;
	public String message_action;
	public String message_func;
	public String message_status;
	public String message_data;
	public SpecialCode(final String json_result) {
		try {
			JSONObject json = new JSONObject(json_result);
			message_desc = json.getString("message_desc");
			message_action = json.getString("message_action");
			message_func = json.getString("message_func");
			message_status = json.getString("message_status");
			message_data = json.getString("message_data");
		}catch(JSONException ex) {}
	}
}