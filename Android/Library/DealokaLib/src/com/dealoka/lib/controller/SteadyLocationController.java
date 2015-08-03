package com.dealoka.lib.controller;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class SteadyLocationController {
	private LocationManager location_manager = null;
	private LocationListener location_listener = null;
	private SteadyLocationCallback location_callback = null;
	private long min_time = 0;
	private float min_distance = 0;
	private Context context;
	public interface SteadyLocationCallback {
		public abstract void didSteadyLocationUpdated(final double latitude, final double longitude, final long time);
		public abstract void didSteadyLocationFailed();
	}
	public SteadyLocationController(final Context context, final SteadyLocationCallback location_callback) {
		this.context = context;
		this.location_callback = location_callback;
	}
	public void setMinimumFilter(final long min_time, final float min_distance) {
		this.min_time = min_time;
		this.min_distance = min_distance;
	}
	public void setLocationCallback(final SteadyLocationCallback location_callback) {
		this.location_callback = location_callback;
	}
	public boolean isStandBy() {
		if(location_manager != null) {
			return true;
		}else {
			return false;
		}
	}
	public void locatLocation() {
		if(location_manager != null) {
			unloadLocation();
		}
		loadLocation();
		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		final String best_provider = location_manager.getBestProvider(criteria, true);
		if(best_provider != null && best_provider.length() > 0) {
			location_manager.requestLocationUpdates(best_provider, min_time, min_distance, location_listener);
		}else {
			final List<String> providers = location_manager.getProviders(true);
			for(final String provider : providers) {
				location_manager.requestLocationUpdates(provider, min_time, min_distance, location_listener);
			}
		}
	}
	public void locatLocationWithGPS() {
		loadLocation();
		location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, min_time, min_distance, location_listener);
	}
	public void locatLocationWithNetwork() {
		loadLocation();
		location_manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, min_time, min_distance, location_listener);
	}
	public void unloadLocation() {
		if(location_manager == null) {
			return;
		}
		location_manager.removeUpdates(location_listener);
		location_manager = null;
	}
	public void loadLocation() {
		location_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		location_listener = new LocationListener() {
			public void onProviderDisabled(final String provider) {
				location_callback.didSteadyLocationFailed();
			}
			public void onProviderEnabled(final String provider) {}
			public void onLocationChanged(final Location location) {
				location_callback.didSteadyLocationUpdated(location.getLatitude(), location.getLongitude(), location.getTime());
			}
			public void onStatusChanged(String provider, int status, Bundle extras) {}
		};
	}
}