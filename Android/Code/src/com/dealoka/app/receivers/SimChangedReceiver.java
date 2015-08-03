package com.dealoka.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.general.GlobalController;

public class SimChangedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalController.getSIMDevice(DealokaApp.getAppContext());
	}
}