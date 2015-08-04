package com.dealoka.app.adapter;

import java.util.ArrayList;

import com.dealoka.lib.control.imageview.RecyclingImageView;
import com.dealoka.lib.GeneralCalendar;
import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.OfferRedeemWallet;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class WalletListAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<OfferRedeemWallet> data = null;
	private WalletListCallback wallet_list_callback;
	private static LayoutInflater inflater = null;
	public interface WalletListCallback {
		public abstract void didSelected(final int position);
		public abstract void didDeleted(final OfferRedeemWallet offer_redeem_wallet);
	}
	private class ViewHolder {
		ViewFlipper view_flipper;
		LinearLayout lay_list;
		TextView title;
		TextView description;
		ImageButton remove;
		RecyclingImageView image;
		ImageView img_expired;
		ImageView img_redeemed;
		TextView lbl_redeem_on;
		Button btn_yes;
		Button btn_no;
	}
	public WalletListAdapter(Activity activity, ArrayList<OfferRedeemWallet> data, final WalletListCallback wallet_list_callback) {
		this.activity = activity;
		this.data = data;
		this.wallet_list_callback = wallet_list_callback;
		inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.wallet_list, null);
			holder.view_flipper = (ViewFlipper)convertView.findViewById(R.id.view_flipper);
			holder.lay_list = (LinearLayout)convertView.findViewById(R.id.lay_list);
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.description = (TextView)convertView.findViewById(R.id.description);
			holder.remove = (ImageButton)convertView.findViewById(R.id.remove);
			holder.image = (RecyclingImageView)convertView.findViewById(R.id.image);
			holder.img_expired = (ImageView)convertView.findViewById(R.id.img_expired);
			holder.img_redeemed = (ImageView)convertView.findViewById(R.id.img_redeemed);
			holder.lbl_redeem_on = (TextView)convertView.findViewById(R.id.lbl_redeem_on);
			holder.btn_yes = (Button)convertView.findViewById(R.id.btn_yes);
			holder.btn_no = (Button)convertView.findViewById(R.id.btn_no);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		final ViewHolder _holder = holder;
		final int pos = position;
		holder.lay_list.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					{
						return true;
					}
					case MotionEvent.ACTION_UP:
					{
						if(_holder.view_flipper.getDisplayedChild() == 1) break;
						wallet_list_callback.didSelected(pos);
						return true;
					}
				}
				return false;
			}
		});
		holder.remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_holder.view_flipper.setInAnimation(activity, R.anim.slide_in_from_right);
				_holder.view_flipper.setOutAnimation(activity, R.anim.slide_out_to_left);
				_holder.view_flipper.showNext();
			}
		});
		holder.btn_no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_holder.view_flipper.setInAnimation(activity, R.anim.slide_in_from_left);
				_holder.view_flipper.setOutAnimation(activity, R.anim.slide_out_to_right);
				_holder.view_flipper.showPrevious();
			}
		});
		holder.btn_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(_holder.view_flipper.getDisplayedChild() == 1) {
					_holder.view_flipper.showPrevious();
				}
				wallet_list_callback.didDeleted(data.get(pos));
			}
		});
		if(data.get(position).c_offer_rec != null) {
			holder.title.setText(data.get(position).c_offer_rec.name);
			holder.description.setText(data.get(position).c_offer_rec.summary);
			holder.title.setSelected(true);
			holder.description.setSelected(true);
		}
		final long next_day = data.get(position).redeem_time + (data.get(position).day_multiplier * 60 * 60 * 24 * 1000);
		holder.img_expired.setVisibility(View.GONE);
		holder.img_redeemed.setVisibility(View.GONE);
		if(data.get(position).expired) {
			holder.img_expired.setVisibility(View.VISIBLE);
			holder.lbl_redeem_on.setText(activity.getString(R.string.text_label_expired));
		}else if(data.get(position).validated) {
			holder.img_redeemed.setVisibility(View.VISIBLE);
			holder.lbl_redeem_on.setText(activity.getString(R.string.text_label_redeemed));
		}else {
			if(data.get(position).c_offer_rec != null) {
				if(next_day > data.get(position).c_offer_rec.validity_end) {
					holder.lbl_redeem_on.setText(activity.getString(R.string.text_label_expired_on) + GeneralCalendar.getDate(GeneralCalendar.getCurrentUTCTimestamp(data.get(position).c_offer_rec.validity_end), "dd.MMM.yyyy HH:mm:ss"));
				}else {
					holder.lbl_redeem_on.setText(activity.getString(R.string.text_label_expired_on) + GeneralCalendar.getDate(GeneralCalendar.getCurrentUTCTimestamp(next_day), "dd.MMM.yyyy HH:mm:ss"));
				}
			}
		}
		if(data.get(position).c_offer_rec != null) {
			String image_url = data.get(position).c_offer_rec.image;
			image_url = image_url.replace("SIZEREQ_", "og");
			image_url = data.get(position).image_prefix + image_url;
			ImageLoader.getInstance().displayImage(image_url, holder.image, GlobalController.getOptionWithNoImage());
		}
		return convertView;
	}
}