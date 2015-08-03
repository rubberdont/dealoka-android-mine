package com.dealoka.lib.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryController {
	private Battery battery = null;
	public Battery getBatteryInformation(final Context context) {
		context.getApplicationContext().registerReceiver(this.BatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		try {
			int i = 0;
			while(battery == null && i <10) {
				Thread.sleep(1000);
				i++;
			}
		}catch(InterruptedException ex) {}
		return battery;
	}
	private BroadcastReceiver BatteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
			final int icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);
			final int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			final int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			final boolean present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
			final int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
			final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
			final String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
			final int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
			final int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
			final boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
			battery = new Battery();
			battery.setHealth(health);
			battery.setIconSmall(icon_small);
			battery.setLevel(level);
			battery.setPlugged(plugged);
			battery.setPresent(present);
			battery.setScale(scale);
			battery.setStatus(status);
			battery.setTechnology(technology);
			battery.setTemperature(temperature);
			battery.setVoltage(voltage);
			battery.setCharging(charging);
			context.unregisterReceiver(BatteryReceiver);
		}
	};
	public class Battery {
		private int health;
		private int iconSmall;          
		private int level;               
		private int plugged;             
		private boolean present;         
		private int scale;               
		private int status;              
		private String technology;       
		private int temperature;         
		private int voltage;
		private boolean charging;
		public boolean isCharging() {
			return charging;
		}
		public void setCharging(boolean charging) {
			this.charging = charging;
		}
		public int getHealth() {
			return health;
		}
		public void setHealth(int health) {
			this.health = health;
		}
		public int getIconSmall() {
			return iconSmall;
		}
		public void setIconSmall(int iconSmall) {
			this.iconSmall = iconSmall;
		}
		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
		public int getPlugged() {
			return plugged;
		}
		public void setPlugged(int plugged) {
			this.plugged = plugged;
		}
		public boolean isPresent() {
			return present;
		}
		public void setPresent(boolean present) {
			this.present = present;
		}
		public int getScale() {
			return scale;
		}
		public void setScale(int scale) {
			this.scale = scale;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getTechnology() {
			return technology;
		}
		public void setTechnology(String technology) {
			this.technology = technology;
		}
		public int getTemperature() {
			return temperature;
		}
		public void setTemperature(int temperature) {
			this.temperature = temperature;
		}
		public int getVoltage() {
			return voltage;
		}
		public void setVoltage(int voltage) {
			this.voltage = voltage;
		} 
	}
}