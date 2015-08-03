package com.dealoka.app.activity;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.OfferRedeemWalletParceable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.dealoka.lib.General;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowUp extends Activity {
	public static ShowUp instance;
	private OfferRedeemWalletParceable offer_redeem_wallet;
	private RelativeLayout lay_barcode;
	private LinearLayout lay_main;
	private ImageButton btn_fingerprint;
	private ImageView img_barcode;
	private TextView lbl_offer_name;
	private TextView lbl_barcode;
	private TextView lbl_unique_text;
	private TextView lbl_unique_code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent().getExtras() != null) {
			offer_redeem_wallet = (OfferRedeemWalletParceable)getIntent().getParcelableExtra("offer");
		}
		setContentView(R.layout.show_up);
		instance = this;
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			GlobalController.openQuestion(this, getString(R.string.text_message_showup_confirmation));
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == Config.question_activity_result) {
			if(data.getExtras().getBoolean("result")) {
				GlobalController.sendReviewNotification(
						offer_redeem_wallet.c_fk_offer_id.$value,
						String.format(DealokaApp.getAppContext().getString(R.string.text_label_review_content), offer_redeem_wallet.c_offer_rec.name));
				setContinue();
			}
		}
	}
	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}
	private void setInitial() {
		lbl_offer_name = (TextView)findViewById(R.id.lbl_offer_name);
		lay_barcode = (RelativeLayout)findViewById(R.id.lay_barcode);
		lay_main = (LinearLayout)findViewById(R.id.lay_main);
		btn_fingerprint = (ImageButton)findViewById(R.id.btn_fingerprint);
		img_barcode = (ImageView)findViewById(R.id.img_barcode);
		lbl_barcode = (TextView)findViewById(R.id.lbl_barcode);
		lbl_unique_text = (TextView)findViewById(R.id.lbl_unique_text);
		lbl_unique_code = (TextView)findViewById(R.id.lbl_unique_code);
		setEventListener();
		setFillText();
	}
	private void setEventListener() {
		lay_main.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					doShow();
					return true;
				case MotionEvent.ACTION_UP:
					doUnshow();
					return true;
				case MotionEvent.ACTION_CANCEL:
					doUnshow();
					return true;
				}
				return false;
			}
		});
		btn_fingerprint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doShow();
			}
		});
	}
	private void setFillText() {
		if(offer_redeem_wallet != null) {
			try {
				Bitmap bitmap = GlobalController.encodeAsBitmap(offer_redeem_wallet.unique_code, BarcodeFormat.CODE_128, 300, 80);
				img_barcode.setImageBitmap(bitmap);
			}catch(WriterException ex) {}
			lbl_offer_name.setText(offer_redeem_wallet.c_offer_rec.name);
			lbl_unique_code.setText(offer_redeem_wallet.unique_code);
			lbl_barcode.setText(setBarcodeText(offer_redeem_wallet.unique_code));
		}
	}
	private void doShow() {
		General.vibrate(DealokaApp.getAppContext(), 500);
		lbl_unique_text.setVisibility(View.VISIBLE);
		lbl_unique_code.setVisibility(View.VISIBLE);
		lay_barcode.setVisibility(View.VISIBLE);
	}
	private void doUnshow() {
		lbl_unique_text.setVisibility(View.INVISIBLE);
		lbl_unique_code.setVisibility(View.INVISIBLE);
		lay_barcode.setVisibility(View.INVISIBLE);
	}
	private String setBarcodeText(final String value) {
		StringBuilder sentence = new StringBuilder();
		for(int i = 0; i < value.length(); i++) {
			char letter = value.charAt(i);
			if(i != 0) {
				sentence.append(' ');
			}
		    sentence.append(letter);
		}
		return General.TEXT_BLANK + sentence;
	}
	private void setContinue() {
		Intent result_intent = new Intent();
		setResult(Activity.RESULT_OK, result_intent);
		finish();
	}
}