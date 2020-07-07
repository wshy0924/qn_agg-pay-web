package com.ggeit.pay.controller;

import com.ggeit.pay.impl.Qn_zfHisServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * @author wshy
 * @data 2020/7/7
 **/
@RestController
@RequestMapping(value = "/lay",method = RequestMethod.GET)
public class testController {
    private final static Logger logger = LoggerFactory.getLogger (testController.class);

    @Autowired
    private Qn_zfHisServiceImpl qn_zfHisService;

    @RequestMapping("/queryBill")
    public Map<String,Object> queryBill(HttpServletRequest request, HttpServletResponse response, @RequestParam String PatientId) throws Exception {
        Map<String,Object> querymap = new HashMap <> ();
        querymap.put("PatientId",PatientId);
       Map<String,Object> returnmap = qn_zfHisService.zf_Queryinfo (querymap);

        return returnmap;
    }

    @RequestMapping("/tradepay")
    public Map<String,Object> tradepay(HttpServletRequest request, HttpServletResponse response, @RequestParam String PatientId,
    @RequestParam String BillNo,@RequestParam String Openid,@RequestParam String PatientName,@RequestParam String BillType,@RequestParam String BillMoney) throws Exception {
        Map<String,Object> trademap = new HashMap <> ();

        trademap.put ("BillNo", BillNo);
        trademap.put ("PatientId", PatientId);
        trademap.put ("OpenID", Openid);
        trademap.put ("PatientName", PatientName);
        trademap.put ("BillType",BillType);
        trademap.put ("BillMoney",BillMoney);

        Map<String,Object> returnmap = qn_zfHisService.zf_TradePay (trademap);

        return returnmap;
    }


}
