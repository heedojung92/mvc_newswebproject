package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.biz.HashTagBiz;
import com.biz.NewsBiz;
import com.google.gson.Gson;
import com.util.DateStringHelper;
import com.util.HashTagGenerator;
import com.util.NewsStatHelper;
import com.vo.HashTagVO;
import com.vo.NewsVO;

@Controller
public class HashTagController {
	static NewsBiz nb = new NewsBiz();
	static HashTagBiz htb=new HashTagBiz();
	NewsStatHelper nsh=NewsStatHelper.getInstance();
	HashTagGenerator htg = HashTagGenerator.getInstance();
	static DateStringHelper dsh=new DateStringHelper();
	
	

	// 키워드(해시태그) 분석 메인페이지로 연결 시켜주는 컨트롤러 메소드
	@RequestMapping("/hashtag_relation.do")
	public ModelAndView hashtagRelation(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("news/hashtag_relation");
		return mav;
	}


	@RequestMapping(value = "/moreNode.do", method = RequestMethod.POST)
	@ResponseBody
	public void moreNode(HttpServletRequest request, HttpServletResponse response) {
		String start_date=request.getParameter("date_start").replaceAll("-", ".");
		String end_date=request.getParameter("date_end").replaceAll("-", ".");
		String tag=request.getParameter("tag_clicked");
		List<NewsVO>searchres=nb.getNewsBetweenDatesWithTag(start_date, end_date, tag);
		//SparkRDD를 이용한 Map-Reduce 작업실시
		Map<String,Integer>rel_tags=htg.hashTagsFromNewsListSpark(searchres);
		
		Map<String,Integer>topSix=htg.getTopNFromMap(rel_tags, 8);
		topSix.remove(tag);
		List<String>tags=new ArrayList<>();
		tags.addAll(topSix.keySet());
		Gson gson=new Gson();
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(tags));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
				
	@RequestMapping(value = "/addNode.do", method = RequestMethod.POST)
	@ResponseBody
	public void addNode(HttpServletRequest request, HttpServletResponse response) {
		String start_date = request.getParameter("date_start").replaceAll("-", ".");
		String end_date = request.getParameter("date_end").replaceAll("-", ".");
		String tag = request.getParameter("tag_chosen").replaceAll("#", "").trim();
		List<NewsVO>searchres=nb.getNewsBetweenDatesWithTag(start_date, end_date, tag);
		Map<String,Integer>rel_tags=htg.hashTagsFromNewsListSpark(searchres);
		Map<String,Integer>topSix=htg.getTopNFromMap(rel_tags, 8);
		topSix.remove(tag);

		List<String>tags=new ArrayList<>();
		tags.addAll(topSix.keySet());
		for(String t:tags) {
			System.out.println(t);
		}
		Gson gson=new Gson();
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(tags));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//키워드 분석 페이지에서, 유저가 날짜를 선택했을 시, 그기간 동안 가장 많이 나왔던 키워드를 집계내어 보내준다.
	@RequestMapping(value = "/recommendtag.do", method = RequestMethod.POST)
	@ResponseBody
	public void recommendtag(HttpServletRequest request, HttpServletResponse response) {
		String start_date = request.getParameter("date_start").replaceAll("-", ".");
		String end_date = request.getParameter("date_end").replaceAll("-", ".");
		List<HashTagVO> tags=htb.getTagsByDate(start_date,end_date);
		List<Map<String,Integer>>res=new ArrayList<>();
		for(HashTagVO htv:tags) {
			res.add(htv.getTotal_tags());
		}
		Map<String, Integer> cnt = htg.mergeMap(res);
		Map<String, Integer> sorted=htg.getTopNFromMap(cnt,30);
		JSONObject obj = new JSONObject();
		obj.put("res", sorted);
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 그래프의 엣지를 클릭하였을 때, 엣지가 연결하고 있던 두 키워드 노드가 등장하는 뉴스데이터를 전송한다 - AJAX
	@RequestMapping(value = "/news_from_tagedge.do", method = RequestMethod.POST)
	@ResponseBody
	public void newsFromTagEdge(HttpServletRequest request, HttpServletResponse response) {
		String start_date = request.getParameter("date_start").replaceAll("-", ".");
		String end_date = request.getParameter("date_end").replaceAll("-", ".");
		String tag_one=request.getParameter("tag_one");
		String tag_two=request.getParameter("tag_two");
		List<NewsVO>tagOne=nb.getNewsBetweenDatesWithTag(start_date, end_date, tag_one);
		String target=tag_two.replaceAll(" ", "");
		tagOne.stream().filter(nv->nv.getNews_body().replaceAll(" ", "").contains(target));
		Gson gson=new Gson();
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(tagOne));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
