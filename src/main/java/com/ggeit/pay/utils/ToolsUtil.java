package com.ggeit.pay.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggeit.pay.config.qn_SysContants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 常用工具整理
 * @author wshy
 * @data 2020/7/7
 **/
public class ToolsUtil {

    private final static Logger logger = LoggerFactory.getLogger(ToolsUtil.class);

    /**
     * map 转 json
     * @param data
     * @return
     */
    public static String MapToJson(Map <String, Object> data) {
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
     * json 转 map
     * @param json
     * @return
     */
    public static Map<String, Object> JsonToMapObj(String json) {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Map<String, Object> map = new HashMap <String, Object> ();
        try {
            map = mapper.readValue(json, Map.class);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 生成订单号
     * 20位订单创建编号日期（具体到秒）+6位随机数字
     */
    public static String createOrderID() {
        return getTime() + getRandom(6);
    }

    /**
     * 生成yyyyMMddHHmmss格式时间
     * @return
     */
    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(System.currentTimeMillis());//当前时间
    }

    /**
     * 生成YYYYMMDD格式时间
     * @return
     */
    public static  String getNowTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(System.currentTimeMillis());//当前时间
    }

    /**
     * 时分秒
     * @return
     */
    public static String sfmTime() {
        SimpleDateFormat df = new SimpleDateFormat("HHmmss");
        return df.format(System.currentTimeMillis());//当前时间
    }

    /**
     * 格式化时间
     *
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
     *
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
     * @param len 位数
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

    /**
     * 获取随机数带字母
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

    /**
     * HEX加密
     * @param str
     * @param charsetName
     * @return
     * @throws UnsupportedEncodingException
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
     * HEX解码
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

    /**
     * ND5签名
     * get请求格式数据MD5签名
     * @param params
     * @param md5key
     * @return
     * @throws Exception
     */
    public static String buildergetStrSign(Map<String, Object> params,String md5key) throws Exception {
        Set <String> keySet = params.keySet();
        List <String> keyList = new ArrayList<String>(keySet);
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
        logger.info("buildergetStrSign= " + sb.toString());
        logger.info("验证sign:" + MD5(sb.toString()).toUpperCase());
        return MD5(sb.toString()).toUpperCase();
    }

    /**
     * MD5签名
     * json格式数据签名{...}md5key
     * @param params
     * @param md5key
     * @return
     * @throws Exception
     */
    public static String builderPostStrSign(Map<String, Object> params,String md5key) throws Exception {

        String paramsStr = ToolsUtil.MapToJson(params);
        String paramstr = StringUtils.deleteWhitespace(paramsStr);
        StringBuffer sb = new StringBuffer();
        sb.append(paramstr);
        sb.append(md5key);
        logger.info("builderPostStrSign = " + sb.toString());
        logger.info("验证sign:" + MD5(sb.toString()).toUpperCase());
        return MD5(sb.toString()).toUpperCase();
    }

    /**
     * 生成 MD5
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

    /**
     * 微信签名
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
     * XML格式字符串转换为Map
     *
     * @param xml XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String xml) {
        try {
            Map<String, String> data = new HashMap<>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream (xml.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            stream.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param map Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, Object> map,String tagName) throws Exception {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document document = documentBuilder.newDocument();
            org.w3c.dom.Element root = document.createElement(tagName);
            document.appendChild(root);
            for (String key: map.keySet()) {
                String value =  map.get(key).toString();
                if (value == null) {
                    value = "";
                }
                value = value.trim();
                org.w3c.dom.Element filed = document.createElement(key);
                filed.appendChild(document.createTextNode(value));
                root.appendChild(filed);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
            writer.close();
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 金额元转分
     * @param amount
     * @return
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

}
