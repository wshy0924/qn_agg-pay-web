package com.ggeit.pay.communication;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;



@Service
public class WXPayRequest {
	
	private final static Logger logger = LoggerFactory.getLogger(WXPayRequest.class);

	/**
	 * http请求,与微信网关通信
	 * @param url
	 * @param data
	 * @return
	 */
	public Map <String, Object> request(String url, String data) {
		HttpClient client;
		Map<String, Object> strResult = new HashMap <String, Object> ();
		PostMethod post = new PostMethod(url.toString());
		try {
			logger.info("WxPayRequest dopost data = " + data);

			//HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
			SimpleHttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager(true);
			HttpConnectionManagerParams params = httpConnectionManager.getParams();
			params.setConnectionTimeout(5000);
			params.setSoTimeout(20000);
			params.setDefaultMaxConnectionsPerHost(1000);
			params.setMaxTotalConnections(1000);

			client = new HttpClient(httpConnectionManager);
			client.getParams().setContentCharset("utf-8");
			client.getParams().setHttpElementCharset("utf-8");

			RequestEntity requestEntity = new StringRequestEntity (data, "application/json", "utf-8");
			post.setRequestEntity(requestEntity);
			post.getParams().setContentCharset("utf-8");

			client.executeMethod(post);
			if (post.getStatusCode() == 200) {
				strResult.put("statuscode", post.getStatusCode() + "");
				strResult.put("conResult", post.getResponseBodyAsString() + "");
			} else {
				strResult.put("statuscode", post.getStatusCode() + "");
			}
			logger.info("WxPayRequest strResult = " + strResult);
		} catch (Exception e) {
			strResult.put("statuscode", "999");
		} finally {
			post.releaseConnection();
		}
		return strResult;
	}

	
	/**
	 * http get 获取openid
	 * @param url
	 * @param params
	 * @return
	 */
	public String httpRequestToString(String url, Map<String, String> params) {
		String result = null;
		try {
			InputStream is = httpRequestToStream(url,  params);
			BufferedReader in = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			result = buffer.toString();
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
	private static InputStream httpRequestToStream(String url, Map<String, String> params) {
		InputStream is = null;
		try {
			String parameters = "";
			boolean hasParams = false;
			for (String key : params.keySet()) {
				String value = URLEncoder.encode(params.get(key), "UTF-8");
				parameters += key + "=" + value + "&";
				hasParams = true;
			}
			if (hasParams) {
				parameters = parameters.substring(0, parameters.length() - 1);
			}
			url += "?" + parameters;
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			conn.setDoInput(true);
			// 设置请求方式，默认为GET
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

}
