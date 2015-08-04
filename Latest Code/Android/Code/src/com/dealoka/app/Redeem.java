package com.dealoka.app;

import com.dealoka.app.R;
import com.dealoka.app.activity.ShowUp;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.model.OfferRedeemWallet;
import com.dealoka.app.model.OfferRedeemWalletParceable;
import com.dealoka.lib.General;

import eu.livotov.zxscan.ZXScanHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Redeem extends Fragment {
	public static Redeem instance;
	public static final String RedeemFragment = "REDEEM_FRAGMENT";
	private static int request_code_scanner = 12345;
	private static int request_code_confirm = 12346;
	private ImageButton btn_header;
	private ImageButton btn_qrcode;
	private Button btn_valid;
	private EditText txt_code;
	private OfferRedeemWallet offer_redeem_wallet;
	private boolean validation = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		offer_redeem_wallet = (OfferRedeemWallet)getArguments().getSerializable("offer");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		View view = inflater.inflate(R.layout.redeem, container, false);
		if(offer_redeem_wallet != null) {
			setInitial(view);
		}else {
			if(Home.instance != null) {
				Home.instance.pop();
			}
		}
		return view;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == request_code_scanner) {
			String code = ZXScanHelper.getScannedCode(data);
			compareCode(code);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		if(validation) {
			if(Home.instance != null) {
				Home.instance.confirmValidation(offer_redeem_wallet.id);
			}
		}
	}
	@Override
	public void onDestroyView() {
		if(WalletDetails.instance != null) {
			WalletDetails.instance.closeRedeemFragment();
		}
		super.onDestroyView();
		instance = null;
	}
	public void SyncExpired(final String id) {
		if(offer_redeem_wallet.id.equals(id)) {
			if(Home.instance != null) {
				Home.instance.confirmExpired();
			}
		}
	}
	public void SyncChanges() {
		if(ShowUp.instance != null) {
			ShowUp.instance.finish();
		}
		GlobalController.pop();
	}
	private void setInitial(final View view) {
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		btn_qrcode = (ImageButton)view.findViewById(R.id.btn_qrcode);
		btn_valid = (Button)view.findViewById(R.id.btn_valid);
		txt_code = (EditText)view.findViewById(R.id.txt_code);
		setEventListener();
		btn_header.setSelected(Home.instance.isPopHeader());
	}
	private void setEventListener() {
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
		btn_qrcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doScanQRCode();
			}
		});
		btn_valid.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doValidation();
			}
		});
		txt_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_GO) {
					doValidation();
					return true;
				}
				return false;
			}
		});
	}
	private void doScanQRCode() {
		if(Home.instance != null) {
			Home.instance.setPause(false);
			ZXScanHelper.scan(Home.instance, request_code_scanner);
		}
	}
	private void doValidation() {
		final String code = txt_code.getText().toString();
		if(!General.isNotNull(code)) {
			return;
		}
		compareCode(code);
	}
	private void doNext() {
		if(Home.instance == null) {
			return;
		}
		Home.instance.setPause(false);
		final OfferRedeemWalletParceable offer_redeem_wallet_parcable = new OfferRedeemWalletParceable(offer_redeem_wallet);
		Intent intent = new Intent(DealokaApp.getAppContext(), ShowUp.class);
		intent.putExtra("offer", offer_redeem_wallet_parcable);
		Home.instance.startActivityForResult(intent, request_code_confirm);
	}
	@SuppressLint("DefaultLocale")
	private void compareCode(final String code) {
		if(!offer_redeem_wallet.merchant_code.toUpperCase().equals(code.toUpperCase())) {
			GlobalController.showAlert(getActivity(), getString(R.string.text_message_wrong_code));
			return;
		}
		long redeem = GlobalVariables.feature_session.Redeem();
		redeem++;
		GlobalVariables.feature_session.Redeem(redeem);
		GlobalVariables.query_controller.addBatchWallet(
				offer_redeem_wallet.id,
				offer_redeem_wallet.c_fk_offer_id.$value,
				offer_redeem_wallet.md5hash,
				offer_redeem_wallet.unique_code,
				offer_redeem_wallet.c_offer_rec.category,
				offer_redeem_wallet.c_fk_merchant_id.$value,
				GlobalVariables.user_session.GetToken());
		GlobalVariables.query_controller.validatedWallet(offer_redeem_wallet.id);
		if(Home.instance != null) {
			Home.instance.executeBatchWallet();
		}
		validation = true;
		General.executeAsync(new PrefetchNextData());
	}
	private class PrefetchNextData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			General.closeLoading();
			doNext();
		}
		@Override
		protected void onPreExecute() {
			if(Home.instance != null) {
				General.showLoading(Home.instance);
			}
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			}catch(InterruptedException ex) {}
			return null;
		}
	}
}