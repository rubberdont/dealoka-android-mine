package com.dealoka.app;

import com.dealoka.app.R;
import com.dealoka.app.activity.Land;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.subs.SubsManager;
import com.dealoka.lib.controller.LocationController;
import com.dealoka.lib.controller.LocationController.LocationCallback;
import com.dealoka.lib.General;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Main extends Activity implements LocationCallback {
	private final int reload_activity_result = 1001;
	private ImageView img_main;
	private LocationController location_controller;
	public Main() {
		location_controller = new LocationController(DealokaApp.getAppContext());
		location_controller.setLocationCallback(this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Tracker t = ((DealokaApp)getApplication()).getTracker();
		t.setScreenName("Android");
		t.send(new HitBuilders.AppViewBuilder().build());
		GlobalVariables.setFontInit();
		prefetchData();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK && requestCode == Config.question_activity_result) {
			if(data.getExtras().getBoolean("result")) {
				GlobalController.openSettingsProvider(this);
			}else {
				location_controller.loadLocationWithOrder();
			}
		}else if(resultCode == Activity.RESULT_CANCELED && requestCode == Config.provider_settings_activity_result) {
			location_controller.loadLocationWithOrder(this);
		}else if(resultCode == Activity.RESULT_OK && requestCode == reload_activity_result) {
			if(data.getExtras().getBoolean("result")) {
				if(!GlobalController.isNetworkConnected(DealokaApp.getAppContext(), false)) {
					GlobalController.showAlert(this, getString(R.string.text_message_connection_lost), reload_activity_result);
				}else {
					location_controller.loadLocationWithOrder();
				}
			}
		}
	}
	@Override
	public void didLocationUpdated(float latitude, float longitude) {
		GlobalVariables.latitude = latitude;
		GlobalVariables.longitude = longitude;
		setContinue();
	}
	@Override
	public void didLocationFailed() {
		if(GlobalController.isLocationRetrieved()) {
			setContinue();
		}else {
			if(!LocationController.isGPSNetworkProviderEnabled(DealokaApp.getAppContext())) {
				GlobalController.openQuestion(this, getString(R.string.text_message_provider_disabled));
			}else {
				GlobalController.showAlert(this, getString(R.string.text_err_no_user_location), reload_activity_result);
			}
		}
	}
	@Override
	public void isProviderEnabled(boolean enabled) {}
	private void prefetchData() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setInitial();
			}
		});
	}
	private void setInitial() {
		img_main = (ImageView)findViewById(R.id.img_main);
		Animation pulse = AnimationUtils.loadAnimation(DealokaApp.getAppContext(), R.anim.pulse);
		img_main.startAnimation(pulse);
		com.facebook.appevents.AppEventsLogger.activateApp(DealokaApp.getAppContext(), DealokaApp.getAppContext().getString(R.string.facebook_app_id));
		if(Config.log_enabled) {
			General.getSignature(DealokaApp.getAppContext());
		}
		if(GlobalController.isNetworkConnected(DealokaApp.getAppContext(), false)) {
			location_controller.loadLocationWithOrder();
		}else {
			if(GlobalController.isLocationRetrieved()) {
				setContinue();
			}else {
				if(LocationController.isGPSNetworkProviderEnabled(DealokaApp.getAppContext())) {
					GlobalController.showAlert(this, getString(R.string.text_message_connection_lost), reload_activity_result);
				}else {
					GlobalController.openQuestion(this, getString(R.string.text_message_provider_disabled));
				}
			}
		}
	}
	private void setContinue() {
		final Intent next_intent;
		if(GlobalVariables.user_session.isSession()) {
			next_intent = new Intent(DealokaApp.getAppContext(), Home.class);
			if(getIntent().getExtras() != null) {
				if(General.isNotNull(getIntent().getExtras().getString(Config.message_notif))) {
					next_intent.putExtra(Config.message_notif, getIntent().getExtras().getString(Config.message_notif));
				}else if(General.isNotNull(getIntent().getExtras().getString(Config.review_notif))) {
					next_intent.putExtra(Config.review_notif, getIntent().getExtras().getString(Config.review_notif));
				}else if(getIntent().getExtras().getSerializable(Config.popup_notif) != null){
					next_intent.putExtra(Config.popup_notif, getIntent().getExtras().getParcelable(Config.popup_notif));
				}
			}
			SubsManager.Reconnect();
			next_intent.addCategory(Intent.CATEGORY_HOME);
			next_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}else {
			General.setBadgeCount(getApplicationContext(), 0, Main.class.getName());
			next_intent = new Intent(DealokaApp.getAppContext(), Land.class);
			next_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		if(next_intent != null) {
			startActivity(next_intent);
		}
		finish();
	}
}