package com.ggeit.pay.impl;

import com.ggeit.pay.communication.WXPayRequest;
import com.ggeit.pay.config.WXPayConstants;
import com.ggeit.pay.config.qn_SysContants;
import com.ggeit.pay.inf.WxPayServiceInf;
import com.ggeit.pay.utils.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayServiceImpl implements WxPayServiceInf {
	
	private final static Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);
	
//	@Value("${para.serverip}")
//	private String serverip;
//	@Value("${para.serverport}")
//    private Integer serverport;
	@Value("${para.wxappid}")
	private String appid;
	@Value("${para.wxsecret}")
	private String secret;
	
    @Autowired
    private WXPayRequest wxrequest;

	/**
	 * 统一支付下单
	 */
	@Override
	public Map<String, Object> UnifiedOrder(Map<String,Object> reqmap) throws Exception {
		// TODO Auto-generated method stub
//    	Map<String, String> attachmap = new HashMap<String,String>();
//    	attachmap.put("tradeParam", "交易参数");
    	
//		Map<String, Object> tradeparammap = new HashMap<String,Object>();
    	Map<String, String> tradeparammap = new HashMap<String,String>();
    	tradeparammap.put("body", reqmap.get("BillName").toString ());	//处方名称
    	tradeparammap.put("detail", "挂号费2.0元");
    	tradeparammap.put("attach", "{\"router\": \"scan\"}");
    	tradeparammap.put("total_fee", WXPayUtil.changeY2F(reqmap.get("BillMoney").toString()));
    	tradeparammap.put("spbill_create_ip", "192.168.1.200");
    	tradeparammap.put("openid", reqmap.get("openid").toString());
    	tradeparammap.put("out_trade_no",reqmap.get("out_trade_no").toString());
    	tradeparammap.put("trade_type", "JSAPI");
    	tradeparammap.put("limit_pay", "no_credit");
    	tradeparammap.put("product_id", "");
    	//tradeparammap.put("nonce_str", WXPayUtil.wxgetRandomPlus(20));
    	
    	Map<String, Object> requestmap = new HashMap<String,Object>();
    	requestmap.put("tradeType", "UnifiedPay");
    	requestmap.put("tradeParam", tradeparammap);
    	requestmap.put("tradeRemark", "remark");
    	requestmap.put("sign", WXPayUtil.wxgenerateSignature(tradeparammap, WXPayConstants.MD5KEY));
    	
    	Map<String, Object> resp = wxrequest.request(WXPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));
    	logger.info("UnifiedOrder resp = "+resp);
		return resp;

	}

    /**
     *  从支付网关获取MD5签名
     */
	@Override
	public Map<String, Object> GenerateMD5(String prepay_id,String appid,String timeStamp,String randomplus) throws Exception {
		// TODO Auto-generated method stub
		String WC_AppID = (String) qn_SysContants.yhConstantsmap.get("WC_AppID");
		Map<String, String> tradeparammap = new HashMap<String,String>();
		tradeparammap.put("appId", WC_AppID);
		//tradeparammap.put("timeStamp", Long.toString(new Date().getTime()/1000));
		tradeparammap.put("timeStamp", timeStamp);
		//tradeparammap.put("nonceStr", WXPayUtil.wxgetRandomPlus(20));
		tradeparammap.put("nonceStr", randomplus);
		tradeparammap.put("package", "prepay_id="+prepay_id);
		tradeparammap.put("signType", "MD5");
		
		Map<String, Object> requestmap = new HashMap<String,Object>();
    	requestmap.put("tradeType", "MD5Sign");
    	requestmap.put("tradeParam", tradeparammap);
    	requestmap.put("tradeRemark", "");
    	requestmap.put("sign", WXPayUtil.wxgenerateSignature(tradeparammap, WXPayConstants.MD5KEY));
    	Map<String, Object> resp = wxrequest.request(WXPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));
    	logger.info("GenerateMD5 resp = "+resp);
        return resp;
	}

	@Override
	public String Authorize (String PatientId, String BillNo,String PatientName) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer url = new StringBuffer();
		url.append("http://open.weixin.qq.com/connect/oauth2/authorize?");
		url.append("appid="+ qn_SysContants.yhConstantsmap.get("WC_AppID").toString());
		//?后参数需经过加密后进行传值
		url.append("&redirect_uri="+URLEncoder.encode("http://gz.wcrmyy.ggzzrj.cn/")+URLEncoder.encode("aggpay/lay/zf_wxcallback?")
				+URLEncoder.encode("&PatientName="+PatientName)
			+URLEncoder.encode("&BillNo="+BillNo)
			+URLEncoder.encode("&PatientId="+PatientId));
		url.append("&response_type=code&scope=snsapi_base&state=");//state参数是给传值的
	    url.append("#wechat_redirect");
	    return  url.toString();
	}

	@Override
	public String GetOpenid(String code) throws Exception {
		// TODO Auto-generated method stub
		String WC_AppID = (String) qn_SysContants.yhConstantsmap.get("WC_AppID");
		String WC_APP_SECRET = (String) qn_SysContants.yhConstantsmap.get("WC_APP_SECRET");
		logger.info("WC_AppID = " + WC_AppID);
		logger.info("WC_APP_SECRET = " + WC_APP_SECRET);
		
		Map params = new HashMap();
		params.put("secret", WC_APP_SECRET);
		params.put("appid", WC_AppID);
		params.put("grant_type", "authorization_code");
		params.put("code", code);
		String result = wxrequest.httpRequestToString(
				"https://api.weixin.qq.com/sns/oauth2/access_token", params);
		
		logger.info("GetOpenid result = "+result);
		Map<String, Object> map = WXPayUtil.wxJsonToMapObj(result);
		
		logger.info("GetOpenid map = "+map);
		
		return (String) map.get("openid");
	}

	/**
	 * 一码付后台生成MD签名
	 */
	@Override
	public Map<String, Object> InsideGenerateMD5(String prepay_id, String appid, String timeStamp, String randomplus)
			throws Exception {
		// TODO Auto-generated method stub
		String WC_AppID = (String) qn_SysContants.yhConstantsmap.get("WC_AppID");
		logger.info("WC_AppID = " + WC_AppID);

		Map<String, String> tradeparammap = new HashMap<String,String>();
		tradeparammap.put("appId", WC_AppID);
		tradeparammap.put("timeStamp", timeStamp);
		tradeparammap.put("nonceStr", randomplus);
		tradeparammap.put("package", "prepay_id="+prepay_id);
		tradeparammap.put("signType", "MD5");
		

		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, Object> responsedata = new HashMap<String,Object>();
		responsedata.put("returnCode", "0000");
		responsedata.put("returnInfo", "正常");
		responsedata.put("data", WXPayUtil.wxgenerateSignature(tradeparammap, (String) qn_SysContants.yhConstantsmap.get ("WC_MD5_KEY")));

		resp.put("statuscode", "200");
		resp.put("conResult", WXPayUtil.wxMapToJson(responsedata));
		
		logger.info("InsideGenerateMD5 resp = " + resp);
        return resp;
	}

	@Override
	public Map<String, Object> Wx_Refund(String amount,String out_trade_no ) throws Exception {
		// TODO Auto-generated method stub
		String price = WXPayUtil.wxchangeY2F(amount).substring(0, WXPayUtil.wxchangeY2F(amount).indexOf("."));
		Map<String, String> refundparammap = new HashMap<String,String>();
		refundparammap.put("out_trade_no", out_trade_no);
		refundparammap.put("out_refund_no", WXPayUtil.wxcreateOrderID());
		refundparammap.put("total_fee", price);
		refundparammap.put("refund_fee", price);
		
    	
    	Map<String, Object> requestmap = new HashMap<String,Object>();
    	requestmap.put("tradeType", "OrderRefund");
    	requestmap.put("tradeParam", refundparammap);
    	requestmap.put("tradeRemark", "remark");
    	requestmap.put("sign", WXPayUtil.wxgenerateSignature(refundparammap, WXPayConstants.MD5KEY));//对tradeParam进行MD5加密
    	
    	Map<String, Object> resp = wxrequest.request(WXPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));
    	logger.info("Wx_Refund resp = "+resp);
        return resp;
		

	}
	/**
	 * 轮询订单状态
	 */
	@Override
	public Map<String, Object> Wx_CycleOrderQuery(String out_trade_no) throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> Querymap = new HashMap<String,String>();
		Querymap.put("out_trade_no", out_trade_no);
		
		Map<String, Object> requestmap = new HashMap<String,Object>();
		requestmap.put("tradeType", "CycleOrderQuery");
		requestmap.put("tradeParam", Querymap);
		requestmap.put("tradeRemark", "");
		requestmap.put("sign", WXPayUtil.wxgenerateSignature(Querymap, WXPayConstants.MD5KEY));
		
		Map<String, Object> resp = wxrequest.request(WXPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));
    	logger.info("Wx_CycleOrderQuery resp = "+resp);
        return resp;
	}

}
