package com.ggeit.pay.communication;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ggeit.pay.utils.JsonUtils;

@Service
public class HisRequest {

	private final static Logger logger = LoggerFactory.getLogger(HisRequest.class);

	public Map<String, Object> dopost(String url, String data) {

		HttpClient client;
		Map<String, Object> strResult = new HashMap<String, Object>();//his返回的map
		PostMethod post = new PostMethod(url.toString());
		try {
			logger.info("YH_HisServiceRequest dopost data = " + data);
			logger.info("YH_HisServiceRequest dopost url = " + url);
			// HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
			
			SimpleHttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager(true);
			HttpConnectionManagerParams params = httpConnectionManager.getParams();
			// 连接超时时间
			params.setConnectionTimeout(5000); 
			// 读取超时时间
			params.setSoTimeout(50000);
			// 每个主机的最大连接数（很重要）
			params.setDefaultMaxConnectionsPerHost(1000); 
			// 最大总连接数（很重要）
			params.setMaxTotalConnections(1000);
			// 设置编码
			client = new HttpClient(httpConnectionManager);
			client.getParams().setContentCharset("utf-8");
			client.getParams().setHttpElementCharset("utf-8");
			// post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
			RequestEntity requestEntity = new StringRequestEntity(data, "application/json", "utf-8");
			post.setRequestEntity(requestEntity);
			post.getParams().setContentCharset("utf-8");
			
			/**HTTP response*/
			// 由客户端执行(发送)Post请求
			client.executeMethod(post);
			if (post.getStatusCode() == 200) {
				//接收his响应信息
				String hisResult = post.getResponseBodyAsString() + "";
				logger.info("hisResult = " + hisResult);
				
				//string转json,json转map
			    //JSONObject jsonObject = JSONObject.parseObject(hisResult);
				
			    strResult = JsonUtils.JsonToMapObj(hisResult);
			    strResult.put("statuscode",post.getStatusCode() + "");
			    
			} else {

				strResult.put("statuscode", post.getStatusCode() + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("e = " + e.getMessage());
			strResult.put("statuscode", "999");
		} finally {
			post.releaseConnection();//关闭连接
		}
		return strResult;

	}

}
