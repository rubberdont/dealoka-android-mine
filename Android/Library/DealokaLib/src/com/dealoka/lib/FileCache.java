package com.dealoka.lib;

import java.io.File;

import android.content.Context;

public class FileCache {
	private File cache_directory;
	public FileCache(final Context context, final String name) {
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cache_directory = new File(android.os.Environment.getExternalStorageDirectory(), name);
		}else {
			cache_directory = context.getCacheDir();
		}
		if(!cache_directory.exists()) {
			cache_directory.mkdirs();
		}
	}
	public File getFile(final String url) {
		final String filename = String.valueOf(url.hashCode());
		final File f = new File(cache_directory, filename);
		return f;
	}
	public void clear() {
		File[] files = cache_directory.listFiles();
		if(files == null) {
			return;
		}
		for(File f:files) {
			f.delete();
		}
	}
}