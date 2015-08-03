package com.dealoka.lib.url;

import java.io.File;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;

public class URLAsyncResponse implements Runnable {
	public static String responseData;
	public static final int DID_START = 0;
    public static final int DID_ERROR = 1;
    public static final int DID_SUCCEED = 2;
    private static final int GET = 0;
    private static final int POST = 1;
    private static final int PUT = 2;
    private static final int DELETE = 3;
    private static final int FILE = 4;
    private String url;
    private int method;
    private Handler handler;
    private List<NameValuePair> data;
    private String filename;
    private HttpClient httpClient;
    public URLAsyncResponse() {
    	this(new Handler());
    }
    public URLAsyncResponse(Handler _handler) {
    	handler = _handler;
    }
    public void create(int method, String url, List<NameValuePair> data) {
        this.method = method;
        this.url = url;
        this.data = data;
        URLConnection.getInstance().push(this);
    }
    public void create(int method, String url, List<NameValuePair> data, String filename) {
        this.method = method;
        this.url = url;
        this.data = data;
        this.filename = filename;
        URLConnection.getInstance().push(this);
    }
    public void get(String url) {
    	create(GET, url, null);
    }
    public void post(String url, List<NameValuePair> data) {
        create(POST, url, data);
    }
    public void file(String url, List<NameValuePair> data, String filename) {
    	create(FILE, url, data, filename);
    }
    public void put(String url, List<NameValuePair> data) {
        create(PUT, url, data);
    }
    public void delete(String url) {
        create(DELETE, url, null);
    }
    public void run() {
        handler.sendMessage(Message.obtain(handler, URLAsyncResponse.DID_START));
        httpClient = new DefaultHttpClient();
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 5000);
        try {
            HttpResponse response = null;
            HttpEntity entityResponse = null;
            UrlEncodedFormEntity entity = null;
            HttpPost httpPost = null;
            switch (method) {
            case GET:
                response = httpClient.execute(new HttpGet(url));
                entityResponse = response.getEntity();
                responseData = EntityUtils.toString(entityResponse);
                break;
            case POST:
            	entity = new UrlEncodedFormEntity(data);
            	entity.setContentEncoding("HTTP.ISO_8859_1");
            	entity.setContentType("application/x-www-form-urlencoded");
                httpPost = new HttpPost(url);
                httpPost.setEntity(entity);
                response = httpClient.execute(httpPost);
                entityResponse = response.getEntity();
                responseData = EntityUtils.toString(entityResponse);
                break;
            case PUT:
            	entity = new UrlEncodedFormEntity(data);
            	entity.setContentEncoding("HTTP.ISO_8859_1");
            	entity.setContentType("application/x-www-form-urlencoded");
                HttpPut httpPut = new HttpPut(url);
                httpPut.setEntity(entity);
                response = httpClient.execute(httpPut);
                entityResponse = response.getEntity();
                responseData = EntityUtils.toString(entityResponse);
                break;
            case DELETE:
                response = httpClient.execute(new HttpDelete(url));
                break;
            case FILE:
            	HttpParams params = new BasicHttpParams();
    	        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    			HttpClient httpClient = new DefaultHttpClient(params);
    	        httpPost = new HttpPost(url);
    			MultipartEntity multiEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    			int size = data.size();
    			while(size > 0) {
    				size--;
    				multiEntity.addPart(data.get(size).getName(), new StringBody(data.get(size).getValue()));
    			}
    			File file = new File(filename);
    			ContentBody cbFile = new FileBody(file);
    			multiEntity.addPart("file", cbFile);
    			httpPost.setEntity(multiEntity);
    			response = httpClient.execute(httpPost);
    	        entityResponse = response.getEntity();
                responseData = EntityUtils.toString(entityResponse);
            	break;
            }
            handler.sendMessage(Message.obtain(handler, URLAsyncResponse.DID_SUCCEED));
        }catch(Exception ex) {
            handler.sendMessage(Message.obtain(handler, URLAsyncResponse.DID_ERROR, ex));
        }
        URLConnection.getInstance().didComplete(this);
    }
}