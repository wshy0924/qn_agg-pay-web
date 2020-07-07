package com.ggeit.pay.utils;

import com.ggeit.pay.config.qn_SysContants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.*;

public class HisMD5Utils {
	private final static Logger logger = LoggerFactory.getLogger(HisMD5Utils.class);
	
	/**
	 * 生成待签名数据
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	   public static String builderSignStr(Map<String, Object> params,String md5key) throws Exception {
	        Set<String> keySet = params.keySet();
	        List<String>keyList = new ArrayList<String>(keySet);
	        Collections.sort(keyList);
	        StringBuilder sb = new StringBuilder();
	        for (String key : keyList) {
	            sb.append(key);
	            sb.append("=");
	            sb.append(params.get(key));
	            sb.append("&");
	        }
	        sb.deleteCharAt(sb.length() - 1); //去掉最后一个&
	        sb.append(md5key);
	        logger.info("builderSignStr= " + sb.toString());
	        logger.info("验证sign:" + MD5(sb.toString()).toUpperCase());
	        return MD5(sb.toString()).toUpperCase();
	    }
	   
	   public static String builderSignStr2(Map<String, Object> params,String md5key) throws Exception {
	      
		 String paramsStr = JsonUtils.MapToJson(params);
		   String paramstr = StringUtils.deleteWhitespace(paramsStr);
	        StringBuffer sb = new StringBuffer();
	        sb.append(paramstr);
	        sb.append(md5key);
	        logger.info("builderSignStr2 = " + sb.toString());
	        logger.info("验证sign:" + MD5(sb.toString()).toUpperCase());
	        return MD5(sb.toString()).toUpperCase();
	    }
	   
		/**
		 * 微信签名
		 * 
		 * @param data
		 * @param key
		 * @return
		 */
		public static String generateSignature(Map<String, String> data, String key) throws Exception {
			Set<String> keySet = data.keySet();
			String[] keyArray = keySet.toArray(new String[keySet.size()]);
			Arrays.sort(keyArray);
			StringBuilder sb = new StringBuilder();
			for (String k : keyArray) {
				if (k.equals(qn_SysContants.yhConstantsmap.get("FIELD_SIGN"))) {
					continue;
				}
				// if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
				// sb.append(k).append("=").append(data.get(k).trim()).append("&");
				sb.append(k).append("=").append(data.get(k).trim()).append("&");
			}
			sb.append("key=").append(key);
			logger.info("sb = " + sb.toString());
			return MD5(sb.toString()).toUpperCase();
		}
		
		/**
		 * 生成 MD5
		 *
		 * @param data
		 *            待处理数据
		 * @return MD5结果
		 */
		public static String MD5(String data) throws Exception {
			java.security.MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(data.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (byte item : array) {
				sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString().toUpperCase();
		}
}
