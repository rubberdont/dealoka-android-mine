package com.dealoka.lib.badge;

import java.io.ByteArrayOutputStream;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

interface BadgeColumns {
	public static final String ID = "_id";
	public static final String PACKAGE = "package";
	public static final String CLASS = "class";
	public static final String BADGE_COUNT = "badgecount";
	public static final String ICON = "icon";
}
public final class Badge implements BadgeColumns, Parcelable {
	private static final Uri CONTENT_URI = Uri.parse("content://com.sec.badge/apps");
	private static final int CONTENT_ID_COLUMN = 0;
	private static final int CONTENT_PACKAGE_COLUMN = 1;
	private static final int CONTENT_CLASS_COLUMN = 2;
	private static final int CONTENT_BADGE_COUNT_COLUMN = 3;
	private static final int CONTENT_ICON_COLUMN = 4;
	private static final String[] CONTENT_PROJECTION = {
		ID, PACKAGE, CLASS, BADGE_COUNT, ICON
	};
	private static final String BADGE_SELECTION = BadgeColumns.CLASS + "=?";
	public Uri mBaseUri;
	public long mId;
	public String mPackage;
	public String mClass;
	public int mBadgeCount;
	public byte[] mIcon;
	public Badge() {
		mBaseUri = CONTENT_URI;
	}
	private boolean isSaved() {
		return mId > 0;
	}
	private void restore(Cursor c) {
		mId = c.getLong(CONTENT_ID_COLUMN);
		mPackage = c.getString(CONTENT_PACKAGE_COLUMN);
		mClass = c.getString(CONTENT_CLASS_COLUMN);
		mBadgeCount = c.getInt(CONTENT_BADGE_COUNT_COLUMN);
		mIcon = c.getBlob(CONTENT_ICON_COLUMN);
	}
	private ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(PACKAGE, mPackage);
		cv.put(CLASS, mClass);
		cv.put(BADGE_COUNT, mBadgeCount);
		cv.put(ICON, mIcon);
		return cv;
	}
	public static boolean isBadgingSupported(Context context) {
		Preferences prefs = Preferences.getPreferences(context);
		final int isSupported = prefs.getIsBadgingSupported();
		if(isSupported == -1) {
			try {
				Cursor c = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
				if(c == null) {
					prefs.setIsBadgingSupported(false);
					return false;
				}else {
					c.close();
					return true;
				}
			}catch(SecurityException ex) {
				return false;
			}
		}else {
			return isSupported == 1;
		}
	}
	public static Badge getBadge(Context context, String className) {
		if(!isBadgingSupported(context)) return null;
		Cursor c = context.getContentResolver().query(
				CONTENT_URI,
				CONTENT_PROJECTION, BADGE_SELECTION,
				new String[] {className}, null);
		try {
			if(!c.moveToFirst()) {
				return null;
			}
			Badge b = new Badge();
			b.restore(c);
			return b;
		}finally {
			c.close();
		}
	}
	public static Badge[] getAllBadges(Context context) {
		if(!isBadgingSupported(context)) return null;
		Badge[] badges = null;
		Cursor c = context.getContentResolver().query(
				CONTENT_URI,
				CONTENT_PROJECTION, null, null, null);
		try {
			if(!c.moveToFirst()) {
				return null;
			}
			badges = new Badge[c.getCount()];
			c.moveToPosition(-1);
			while (c.moveToNext()) {
				Badge b = new Badge();
				b.restore(c);
				badges[c.getPosition()] = b;
			}
			return badges;
		}finally {
			c.close();
		}
	}
	public Bitmap getIcon() {
		if(mIcon == null || mIcon.length == 0) return null;
		return BitmapFactory.decodeByteArray(mIcon, 0, mIcon.length);
	}
	public void setIcon(Bitmap icon) {
		if(icon == null) return;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		mIcon = stream.toByteArray();
	}
	public void setIcon(Drawable icon) {
		if(icon == null) return;
		BitmapDrawable bitDw = ((BitmapDrawable) icon);
		setIcon(bitDw.getBitmap());
	}
	public Uri save(Context context) {
		if(isSaved() || !isBadgingSupported(context)) {
			throw new UnsupportedOperationException();
		}
		Uri res = context.getContentResolver().insert(mBaseUri, toContentValues());
		mId = Long.parseLong(res.getPathSegments().get(1));
		return res;
	}
	public boolean update(Context context) {
		if(!isSaved() || !isBadgingSupported(context)) {
			throw new UnsupportedOperationException();
		}
		Uri uri = ContentUris.withAppendedId(mBaseUri, mId);
		final int rows = context.getContentResolver().update(uri, toContentValues(), null, null);
		return rows > 0;
	}
	public boolean delete(Context context) {
		if(!isBadgingSupported(context)) {
			throw new UnsupportedOperationException();
		}
		Uri uri = ContentUris.withAppendedId(mBaseUri, mId);
		final int rows = context.getContentResolver().delete(uri, null, null);
		return rows > 0;
	}
	public static boolean deleteAllBadges(Context context) {
		if(!isBadgingSupported(context)) {
			throw new UnsupportedOperationException();
		}
		final int rows = context.getContentResolver().delete(CONTENT_URI,
				BadgeColumns.PACKAGE + "=?", new String[] {context.getPackageName()});
		return rows > 0;
	}
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Badge)) return false;
		Badge b = (Badge)o;
		if(mId != b.mId) return false;
		if(!TextUtils.equals(mPackage, b.mPackage)) return false;
		if(!TextUtils.equals(mClass, b.mClass)) return false;
		if(mBadgeCount != b.mBadgeCount) return false;
		if(mIcon != b.mIcon) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result = 17;
		result = (31 * result + (int)(mId ^ (mId >>> 32)));
		result = (31 * result + (mPackage == null ? 0 : mPackage.hashCode()));
		result = (31 * result + (mClass == null ? 0 : mClass.hashCode()));
		result = (31 * result + mBadgeCount);
		result = (31 * result + (mIcon == null ? 0 : mIcon.hashCode()));
		return result;
	}
	@Override
	public String toString() {
		return BadgeColumns.ID + ": " + String.valueOf(mId) + ", "
				+ BadgeColumns.PACKAGE + ": " + String.valueOf(mPackage) + ", "
				+ BadgeColumns.CLASS + ": " + String.valueOf(mClass) + ", "
				+ BadgeColumns.BADGE_COUNT + ": " + String.valueOf(mBadgeCount) + ", "
				+ "hasIcon: " + (mIcon != null ? "true" : "false");
	}
	@Override
	public int describeContents() {
		return 0;
	}
	public static final Parcelable.Creator<Badge> CREATOR = new Parcelable.Creator<Badge>() {
		@Override
		public Badge createFromParcel(Parcel in) {
			return new Badge(in);
		}
		@Override
		public Badge[] newArray(int size) {
			return new Badge[size];
		}
	};
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mPackage);
		dest.writeString(mClass);
		dest.writeInt(mBadgeCount);
		dest.writeByteArray(mIcon);
	}
	public Badge(Parcel in) {
		mBaseUri = Badge.CONTENT_URI;
		mId = in.readLong();
		mPackage = in.readString();
		mClass = in.readString();
		mBadgeCount = in.readInt();
		mIcon = in.createByteArray();
	}
}