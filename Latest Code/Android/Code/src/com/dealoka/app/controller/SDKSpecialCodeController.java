package com.dealoka.app.controller;

import com.dealoka.app.R;
import com.dealoka.app.call.CallManager;
import com.dealoka.lib.General;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;

public class SDKSpecialCodeController extends Activity {
	public static SDKSpecialCodeController instance;
	private View vw_frame;
	private EditText txt_wallet_code;
	private Button btn_apply;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdk_special_code);
		instance = this;
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
		txt_wallet_code = (EditText)findViewById(R.id.txt_wallet_code);
		btn_apply = (Button)findViewById(R.id.btn_apply);
		General.setAlpha(vw_frame, 0.7f);
		setEventListener();
	}
	private void setEventListener() {
		btn_apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String wallet_code = txt_wallet_code.getText().toString();
				doApply(wallet_code);
			}
		});
	}
	private void doApply(final String wallet_code) {
		if(!General.isNotNull(wallet_code)) {
			unShow(false);
			return;
		}
		General.showLoading(SDKSpecialCodeController.this);
		CallManager.callSDKGetCoupon(wallet_code);
	}
}