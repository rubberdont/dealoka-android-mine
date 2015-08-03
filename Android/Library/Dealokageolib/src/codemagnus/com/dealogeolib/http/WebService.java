/*
 * 
 * Copyright (C) 2014 CodeMagnus. All Rights Reserved.
 * Created by Edgar Harold C. Reyes
 * 
 */
package codemagnus.com.dealogeolib.http;


import static codemagnus.com.dealogeolib.utils.LogUtils.LOGD;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import codemagnus.com.dealogeolib.http.model.ValuePair;

public class WebService {

	private static final String tag 			= "WebService";

    //for wifi post
    public static final String COORDINATES 		= "coordinates";
    public static final String WIFIS 			= "wifis";
    public static final String TOWERS 			= "towers";
    public static final String LATITUDE 		= "lat";
    public static final String LONGITUDE 		= "lng";
    public static final String DEVICE_ID 		= "device_id";


    //wifi post params
    public static final String BSSID 		= "bssid";
    public static final String FREQUENCY 	= "frequency";
    public static final String HIDDEN_SSID 	= "hidden_ssid";
    public static final String NETWORK_ID 	= "network_id";
    public static final String RSSI 	    = "rssi";
    public static final String SSID 	    = "ssid";
    public static final String CAPABILITIES 	= "capabilities";

    //tower cell post
    public static final String ID 	                    = "id";
    public static final String BTS 	                    = "bts";
    public static final String OPERATOR 	            = "operator";
    public static final String LOCATION_AREA_CODE 	    = "location_area_code";
    public static final String CELL_ID 	                = "cell_id";
    public static final String SIGNAL_STRENGTH 	        = "signal_strength";
    public static final String TOWER_RSSI 	            = "rssi";
    public static final String PRIMARY_SCRAMBLED_CODE 	= "primaryScrambleCode";
    public static final String NETWORK_TYPE 	        = "network_type";
    public static final String TOWER_LATLNG 	        = "tower_latLng";

	public static final String MESSAGE 			= "message";
	public static final String STATUS_CODE 		= "status_code";
	public static final String ERROR 			= "error";
	public static final String ERROR_MESSAGE 	= "error_message";

	public static final String METHOD_DELETE 	= "DELETE";
	public static final String METHOD_POST 		= "POST";
	public static final String METHOD_PUT 		= "PUT";
	public static final String METHOD_GET 		= "GET";
	private static final int TIME_OUT 	 		= 10000;

	public static Response HttpUrlConnection(String url, String requestMethod,
											 List<ValuePair> headers, String parameters) throws Exception {
		Response webserviceResponse = new Response();
		DataOutputStream request;
		HttpURLConnection connection = null;
		String response;
		int status;
		try {
			URL newURL = new URL(url);
			LOGD(tag, "HttpUrlConnection url: " + url);
			connection = (HttpURLConnection) newURL.openConnection();

			connection.setRequestMethod(requestMethod);
			connection.setConnectTimeout(TIME_OUT);
			if (!requestMethod.equals("GET")) {
				for (ValuePair header : headers) {
                    LOGD(tag, "HttpUrlConnection header: " + header.toString());
					connection.setRequestProperty(header.getKey(), header.getValue());
				}
                LOGD(tag, "HttpUrlConnection parameter: " + parameters);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				request = new DataOutputStream(connection.getOutputStream());
				if (parameters != null)
					request.writeBytes(parameters);
				request.flush();
				request.close();
			}
			status = connection.getResponseCode();
			String line;
			InputStream is;
			if (status == 200)
				is = connection.getInputStream();
			else
				is = connection.getErrorStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			int readBytes = 0;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
				readBytes += line.getBytes("ISO-8859-2").length + 2; // CRLF bytes!!
                LOGD("HttpUrlConnection", readBytes + "%");
			}
			reader.close();
			response = sb.toString();

            LOGD("HttpURLConnection", "response " + connection.getResponseMessage());
            LOGD("HttpUrlConnection", "status " + status);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		webserviceResponse.setHttpResponse(null);
		webserviceResponse.setResponseMesssage(response);
		webserviceResponse.setResponseCode(status);
		webserviceResponse.setException(null);

		return webserviceResponse;
	}

	public static Response HttpUrlConnectionPostImage(String url, File file, String filename) throws Exception {
		Response webserviceResponse = new Response();

		HttpURLConnection conn;
		DataOutputStream dos;
//		DataInputStream inStream = null;
		String response;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int status;

		int bytesRead, bytesAvailable, bufferSize;

		byte[] buffer;

		int maxBufferSize = 1024 * 1024;

		FileInputStream fileInputStream = new FileInputStream(file);


		URL newUrl = new URL(url);

		conn = (HttpURLConnection) newUrl.openConnection();

		conn.setDoInput(true);
		// Allow Outputs
		conn.setDoOutput(true);
		// Don't use a cached copy.
		conn.setUseCaches(false);
		// Use a post method.
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");

		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

		dos = new DataOutputStream(conn.getOutputStream());

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + filename + "\"" + lineEnd);
		dos.writeBytes(lineEnd);

        LOGD("MediaPlayer", "Headers are written");

		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		buffer = new byte[bufferSize];


		bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		while (bytesRead > 0) {
			dos.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}

		dos.writeBytes(lineEnd);
		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		status = conn.getResponseCode();
		InputStream is;
		if(status == 200){
			is = conn.getInputStream();
		}else {
			is = conn.getErrorStream();
		}
		BufferedReader in = new BufferedReader(
				new InputStreamReader(is));
		String inputLine;
		StringBuilder sb = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		response = sb.toString();
        LOGD("HttpURLConnection", "response " + conn.getResponseMessage());
        LOGD("HttpUrlConnection", "status " + status);
		// close streams
        LOGD("MediaPlayer", "File is written");
		fileInputStream.close();
		dos.flush();
		dos.close();

//        try {
//            inStream = new DataInputStream (is);
//            String str;
//            StringBuilder strB = new StringBuilder();
//            while (( str = inStream.readUTF()) != null){
//                strB.append(str);
//                Log.e("MediaPlayer","Server Response"+str);
//            }
//            Log.e("MediaPlayer", "response: " + strB.toString());
//            inStream.close();
//
//        }
//        catch (IOException ioex){
//            Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
//        }

		webserviceResponse.setHttpResponse(null);
		webserviceResponse.setResponseMesssage(response);
		webserviceResponse.setResponseCode(status);
		webserviceResponse.setException(null);

		return webserviceResponse;
	}
}
