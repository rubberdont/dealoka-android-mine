package com.dealoka.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Doc {
	public static Writer openCSV(final String path, final String init_write) throws IOException {
		File csv = new File(path);
		final boolean exists = csv.exists();
		Writer out = new BufferedWriter(new FileWriter(csv, true), 4096);
		if(!exists && General.isNotNull(init_write)) {
			try {
				out.write(init_write);
			}catch(IOException ex) {
				try {
					out.close();
				}catch (IOException ex2) {
					throw ex;
				}
			}
		}
		return out;
	}
}