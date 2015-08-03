package com.dealoka.lib.control;

import com.dealoka.lib.General;
import com.dealoka.lib.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Alert extends Activity {
	public static Alert instance;
	private TextView alert_title;
	private TextView alert_message;
	private Button btn_ok;
	private String title = General.TEXT_BLANK;
	private String message = General.TEXT_BLANK;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert);
		instance = this;
		if(getIntent().getExtras() != null) {
			title = getIntent().getStringExtra("title");
			message = getIntent().getStringExtra("message");
		}
		setInitial();
	}
	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			close(false);
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	public void close(final boolean ok) {
		Intent result_intent = new Intent();
		if(ok) {
			setResult(Activity.RESULT_OK, result_intent);
		}else {
			setResult(Activity.RESULT_CANCELED, result_intent);
		}
		finish();
	}
	private void setInitial() {
		alert_title = (TextView)findViewById(R.id.alert_title);
		alert_message = (TextView)findViewById(R.id.alert_message);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		if(General.isNotNull(title)) {
			alert_title.setText(title);
		}
		if(General.isNotNull(message)) {
			alert_message.setText(message);
		}
		setEventListener();
	}
	private void setEventListener() {
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				close(true);
			}
		});
	}
}