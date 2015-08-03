package com.dealoka.lib.social;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;

public class GooglePlusController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	public static enum GooglePlusGender {
		Male ("Male"),
		Female ("Female"),
		Others ("Others");
		private final String id;
		GooglePlusGender(String id) { this.id = id; }
	    public String getValue() { return id; }
	};
	public static class GOOGLE_PLUS_INFO {
		public String google_plus_id;
		public String email;
		public String name;
		public String birthday;
		public String gender;
	}
	private final static String DEFAULT_BIRTHDAY = "01/01/1990";
	private final static String DEFAULT_GENDER = GooglePlusGender.Male.getValue();
	private Activity activity;
	private GoogleApiClient google_api_client;
	private ConnectionResult connection_result;
	private GooglePlusCallback google_plus_callback;
	private int intent_request_code = 0;
	private boolean intent_in_progress;
	private boolean signin_triggered = false;
	public interface GooglePlusCallback {
		public abstract void didGooglePlusConnected(final GOOGLE_PLUS_INFO gp_info);
		public abstract void didGooglePlusError();
		public abstract void didGooglePlusFailed();
		public abstract void didGooglePlusSignOut();
	}
	public GooglePlusController(final Activity activity, final int intent_request_code, final GooglePlusCallback google_plus_callback) {
		this.activity = activity;
		this.intent_request_code = intent_request_code;
		this.google_plus_callback = google_plus_callback;
		setInitial();
	}
	@Override
    public void onConnectionFailed(ConnectionResult result) {
		if(!intent_in_progress) {
			connection_result = result;
			if(signin_triggered && result.hasResolution()) {
				resolveSignin();
			}
		}
    }
	@Override
	public void onConnected(Bundle connectionHint) {
		signin_triggered = false;
		Person person = Plus.PeopleApi.getCurrentPerson(google_api_client);
		if(person == null) {
			google_plus_callback.didGooglePlusFailed();
			signout();
			return;
		}
		String gender = GooglePlusController.DEFAULT_GENDER;
		switch(person.getGender()) {
			case 0:
				gender = GooglePlusGender.Female.getValue();
				break;
			case 1:
				gender = GooglePlusGender.Male.getValue();
				break;
			case 2:
				gender = GooglePlusGender.Others.getValue();
				break;
		}
		GOOGLE_PLUS_INFO google_plus_info = new GOOGLE_PLUS_INFO();
		google_plus_info.google_plus_id = person.getId();
		google_plus_info.name = person.getDisplayName();
		google_plus_info.email = Plus.AccountApi.getAccountName(google_api_client);
		if(person.hasBirthday()) {
			google_plus_info.birthday = person.getBirthday();
		}else {
			google_plus_info.birthday = GooglePlusController.DEFAULT_BIRTHDAY;
		}
		google_plus_info.gender = gender;
		google_plus_callback.didGooglePlusConnected(google_plus_info);
	}
	@Override
	public void onConnectionSuspended(int cause) {}
	public void onActivityResult(final int request_code, final int response_code) {
		if(request_code == intent_request_code) {
			if(response_code != Activity.RESULT_OK) {
				signin_triggered = false;
			}
			intent_in_progress = false;
			if(response_code == Activity.RESULT_OK) {
				if(!google_api_client.isConnecting()) {
					google_api_client.connect();
				}
			}
		}
	}
	public void signin() {
		signin_triggered = true;
		if(google_api_client.isConnecting()) {
			return;
		}
		resolveSignin();
	}
	public void signout() {
		if(google_api_client.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(google_api_client);
			google_api_client.reconnect();
			google_plus_callback.didGooglePlusSignOut();
		}		
	}
	public void start() {
		google_api_client.connect();
	}
	public void stop() {
		if(google_api_client.isConnected()) {
			google_api_client.disconnect();
		}
	}
	public void share(final String value) {
		final Intent share = new PlusShare.Builder(activity)
			.setType("text/plain")
			.setText(value)
			.getIntent();
		activity.startActivityForResult(share, 0);
	}
	private void resolveSignin() {
		int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if(available != ConnectionResult.SUCCESS) {
			google_plus_callback.didGooglePlusError();
			return;
		}
		if(!connection_result.hasResolution()) {
			return;
		}
		try {
			intent_in_progress = true;
			activity.startIntentSenderForResult(connection_result.getResolution().getIntentSender(), intent_request_code, null, 0, 0, 0);
		}catch(SendIntentException ex) {
			intent_in_progress = false;
			google_api_client.connect();
		}
	}
	private void setInitial() {
		google_api_client = new GoogleApiClient.Builder(activity)
			.addApi(Plus.API, Plus.PlusOptions.builder().build())
			.addScope(Plus.SCOPE_PLUS_PROFILE)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();
	}
}