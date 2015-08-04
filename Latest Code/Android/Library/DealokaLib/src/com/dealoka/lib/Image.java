package com.dealoka.lib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.TypedValue;

public class Image {
	public static int getPixels(final Context context, final int dipValue) {
		Resources r = context.getResources();
		int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
		return px;
	}
	public static byte[] getBytesFromBitmap(final Bitmap bitmap, final int compress_quality) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, compress_quality, stream);
		return stream.toByteArray();
	}
	public static String getStringFromBitmap(final Bitmap bitmap, final int comress_quality) {
		if(bitmap == null) {
			return General.TEXT_BLANK;
		}
		return General.getStringFromBytes(getBytesFromBitmap(bitmap, comress_quality));
	}
	public static Bitmap getBitmapFromBytes(final byte[] bytes) {
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	public static Bitmap getBitmapFromString(final String value) {
		byte[] bytes = General.getBytesFromString(value);
		return getBitmapFromBytes(bytes);
	}
	public static Bitmap getBitmapFromString(final String value, final int width, final int height) {
		byte[] bytes = General.getBytesFromString(value);
		return decodeToLowResImage(bytes, width, height);
	}
	public static Bitmap decodeToLowResImage(final byte[] b, final int width, final int height) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new ByteArrayInputStream(b), null, o);
			final int REQUIRED_SIZE_WIDTH = (int)(width * 0.7);
			final int REQUIRED_SIZE_HEIGHT = (int)(height * 0.7);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while(true) {
				if(width_tmp / 2 < REQUIRED_SIZE_WIDTH || height_tmp / 2 < REQUIRED_SIZE_HEIGHT) break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new ByteArrayInputStream(b), null, o2);
		}catch(OutOfMemoryError ex) {}
		return null;
	}
	public static String getStringFromBitmap(final Bitmap bitmap) {
		if(bitmap == null) {
			return General.TEXT_BLANK;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		final byte[] bytes = baos.toByteArray();
		return General.getStringFromBytes(bytes);
	}
}