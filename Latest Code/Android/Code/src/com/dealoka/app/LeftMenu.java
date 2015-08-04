package com.dealoka.app;

import com.dealoka.app.adapter.MenuListAdapter;
import com.dealoka.app.controller.RateController;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.General;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class LeftMenu {
	private TextView lbl_name;
	private ListView lst_menu;
	private MenuListAdapter menu_list_adapter = new MenuListAdapter();
	public LeftMenu() {
		setInitial();
	}
	private void setInitial() {
		lbl_name = (TextView)Home.instance.findViewById(R.id.lbl_name);
		lst_menu = (ListView)Home.instance.findViewById(R.id.lst_menu);
		setEventListener();
		populateData();
		addList();
	}
	private void populateData() {
		if(General.isNotNull(GlobalVariables.user_session.GetEmail())) {
			lbl_name.setText(GlobalVariables.user_session.GetEmail());
		}else {
			lbl_name.setText(GlobalVariables.user_session.GetFName() + " " + GlobalVariables.user_session.GetLName());
		}
	}
	private void setEventListener() {
		lst_menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				doSelect(position);
			}
		});
	}
	private void doSelect(final int index) {
		switch(index) {
		case 0:
			openPoints();
			break;
		case 1:
			if(GlobalVariables.sim_session.DUALSIM()) {
				openSimCard();
			}else {
				if(General.isNotNull(GlobalVariables.imsi)) {
					GlobalController.showAlert(Home.instance, String.format(DealokaApp.getAppContext().getString(R.string.text_message_single_simcard), GlobalVariables.operator_name));
				}else {
					GlobalController.showAlert(Home.instance, DealokaApp.getAppContext().getString(R.string.text_message_no_simcard));
				}
			}
			break;
		case 2:
			openSDKSpecialCode();
			break;
		case 3:
			openRate();
			break;
		case 4:
			openSupport();
			break;
		case 5:
			Home.instance.openAbout();
			break;
		case 6:
			openTutorial();
			break;
		default:
			break;
		}
		Home.instance.closeDrawers();
	}
	private void addList() {
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lst_menu.setAdapter(menu_list_adapter);
			}
		});
	}
	private void openPoints() {
		GlobalController.showAlert(Home.instance, DealokaApp.getAppContext().getString(R.string.text_message_coming_soon));
	}
	private void openSDKSpecialCode() {
		GlobalController.openSDKSpecialCode(Home.instance);
	}
	private void openSimCard() {
		GlobalController.openSimCard(
				Home.instance,
				DealokaApp.getAppContext().getString(R.string.text_label_menu_dual_caption));
	}
	private void openRate() {
		RateController.openMarket(Home.instance);
	}
	private void openSupport() {
		GlobalController.openSendMessage(Home.instance, General.TEXT_BLANK);
	}
	private void openTutorial() {
		GlobalController.openTutorial(Home.instance);
	}
}