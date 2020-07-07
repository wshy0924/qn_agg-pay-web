package com.ggeit.pay.utils;
import java.nio.charset.Charset;

import java.util.HashMap;

import java.util.Map;

import java.util.Map.Entry;

public class XmlEncodeUtils {
	 static Map<String,String> input = new HashMap<String, String>();//XML报文头
	 Map<String,String> dataset = new HashMap<String, String>();//XML报文数据
	 static Map<String,String> row = new HashMap<String, String>();//XML报文数据
 
	public static String setinputParameter(String key,String value){

		if(input == null){
			input = new HashMap<String, String>();
		}
		return input.put(key, value);
	}

	/**往XML模型添加报文头
	 * @param key 数据名
	 * @param value 数据值
	 * @return
	 */
	public static String setdatasetParameter(String key,String value){
		if(input == null){
			input = new HashMap<String, String>();
		}
		return input.put(key, value);
	}

	/**往XML模型添加报文数据
	 * @param key 数据名
	 * @param value 数据值
	 * @return
	 */
	public static String setrowParameter(String key,String value){
	if(row == null){
			row = new HashMap<String, String>();
		}
	return row.put(key, value);
	}

	public static String getinputParameter(String key){
		return input!=null?input.get(key):null;
	}
	
	public String getdatasetParameter(String key){
		return dataset!=null?dataset.get(key):null;
	}
	
	public String getrowParameter(String key){
		return row!=null?row.get(key):null;
	}

	/**  产生模型对应的XML数据
	 * @param charset 编码
	 * @return
	 */

	public static String toSendData(Charset charset){
		StringBuilder builder = new StringBuilder();
		//builder.append("<?xml version=\"1.0\" encoding=\"").append(charset.displayName()).append("\"?>");
		builder.append("<input>");
		if(input!=null){
			for(Entry<String, String> keyVal:input.entrySet()){
				if(keyVal!=null){
					builder.append("<").append(keyVal.getKey()).append(">");
					builder.append(keyVal.getValue()!=null?keyVal.getValue():"");
					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}
		builder.append("<dataset>");
		builder.append("<row>");

		if(row!=null){

			for(Entry<String, String> keyVal:row.entrySet()){

				if(keyVal!=null){

					builder.append("<").append(keyVal.getKey()).append(">");

					builder.append(keyVal.getValue()!=null?keyVal.getValue():"");

					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}

		builder.append("</row>");
		builder.append("</dataset>");
		builder.append("</input>");

		return builder.toString();

	}
	

//	public static void main(String[] args) {
//
//		XmlEncodeUtils model = new XmlEncodeUtils();
//
//		model.setinputParameter("HeadName1", "value1");
//
//		model.setinputParameter("HeadName2", "value2");
//
//		model.setrowParameter("HeadName3", "value3");
//
//		model.setrowParameter("RootName1", "RootValue1");
//
//		String xmlString = model.toSendData(Charset.forName("GBK"));
//
//		System.out.println(xmlString);
//
//	}


}

