package com.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.biz.HashTagBiz;
import com.biz.MemberBiz;
import com.biz.NewsBiz;
import com.biz.NewsWithViewBiz;
import com.google.gson.Gson;
import com.util.Crawler;
import com.util.HashTagGenerator;
import com.util.LogStatHelper;
import com.vo.HashTagVO;
import com.vo.NewsVO;
import com.vo.NewsWithViewVO;

@Controller
public class AdminController {
	static NewsBiz nb=new NewsBiz();
				
	//관리자 권한으로 로그인 되었을 시, 관리자 화면에 표시 될 데이터들을 계산한다. 
	@RequestMapping("/admin_login.do")
	public ModelAndView newsstat(HttpServletRequest request) {
		List<NewsVO>allNews=nb.getAllNews();
		Map<String,Integer>dayStat=new HashMap<>();
		Map<String,Integer>monthStat=new HashMap<>();
		for(NewsVO nv:allNews) {
			String date=nv.getNews_date().split("@")[0];
			String month=date.substring(0,7);
			if(dayStat.containsKey(date)) {
				dayStat.put(date,dayStat.get(date)+1);
			}else {
				dayStat.put(date,1);
			}
			if(monthStat.containsKey(month)) {
				monthStat.put(month,monthStat.get(month)+1);
			}else {
				monthStat.put(month,1);
			}
		}
		TreeMap<String,Integer>dayStatSorted=new TreeMap<>(dayStat);
		TreeMap<String,Integer>monthStatSorted=new TreeMap<>(monthStat);
		//2020.01.01 부터의 로그데이터를 수집한다.
		List<Map<String,Integer>>logdata=new LogStatHelper().runLogAnalyze("2020.01.01");
		Map<String,Integer>hourlyData=logdata.get(0);
		Map<String,Integer>dailyData=logdata.get(1);		
		List<NewsWithViewVO>mostView=new NewsWithViewBiz().getMostViewNews();		
		int memcnt=new MemberBiz().countMember();		
		Gson gson = new Gson();		
		ModelAndView mav=new ModelAndView();
		mav.getModelMap().addAttribute("dayStat", gson.toJson(dayStatSorted));
		mav.getModelMap().addAttribute("monthStat", gson.toJson(monthStatSorted));		
		mav.getModelMap().addAttribute("hourlyVisitorStat", gson.toJson(hourlyData));
		mav.getModelMap().addAttribute("dailyVisitorStat", gson.toJson(dailyData));		
		mav.getModelMap().addAttribute("mostviewed",gson.toJson(mostView));
		mav.getModelMap().addAttribute("membercnt",gson.toJson(memcnt));	
		mav.setViewName("admin/adminmain");
		return mav;
		
	}
}
