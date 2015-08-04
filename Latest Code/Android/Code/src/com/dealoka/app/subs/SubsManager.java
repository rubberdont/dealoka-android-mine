package com.dealoka.app.subs;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.subs.SubsController.SubsListener;
import com.dealoka.lib.General;

public class SubsManager {
	public enum SubsEnum {
		SubsCategory,
		SubsMessagePopupDialog
	};
	public static ArrayList<SubsEnum> subs_reconnect_list = new ArrayList<SubsManager.SubsEnum>();
	public static SubsController subs_controller;
	public static void init() {
		SubsManager.subs_controller = new SubsController();
		SubsManager.subs_controller.setListener(subs_listener, true);
		SubsManager.Start();
		if(SubsCategory.category_db.size() <= 0) {
			com.dealoka.app.model.Category.loadFromAssets();
		}
		if(SubsGender.gender_db.size() <= 0) {
			com.dealoka.app.model.Gender.loadFromAssets();
		}
	}
	private static SubsListener subs_listener = new SubsListener() {
		@Override
		public void onSubRemoved(String collection, String id) {
			if(collection.equals(SubsConst.collection_db_category)) {
				SubsCategory.removeCategory(id);
			}
		}
		@Override
		public void onSubFailed() {}
		@Override
		public void onSubEOF() {
			SubsManager.Reconnect();
		}
		@Override
		public void onSubConnected(String subs_id) {
			if(SubsManager.subs_reconnect_list.size() > 0) {
				runReconnect();
			}
		}
		@Override
		public void onSubChanged(String collection, String id, String result) {
			if(collection.equals(SubsConst.collection_db_category)) {
				SubsCategory.changeCategory(id, result);
			}
		}
		@Override
		public void onSubAdded(String collection, String id, String result) {
			if(collection.equals(SubsConst.collection_db_category)) {
				SubsCategory.addCategory(id, result);
			}else if(collection.equals(SubsConst.collection_db_message_popup_dialog)) {
				SubsMessagePopupDialog.addMessagePopDialog(id, result);
			}
		}
	};
	private static void runReconnect() {
		final Iterator<SubsEnum> iterator = subs_reconnect_list.iterator();
		while(iterator.hasNext()) {
			final SubsEnum subs_enum = iterator.next();
			if(subs_enum == SubsEnum.SubsCategory) {
				SubsManager.subs_controller.subsCategory();
			}else if(subs_enum == SubsEnum.SubsMessagePopupDialog) {
				if(GlobalVariables.user_session.isSession()) {
					SubsManager.subs_controller.subsMessagePopupDialog(GlobalVariables.user_session.GetToken());
				}
			}
		}
		SubsManager.subs_reconnect_list.clear();
	}
	public static void Start() {
		SubsCategory.subscribeCategory();
		SubsGender.subscribeGender();
		if(GlobalVariables.user_session.isSession()) {
			SubsManager.Home();
		}
	}
	public static void Home() {
		SubsMessagePopupDialog.subscribeMessagePopupDialog(GlobalVariables.user_session.GetToken());
	}
	public static void disconnectAllWhenItsSwitched() {
		SubsManager.subs_reconnect_list = new ArrayList<SubsManager.SubsEnum>();
		SubsManager.Reconnect();
	}
	public static String getRandomSubsID() {
		return String.valueOf(new Date().getTime());
	}
	public static void Reconnect() {
		if(SubsManager.subs_controller == null) {
			return;
		}
		GlobalVariables.session = General.TEXT_BLANK;
		SubsManager.subs_controller.disconnect();
		SubsManager.subs_controller = null;
		SubsManager.init();
	}
}