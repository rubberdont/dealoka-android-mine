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
import android.widget.Button;;

public class QuestionController extends Activity {
	private View vw_frame;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int def = getIntent().getIntExtra("default", 0);
		if(def == 0) {
			setContentView(R.layout.question);
		}else {
			setContentView(R.layout.tac);
		}
		((Button)findViewById(R.id.btn_yes)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				unShow(true);
			}
		});
		((Button)findViewById(R.id.btn_no)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				unShow(false);
			}
		});
		((TextView)findViewById(R.id.lbl_caption)).setText(getIntent().getStringExtra("message"));
		if(def == 1) {
			((TextView)findViewById(R.id.lbl_value)).setText(getIntent().getStringExtra("value"));
		}
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			unShow(false);
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	public void unShow(final boolean result) {
		Intent result_intent = new Intent();
		result_intent.putExtra("result", result);
		setResult(Activity.RESULT_OK, result_intent);
		finish();
	}
	private void setInitial() {
		vw_frame = (View)findViewById(R.id.vw_frame);
		General.setAlpha(vw_frame, 0.7f);
	}
}