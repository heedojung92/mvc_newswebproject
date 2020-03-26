package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.biz.HashTagBiz;
import com.biz.NewsBiz;
import com.google.gson.Gson;
import com.util.DateStringHelper;
import com.vo.HashTagVO;
import com.vo.NewsVO;

@Controller
public class CategoryNewsController {
	static NewsBiz nb = new NewsBiz();	
	//카테고리 뉴스 메인페이지로 매핑시켜주는 메소드
	@RequestMapping("/cat_news.do")
	public ModelAndView catnews(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashTagBiz htb=new HashTagBiz();
		List<NewsVO> categoryNews = new ArrayList<>();
		String todayAsString=new DateStringHelper().getCurrentDate();
		String[] sids = { "259101", "258101", "261101", "771101", "262101", "263101", "226105", "230105" };
		Gson gson = new Gson();
		for (String sid : sids) {
			categoryNews = new ArrayList<>();
			 List<NewsVO> sub=nb.getNewsByDateAndSid(todayAsString, sid);
			for(int i=0;i<8&&i<sub.size();i++) {
				categoryNews.add(sub.get(i));
			}
			mav.getModelMap().addAttribute(sid, categoryNews);
		}
		HashTagVO htv=htb.getTagByDate(todayAsString);
		mav.getModelMap().addAttribute("tagdict",gson.toJson(htv));		
		mav.getModelMap().addAttribute("recentCategoryNews", categoryNews);
		mav.setViewName("news/category_news_mainpage");
		return mav;
	}
	@RequestMapping(value = "/cat_news_from_tag.do", method = RequestMethod.POST)
	@ResponseBody
	public void catNewsFromTagChoice(HttpServletRequest request, HttpServletResponse response) {
		String tag_choice = request.getParameter("tag_clicked").trim();
		String sid_choice = request.getParameter("sid_choice");

		Gson gson = new Gson();
		List<NewsVO> res = nb.newsSearchFromDateAndTagAndSid(new DateStringHelper().getCurrentDate(), tag_choice, sid_choice);
		Map<Integer, String> newsNotitle = new HashMap<>();
		for (int i=0;i<res.size()&&i<8;i++) {
			newsNotitle.put(res.get(i).getNews_no(),res.get(i).getNews_title());
		};
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(newsNotitle));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//카테고리뉴스 메인페이지에서 더보기를 클릭하였을 때, 카테고리 뉴스페이지로 연결 시켜주는 메소드
	@RequestMapping("/news_in_category.do")
	public ModelAndView newsincategory(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String category=request.getParameter("cat");
		Gson gson = new Gson();
		List<NewsVO> sub = nb.getNewsByDateAndSid(new DateStringHelper().getCurrentDate(), category);				
		mav.getModelMap().addAttribute("category", category);
		mav.getModelMap().addAttribute("category_news", gson.toJson(sub));
		mav.setViewName("news/category_news");
		return mav;
	}
	//카테고리 뉴스(더보기) 페이지에서 날짜를 선택하였을 시, 보여지는 화면을 동적으로 바꾸어 줄 수 있게 해주는 메소드(via AJAX)
	@RequestMapping(value = "/generate_catnews.do", method = RequestMethod.POST)
	@ResponseBody
	public void catNewsFromTag(HttpServletRequest request, HttpServletResponse response) {
		String category = request.getParameter("category");
		String date_choice = request.getParameter("date_choice").replaceAll("-",".");
		List<NewsVO> recentNews = nb.getNewsByDateAndSid(date_choice, category);
		Gson gson = new Gson();
		String jsonstr=gson.toJson(recentNews);
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(recentNews));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
