package com.dealoka.app.activity;

import org.json.JSONException;
import org.json.JSONObject;

import codemagnus.com.dealogeolib.utils.LogUtils;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.call.CallConst;
import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.lib.General;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Forgot extends Activity {
	private View vw_frame;
	private EditText txt_email;
	private Button btn_send;
	private Button btn_cancel;
	public static Forgot activity;
	private CallController call_controller;
	public Forgot() {
		activity = this;
		call_controller = new CallController(call_listener);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot);
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Intent result_intent = new Intent();
			result_intent.putExtra("result", false);
			setResult(Activity.RESULT_OK, result_intent);
			unShow();
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	public void unShow() {
		finish();
	}
	private void setInitial() {
		vw_frame = (View)findViewById(R.id.vw_frame);
		btn_send = (Button)findViewById(R.id.btn_send);
		btn_cancel = (Button)findViewById(R.id.btn_cancel);
		txt_email = (EditText)findViewById(R.id.txt_email);
		General.setAlpha(vw_frame, Config.alpha);
		setEventListener();
	}
	private void setEventListener() {
		txt_email.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND) {
					forgot();
					return true;
				}
				return false;
			}
		});
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forgot();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent result_intent = new Intent();
				result_intent.putExtra("result", false);
				setResult(Activity.RESULT_OK, result_intent);
				unShow();
			}
		});
	}
	private void forgot() {
		if(!GlobalController.isNetworkConnected(DealokaApp.getAppContext(), true)) {
			LogUtils.LOGD("FORGOT", "isNetworkConnected");
			return;
		}
		final String email = ((EditText)findViewById(R.id.txt_email)).getText().toString();
		if(!General.isEmailValid(email)) {
			GlobalController.showAlert(this, getString(R.string.text_err_email_blank));
			LogUtils.LOGD("FORGOT", "!isEmailValid");
			return;
		}
		call_controller.callUserForgotPassword(email);
	}
	private CallListener call_listener = new CallListener() {
		@Override
		public void onCallFailed() {}
		@Override
		public void onCallConnected() {}
		@Override
		public void onCallReturned(String result) {
			try {
				JSONObject json = new JSONObject(result);
				if(json.getString("message_func").equals(CallConst.api_user_registration)) {
					if(json.getString("message_action").equals("REQUEST_PASSWORD_SUCCESSFUL")) {
						GlobalController.showAlert(Forgot.activity, json.getString("message_desc"));
					}else if(json.getString("message_action").equals("REQUEST_PASSWORD_FAILED")) {
						GlobalController.showAlert(Forgot.activity, json.getString("message_desc"));
					}
				}
			}catch(JSONException ex) {}
		}
	};
}