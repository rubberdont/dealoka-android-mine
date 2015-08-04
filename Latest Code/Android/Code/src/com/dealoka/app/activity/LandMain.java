package com.dealoka.app.activity;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.call.CallConst;
import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.General;
import com.dealoka.lib.social.FacebookController.FB_INFO;
import com.dealoka.lib.social.FacebookController.FacebookSigninCallback;
import com.dealoka.lib.social.GooglePlusController.GOOGLE_PLUS_INFO;
import com.dealoka.lib.social.GooglePlusController;
import com.dealoka.lib.social.GooglePlusController.GooglePlusCallback;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LandMain {
	public GooglePlusController google_plus_controller;
	private static final List<String> PERMISSIONS = Arrays.asList("email", "user_birthday");
	private View view;
	private Button btn_register;
	private Button btn_login;
	private Button btn_facebook;
	private Button btn_google_plus;
	private Button btn_tutorial;
	public LandMain(final View view) {
		this.view = view;
		setInitial();
	}
	private void setInitial() {
		btn_register = (Button)view.findViewById(R.id.btn_register);
		btn_login = (Button)view.findViewById(R.id.btn_login);
		btn_facebook = (Button)view.findViewById(R.id.btn_facebook);
		btn_google_plus = (Button)view.findViewById(R.id.btn_google_plus);
		btn_tutorial = (Button)view.findViewById(R.id.btn_tutorial);
		setEventListener();
		tutorial();
	}
	private void setEventListener() {
		btn_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});
		btn_facebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				facebook();
			}
		});
		btn_google_plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				googlePlus();
			}
		});
		btn_tutorial.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tutorial();
			}
		});
	}
	private void tutorial() {
		GlobalController.openTutorial(Land.instance);
	}
	private void register() {
		Land.instance.setPage(0);
	}
	private void login() {
		Land.instance.setPage(2);
	}
	private void facebook() {
		GlobalVariables.facebook_controller.signin(Land.instance, PERMISSIONS, new FacebookSigninCallback() {
			@Override
			public void didFBSignIn(FB_INFO fb_info) {
				if(!General.isNotNull(fb_info.email)) {
					fb_info.email = fb_info.fb_id + "@dealoka.com";
				}
				final ExecuteFBData execute_fb_data = new ExecuteFBData(fb_info);
				General.executeAsync(execute_fb_data);
			}
			@Override
			public void didFBSignInError() {
				GlobalVariables.facebook_controller.signout();
				GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_facebook));
			}
			@Override
			public void didFBSignInCancel() {}
		});
	}
	private void googlePlus() {
		google_plus_controller = new GooglePlusController(
				Land.instance,
				Config.google_plus_activity_result,
				new GooglePlusCallback() {
			@Override
			public void didGooglePlusSignOut() {}
			@Override
			public void didGooglePlusFailed() {}
			@Override
			public void didGooglePlusError() {}
			@Override
			public void didGooglePlusConnected(GOOGLE_PLUS_INFO gp_info) {
				final ExecuteGooglePlusData execute_google_plus_data = new ExecuteGooglePlusData(gp_info);
				General.executeAsync(execute_google_plus_data);
			}
		});
		google_plus_controller.start();
		google_plus_controller.signin();
	}
	private class ExecuteFBData extends AsyncTask<Void, Void, Void> {
		private CallController call_controller;
		private FB_INFO fb_info;
		public ExecuteFBData(final FB_INFO fb_info) {
			this.fb_info = fb_info;
		}
		@Override
		protected void onPostExecute(Void result) {
			call_controller = new CallController(new CallListener() {
				@Override
				public void onCallReturned(String result) {
					General.closeLoading();
					try {
						JSONObject json = new JSONObject(result);
						if(json.getString("message_func").equals(CallConst.api_user_facebook_registration)) {
							if(json.getString("message_action").equals("USER_LOGIN_SUCCESSFUL")) {
								JSONObject data = new JSONObject(json.getString("message_data"));
								Land.instance.setContinue(
										json.getString("user_token"),
										data.getString("user_email"),
										data.getString("fname"),
										data.getString("lname"),
										data.getString("gender"),
										data.getString("birthday"));
							}else if(json.getString("message_action").equals("USER_LOGIN_FAILED")) {
								GlobalController.showAlert(Land.instance, json.getString("message_desc"));
							}else if(json.getString("message_action").equals("USER_REGISTRATION_FAILED")) {
								GlobalController.showAlert(Land.instance, json.getString("message_desc"));
							}
						}
					}catch(JSONException ex) {}
				}
				@Override
				public void onCallFailed() {
					General.closeLoading();
					GlobalController.showAlert(Land.instance, Land.instance.getString(R.string.text_err_system));
				}
				@Override
				public void onCallConnected() {
					call_controller.callUserFacebookRegistration(
							fb_info.fb_id,
							fb_info.email,
							fb_info.first_name,
							fb_info.last_name,
							fb_info.gender,
							GlobalVariables.longitude,
							GlobalVariables.latitude,
							fb_info.birthday);
				}
			});
		}
		@Override
		protected void onPreExecute() {
			General.showLoading(Land.instance);
		}
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}
	private class ExecuteGooglePlusData extends AsyncTask<Void, Void, Void> {
		private CallController call_controller;
		private GOOGLE_PLUS_INFO google_plus_info;
		public ExecuteGooglePlusData(final GOOGLE_PLUS_INFO google_plus_info) {
			this.google_plus_info = google_plus_info;
		}
		@Override
		protected void onPostExecute(Void result) {
			call_controller = new CallController(new CallListener() {
				@Override
				public void onCallReturned(String result) {
					General.closeLoading();
					try {
						JSONObject json = new JSONObject(result);
						if(json.getString("message_func").equals(CallConst.api_user_facebook_registration)) {
							if(json.getString("message_action").equals("USER_LOGIN_SUCCESSFUL")) {
								JSONObject data = new JSONObject(json.getString("message_data"));
								Land.instance.setContinue(
										json.getString("user_token"),
										data.getString("user_email"),
										data.getString("fname"),
										data.getString("lname"),
										data.getString("gender"),
										data.getString("birthday"));
							}else if(json.getString("message_action").equals("USER_LOGIN_FAILED")) {
								GlobalController.showAlert(Land.instance, json.getString("message_desc"));
							}else if(json.getString("message_action").equals("USER_REGISTRATION_FAILED")) {
								GlobalController.showAlert(Land.instance, json.getString("message_desc"));
							}
						}
					}catch(JSONException ex) {}
				}
				@Override
				public void onCallFailed() {
					General.closeLoading();
				}
				@Override
				public void onCallConnected() {
					call_controller.callUserFacebookRegistration(
							google_plus_info.google_plus_id,
							google_plus_info.email,
							google_plus_info.name,
							General.TEXT_BLANK,
							google_plus_info.gender,
							GlobalVariables.longitude,
							GlobalVariables.latitude,
							google_plus_info.birthday);
				}
			});
		}
		@Override
		protected void onPreExecute() {
			General.showLoading(Land.instance);
		}
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}
}