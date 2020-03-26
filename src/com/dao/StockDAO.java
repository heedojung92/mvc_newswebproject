package com.dao;

import java.util.List;

import com.vo.StockVO;

public interface StockDAO {
	List<StockVO> getStockList(String compName,String dayStart,String dayEnd);

	void insertBatch(List<StockVO> stocklist);

	String getMaxDate();

}
