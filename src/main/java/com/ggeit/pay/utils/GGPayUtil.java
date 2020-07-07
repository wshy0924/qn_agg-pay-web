package com.ggeit.pay.utils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ggeit.pay.config.GGPayConstants;


@Service
public class GGPayUtil {

	private final static Logger logger = LoggerFactory.getLogger(GGPayUtil.class);

	/**
	 * 支付平台计算签名
	 * @param data --使用LinkedHashMap
	 * @param key --GGPayConstants.MD5KEY
	 * @return
	 * @throws Exception
	 */
	public static String geitgenerateSignature(Map<String, String> data, String key) throws Exception {
		logger.info("data = " + data);
		String databuff = "";
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		// Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String k : keyArray) {
			sb.append("\"").append(k).append("\"").append(":").append("\"").append(((String) data.get(k)).trim())
					.append("\"").append(",");
		}
		databuff = sb.toString();
		databuff = databuff.substring(0, databuff.length() - 1);
		databuff = databuff + "}&key:" + key;
		logger.info("databuff = " + databuff);
		return ggMD5(databuff).toUpperCase();
	}
	
	/**
	 * 生成 MD5
	 *
	 * @param data
	 *            待处理数据
	 * @return MD5结果
	 */
	public static String ggMD5(String data) throws Exception {
		java.security.MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

}
