package com.dealoka.app.services;

import com.dealoka.app.receivers.CheckBroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CheckService extends Service {
	private final IBinder binder = new LocalBinder();
	private static final long ALARM_INTERVAL = 5 * 1000;
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startAlarm();
		return Service.START_STICKY;
	}
	public void startAlarm() {
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, CheckBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, pi);
	}
	public class LocalBinder extends Binder {
		CheckService getService() {
			return CheckService.this;
		}
	}
}