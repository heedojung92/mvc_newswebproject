package com.dao;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.dao.NewsDAOImpl.NewsVORowMapper;
import com.vo.NewsSeqVO;
import com.vo.NewsVO;

public interface NewsDAO {
	
	void insertBatch(List<NewsVO> newslist);
	
	List<NewsVO>getNewsByDate(String date);
	
	List<NewsVO> getNewsByDateAndSid(String date, String sid);
	
	List<NewsVO>getAllNews();
	
	int countScrap(int newsNo);
	
	List<NewsVO> getRealNews(int newsNo);

	List<NewsVO> getScrapNews(String userNo);
	
	List<NewsVO> getViewNews(String userNo);
	
	int doScrapNews(String userNo, String newsNo);
	
	int countScrapNews(String userNo,String newsNo);
	
	int deleteScrapNews(String userNo, String newsNo);
			
	List<NewsVO>getRelNews(int newsno);
		
	String getMaxNewsDate();
	
	List<NewsVO>getNewsBetweenDates(String from,String to);
	
	List<NewsVO>newsSearchFromDateAndTag(String date,String tag);
		
	List<NewsVO>newsSearchFromDateAndPub(String date,String pub);
	
	List<NewsVO>newsSearchFromDateAndTagAndSid(String date,String tag,String sid);

	List<NewsVO> getNewsBetweenDatesWithTag(String from, String to, String tag);

		
}
