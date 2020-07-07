package com.ggeit.pay.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ggeit.pay.config.GGPayConstants;
import com.ggeit.pay.config.WXPayConstants;
import com.ggeit.pay.impl.GGPayServiceImpl;
import com.ggeit.pay.utils.GGPayUtil;
import com.ggeit.pay.utils.WXPayUtil;

/**
 * 测试国光支付平台Http接口,报文内容与WebService一致
 * @author Administrator
 *
 */
public class TestGGPayHttp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//统一支付平台微信下单
		GGPayServiceImpl ggpayimpl = new GGPayServiceImpl();
		Map<String, Object> resp = null ;
        try {
			resp = ggpayimpl.WxUnifiedOrder("0.01", "123321");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("resp = " + resp);
		
	}

}
