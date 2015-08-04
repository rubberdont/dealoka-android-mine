package com.dealoka.app.subs;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.app.activity.Gender;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.subs.SubsController.SubsListener;
import com.dealoka.lib.General;

public class SubsGender {
	private static ArrayList<com.dealoka.app.model.Gender> _gender;
	public static ArrayList<com.dealoka.app.model.Gender> gender_db;
	private static SubsController subs_controller;
	public static void subscribeGender() {
		if(SubsGender.gender_db != null) {
			if(SubsGender.gender_db.size() <= 0) {
				SubsGender.gender_db = GlobalVariables.query_controller.getGender();
			}
		}else {
			SubsGender.gender_db = GlobalVariables.query_controller.getGender();
		}
		SubsGender._gender = new ArrayList<com.dealoka.app.model.Gender>();
		SubsGender.subs_controller = new SubsController();
		SubsGender.subs_controller.setListener(new SubsListener() {
			@Override
			public void onSubConnected(String subs_id) {
				SubsGender.subs_controller.subsGender();
			}
			@Override
			public void onSubFailed() {}
			@Override
			public void onSubAdded(String collection, String id, String result) {
				try {
					JSONObject json = new JSONObject(result);
					final com.dealoka.app.model.Gender gender =
							new com.dealoka.app.model.Gender(id, json);
					if(SubsGender.isGenderExisted(id, false)) {
						return;
					}
					SubsGender.removeGenderDBTemp();
					SubsGender._gender.add(gender);
					if(SubsGender.isGenderDBExisted(id, false)) {
						GlobalVariables.query_controller.updateGender(gender);
						SubsGender.changeGenderDB(gender);
					}else {
						SubsGender.gender_db.add(0, gender);
						GlobalVariables.query_controller.addGender(gender);
						if(Gender.instance != null) {
							Gender.instance.SyncNewAdded();
						}
					}
				}catch(JSONException ex) {}
			}
			@Override
			public void onSubChanged(String collection, String id, String result) {
				try {
					JSONObject json = new JSONObject(result);
					final com.dealoka.app.model.Gender gender =
							new com.dealoka.app.model.Gender(id, json.getString("gender_name"));
					SubsGender.changeGenderDB(gender);
					GlobalVariables.query_controller.updateGender(gender);
					if(Gender.instance != null) {
						Gender.instance.SyncChanged();
					}
				}catch(JSONException ex) {}
			}
			@Override
			public void onSubRemoved(String collection, String id) {
				SubsGender.isGenderExisted(id, true);
				SubsGender.isGenderDBExisted(id, true);
				GlobalVariables.query_controller.deleteGender(id);
				if(Gender.instance != null) {
					Gender.instance.SyncRemoved();
				}
			}
			@Override
			public void onSubEOF() {
				SubsGender.disconnect();
			}
		}, true);
	}
	public static void disconnect() {
		if(SubsGender.subs_controller != null) {
			SubsGender.subs_controller.disconnect();
			SubsGender.subs_controller = null;
		}
	}
	private static int getIndexGenderDB(final String id) {
		int index = SubsGender.gender_db.size();
		while(index > 0) {
			index--;
			if(SubsGender.gender_db.get(index).id.equals(id)) {
				break;
			}
		}
		if(index == SubsGender.gender_db.size()) {
			index = -1;
		}
		return index;
	}
	private static boolean isGenderExisted(final String id, final boolean delete) {
		boolean duplicate = false;
		Iterator<com.dealoka.app.model.Gender> iterator = SubsGender._gender.iterator();
		while(iterator.hasNext()) {
			if(iterator.next().id.equals(id)) {
				if(delete) {
					iterator.remove();
				}
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}
	private static boolean isGenderDBExisted(final String id, final boolean delete) {
		boolean duplicated = false;
		Iterator<com.dealoka.app.model.Gender> iterator = SubsGender.gender_db.iterator();
		while(iterator.hasNext()) {
			if(iterator.next().id.equals(id)) {
				if(delete) {
					iterator.remove();
				}
				duplicated = true;
				break;
			}
		}
		return duplicated;
	}
	private static void removeGenderDBTemp() {
		Iterator<com.dealoka.app.model.Gender> iterator = SubsGender.gender_db.iterator();
		while(iterator.hasNext()) {
			if(iterator.next().id.equals(General.TEXT_BLANK)) {
				iterator.remove();
				break;
			}
		}
	}
	private static int changeGenderDB(final com.dealoka.app.model.Gender gender) {
		int index = SubsGender.getIndexGenderDB(gender.id);
		if(index != -1) {
			SubsGender.gender_db.get(index).gender_name = gender.gender_name;
		}
		return index;
	}
}