package com.dealoka.app;

import static codemagnus.com.dealogeolib.utils.LogUtils.LOGD;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.dealoka.app.adapter.OffersListAdapter;
import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.model.OfferGeo;
import com.dealoka.app.model.call.ActionType;
import com.dealoka.app.model.call.GetOffer;
import com.dealoka.app.model.call.TrackObject;
import com.dealoka.lib.General;
import com.dealoka.lib.control.listview.CustomListView;
import com.dealoka.lib.control.listview.NewPageAdapterListener.NewPageListener;
import com.dealoka.lib.controller.LocationController;
import com.dealoka.lib.controller.LocationController.LocationCallback;

public class OffersDetails extends Fragment implements LocationCallback {
	public static OffersDetails instance;
	public static final String OffersDetailsFragment = "OFFERS_DETAILS_FRAGMENT";
	private LocationController location_controller;
	private ImageButton btn_header;
	private ProgressBar pgb_loading;
	private CustomListView lst_offers;
	private OffersListAdapter list_adapter;
	private CallController track_call_controller;
	private CallController update_call_controller;
	private Download download_fragment;
	private LayoutInflater inflater;
	private ArrayList<OfferGeo> data;
	private String category;
	private float latitude;
	private float longitude;
	private int index = 0;
	private boolean updated = false;
	private boolean failed = false;
	public OffersDetails() {
		location_controller = new LocationController(DealokaApp.getAppContext());
		location_controller.setLocationCallback(this);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		category = getArguments().getString("category");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		this.inflater = inflater;
		try {
			final View view = inflater.inflate(R.layout.offers_details, container, false);
			setInitial(view);
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onDestroyView() {
		instance = null;
		if(Offers.instance != null) {
			Offers.instance.closeOffersDetailsFragment();
		}
		super.onDestroyView();
	}
	@Override
	public void didLocationUpdated(float latitude, float longitude) {
		GlobalVariables.latitude = latitude;
		GlobalVariables.longitude = longitude;
		this.latitude = latitude;
		this.longitude = longitude;
		populateData();
	}
	@Override
	public void didLocationFailed() {
		GlobalController.showToast(R.string.text_err_no_user_location);
	}
	@Override
	public void isProviderEnabled(boolean enabled) {}
	public void closeDownloadFragment() {
		download_fragment = null;
	}
	@SuppressLint("InflateParams")
	private void setInitial(final View view) {
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		pgb_loading = (ProgressBar)view.findViewById(R.id.pgb_loading);
		lst_offers = (CustomListView)view.findViewById(R.id.lst_offers);
		setEventListener();
		btn_header.setSelected(Home.instance.isPopHeader());
		lst_offers.setLoadingView(inflater.inflate(R.layout.loading_list, null));
		data = new ArrayList<OfferGeo>();
		init();
	}
	private void setEventListener() {
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
		lst_offers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setSelected(arg2);
			}
		});
	}
	private void init() {
		showLoading(true);
		if(GlobalController.isLocationRetrieved()) {
			latitude = GlobalVariables.latitude;
			longitude = GlobalVariables.longitude;
			populateData();
		}else {
			location_controller.loadLocationWithNetwork();
		}
	}
	private void setSelected(final int index) {
		if(download_fragment != null) {
			return;
		}
		if(index > data.size() - 1) {
			return;
		}
		track_call_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {}
			@Override
			public void onCallFailed() {}
			@Override
			public void onCallConnected() {
				final OfferGeo offer_geo = data.get(index);
				final TrackObject offer_track_action = new TrackObject(
						category,
						GlobalVariables.latitude,
						GlobalVariables.longitude,
						GlobalVariables.user_session.GetToken(),
						offer_geo.c_fk_offer_id.$value,
						ActionType.CLICK,
						offer_geo.c_fk_merchant_id.$value,
						GlobalVariables.user_session.GetGender(),
						new TrackObject.BTSObject(
								GlobalVariables.longitude,
								GlobalVariables.latitude,
								GlobalVariables.orientation,
								GlobalVariables.signal,
								GlobalVariables.cid,
								GlobalVariables.lac,
								GlobalVariables.mcc,
								GlobalVariables.mnc));
				track_call_controller.callOfferTrackAction(offer_track_action);
			}
		});
		final Bundle args = new Bundle();
		args.putString("from", OffersDetailsFragment);
		args.putSerializable("offer", data.get(index));
		download_fragment = new Download();
		download_fragment.setArguments(args);
		final FragmentTransaction fragment_transaction = getFragmentManager().beginTransaction();
		fragment_transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		fragment_transaction.add(R.id.lay_child, download_fragment, Download.DownloadFragment).addToBackStack(null).commit();
	}
	private void populateData() {
		index = 0;
		if(Home.instance != null) {
			Home.instance.startLookupConnection();
		}
		update_call_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {
				LOGD(OffersDetailsFragment, result);
				addOffersData(result);
			}
			@Override
			public void onCallFailed() {
				showLoading(false);
				if(Home.instance != null) {
					Home.instance.stopLookupConnection();
				}
				GlobalController.showToast(R.string.text_err_connection_bad);
			}
			@Override
			public void onCallConnected() {
				final GetOffer get_offer = new GetOffer(GlobalVariables.user_session.GetToken(), category, GlobalController.getDeviceID(), latitude, longitude, index, Config.list_limit);
				update_call_controller.callGetOffer(get_offer);
			}
		});
	}
	private void updateOffers() {
		if(Home.instance != null) {
			Home.instance.startLookupConnection();
		}
		updated = false;
		failed = false;
		update_call_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {
				updateOffersData(result);
			}
			@Override
			public void onCallFailed() {
				if(Home.instance != null) {
					Home.instance.stopLookupConnection();
				}
				updated = false;
				failed = true;
			}
			@Override
			public void onCallConnected() {
				final GetOffer get_offer = new GetOffer(GlobalVariables.user_session.GetToken(), category, GlobalController.getDeviceID(), latitude, longitude, index, Config.list_limit);
				update_call_controller.callGetOffer(get_offer);
			}
		});
	}
	private void addOffersData(final String result) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showLoading(false);
				if(Home.instance != null) {
					Home.instance.stopLookupConnection();
				}
				try {
					data = new ArrayList<OfferGeo>();
					int total_count = 0;
					JSONObject json = new JSONObject(result);
					JSONArray message_data = json.getJSONArray("message_data");
					for(int counter = 0; counter < message_data.length(); counter++) {
						final OfferGeo offer_geo = new OfferGeo(General.TEXT_BLANK, message_data.getString(counter));
						total_count = offer_geo.total_count;
						json = message_data.getJSONObject(counter);
						if(!isOfferDataExisted(offer_geo.c_fk_offer_id.$value)) {
							data.add(offer_geo);
						}
					}
					index += message_data.length();
					if(message_data.length() > 0) {
						index--;
						list_adapter = new OffersListAdapter(update_task, data);
						lst_offers.setAdapter(list_adapter);
						if(total_count > Config.list_limit) {
							list_adapter.notifyHasMore();
						}
					}
				}catch(JSONException ex) {}
			}
		});
	}
	private void updateOffersData(final String result) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(Home.instance != null) {
					Home.instance.stopLookupConnection();
				}
				try {
					JSONObject json = new JSONObject(result);
					JSONArray message_data = json.getJSONArray("message_data");
					for(int counter = 0; counter < message_data.length(); counter++) {
						final OfferGeo offer_geo = new OfferGeo(General.TEXT_BLANK, message_data.getString(counter));
						json = message_data.getJSONObject(counter);
						if(!isOfferDataExisted(offer_geo.c_fk_offer_id.$value)) {
							data.add(offer_geo);
							list_adapter.notifyDataSetChanged();
						}
					}
					index += message_data.length();
					if(message_data.length() > 0) {
						if(message_data.length() < Config.list_limit) {
							failed = true;
						}else {
							index--;
							failed = false;
						}
					}else {
						failed = true;
					}
				}catch(JSONException ex) {}
				updated = true;
			}
		});
	}
	private boolean isOfferDataExisted(final String offer_id) {
		boolean duplicate = false;
		int index = data.size();
		while(index > 0) {
			index--;
			if(data.get(index).c_fk_offer_id.$value.equals(offer_id)) {
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}

	public void showLoading(final boolean show) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(show) {
					pgb_loading.setVisibility(View.VISIBLE);
				}else {
					pgb_loading.setVisibility(View.GONE);
				}
			}
		});
	}
	private NewPageListener update_task = new NewPageListener() {
		int init_size;
		@Override
		public void onScrollNext() {
			AsyncTask<Void, Void, Void> scroll = new AsyncTask<Void, Void, Void>() {
				@Override
				protected void onPreExecute() {
					init_size = data.size();
					list_adapter.lock();
					updateOffers();
				}
				@Override
				protected Void doInBackground(Void ... params) {
					try {
						while(!updated) {
							Thread.sleep(1000);
						}
					}catch(InterruptedException ex) {}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					if(data != null) {
						if(init_size >= data.size() && failed && updated) {
							list_adapter.notifyEndOfList();
							return;
						}
					}
					list_adapter.notifyHasMore();
				};
                @Override
                protected void onCancelled() {
                	list_adapter.notifyHasMore();
                }
			};
			General.executeAsync(scroll);
		}
	};
}