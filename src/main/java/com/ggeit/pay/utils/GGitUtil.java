package com.ggeit.pay.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.ServiceMode;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GGitUtil {
	/**
	 * 生成订单号
	 * 		20位订单创建编号日期（具体到秒）+6位随机数字
	 */
		public static String createOrderID() {
			return getTime() + getRandom(6);
		}
		/**
		 * 获取当前时间
		 * @return
		 */
		public static String getTime() {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			return df.format(System.currentTimeMillis());//当前时间
		}
		public static  String getNowTime(){
			SimpleDateFormat df = new SimpleDateFormat("YYYYMMDD");
			return df.format(System.currentTimeMillis());//当前时间
		}
		/**
		 * 获取随机数
		 * @param len
		 * @return
		 */
		public static String getRandom(int len) {
			StringBuffer flag = new StringBuffer();
			String sources = "0123456789";
			Random rand = new Random();
			for (int j = 0; j < len; j++) {
				flag.append(sources.charAt(rand.nextInt(9)) + "");
			}
			return flag.toString();
		}
	

		    //******************************************************************************************

		    //                                                                      参数或返回值为字符串

		    //******************************************************************************************
		    /**
		     * 将原始字符串转换成16进制字符串【方法一】
		     */
		    public static String str2HexStr(String input, String charsetName) throws UnsupportedEncodingException {
		        byte buf[] = input.getBytes(charsetName);
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < buf.length; i++) {
		            //以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式。
		            //如果参数为负，那么无符号整数值为参数加上 2^32；否则等于该参数。将该值转换为十六进制（基数 16）的无前导 0 的 ASCII 数字字符串。
		            //如果无符号数的大小值为零，则用一个零字符 '0' (’\u0030’) 表示它；否则，无符号数大小的表示形式中的第一个字符将不是零字符。
		            //用以下字符作为十六进制数字【0123456789abcdef】。这些字符的范围是从【'\u0030' 到 '\u0039'】和从【'\u0061' 到 '\u0066'】。
		            String hex = Integer.toHexString(buf[i] & 0xFF);//其实核心也就这一样代码
		            if (hex.length() == 1) hex = '0' + hex;
		            sb.append(hex.toUpperCase());
		        }
		        return sb.toString();
		    }

		    /**
		     * 将原始字符串转换成16进制字符串【方法二】
		     */
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

		    //******************************************************************************************

		    //                                                                      参数或返回值为字节数组

		    //******************************************************************************************
		    /**
		     * 将二进制转换成16进制
		     */
		    public static String encode(byte buf[]) {
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < buf.length; i++) {
		            String hex = Integer.toHexString(buf[i] & 0xFF);
		            if (hex.length() == 1) hex = '0' + hex;
		            sb.append(hex.toUpperCase());
		        }
		        return sb.toString();
		    }

		    /**
		     * 将16进制转换为二进制(服务端)
		     */
		    public static byte[] deocde(String hexStr) {
		        if (hexStr.length() < 1) return null;
		        byte[] result = new byte[hexStr.length() / 2];
		        for (int i = 0; i < hexStr.length() / 2; i++) {
		            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
		            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
		            result[i] = (byte) (high * 16 + low);
		        }
		        return result;
		    }

		    //******************************************************************************************

		    //                          org.apache.commons.codec.binary.Hex的实现方式

		    //******************************************************************************************

		    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		    public static char[] encodeHex(byte[] data) {
		        int l = data.length;
		        char[] out = new char[l << 1];
		        int i = 0;
		        for (int j = 0; i < l; i++) {
		            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
		            out[(j++)] = DIGITS[(0xF & data[i])];
		        }
		        return out;
		    }

		    public static byte[] decodeHex(char[] data) throws Exception {
		        int len = data.length;
		        if ((len & 0x1) != 0) throw new Exception("Odd number of characters.");
		        byte[] out = new byte[len >> 1];

		        int i = 0;
		        for (int j = 0; j < len; i++) {
		            int f = toDigit(data[j], j) << 4;
		            j++;
		            f |= toDigit(data[j], j);
		            j++;
		            out[i] = ((byte) (f & 0xFF));
		        }
		        return out;
		    }

		    private static int toDigit(char ch, int index) throws Exception {
		        int digit = Character.digit(ch, 16);
		        if (digit == -1) throw new Exception("Illegal hexadecimal charcter " + ch + " at index " + index);
		        return digit;
		    }
		    
			/**
			 * map转json
			 * 
			 * @param data
			 * @return
			 */
			public static String MapToJson(Map<String, Object> data) {
				ObjectMapper mapper = new ObjectMapper();
				String mjson = null;
				try {
					mjson = mapper.writeValueAsString(data);
					//logger.info("mjson = " + mjson);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return mjson;
			}
			
			/**
			 *json转map
			 * 
			 * @param json
			 * @return
			 */
			public static Map<String, Object> JsonToMapObj(String json) {
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

			    * 

			    * String转map

			    * @param str

			    * @return

			    */

			   public static Map<String,Object> getStringToMap(String str){

			       //根据逗号截取字符串数组

			       String[] str1 = str.split(",");

			       //创建Map对象

			       Map<String,Object> map = new HashMap<>();

			       //循环加入map集合

			       for (int i = 0; i < str1.length; i++) {

			           //根据":"截取字符串数组

			           String[] str2 = str1[i].split(":");

			           //str2[0]为KEY,str2[1]为值

			           map.put(str2[0],str2[1]);

			       }

			       return map;

			   }
			
		    /**
		     * 加密
		     * 
		     * @param encryptText 需要加密的信息
		     * @param key 加密密钥
		     * @return 加密后Base64编码的字符串
		     */
		    public static String encrypt(String encryptText, String key) {

		        if (encryptText == null || key == null) {
		            throw new IllegalArgumentException("encryptText or key must not be null");
		        }

		        try {
		            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
		            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

		            Cipher cipher = Cipher.getInstance("DES");
		            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		            byte[] bytes = cipher.doFinal(encryptText.getBytes(Charset.forName("UTF-8")));
		            return Base64.encodeBase64String(bytes);
		           // return bytes.toString();

		        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException
		            | BadPaddingException | IllegalBlockSizeException e) {
		            throw new RuntimeException("encrypt failed", e);
		        }

		    }

		    /**
		     * 解密
		     * 
		     * @param desResult1 需要解密的信息
		     * @param key 解密密钥，经过Base64编码
		     * @return 解密后的字符串
		     */
		    public static  String decrypt(String decryptText, String key) {

		        if (decryptText == null || key == null) {
		            throw new IllegalArgumentException("decryptText or key must not be null");
		        }

		        try {
		            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
		            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
		            System.out.println("加密密钥：" + secretKey);

		            Cipher cipher = Cipher.getInstance("DES");
		            cipher.init(Cipher.DECRYPT_MODE, secretKey);
		            byte[] bytes = cipher.doFinal(Base64.decodeBase64(decryptText));
		            //byte[] bytes = cipher.doFinal(desResult1);
		            return new String(bytes, Charset.forName("UTF-8"));

		        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException
		            | BadPaddingException | IllegalBlockSizeException e) {
		            throw new RuntimeException("decrypt failed", e);
		        }
		    }
		    
		    public static String getnowTime() {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return df.format(System.currentTimeMillis());//当前时间
			}
		    
		    public static String clearTime() {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				return df.format(System.currentTimeMillis());//当前时间
			}
		    
		    public static String sfmTime() {
				SimpleDateFormat df = new SimpleDateFormat("HHmmss");
				return df.format(System.currentTimeMillis());//当前时间
			}

}
