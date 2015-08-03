package com.dealoka.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {
	private MemoryCache memoryCache = new MemoryCache();
	private FileCache fileCache;
	private Map<ImageView, String> image_views = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private final int blank_image_res;
	public ImageLoader(final Context context, final String name, final int blank_image_res) {
		this.blank_image_res = blank_image_res;
		fileCache = new FileCache(context, name);
		executorService = Executors.newFixedThreadPool(5);
	}
	public void DisplayImage(final String url, final ImageView image_view) {
		try {
			image_views.put(image_view, url);
			final Bitmap bitmap = memoryCache.get(url);
			if(bitmap != null) {
				image_view.setImageBitmap(bitmap);
			}else {
				queueImage(url, image_view);
				image_view.setImageResource(blank_image_res);
			}
		}catch(OutOfMemoryError ex) {}
	}
	public void DisplayImage(final Bitmap bitmap, final ImageView image_view) {
		try {
			if(bitmap != null) {
				image_view.setImageBitmap(bitmap);
			}else {
				image_view.setImageResource(blank_image_res);
			}
		}catch(OutOfMemoryError ex) {}
	}
	public Bitmap getBitmap(final String url) {
		File f = fileCache.getFile(url);
		final Bitmap b = decodeFile(f);
		if(b != null) {
			return b;
		}
		try {
			Bitmap bitmap = null;
			final URL imageUrl = new URL(url);
			final HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.setInstanceFollowRedirects(true);
			final InputStream is = conn.getInputStream();
			final OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		}catch(Exception ex) {
			return null;
		}
	}
	private void queueImage(final String url, final ImageView image_view) {
		ImageToLoad p = new ImageToLoad(url, image_view);
		executorService.submit(new ImagesLoader(p));
	}
	private Bitmap decodeFile(File f) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			o.inPreferredConfig = Bitmap.Config.ARGB_8888;
			o.inDither = false;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			final int REQUIRED_SIZE = 100;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while(true) {
				if(width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
			o2.inDither = false;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		}catch(FileNotFoundException ex) {
		}catch(IOException ex) {}
		return null;
	}
	private class ImageToLoad {
		final String url;
		final ImageView image_view;
		public ImageToLoad(final String u, final ImageView i) {
			url = u;
			image_view = i;
		}
	}
	private class ImagesLoader implements Runnable {
		private ImageToLoad imageToLoad;
		ImagesLoader(ImageToLoad imageToLoad) {
			this.imageToLoad = imageToLoad;
		}
		@Override
		public void run() {
			if(ImageViewReused(imageToLoad)) {
				return;
			}
			final Bitmap bmp = getBitmap(imageToLoad.url);
			memoryCache.put(imageToLoad.url, bmp);
			if(ImageViewReused(imageToLoad)) {
				return;
			}
			final BitmapDisplayer bd = new BitmapDisplayer(bmp, imageToLoad);
			Activity a = (Activity)imageToLoad.image_view.getContext();
			a.runOnUiThread(bd);
		}
	}
	private boolean ImageViewReused(final ImageToLoad imageToLoad) {
		final String tag = image_views.get(imageToLoad.image_view);
		if(tag == null || !tag.equals(imageToLoad.url)) {
			return true;
		}
		return false;
	}
	private class BitmapDisplayer implements Runnable {
		private Bitmap bitmap;
		private ImageToLoad imageToLoad;
		public BitmapDisplayer(final Bitmap b, final ImageToLoad p) {
			bitmap = b;
			imageToLoad = p;
		}
		public void run() {
			try {
				if(ImageViewReused(imageToLoad)) return;
				if(bitmap != null) {
					imageToLoad.image_view.setImageBitmap(bitmap);
				}else {
					imageToLoad.image_view.setImageResource(blank_image_res);
				}
			}catch(OutOfMemoryError ex) {}
		}
	}
	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}
}