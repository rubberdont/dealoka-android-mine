package com.dealoka.app.general.session;

import android.content.Context;
import android.content.SharedPreferences;

public class TimeSession {
	private final Context context;
	private final String default_time_session = SessionConst.session_time_name + "TIME";
	public TimeSession(final Context context) {
		this.context = context;
	}
	public void openSession(final long time) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_time_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(default_time_session, time);
		editor.commit();
	}
	public void closeSession() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_time_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(default_time_session);
		editor.commit();
	}
	public boolean isSession() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_time_name, Context.MODE_PRIVATE);
		if(preferences.getLong(default_time_session, 0) > 0) {
			return true;
		}else {
			return false;
		}
	}
	public final long GetFromSession() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_time_name, Context.MODE_PRIVATE);  
		return preferences.getLong(default_time_session, 0);
	}
	public void Time(final long time) {
		openSession(time);
	}
	public final long GetTime() {
		return GetFromSession();
	}
}