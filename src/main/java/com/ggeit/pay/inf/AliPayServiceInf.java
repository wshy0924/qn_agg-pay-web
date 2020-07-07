package com.ggeit.pay.inf;

import java.util.Map;

import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;

public interface AliPayServiceInf {
	
	//String Authorize(String price,String name ,String cfsb) throws Exception;//oauth2认证
	
	String GetUserid(String code) throws Exception;//获取userid
	
	AlipayTradeCreateResponse UnifiedOrder(String amount,String userid) throws Exception;//统一支付下单

	//String Authorize(String price, String name, String cfsb, String yllb) throws Exception;

	//String Authorize(String price, String name, String cfsb, String yllb, String brid) throws Exception;
	
	AlipayTradeRefundResponse Ali_Refund(String out_trade_no ,String trade_no ,String refund_amount)throws Exception;


	String Authorize(String PatientId, String transType, String BillNo) throws Exception;
}
