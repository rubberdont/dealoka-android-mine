package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

public class RedeemCouponCode {
	public String message_desc;
	public String message_action;
	public String message_func;
	public String message_status;
	public message_data c_message_data;
	public class message_data {
		public String _id;
		public String md5hash;
		public String offer_code;
		public String offer_item;
		public message_data(final JSONObject json) {
			try {
				_id = json.getString("_id");
				md5hash = json.getString("md5hash");
				offer_code = json.getString("offer_code");
				offer_item = json.getString("offer_item");
			}catch(JSONException ex) {}
		}
	}
	public RedeemCouponCode(final String json_result) {
		try {
			JSONObject json = new JSONObject(json_result);
			message_desc = json.getString("message_desc");
			message_action = json.getString("message_action");
			message_func = json.getString("message_func");
			message_status = json.getString("message_status");
			c_message_data = new message_data(new JSONObject(json.getString("message_data")));
		}catch(JSONException ex) {}
	}
}