package com.ggeit.pay.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

	private final static Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	@Autowired
	private SqlSession sqlSession;
	
	/**
	 * 可以在这个服务里处理多个SQL，加上事务
	 * @param paramMap
	 * @return
	 */
	public Object service(Map paramMap) {
		String tradeCode = (String) paramMap.get("tradeCode");
		logger.info("tradeCode = " + tradeCode);
		return selectOrder(tradeCode,paramMap);
	}
	
	public List<Map> selectOrder(String stmt,Map paramMap){
		List<Map> orderinfoList = sqlSession.selectList(stmt, paramMap);
		return orderinfoList;
	}
	
	public boolean insert(String stmt,Map<String, Object> paramMap) {
        int ret = Integer.valueOf(sqlSession.insert(stmt, paramMap));
        if(ret == 1)//成功
            return true;
        else 
        	return false;
	}
	
	public boolean update(String stmt,Map<String, Object> paramMap) {
		int ret = Integer.valueOf(sqlSession.update(stmt, paramMap));
        if(ret == 1)//成功
            return true;
        else 
        	return false;
	}
	
}
