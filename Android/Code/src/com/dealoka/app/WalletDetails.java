package com.dealoka.app;

import com.dealoka.app.R;
import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.OfferRedeemWallet;
import com.dealoka.lib.General;
import com.dealoka.lib.GeneralCalendar;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WalletDetails extends Fragment {
	public static WalletDetails instance;
	public static final String WalletDetailsFragment = "WALLET_DETAILS_FRAGMENT";
	private RelativeLayout lay_timer;
	private ImageButton btn_header;
	private TextView lbl_timer;
	private TextView lbl_offer_title;
	private TextView lbl_offer_summary;
	private TextView lbl_summary;
	private ImageButton img_offer;
	private Button btn_validate;
	private Button btn_send_message;
	private ImageView img_validated;
	private ImageView img_expired;
	private Redeem redeem_fragment;
	private DownTimer down_timer;
	private OfferRedeemWallet offer_redeem_wallet;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int index = getArguments().getInt("index");
		if(CallOfferRedeemWallet.offer_redeem_wallet_db == null) {
			return;
		}
		if(CallOfferRedeemWallet.offer_redeem_wallet_db.size() > index) {
			offer_redeem_wallet = CallOfferRedeemWallet.offer_redeem_wallet_db.get(index);
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		try {
			final View view = inflater.inflate(R.layout.wallet_details, container, false);
			setInitial(view);
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onDestroy() {
		if(down_timer != null) {
			down_timer.cancel();
		}
		super.onDestroy();
	}
	@Override
	public void onDestroyView() {
		instance = null;
		if(Wallet.instance != null) {
			Wallet.instance.closeWalletDetailsFragment();
		}
		super.onDestroyView();
	}
	public void setValidated() {
		btn_validate.setVisibility(View.GONE);
		img_validated.setVisibility(View.VISIBLE);
		lay_timer.setVisibility(View.GONE);
	}
	public void setExpired() {
		btn_validate.setVisibility(View.GONE);
		img_expired.setVisibility(View.VISIBLE);
		if(img_validated.isShown()) {
			img_validated.setVisibility(View.GONE);
		}
		lay_timer.setVisibility(View.GONE);
	}
	public void closeRedeemFragment() {
		redeem_fragment = null;
	}
	private void setInitial(final View view) {
		if(offer_redeem_wallet == null) {
			Home.instance.pop();
			return;
		}
		lay_timer = (RelativeLayout)view.findViewById(R.id.lay_timer);
		lbl_timer = (TextView)view.findViewById(R.id.lbl_timer);
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		lbl_offer_title = (TextView)view.findViewById(R.id.lbl_offer_title);
		lbl_offer_summary = (TextView)view.findViewById(R.id.lbl_offer_summary);
		lbl_summary = (TextView)view.findViewById(R.id.lbl_summary);
		img_offer = (ImageButton)view.findViewById(R.id.img_offer);
		btn_validate = (Button)view.findViewById(R.id.btn_validate);
		btn_send_message = (Button)view.findViewById(R.id.btn_send_message);
		img_validated = (ImageView)view.findViewById(R.id.validated);
		img_expired = (ImageView)view.findViewById(R.id.expired);
		setEventListener();
		General.setAlpha(lay_timer, 0.8f);
		btn_header.setSelected(Home.instance.isPopHeader());
		lbl_offer_title.setText(offer_redeem_wallet.c_offer_rec.name);
		lbl_offer_summary.setText(offer_redeem_wallet.c_offer_rec.summary);
		lbl_summary.setText(offer_redeem_wallet.c_offer_rec.conditions);
		GradientDrawable validate_drawable = (GradientDrawable)btn_validate.getBackground();
		validate_drawable.setColor(Color.parseColor("#00930a"));
		setImageView(offer_redeem_wallet.c_offer_rec.image);
		if(offer_redeem_wallet.expired) {
			setExpired();
		}else if(offer_redeem_wallet.validated) {
			setValidated();
		}else {
			final long next_day = offer_redeem_wallet.redeem_time + (offer_redeem_wallet.day_multiplier * 60 * 60 * 24 * 1000);
			if(next_day > offer_redeem_wallet.c_offer_rec.validity_end) {
				down_timer = new DownTimer(offer_redeem_wallet.c_offer_rec.validity_end - GeneralCalendar.getUTCTimestamp());
			}else {
				down_timer = new DownTimer(next_day - GeneralCalendar.getUTCTimestamp());
			}
			down_timer.start();
		}
		if(offer_redeem_wallet.online_flag) {
			btn_validate.setText(offer_redeem_wallet.button_name);
		}
	}
	private void setEventListener() {
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
		btn_validate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doValidate();
			}
		});
		btn_send_message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSendMessage();
			}
		});
	}
	private void setImageView(final String url) {
		String image_url = url.replace("SIZEREQ_", "og");
		image_url = offer_redeem_wallet.image_prefix + image_url;
		ImageLoader.getInstance().displayImage(image_url, img_offer, GlobalController.getOption(true, true));
	}
	private void doValidate() {
		if(offer_redeem_wallet.online_flag) {
			if(Home.instance == null) {
				return;
			}
			GlobalController.openWeb(Home.instance, offer_redeem_wallet.go_to_url);
			return;
		}
		if(redeem_fragment != null) {
			return;
		}
		final Bundle args = new Bundle();
		args.putSerializable("offer", offer_redeem_wallet);
		redeem_fragment = new Redeem();
		redeem_fragment.setArguments(args);
		final FragmentTransaction fragment_transaction = getFragmentManager().beginTransaction();
		fragment_transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		fragment_transaction.add(R.id.lay_child, redeem_fragment, Redeem.RedeemFragment).addToBackStack(null).commit();
	}
	private void doSendMessage() {
		if(Home.instance != null) {
			GlobalController.openSendMessage(Home.instance, offer_redeem_wallet.c_fk_offer_id.$value);
		}
	}
	private class DownTimer extends CountDownTimer {
		public DownTimer(final long timer) {
			super(timer, 1000);
		}
		@Override
		public void onTick(long millisUntilFinished) {
			lbl_timer.setText(GlobalController.getHHMMSSFromTimeStamp(millisUntilFinished));
		}
		@Override
		public void onFinish() {}
	}
}