package com.dealoka.app.general.map;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;

import com.dealoka.app.OfferMap;
import com.google.android.gms.maps.model.LatLng;
import com.dealoka.lib.General;

import android.os.AsyncTask;

public class GetDirectionAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList<LatLng>> {
	public static final String USER_CURRENT_LAT = "user_current_lat";
	public static final String USER_CURRENT_LONG = "user_current_long";
	public static final String DESTINATION_LAT = "destination_lat";
	public static final String DESTINATION_LONG = "destination_long";
	public static final String DIRECTIONS_MODE = "directions_mode";
	private OfferMap activity;
	private Exception exception;
	public GetDirectionAsyncTask(final OfferMap activity) {
		this.activity = activity;
	}
	@Override
	public void onPreExecute() {
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onPostExecute(ArrayList result) {
		General.closeLoading();
		if(exception == null) {
			activity.handleGetDirectionsResult(result);
		}else {
			processException();
		}
	}
	@Override
	protected ArrayList<LatLng> doInBackground(Map<String, String>... params) {
		Map<String, String> paramMap = params[0];
		try {
			LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
			LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
			GMapV2Direction md = new GMapV2Direction();
			Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
			ArrayList<LatLng> directionPoints = md.getDirection(doc);
			return directionPoints;
		}catch(Exception ex) {
			exception = ex;
			return null;
		}
	}
	private void processException() {}
}
