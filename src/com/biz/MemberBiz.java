package com.biz;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.dao.MemberDAO;
import com.vo.MemberVO;

@Service
public class MemberBiz {
	private static MemberDAO dao=null;
	static {
		ApplicationContext factory=
				new ClassPathXmlApplicationContext("com/dao/applicationContext.xml");
		dao=(MemberDAO)factory.getBean("memberdao");
	}
	public MemberDAO getMemberDAO() {
		return dao;
	}
	public int loginCheck(String email,String pw) {
		return getMemberDAO().loginCheck(email, pw);
	}
	public MemberVO getMemberByEmail(String email) {
		return getMemberDAO().getMemberByEmail(email);
	}	
	public int viewCheck(int userno, int newsno) {
		return getMemberDAO().viewCheck(userno, newsno);
	}
	public int memUpdate(String pw, String name, String email) {
		return getMemberDAO().memUpdate(pw, name, email);
	}
	public int updateview(int userno, int newsno) {
		return getMemberDAO().updateview(userno, newsno);
	}
	public int countMember() {
		return getMemberDAO().countMember();
	}
	
}
