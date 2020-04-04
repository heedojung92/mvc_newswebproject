package com.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.biz.NewsBiz;
import com.google.gson.Gson;
import com.util.DateStringHelper;
import com.util.NewsStatHelper;
import com.vo.NewsVO;

@Controller
public class StatController {
	static NewsBiz nb = new NewsBiz();
	NewsStatHelper nsh=NewsStatHelper.getInstance();
	
	@RequestMapping("/news_stat.do")
	public ModelAndView newsstat(HttpServletRequest request) {
		List<NewsVO>todayNews=nb.getNewsByDate(new DateStringHelper().getCurrentDate());
		nsh.setNewsList(todayNews);
		Map<String,Integer>newsPub=nsh.getTopN(nsh.newsPubStat(), 15);
		Map<String,Integer>newsCat=nsh.getTopN(nsh.newsCatStat(), 15);
		newsCat.remove(null);
		Map<String,Integer>newsComp=nsh.getTopN(nsh.newsCompStat(), 15);				
		ModelAndView mav = new ModelAndView();		
		Gson gson = new Gson();
		String pubstat = gson.toJson(newsPub);			
		mav.getModelMap().addAttribute("pubStat", pubstat);
		mav.getModelMap().addAttribute("catStat", gson.toJson(newsCat));
		mav.getModelMap().addAttribute("compStat", gson.toJson(newsComp));
		mav.setViewName("news/news_statpage");
		return mav;
	}
	
	@RequestMapping(value = "/time_series_analysis.do", method = RequestMethod.POST)
	@ResponseBody
	public void timeSeriesData(HttpServletRequest request, HttpServletResponse response) {
		String data_choice=request.getParameter("data_clicked");
		String chart_choice=request.getParameter("chart");
		String today=new DateStringHelper().getCurrentDate();
		String aMonthBefore=new DateStringHelper().nDaysBefore(today, 30);
		List<NewsVO>news_history=nb.getNewsBetweenDates(aMonthBefore, today);
		nsh.setNewsList(news_history);		
		Map<String, Integer> time_series_data=nsh.getTimeSeriesData(data_choice, chart_choice);		
		JSONObject obj=new JSONObject();
		obj.put("time_data",time_series_data);
		try {
			response.setContentType("application/json;charset=utf-8");		
			response.getWriter().print(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
