package com.ggeit.pay.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ggeit.pay.communication.ALIPayRequest;
import com.ggeit.pay.inf.AliPayServiceInf;
import com.ggeit.pay.utils.AliPayUtil;

@Service
public class AliPayServiceImpl implements AliPayServiceInf {

	private final static Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);

	// @Value("${para.serverip}")
	// private String serverip;
	// @Value("${para.serverport}")
	// private Integer serverport;
	@Value("${para.aliappid}")
	private String appid;

	@Autowired
	private ALIPayRequest alirequest;

	@Override
	public String Authorize(String PatientId,String transType,String IdCard) throws Exception {
		// TODO Auto-generated method stub

		StringBuffer url = new StringBuffer();
		// url.append("https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm?");//沙箱环境
		url.append("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?");
		url.append("app_id=" + this.appid);
		url.append("&scope=auth_base");
		url.append("&redirect_uri=" + "http://www.ggzzrj.cn/" + "aggpay/lay/zf_alicallback?");
		url.append("PatientId=" + PatientId + "&transType=" + transType + "&IdCard=" + IdCard);
		return url.toString();
	}

	@Override
	public String GetUserid(String code) throws Exception {
		// TODO Auto-generated method stub
		Map params = new HashMap();
		params.put("auth_code", code);
		params.put("appid", this.appid);
		params.put("grant_type", "authorization_code");
		String result = alirequest.RequestAliPayOauthToken(params);

		logger.info("GetUserid result = " + result);

		return result;
	}

	@Override
	public AlipayTradeCreateResponse UnifiedOrder(String amount, String userid) throws Exception {
		// TODO Auto-generated method stub
		AlipayTradeCreateRequest req = new AlipayTradeCreateRequest();
		// SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		// JSONObject json=new JSONObject();
		// json.put("out_trade_no", "999"+sdf.format(new Date()));
		// json.put("total_amount", amount);
		// json.put("subject", "THESUBJECT");//订单标题
		// json.put("buyer_id",
		// userid);//买家的支付宝唯一用户号（2088开头的16位纯数字）,和buyer_logon_id不能同时为空
		// String jsonStr=json.toString();

		Map<String, Object> requestmap = new HashMap<String, Object>();
		requestmap.put("out_trade_no", AliPayUtil.alicreateOrderID());
		requestmap.put("total_amount", amount);
		requestmap.put("subject", "THESUBJECT");//订单标题
		requestmap.put("buyer_id", userid);//买家的支付宝唯一用户号（2088开头的16位纯数字）,和buyer_logon_id不能同时为空
		
		String jsonStr = AliPayUtil.aliMapToJson(requestmap);
		req.setNotifyUrl("http://www.ggzzrj.cn/aggpay/lay/ali_notify");
		req.setBizContent(jsonStr);
		AlipayTradeCreateResponse response = alirequest.RequestAliPayUnifiedOrder(req, this.appid);
		return response;
	}

	@Override
	public AlipayTradeRefundResponse Ali_Refund(String pay_orderid, String trade_no, String amount) throws Exception {
		// TODO Auto-generated method stub

		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		Map<String, Object> requestmap = new HashMap<String, Object>();
		requestmap.put("out_trade_no", pay_orderid);
		requestmap.put("trade_no", trade_no);
		requestmap.put("refund_amount", amount);
		
		
		String jsonStr = AliPayUtil.aliMapToJson(requestmap);
		request.setBizContent(jsonStr);
		AlipayTradeRefundResponse response = alirequest.RequestAliPayRefund(request, this.appid);
		logger.info("Ali_Refund response = " + response);
		return response;
	}

}
