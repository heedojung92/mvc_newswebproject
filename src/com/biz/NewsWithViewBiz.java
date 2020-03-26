package com.biz;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.dao.NewsWithViewDAO;
import com.vo.NewsWithViewVO;



@Service
public class NewsWithViewBiz {
	private static NewsWithViewDAO dao=null;
	static {
		ApplicationContext factory=
				new ClassPathXmlApplicationContext("com/dao/applicationContext.xml");
		dao=(NewsWithViewDAO)factory.getBean("newswithviewdao");
	}
	public NewsWithViewDAO getNewsViewDAO() {
		return dao;
	}
	
	public List<NewsWithViewVO> getMostViewNews(String date){
		return getNewsViewDAO().getMostViewNews(date);
	}
	
	public List<NewsWithViewVO> getMostViewNews(){
		return getNewsViewDAO().getMostViewNews();
	}
	
}
