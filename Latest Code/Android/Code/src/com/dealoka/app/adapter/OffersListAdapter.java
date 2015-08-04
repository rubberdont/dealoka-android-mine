package com.dealoka.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.OfferGeo;
import com.dealoka.lib.control.imageview.RecyclingImageView;
import com.dealoka.lib.control.listview.InfiniteScrollListAdapter;
import com.dealoka.lib.control.listview.NewPageAdapterListener.NewPageListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OffersListAdapter extends InfiniteScrollListAdapter {
	private class ViewHolder {
		TextView title;
		TextView description;
		TextView distance;
		RecyclingImageView image;
		LinearLayout lay_special;
	}
	private NewPageListener new_page_listener;
	private ArrayList<OfferGeo> data = null;
	private static LayoutInflater inflater = null;
	public OffersListAdapter(final NewPageListener new_page_listener, final ArrayList<OfferGeo> data) {
		this.new_page_listener = new_page_listener;
		this.data = data;
		inflater = (LayoutInflater)DealokaApp.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public int getCount() {
		return data.size();
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	}
	@Override
	protected void onScrollNext() {
		if(new_page_listener != null) {
			new_page_listener.onScrollNext();
		}
	}
	@SuppressLint("InflateParams")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public View getInfiniteScrollListView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.offers_list, null);
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.description = (TextView)convertView.findViewById(R.id.description);
			holder.distance = (TextView)convertView.findViewById(R.id.distance);
			holder.image = (RecyclingImageView)convertView.findViewById(R.id.image);
			holder.lay_special = (LinearLayout)convertView.findViewById(R.id.lay_special);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.title.setText(data.get(position).c_offer_rec.name);
		holder.description.setText(data.get(position).address);
		holder.distance.setText(String.valueOf(data.get(position).distance / 1000) + "km");
		holder.title.setSelected(true);
		holder.description.setSelected(true);
		String image_url = data.get(position).c_offer_rec.image;
		image_url = image_url.replace("SIZEREQ_", "og");
		image_url = data.get(position).image_prefix + image_url;
		ImageLoader.getInstance().displayImage(image_url, holder.image, GlobalController.getOptionWithNoImage());
		holder.lay_special.setVisibility(View.GONE);
		if(data.get(position).c_offer_rec.special_promos != null) {
			if(data.get(position).c_offer_rec.special_promos.size() > 0) {
				holder.lay_special.removeAllViews();
				holder.lay_special.setVisibility(View.VISIBLE);
				Iterator iterator = data.get(position).c_offer_rec.special_promos.iterator();
				while(iterator.hasNext()) {
					View view = inflater.inflate(R.layout.special_offers_view, null, false);
					final HashMap<String, Object> special_promos = (HashMap<String, Object>)iterator.next();
					RelativeLayout lay_main = (RelativeLayout)view.findViewById(R.id.lay_main);
					lay_main.setBackgroundColor(Color.parseColor(special_promos.get("offer_background_color").toString()));
					TextView lbl_special_text = (TextView)view.findViewById(R.id.special_text);
					lbl_special_text.setText(special_promos.get("offer_text").toString());
					lbl_special_text.setTextColor(Color.parseColor(special_promos.get("offer_text_color").toString()));
					RecyclingImageView special_image = (RecyclingImageView)view.findViewById(R.id.special_image);
					ImageLoader.getInstance().displayImage(special_promos.get("offer_logo").toString(), special_image, GlobalController.getOption(true, true));
					holder.lay_special.addView(lay_main);
				}
			}
		}
		return convertView;
	}
}