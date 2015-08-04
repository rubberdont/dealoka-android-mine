package com.dealoka.app.general.session;

import com.dealoka.lib.General;

import android.content.Context;
import android.content.SharedPreferences;

public class FeatureSession {
	private final Context context;
	private final String default_redeem_session = SessionConst.session_feature_name + "REDEEM";
	private final String default_latitude_session = SessionConst.session_feature_name + "LATITUDE";
	private final String default_longitude_session = SessionConst.session_feature_name + "LONGITUDE";
	private final String default_phone_session = SessionConst.session_feature_name + "PHONE";
	public FeatureSession(final Context context) {
		this.context = context;
	}
	public void closeSession() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(default_latitude_session);
		editor.remove(default_longitude_session);
		editor.remove(default_redeem_session);
		editor.commit();
	}
	public boolean isLocation() {
		if(
			(Latitude() != 0.0f) &&
			(Longitude() != 0.0f)) {
			return true;
		}else {
			return false;
		}
	}
	public void Latitude(final float latitude) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(default_latitude_session, latitude);
		editor.commit();
	}
	public void Longitude(final float longitude) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(default_longitude_session, longitude);
		editor.commit();
	}
	public void Redeem(final long redeem) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(default_latitude_session, redeem);
		editor.commit();
	}
	public void Phone(final String phone) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(default_phone_session, phone);
		editor.commit();
	}
	public float Latitude() {
		try {
			SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);
			return preferences.getFloat(default_latitude_session, 0.0f);
		}catch(ClassCastException ex) {}
		return 0.0f;
	}
	public float Longitude() {
		try {
			SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);
			return preferences.getFloat(default_longitude_session, 0.0f);
		}catch(ClassCastException ex) {}
		return 0.0f;
	}
	public long Redeem() {
		try {
			SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);
			return preferences.getLong(default_redeem_session, 0L);
		}catch(ClassCastException ex) {}
		return 0L;
	}
	public String Phone() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_feature_name, Context.MODE_PRIVATE);
		return preferences.getString(default_phone_session, General.TEXT_BLANK);
	}
}