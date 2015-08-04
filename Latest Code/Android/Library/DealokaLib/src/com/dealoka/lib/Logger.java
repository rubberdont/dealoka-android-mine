package com.dealoka.lib;

import android.util.Log;

public class Logger {
	public static void d(final String message) {
		if(Config.LOG_ENABLED) {
			Log.d(Config.TAG, message);
		}
	}
	public static void i(final String message) {
		Log.i(Config.TAG, message);
	}
	public static void e(final String message, Throwable e) {
		if(e != null) {
			Log.e(Config.TAG, message, e);
		}else {
			Log.e(Config.TAG, message);
		}
	}
}
