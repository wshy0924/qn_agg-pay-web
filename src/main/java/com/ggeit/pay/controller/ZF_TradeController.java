package com.ggeit.pay.controller;

import com.ggeit.pay.impl.AliPayServiceImpl;
import com.ggeit.pay.impl.OrderService;
import com.ggeit.pay.impl.Qn_zfHisServiceImpl;
import com.ggeit.pay.impl.WxPayServiceImpl;
import com.ggeit.pay.utils.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/data")

public class ZF_TradeController {
	
	private final static Logger logger = LoggerFactory.getLogger(ZF_TradeController.class);

	@Autowired
	private WxPayServiceImpl wxpayimpl;
	@Autowired
	private AliPayServiceImpl alipayimpl;
	@Autowired
	private OrderService orderservice;
	@Autowired
	private Qn_zfHisServiceImpl qn_zfhisserviceImpl;

	/**
	 * 微信 这里return是返回ajax是否成功，通过data具体返回交易逻辑处理是否成功 逻辑处理完后，下一步动作可以返回给原始JSP去处理
	 * @param InputAmount
	 * @param openid
	 * @param BillNo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/wx_zftradeCreate", method = RequestMethod.POST)
	public Map<String,Object> doWxPay(@RequestParam("InputAmount") String InputAmount,@RequestParam("openid") String openid,
			@RequestParam("BillNo") String BillNo,@RequestParam String BillMoney,@RequestParam String BillName) throws Exception {
		logger.info("----------data dowxpay----------");
		logger.info("openid = " + openid);
		logger.info("BillNo = " + BillNo);
		logger.info("BillMoney = " + BillMoney);
		logger.info("BillName = " + BillName);

		Map<String, Object> map = new HashMap<String, Object>();

		String out_trade_no = WXPayUtil.wxcreateOrderID();				//本地生成微信c端流水号
		Map<String,Object> reqmap = new HashMap<String,Object>();
		reqmap.put("BillMoney", BillMoney);
		reqmap.put("openid", openid);
		reqmap.put("out_trade_no", out_trade_no);
		reqmap.put("BillName", BillName);

		Map<String, Object> unifiedordermap = wxpayimpl.UnifiedOrder(reqmap);

		if (unifiedordermap.get("statuscode").equals("200")) {
			String conResult = (String) unifiedordermap.get("conResult");
			Map<String, Object> conResultmap = WXPayUtil.wxJsonToMapObj(conResult);
			if (conResultmap.get("returnCode").equals("0000")) {
				Map<String, Object> datamap = (Map<String, Object>) conResultmap.get("data");
				String timeStamp = Long.toString(new Date().getTime()/1000);
				String randomplus = WXPayUtil.wxgetRandomPlus(20);
				//Map<String, Object> generatemd5map = wxpayimpl.GenerateMD5((String) datamap.get("prepay_id"),(String) datamap.get("appid"),timeStamp,randomplus);
				Map<String, Object> generatemd5map = wxpayimpl.InsideGenerateMD5((String) datamap.get("prepay_id"),(String) datamap.get("appid"),timeStamp,randomplus);
				if (generatemd5map.get("statuscode").equals("200")) {					//md5签名状态
					conResult = (String) generatemd5map.get("conResult");
					conResultmap = WXPayUtil.wxJsonToMapObj(conResult);
					if (conResultmap.get("returnCode").equals("0000")) {				//响应数据
//						map.put("retcode", "0000");
//						map.put("msg", "下单成功!");

						//支付详情页显示信息,根据BillNo查询详情页需要显示的信息

						map.put("BillNo", BillNo);									//单据唯一标识
						//调起支付台
						map.put("md5", conResultmap.get("data"));
						map.put("appid", datamap.get("appid"));
						map.put("mch_id", datamap.get("mch_id"));
						map.put("nonce_str", randomplus);
						map.put("prepay_id", "prepay_id="+datamap.get("prepay_id"));
						map.put("timeStamp", timeStamp);
						//map.put("pay_orderid",unifiedordermap.get("pay_orderid"));


						//支付详情页跳转标志
//						map.put("urlflag", "third_wxpay");

						//将微信支付的订单号插入billinfo数据库
						Map<String,Object> insertmap = new HashMap<String,Object>();
						insertmap.put("PayOrderid",out_trade_no);						//第三方支付订单号
						insertmap.put("ReturnOrderid", datamap.get("prepay_id"));	//微信返回的订单号
						insertmap.put ("PayStatus","PrePayed");
						insertmap.put("BillNo",BillNo);
						Boolean insertflag = orderservice.insert ("billinfo.insert", insertmap);
						logger.info("zf_wxpayorderid插入状态：" + insertflag);

					}else {
						map.put("retcode", "1111");
						map.put("tips", "支付失败!");

					}
				} else {
					map.put("retcode", "1111");
					map.put("tips", "支付失败!");

				}
			} else {
				map.put("retcode", "1111");
				map.put("tips", "支付失败!");

			}
		} else {
			map.put("retcode", "1111");
			map.put("tips", "支付失败!");

		}
		logger.info("ajax return map = "+map);

		return map;		//显示到支付详情页
	}
}
