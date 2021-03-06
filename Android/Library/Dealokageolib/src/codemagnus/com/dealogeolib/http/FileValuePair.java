/*
 * 
 * Copyright (C) 2014 CodeMagnus. All Rights Reserved.
 * Created by Edgar Harold C. Reyes
 * eharoldreyes@gmail.com
 * 
 */
package codemagnus.com.dealogeolib.http;

import java.io.File;

public class FileValuePair {

	private File file;
	private String name;

	public FileValuePair(String name, File file){
		this.setFile(file);
		this.setName(name);
	}
	
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
