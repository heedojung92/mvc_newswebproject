package com.biz;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.dao.StockDAO;
import com.vo.StockVO;

@Service
public class StockBiz {
	private static StockDAO dao=null;
	static {
		ApplicationContext factory=
				new ClassPathXmlApplicationContext("com/dao/applicationContext.xml");
		dao=(StockDAO)factory.getBean("stockdao");
	}
	public StockDAO getStockDAO() {
		return dao;
	}
	public List<StockVO> getStockList(String compName, String dayStart, String dayEnd){
		return getStockDAO().getStockList(compName, dayStart, dayEnd);
	}
	public void insertBatch(List<StockVO> stocklist) {
		getStockDAO().insertBatch(stocklist);
	}
	public String getMaxDate() {
		return getStockDAO().getMaxDate(); 
	}

}
