package com.dealoka.app.general.session;

import com.dealoka.lib.General;

import android.content.Context;
import android.content.SharedPreferences;

public class SIMSession {
	private final Context context;
	private final String default_dual_sim_session = SessionConst.session_sim_name + "DUAL_SIM";
	private final String default_device_id_session = SessionConst.session_sim_name + "DEVICE_ID";
	private final String default_imsi_session = SessionConst.session_sim_name + "IMSI";
	private final String default_init_session = SessionConst.session_sim_name + "INIT";
	public SIMSession(final Context context) {
		this.context = context;
	}
	public void removeAll() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_sim_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(default_dual_sim_session);
		editor.remove(default_device_id_session);
		editor.remove(default_imsi_session);
		editor.remove(default_init_session);
		editor.commit();
	}
	public void DUALSIM(final boolean dual_sim) {
		commitSession(default_dual_sim_session, dual_sim);
	}
	public final boolean DUALSIM() {
		return GetFromSession(default_dual_sim_session, false);
	}
	public void runINIT() {
		commitSession(default_init_session, false);
	}
	public final boolean INIT() {
		return GetFromSession(default_init_session, true);
	}
	public void DEVICEID(final String device_id) {
		commitSession(default_device_id_session, device_id);
	}
	public final String DEVICEID() {
		return GetFromSession(default_device_id_session);
	}
	public void IMSI(final String imsi) {
		commitSession(default_imsi_session, imsi);
	}
	public final String IMSI() {
		return GetFromSession(default_imsi_session);
	}
	private final boolean GetFromSession(final String id, final boolean init) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_sim_name, Context.MODE_PRIVATE);  
		return preferences.getBoolean(id, init);
	}
	private final String GetFromSession(final String id) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_sim_name, Context.MODE_PRIVATE);  
		return preferences.getString(id, General.TEXT_BLANK);
	}
	private void commitSession(final String key, final boolean value) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_sim_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	private void commitSession(final String key, final String value) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_sim_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
}