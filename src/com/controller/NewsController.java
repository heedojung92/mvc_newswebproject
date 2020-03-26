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
	//���� ������������ ���ν��� �ִ� �޼ҵ�
	@RequestMapping("/news_main.do")
	public ModelAndView mainnewspage(HttpServletRequest request) {
		new AutoUpdate().updateNewsAndTag();
		ModelAndView mav = new ModelAndView();
		String todayAsString=new DateStringHelper().getCurrentDate();				
		NewsWithViewBiz nvb=new NewsWithViewBiz();
		HashTagGenerator htg=new HashTagGenerator();
		HashTagBiz htb=new HashTagBiz();		
		//���� ���� �󵵼��� ���� Ű����(�ؽ� �±�)���� ���踦 ����.
		Map<String,Integer>hashtagmap=htg.getTopNFromMap(htb.getTagByDate(todayAsString).getTotal_tags(),50);
		//���� ���� ���� ��ȸ �� ������ ���ϱ� ���� �޼ҵ� 
		List<NewsWithViewVO>mostViewToday=nvb.getMostViewNews(todayAsString);
		//�ֽ� �������� ���ϱ� ���� �޼ҵ�
		List<NewsVO>todayNews=nb.getNewsByDate(todayAsString);
		//Gson ��ü :Java ��ü(List ����)���� View�� ����(.jsp)�� ��, JSONŸ������ ��ȯ�Ͽ� ������ �� �ִ�.
		Gson gson = new Gson();			
		mav.getModelMap().addAttribute("hashtag",gson.toJson(hashtagmap));
		mav.getModelMap().addAttribute("recentNews", todayNews);
		mav.getModelMap().addAttribute("mostviewedtoday",gson.toJson(mostViewToday));
		mav.setViewName("news/news_mainpage");
		return mav;
	}
	//���� �˻� ��� �������� ���ν����ִ� �޼ҵ�
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
		String searchType="Ű����";
		List<NewsVO>res=new ArrayList<>();
		String cat=query.split("-")[0].trim();
		String val=query.split("-")[1].trim();	
		if(date!=null) {						
			if(cat.contains("�о�")) {
				searchType="ī�װ�";
				String sid=new NewsStatHelper().catConverter(val);
				res=nb.getNewsByDateAndSid(date, sid);
			}else if(cat.contains("���")||cat.contains("�˻�")){
				if(date.contains("-")) {
					date=date.replaceAll("-", ".");
				}
				if(cat.contains("�˻�")) {
					searchType="�˻���";
					res=nb.newsSearchFromDateAndTag(date, val);
				}else {
					searchType="���";
					res=nb.newsSearchFromDateAndTag(date, val);
				}				
			}else {
				searchType="��л�";
				res=nb.newsSearchFromDateAndPub(date, val);
			}
		}
		//�˻��� �����̵Ǵ� ������ �Ⱓ�� �Ϸ� �̻��� ���
		else {
			if(dateStart.contains("-")) {
				dateStart=dateStart.replaceAll("-", ".");
				dateEnd=dateEnd.replaceAll("-", ".");
			}
			List<NewsVO>betweendates=nb.getNewsBetweenDates(dateStart.trim(),dateEnd.trim());
			if(cat.contains("�о�")) {
				searchType="ī�װ�";
				String sid=new NewsStatHelper().catConverter(val);
				for(NewsVO nv:betweendates) {
					if(nv.getNews_sid().equals(sid)) {
						res.add(nv);
					}
				}
			}else if(cat.contains("���")||cat.contains("�˻�")){
				if(cat.contains("�˻�")) {
					searchType="�˻���";
					res=nb.getNewsBetweenDatesWithTag(dateStart.trim(),dateEnd.trim(),val);
				}else {
					searchType="���";
					res=nb.getNewsBetweenDatesWithTag(dateStart.trim(),dateEnd.trim(),val);
				}				
			}else {
				searchType="��л�";
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
