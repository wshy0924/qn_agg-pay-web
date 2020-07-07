package com.ggeit.pay.communication;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GGPayRequest {
	
	private final static Logger logger = LoggerFactory.getLogger(GGPayRequest.class);
	
	/**
	 * 与国光支付平台通信
	 * @param url
	 * @param data
	 * @return
	 */
	public Map<String, Object> request(String url, String data) {
		HttpPost httppost = new HttpPost(url);
		Map<String, Object> strResult = new HashMap<String, Object>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		StringEntity s;
		String statuscode = "";
		
		try {
			s = new StringEntity(data,"utf-8");//确保字符编码正确
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			httppost.setEntity(s);
			httppost.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
			CloseableHttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				statuscode = response.getStatusLine().getStatusCode() + "";
				String conResult = EntityUtils.toString(response.getEntity());
				strResult.put("statuscode", statuscode);
				strResult.put("conResult", conResult);
			} else {
				statuscode = response.getStatusLine().getStatusCode() + "";
				strResult.put("statuscode", statuscode);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			statuscode = "999";
			strResult.put("statuscode", statuscode);
			e.printStackTrace();
		}
		logger.info("GGPayRequest request strResult = " + strResult);
		return strResult;
	}


}
