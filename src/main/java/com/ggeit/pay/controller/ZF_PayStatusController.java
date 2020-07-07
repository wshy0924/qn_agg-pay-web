package com.ggeit.pay.controller;

import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ggeit.pay.config.qn_SysContants;
import com.ggeit.pay.impl.AliPayServiceImpl;
import com.ggeit.pay.impl.OrderService;
import com.ggeit.pay.impl.Qn_zfHisServiceImpl;
import com.ggeit.pay.impl.WxPayServiceImpl;
import com.ggeit.pay.utils.ToolsUtil;
import com.ggeit.pay.utils.WXPayUtil;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/22
 **/
@Controller
@CrossOrigin
@RequestMapping("/status")
public class ZF_PayStatusController {
    private final static Logger logger = LoggerFactory.getLogger(ZF_PayStatusController.class);

    @Autowired
    private WxPayServiceImpl wxpayimpl;
    @Autowired
    private AliPayServiceImpl alipayimpl;
    @Autowired
    private OrderService orderservice;
    @Autowired
    private Qn_zfHisServiceImpl qn_zfhisserviceImpl;

    @RequestMapping(value = "/ZF_wxpaystatus" ,method = RequestMethod.GET)
    public String ZFwxpaystatus(HttpServletRequest request, HttpServletResponse response, @RequestParam String BillNo, Map <String,Object> map) throws Exception{
        logger.info("接收支付详情页数据BillNo：" + BillNo);

        Map <String, Object> trademap = new HashMap <String, Object> ();        //请求his结算map
        Map<String,Object> reqmap = new HashMap <String,Object> ();            //请求billinfo的map

        //根据BillNo查billinfo数据库
        reqmap.put("BillNo", BillNo);						//BillNo由支付详情页传入
        List <Map> resultlistmap = orderservice.selectOrder("billinfo.select", reqmap);
        HashMap resultmap = (HashMap) resultlistmap.get(0);		//结果唯一

        //下面参数用于his结算
        String PayOrderid = (String) resultmap.get("PayOrderid");
        String Openid = (String) resultmap.get("Openid");
        String BillMoney = (String) resultmap.get("BillMoney");
        String PatientId = (String) resultmap.get("PatientId");
        String PatientName = (String) resultmap.get("PatientName");
        String BillType = (String) resultmap.get ("BillType");

        //--------------------------step01 根据微信支付订单号轮询支付结果-----------------------
        logger.info ("---------------------step01 根据微信支付订单号轮询支付结果-----------------------");
        Map<String,Object> querymap = wxpayimpl.Wx_CycleOrderQuery(PayOrderid);

        String conresultstr = querymap.get("conResult").toString ();
        Map<String,Object> conresultmap = ToolsUtil.JsonToMapObj (conresultstr);
        Map<String,Object> conResultdatamap = (Map<String, Object>) conresultmap.get("data");
        logger.info ("conResultmap = " + conResultdatamap);
        String trade_state =  conResultdatamap.get("trade_state").toString ();
        String ReturnOrderid = conResultdatamap.get ("transaction_id").toString ();    //微信返回的订单号
        logger.info ("wx_xycleorderquery trade_state {}",trade_state);

        if(!"SUCCESS".equals(trade_state)) {					//微信支付失败
            map.put("tips", "订单支付失败!");
            return "wc_failure";
        }else {
            //-----------------------step02 调用his结算接口----------------------
            logger.info ("----------------------------ste02 调用His结算接口------------------------");
            trademap.put ("BillNo", BillNo);
            trademap.put ("PatientId", PatientId);
            trademap.put ("OpenID", Openid);
            trademap.put ("PatientName", PatientName);
            trademap.put ("BillType",BillType);
            trademap.put ("BillMoney",BillMoney);

            Map <String, Object> traderesultmap = qn_zfhisserviceImpl.zf_TradePay (trademap);
            if (!"0000".equals (traderesultmap.get ("returnCode"))) {
                map.put ("tips", traderesultmap.get ("returnInfo"));
                return "wc_failure";
            } else {

                Map <String, Object> traderesultdatamap = (Map <String, Object>) traderesultmap.get ("data");
                //判断his返回状态，成功，返回success，失败返回failure，失败时，调用微信退费接口

                if ("0000".equals (traderesultdatamap.get ("returnCode"))) {
                    map.put ("tips", "系统确认成功" + "\n" + "您已成功支付" + BillMoney + "元！");
                    //billinfo中paystatus设为payed
                    Map <String, Object> insertMap = new HashMap <String, Object> ();
                    insertMap.put ("BillNo", BillNo);
                    insertMap.put ("PayStatus", "Payed");
                    Boolean resultflag = orderservice.update ("billinfo.update", insertMap);
                    logger.info ("update billinfo PayStatus to Payed：" + resultflag);

                    //拼接支付成功页面显示订单具体信息
                    map.put ("PatientName", PatientName);
                    map.put ("PatientId", PatientId);
                    map.put ("payType", "微信支付");
                    map.put ("BillCost", BillMoney);
                    String nowtime = ToolsUtil.getNowTime ();
                    map.put ("NowTime", nowtime);

                    return "wc_success";

                } else if ("AAAA".equals (traderesultdatamap.get ("Return"))) {            //只有确认return为AAAA才进行退费
                    //结算失败，调用微信退款接口
                    Map <String, Object> refundmap = wxpayimpl.Wx_Refund (BillNo, PayOrderid);
                    if (refundmap.get ("statuscode").equals ("200")) {                //微信服务网关退款接口返回
                        String conResult = (String) refundmap.get ("conResult");
                        Map <String, Object> conResultmap = WXPayUtil.wxJsonToMapObj (conResult);
                        if (conResultmap.get ("returnCode").equals ("0000")) {        //退款接口调用成功，需再判断接口返回内容，确定是否退款成功
                            Map <String, Object> datamap = (Map <String, Object>) conResultmap.get ("data");
                            logger.info ("------------wx退款成功-----------------");
                            if (datamap.get ("refund_fee").toString () != "" && "SUCCESS".equals (datamap.get ("result_code").toString ())) {  //退款成功
                                String refund_fee = datamap.get ("refund_fee").toString ();
                                String refund_fee_yuan = WXPayUtil.wxchangeF2Y (refund_fee);    //分 转 元
                                map.put ("tips", "订单支付异常，已退款" + refund_fee_yuan + "元!");

                                //billinfo中paystatus设为Refunded
                                Map <String, Object> insertMap = new HashMap <String, Object> ();
                                insertMap.put ("BillNo", BillNo);
                                insertMap.put ("PayStatus", "Refunded");
                                Boolean updateflag = orderservice.update ("billinfo.update", insertMap);
                                logger.info ("update billinfo PayStatus to Refunded：" + updateflag);

                            } else {
                                logger.info ("-----------------wx退款失败---------------");
                                map.put ("tips", "系统缴费失败，并尝试退款异常，请联系医院窗口处理退费！");

                                //billinfo中paystatus设为Refunded_Err
                                Map <String, Object> insertMap = new HashMap <String, Object> ();
                                insertMap.put ("BillNo", BillNo);
                                insertMap.put ("PayStatus", "Refunded_Err");
                                Boolean updateflag = orderservice.update ("billinfo.update", insertMap);
                                logger.info ("update billinfo PayStatus to Refunded_Err：" + updateflag);

                            }
                        }
                    }
                    return "wc_failure";
                } else {
                    map.put ("tips", "缴费失败，请联系医院进行查询！");
                    return "wc_failure";
                }
            }
        }

    }

    /**
     * 阿里 这里return是返回ajax是否成功，通过data具体返回交易逻辑处理是否成功 逻辑处理完后，下一步动作可以返回给原始JSP去处理
     *
     * @param
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/ali_zftradeCreate", method = RequestMethod.POST)
    public String doAliPay(@RequestParam("InputAmount") String InputAmount, @RequestParam("userid") String userid,
                           @RequestParam("BillNo") String BillNo){
        logger.info("----------data dopay----------");
        //logger.info("remarkinfo = " + remarkinfo);
        Map<String, String> map = new HashMap<String, String>(); //返回数据
        try {
            AlipayTradeCreateResponse unifiedorderresp = alipayimpl.UnifiedOrder(InputAmount, userid);
            logger.info("doalipay unifiedorderresp = " +unifiedorderresp);
            if(unifiedorderresp.isSuccess()){
                logger.info("----------支付宝统一下单成功----------");
                logger.info("doalipay TradeNo = "+unifiedorderresp.getTradeNo());//支付宝交易号
                logger.info("doalipay OutTradeNo = "+unifiedorderresp.getOutTradeNo());//商户订单号
                logger.info("doalipay Body = "+unifiedorderresp.getBody());
//				map.put("tradeNO", unifiedorderresp.getTradeNo());
//				map.put("pay_orderid", unifiedorderresp.getOutTradeNo());

                //将out_trade_no、total_amout、trade_no插入alinotify数据库，用于验证异步通知结果
                Map<String,Object> notifymap = new HashMap<String,Object>();
                notifymap.put("out_trade_no", unifiedorderresp.getOutTradeNo());
                notifymap.put("total_amount", InputAmount);
                notifymap.put("trade_no", unifiedorderresp.getTradeNo());
                Boolean notifyflag = orderservice.insert("alinotify.insert", notifymap);
                logger.info("notifyflag 插入数据库： " + notifyflag);

                //支付详情页显示信息,根据BillNo查询详情页需要现实的信息
                Map<String,Object> paramMap = new HashMap<String,Object>();//请求bill_info数据库map
                paramMap.put("BillNo",BillNo);
                Map<String,Object> resultmap = (Map<String, Object>) orderservice.selectOrder("billno.select", paramMap);

                //显示页信息录入
                map.put("PatientName",(String) resultmap.get("PatientName"));	//就诊人
                map.put("BillNo", BillNo);	//单据号
                map.put("transType", qn_SysContants.transtypemap.get(resultmap.get("transType")));	//业务类型
                map.put("payType", qn_SysContants.transtypemap.get(resultmap.get("BillMethod")));	//支付方式
                map.put("BillCost",(String) resultmap.get("TotalAmount"));	//单据费用
                String nowtime = ToolsUtil.getNowTime();
                map.put("NowTime", nowtime);	//订单支付时间
                map.put("trade_no", unifiedorderresp.getTradeNo());

                //支付详情页跳转标志
                map.put("urlflag", "third_alipay");

                //将ali支付的订单号插入yh_billnoinfo数据库
                Map<String,Object> insertmap = new HashMap<String,Object>();
                insertmap.put("zf_alipayorderid",unifiedorderresp.getOutTradeNo());//ali支付订单号
                Boolean insertflag = orderservice.insert("billno.insert", insertmap);
                logger.info("zf_alipayorderid插入状态：" + insertflag);

                return "wc_payInfo";//支付详情页

            }else {
                map.put("retcode", "1111");
                map.put("tips", "支付失败!");
                return "wc_failure";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            map.put("retcode", "1111");
            map.put("tips", "支付失败!");
            e.printStackTrace();
            return "wc_failure";
        }
    }
    /**
     * 支付宝下单状态
     * @param request
     * @param response
     * @param paramStr
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/ZF_alipaystatus" ,method = RequestMethod.POST)
    public String ZFalipaystatus(HttpServletRequest request,HttpServletResponse response, @RequestBody JsonObject paramStr,Map<String,Object> map) throws Exception{
        logger.info("接收支付详情页数据：" + paramStr);

        Map<String,Object> paramMap = ToolsUtil.JsonToMapObj(paramStr.toString());
        String BillNo = (String) paramMap.get("BillNo");
        String trade_no = (String) paramMap.get("trade_no");
        //根据BillNo查yh_billno数据库
        Map<String,Object> reqmap = new HashMap<String,Object>();
        reqmap.put("BillNo", BillNo);//BillNo由支付详情页传入
        Map<String,Object> resultmap = (Map<String, Object>) orderservice.selectOrder("yh_paystatus.select", reqmap);
        String zf_alipayorderid = (String) resultmap.get("zf_alipayorderid");

        //根据支付宝支付订单号查询ali异步通知数据中的pay_status
        logger.info("--------------------查询ali异步通知数据库中支付状态-------------------");
        //根据trade_no查询ali_notify数据库，取出trade_status
        Map<String,Object> selectmap = new HashMap<String,Object>();
        selectmap.put("trade_no", trade_no);
        List<Map> alinotifyList = orderservice.selectOrder("alinotify.selectBytradeno",selectmap);

        String trade_status = (String) alinotifyList.get(0).get("trade_status");

        //判断trade_status状态
        if(!trade_status.equals("TRADE_SUCCESS")) {//ali支付失败
            map.put("tips", trade_status);
            return "failure";
        }else {
            //调用his结算接口
            logger.info("----------------------------调用His结算接口------------------------");
            Map<String,Object> trademap  = new HashMap<String,Object>();
            trademap.put("PatientId", resultmap.get("PatientId"));
            trademap.put("BillNo", resultmap.get("BillNo"));
            trademap.put("BillMethod", resultmap.get("BillMethod"));
            trademap.put("BusinessNumber", "205");
            trademap.put("TerminalNumber", qn_SysContants.yhConstantsmap.get("TerminalNumber").toString());
            trademap.put("CooperationUnit", qn_SysContants.yhConstantsmap.get("CooperationUnit").toString());
            trademap.put("Price", resultmap.get("BillCost"));	//单据费用

            Map<String,Object> traderesultmap = qn_zfhisserviceImpl.zf_TradePay(trademap);
            //判断his返回状态，成功，返回success，失败返回failure，失败时，调用微信退费接口
            String amount = resultmap.get("BillCost").toString();

            if("1".equals(traderesultmap.get("Return"))) {

                map.put("tips", "系统确认成功"+"\n"+"您已成功支付" + amount + "元！");
                return "wc_success";
            }else {
                //调用支付宝退款接口
                AlipayTradeRefundResponse refundresp = alipayimpl.Ali_Refund(zf_alipayorderid, trade_no, amount);
                logger.info("body=" + refundresp.getBody());
                logger.info(refundresp.getRefundFee());

                if (refundresp.isSuccess()) {
                    logger.info("------------------ali订单退款成功------------------");
                    String refund_fee = refundresp.getRefundFee();
                    String third_refundid = refundresp.getOutTradeNo(); //退款订单号
                    map.put("tips", "订单支付异常，已退款" + refundresp.getRefundFee() + "元!");
                    //map.put("tips","订单支付异常，正在请求退款，请稍后查询！");
                    return "wc_failure";
                }else {
                    map.put("tips", "系统缴费失败，并尝试退款失败，请联系医院窗口处理退费！");
                    //map.put("tips","订单支付异常，正在请求退款，请稍后查询！");
                    return "wc_failure";
                }
            }
        }
    }

    /**
     * sucess支付成功
     * @param map
     * @param tips
     * @return
     */
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String success(Map<String, Object> map, String tips) {
        logger.info("----------layer success----------");
        map.put("tips", tips);
        return "wc_success";
    }
    /**
     * failure支付失败
     * @param map
     * @param
     * @return
     */
    @RequestMapping(value = "/failure", method = RequestMethod.GET)
    public String failure(Map<String, Object> map, String status) {
        logger.info("----------layer failure----------");
//		logger.info("支付失败页面返回的cfsb = " + cfsb);
//		logger.info("支付失败页面返回的pay_orderid = " + pay_orderid);

        map.put("tips", status);
        return "wc_failure";
    }

    /**
     * 退款接口（）用于测试退款
     * @param request
     * @param response
     * @param PreMoney
     * @param ThirdOrderid
     * @throws Exception
     */
    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public void refund(HttpServletRequest request,HttpServletResponse response,@RequestParam String PreMoney,@RequestParam String ThirdOrderid) throws Exception {
        logger.info("----------layer refund----------");

        Map<String, Object> refundmap = wxpayimpl.Wx_Refund(PreMoney, ThirdOrderid);

    }

}
