package com.ggeit.pay.utils;

import java.io.UnsupportedEncodingException;

public class jiami { 
	
	public static String str2HexStr2(String str, String charsetName) throws UnsupportedEncodingException {
    byte[] bs = str.getBytes(charsetName);
    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 0, bit; i < bs.length; i++) {
        bit = (bs[i] & 0x0f0) >> 4;
        sb.append(chars[bit]);
        bit = bs[i] & 0x0f;
        sb.append(chars[bit]);
    }
    return sb.toString();
}

/**
 * 解码
 * 将16进制字符串转换为原始字符串
 */
public static String hexStr2Str(String hexStr, String charsetName) throws UnsupportedEncodingException {
    if (hexStr.length() < 1) return null;
    byte[] hexbytes = new byte[hexStr.length() / 2];
    for (int i = 0; i < hexStr.length() / 2; i++) {
        int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
        int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
        hexbytes[i] = (byte) (high * 16 + low);
    }
    return new String(hexbytes, charsetName);
}

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
	
			String encodename = jiami.str2HexStr2("字符串", "UTF-8");
			System.out.println("encodename= " + encodename);
			
			String decodename = jiami.hexStr2Str(encodename, "UTF-8");
			System.out.println("decodename= " + decodename);
	}

}
