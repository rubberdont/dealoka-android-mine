package com.dealoka.lib.controller;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class ImageGalleryController {
	public interface ImageGalleryCallback {
		public abstract void didImageGalleryOK(final Bitmap bitmap);
		public abstract void didImageGalleryCancel();
	}
	private static final int IMAGE_GALLERY_ACTIVITY_RESULT = 1001;
	private static Activity activity;
	private static ImageGalleryCallback image_gallery_callback;
	public static void open(final Activity act, final ImageGalleryCallback igc) {
		activity = act;
		image_gallery_callback = igc;
		final Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		activity.startActivityForResult(intent, IMAGE_GALLERY_ACTIVITY_RESULT); 
	}
	public static Bitmap onActivityResult(final int request_code, final int result_code, Intent intent) { 
		Bitmap bitmap = null;
		switch(request_code) {
			case IMAGE_GALLERY_ACTIVITY_RESULT:
				if(result_code == Activity.RESULT_OK) {
					final Uri selected_image = intent.getData();
					try {
						final InputStream image_stream = activity.getContentResolver().openInputStream(selected_image);
						bitmap = BitmapFactory.decodeStream(image_stream);
						if(image_gallery_callback != null) {
							image_gallery_callback.didImageGalleryOK(bitmap);
						}
					}catch(FileNotFoundException e) {}
				}
		}
		return bitmap;
	}
}