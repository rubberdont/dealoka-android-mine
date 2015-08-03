package com.dealoka.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.map.GMapV2Direction;
import com.dealoka.app.general.map.GetDirectionAsyncTask;
import com.dealoka.app.model.Offer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.dealoka.lib.General;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class OfferMap extends Fragment {
	public static final String OfferMapFragment = "OFFER_MAP_FRAGMENT";
	private ImageButton btn_header;
	private Offer offer;
	private GoogleMap map;
	private Polyline new_polyline;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		offer = (Offer)getArguments().getSerializable("offer");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.offer_map, container, false);
		int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(DealokaApp.getAppContext());
		if(checkGooglePlayServices != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, getActivity(), 1001).show();
			return view;
		}
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		map = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.mapview)).getMap();
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
		btn_header.setSelected(Home.instance.isPopHeader());
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			@SuppressLint("UseValueOf")
			@Override
			public boolean onMyLocationButtonClick() {
				if(map.getMyLocation() == null) {
					return false;
				}
				if(offer != null) {
					findDirections(new Double(map.getMyLocation().getLatitude()).floatValue(), new Double(map.getMyLocation().getLongitude()).floatValue(), offer.merchant_latitude, offer.merchant_longitude, GMapV2Direction.MODE_DRIVING);
				}
				return false;
			}
		});
		MapsInitializer.initialize(getActivity());
		setInitial(view);
		return view;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(Download.instance != null) {
			Download.instance.closeOfferMapFragment();
		}
		if(map != null) {
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.setMyLocationEnabled(false);
		}
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapview));   
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}
	public void handleGetDirectionsResult(final ArrayList<LatLng> direction_points) {
		PolylineOptions rect_line = new PolylineOptions().width(7).color(Color.BLUE);
		for(int i = 0 ; i < direction_points.size() ; i++) {
			rect_line.add(direction_points.get(i));
		}
		if(new_polyline != null) {
			new_polyline.remove();
		}
		new_polyline = map.addPolyline(rect_line);
	}
	private void setInitial(final View view) {
		if(offer != null) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(offer.merchant_latitude, offer.merchant_longitude), 15);
			map.animateCamera(cameraUpdate);
			map.addMarker(new MarkerOptions()
			.position(new LatLng(offer.merchant_latitude, offer.merchant_longitude))
	        .title(offer.merchant_name)
	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
		}
	}
	@SuppressWarnings("unchecked")
	public void findDirections(
			final float lat1,
			final float long1,
			final float lat2,
			final float long2,
			final String mode) {
		General.showLoading(getActivity());
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionAsyncTask.USER_CURRENT_LAT, String.valueOf(lat1));
		map.put(GetDirectionAsyncTask.USER_CURRENT_LONG, String.valueOf(long1));
		map.put(GetDirectionAsyncTask.DESTINATION_LAT, String.valueOf(lat2));
		map.put(GetDirectionAsyncTask.DESTINATION_LONG, String.valueOf(long2));
		map.put(GetDirectionAsyncTask.DIRECTIONS_MODE, mode);
		new GetDirectionAsyncTask(this).execute(map);	
	}
}