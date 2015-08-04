package com.dealoka.lib.badge;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	public static final String PREFERENCES_FILE = "Badge.Main";
	private static final String PREF_IS_BADGING_SUPPORTED = "isBadgingSupported";
	private static Preferences sPreferences;
	private final SharedPreferences mSharedPreferences;
	private Preferences(Context context) {
		mSharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
	}
	public static synchronized Preferences getPreferences(Context context) {
		if(sPreferences == null) {
			sPreferences = new Preferences(context);
		}
		return sPreferences;
	}
	public int getIsBadgingSupported() {
		return mSharedPreferences.getInt(PREF_IS_BADGING_SUPPORTED, -1);
	}
	public void setIsBadgingSupported(boolean set) {
		mSharedPreferences.edit().putInt(PREF_IS_BADGING_SUPPORTED, set ? 1 : 0).apply();
	}
}