package com.dealoka.app.receivers;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.Home;
import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.dealoka.lib.General;
import com.parse.ParsePushBroadcastReceiver;

public class ParseReceiver extends ParsePushBroadcastReceiver {
	@Override
	protected Notification getNotification(Context context, Intent intent) {
		return null;
	}
	@Override
	protected void onPushDismiss(Context context, Intent intent) {
		super.onPushDismiss(context, intent);
	}
	@Override
	protected void onPushOpen(Context arg0, Intent arg1) {
		super.onPushOpen(arg0, arg1);
	}
	@Override
	protected void onPushReceive(Context arg0, Intent arg1) {
		final String message = parse(arg1.getStringExtra("com.parse.Data"));
		if(Home.instance != null) {
			if(Home.instance.isOpened()) {
				Home.instance.showNews(message);
				return;
			}
		}
		GlobalController.sendNewsNotification(DealokaApp.getAppContext().getString(R.string.app_name), message);
	}
	private String parse(final String data) {
		try {
			JSONObject json = new JSONObject(data);
			return json.getString("alert");
		}catch(JSONException ex) {}
		return General.TEXT_BLANK;
	}
}