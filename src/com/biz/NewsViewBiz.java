package com.biz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.dao.NewsDAO;
import com.dao.ViewCntDAO;



@Service
public class NewsViewBiz {
	private static ViewCntDAO dao=null;
	static {
		ApplicationContext factory=
				new ClassPathXmlApplicationContext("com/dao/applicationContext.xml");
		dao=(ViewCntDAO)factory.getBean("viewcntdao");
	}
	public ViewCntDAO getDAO() {
		return dao;
	}
	
	public void updateView(int news_no) {
		getDAO().updateView(news_no);
	}

}
