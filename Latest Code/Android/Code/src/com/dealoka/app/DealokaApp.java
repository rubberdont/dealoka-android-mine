package com.dealoka.app;

import codemagnus.com.dealogeolib.DealokaGeoLib;
import codemagnus.com.dealogeolib.config.GeoConfiguration;

import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.services.AppService;
import com.dealoka.lib.General;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class DealokaApp extends Application {
	private static Context context;
	private static boolean activity_visible = false;
	private static Tracker tracker;
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		setInitial();
		setDealokaGeoLib();
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
		GlobalVariables.unload();
	}
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	public synchronized Tracker getTracker() {
		if(tracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
			tracker = analytics.newTracker(R.xml.analytics);
		}
		return tracker;
	}
	public static Context getAppContext() {
		return context;
	}
	public static void activityResumed() {
		activity_visible = true;
	}
	public static void activityPaused() {
		activity_visible = false;
	}
	public static void activityDestroyed() {
		activity_visible = false;
	}
	public static boolean isVisible() {
		return activity_visible;
	}
	private void setInitial() {
		GlobalVariables.load(context);
		Intent intent_service = new Intent(this, AppService.class);
		startService(intent_service);
		GoogleAnalytics.getInstance(DealokaApp.getAppContext()).enableAutoActivityReports(this);
		setParse();
	}
	private void setParse() {
		Parse.initialize(this, "7cb27Q57wLJ2nESFcHPhNJAfH45iiiS0NLHsnIAq", "lp2BYBA9kBxCrnbKjO3lJYv3f6B49H0OdjJWqVjf");
		ParsePush.subscribeInBackground(General.TEXT_BLANK, new SaveCallback() {
			@Override
			public void done(ParseException ex) {
				if(ex == null) {
				}else {}
			}
		});
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	private void setDealokaGeoLib(){
		GeoConfiguration geoConfiguration = new GeoConfiguration.Builder(DealokaApp.getAppContext())
				.setEndpoint(Config.DEALOKA_GEO_LIB_URL)
				.writeDebugLogs()
                .build();
		DealokaGeoLib.getInstance().init(geoConfiguration);
	}
}