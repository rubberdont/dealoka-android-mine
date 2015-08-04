package com.dealoka.app.call;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.dealoka.lib.General;
import com.dealoka.app.DealokaApp;
import com.dealoka.app.Home;
import com.dealoka.app.R;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.controller.DualController;
import com.dealoka.app.controller.SDKSpecialCodeController;
import com.dealoka.app.controller.SpecialCodeController;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.model.SDKCouponCode;
import com.dealoka.app.model.SpecialCode;
import com.dealoka.app.url.URLController;
import com.dealoka.app.url.URLController.URLCallback;

public class CallManager {
	private static CallController trigger_ssp_controller;
	private static CallController delete_message_dialog_controller;
	private static CallController send_message_request_controller;
	private static CallController sdk_get_coupon_controller;
	private static CallController special_code_get_coupon_controller;
	private static String device_id = General.TEXT_BLANK;
	private static String imsi = General.TEXT_BLANK;
	@SuppressLint("HandlerLeak")
	public static void callTriggerSSP() {
		if(GlobalVariables.sim_session.DUALSIM()) {
			if(GlobalVariables.imsi_list.contains(GlobalVariables.sim_session.IMSI())) {
				CallManager.device_id = GlobalVariables.sim_session.DEVICEID();
				CallManager.imsi = GlobalVariables.sim_session.IMSI();
			}else {
				if(Home.instance != null) {
					if(DualController.instance != null) {
						return;
					}
					Home.instance.checkDualSIM();
				}
				return;
			}
		}else {
			CallManager.device_id = GlobalVariables.device_id;
			CallManager.imsi = GlobalVariables.imsi;
		}
		if(!General.isNotNull(CallManager.device_id)) {
			CallManager.device_id = General.TEXT_BLANK;
		}
		if(!General.isNotNull(CallManager.imsi)) {
			CallManager.imsi = General.TEXT_BLANK;
		}
		trigger_ssp_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {
				String url = General.TEXT_BLANK;
				try {
					JSONObject json = new JSONObject(result);
					if(json.getString("message_func").equals(CallConst.api_trigger_ssp)) {
						JSONObject message_data = json.getJSONObject("message_data");
						url = message_data.getString("url");
					}
				}catch(JSONException ex) {}
				if(!General.isNotNull(url)) {
					return;
				}
				String parameter = "phone_id=" + CallManager.device_id + "&user_token=" + GlobalVariables.user_session.GetToken();
				url = url + "?" + parameter;
				if(!url.startsWith("http://") && !url.startsWith("https://")) {
					url = "http://" + url;
				}
				callURL(url);
			}
			@Override
			public void onCallFailed() {}
			@Override
			public void onCallConnected() {
				trigger_ssp_controller.callTriggerSSP(CallManager.imsi, GlobalVariables.user_session.GetToken(), CallManager.device_id);
			}
		});
	}
	public static void callDeleteMessageDialog () {
		delete_message_dialog_controller = new CallController(new CallListener() {
			@Override
			public void onCallConnected() {
				delete_message_dialog_controller.callDeleteMessageDialog(GlobalVariables.user_session.GetToken());
			}
			@Override
			public void onCallFailed() {}
			@Override
			public void onCallReturned(String result) {}
		});
	}
	public static void callSendMessageRequest(
			final String offer_id,
			final String phone_number,
			final String email,
			final String message) {
		send_message_request_controller = new CallController(new CallListener() {
			@Override
			public void onCallConnected() {
				send_message_request_controller.callSendMessageRequestController(offer_id, phone_number, email, message, GlobalVariables.user_session.GetToken());
			}
			@Override
			public void onCallFailed() {
				if(Home.instance != null) {
					Home.instance.showToast(DealokaApp.getAppContext().getString(R.string.text_err_message));
				}
			}
			@Override
			public void onCallReturned(String result) {
				if(Home.instance != null) {
					Home.instance.showToast(DealokaApp.getAppContext().getString(R.string.text_message_sent));
				}
			}
		});
	}
	public static void callSDKGetCoupon(final String wallet_code) {
		sdk_get_coupon_controller = new CallController(new CallListener() {
			@Override
			public void onCallConnected() {
				sdk_get_coupon_controller.callSDKGetCouponController(wallet_code, GlobalVariables.user_session.GetToken());
			}
			@Override
			public void onCallFailed() {
				General.closeLoading();
			}
			@Override
			public void onCallReturned(String result) {
				General.closeLoading();
				final SDKCouponCode sdk_coupon_code = new SDKCouponCode(result);
				if(sdk_coupon_code.message_action.equals("SDK_MOBILE_GET_COUPON_SUCCESS")) {
					if(!CallOfferRedeemWallet.addOfferRedeemWallet(sdk_coupon_code.message_data)) {
						GlobalController.showAlert(DealokaApp.getAppContext().getString(R.string.text_err_system));
						return;
					}
					GlobalController.showAlert(Home.instance, sdk_coupon_code.message_desc);
					if(SDKSpecialCodeController.instance != null) {
						SDKSpecialCodeController.instance.unShow(true);
					}
				}else if(sdk_coupon_code.message_action.equals("SDK_MOBILE_GET_COUPON_FAILED")) {
					GlobalController.showAlert(Home.instance, sdk_coupon_code.message_desc);
					if(SDKSpecialCodeController.instance != null) {
						SDKSpecialCodeController.instance.unShow(false);
					}
				}
			}
		});
	}
	public static void callSpecialCodeGetCoupon(final String wallet_code) {
		special_code_get_coupon_controller = new CallController(new CallListener() {
			@Override
			public void onCallConnected() {
				special_code_get_coupon_controller.callSpecialCodeGetCouponController(
						wallet_code,
						GlobalVariables.user_session.GetToken(),
						GlobalController.getDeviceID());
			}
			@Override
			public void onCallFailed() {
				General.closeLoading();
			}
			@Override
			public void onCallReturned(String result) {
				General.closeLoading();
				final SpecialCode special_code = new SpecialCode(result);
				if(special_code.message_action.equals("SPECIAL_CODE_MOBILE_GET_COUPON_SUCCESS")) {
					if(!CallOfferRedeemWallet.addOfferRedeemWallet(special_code.message_data)) {
						GlobalController.showAlert(DealokaApp.getAppContext().getString(R.string.text_err_system));
						return;
					}
					GlobalController.showAlert(Home.instance, special_code.message_desc);
					if(SpecialCodeController.instance != null) {
						SpecialCodeController.instance.unShow(true);
					}
				}else if(special_code.message_action.equals("SPECIAL_CODE_MOBILE_GET_COUPON_FAILED")) {
					GlobalController.showAlert(Home.instance, special_code.message_desc);
					if(SpecialCodeController.instance != null) {
						SpecialCodeController.instance.unShow(false);
					}
				}
			}
		});
	}
	private static void callURL(final String url) {
		if(!General.isNotNull(url)) {
			return;
		}
		General.executeAsync(new URLController.URLTask(url, new URLCallback() {
			@Override
			public void didURLResponse(String response) {}
			@Override
			public void didURLFailed() {}
		}));
	}
}