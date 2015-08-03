package com.dealoka.lib;
import java.io.*;
public class Utils {
	public static void CopyStream(final InputStream is, final OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes=new byte[buffer_size];
			for(;;) {
				final int count = is.read(bytes, 0, buffer_size);
				if(count == -1) {
					break;
				}
				os.write(bytes, 0, count);
			}
		}catch(Exception ex) {}
	}
}