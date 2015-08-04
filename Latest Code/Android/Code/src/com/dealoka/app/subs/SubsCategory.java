package com.dealoka.app.subs;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.app.Offers;
import com.dealoka.app.activity.Category;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.subs.SubsManager.SubsEnum;
import com.dealoka.lib.General;

public class SubsCategory {
	private static ArrayList<com.dealoka.app.model.Category> _category;
	public static ArrayList<com.dealoka.app.model.Category> category_db;
	public static void subscribeCategory() {
		SubsCategory.category_db = GlobalVariables.query_controller.getCategory();
		SubsCategory._category = new ArrayList<com.dealoka.app.model.Category>();
		if(SubsManager.subs_reconnect_list.indexOf(SubsEnum.SubsCategory) != -1) {
			SubsManager.subs_reconnect_list.remove(SubsEnum.SubsCategory);
		}
		if(SubsManager.subs_controller == null) {
			return;
		}
		SubsCategory.resubscribeCategory();
	}
	public static void resubscribeCategory() {
		if(SubsManager.subs_controller.isConnected()) {
			SubsManager.subs_controller.subsCategory();
		}else {
			SubsManager.subs_reconnect_list.add(SubsEnum.SubsCategory);
		}
	}
	public static void addCategory(final String id, final String result) {
		try {
			JSONObject json = new JSONObject(result);
			final com.dealoka.app.model.Category category =
					new com.dealoka.app.model.Category(id, json);
			if(SubsCategory.isCategoryExisted(category, false)) {
				return;
			}
			SubsCategory.removeCategoryDBTemp();
			SubsCategory._category.add(category);
			if(category.total_count != 0) {
				if(category.total_count != SubsCategory.category_db.size()) {
					if(category.total_count == SubsCategory._category.size()) {
						GlobalVariables.query_controller.truncateCategory();
						GlobalVariables.query_controller.addCategoryChanges(SubsCategory._category);
						SubsCategory.category_db = GlobalVariables.query_controller.getCategory();
						if(Category.instance != null) {
							Category.instance.SyncNewChanges();
						}
						if(Offers.instance != null) {
							Offers.instance.SyncCategoryNewChanges();
						}
					}
					return;
				}
			}
			if(SubsCategory.isCategoryDBExisted(category, false)) {
				GlobalVariables.query_controller.updateCategory(category);
				SubsCategory.changeCategoryDB(category);
			}else {
				SubsCategory.category_db.add(0, category);
				GlobalVariables.query_controller.addCategory(category);
				if(Category.instance != null) {
					Category.instance.SyncNewAdded();
				}
				if(Offers.instance != null) {
					Offers.instance.SyncCategoryNewAdded(category);
				}
			}
		}catch(JSONException ex) {}
	}
	public static void changeCategory(final String id, final String result) {
		try {
			JSONObject json = new JSONObject(result);
			final com.dealoka.app.model.Category category =
					new com.dealoka.app.model.Category(id, json.getString("category_name"));
			SubsCategory.changeCategoryDB(category);
			GlobalVariables.query_controller.updateCategory(category);
			if(Category.instance != null) {
				Category.instance.SyncChanged();
			}
			if(Offers.instance != null) {
				Offers.instance.SyncCategoryChanged(category);
			}
		}catch(JSONException ex) {}
	}
	public static void removeCategory(final String id) {
		SubsCategory.isCategoryExisted(id, true);
		SubsCategory.isCategoryDBExisted(id, true);
		GlobalVariables.query_controller.deleteCategory(id);
		if(Category.instance != null) {
			Category.instance.SyncRemoved();
		}
	}
	private static int getIndexCategoryDB(final String id) {
		int index = SubsCategory.category_db.size();
		while(index > 0) {
			index--;
			if(SubsCategory.category_db.get(index).id.equals(id)) {
				break;
			}
		}
		if(index == SubsCategory.category_db.size()) {
			index = -1;
		}
		return index;
	}
	private static boolean isCategoryExisted(final com.dealoka.app.model.Category category, final boolean delete) {
		boolean duplicate = false;
		Iterator<com.dealoka.app.model.Category> iterator = SubsCategory._category.iterator();
		while(iterator.hasNext()) {
			final com.dealoka.app.model.Category __category = iterator.next();
			if(__category.id.equals(category.id) && __category.category_name.equals(category.category_name)) {
				if(delete) {
					iterator.remove();
				}
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}
	private static boolean isCategoryExisted(final String id, final boolean delete) {
		boolean duplicate = false;
		Iterator<com.dealoka.app.model.Category> iterator = SubsCategory._category.iterator();
		while(iterator.hasNext()) {
			final com.dealoka.app.model.Category __category = iterator.next();
			if(__category.id.equals(id)) {
				if(delete) {
					iterator.remove();
				}
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}
	private static boolean isCategoryDBExisted(final com.dealoka.app.model.Category category, final boolean delete) {
		boolean duplicated = false;
		Iterator<com.dealoka.app.model.Category> iterator = SubsCategory.category_db.iterator();
		while(iterator.hasNext()) {
			final com.dealoka.app.model.Category __category = iterator.next();
			if(__category.id.equals(category.id) && __category.category_name.equals(category.category_name)) {
				if(delete) {
					iterator.remove();
				}
				duplicated = true;
				break;
			}
		}
		return duplicated;
	}
	private static boolean isCategoryDBExisted(final String id, final boolean delete) {
		boolean duplicated = false;
		Iterator<com.dealoka.app.model.Category> iterator = SubsCategory.category_db.iterator();
		while(iterator.hasNext()) {
			final com.dealoka.app.model.Category __category = iterator.next();
			if(__category.id.equals(id)) {
				if(delete) {
					iterator.remove();
				}
				duplicated = true;
				break;
			}
		}
		return duplicated;
	}
	private static void removeCategoryDBTemp() {
		Iterator<com.dealoka.app.model.Category> iterator = SubsCategory.category_db.iterator();
		while(iterator.hasNext()) {
			com.dealoka.app.model.Category category = iterator.next();
			if(category.id.equals(General.TEXT_BLANK)) {
				GlobalVariables.query_controller.deleteCategory(category.id);
				iterator.remove();
				break;
			}
		}
	}
	private static int changeCategoryDB(final com.dealoka.app.model.Category category) {
		int index = SubsCategory.getIndexCategoryDB(category.id);
		if(index != -1) {
			SubsCategory.category_db.get(index).category_name = category.category_name;
		}
		return index;
	}
}