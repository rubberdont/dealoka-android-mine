package com.dealoka.app.subs;

import java.util.HashMap;
import java.util.Map;

import com.dealoka.app.general.DDPController;
import com.google.gson.Gson;
import com.dealoka.lib.General;

public class SubsController {
	private final Gson gson = new Gson();
	private DDPController ddp_controller;
	private String subs_id = General.TEXT_BLANK;
	public interface SubsListener {
		public void onSubConnected(final String subs_id);
		public void onSubAdded(final String collection, final String id, final String result);
		public void onSubChanged(final String collection, final String id, final String result);
		public void onSubRemoved(final String collection, final String id);
		public void onSubFailed();
		public void onSubEOF();
	}
	public SubsController getSubController() {
		return this;
	}
	public SubsController() {}
	public SubsController(final String subs_id) {
		this.subs_id = subs_id;
	}
	public void setListener(final SubsListener subs_listener, final boolean auto_reconnect) {
		ddp_controller = new DDPController(subs_id);
		ddp_controller.init(subs_listener, auto_reconnect);
	}
	public void setAutoReconnect(final boolean auto_reconnect) {
		ddp_controller.setAutoReconnect(auto_reconnect);
	}
	public void connect() {
		ddp_controller.connect();
	}
	public boolean isConnecting() {
		if(ddp_controller == null) {
			return false;
		}else {
			return ddp_controller.isConnecting();
		}
	}
	public boolean isConnected() {
		if(ddp_controller == null) {
			return false;
		}else {
			return ddp_controller.isConnected();
		}
	}
	public void subsCategory() {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("version", SubsConst.db_version);
		String json = gson.toJson(message);
		ddp_controller.do_subs(SubsConst.publish_db_category, new String[] {json});
	}
	public void subsGender() {
		ddp_controller.do_subs(SubsConst.publish_db_gender);
	}
	public void subsMessagePopupDialog(final String user_token) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("user_token", user_token);
		String json = gson.toJson(message);
		ddp_controller.do_subs(SubsConst.publish_db_message_popup_dialog, new String[] {json});
	}
	public void unsubs(final String unique_id) {
		ddp_controller.do_unsubs(unique_id);
	}
	public void disconnect() {
		if(ddp_controller == null) {
			return;
		}
		ddp_controller.disconnect();
	}
}