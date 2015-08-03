package com.dealoka.lib.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.dealoka.lib.net.Base64;
import com.dealoka.lib.net.CustomDefaultHttpClient;
import com.dealoka.lib.net.CustomHttpResponse;
import com.dealoka.lib.net.HttpUtils;
import com.dealoka.lib.net.http.EntityFile;
import com.dealoka.lib.net.http.SimpleMultipartEntity;

public class URLResponse {
	public static enum URLEnum {
		Response ("Response"),
		StatusCode ("StatusCode");
		private final String id;
		URLEnum(String id) { this.id = id; }
	    public String getValue() { return id; }
	};
	public static interface URLCallback {
		public abstract void didURLResponse(final HashMap<URLEnum, Object> response);
		public abstract void didURLFailed();
	}
	private static URLResponse instance = null;
	private CustomDefaultHttpClient http_client = null;
	private URLResponse(final String user_agent) {
		http_client = new CustomDefaultHttpClient((DefaultHttpClient)HttpUtils.getNewHttpClient());
		HttpParams params = new BasicHttpParams();
		int timeout = 30000;
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		int timeout_socket = 50000;
		HttpConnectionParams.setSoTimeout(params, timeout_socket);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF_8");
		HttpProtocolParams.setUseExpectContinue(params, false);
		HttpProtocolParams.setUserAgent(params, user_agent);
		http_client.setParams(params);
	}
	public static URLResponse getInstance(final String user_agent) {
		instance = new URLResponse(user_agent);
		return instance;
	}
	public static HashMap<URLEnum, Object> post(
			final String user_agent,
			final String url,
			final HashMap<String, String> parameters) {
		CustomHttpResponse response = null;
		HashMap<URLEnum, Object> result = new HashMap<URLEnum, Object>();
		try {
			response = URLResponse.getInstance(user_agent).post(url, parameters);
			if(response != null) {
				if(response.getStatusLine() != null) {
					result.put(URLEnum.StatusCode, response.getStatusLine().getStatusCode());
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						result.put(URLEnum.Response, response.getResponseAsString());
					}
				}else {
					result.put(URLEnum.StatusCode, HttpStatus.SC_GATEWAY_TIMEOUT);
				}
			}else {
				result.put(URLEnum.StatusCode, HttpStatus.SC_GATEWAY_TIMEOUT);
			}
		}catch(IOException ex) {
			result.put(URLEnum.StatusCode, HttpStatus.SC_GATEWAY_TIMEOUT);
		}
		return result;
	}
	public static HashMap<URLEnum, Object> get(
			final String user_agent,
			final String url) {
		CustomHttpResponse response = null;
		HashMap<URLEnum, Object> result = new HashMap<URLEnum, Object>();
		try {
			response = URLResponse.getInstance(user_agent).get(url);
			if(response != null) {
				if(response.getStatusLine() != null) {
					result.put(URLEnum.StatusCode, response.getStatusLine().getStatusCode());
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						result.put(URLEnum.Response, response.getResponseAsString());
					}
				}else {
					result.put(URLEnum.StatusCode, HttpStatus.SC_GATEWAY_TIMEOUT);
				}
			}else {
				result.put(URLEnum.StatusCode, HttpStatus.SC_GATEWAY_TIMEOUT);
			}
		}catch(IOException ex) {
			result.put(URLEnum.StatusCode, HttpStatus.SC_GATEWAY_TIMEOUT);
		}
		return result;
	}
	private static List<NameValuePair> getHttpParamsFromMap(Map<String, String> params) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		for(Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			parameters.add(new BasicNameValuePair(key, value));
		}
		return parameters;
	}
	public CustomHttpResponse methodAsParameter(String url, String methodAsString, Map<String, String> params, String user, String pass) throws IOException {
		HttpPost method = new HttpPost(url);
		params.put("_method", methodAsString);
		method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
		return sendUsingMethodUsingCredentials(method, user, pass);
	}
	public CustomHttpResponse methodAsParameter(String url, String methodAsString, Map<String, String> params) throws IOException {
		HttpPost method = new HttpPost(url);
		params.put("_method", methodAsString);
		method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
		return sendUsingMethod(method);
	}
	public CustomHttpResponse put(String url, Map<String, String> params) throws IOException {
		HttpPut method = new HttpPut(url);
		method.setHeader("Accept", "*/*");
		method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse put(String url, Map<String, String> params, String user, String pass) throws IOException {
		HttpPut method = new HttpPut(url);
		method.setHeader("Accept", "*/*");
		method.addHeader("Authorization", "Basic " + getCredentials(user, pass));
		method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse post(String url, Map<String, String> params) throws IOException {
		HttpPost method = new HttpPost(url);
		method.setHeader("Accept", "*/*");
		method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
 		http_client.setRedirectHandler(new NotRedirectHandler());
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse post(String url, Map<String, String> params, List<EntityFile> entity_files) throws IOException {
		HttpPost method = new HttpPost(url);
		method.setHeader("Accept", "*/*");
		SimpleMultipartEntity entity = new SimpleMultipartEntity();
		for (Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			entity.addPart(key, value);
		}
		try {
			for(int i = 0; i< entity_files.size(); i++) {
				EntityFile entity_file = entity_files.get(i);
				boolean is_last = ((i+1)== entity_files.size() ? true : false);
				entity.addPart(entity_file.getType(), entity_file.getName(), entity_file.getFile(), entity_file.getMimeType(), is_last);
			}
		}catch(Exception ex) {}
		method.setEntity(entity);
		http_client.setRedirectHandler(new NotRedirectHandler());
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse postAutentication(String url, Map<String, String> params, String api_key, List<EntityFile> entity_files) throws IOException {
		HttpPost method = new HttpPost(url);
		method.setHeader("Accept", "*/*");
		method.addHeader("Authorization", "Basic " + getCredentials(api_key, "X"));
		SimpleMultipartEntity entity = new SimpleMultipartEntity();
		for(Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			entity.addPart(key, value);
		}
		for(int i = 0; i < entity_files.size(); i++) {
			EntityFile entity_file = entity_files.get(i);
			boolean is_last = ((i+1)== entity_files.size() ? true : false);
			entity.addPart(entity_file.getType(), entity_file.getName(), entity_file.getFile(), entity_file.getMimeType(), is_last);
		}
		method.setEntity(entity);
		http_client.setRedirectHandler(new NotRedirectHandler());
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse postStatusAutentication(String url, String status, Map<String, String> params, String api_key) throws IOException {
		HttpPost method = new HttpPost(url);
		method.setHeader("Accept", "*/*");
		method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
		method.addHeader("Authorization", "Basic " + getCredentials(api_key, "X"));
		method.addHeader("X-Prey-Status", status);
		http_client.setRedirectHandler(new NotRedirectHandler());
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse postAutentication(String url, Map<String, String> params, String api_key) throws IOException {
		HttpPost method = new HttpPost(url);
		method.setHeader("Accept", "*/*");
		method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
		method.addHeader("Authorization", "Basic " + getCredentials(api_key, "X"));
		http_client.setRedirectHandler(new NotRedirectHandler());
		HttpResponse httpResponse = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(httpResponse);
		return response;
	}
	public CustomHttpResponse getAutentication(String url, Map<String, String> params, String api_key) throws IOException {
		HttpPost method = new HttpPost(url);
		method.setHeader("Accept", "*/*");
		if(params != null) {
			method.setEntity(new UrlEncodedFormEntity(getHttpParamsFromMap(params), HTTP.UTF_8));
		}
		method.addHeader("Authorization", "Basic " + getCredentials(api_key, "X"));
		http_client.setRedirectHandler(new NotRedirectHandler());
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse get(String url, Map<String, String> params) throws IOException {
		HttpGet method = new HttpGet(url + URLEncodedUtils.format(getHttpParamsFromMap(params), "UTF-8"));
		method.setHeader("Accept", "*/*");
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public CustomHttpResponse get(String url, Map<String, String> params, String user, String pass) throws IOException {
		HttpGet method = null;
		if(params != null) {
			 method = new HttpGet(url + URLEncodedUtils.format(getHttpParamsFromMap(params), "UTF-8"));
		}else{
			 method = new HttpGet(url);
		}
		method.setHeader("Accept", "*/*");
		method.addHeader("Authorization", "Basic " + getCredentials(user, pass));
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		method.removeHeaders("Authorization");
		return response;
	}
	public CustomHttpResponse getAutentication2(String url, Map<String, String> params, String api_key) throws IOException {
		HttpGet method =null;
		if(params != null){
			 method = new HttpGet(url + URLEncodedUtils.format(getHttpParamsFromMap(params), "UTF-8"));
		}else{
			 method = new HttpGet(url);
		}
		method.setHeader("Accept", "*/*");
		method.addHeader("Authorization", "Basic " + getCredentials(api_key, "X"));
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		method.removeHeaders("Authorization");
		return response;
	}
	public CustomHttpResponse delete(String url, Map<String, String> params, String api_key) throws IOException {
		HttpDelete method = new HttpDelete(url + URLEncodedUtils.format(getHttpParamsFromMap(params), "UTF-8"));
		method.setHeader("Accept", "*/*");
		method.addHeader("Authorization", "Basic " + getCredentials(api_key, "X"));
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	private CustomHttpResponse sendUsingMethodUsingCredentials(HttpPost method, String user, String pass) throws IOException {
		CustomHttpResponse response = null;
		try {
			method.addHeader("Authorization", "Basic " + getCredentials(user, pass));
			HttpResponse http_response = http_client.execute(method);
			response = new CustomHttpResponse(http_response);
		}catch (IOException ex) {
			throw ex;
		}
		return response;
	}
	private String getCredentials(String user, String password) {
		return (Base64.encodeBytes((user + ":" + password).getBytes()));
	}
	private CustomHttpResponse sendUsingMethod(HttpRequestBase method) throws IOException {
		CustomHttpResponse response = null;
		try {
			HttpResponse http_response = http_client.execute(method);
			response = new CustomHttpResponse(http_response);
		}catch(IOException ex) {
			throw ex;
		}
		return response;
	}
	public CustomHttpResponse get(String url) throws IOException {
		HttpGet method = new HttpGet(url);
		HttpResponse http_response = http_client.execute(method);
		CustomHttpResponse response = new CustomHttpResponse(http_response);
		return response;
	}
	public StringBuilder getStringHttpResponse(HttpResponse httpResponse) throws Exception {
		HttpEntity http_entity = null;
		InputStream is = null;
		InputStreamReader input = null;
		BufferedReader reader = null;
		StringBuilder sb = null;
		try {
			http_entity = httpResponse.getEntity();
			is = http_entity.getContent();
			input = new InputStreamReader(is, "iso-8859-1");
			reader = new BufferedReader(input, 8);
			sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			sb.toString().trim();
		}catch(IllegalStateException ex) {
		}catch(Exception ex) {
		}finally {
			try {
				if(is != null) is.close();
			}catch(IOException ex) {}
			try {
				if(reader != null) reader.close();
			}catch(IOException ex) {}
			try {
				if(input != null) input.close();
			}catch(IOException ex) {}
		}
		return sb;
	}
}
final class NotRedirectHandler implements RedirectHandler {
	public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
		return false;
	}
	public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
		return null;
	}
}