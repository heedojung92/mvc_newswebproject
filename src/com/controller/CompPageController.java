package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.biz.NewsBiz;
import com.biz.StockBiz;
import com.google.gson.Gson;
import com.util.AutoUpdate;
import com.util.HashTagGenerator;
import com.util.NewsStatHelper;
import com.vo.NewsVO;
import com.vo.StockVO;

@Controller
public class CompPageController {
	static NewsBiz nb=new NewsBiz();
	//기업별 뉴스 메인페이지로 매핑 시켜주는 메소드  
	@RequestMapping("/comp_news.do")
	public ModelAndView compnews(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		//주가 데이터 자동 크롤링 후, DB에 저장한다.
		new AutoUpdate().updateStock();
		mav.setViewName("news/company_news_mainpage");
		return mav;
	}	
	//기업별 뉴스 페이지에서 유저의 요청(회사명, 시작일자, 종료일자)에 따라, 화면에 표시될 데이터를 동적으로 바꾸어 줄 수 있는 메소드(AJAX)
	@RequestMapping(value = "/update_compnews.do", method = RequestMethod.POST)
	@ResponseBody
	public void catNewsFromTag(HttpServletRequest request, HttpServletResponse response) {
		String compname = request.getParameter("compname");
		String day_start = request.getParameter("daybegin").replaceAll("-",".");
		String day_end=request.getParameter("dayend").replaceAll("-",".");
		//입력된 회사명, 시작일자, 종료일자를 받아와 주가데이터를 Oracle DB에서 가지고 오는 메소드
		List<StockVO>stock_res=new StockBiz().getStockList(compname, day_start, day_end);
		List<NewsVO>news=nb.getNewsBetweenDates(day_start, day_end);		
		HashTagGenerator htg=new HashTagGenerator();
		List<NewsVO>news_res=nb.getNewsBetweenDatesWithTag(day_start,day_end,compname);
		Map<String,Integer>comptags=htg.hashTagsFromNewsList(news_res);
		Map<String,Integer>map_res=htg.getTopNFromMap(comptags, 60);
		Gson gson = new Gson();
		String stockDataJson=gson.toJson(stock_res);
		String hashTagJson=gson.toJson(map_res);
		String both=stockDataJson+"%"+hashTagJson;		
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(both));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	//그래프에서 선택된 날짜와 선택된 회사의 뉴스를 가져와, 화면에 표시될 데이터를 동적으로 바꾸어 줄 수 있는 메소드(AJAX)
	@RequestMapping(value = "/getcompnews.do", method = RequestMethod.POST)
	@ResponseBody
	public void getCompNews(HttpServletRequest request, HttpServletResponse response) {
		String date=request.getParameter("datechoice");
		String compname=request.getParameter("compname");
		List<NewsVO>res=nb.newsSearchFromDateAndTag(date, compname);
		List<NewsVO>newsFordate=nb.getNewsByDate(date);
		HashTagGenerator htg=new HashTagGenerator();
		NewsStatHelper nsh=new NewsStatHelper(newsFordate);
		Map<String,Integer>compStat=new HashTagGenerator().getTopNFromMap(nsh.newsCompStat(),20);
		Map<String,Integer>comptags=htg.hashTagsFromNewsList(res);
		Map<String,Integer>map_res=htg.getTopNFromMap(comptags, 20);
		List<Object>container=new ArrayList<>();
		container.add(res); container.add(compStat);container.add(map_res);		
		Gson gson = new Gson();		
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(container));
		} catch (IOException e) {
			e.printStackTrace();
		}									
	}
	//워드 클라우드에서 선택된 키워드와 옵션적으로 선택되는 날짜를 기반으로 뉴스를 가져와, 화면에 표시될 데이터를 동적으로 바꾸어 줄 수 있는 메소드(AJAX)
	@RequestMapping(value = "/compnewstag.do", method = RequestMethod.POST)
	@ResponseBody
	public void getCompTagNews(HttpServletRequest request, HttpServletResponse response) {
		String date=request.getParameter("datechoice");
		String compname=request.getParameter("compname");
		String tagClicked=request.getParameter("tag_clicked");
		List<NewsVO>res=new ArrayList<>();
		if(!date.contains("#")) {
			res=nb.newsSearchFromDateAndTag(date, compname);
		}else {
			String day_start = request.getParameter("daybegin").replaceAll("-",".");
			String day_end=request.getParameter("dayend").replaceAll("-",".");
			res=nb.getNewsBetweenDatesWithTag(day_start, day_end, compname);
		}		
		List<NewsVO>tag_res=new ArrayList<>();
		for(NewsVO nv:res) {
			if(nv.getNews_body().contains(tagClicked)) {
				tag_res.add(nv);
			}
		}
		Gson gson = new Gson();
		String newsJson=gson.toJson(tag_res);		
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(tag_res));
		} catch (IOException e) {
			e.printStackTrace();
		}							
	}
}
