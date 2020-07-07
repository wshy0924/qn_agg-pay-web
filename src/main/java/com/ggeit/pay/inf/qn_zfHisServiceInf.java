package com.ggeit.pay.inf;

import java.util.Map;

public interface qn_zfHisServiceInf {
	Map<String, Object> zf_Queryinfo(Map<String, Object> reqmap)throws Exception;//查询代缴费处方信息

	Map<String, Object> zf_TradePay(Map<String, Object> map) throws Exception;	//his缴费

}
