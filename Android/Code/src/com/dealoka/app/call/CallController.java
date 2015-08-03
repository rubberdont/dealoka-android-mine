package com.dealoka.app.call;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dealoka.app.general.Config;
import com.dealoka.app.general.DDPController;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.call.GetOffer;
import com.dealoka.app.model.call.OfferValidateCode;
import com.dealoka.app.model.call.RedeemCouponCode;
import com.dealoka.app.model.call.TrackObject;
import com.google.gson.Gson;

public class CallController {
	private final Gson gson = new Gson();
	private DDPController ddp_controller;
	public CallController(final CallListener call_listener) {
		ddp_controller = new DDPController();
		ddp_controller.init(call_listener);
	}
	public void disconnect() {
		if(ddp_controller != null) {
			ddp_controller.disconnect();
		}
	}
	public void callUserLogin(final String username, final String password) {
		ddp_controller.do_call(CallConst.api_user_login, new String[]{username, password});
	}
	public void callUserRegistration(
			final String username,
			final String email,
			final String password,
			final String gender,
			final String birthday,
			final List<String> category,
			final float latitude,
			final float longitude) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("username", username);
		message.put("email", email);
		message.put("password", password);
		message.put("gender", gender);
		message.put("birthday", birthday);
		message.put("category", category);
		message.put("lat", latitude);
		message.put("lng", longitude);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_user_registration, new String[] {json});
	}
	public void callUserFacebookRegistration(
			final String fb_id,
			final String email,
			final String fname,
			final String lname,
			final String gender,
			final float lng,
			final float lat,
			final String birthday) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("fb_id", fb_id);
		message.put("email", email);
		message.put("fname", fname);
		message.put("lname", lname);
		message.put("gender", gender);
		message.put("lng", lng);
		message.put("lat", lat);
		message.put("birthday", birthday);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_user_facebook_registration, new String[] {json});
	}
	public void callUserForgotPassword(final String email) {
		ddp_controller.do_call(CallConst.api_user_forgot_password, new String[] {email});
	}
	public void callGetTopOffer(final String user_token) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("user_token", user_token);
		message.put("phone_id", GlobalController.getDeviceID());
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_get_top_offer, new String[] {json});
	}
	public void callOfferSetDownloaded(final String offer_location_id) {
		ddp_controller.do_call(CallConst.api_offer_set_downloaded, new String[] {offer_location_id});
	}
	public void callOfferTrackAction(final TrackObject offer_track_action) {
		Map<String, Object> bts_object = new HashMap<String, Object>();
		bts_object.put("lng", offer_track_action.bts_object.lng);
		bts_object.put("lat", offer_track_action.bts_object.lat);
		bts_object.put("orient", offer_track_action.bts_object.orient);
		bts_object.put("signal", offer_track_action.bts_object.signal);
		bts_object.put("cid", offer_track_action.bts_object.cid);
		bts_object.put("lac", offer_track_action.bts_object.lac);
		bts_object.put("mcc", offer_track_action.bts_object.mcc);
		bts_object.put("mnc", offer_track_action.bts_object.mnc);
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("category", offer_track_action.category);
		message.put("lng", offer_track_action.lng);
		message.put("lat", offer_track_action.lat);
		message.put("user_token", offer_track_action.user_token);
		message.put("offer_id", offer_track_action.offer_id);
		message.put("action_type", offer_track_action.action_type);
		message.put("merchant_id", offer_track_action.merchant_id);
		message.put("gender", offer_track_action.gender);
		message.put("bts_object", bts_object);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_offer_track_action, new String[] {json});
	}
	public void callRedeemCouponCode(final RedeemCouponCode redeem_coupon_code) {
		Map<String, Object> bts_object = new HashMap<String, Object>();
		bts_object.put("lng", redeem_coupon_code.c_track_object.bts_object.lng);
		bts_object.put("lat", redeem_coupon_code.c_track_object.bts_object.lat);
		bts_object.put("orient", redeem_coupon_code.c_track_object.bts_object.orient);
		bts_object.put("signal", redeem_coupon_code.c_track_object.bts_object.signal);
		bts_object.put("cid", redeem_coupon_code.c_track_object.bts_object.cid);
		bts_object.put("lac", redeem_coupon_code.c_track_object.bts_object.lac);
		bts_object.put("mcc", redeem_coupon_code.c_track_object.bts_object.mcc);
		bts_object.put("mnc", redeem_coupon_code.c_track_object.bts_object.mnc);
		Map<String, Object> track_object = new HashMap<String, Object>();
		track_object.put("category", redeem_coupon_code.c_track_object.category);
		track_object.put("lng", redeem_coupon_code.c_track_object.lng);
		track_object.put("lat", redeem_coupon_code.c_track_object.lat);
		track_object.put("user_token", redeem_coupon_code.c_track_object.user_token);
		track_object.put("offer_id", redeem_coupon_code.c_track_object.offer_id);
		track_object.put("action_type", redeem_coupon_code.c_track_object.action_type);
		track_object.put("merchant_id", redeem_coupon_code.c_track_object.merchant_id);
		track_object.put("gender", redeem_coupon_code.c_track_object.gender);
		track_object.put("bts_object", bts_object);
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("offer_id", redeem_coupon_code.offer_id);
		message.put("user_token", redeem_coupon_code.user_token);
		message.put("phone_id", redeem_coupon_code.phone_id);
		message.put("version", redeem_coupon_code.version);
		message.put("phone_type", redeem_coupon_code.phone_type);
		message.put("track_object", track_object);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_redeem_coupon_code, new String[] {json});
	}
	public void callRedeemSpecialCouponCode(final RedeemCouponCode redeem_coupon_code) {
		Map<String, Object> bts_object = new HashMap<String, Object>();
		bts_object.put("lng", redeem_coupon_code.c_track_object.bts_object.lng);
		bts_object.put("lat", redeem_coupon_code.c_track_object.bts_object.lat);
		bts_object.put("orient", redeem_coupon_code.c_track_object.bts_object.orient);
		bts_object.put("signal", redeem_coupon_code.c_track_object.bts_object.signal);
		bts_object.put("cid", redeem_coupon_code.c_track_object.bts_object.cid);
		bts_object.put("lac", redeem_coupon_code.c_track_object.bts_object.lac);
		bts_object.put("mcc", redeem_coupon_code.c_track_object.bts_object.mcc);
		bts_object.put("mnc", redeem_coupon_code.c_track_object.bts_object.mnc);
		Map<String, Object> track_object = new HashMap<String, Object>();
		track_object.put("category", redeem_coupon_code.c_track_object.category);
		track_object.put("lng", redeem_coupon_code.c_track_object.lng);
		track_object.put("lat", redeem_coupon_code.c_track_object.lat);
		track_object.put("user_token", redeem_coupon_code.c_track_object.user_token);
		track_object.put("offer_id", redeem_coupon_code.c_track_object.offer_id);
		track_object.put("action_type", redeem_coupon_code.c_track_object.action_type);
		track_object.put("merchant_id", redeem_coupon_code.c_track_object.merchant_id);
		track_object.put("gender", redeem_coupon_code.c_track_object.gender);
		track_object.put("bts_object", gson.toJson(bts_object));
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("offer_id", redeem_coupon_code.offer_id);
		message.put("user_token", redeem_coupon_code.user_token);
		message.put("phone_id", redeem_coupon_code.phone_id);
		message.put("imsi", redeem_coupon_code.imsi);
		message.put("version", redeem_coupon_code.version);
		message.put("phone_type", redeem_coupon_code.phone_type);
		message.put("special_coupon_id", redeem_coupon_code.special_coupon_id);
		message.put("offer_special_brand", redeem_coupon_code.offer_special_brand);
		message.put("track_object", track_object);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_redeem_special_coupon_code, new String[] {json});
	}
	public void callOfferValidateCode(final OfferValidateCode offer_validate_code) {
		Map<String, Object> bts_object = new HashMap<String, Object>();
		bts_object.put("lng", offer_validate_code.c_track_object.bts_object.lng);
		bts_object.put("lat", offer_validate_code.c_track_object.bts_object.lat);
		bts_object.put("orient", offer_validate_code.c_track_object.bts_object.orient);
		bts_object.put("signal", offer_validate_code.c_track_object.bts_object.signal);
		bts_object.put("cid", offer_validate_code.c_track_object.bts_object.cid);
		bts_object.put("lac", offer_validate_code.c_track_object.bts_object.lac);
		bts_object.put("mcc", offer_validate_code.c_track_object.bts_object.mcc);
		bts_object.put("mnc", offer_validate_code.c_track_object.bts_object.mnc);
		Map<String, Object> track_object = new HashMap<String, Object>();
		track_object.put("category", offer_validate_code.c_track_object.category);
		track_object.put("lng", offer_validate_code.c_track_object.lng);
		track_object.put("lat", offer_validate_code.c_track_object.lat);
		track_object.put("user_token", offer_validate_code.c_track_object.user_token);
		track_object.put("offer_id", offer_validate_code.c_track_object.offer_id);
		track_object.put("action_type", offer_validate_code.c_track_object.action_type);
		track_object.put("merchant_id", offer_validate_code.c_track_object.merchant_id);
		track_object.put("gender", offer_validate_code.c_track_object.gender);
		track_object.put("bts_object", bts_object);
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("offer_id", offer_validate_code.offer_id);
		message.put("user_token", offer_validate_code.user_token);
		message.put("md5hash", offer_validate_code.md5hash);
		message.put("unique_code", offer_validate_code.unique_code);
		message.put("track_object", track_object);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_offer_validate_code, new String[] {json});
	}
	public void callGetOffer(final GetOffer get_offer) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("user_token", get_offer.user_token);
		message.put("category", get_offer.category);
		message.put("phone_id", get_offer.phone_id);
		message.put("latitude", get_offer.latitude);
		message.put("longitude", get_offer.longitude);
		message.put("start", get_offer.start);
		message.put("limit", get_offer.limit);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_get_offer, new String[] {json});
	}
	public void callTriggerSSP(final String imsi, final String user_token, final String phone_id) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("imsi", imsi);
		message.put("user_token", user_token);
		message.put("phone_id", phone_id);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_trigger_ssp, new String[] {json});
	}
	public void callDeleteMessageDialog(final String user_token) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("user_token", user_token);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_delete_message_dialog, new String[] {json});
	}
	public void callSendMessageRequestController(
			final String offer_id,
			final String phone_number,
			final String email,
			final String msg,
			final String user_token) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("offer_id", offer_id);
		message.put("phone_number", phone_number);
		message.put("email", email);
		message.put("message", msg);
		message.put("user_token", user_token);
		message.put("phone_type", Config.phone_type);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_send_message_request, new String[] {json});
	}
	public void callSDKGetCouponController(
			final String wallet_code,
			final String user_token) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("wallet_code", wallet_code);
		message.put("user_token", user_token);
		String json = gson.toJson(message);
		ddp_controller.do_call(CallConst.api_sdk_get_coupon, new String[] {json});
	}
 	public static interface CallListener {
		public void onCallConnected();
		public void onCallReturned(final String result);
		public void onCallFailed();
	}
}