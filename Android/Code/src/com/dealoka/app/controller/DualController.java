package com.dealoka.app.controller;

import com.dealoka.app.R;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.General;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class DualController extends Activity {
	public static DualController instance;
	private View vw_frame;
	private LinearLayout lay_selection;
	private Button btn_ok;
	private int index_selection = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dual);
		instance = this;
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onResume() {
		super.onResume();
		loadSelection();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	private void setInitial() {
		vw_frame = (View)findViewById(R.id.vw_frame);
		lay_selection = (LinearLayout)findViewById(R.id.lay_selection);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		setEventListener();
		General.setAlpha(vw_frame, 0.7f);
		if(General.isNotNull(getIntent().getExtras().getString("caption"))) {
			((TextView)findViewById(R.id.lbl_caption)).setText(getIntent().getExtras().getString("caption"));
		}
	}
	private void setEventListener() {
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doOK();
			}
		});
	}
	private void doOK() {
		if(index_selection != -1) {
			GlobalVariables.sim_session.runINIT();
			GlobalVariables.sim_session.DEVICEID(GlobalVariables.device_id_list.get(index_selection));
			GlobalVariables.sim_session.IMSI(GlobalVariables.imsi_list.get(index_selection));
			unShow();
		}
	}
	private void loadSelection() {
		index_selection = -1;
		lay_selection.removeAllViews();
		if(GlobalVariables.imsi_list.size() > 0) {
			final int index_of_selection = GlobalVariables.imsi_list.indexOf(GlobalVariables.sim_session.IMSI());
			final RadioGroup radio_group = new RadioGroup(this);
			for(int counter = 0; counter < GlobalVariables.imsi_list.size(); counter++) {
				RadioButton radio_button = new RadioButton(this);
				radio_button.setText(GlobalVariables.operator_name_list.get(counter));
				radio_button.setTextColor(Color.BLACK);
				radio_button.setId(counter);
				radio_group.addView(radio_button);
			}
			if(index_of_selection != -1) {
				radio_group.check(index_of_selection);
				index_selection = index_of_selection;
			}
			radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					index_selection = checkedId;
				}
			});
			lay_selection.addView(radio_group);
		}else {
			unShow();
		}
	}
	private void unShow() {
		Intent result_intent = new Intent();
		setResult(Activity.RESULT_OK, result_intent);
		finish();
	}
}