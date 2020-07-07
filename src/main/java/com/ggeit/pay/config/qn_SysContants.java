package com.ggeit.pay.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import com.ggeit.pay.utils.JsonUtils;


public class qn_SysContants {
	
//	public static String req_url = "http://www.ggzzrj.cn:8080/PAY_Web/YhybGateway.do";
//	//public static String req_url_querystatus = "http://279352z30c.zicp.vip:39286/WebServiceEntry";
//	public static final String WXMD5KEY = "ErThh9yIyTjVQKAT14mMZExI7NIqzNYx";
//	public static final String FIELD_SIGN = "sign";
	
	public static Map<String, Object> yhConstantsmap = new HashMap<String, Object>();
	
	static {
        ClassPathResource classPathResource = new ClassPathResource("config/qncfpay.json");
		try {
			String alipayData =  IOUtils.toString(classPathResource.getInputStream(), Charset.forName("UTF-8"));
			yhConstantsmap = JsonUtils.JsonToMapObj(alipayData);	//json转map
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, String> transtypemap = new HashMap<String, String>();
		
		static {
			transtypemap.put("MZGH", "门诊挂号");
			transtypemap.put("MZJF", "门诊缴费");
			transtypemap.put("MZYY", "门诊预约");
			transtypemap.put("ZYCZ", "住院预交");
		}
		
	public static HashMap<String, String> BillMethodmap = new HashMap<String, String>();
			
			static {
				BillMethodmap.put("3", "微信支付");
				BillMethodmap.put("5", "支付宝支付");
			}

}
