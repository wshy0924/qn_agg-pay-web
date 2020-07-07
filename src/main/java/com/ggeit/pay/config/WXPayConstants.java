package com.ggeit.pay.config;

import java.util.HashMap;

/**
 * 微信配置
 * @author Administrator
 *
 */
public class WXPayConstants {
	
	//微信支付网关签名密钥
	public static final String MD5KEY = "9rpJyxfDvSY6z3cVhyd4Ku9B4sfWiVbz";
	
	public static final String WXMD5KEY = "tr4NgF2r98EjsA0Y94l0rtSND13y7oxX";

	public static final String FIELD_SIGN = "sign";

	//务川国光微信支付网关
	//public static final String URL = "http://wx.wczzxrmyy.com/wxpro/WxServerGatewayServlet";
	public static final String URL = "http://127.0.0.1:80/wxpro/WxServerGatewayServlet";


	public static HashMap<String, String> remarkmap = new HashMap<String, String>();
	
	static {
		remarkmap.put("1001", "门诊挂号");
		remarkmap.put("1002", "门诊缴费");
	}
	
	
}
