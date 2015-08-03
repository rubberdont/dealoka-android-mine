package com.dealoka.app.adapter;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.General;
import com.dealoka.lib.control.imageview.RecyclingImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {
	private class ViewHolder {
		RecyclingImageView img_menu;
		TextView lbl_menu;
	}
	private LayoutInflater inflater;
	private int[] menu_icon;
	public MenuListAdapter() {
		menu_icon = General.getIntegerArray(DealokaApp.getAppContext(), R.array.menu_icon);
		inflater = (LayoutInflater)DealokaApp.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return GlobalVariables.menu_list.length;
	}
	@Override
	public Object getItem(int position) {
		return position;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.menu_list, null);
			holder.img_menu = (RecyclingImageView)convertView.findViewById(R.id.img_menu);
			holder.lbl_menu = (TextView)convertView.findViewById(R.id.lbl_menu);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		ImageLoader.getInstance().displayImage("drawable://" + menu_icon[position], holder.img_menu, GlobalController.getOption(true, false));
		holder.lbl_menu.setText(GlobalVariables.menu_list[position]);
		return convertView;
	}
}