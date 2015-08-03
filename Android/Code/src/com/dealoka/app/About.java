package com.dealoka.app;

import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.general.PowerController;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class About extends Fragment {
	public static About instance;
	public static final String AboutFragment = "ABOUT_FRAGMENT";
	private ImageButton btn_header;
	private TextView lbl_version;
	private TextView lbl_user_email;
	private TextView lbl_email;
	private Button btn_tc;
	private RelativeLayout lay_powered;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		try {
			final View view = inflater.inflate(R.layout.about, container, false);
			setInitial(view);
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onDestroyView() {
		instance = null;
		if(Home.instance != null) {
			Home.instance.closeAboutFragment();
		}
		super.onDestroyView();
	}
	private void setInitial(final View view) {
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		lay_powered = (RelativeLayout)view.findViewById(R.id.lay_powered);
		lbl_version = (TextView)view.findViewById(R.id.lbl_version);
		lbl_user_email = (TextView)view.findViewById(R.id.lbl_user_email);
		lbl_email = (TextView)view.findViewById(R.id.lbl_email);
		btn_tc = (Button)view.findViewById(R.id.btn_tc);
		String version = DealokaApp.getAppContext().getString(R.string.text_err_no_version);
		try {
			version = DealokaApp.getAppContext().getPackageManager().getPackageInfo(DealokaApp.getAppContext().getPackageName(), 0).versionName;
		}catch(NameNotFoundException ex) {}
		lbl_version.setText(version);
		lbl_user_email.setText(GlobalVariables.user_session.GetEmail());
		lbl_email.setText(getString(R.string.text_label_email));
		PowerController.loadLayout(lay_powered);
		setEventListener();
	}
	private void setEventListener() {
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
		btn_tc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.openTC(Home.instance);
			}
		});
	}
}