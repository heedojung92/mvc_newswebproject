package com.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.dao.NewsDAO;
import com.vo.NewsSeqVO;
import com.vo.NewsVO;

@Service
public class NewsBiz {
	private static NewsDAO dao=null;
	static {
		ApplicationContext factory=
				new ClassPathXmlApplicationContext("com/dao/applicationContext.xml");
		dao=(NewsDAO)factory.getBean("newsdao");
	}
	public NewsDAO getNewsDAO() {
		return dao;
	}	
	
	public void insertBatch(List<NewsVO> newslist) {
		getNewsDAO().insertBatch(newslist);
	}
	
	public List<NewsVO>getNewsByDate(String date){
		return getNewsDAO().getNewsByDate(date);
	}
	
	public List<NewsVO> getNewsByDateAndSid(String date, String sid){
		return getNewsDAO().getNewsByDateAndSid(date, sid);
	}
	public List<NewsVO>getAllNews(){
		return getNewsDAO().getAllNews();
	}
	public List<NewsVO> getNewsBetweenDatesWithTag(String from, String to, String tag){
		return getNewsDAO().getNewsBetweenDatesWithTag(from, to, tag);
	}
	public List<NewsVO>newsSearchFromDateAndTagAndSid(String date,String tag,String sid){
		return getNewsDAO().newsSearchFromDateAndTagAndSid(date, tag, sid);
	}
			
	public List<NewsVO> getRealNews(int newsNo){
		return getNewsDAO().getRealNews(newsNo);
	}	

	public List<NewsVO> getScrapNews(String userNo){
		return getNewsDAO().getScrapNews(userNo);
	}
	public int countScrapNews(String userNo,String newsNo) {
		return getNewsDAO().countScrapNews(userNo, newsNo);
	}
		
	public int doScrapNews(String userNo, String newsNo) {
		return getNewsDAO().doScrapNews(userNo, newsNo);
	}	
	public int deleteScrapNews(String userNo, String newsNo) {
		return getNewsDAO().deleteScrapNews(userNo, newsNo);
	}	
	
	public List<NewsVO> getViewNews(String userNo){
		return getNewsDAO().getViewNews(userNo);
	}
		
	public List<NewsVO>getRelNews(int newsno){
		return getNewsDAO().getRelNews(newsno);
	}
		
	public List<NewsVO>getNewsBetweenDates(String from,String to){
		return getNewsDAO().getNewsBetweenDates(from, to);
	}
	
	public String getMaxNewsDate() {
		return getNewsDAO().getMaxNewsDate();
	}
	
	public List<NewsVO>newsSearchFromDateAndTag(String date,String tag){
		return getNewsDAO().newsSearchFromDateAndTag(date, tag);
	};
	
	public List<NewsVO>newsSearchFromDateAndPub(String date,String pub){
		return getNewsDAO().newsSearchFromDateAndPub(date, pub);
	}

	
}
