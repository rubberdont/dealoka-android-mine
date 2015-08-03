package com.dealoka.app.controller;

import com.dealoka.app.R;
import com.dealoka.lib.General;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class TCController extends Activity {
	private View lay_frame;
	private WebView web_main;
	private Button btn_ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tc);
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
		web_main = (WebView)findViewById(R.id.web_main);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		General.setAlpha(lay_frame, 0.7f);
		setEventListener();
		web_main.loadUrl("file:///android_asset/tc.htm");
	}
	private void setEventListener() {
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				unload(true);
			}
		});
	}
	private void unload(final boolean result) {
		Intent result_intent = new Intent();
		result_intent.putExtra("result", result);
		setResult(Activity.RESULT_OK, result_intent);
		finish();
	}
}