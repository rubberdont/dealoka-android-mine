package com.dealoka.app.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.call.CallConst;
import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.lib.General;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Login {
	private LinearLayout lay_main;
	private EditText txt_email;
	private EditText txt_password;
	private Button btn_login;
	private Button btn_forgot_password;
	private String email;
	private String password;
	public Login(final View view) {
		setInitial(view);
	}
	public void setAlpha() {
		General.setAlpha(lay_main, Config.alpha);
	}
	private void setInitial(final View view) {
		lay_main = (LinearLayout)view.findViewById(R.id.lay_main);
		txt_email = (EditText)view.findViewById(R.id.txt_email);
		txt_password = (EditText)view.findViewById(R.id.txt_password);
		btn_login = (Button)view.findViewById(R.id.btn_login);
		btn_forgot_password = (Button)view.findViewById(R.id.btn_forgot_password);
		General.setPasswordEditText(txt_password);
		setEventListener();
	}
	private void setEventListener() {
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});
		btn_forgot_password.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forgotPassword();
			}
		});
		txt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					login();
					return true;
				}
				return false;
			}
		});
	}
	private void forgotPassword() {
		openForgot();
	}
	private void login() {
		if(!GlobalController.isNetworkConnected(DealokaApp.getAppContext(), true)) {
			return;
		}
		email = txt_email.getText().toString();
		password = txt_password.getText().toString();
		if(!General.isEmailValid(email)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_email_blank));
			return;
		}
		if(!General.isNotNull(password)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_password_blank));
			return;
		}
		General.executeAsync(new PrefetchLoginData());
	}
	private void setContinue(
			final String token,
			final String email,
			final String first_name,
			final String last_name,
			final String gender,
			final String birthday) {
		Land.instance.setContinue(token, email, first_name, last_name, gender, birthday);
	}
	private void openForgot() {
		Intent forgot_intent = new Intent(DealokaApp.getAppContext(), Forgot.class);
		Land.instance.startActivityForResult(forgot_intent, Config.forgot_activity_result);
	}
	private class PrefetchLoginData extends AsyncTask<Void, Void, Void> {
		private CallController call_controller;
		@Override
		protected void onPostExecute(Void result) {
			call_controller = new CallController(new CallListener() {
				@Override
				public void onCallReturned(String result) {
					General.closeLoading();
					try {
						JSONObject json = new JSONObject(result);
						if(json.getString("message_func").equals(CallConst.api_user_login)) {
							if(json.getString("message_action").equals("USER_LOGIN_SUCCESSFUL")) {
								JSONObject data = new JSONObject(json.getString("message_data"));
								setContinue(
										json.getString("user_token"),
										data.getString("user_email"),
										data.getString("fname"),
										data.getString("lname"),
										data.getString("gender"),
										data.getString("birthday"));
							}else if(json.getString("message_action").equals("USER_LOGIN_FAILED")) {
								GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_login_failed));
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
					call_controller.callUserLogin(email, password);
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