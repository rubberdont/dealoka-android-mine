package com.dealoka.app.receivers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import codemagnus.com.dealogeolib.service.DealokaService;

import com.dealoka.app.controller.PopupOffersController;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.OfferGeo;
import com.dealoka.lib.General;

public class OffersBroadcastReceiver  extends BroadcastReceiver{
	public static final String TAG = "OffersBroadcastReceiver";
	public static JSONArray savedOfferArray = new JSONArray();
	@Override
	public void onReceive(Context context, Intent intent) {
	        try {
	        	String offers = intent.getExtras().getString(DealokaService.NEW_OFFERS);
	        	JSONArray temp = new JSONArray(offers);
	        	storeOffers(context, temp);
	        	OfferGeo offerInfo = new OfferGeo(General.TEXT_BLANK, temp.getString(0));
	        	Intent listIntent = new Intent(context, PopupOffersController.class);
	    		listIntent.putParcelableArrayListExtra(DealokaService.NEW_OFFERS, broadcastOffers());
	    		if(!PopupOffersController.popupIsActive){
	    			GlobalController.sendOffersNotification(context, listIntent, offerInfo);
	    		}else{
	    			PopupOffersController.instance.updatePopupList(listIntent);
	    		}
			} catch (JSONException e) {e.printStackTrace();}
	}
	private void storeOffers(Context context, JSONArray temp) throws JSONException{
		for (int i = 0; i < temp.length(); i++) {
			savedOfferArray.put(temp.getJSONObject(i));
		}
	}
	private ArrayList<OfferGeo> broadcastOffers() throws JSONException{
		ArrayList<OfferGeo> offersList = new ArrayList<OfferGeo>();
		for (int i = 0; i < savedOfferArray.length(); i++) {
			JSONObject json = savedOfferArray.getJSONObject(i);
			OfferGeo newOffer = new OfferGeo(
					json.getString("_id"), 
					json.toString());
			offersList.add(newOffer);
		}
		return offersList;
	}
}
