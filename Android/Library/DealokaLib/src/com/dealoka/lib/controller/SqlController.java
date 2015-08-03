package com.dealoka.lib.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SqlController {
	private final Context context;
	private final String path_sql;
	private final String sql_file;
	public SqlController(final Context context, final String path_sql, final String sql_file) {
		this.context = context;
		this.path_sql = path_sql;
		this.sql_file = sql_file;
	}
	public SQLiteDatabase openConnection() {
		SQLiteDatabase DBConnection = null;
		createDatabase();
		DBConnection = SQLiteDatabase.openDatabase(path_sql + sql_file, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return DBConnection;
	}
	public void closeConnection(SQLiteDatabase DBConnection) {
		if(DBConnection != null) {
			DBConnection.close();
		}
	}
	private void createDatabase() {
		boolean dbExist = checkDataBase();
		if(dbExist) {
		}else {
			try {
				InputStream input = context.getAssets().open(sql_file);
				new File(path_sql + sql_file).getParentFile().mkdir();
				OutputStream output = new FileOutputStream(path_sql + sql_file);
				byte[] buffer = new byte[1024];
				int length;
				while((length = input.read(buffer))>0) {
					output.write(buffer, 0, length);
				}
				output.flush();
				output.close();
				input.close();
			}catch(IOException ex) {}
    	}
	}
	private boolean checkDataBase() {
		File dbFile = new File(path_sql + sql_file);
		return dbFile.exists();
    }
}