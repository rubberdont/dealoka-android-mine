package com.dealoka.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.dealoka.lib.controller.LocationController;
import com.dealoka.lib.controller.LocationController.LocationCallback;
import com.dealoka.lib.General;
import com.dealoka.app.R;
import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.controller.PopupOffersController.PopupOffer;
import com.dealoka.app.controller.ShareController;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.general.ReviewController;
import com.dealoka.app.model.Offer;
import com.dealoka.app.model.OfferGeo;
import com.dealoka.app.model.OfferTopOffer;
import com.dealoka.app.model.call.ActionType;
import com.dealoka.app.model.call.RedeemCouponCode;
import com.dealoka.app.model.call.TrackObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Download extends Fragment implements LocationCallback {
	public static Download instance;
	public static final String DownloadFragment = "DOWNLOAD_FRAGMENT";
	private enum Download_Status {
		NONE, DOWNLOAD, DOWNLOAD_SPECIAL
	};
	private Offer offer;
	private LocationController location_controller;
	private CallController call_controller;
	private LayoutInflater inflater;
	private LinearLayout lay_wallet_box;
	private Button btn_wallet;
	private TextView lbl_wallet_point;
	private Button btn_wallet_point;
	private RelativeLayout lay_special;
	private ImageButton btn_header;
	private TextView lbl_title;
	private TextView lbl_offer_title;
	private TextView lbl_address;
	private Button btn_map;
	private Button btn_download;
	private Button btn_send_message;
	private Button btn_share;
	private ImageView img_downloaded;
	private ImageButton img_offer;
	private TextView lbl_downloaded;
	private TextView lbl_available;
	private ImageButton btn_review;
	private Button btn_description;
	private Button btn_condition;
	private Detail detail_fragment;
	private OfferMap offer_map_fragment;
	private Download_Status download_status = Download_Status.NONE;
	private int downloaded;
	private int available;
	private long coupon_id = 0;
	private String offer_special_brand;
	private String imsi = General.TEXT_BLANK;
	public Download() {
		location_controller = new LocationController(DealokaApp.getAppContext());
		location_controller.setLocationCallback(this);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments().getString("from").equals(
				OffersDetails.OffersDetailsFragment)) {
			if(OffersDetails.instance == null) {
				return;
			}
			final OfferGeo offer_geo = (OfferGeo) getArguments()
					.getSerializable("offer");
			offer = new Offer(offer_geo.c_fk_offer_id.$value,
					offer_geo.c_offer_rec.category,
					offer_geo.c_fk_merchant_id.$value,
					offer_geo.c_merchant_rec.merchant_name,
					offer_geo.c_merchant_point.Latitude,
					offer_geo.c_merchant_point.Longitude, offer_geo.address,
					offer_geo.c_offer_rec.validity_end,
					offer_geo.c_offer_rec.name, offer_geo.c_offer_rec.summary,
					offer_geo.image_prefix, offer_geo.c_offer_rec.image,
					offer_geo.c_offer_rec.conditions,
					offer_geo.main_button_render, offer_geo.imsi_check_flag,
					offer_geo.c_offer_rec.special_promos);
			downloaded = offer_geo.redeemed;
			available = offer_geo.available;
		}else if(getArguments().getString("from").equals(
				Offers.OffersFragment)) {
			if(Offers.instance == null) {
				return;
			}
			final OfferTopOffer offer_top_offer = (OfferTopOffer) getArguments()
					.getSerializable("offer");
			offer = new Offer(offer_top_offer.c_fk_offer_id.$value,
					offer_top_offer.c_offer_rec.category,
					offer_top_offer.c_fk_merchant_id.$value,
					offer_top_offer.c_merchant_rec.merchant_name,
					offer_top_offer.c_merchant_point.Latitude,
					offer_top_offer.c_merchant_point.Longitude,
					offer_top_offer.address,
					offer_top_offer.c_offer_rec.validity_end,
					offer_top_offer.c_offer_rec.name,
					offer_top_offer.c_offer_rec.summary,
					offer_top_offer.image_prefix,
					offer_top_offer.c_offer_rec.image,
					offer_top_offer.c_offer_rec.conditions,
					offer_top_offer.main_button_render,
					offer_top_offer.imsi_check_flag,
					offer_top_offer.c_offer_rec.special_promos);
			downloaded = offer_top_offer.redeemed;
			available = offer_top_offer.available;
		}else if(getArguments().getString("from").equals(
				PopupOffer.TAG)){
			final OfferGeo offer_geo = (OfferGeo) getArguments()
					.getSerializable("offer");
			offer = new Offer(offer_geo.c_fk_offer_id.$value,
					offer_geo.c_offer_rec.category,
					offer_geo.c_fk_merchant_id.$value,
					offer_geo.c_merchant_rec.merchant_name,
					offer_geo.c_merchant_point.Latitude,
					offer_geo.c_merchant_point.Longitude, offer_geo.address,
					offer_geo.c_offer_rec.validity_end,
					offer_geo.c_offer_rec.name, offer_geo.c_offer_rec.summary,
					offer_geo.image_prefix, offer_geo.c_offer_rec.image,
					offer_geo.c_offer_rec.conditions,
					offer_geo.main_button_render, offer_geo.imsi_check_flag,
					offer_geo.c_offer_rec.special_promos);
			downloaded = offer_geo.redeemed;
			available = offer_geo.available;
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		this.inflater = inflater;
		try {
			final View view = inflater.inflate(R.layout.download, container,
					false);
			if(offer != null) {
				setInitial(view);
			}else {
				if (Home.instance != null) {
					Home.instance.pop();
				}
			}
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onDestroyView() {
		instance = null;
		if(OffersDetails.instance != null) {
			OffersDetails.instance.closeDownloadFragment();
		}else if(Offers.instance != null) {
			Offers.instance.closeDownloadFragment();
		}
		super.onDestroyView();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK
				&& requestCode == Config.question_activity_result) {
			if(data.getExtras().getBoolean("result")) {
				if(download_status == Download_Status.DOWNLOAD) {
					General.showLoading(Home.instance);
					doRedeem();
				}else if(download_status == Download_Status.DOWNLOAD_SPECIAL) {
					General.showLoading(Home.instance);
					doRedeemSpecialCoupon();
				}
			}
		}
	}
	@Override
	public void didLocationUpdated(float latitude, float longitude) {
		GlobalVariables.latitude = latitude;
		GlobalVariables.longitude = longitude;
		doCall();
	}
	@Override
	public void didLocationFailed() {
		General.showToast(DealokaApp.getAppContext(),
				getString(R.string.text_err_no_user_location), 1000);
		General.closeLoading();
	}
	@Override
	public void isProviderEnabled(boolean enabled) {
	}
	public void closeDetailFragment() {
		detail_fragment = null;
	}
	public void closeOfferMapFragment() {
		offer_map_fragment = null;
	}
	private void setInitial(final View view) {
		lay_wallet_box = (LinearLayout) view.findViewById(R.id.lay_wallet_box);
		btn_wallet = (Button) view.findViewById(R.id.btn_wallet);
		lbl_wallet_point = (TextView) view.findViewById(R.id.lbl_wallet_point);
		btn_wallet_point = (Button) view.findViewById(R.id.btn_wallet_point);
		lay_special = (RelativeLayout) view.findViewById(R.id.lay_special);
		btn_header = (ImageButton) view.findViewById(R.id.btn_header);
		lbl_title = (TextView) view.findViewById(R.id.lbl_title);
		img_offer = (ImageButton) view.findViewById(R.id.img_offer);
		lbl_offer_title = (TextView) view.findViewById(R.id.lbl_offer_title);
		lbl_address = (TextView) view.findViewById(R.id.lbl_address);
		btn_map = (Button) view.findViewById(R.id.btn_map);
		btn_download = (Button) view.findViewById(R.id.btn_download);
		btn_send_message = (Button) view.findViewById(R.id.btn_send_message);
		btn_share = (Button) view.findViewById(R.id.btn_share);
		img_downloaded = (ImageView) view.findViewById(R.id.img_downloaded);
		lbl_downloaded = (TextView) view.findViewById(R.id.lbl_downloaded);
		lbl_available = (TextView) view.findViewById(R.id.lbl_available);
		btn_review = (ImageButton) view.findViewById(R.id.btn_review);
		btn_description = (Button) view.findViewById(R.id.btn_description);
		btn_condition = (Button) view.findViewById(R.id.btn_condition);
		setEventListener();
		btn_header.setSelected(Home.instance.isPopHeader());
		lbl_title.setText(offer.merchant_name);
		lbl_offer_title.setText(offer.name);
		lbl_address.setText(offer.merchant_address);
		if(CallOfferRedeemWallet.isOfferNotDownloaded(offer.id)) {
			prefetchButtonRedeem(false);
			prefetchButtonRedeemSpecialCoupon(false);
			updateWalletPoint(true);
		}else {
			updateWalletPoint(false);
		}
		if(!offer.main_button_render) {
			btn_download.setVisibility(View.GONE);
		}
		setImageView(offer.image_prefix, offer.image);
		setSpecialPromos();
		prefetchData();
	}
	private void setEventListener() {
		btn_wallet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doWallet();
			}
		});
		btn_wallet_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doWallet();
			}
		});
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
		btn_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doMap();
			}
		});
		btn_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doShare();
			}
		});
		btn_review.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doReview();
			}
		});
		btn_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Home.instance == null) {
					return;
				}
				if(available > 0) {
					download_status = Download_Status.DOWNLOAD;
					GlobalController.openQuestion(
									Home.instance,
									DealokaApp.getAppContext().getString(R.string.text_message_download_confirmation),
									offer.summary);
				}else {
					GlobalController.showAlert(Home.instance, DealokaApp.getAppContext().getString(R.string.text_err_no_available));
				}
			}
		});
		btn_send_message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSendMessage();
			}
		});
		GradientDrawable download_drawable = (GradientDrawable) btn_download
				.getBackground();
		download_drawable.setColor(Color.parseColor("#00930a"));
		btn_description.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDescription();
			}
		});
		btn_condition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCondition();
			}
		});
	}
	private void setImageView(final String image_prefix, final String url) {
		String image_url = url.replace("SIZEREQ_", "og");
		image_url = image_prefix + image_url;
		ImageLoader.getInstance().displayImage(image_url, img_offer,
				GlobalController.getOption(true, true));
	}
	@SuppressLint("InflateParams")
	@SuppressWarnings("rawtypes")
	private void setSpecialPromos() {
		if(offer.special_promos == null) {
			prefetchButtonRedeemSpecialCoupon(false);
			return;
		}
		if(offer.special_promos.size() <= 0) {
			prefetchButtonRedeemSpecialCoupon(false);
			return;
		}
		int id = 0;
		List<Button> iv = new ArrayList<Button>();
		final Iterator iterator = offer.special_promos.iterator();
		while(iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			final HashMap<String, Object> special_promos = (HashMap<String, Object>)iterator.next();
			if(Boolean.parseBoolean(special_promos.get("offer_button_render").toString())) {
				View view = inflater.inflate(R.layout.button_view, null, false);
				final Button button = (Button) view.findViewById(R.id.button);
				button.setId(id + 1);
				button.setText(special_promos.get("offer_button_text")
						.toString());
				button.setTextColor(Color.parseColor(special_promos.get(
						"offer_button_text_color").toString()));
				GradientDrawable drawable = (GradientDrawable) button
						.getBackground();
				drawable.setColor(Color.parseColor(special_promos.get(
						"offer_button_background_color").toString()));
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(Home.instance == null) {
							return;
						}
						if(GlobalVariables.sim_session.DUALSIM()) {
							imsi = GlobalVariables.sim_session.IMSI();
						}else {
							imsi = GlobalVariables.imsi;
						}
						if(!General.isNotNull(imsi)) {
							imsi = General.TEXT_BLANK;
						}
						if(offer.imsi_check_flag) {
							if(!General.isNotNull(imsi)) {
								General.showToast(DealokaApp.getAppContext(),
										getString(R.string.text_err_no_imsi),
										1000);
								return;
							}
						}
						coupon_id = 0;
						offer_special_brand = General.TEXT_BLANK;
						if(available > 0) {
							download_status = Download_Status.DOWNLOAD_SPECIAL;
							coupon_id = Long.parseLong(special_promos.get(
									"promo_id").toString());
							offer_special_brand = special_promos.get(
									"offer_special_brand").toString();
							GlobalController
									.openQuestion(
											Home.instance,
											getString(R.string.text_message_download_confirmation),
											offer.summary);
						} else {
							GlobalController.showAlert(Home.instance,
									getString(R.string.text_err_no_available));
						}
					}
				});
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL,
						RelativeLayout.TRUE);
				if (id > 0) {
					params.addRule(RelativeLayout.BELOW, iv.get(iv.size() - 1)
							.getId());
					params.setMargins(0, 17, 0, 0);
				}
				lay_special.addView(button, params);
				button.setPadding(0, 15, 0, 15);
				iv.add(button);
				id++;
			}
		}
	}
	private void doWallet() {
		if (Home.instance == null) {
			return;
		}
		Home.instance.openWallet();
	}
	private void doSendMessage() {
		if (Home.instance != null) {
			GlobalController.openSendMessage(Home.instance, offer.id);
		}
	}
	private void doShare() {
		String image_url = offer.image.replace("SIZEREQ_", "og");
		image_url = offer.image_prefix + image_url;
		ShareController.share(getActivity(),
				getString(R.string.text_message_title_share), String.format(
						getString(R.string.text_message_share), offer.name));
	}
	private void doMap() {
		if(offer_map_fragment != null) {
			return;
		}
		if(!LocationController.isGPSNetworkProviderEnabled(DealokaApp
				.getAppContext())) {
			if(Home.instance != null) {
				GlobalController.openSettingsProvider(Home.instance);
			}
			return;
		}
		Bundle args = new Bundle();
		args.putSerializable("offer", offer);
		offer_map_fragment = new OfferMap();
		offer_map_fragment.setArguments(args);
		final FragmentTransaction fragment_transaction = getFragmentManager()
				.beginTransaction();
		fragment_transaction.setCustomAnimations(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right, R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);
		fragment_transaction
				.add(R.id.lay_child, offer_map_fragment,
						OfferMap.OfferMapFragment).addToBackStack(null)
				.commit();
	}
	private void doReview() {
		ReviewController.open(this, offer.id);
	}
	private void doRedeem() {
		if(!GlobalController.isNetworkConnected(DealokaApp.getAppContext(),
				true)) {
			General.closeLoading();
			return;
		}
		if(GlobalController.isLocationRetrieved()) {
			doCall();
		}else {
			location_controller.loadLocationWithNetwork();
		}
	}
	private void doRedeemSpecialCoupon() {
		if(!GlobalController.isNetworkConnected(DealokaApp.getAppContext(),
				true)) {
			General.closeLoading();
			return;
		}
		if(GlobalController.isLocationRetrieved()) {
			doCall();
		}else {
			location_controller.loadLocationWithNetwork();
		}
	}
	private void doDescription() {
		if(detail_fragment != null) {
			return;
		}
		Bundle args = new Bundle();
		args.putString("set", "description");
		args.putSerializable("offer", offer);
		detail_fragment = new Detail();
		detail_fragment.setArguments(args);
		final FragmentTransaction fragment_transaction = getFragmentManager()
				.beginTransaction();
		fragment_transaction.setCustomAnimations(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right, R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);
		fragment_transaction
				.add(R.id.lay_child, detail_fragment, Detail.DetailFragment)
				.addToBackStack(null).commit();
	}
	private void doCondition() {
		if(detail_fragment != null) {
			return;
		}
		Bundle args = new Bundle();
		args.putString("set", "condition");
		args.putSerializable("offer", offer);
		detail_fragment = new Detail();
		detail_fragment.setArguments(args);
		final FragmentTransaction fragment_transaction = getFragmentManager()
				.beginTransaction();
		fragment_transaction.setCustomAnimations(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right, R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);
		fragment_transaction
				.add(R.id.lay_child, detail_fragment, Detail.DetailFragment)
				.addToBackStack(null).commit();
	}
	private void doCall() {
		if(download_status == Download_Status.DOWNLOAD) {
			redeemCoupon();
		}else if(download_status == Download_Status.DOWNLOAD_SPECIAL) {
			redeemSpecialCoupon();
		}
	}
	private void redeemCoupon() {
		call_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {
				if(download_status == Download_Status.DOWNLOAD) {
					final com.dealoka.app.model.RedeemCouponCode redeem_coupon_code = new com.dealoka.app.model.RedeemCouponCode(
							result);
					if(redeem_coupon_code.message_action
							.equals("REDEEM_OFFER_SUCCESS")) {
						if(!CallOfferRedeemWallet.addOfferRedeemWallet(
								redeem_coupon_code.c_message_data._id,
								redeem_coupon_code.c_message_data.offer_item)) {
							GlobalController.showAlert(DealokaApp
									.getAppContext().getString(
											R.string.text_err_system));
							return;
						}
						available--;
						downloaded++;
						prefetchData();
						prefetchButtonRedeem(false);
						prefetchButtonRedeemSpecialCoupon(false);
						updateWalletPoint(true);
						GlobalController
								.showAlert(redeem_coupon_code.message_desc);
					}else if(redeem_coupon_code.message_action
							.equals("REDEEM_OFFER_ALREADY_REDEEMED")) {
						prefetchButtonRedeem(false);
						prefetchButtonRedeemSpecialCoupon(false);
						updateWalletPoint(true);
						GlobalController
								.showAlert(redeem_coupon_code.message_desc);
					}else if(redeem_coupon_code.message_action
							.equals("REDEEM_OFFER_FAILED")) {
						GlobalController
								.showAlert(redeem_coupon_code.message_desc);
					}
				}
				General.closeLoading();
			}
			@Override
			public void onCallFailed() {
				General.closeLoading();
				GlobalController.showAlert(DealokaApp.getAppContext()
						.getString(R.string.text_err_system));
			}
			@Override
			public void onCallConnected() {
				final RedeemCouponCode redeem_coupon_code = new RedeemCouponCode(
						offer.id, GlobalVariables.user_session.GetToken(),
						GlobalController.getDeviceID(), GlobalController
								.getVersion(), Config.phone_type,
						new TrackObject(offer.category,
								GlobalVariables.longitude,
								GlobalVariables.latitude,
								GlobalVariables.user_session.GetToken(),
								offer.id, ActionType.REDEEM, offer.merchant_id,
								GlobalVariables.user_session.GetGender(),
								new TrackObject.BTSObject(
										GlobalVariables.longitude,
										GlobalVariables.latitude,
										GlobalVariables.orientation,
										GlobalVariables.signal,
										GlobalVariables.cid,
										GlobalVariables.lac,
										GlobalVariables.mcc,
										GlobalVariables.mnc)));
				call_controller.callRedeemCouponCode(redeem_coupon_code);
			}
		});
	}
	private void redeemSpecialCoupon() {
		call_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {
				if(download_status == Download_Status.DOWNLOAD_SPECIAL) {
					final com.dealoka.app.model.RedeemCouponCode redeem_coupon_code = new com.dealoka.app.model.RedeemCouponCode(
							result);
					if(redeem_coupon_code.message_action
							.equals("REDEEM_OFFER_SUCCESS")) {
						if(!CallOfferRedeemWallet.addOfferRedeemWallet(
								redeem_coupon_code.c_message_data._id,
								redeem_coupon_code.c_message_data.offer_item)) {
							GlobalController.showAlert(DealokaApp
									.getAppContext().getString(
											R.string.text_err_system));
							return;
						}
						available--;
						downloaded++;
						prefetchData();
						prefetchButtonRedeem(false);
						prefetchButtonRedeemSpecialCoupon(false);
						updateWalletPoint(true);
						GlobalController
								.showAlert(redeem_coupon_code.message_desc);
					}else if(redeem_coupon_code.message_action
							.equals("REDEEM_OFFER_ALREADY_REDEEMED")) {
						prefetchButtonRedeem(false);
						prefetchButtonRedeemSpecialCoupon(false);
						updateWalletPoint(true);
						GlobalController
								.showAlert(redeem_coupon_code.message_desc);
					}else if(redeem_coupon_code.message_action
							.equals("REDEEM_OFFER_FAILED")) {
						GlobalController
								.showAlert(redeem_coupon_code.message_desc);
					}
				}
				General.closeLoading();
			}
			@Override
			public void onCallFailed() {
				General.closeLoading();
				GlobalController.showAlert(DealokaApp.getAppContext()
						.getString(R.string.text_err_system));
			}
			@Override
			public void onCallConnected() {
				final RedeemCouponCode redeem_coupon_code = new RedeemCouponCode(
						offer.id, GlobalVariables.user_session.GetToken(),
						GlobalController.getDeviceID(), imsi, GlobalController
								.getVersion(), Config.phone_type, coupon_id,
						offer_special_brand, new TrackObject(offer.category,
								GlobalVariables.longitude,
								GlobalVariables.latitude,
								GlobalVariables.user_session.GetToken(),
								offer.id, ActionType.REDEEM, offer.merchant_id,
								GlobalVariables.user_session.GetGender(),
								new TrackObject.BTSObject(
										GlobalVariables.longitude,
										GlobalVariables.latitude,
										GlobalVariables.orientation,
										GlobalVariables.signal,
										GlobalVariables.cid,
										GlobalVariables.lac,
										GlobalVariables.mcc,
										GlobalVariables.mnc)));
				call_controller.callRedeemSpecialCouponCode(redeem_coupon_code);
			}
		});
	}
	public void updateWalletPoint(final boolean visible) {
		if (Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lbl_wallet_point.setText(General.TEXT_BLANK
						+ CallOfferRedeemWallet.getWalletPoint());
				if (visible) {
					lay_wallet_box.setVisibility(View.VISIBLE);
				} else {
					lay_wallet_box.setVisibility(View.GONE);
				}
			}
		});
	}
	private void prefetchData() {
		if (Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lbl_available.setText(General.TEXT_BLANK + available);
				lbl_downloaded.setText(General.TEXT_BLANK + downloaded);
			}
		});
	}
	private void prefetchButtonRedeem(final boolean visible) {
		if (Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (visible) {
					btn_download.setVisibility(View.VISIBLE);
					img_downloaded.setVisibility(View.GONE);
				} else {
					btn_download.setVisibility(View.GONE);
					img_downloaded.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	private void prefetchButtonRedeemSpecialCoupon(final boolean visible) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(visible) {
					lay_special.setVisibility(View.VISIBLE);
				}else {
					lay_special.setVisibility(View.GONE);
				}
			}
		});
	}
}