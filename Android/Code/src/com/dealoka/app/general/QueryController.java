package com.dealoka.app.general;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.model.BatchWallet;
import com.dealoka.app.model.Category;
import com.dealoka.app.model.Gender;
import com.dealoka.app.model.OfferRedeemWallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

public class QueryController {
	public QueryController() {}
	public ArrayList<Category> getCategory() {
		ArrayList<Category> value = new ArrayList<Category>();
		String sql = "SELECT id, key, value, image FROM category ORDER BY key ASC";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, null);
			while(c.moveToNext()) {
				value.add(new Category(
						c.getString(c.getColumnIndex("id")),
						c.getString(c.getColumnIndex("key")),
						c.getString(c.getColumnIndex("value")),
						c.getString(c.getColumnIndex("image"))));
			}
		}finally {
			try {
				if(c != null) {
					c.close();
				}
			}catch(SQLiteException ex) {}
		}
		return value;
	}
	public ArrayList<Gender> getGender() {
		ArrayList<Gender> value = new ArrayList<Gender>();
		String sql = "SELECT id, key, value FROM gender";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, null);
			while(c.moveToNext()) {
				value.add(new Gender(
						c.getString(c.getColumnIndex("id")),
						c.getString(c.getColumnIndex("key")),
						c.getString(c.getColumnIndex("value"))));
			}
			c.close();
		}finally {
			try {
				if(c != null) {
					c.close();
				}
			}catch(SQLiteException ex) {}
		}
		return value;
	}
	public ArrayList<OfferRedeemWallet> getWallet() {
		int wallet_point = 0;
		ArrayList<OfferRedeemWallet> value = new ArrayList<OfferRedeemWallet>();
		String sql = "SELECT id, result, validated, expired FROM wallet ORDER BY validated, expired ASC";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, null);
			while(c.moveToNext()) {
				final OfferRedeemWallet offer_redeem_wallet = new OfferRedeemWallet(
						c.getString(c.getColumnIndex("id")),
						c.getString(c.getColumnIndex("result")));
				offer_redeem_wallet.validated = (c.getInt(c.getColumnIndex("validated")) == 1) ? true : false;
				if(c.getInt(c.getColumnIndex("expired")) == 0 && offer_redeem_wallet.expired == true) {
					expiredWallet(offer_redeem_wallet.id);
				}
				value.add(offer_redeem_wallet);
				if(!offer_redeem_wallet.validated && !offer_redeem_wallet.expired) {
					wallet_point++;
				}
			}
		}finally {
			try {
				if(c != null) {
					c.close();
				}
			}catch(SQLiteException ex) {}
		}
		CallOfferRedeemWallet.setWalletPoint(wallet_point);
		return value;
	}
	public ArrayList<BatchWallet> getBatchWallet() {
		ArrayList<BatchWallet> value = new ArrayList<BatchWallet>();
		String sql = "SELECT offer_redeem_wallet_id, offer_id, sent, log, md5hash, unique_code, category, merchant_id, user_token FROM batch_wallet WHERE sent = 0";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, null);
			while(c.moveToNext()) {
				BatchWallet batch_wallet = new BatchWallet();
				batch_wallet.offer_redeem_wallet_id = c.getString(c.getColumnIndex("offer_redeem_wallet_id"));
				batch_wallet.offer_id = c.getString(c.getColumnIndex("offer_id"));
				batch_wallet.sent = c.getInt(c.getColumnIndex("sent"));
				batch_wallet.log = c.getLong(c.getColumnIndex("log"));
				batch_wallet.md5hash = c.getString(c.getColumnIndex("md5hash"));
				batch_wallet.unique_code = c.getString(c.getColumnIndex("unique_code"));
				batch_wallet.category = c.getString(c.getColumnIndex("category"));
				batch_wallet.merchant_id = c.getString(c.getColumnIndex("merchant_id"));
				batch_wallet.user_token = c.getString(c.getColumnIndex("user_token"));
				value.add(batch_wallet);
			}
		}finally {
			try {
				if(c != null) {
					c.close();
				}
			}catch(SQLiteException ex) {}
		}
		return value;
	}
	public void addCategory(final Category category) {
		GlobalVariables.sql_connection.execSQL(
				"INSERT INTO category VALUES (?, ?, ?, ?)",
				new String[] {category.id, category.category_value, category.category_name, category.category_image});
	}
	public void addCategoryChanges(final ArrayList<Category> category_list) {
		Iterator<Category> iterator = category_list.iterator();
		while(iterator.hasNext()) {
			Category category = iterator.next();
			addCategory(category);
		}
	}
	public void addGender(final Gender gender) {
		GlobalVariables.sql_connection.execSQL(
				"INSERT INTO gender VALUES (?, ?, ?)",
				new String[] {gender.id, gender.gender_value, gender.gender_name});
	}
	public void addWallet(final String id, final String result, final boolean validated, final boolean expired) {
		GlobalVariables.sql_connection.execSQL(
				"INSERT INTO wallet VALUES (?, ?, ?, ?)",
				new Object[] {id, result, validated ? 1 : 0, expired ? 1 : 0});
	}
	public void addBatchWallet(
			final String offer_redeem_wallet_id,
			final String offer_id,
			final String md5hash,
			final String unique_code,
			final String category,
			final String merchant_id,
			final String user_token) {
		GlobalVariables.sql_connection.execSQL(
				"INSERT INTO batch_wallet (offer_redeem_wallet_id, offer_id, sent, log, md5hash, unique_code, category, merchant_id, user_token) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new Object[] {offer_redeem_wallet_id, offer_id, 0, new Date().getTime(), md5hash, unique_code, category, merchant_id, user_token});
	}
	public void updateCategory(final Category category) {
		GlobalVariables.sql_connection.execSQL(
				"UPDATE category SET value = ? WHERE id = ?",
				new String[] {category.category_name, category.id});
	}
	public void updateGender(final Gender gender) {
		GlobalVariables.sql_connection.execSQL(
				"UPDATE gender SET value = ? WHERE id = ?",
				new String[] {gender.gender_name, gender.id});
	}
	public void expiredWallet(final String id) {
		GlobalVariables.sql_connection.execSQL(
				"UPDATE wallet SET expired = 1 WHERE id = ?",
				new Object[] {id});
	}
	public void unexpiredWallet(final String id) {
		GlobalVariables.sql_connection.execSQL(
				"UPDATE wallet SET expired = 0 WHERE id = ?",
				new Object[] {id});
	}
	public void validatedWallet(final String id) {
		GlobalVariables.sql_connection.execSQL(
				"UPDATE wallet SET validated = 1 WHERE id = ?",
				new Object[] {id});
	}
	public void updateBatchWallet(final String offer_redeem_wallet_id) {
		GlobalVariables.sql_connection.execSQL(
				"UPDATE batch_wallet SET sent = 1, log = ? WHERE offer_redeem_wallet_id = ?",
				new Object[] {new Date().getTime(), offer_redeem_wallet_id});
	}
	public void deleteCategory(final String id) {
		GlobalVariables.sql_connection.execSQL(
				"DELETE FROM category WHERE id = ?",
				new String[] {id});
	}
	public void deleteGender(final String id) {
		GlobalVariables.sql_connection.execSQL(
				"DELETE FROM gender WHERE id = ?",
				new String[] {id});
	}
	public void deleteWallet(final String id) {
		GlobalVariables.sql_connection.execSQL(
				"DELETE FROM wallet WHERE id = ? OR id = ?",
				new String[] {id, "-" + id});
	}
	public boolean isCategoryExist(final String id) {
		boolean bExist = false;
		String sql = "SELECT COUNT(*) FROM category WHERE id = ?";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, new String[] {id});
			c.moveToFirst();
			if(c.getInt(0) > 0) {
				bExist = true;
			}else {
				bExist = false;
			}
		}finally {
			if(c != null) {
				c.close();
			}
		}
		return bExist;
	}
	public boolean isGenderExist(final String id) {
		boolean bExist = false;
		String sql = "SELECT COUNT(*) FROM gender WHERE id = ?";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, new String[] {id});
			c.moveToFirst();
			if(c.getInt(0) > 0) {
				bExist = true;
			}else {
				bExist = false;
			}
		}finally {
			if(c != null) {
				c.close();
			}
		}
		return bExist;
	}
	public boolean isWalletExist(final String id) {
		boolean bExist = false;
		String sql = "SELECT COUNT(*) FROM wallet WHERE id = ? OR id = ?";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, new String[] {id, "-" + id});
			c.moveToFirst();
			if(c.getInt(0) > 0) {
				bExist = true;
			}else {
				bExist = false;
			}
		}finally {
			if(c != null) {
				c.close();
			}
		}
		return bExist;
	}
	public boolean isBatchWalletExist() {
		boolean bExist = false;
		String sql = "SELECT COUNT(*) FROM batch_wallet WHERE sent = 0";
		Cursor c = null;
		try {
			c = GlobalVariables.sql_connection.rawQuery(sql, new String[] {});
			c.moveToFirst();
			if(c.getInt(0) > 0) {
				bExist = true;
			}else {
				bExist = false;
			}
		}finally {
			if(c != null) {
				c.close();
			}
		}
		return bExist;
	}
	public void truncateCategory() {
		GlobalVariables.sql_connection.execSQL("DELETE FROM category");
		GlobalVariables.sql_connection.execSQL("VACUUM");
	}
	public void truncateGender() {
		GlobalVariables.sql_connection.execSQL("DELETE FROM gender");
		GlobalVariables.sql_connection.execSQL("VACUUM");
	}
	public void truncateWallet() {
		GlobalVariables.sql_connection.execSQL("DELETE FROM wallet");
		GlobalVariables.sql_connection.execSQL("VACUUM");
	}
}