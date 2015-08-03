package com.dealoka.lib.control;

import com.dealoka.lib.General;
import com.dealoka.lib.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class Loading extends Activity {
	public static Loading instance;
	private View loading_frame;
	private TextView loading_title;
	private TextView loading_message;
	private String title = General.TEXT_BLANK;
	private String message = General.TEXT_BLANK;
	private boolean cancelable = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		instance = this;
		if(getIntent().getExtras() != null) {
			title = getIntent().getStringExtra("title");
			message = getIntent().getStringExtra("message");
			cancelable = getIntent().getBooleanExtra("cancelable", false);
		}
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(cancelable) {
				close(false);
			}
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	public void close() {
		close(true);
	}
	public void changeTitle(final String title) {
		if(General.isNotNull(title)) {
			loading_title.setText(title);
		}
	}
	public void changeMessage(final String message) {
		if(General.isNotNull(message)) {
			loading_message.setText(message);
		}
	}
	private void setInitial() {
		loading_frame = (View)findViewById(R.id.loading_frame);
		loading_title = (TextView)findViewById(R.id.loading_title);
		loading_message = (TextView)findViewById(R.id.loading_message);
		General.setAlpha(loading_frame, 0.7f);
		if(General.isNotNull(title)) {
			loading_title.setText(title);
		}
		if(General.isNotNull(message)) {
			loading_message.setText(message);
		}
	}
	private void close(final boolean result) {
		Intent result_intent = new Intent();
		result_intent.putExtra("result", result);
		setResult(Activity.RESULT_OK, result_intent);
		finish();
	}
}