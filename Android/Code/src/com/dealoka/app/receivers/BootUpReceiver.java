package com.dealoka.app.receivers;

import codemagnus.com.dealogeolib.service.DealokaService;

import com.dealoka.app.general.GlobalVariables;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {
	private static boolean start = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			if(GlobalVariables.user_session.isSession()) {
				BootUpReceiver.start = true;
			}
			context.startService(new Intent(context, DealokaService.class));
		}
	}
	public static boolean isStarted() {
		return start;
	}
}