package com.dealoka.lib;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class MemoryCache {
	private Map<String, SoftReference<Bitmap>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
	public Bitmap get(final String id) {
		if(!cache.containsKey(id)) {
			return null;
		}
		final SoftReference<Bitmap> ref = cache.get(id);
		return ref.get();
	}
	public void put(final String id, final Bitmap bitmap) {
		cache.put(id, new SoftReference<Bitmap>(bitmap));
	}
	public void clear() {
		cache.clear();
	}
}