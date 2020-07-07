package com.ggeit.pay.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggeit.pay.config.WXPayConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WXPayUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(WXPayUtil.class);

	/**
	 * 微信map转json
	 * 
	 * @param data
	 * @return
	 */
	public static String wxMapToJson(Map<String, Object> data) {
		ObjectMapper mapper = new ObjectMapper();
		String mjson = null;
		try {
			mjson = mapper.writeValueAsString(data);
			logger.info("mjson = " + mjson);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mjson;
	}

	/**
	 * 微信json转map
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, Object> wxJsonToMapObj(String json) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = mapper.readValue(json, Map.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 微信签名
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public static String wxgenerateSignature(Map<String, String> data, String key) throws Exception {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if (k.equals(WXPayConstants.FIELD_SIGN)) {
				continue;
			}
			// if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
			// sb.append(k).append("=").append(data.get(k).trim()).append("&");
			sb.append(k).append("=").append(data.get(k).trim()).append("&");
		}
		sb.append("key=").append(key);
		logger.info("sb = " + sb.toString());
		return wxMD5(sb.toString()).toUpperCase();
	}

	/**
	 * 生成 MD5
	 *
	 * @param data
	 *            待处理数据
	 * @return MD5结果
	 */
	public static String wxMD5(String data) throws Exception {
		java.security.MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 获取订单号
	 * 
	 * @return
	 */
	public static String wxcreateOrderID() {
		return wxgetTime() + wxgetRandom(6);
	}

	/**
	 * 获取时间
	 * 
	 * @param
	 */
	public static String wxgetTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(System.currentTimeMillis());
	}

	/**
	 * 格式化时间
	 * @param format
	 * @return
	 */
	public static String getDateTime(String format) {
		Date now = new Date();
		SimpleDateFormat sd = new SimpleDateFormat(format);
		return sd.format(now);
	}
	/**
	 * 格式化前一天
	 * @param format
	 * @return
	 */
	public static String getBeforeDateTime(String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1); // 得到前一天
		Date date = calendar.getTime();
		SimpleDateFormat sd = new SimpleDateFormat(format);
		return sd.format(date);
	}
	/**
	 * 改变时间字符串的格式
	 * @param strTime
	 * @param srcformat
	 * @param desformat
	 * @return
	 */
	public static String formatStringTime(String strTime,String srcformat,String desformat) throws ParseException {
		String now = "";

		Date date = new SimpleDateFormat(srcformat).parse(strTime);
		now = new SimpleDateFormat(desformat).format(date);

		return now;
	}

	/**
	 * 获取随机数
	 * 
	 * @return
	 */
	public static String wxgetRandom(int len) {
		StringBuffer flag = new StringBuffer();
		String sources = "0123456789";
		Random rand = new Random();
		for (int j = 0; j < len; j++) {
			flag.append(sources.charAt(rand.nextInt(9)) + "");
		}
		return flag.toString();
	}

	/**
	 * 获取随机数带字母
	 * 
	 * @param len
	 * @return
	 */
	public static String wxgetRandomPlus(int len) {
		StringBuffer flag = new StringBuffer();
		String sources = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random rand = new Random();
		for (int j = 0; j < len; j++) {
			flag.append(sources.charAt(rand.nextInt(62)) + "");
		}
		return flag.toString();
	}

	/*
	 * 金额元转分
	 */
	public static String wxchangeY2F(String amount) {
		return BigDecimal.valueOf(new Double(amount).doubleValue()).multiply(new BigDecimal(100)).toString();
	}
	
	/**
	 * 元转分 
	 * 1.23-->123
	 * 银联商务微信支付中，"分"为无小数点类型
	 * @param amount
	 * @return
	 */
	public static String changeY2F(String amount) {
		BigDecimal price = BigDecimal.valueOf(new Double(amount)).multiply(new BigDecimal(100));
		DecimalFormat df2 =new DecimalFormat("#");  //定义分格式
	    String str2 =df2.format(price); 
    return str2;
	}
	
	/**
	 * 金额分转元
	 */
	/**
	 * 将字符串"分"转换成"元"（长格式），如：100分被转换为1.00元。
	 * @param s
	 * @return
	 */
	public static String convertCent2Dollar(String s) {
	    if("".equals(s) || s ==null){
	        return "";
	    }
	    long l;
	    if(s.length() != 0) {
	        if(s.charAt(0) == '+') {
	            s = s.substring(1);
	        }
	        l = Long.parseLong(s);
	    } else {
	        return "";
	    }
	    boolean negative = false;
	    if(l < 0) {
	        negative = true;
	        l = Math.abs(l);
	    }
	    s = Long.toString(l);
	    if(s.length() == 1) {
	        return (negative ? ("-0.0" + s) : ("0.0" + s));
	    }
	    if(s.length() == 2) {
	        return (negative ? ("-0." + s) : ("0." + s));
	    }else {
	        return (negative ? ("-" + s.substring(0, s.length() - 2) + "." + s
	                .substring(s.length() - 2)) : (s.substring(0,
	                s.length() - 2)
	                + "." + s.substring(s.length() - 2)));
	    }
	}

	/**
	 * 将字符串"分"转换成"元"（短格式），如：100分被转换为1元。
	 * @param s
	 * @return
	 */
	public static String wxchangeF2Y(String s) {
	    String ss = convertCent2Dollar(s);
	    ss = "" + Double.parseDouble(ss);
	    if(ss.endsWith(".0")) {
	        return ss.substring(0, ss.length() - 2);
	    }
	    if(ss.endsWith(".00")) {
	        return ss.substring(0, ss.length() - 3);
	    }else {
	        return ss;
	    }
	}

	public static void main(String[] args) {
		String string  = changeY2F("0.08");
		System.out.println(string);
		String str2 = wxchangeY2F("0.08");
		System.out.println(str2);
	}

}
