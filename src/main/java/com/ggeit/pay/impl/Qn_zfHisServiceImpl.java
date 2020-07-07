package com.ggeit.pay.impl;

import com.ggeit.pay.communication.HisRequest;
import com.ggeit.pay.config.qn_SysContants;
import com.ggeit.pay.inf.qn_zfHisServiceInf;
import com.ggeit.pay.utils.ToolsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Qn_zfHisServiceImpl implements qn_zfHisServiceInf {
	private final static Logger logger = LoggerFactory.getLogger(Qn_zfHisServiceImpl.class);
	@Autowired
	private HisRequest hisrequest;
	/**
	 * 自费查询代缴费信息
	 */
	@Override
	public Map<String, Object> zf_Queryinfo(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> reqmap = new HashMap<String,Object>();	//请求his的map
		Map<String,Object> tradeParamMap = new HashMap <> (); //tradeParam部分map

		reqmap.put ("tradeType","H401");
		tradeParamMap.put ("patientid",map.get ("PatientId"));
		tradeParamMap.put ("orgcode"," ");
		tradeParamMap.put ("errormsg"," ");

		reqmap.put ("tradeParam",tradeParamMap);

		reqmap.put("sign", ToolsUtil.builderPostStrSign (tradeParamMap, qn_SysContants.yhConstantsmap.get("HISMD5KEY").toString()));//算签名sign
		logger.info("查询代缴费信息reqmap = " + reqmap);
		Map<String,Object> respmap = hisrequest.dopost(qn_SysContants.yhConstantsmap.get("HISUrl").toString(), ToolsUtil.MapToJson(reqmap));
		logger.info("his返回代缴费信息：" + respmap);
		
		return respmap;
	}

	/**
	 * 自费结算
	 */
	@Override
	public Map<String, Object> zf_TradePay(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub

		Map<String,Object> reqmap = new HashMap<String,Object>();
		Map<String,Object> tradeparamMap = new HashMap <> ();

		reqmap.put ("tradeType","H402");

		tradeparamMap.put ("flowno",map.get ("BillNo"));
		tradeparamMap.put ("billtype",map.get ("BillType"));	//处方类型，H401返回
		tradeparamMap.put ("patientid",map.get ("PatientId"));
		tradeparamMap.put ("trantype","3");						//3微信支付
		tradeparamMap.put ("merchantnum","");
		tradeparamMap.put ("termid","");
		tradeparamMap.put ("refnum",map.get ("PayOrderid"));	//流水号,同支付流水号
		tradeparamMap.put ("tracenum",map.get ("PayOrderid"));	//流水号
		tradeparamMap.put ("trancardnum","");	//流水号
		tradeparamMap.put ("amount",map.get ("BillMoney"));
		tradeparamMap.put ("machinenum","");

		tradeparamMap.put ("in_operator","1605");	//操作员号,测试环境传1605

		tradeparamMap.put ("orgcode","");
		tradeparamMap.put ("displaywincode","");
		tradeparamMap.put ("wincount","");
		tradeparamMap.put ("errormsg","");	//异常错误信息
		tradeparamMap.put ("clearDate_app",ToolsUtil.getNowTime ());	//银行清算时间，当前时间
		tradeparamMap.put ("OpenID_app",map.get ("Openid"));
		tradeparamMap.put ("HisCardNo_app",map.get ("PatientId"));
		tradeparamMap.put ("PatientName_app",map.get ("PatientName"));	//患者姓名
		tradeparamMap.put ("FlowNo_app",map.get ("PayOrderid"));
		tradeparamMap.put ("Amt_app",map.get ("BillMoney"));
		tradeparamMap.put ("Amt_order_app",map.get ("BillMoney"));
		tradeparamMap.put ("YBAmt_app",0);
		tradeparamMap.put ("PatientId_app",map.get ("PatientId"));
		tradeparamMap.put ("BillNo_app",map.get ("BillNo"));
		tradeparamMap.put ("AppPayType","wechatpayWeb");	//传固定值
		tradeparamMap.put ("Area_app","");	//院区地址

		reqmap.put ("tradeparam",tradeparamMap);

		reqmap.put("sign", ToolsUtil.builderPostStrSign (tradeparamMap, qn_SysContants.yhConstantsmap.get("HISMD5KEY").toString()));//算签名sign
		
		Map<String,Object> respmap = hisrequest.dopost(qn_SysContants.yhConstantsmap.get("HISUrl").toString(), ToolsUtil.MapToJson(reqmap));
		logger.info("YH_his返回结算信息：" + respmap);
		
		return respmap;
	}

}
