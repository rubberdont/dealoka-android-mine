package com.dealoka.app.controller;

import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.controller.SensorController;
import com.dealoka.lib.controller.SensorController.SensorCallback;

import android.content.Context;

public class TrackerSensorController {
	private SensorController sensor_controller;
	public TrackerSensorController(final Context context) {
		sensor_controller = new SensorController(context, 0, sensor_callback);
	}
	public void load() {
		sensor_controller.load();
	}
	public void unload() {
		sensor_controller.unload();
	}
	public boolean isStandBy() {
		return sensor_controller.isStandBy();
	}
	private SensorCallback sensor_callback = new SensorCallback() {
		@Override
		public void didShake() {}
		@Override
		public void didMagnetic(float degree) {
			GlobalVariables.orientation = degree;
		}
	};
}