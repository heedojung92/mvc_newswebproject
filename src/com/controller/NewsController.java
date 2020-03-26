package com.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biz.HashTagBiz;
import com.biz.NewsBiz;
import com.biz.NewsWithViewBiz;
import com.google.gson.Gson;
import com.util.AutoUpdate;
import com.util.DateStringHelper;
import com.util.HashTagGenerator;
import com.util.NewsStatHelper;
import com.vo.NewsVO;
import com.vo.NewsWithViewVO;

@Controller
public class NewsController {
	static NewsBiz nb = new NewsBiz();	
	//메인 뉴스페이지로 매핑시켜 주는 메소드
	@RequestMapping("/news_main.do")
	public ModelAndView mainnewspage(HttpServletRequest request) {
		new AutoUpdate().updateNewsAndTag();
		ModelAndView mav = new ModelAndView();
		String todayAsString=new DateStringHelper().getCurrentDate();				
		NewsWithViewBiz nvb=new NewsWithViewBiz();
		HashTagGenerator htg=new HashTagGenerator();
		HashTagBiz htb=new HashTagBiz();		
		//오늘 등장 빈도수가 높은 키워드(해시 태그)들의 집계를 낸다.
		Map<String,Integer>hashtagmap=htg.getTopNFromMap(htb.getTagByDate(todayAsString).getTotal_tags(),50);
		//오늘 가장 많이 조회 된 뉴스를 구하기 위한 메소드 
		List<NewsWithViewVO>mostViewToday=nvb.getMostViewNews(todayAsString);
		//최신 뉴스들을 구하기 위한 메소드
		List<NewsVO>todayNews=nb.getNewsByDate(todayAsString);
		//Gson 객체 :Java 객체(List 포함)들을 View에 전달(.jsp)할 때, JSON타입으로 변환하여 보내줄 수 있다.
		Gson gson = new Gson();			
		mav.getModelMap().addAttribute("hashtag",gson.toJson(hashtagmap));
		mav.getModelMap().addAttribute("recentNews", todayNews);
		mav.getModelMap().addAttribute("mostviewedtoday",gson.toJson(mostViewToday));
		mav.setViewName("news/news_mainpage");
		return mav;
	}
	//뉴스 검색 결과 페이지로 매핑시켜주는 메소드
	@RequestMapping("/searchnews.do")
	public ModelAndView newssearchres(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String query=request.getParameter("query");
		String dateStart=request.getParameter("date_start");
		String dateEnd=request.getParameter("date_end");
		Gson gson=new Gson();
		String date=null;
		if(dateStart.equals(dateEnd)) {
			date=dateStart;
		}
		String searchType="키워드";
		List<NewsVO>res=new ArrayList<>();
		String cat=query.split("-")[0].trim();
		String val=query.split("-")[1].trim();	
		if(date!=null) {						
			if(cat.contains("분야")) {
				searchType="카테고리";
				String sid=new NewsStatHelper().catConverter(val);
				res=nb.getNewsByDateAndSid(date, sid);
			}else if(cat.contains("기업")||cat.contains("검색")){
				if(date.contains("-")) {
					date=date.replaceAll("-", ".");
				}
				if(cat.contains("검색")) {
					searchType="검색어";
					res=nb.newsSearchFromDateAndTag(date, val);
				}else {
					searchType="기업";
					res=nb.newsSearchFromDateAndTag(date, val);
				}				
			}else {
				searchType="언론사";
				res=nb.newsSearchFromDateAndPub(date, val);
			}
		}
		//검색의 기준이되는 뉴스의 기간이 하루 이상일 경우
		else {
			if(dateStart.contains("-")) {
				dateStart=dateStart.replaceAll("-", ".");
				dateEnd=dateEnd.replaceAll("-", ".");
			}
			List<NewsVO>betweendates=nb.getNewsBetweenDates(dateStart.trim(),dateEnd.trim());
			if(cat.contains("분야")) {
				searchType="카테고리";
				String sid=new NewsStatHelper().catConverter(val);
				for(NewsVO nv:betweendates) {
					if(nv.getNews_sid().equals(sid)) {
						res.add(nv);
					}
				}
			}else if(cat.contains("기업")||cat.contains("검색")){
				if(cat.contains("검색")) {
					searchType="검색어";
					res=nb.getNewsBetweenDatesWithTag(dateStart.trim(),dateEnd.trim(),val);
				}else {
					searchType="기업";
					res=nb.getNewsBetweenDatesWithTag(dateStart.trim(),dateEnd.trim(),val);
				}				
			}else {
				searchType="언론사";
				for(NewsVO nv:betweendates) {
					if(nv.getNews_pub().equals(val)) {
						res.add(nv);
					}
				}
			}
			
		}
		mav.getModelMap().addAttribute("date_start",dateStart);
		mav.getModelMap().addAttribute("date_end",dateEnd);
		mav.getModelMap().addAttribute("input",val);
		mav.getModelMap().addAttribute("search_res",gson.toJson(res));
		mav.getModelMap().addAttribute("news_res",res);
		mav.getModelMap().addAttribute("search_type",searchType);
		mav.setViewName("news/news_search_result");
		return mav;
	}		
}
