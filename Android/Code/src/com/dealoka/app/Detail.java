package com.dealoka.app;

import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.model.Offer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class Detail extends Fragment {
	public static Detail instance;
	public static final String DetailFragment = "DETAIL_FRAGMENT";
	private enum Detail_Set {
		none,
		description,
		condition
	};
	private ImageButton btn_header;
	private TextView lbl_title;
	private TextView lbl_text;
	private Detail_Set detail_set = Detail_Set.none;
	private Offer offer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments().getString("set").equals("description")) {
			detail_set = Detail_Set.description;
		}else if(getArguments().getString("set").equals("condition")) {
			detail_set = Detail_Set.condition;
		}
		offer = (Offer)getArguments().getSerializable("offer");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		try {
			final View view = inflater.inflate(R.layout.detail, container, false);
			setInitial(view);
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onDestroyView() {
		instance = null;
		if(Download.instance != null) {
			Download.instance.closeDetailFragment();
		}
		super.onDestroyView();
	}
	private void setInitial(final View view) {
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		lbl_title = (TextView)view.findViewById(R.id.lbl_title);
		lbl_text = (TextView)view.findViewById(R.id.lbl_text);
		setEventListener();
		btn_header.setSelected(Home.instance.isPopHeader());
		switch(detail_set) {
		case none:
			break;
		case description:
			lbl_title.setText(DealokaApp.getAppContext().getString(R.string.text_label_description));
			lbl_text.setText(offer.summary);
			break;
		case condition:
			lbl_title.setText(DealokaApp.getAppContext().getString(R.string.text_label_condition));
			lbl_text.setText(offer.conditions);
			break;
		}
	}
	private void setEventListener() {
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
	}
}