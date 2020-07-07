package com.ggeit.pay.inf;

import java.util.Map;

public interface WxPayServiceInf {
	
	//Map<String, Object> UnifiedOrder(String amount,String openid) throws Exception;//统一支付下单
	
	Map<String, Object> GenerateMD5(String prepay_id,String appid,String timeStamp,String randomplus) throws Exception;//从微信网关获取MD5签名

	Map<String, Object> InsideGenerateMD5(String prepay_id,String appid,String timeStamp,String randomplus) throws Exception;//一码付后端生成MD5签名

    String Authorize (String PatientId, String BillNo, String PatientName) throws Exception;

    String GetOpenid(String code) throws Exception;//获取openid

	//String Authorize(String price, String name, String cfsb, String yllb, String brid) throws Exception; //oauth2认证
	
	Map<String, Object> Wx_Refund(String amount,String openid) throws Exception;//微信退款
	
	Map<String, Object> Wx_CycleOrderQuery(String out_trade_no) throws Exception;//微信退款

	Map<String, Object> UnifiedOrder(Map<String, Object> reqmap) throws Exception;//统一支付下单
}
