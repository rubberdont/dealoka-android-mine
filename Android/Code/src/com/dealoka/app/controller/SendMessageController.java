package com.dealoka.app.controller;

import com.dealoka.app.R;
import com.dealoka.app.call.CallManager;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.General;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendMessageController extends Activity {
	private View lay_frame;
	private EditText txt_phone_number;
	private EditText txt_email;
	private EditText txt_message;
	private Button btn_send;
	private String offer_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message);
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			unload(false);
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	private void setInitial() {
		lay_frame = (View)findViewById(R.id.lay_frame);
		txt_phone_number = (EditText)findViewById(R.id.txt_phone_number);
		txt_email = (EditText)findViewById(R.id.txt_email);
		txt_message = (EditText)findViewById(R.id.txt_message);
		txt_phone_number.setText(GlobalVariables.feature_session.Phone());
		txt_email.setText(GlobalVariables.user_session.GetEmail());
		btn_send = (Button)findViewById(R.id.btn_send);
		General.setAlpha(lay_frame, 0.7f);
		setEventListener();
		if(getIntent().getExtras() != null) {
			offer_id = getIntent().getStringExtra("offer_id");
		}
	}
	private void setEventListener() {
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSend();
			}
		});
	}
	private void unload(final boolean result) {
		Intent result_intent = new Intent();
		result_intent.putExtra("result", result);
		setResult(Activity.RESULT_OK, result_intent);
		finish();
	}
	private void doSend() {
		final String phone_number = txt_phone_number.getText().toString();
		final String email = txt_email.getText().toString();
		final String message = txt_message.getText().toString();
		if(!General.isNotNull(phone_number)) {
			return;
		}else if(!General.isEmailValid(email)) {
			return;
		}else if(!General.isNotNull(message)) {
			return;
		}
		GlobalVariables.feature_session.Phone(phone_number);
		CallManager.callSendMessageRequest(offer_id, phone_number, email, message);
		unload(true);
	}
}