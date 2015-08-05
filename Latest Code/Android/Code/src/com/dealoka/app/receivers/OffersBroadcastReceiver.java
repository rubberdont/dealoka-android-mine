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
	@Override
	public void onReceive(Context context, Intent intent) {
	        try {
	        	JSONArray savedOfferArray = new JSONArray();
	        	String offers = intent.getExtras().getString(DealokaService.NEW_OFFERS);
	        	JSONArray temp = new JSONArray(offers);
	        	storeOffers(savedOfferArray, temp);
	        	OfferGeo offerInfo = new OfferGeo(General.TEXT_BLANK, temp.getString(0));
	        	Intent listIntent = new Intent(context, PopupOffersController.class);
	    		listIntent.putParcelableArrayListExtra(DealokaService.NEW_OFFERS, broadcastOffers(savedOfferArray));
	    		if(!PopupOffersController.popupIsActive){
	    			GlobalController.sendOffersNotification(context, listIntent, offerInfo);
	    		}else{
	    			PopupOffersController.instance.updatePopupList(listIntent);
	    		}
			} catch (JSONException e) {e.printStackTrace();}
	}
	private void storeOffers(JSONArray offers, JSONArray temp) throws JSONException{
		for (int i = 0; i < temp.length(); i++) {
			offers.put(temp.getJSONObject(i));
		}
	}
	private ArrayList<OfferGeo> broadcastOffers(JSONArray savedOfferArray) throws JSONException{
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
