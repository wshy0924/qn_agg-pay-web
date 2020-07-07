package com.ggeit.pay.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.response.AlipayTradeCreateResponse;
import com.ggeit.pay.communication.GGPayRequest;
import com.ggeit.pay.config.GGPayConstants;
import com.ggeit.pay.inf.GGPayServiceInf;
import com.ggeit.pay.utils.GGPayUtil;
import com.ggeit.pay.utils.WXPayUtil;

@Service
public class GGPayServiceImpl implements GGPayServiceInf {

	private final static Logger logger = LoggerFactory.getLogger(GGPayServiceImpl.class);

	//需要Spring环境启动
	//@Autowired
	//private GGPayRequest ggrequest;

	/**
	 * 统一平台微信下单
	 */
	@Override
	public Map<String, Object> WxUnifiedOrder(String amount, String openid) throws Exception {
		// TODO Auto-generated method stub
		logger.info("----------WxUnifiedOrder--------");
		Map<String, String> datamap = new LinkedHashMap<String, String>();
		Map<String, Object> requestmap = new HashMap<String, Object>();
		datamap.put("transCode", "0001");
		datamap.put("indCode", "8410");// 行业代码
		datamap.put("chanelType", "01");// 渠道类型
		datamap.put("merchantId", "000001");// 商户号
		datamap.put("operId", "");// 操作员号
		datamap.put("termId", "0010010001");// 终端号
		datamap.put("depart", "儿科");
		datamap.put("orderType", "8410001");// 订单类型
		datamap.put("payType", "");// 支付类型,JSAPI支付,需要增加一种
		datamap.put("ybFlag", "0");// 1-参与医保，0-不参与医保
		datamap.put("orderAmt", WXPayUtil.wxchangeY2F(amount).substring(0, WXPayUtil.wxchangeY2F(amount).indexOf(".")));// 订单总额
		datamap.put("settleAmt", WXPayUtil.wxchangeY2F(amount).substring(0, WXPayUtil.wxchangeY2F(amount).indexOf(".")));// 付款金额,订单总额-付款金额=医保统筹
		datamap.put("buyerName", "张三");
		datamap.put("buyerTel", "12345678901");
		requestmap.put("data", datamap);
		requestmap.put("sign", GGPayUtil.geitgenerateSignature(datamap, GGPayConstants.MD5KEY));
        logger.info("GG WxUnifiedOrder requestmap = " + WXPayUtil.wxMapToJson(requestmap));
        GGPayRequest ggrequest = new GGPayRequest();
        Map<String, Object> resp = ggrequest.request(GGPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));//需要prepay_id、appid、mch_id等       
        logger.info("GG WxUnifiedOrder resp = " + resp);
		return resp;
	}
	
	/**
	 * 统一平台支付宝下单
	 */
	@Override
	public AlipayTradeCreateResponse AliUnifiedOrder(String amount, String userid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
