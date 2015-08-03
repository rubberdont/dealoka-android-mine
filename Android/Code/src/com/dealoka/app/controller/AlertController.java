package com.dealoka.app.controller;

import com.dealoka.app.R;
import com.dealoka.lib.General;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;

public class AlertController extends Activity {
	private View vw_frame;
	private Button btn_ok;
	private TextView lbl_caption;
	private String message = General.TEXT_BLANK;
	private int request_code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert);
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			unShow();
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	public void unShow() {
		if(request_code > 0) {
			Intent result_intent = new Intent();
			result_intent.putExtra("result", true);
			setResult(Activity.RESULT_OK, result_intent);
		}
		finish();
	}
	private void setInitial() {
		vw_frame = (View)findViewById(R.id.vw_frame);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		lbl_caption = (TextView)findViewById(R.id.lbl_caption);
		if(getIntent().getExtras() != null) {
			message = getIntent().getStringExtra("message");
			request_code = getIntent().getIntExtra("request_code", 0);
		}
		General.setAlpha(vw_frame, 0.7f);
		lbl_caption.setText(message);
		setEventListener();
	}
	private void setEventListener() {
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				unShow();
			}
		});
	}
}