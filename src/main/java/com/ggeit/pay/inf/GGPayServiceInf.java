package com.ggeit.pay.inf;

import java.util.Map;

import com.alipay.api.response.AlipayTradeCreateResponse;

public interface GGPayServiceInf {
	
	Map<String, Object> WxUnifiedOrder(String amount,String openid) throws Exception;//统一支付下单
	
	AlipayTradeCreateResponse AliUnifiedOrder(String amount,String userid) throws Exception;//统一支付下单
}
