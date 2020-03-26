package com.biz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.dao.ViewCntDAO;


@Service
public class ViewCntBiz {
	private static ViewCntDAO dao=null;
	static {
		ApplicationContext factory=
				new ClassPathXmlApplicationContext("com/dao/applicationContext.xml");
		dao=(ViewCntDAO)factory.getBean("viewcntdao");
	}
	public ViewCntDAO getViewCntDAO() {
		return dao;
	}	
	
	public void updateView(int news_no) {
		getViewCntDAO().updateView(news_no);
	}

	public int countScrap(int newsNo) {
		return getViewCntDAO().countScrap(newsNo);
	}
	
	public int countView(int newsNo) {
		return getViewCntDAO().countView(newsNo);
	}
	public void updateSeq(int prev_news_no, int next_news_no) {
		getViewCntDAO().updateSeq(prev_news_no, next_news_no);
	}

}
