package com.dealoka.lib.controller;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.dealoka.lib.Config;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

public class LocationController {
	private final static long THIRTY_SECONDS = 1000 * 30;
	private final static long ONE_HOUR = 1000 * 60 * 60;
	private long now = 0;
	private long min_time = 0;
	private float min_distance = 0;
	public enum Location_Status {
		none,
		error,
		changed
	};
	public enum Location_Mode {
		network,
		gps,
		passive
	}
	private LocationManager location_manager = null;
	private LocationListener location_listener = null;
	private LocationCallback location_callback = null;
	private Location_Status location_status = Location_Status.none;
	private Location_Mode location_mode = Location_Mode.network;
	private Context context;
	private Timer timeout_timer = null;
	private int counter = 0;
	public interface LocationCallback {
		public abstract void didLocationUpdated(final float latitude, final float longitude);
		public abstract void didLocationFailed();
		public abstract void isProviderEnabled(final boolean enabled);
	}
	@SuppressLint("UseValueOf")
	public LocationController(final Context context) {
		this.context = context;
		location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		location_listener = new LocationListener() {
			public void onProviderDisabled(final String provider) {
				if(location_mode == Location_Mode.passive) {
					location_callback.didLocationFailed();
					return;
				}
				location_status = Location_Status.error;
				unloadLocation();
				location_callback.didLocationFailed();
			}
			public void onProviderEnabled(final String provider) {}
			public void onStatusChanged(final String provider, final int status, final Bundle extras) {}
			public void onLocationChanged(final Location location) {
				if(location_mode == Location_Mode.passive) {
					long delta = location.getTime() - now;
					if(delta < THIRTY_SECONDS || delta >= ONE_HOUR)
						return;
					unloadLocation();
					final Double latitude = new Double(location.getLatitude());
					final Double longitude = new Double(location.getLongitude());
					location_callback.didLocationUpdated(latitude.floatValue(), longitude.floatValue());
					return;
				}
				stopTimeoutCounter();
				location_status = Location_Status.changed;
				unloadLocation();
				final Double latitude = new Double(location.getLatitude());
				final Double longitude = new Double(location.getLongitude());
				location_callback.didLocationUpdated(latitude.floatValue(), longitude.floatValue());
			}
		};
	}
	public void setMinimumFilter(final long min_time, final float min_distance) {
		this.min_time = min_time;
		this.min_distance = min_distance;
	}
	public void setLocationCallback(final LocationCallback location_callback) {
		this.location_callback = location_callback;
	}
	public void loadLocation(final LocationCallback location_callback) {
		setLocationCallback(location_callback);
		location_status = Location_Status.none;
		location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		final String bestProvider = location_manager.getBestProvider(criteria, true);
		if(bestProvider != null && bestProvider.length() > 0) {
			location_manager.requestLocationUpdates(bestProvider, min_time, min_distance, location_listener);
		}else {
			final List<String> providers = location_manager.getProviders(true);
			for(final String provider : providers) {
				location_manager.requestLocationUpdates(provider, min_time, min_distance, location_listener);
			}
		}
		startTimeoutCounter();
	}
	public void loadLocationWithGPS(final LocationCallback location_callback) {
		setLocationCallback(location_callback);
		location_status = Location_Status.none;
		location_mode = Location_Mode.gps;
		location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, min_time, min_distance, location_listener);
		startTimeoutCounter();
	}
	public void loadLocationWithGPS() {
		if(location_callback == null) {
			return;
		}
		loadLocationWithGPS(location_callback);
	}
	public void loadLocationWithNetwork(final LocationCallback location_callback) {
		setLocationCallback(location_callback);
		location_status = Location_Status.none;
		location_mode = Location_Mode.network;
		location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		location_manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, min_time, min_distance, location_listener);
		startTimeoutCounter();
	}
	public void loadLocationWithNetwork() {
		if(location_callback == null) {
			return;
		}
		loadLocationWithNetwork(location_callback);
	}
	@TargetApi(Build.VERSION_CODES.FROYO)
	public void loadLocationWithPassive(final long now, final LocationCallback location_callback) {
		setLocationCallback(location_callback);
		this.now = now;
		location_mode = Location_Mode.passive;
		location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		location_manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, THIRTY_SECONDS, 0, location_listener);
	}
	public void loadLocationWithPassive(final long now) {
		if(location_callback == null) {
			return;
		}
		loadLocationWithPassive(now, location_callback);
	}
	public void loadLocationWithOrder(final LocationCallback location_callback) {
		if(isNetworkProviderEnabled(context)) {
			loadLocationWithNetwork(location_callback);
			return;
		}
		if(isGPSProviderEnabled(context)) {
			loadLocationWithGPS(location_callback);
			return;
		}
		location_callback.didLocationFailed();
	}
	public void loadLocationWithOrder() {
		if(location_callback == null) {
			return;
		}
		loadLocationWithOrder(location_callback);
	}
	public Location getCurrentLocation() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		LocationManager loc_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Location current_location = null;
		try {
			current_location = loc_manager.getLastKnownLocation(loc_manager.getBestProvider(criteria, true));
			if(current_location == null) {
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				current_location = loc_manager.getLastKnownLocation(loc_manager.getBestProvider(criteria, true));
			}
		}catch(Exception ex) {}
		return current_location;
	}
	public void unloadLocation() {
		if(location_manager == null) {
			return;
		}
		location_manager.removeUpdates(location_listener);
	}
	public static boolean isGPSNetworkProviderEnabled(final Context context) {
		boolean enabled = false;
		LocationManager location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if(location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			enabled = true;
		}
		if(location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			enabled = true;
		}
		return enabled;
	}
	public static boolean isGPSProviderEnabled(final Context context) {
		LocationManager location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if(location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		}
		return false;
	}
	public static boolean isNetworkProviderEnabled(final Context context) {
		LocationManager location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if(location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			return true;
		}
		return false;
	}
	private void startTimeoutCounter() {
		counter = 0;
		timeout_timer = new Timer();
		timeout_timer.scheduleAtFixedRate(new TimeoutCounter(), 1000, 1000);
	}
	private void stopTimeoutCounter() {
		if(timeout_timer == null) {
			return;
		}
		counter = 0;
		timeout_timer.cancel();
		timeout_timer = null;
	}
	@SuppressLint("UseValueOf")
	private class TimeoutCounter extends TimerTask {
		@Override
		public void run() {
			if(counter < Config.LOCATION_TIMER) {
				counter++;
			}else {
				if(location_status == Location_Status.none) {
					Location location = getCurrentLocation();
					if(location != null) {
						location_status = Location_Status.changed;
						unloadLocation();
						final Double latitude = new Double(location.getLatitude());
						final Double longitude = new Double(location.getLongitude());
						location_callback.didLocationUpdated(latitude.floatValue(), longitude.floatValue());
					}else {
						location_status = Location_Status.error;
						unloadLocation();
						location_callback.didLocationFailed();
					}
				}
				stopTimeoutCounter();
			}
		}
	}
}