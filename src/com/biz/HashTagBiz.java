package com.biz;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.dao.HashTagDAO;
import com.vo.HashTagVO;

@Service
public class HashTagBiz {
	private static HashTagDAO dao=null;
	static {
		ApplicationContext factory=
				new ClassPathXmlApplicationContext("com/dao/applicationContext.xml");
		dao=(HashTagDAO)factory.getBean("hashtagdao");
	}
	public HashTagDAO getDAO() {
		return dao;
	}	
	public int insertOne(HashTagVO htv) {
		return getDAO().insertOne(htv);
	}	
	public void insertBatch(List<HashTagVO> taglist) {
		getDAO().insertBatch(taglist);
	}	
	public List<HashTagVO> getTagsByDate(String from,String to){
		return getDAO().getTagsByDate(from,to);
	}	
	public HashTagVO getTagByDate(String date) {
		return getDAO().getTagByDate(date);
	}
	
}
