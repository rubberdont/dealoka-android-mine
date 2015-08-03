package com.dealoka.app;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;

import com.dealoka.app.R;
import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.model.Category;
import com.dealoka.app.model.OfferTopOffer;
import com.dealoka.app.model.call.ActionType;
import com.dealoka.app.model.call.TrackObject;
import com.dealoka.app.subs.SubsCategory;
import com.dealoka.lib.General;
import com.dealoka.lib.manager.PhoneManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Offers extends Fragment {
	public static Offers instance;
	public static final String OffersFragment = "OFFERS_FRAGMENT";
	private LayoutInflater inflater;
	private ViewPager vwp_top_offers;
	private LinearLayout lay_bar_top_offers;
	private RelativeLayout lay_top_offer_point;
	private LinearLayout lay_category;
	private TextView lbl_title;
	private TextView lbl_description;
	private TextView lbl_feature;
	private OffersDetails offers_details_fragment;
	private Download download_fragment;
	private CallController call_controller;
	private ArrayList<OfferTopOffer> offer_top_offer_data;
	private ArrayList<ImageView> top_offers_points;
	private ArrayList<View> top_offers_view;
	private ArrayList<View> categories_view;
	private CallController top_offer_controller;
	public Offers() {
		if(offer_top_offer_data == null) {
			offer_top_offer_data = new ArrayList<OfferTopOffer>();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		this.inflater = inflater;
		try {
			final View view = inflater.inflate(R.layout.offers, container, false);
			setInitial(view);
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onResume() {
		super.onResume();
		populateTopOffersData();
		if(categories_view == null) {
			populateCategoryData();
		}else {
			if(categories_view.size() <= 0) {
				populateCategoryData();
			}
		}
	}
	@Override
	public void onDestroyView() {
		instance = null;
		super.onDestroyView();
	}
	public void SyncCategoryNewAdded(final Category category) {
		if(category.current_count != 0) {
			setCategory(SubsCategory.category_db.size());
		}else {
			if(SubsCategory.category_db.size() % 3 == 0) {
				final View view = setCategoryView(SubsCategory.category_db.size() - 1);
				if(view != null) {
					categories_view.add(view);
				}
			}else {
				Home.instance.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final View view = categories_view.get(categories_view.size() - 1);
						if(view != null) {
							setCategoryView(view, SubsCategory.category_db.size() - 1, SubsCategory.category_db.size() % 3);
						}
					}
				});
			}
			setCategoryView();
		}
	}
	public void SyncCategoryNewChanges() {
		if(SubsCategory.category_db == null) {
			return;
		}
		setCategory(SubsCategory.category_db.size());
	}
	public void SyncCategoryChanged(final Category category) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int size = categories_view.size();
				while(size > 0) {
					size--;
					final View view = categories_view.get(size);
					if(((RelativeLayout)view.findViewById(R.id.lay_1_1)).getVisibility() == View.VISIBLE) {
						final ImageButton btn_category = (ImageButton)view.findViewById(R.id.btn_category_1_1);
						if(btn_category.getTag() == category.id) {
							((TextView)view.findViewById(R.id.lbl_title_1_1)).setText(category.category_name);
							break;
						}
					}
					if(((RelativeLayout)view.findViewById(R.id.lay_1_2)).getVisibility() == View.VISIBLE) {
						final ImageButton btn_category = (ImageButton)view.findViewById(R.id.btn_category_1_2);
						if(btn_category.getTag() == category.id) {
							((TextView)view.findViewById(R.id.lbl_title_1_2)).setText(category.category_name);
							break;
						}
					}
				}
			}
		});
	}
	public void closeOffersDetailsFragment() {
		offers_details_fragment = null;
	}
	public void closeDownloadFragment() {
		download_fragment = null;
	}
	private void setInitial(final View view) {
		lay_bar_top_offers = (LinearLayout)view.findViewById(R.id.lay_bar_top_offers);
		lay_top_offer_point = (RelativeLayout)view.findViewById(R.id.lay_top_offer_point);
		lay_category = (LinearLayout)view.findViewById(R.id.lay_category);
		vwp_top_offers = (ViewPager)view.findViewById(R.id.vwp_top_offers);
		lbl_title = (TextView)view.findViewById(R.id.lbl_title);
		lbl_description = (TextView)view.findViewById(R.id.lbl_description);
		lbl_feature = (TextView)view.findViewById(R.id.lbl_feature);
		setEventListener();
		setAlphaInView();
	}
	private void setEventListener() {
		vwp_top_offers.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				setTopOffersSelected(arg0);
				setPointSelected(top_offers_points, arg0, offer_top_offer_data.size());
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	private void setAlphaInView() {
		General.setAlpha(lay_bar_top_offers, 0.6f);
		General.setAlpha(lbl_feature, 0.8f);
	}
	private void populateTopOffersData() {
		if(offer_top_offer_data.size() > 0) {
			return;
		}
		setTopOfferLoading();
		top_offer_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {
				try {
					offer_top_offer_data = new ArrayList<OfferTopOffer>();
					JSONArray array = new JSONArray(result);
					for(int counter = 0; counter < array.length(); counter++) {
						final OfferTopOffer offer_top_offer = new OfferTopOffer(General.TEXT_BLANK, array.getString(counter));
						offer_top_offer_data.add(offer_top_offer);
					}
					setTopOffers();
				}catch(JSONException ex) {}
			}
			@Override
			public void onCallFailed() {
				if(offer_top_offer_data.size() > 0) {
					return;
				}
				if(PhoneManager.isPhoneNetworkConnected(DealokaApp.getAppContext())) {
					populateTopOffersData();
				}
			}
			@Override
			public void onCallConnected() {
				top_offer_controller.callGetTopOffer(GlobalVariables.user_session.GetToken());
			}
		});
	}
	private void populateCategoryData() {
		if(SubsCategory.category_db == null) {
			return;
		}
		setCategory(SubsCategory.category_db.size());
	}
	private void setTopOffers() {
		if(Home.instance == null) {
			return;
		}
		if(offer_top_offer_data.size() <= 0) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lay_bar_top_offers.setVisibility(View.VISIBLE);
				top_offers_view = new ArrayList<View>();
				int counter = 0;
				final Iterator<OfferTopOffer> iterator = offer_top_offer_data.iterator();
				while(iterator.hasNext()) {
					final OfferTopOffer offer_top_offer = iterator.next();
					counter++;
					String image_url = offer_top_offer.c_offer_rec.image;
					image_url = image_url.replace("SIZEREQ_", "og");
					image_url = offer_top_offer.image_prefix + image_url;
					View view = setTopOfferView(image_url, offer_top_offer, counter);
					top_offers_view.add(view);
				}
				top_offers_points = GlobalController.setDrawPoint(lay_top_offer_point, DealokaApp.getAppContext(), counter);
				setPointSelected(top_offers_points, 0, counter);
				setTopOffersSelected(0);
				vwp_top_offers.setAdapter(new TopOffersPagerAdapter());
			}
		});
	}
	private void setCategory(final int count) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int size = count;
				categories_view = new ArrayList<View>();
				while(size > 0) {
					size--;
					final View view = setCategoryView(size);
					if(size > 0) {
						size--;
						if(view != null) {
							setCategoryView(view, size, 2);
						}
					}
					categories_view.add(view);
				}
				setCategoryView();
			}
		});
	}
	@SuppressLint("InflateParams")
	private void setTopOfferLoading() {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lay_bar_top_offers.setVisibility(View.GONE);
				final View view = inflater.inflate(R.layout.image_view, null, false);
				view.setBackgroundColor(DealokaApp.getAppContext().getResources().getColor(R.color.Background));
				final ImageButton button = (ImageButton)view.findViewById(R.id.button);
				button.setScaleType(ScaleType.FIT_CENTER);
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.ic_logo, button, GlobalController.getOption(true, true));
				top_offers_view = new ArrayList<View>();
				top_offers_view.add(view);
				vwp_top_offers.setAdapter(new TopOffersPagerAdapter());
			}
		});
	}
	private void setPointSelected(ArrayList<ImageView> points_of, final int index, final int size) {
		if(size <= 0) {
			return;
		}
		for(int counter = index; counter < size; counter++) {
			points_of.get(counter).setSelected(false);
		}
		for(int counter = 0; counter <= index; counter++) {
			points_of.get(counter).setSelected(true);
		}
	}
	@SuppressLint("InflateParams")
	private View setTopOfferView(final String url, final OfferTopOffer offer_top_offer, final int index) {
		View view = inflater.inflate(R.layout.image_view, null, false);
		ImageButton btn_offer = (ImageButton)view.findViewById(R.id.button);
		btn_offer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDownload(offer_top_offer, index - 1);
			}
		});
		ImageLoader.getInstance().displayImage(url, btn_offer, GlobalController.getOptionWithNoImage());
		return view;
	}
	private void setCategoryView() {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lay_category.removeAllViews();
				final Iterator<View> iterator = categories_view.iterator();
				while(iterator.hasNext()) {
					lay_category.addView(iterator.next());
				}
			}
		});
	}
	@SuppressLint("InflateParams")
	private View setCategoryView(final int index) {
		if(Home.instance == null) {
			return null;
		}
		if(!(SubsCategory.category_db.size() > 0)) {
			return null;
		}
		if(index > (SubsCategory.category_db.size() - 1)) {
			return null;
		}
		View view = inflater.inflate(R.layout.category_view, null, false);
		ImageView img_category = (ImageView)view.findViewById(R.id.img_category_1_1);
		Button btn_category = (Button)view.findViewById(R.id.btn_category_1_1);
		TextView lbl_title = (TextView)view.findViewById(R.id.lbl_title_1_1);
		ImageLoader.getInstance().displayImage(SubsCategory.category_db.get(index).category_image, img_category, GlobalController.getOption(true, true));
		final String category_value = SubsCategory.category_db.get(index).category_value;
		btn_category.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doOfferDetails(category_value);
			}
		});
		((RelativeLayout)view.findViewById(R.id.lay_1_1)).setVisibility(View.VISIBLE);
		((RelativeLayout)view.findViewById(R.id.lay_1_2)).setVisibility(View.GONE);
		lbl_title.setText(SubsCategory.category_db.get(index).category_name);
		btn_category.setTag(SubsCategory.category_db.get(index).id);
		return view;
	}
	private void setCategoryView(final View view, final int index, final int base_row_column) {
		if(Home.instance == null) {
			return;
		}
		if(!(SubsCategory.category_db.size() > 0)) {
			return;
		}
		if(index > (SubsCategory.category_db.size() - 1)) {
			return;
		}
		if(view == null) {
			return;
		}
		final String category = SubsCategory.category_db.get(index).category_value;
		ImageView img_category = null;
		Button btn_category = null;
		TextView lbl_title = null;
		switch(base_row_column) {
		case 2:
			img_category = (ImageView)view.findViewById(R.id.img_category_1_2);
			btn_category = (Button)view.findViewById(R.id.btn_category_1_2);
			lbl_title = (TextView)view.findViewById(R.id.lbl_title_1_2);
			((RelativeLayout)view.findViewById(R.id.lay_1_2)).setVisibility(View.VISIBLE);
			break;
		}
		ImageLoader.getInstance().displayImage(SubsCategory.category_db.get(index).category_image, img_category, GlobalController.getOption(true, true));
		btn_category.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doOfferDetails(category);
			}
		});
		lbl_title.setText(SubsCategory.category_db.get(index).category_name);
		btn_category.setTag(SubsCategory.category_db.get(index).id);
	}
	private void doOfferDetails(final String category_value) {
		if(offers_details_fragment != null) {
			return;
		}
		Bundle args = new Bundle();
		args.putString("category", category_value);
		offers_details_fragment = new OffersDetails();
		offers_details_fragment.setArguments(args);
		getFragmentManager().beginTransaction().add(R.id.lay_child, offers_details_fragment, OffersDetails.OffersDetailsFragment).addToBackStack(null).commit();
	}
	private void doDownload(final OfferTopOffer offer_top_offer, final int index) {
		if(download_fragment != null) {
			return;
		}
		call_controller = new CallController(new CallListener() {
			@Override
			public void onCallReturned(String result) {}
			@Override
			public void onCallFailed() {}
			@Override
			public void onCallConnected() {
				final TrackObject offer_track_action = new TrackObject(
						offer_top_offer.c_offer_rec.category,
						GlobalVariables.latitude,
						GlobalVariables.longitude,
						GlobalVariables.user_session.GetToken(),
						offer_top_offer.c_fk_offer_id.$value,
						ActionType.CLICK,
						offer_top_offer.c_fk_merchant_id.$value,
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
				call_controller.callOfferTrackAction(offer_track_action);
			}
		});
		Bundle args = new Bundle();
		args.putString("from", OffersFragment);
		args.putSerializable("offer", offer_top_offer);
		download_fragment = new Download();
		download_fragment.setArguments(args);
		getFragmentManager().beginTransaction().add(R.id.lay_child, download_fragment, Download.DownloadFragment).addToBackStack(null).commit();
	}
	private void setTopOffersSelected(final int index) {
		if(!(offer_top_offer_data.size() > 0)) {
			return;
		}
		if(index > (offer_top_offer_data.size() - 1)) {
			return;
		}
		lbl_title.setText(offer_top_offer_data.get(index).c_offer_rec.name);
		lbl_description.setText(offer_top_offer_data.get(index).c_offer_rec.summary);
		lbl_title.setSelected(true);
		lbl_description.setSelected(true);
	}
	private class TopOffersPagerAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			return top_offers_view.size();
		}
		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			if(top_offers_view.size() == 0) {
				return null;
			}
			if(top_offers_view.size() < position) {
				return null;
			}
			collection.addView(top_offers_view.get(position), 0);
			return top_offers_view.get(position);
		}
		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			if(top_offers_view.size() == 0) {
				return;
			}
			if(top_offers_view.size() < position) {
				return;
			}
			collection.removeView(top_offers_view.get(position));
		}
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}
		@Override
		public void finishUpdate(ViewGroup arg0) {}
		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}
		@Override
		public Parcelable saveState() {
			return null;
		}
		@Override
		public void startUpdate(ViewGroup arg0) {}
    }
}