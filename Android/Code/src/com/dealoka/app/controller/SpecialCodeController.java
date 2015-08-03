package com.dealoka.app.controller;

import com.dealoka.app.R;
import com.dealoka.app.call.CallManager;
import com.dealoka.lib.General;

import eu.livotov.zxscan.ZXScanHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;

public class SpecialCodeController extends Activity {
	public static SpecialCodeController instance;
	private static int request_code_scanner = 12345;
	private View vw_frame;
	private ImageButton btn_qrcode;
	private EditText txt_wallet_code;
	private Button btn_apply;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.special_code);
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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == request_code_scanner) {
			String wallet_code = ZXScanHelper.getScannedCode(data);
			doApply(wallet_code);
		}
	}
	public void unShow(final boolean result) {
		Intent result_intent = new Intent();
		result_intent.putExtra("result", result);
		setResult(Activity.RESULT_OK, result_intent);
		finish();
	}
	private void setInitial() {
		vw_frame = (View)findViewById(R.id.vw_frame);
		btn_qrcode = (ImageButton)findViewById(R.id.btn_qrcode);
		txt_wallet_code = (EditText)findViewById(R.id.txt_wallet_code);
		btn_apply = (Button)findViewById(R.id.btn_apply);
		General.setAlpha(vw_frame, 0.7f);
		setEventListener();
	}
	private void setEventListener() {
		btn_qrcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doScanQRCode();
			}
		});
		btn_apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String wallet_code = txt_wallet_code.getText().toString();
				doApply(wallet_code);
			}
		});
	}
	private void doScanQRCode() {
		ZXScanHelper.scan(this, request_code_scanner);
	}
	private void doApply(final String wallet_code) {
		if(!General.isNotNull(wallet_code)) {
			unShow(false);
			return;
		}
		General.showLoading(SpecialCodeController.this);
		CallManager.callSDKGetCoupon(wallet_code);
	}
}