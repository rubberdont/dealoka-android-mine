package com.dealoka.lib.controller;

import com.dealoka.lib.Config;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorController {
	private static final int SHAKE_THRESHOLD = 600;
	private Context context;
	private SensorManager sensor_manager;
	private Sensor sensor_accelerometer;
	private Sensor sensor_magnetic_field;
	private SensorCallback sensor_callback;
	private long update_timer = 0;
	private long last_update = 0;
	private float last_x, last_y, last_z;
	private float[] gravity;
	private float[] geo_magnetic;
	public interface SensorCallback {
		public abstract void didShake();
		public abstract void didMagnetic(final float degree);
	}
	public SensorController(final Context context, final long update_timer, final SensorCallback sensor_callback) {
		this.context = context;
		this.update_timer = update_timer;
		this.sensor_callback = sensor_callback;
	}
	public void load() {
		if(sensor_manager != null) {
			unload();
		}
		sensor_manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensor_accelerometer = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensor_magnetic_field = sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensor_manager.registerListener(sensor_listener, sensor_accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensor_manager.registerListener(sensor_listener, sensor_magnetic_field, SensorManager.SENSOR_DELAY_NORMAL);
	}
	public void unload() {
		if(sensor_manager == null) {
			return;
		}
		sensor_manager.unregisterListener(sensor_listener);
		sensor_manager = null;
	}
	public boolean isStandBy() {
		if(sensor_manager != null) {
			return true;
		}else {
			return false;
		}
	}
	public void setUpdateTimer(final long value) {
		update_timer = value;
	}
	private SensorEventListener sensor_listener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			Sensor sensor = event.sensor;
			if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				gravity = event.values;
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
				final long current_time = System.currentTimeMillis();
				if((current_time - last_update) > update_timer) {
					final long diff = (current_time - last_update);
					last_update = current_time;
					float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diff * 10000;
					if(speed > SHAKE_THRESHOLD) {
						sensor_callback.didShake();
					}
					last_x = x;
					last_y = y;
					last_z = z;
				}
			}
			if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				geo_magnetic = event.values;
			}
			if(gravity != null && geo_magnetic != null) {
				float R[] = new float[9];
				float I[] = new float[9];
				final boolean success = SensorManager.getRotationMatrix(R, I, gravity, geo_magnetic);
				if(success) {
					float orientation[] = new float[3];
					SensorManager.getOrientation(R, orientation);
					final float azimut = orientation[0];
					sensor_callback.didMagnetic(-azimut * 360 / ( 2 * (float)Config.PI));
				}
			}
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
}