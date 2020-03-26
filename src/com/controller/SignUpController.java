package com.controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.dao.SignUpDAO;
import com.dao.SignUpValidDAO;
import com.vo.MemberVO;

@Controller
public class SignUpController {
	private static SignUpValidDAO signUpValidDao = new SignUpValidDAO();
	
	@RequestMapping("signuprequest.do")
	public ModelAndView SignUpRequest(HttpServletRequest request) {	
		ModelAndView mav=new ModelAndView();
		mav.setViewName("member/signup");
		return mav;
	}	
	@RequestMapping("/signupcontrol.do")
	public Object SignUp(HttpServletRequest request) {		
		MemberVO memberVo = new MemberVO();
		memberVo.setUser_email(request.getParameter("email"));
		memberVo.setUser_pw(request.getParameter("pw"));
		memberVo.setUser_name(request.getParameter("name"));
		memberVo.setUser_cityno(request.getParameter("city_no"));		
		SignUpDAO signUpDao = new SignUpDAO();		
		int ok = signUpDao.register(memberVo);	
		ModelAndView mav = new ModelAndView();
		if (ok > 0) {
			request.getSession().setAttribute("signedUser", memberVo);
			mav.setViewName("member/mypage");
		} 
		return mav;
	}

	// id 중복확인 및 이메일 형식 확인
	@RequestMapping(value = "/idCheck.do", method = RequestMethod.GET)
	public void idCheck(HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("email");
		 boolean err = false;
		  String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";   
		  Pattern p = Pattern.compile(regex);
		  Matcher m = p.matcher(email);
		  if(m.matches()) {
		   err = true; 
		  }				
		int result = signUpValidDao.idCheck(email);
		if(result==0&&!err) {
			result=-1;
		}
		try {
			response.getWriter().print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 이름 / 닉네임 중복확인
	@RequestMapping(value = "/nameCheck.do", method = RequestMethod.GET)
	public void nickCheck(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
		int result = new SignUpValidDAO().nameCheck(name);
		try {
			response.getWriter().print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
