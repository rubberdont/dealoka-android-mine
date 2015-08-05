package com.dealoka.app.controller;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import codemagnus.com.dealogeolib.service.DealokaService;

import com.dealoka.app.Home;
import com.dealoka.app.Main;
import com.dealoka.app.R;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.OfferGeo;
import com.dealoka.app.receivers.OffersBroadcastReceiver;
import com.dealoka.lib.calligraphy.CalligraphyContextWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PopupOffersController extends FragmentActivity{
	public static PopupOffersController instance;
	public static boolean popupIsActive;
	private ListView popUpList;
	private ArrayList<OfferGeo> popupOffers;
	private PopupListAdapter popupListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_offers);
		instance = this;
		popupIsActive = true;
		setInitial();
	}
	private void setInitial(){
		popUpList 			= (ListView) findViewById(R.id.offers_list);
		popupOffers 		= getIntent().getParcelableArrayListExtra(DealokaService.NEW_OFFERS);
		popupListAdapter 	= new PopupListAdapter(popupOffers);
		popUpList			.setAdapter(popupListAdapter);
		clearSavedOffers();
		showPopup(popupOffers.get(0));
		popUpList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				showPopup(popupOffers.get(position));
			}
		});
	}
	public void updatePopupList(Intent intent){
		ArrayList<OfferGeo> newOffers = intent.getParcelableArrayListExtra(DealokaService.NEW_OFFERS);
		if(popupOffers != null){
			for(int x = 0; x < newOffers.size(); x++){
				OfferGeo newOffer = newOffers.get(x);
				if(!popupOffers.contains(newOffer))
					popupOffers.add(newOffer);
			}
		}else{
			popupOffers = newOffers;
		}
		if(popupListAdapter != null)
			popupListAdapter.notifyDataSetChanged();
		
		clearSavedOffers();
	}
	private void clearSavedOffers(){
		GlobalController.closeNotification(84);
	}
	private void showPopup(OfferGeo offer){
		Bundle args = new Bundle();
		args.putParcelable("offer", offer);
		PopupOffer popupOffer = new PopupOffer();
		popupOffer.setArguments(args);
		getSupportFragmentManager().beginTransaction().replace(R.id.popup_container, popupOffer, 
				PopupOffer.TAG).addToBackStack(null).commit();
		popUpList.setVisibility(View.GONE);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		popupIsActive = false;
	}
	private class PopupListAdapter extends BaseAdapter{
		private ArrayList<OfferGeo> list;
		public PopupListAdapter(ArrayList<OfferGeo> items) {
			this.list = items;
		}
		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public OfferGeo getItem(int position) {
			return list.get(position);
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final OfferGeo oneOffer = list.get(position);
			ViewHolder vHolder;
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.popup_offer, parent, false);
				vHolder = new ViewHolder(convertView);
				convertView.setTag(vHolder);
			}else{
				vHolder = (ViewHolder) convertView.getTag();
			}
			vHolder.setData(oneOffer);
			return convertView;
		}
		class ViewHolder{
			private ImageView offerImage;
            private TextView merchantName, offerDescription;
			public ViewHolder(View itemView){
				offerImage        	= (ImageView) itemView.findViewById(R.id.offer_image);
                merchantName        = (TextView) itemView.findViewById(R.id.merchant_name);
                offerDescription    = (TextView) itemView.findViewById(R.id.offer_desc);
			}
			public void setData(OfferGeo offer){
				merchantName.setText(offer.c_merchant_rec.merchant_name);
				String image_url = offer.c_offer_rec.image.replace("SIZEREQ_", "og");
				image_url = "http://img.linkertise.com/" + image_url;
				String description = offer.c_offer_rec.name;
				ImageLoader.getInstance().displayImage(image_url, offerImage, GlobalController.getOptionWithNoImage());
				offerDescription.setText(description);
				offerDescription.setSelected(true);
			}
		}
	}
	public class PopupOffer extends Fragment{
		public static final String TAG = "PopupOffer";
		private TextView merchantName, offerDesc, offerAddress, offerDistance;
		private ImageView offerImage, mapImage;
		private TextView closeButton, moreInfoButton;
		private OfferGeo offer;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.popup, container, false);
			return rootView;
		}
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			merchantName		= (TextView) view.findViewById(R.id.merchant_name);
			offerDesc			= (TextView) view.findViewById(R.id.offer_desc);
			offerImage			= (ImageView) view.findViewById(R.id.offer_image);
			offerAddress		= (TextView) view.findViewById(R.id.offer_address);
			offerDistance		= (TextView) view.findViewById(R.id.offer_distance);
			closeButton			= (TextView) view.findViewById(R.id.close_button);
			moreInfoButton		= (TextView) view.findViewById(R.id.more_info_button);
			mapImage			= (ImageView) view.findViewById(R.id.map_image);
		}
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			offer = (OfferGeo) getArguments().getSerializable("offer");
			merchantName.setText(offer.c_merchant_rec.merchant_name);
			String image_url = offer.c_offer_rec.image.replace("SIZEREQ_", "og");
			image_url = offer.image_prefix + image_url;
			String description = offer.c_offer_rec.name;
			String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=";
			mapUrl += offer.c_merchant_point.Latitude + "," + offer.c_merchant_point.Longitude;
			mapUrl += "&zoom=15&size=400x150&scale=2&maptype=roadmap";
			mapUrl += "&markers=icon:http://chart.apis.google.com/chart?chst=d_map_pin_icon%26chld=cafe%257C996600|" + offer.c_merchant_point.Latitude + "," + offer.c_merchant_point.Longitude;
			ImageLoader.getInstance().displayImage(image_url, offerImage, GlobalController.getOptionWithNoImage());
			ImageLoader.getInstance().displayImage(mapUrl, mapImage, GlobalController.getOptionWithNoImage());
			Log.e("MAP URL", mapUrl);
			offerDesc.setText(description);
			offerDesc.setSelected(true);
			offerAddress.setText(offer.address);
			offerDistance.setText((offer.distance / 1000) + "km");
			setEventListener();
		}
		private void setEventListener(){
			closeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getFragmentManager().popBackStack();
					popUpList.setVisibility(View.VISIBLE);
					if(popupOffers.size() == 1){
						finish();
					}
				}
			});
			moreInfoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					if(Home.instance != null && Home.instance.isOpened()){
						Home.instance.openDownload(offer);
					}else{
						Intent offerInfoIntent = new Intent(PopupOffersController.this, Main.class);
						offerInfoIntent.putExtra(Config.popup_notif, getArguments().getSerializable("offer"));
						startActivity(offerInfoIntent);
					}
				}
			});
		}
		@Override
		public void onDestroyView() {
			popUpList.setVisibility(View.VISIBLE);
			super.onDestroyView();
		}
	}
}
