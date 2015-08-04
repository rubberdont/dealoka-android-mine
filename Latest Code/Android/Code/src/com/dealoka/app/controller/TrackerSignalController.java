package com.dealoka.app.controller;

import android.content.Context;

import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.manager.PhoneManager;
import com.dealoka.lib.manager.PhoneManager.PhoneStateCallback;

public class TrackerSignalController {
	private PhoneStateCallback phone_state_callback = new PhoneStateCallback() {
		@Override
		public void didSignalChanged(int strength) {
			GlobalVariables.signal = strength;
		}
	};
	public TrackerSignalController(final Context context) {
		PhoneManager.getPhoneState(context, phone_state_callback);
	}
}