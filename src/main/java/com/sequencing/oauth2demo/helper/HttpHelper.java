package com.sequencing.oauth2demo.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHelper {

	private static final Logger log = LoggerFactory.getLogger(HttpHelper.class);
	private static final HttpClient client = HttpClientBuilder.create().build();

	public static String doGet(String uri, Map<String, String> headers)
	{
		try {
			HttpGet get = new HttpGet(uri);

			if (headers != null) {
				for (Map.Entry<String, String> h : headers.entrySet())
					get.addHeader(h.getKey(), h.getValue());
			}

			HttpResponse response = client.execute(get);
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200)
				throw new RuntimeException(uri + " returned code " + statusCode);

			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		}
		catch (IOException e) {
			log.debug("Error executing HTTP GET request to " + uri, e);
		}
		catch (ParseException e) {
			log.debug("Error executing HTTP GET request to " + uri, e);
		}
		
		return null;
	}

	public static String doPost(String uri, Map<String, String> headers, Map<String, String> params)
	{
		try {
			HttpPost post = new HttpPost(uri);

			if (headers != null) {
				for (Map.Entry<String, String> h : headers.entrySet())
					post.addHeader(h.getKey(), h.getValue());
			}

			if (params != null) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> p : params.entrySet())
					pairs.add(new BasicNameValuePair(p.getKey(), p.getValue()));

				post.setEntity(new UrlEncodedFormEntity(pairs));
			}

			HttpResponse response = client.execute(post);
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException(uri + " returned code " + statusCode);

			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} 
		catch (IOException e) {
			log.debug("Error executing HTTP POST request to " + uri, e);
		}
		catch (ParseException e) {
			log.debug("Error executing HTTP POST request to " + uri, e);
		}
		return null;
	}

	public static Map<String, String> getBasicAuthenticationHeader(String username, String password) {
		byte[] encodedBytes = Base64.encodeBase64((username + ":" + password).getBytes());
		String encoded = new String(encodedBytes);
		Map<String, String> header = new HashMap<String, String>(1);
		header.put("Authorization", "Basic " + encoded);
		return header;
	}
}