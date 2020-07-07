package com.ggeit.pay.communication;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ggeit.pay.config.ALIPayConstants;

@Service
public class ALIPayRequest {

	private final static Logger logger = LoggerFactory.getLogger(ALIPayRequest.class);

	public String RequestAliPayOauthToken(Map<String, String> params) {
		String strResult = "";
		AlipayClient alipayClient = new DefaultAlipayClient(ALIPayConstants.URL, params.get("appid"),
				ALIPayConstants.APP_PRIVATE_KEY, ALIPayConstants.FORMAT, ALIPayConstants.CHARSET,
				ALIPayConstants.ALIPAY_PUBLIC_KEY, ALIPayConstants.SIGNTYPE);
		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
		request.setCode(params.get("auth_code"));
		request.setGrantType(params.get("grant_type"));
		try {
			AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
			// System.out.println(oauthTokenResponse.getBody());
			strResult = oauthTokenResponse.getUserId();// userid同时是buyer_id
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
		}
		return strResult;
	}

	public AlipayTradeCreateResponse RequestAliPayUnifiedOrder(AlipayTradeCreateRequest req, String appid) {
		AlipayTradeCreateResponse resp = null;
		AlipayClient alipayClient = new DefaultAlipayClient(ALIPayConstants.URL, appid, ALIPayConstants.APP_PRIVATE_KEY,
				ALIPayConstants.FORMAT, ALIPayConstants.CHARSET, ALIPayConstants.ALIPAY_PUBLIC_KEY,
				ALIPayConstants.SIGNTYPE);
		try {
			resp = alipayClient.execute(req);
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
	}
	
	public AlipayTradeRefundResponse RequestAliPayRefund(AlipayTradeRefundRequest req, String appid) {
		
		AlipayTradeRefundResponse  resp = null;
		AlipayClient alipayClient = new DefaultAlipayClient(ALIPayConstants.URL, appid, ALIPayConstants.APP_PRIVATE_KEY,
				ALIPayConstants.FORMAT, ALIPayConstants.CHARSET, ALIPayConstants.ALIPAY_PUBLIC_KEY,
				ALIPayConstants.SIGNTYPE);
		try {
			resp = alipayClient.execute(req);
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
		
	}

}
