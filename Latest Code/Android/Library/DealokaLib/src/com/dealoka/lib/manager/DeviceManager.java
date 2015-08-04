package com.dealoka.lib.manager;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class DeviceManager {
	public static void changeBrightness(final Context context, final int brightness) {
		Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
	}
	public static int getBrightness(final Context context) {
		try {
			return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		}catch (SettingNotFoundException ex) {}
		return 0;
	}
}