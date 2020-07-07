package com.ggeit.pay.controller;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.ggeit.pay.config.ALIPayConstants;
import com.ggeit.pay.impl.*;
import com.ggeit.pay.utils.GGitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.*;
import java.util.Map.Entry;


@Controller
@RequestMapping("/lay")

// 支持返回页面只能使用@Controller、不能使用@RestController
public class ZF_LayerController {

	private final static Logger logger = LoggerFactory.getLogger(ZF_LayerController.class);

	@Autowired
	private WxPayServiceImpl wxpayimpl;
	
	@Autowired
	private AliPayServiceImpl alipayimpl;

	@Autowired
	private Qn_zfHisServiceImpl qn_zfhisserviceimpl;
	
	@Autowired
	private OrderService orderservice;

	/**
	 * http://localhost:8080/aggpay/lay/qncfpay?PatientId=00001&BillNo=112231
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/qncfpay", method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response, @RequestParam String PatientName, @RequestParam String PatientId,
						@RequestParam String BillNo, Map<String, Object> map) throws Exception {
		logger.info("---------------------接收扫码信息-------------------");
		logger.info("PatientId : " + PatientId);
		logger.info("BillNo : " + BillNo);
		logger.info("PatientName : " + PatientName);
		//判断扫描二维码的app
		logger.info("----------layer index----------");
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		logger.info("index userAgent= " + userAgent);
		
		//userAgent = "|alipayclient";
		if (userAgent.indexOf("micromessenger") > 0) {
			logger.info("----------微信支付----------");
			String url = wxpayimpl.Authorize(PatientId,BillNo,PatientName);
			logger.info("url = " + url);
			return "redirect:" + url;
		} else if (userAgent.indexOf("alipayclient") > 0) {
			logger.info("----------阿里支付----------");
//			String url = alipayimpl.Authorize(IdCard,transType,PatientId);
//			logger.info("url = " + url);
//
//			return "redirect:" + url;
			map.put("tips", "当前处方不支持支付宝支付！");
			return "wc_failure";// 用了html不能使用反斜杠如"/tips"
		} else {
			map.put("tips", "未匹配到APP类型");
			return "wc_failure";// 用了html不能使用反斜杠如"/tips"
		}
	}

	@RequestMapping(value = "/zf_wxcallback", method = RequestMethod.GET)
	public String doWxCallBack(HttpServletRequest request, HttpServletResponse response,String PatientId,String PatientName,
			String BillNo, Map<String, Object> map) throws Exception {
			logger.info("----------layer wxcallback----------");
			//------------网页授权认证回调获取code---------------------
			String code = request.getParameter("code");
	//		String state = request.getParameter("state");
	//		String remark = WXPayConstants.remarkmap.get(state);
			logger.info("wxcallback code = " + code);
			String openid = wxpayimpl.GetOpenid(code);				//获取用户唯一标识openid
			logger.info("wxcallback openid= " + openid);
			logger.info("wxcallback PatientId = " + PatientId);
			logger.info("wxcallback BillNo = " + BillNo);
			logger.info("wxcallback PatientName = " + PatientName);

			//-------------------step01 查询代缴费信息--------------------------------
			logger.info("------------------step01 查询代缴费信息------------------------");
			Map<String,Object> querymap = new HashMap<String,Object>();
			querymap.put("PatientId",PatientId);
			Map<String,Object> queryresultmap = qn_zfhisserviceimpl.zf_Queryinfo(querymap);

			if(!"0000".equals(queryresultmap.get("returnCode"))) {

				map.put("tips", "未获取到缴费明细,请确认单据有效性!");
				return "wc_failure";

			}else {

				Map<String,Object> resultdatamap = (Map<String, Object>) queryresultmap.get("data");
				logger.info("代缴费信息结果 = " + resultdatamap);
				List<Map> ResultSet = (List <Map>) resultdatamap.get ("ResultSet");
				logger.info ("ResultSet = " + ResultSet);
				String FlowNo = "";
				String BillName = "";
				String BillMoney = "";
				String BillTime = "";
				String SetDepartName = "";

				if (!ResultSet.isEmpty ()){

					for (int i = 0; i < ResultSet.size(); i++) {
						HashMap temp = (HashMap) ResultSet.get(i);
						FlowNo = temp.get("FLOWNO").toString();		//处方号
						logger.info("代缴费处方号 = " + FlowNo);

						if(BillNo.equals(FlowNo)) {
							BillName = GGitUtil.str2HexStr2(temp.get("BILLNAME").toString(), "UTF-8");		//处方描述
							BillMoney = temp.get("BILLMONEY").toString();	//处方金额
							BillTime = temp.get("BILLTIME").toString();
							SetDepartName = temp.get("SETDEPARTNAME").toString();

						}
					}
				}else {
					map.put("tips", "未获取到缴费明细,请确认单据有效性!");
					return "wc_failure";
				}
				logger.info("获取指定处方单中BillTime = " + BillTime);
				logger.info("获取指定处方单中BillName = " + BillName);
				logger.info("获取指定处方单中BillMoney = " + BillMoney);
				logger.info("获取指定处方单中SetDepartName = " + SetDepartName);

				//插入到billno表中
				logger.info("------------part01-billno信息录入---------------");
				Map<String,Object> insertMap = new HashMap<String,Object>();
				insertMap.put("BillNo", BillNo);
				insertMap.put("PatientId", PatientId);
				insertMap.put("SetDepartName", SetDepartName);
				insertMap.put("BillMoney", BillMoney);
				insertMap.put("BillTime", BillTime);
				insertMap.put("BillName", BillName);
				insertMap.put("PayMethod", "微信支付");
				insertMap.put("PatientName", PatientName);


				Boolean flag = orderservice.insert("billinfo.insertall", insertMap);
				logger.info("qn_billinfo信息录入状态 ：" + flag);

				//拼接前端显示信息
				map.put("openid",openid);
				map.put("BillNo", BillNo);
				map.put("BillName", BillName);
				map.put("BillMoney", BillMoney);

				return "wc_zfwxpayconfirm";		//返回微信支付页面

			}
		}

}
