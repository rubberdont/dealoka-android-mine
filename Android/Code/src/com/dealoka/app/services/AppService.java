package com.dealoka.app.services;

import codemagnus.com.dealogeolib.service.DealokaService;
import codemagnus.com.dealogeolib.utils.GeneralUtils;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.subs.SubsManager;

import android.app.IntentService;
import android.content.Intent;

public class AppService extends IntentService {
	public static String TAG = AppService.class.getCanonicalName();
	public static AppService service;
	public AppService() {
		super("AppService");
		service = this;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		SubsManager.init();
		new Thread(new Runnable() {
			public void run() {
				DealokaApp.getAppContext().stopService(new Intent(DealokaApp.getAppContext(), CheckService.class));
				DealokaApp.getAppContext().startService(new Intent(DealokaApp.getAppContext(), CheckService.class));
				if(!GeneralUtils.ISSERVICERUNNING(DealokaApp.getAppContext(), DealokaService.class))
					DealokaApp.getAppContext().startService(new Intent(DealokaApp.getAppContext(), DealokaService.class));
			}
		}).start();
	}
}