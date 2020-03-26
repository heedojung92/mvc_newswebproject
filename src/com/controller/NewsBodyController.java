package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.biz.MemberBiz;
import com.biz.NewsBiz;
import com.biz.NewsViewBiz;
import com.biz.ViewCntBiz;
import com.google.gson.Gson;
import com.util.HashTagGenerator;
import com.util.NewsSimilarityHelper;
import com.vo.MemberVO;
import com.vo.NewsVO;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

@Controller
public class NewsBodyController {
	static NewsBiz nb = new NewsBiz();
	static ViewCntBiz vcb=new ViewCntBiz();

	//뉴스 본문 페이지로 연결되는 컨트롤러 메소드
	@RequestMapping("newsbody.do")
	public ModelAndView viewNewsBody(HttpServletRequest request) {	
		String newsNo=request.getParameter("newsno");
		int current = Integer.valueOf(newsNo);
		new NewsViewBiz().updateView(current);
		int viewcnt=vcb.countView(current);
		int scrapcnt=vcb.countScrap(current);
		String prev = (String) request.getSession().getAttribute("prevnews");
		if (prev == null) {
			request.getSession().setAttribute("prevnews", newsNo);
		} else {
			int prevno = Integer.valueOf(prev);
			if (current != prevno) {
				vcb.updateSeq(prevno, current);
			}
		}		
		request.getSession().setAttribute("prevnews", newsNo);				
		NewsVO nv = nb.getRealNews(current).get(0);
		Map<String,Integer>hashtagdict=new HashTagGenerator().getHashtags(nv);
		Set<String>tags=hashtagdict.keySet();
		List<String>hashtags=new ArrayList<>();hashtags.addAll(tags);
		Gson gson = new Gson();		
		List<NewsVO> rellist=nb.getRelNews(current);								
		ModelAndView mav = new ModelAndView();		
		NewsSimilarityHelper nsh=new NewsSimilarityHelper();	
		
		Set<NewsVO>cossimNews=nsh.cosSimNews(nv);		
		Set<NewsVO>jaccardsimNews=nsh.jaccardSimNews(nv);	
		Set<NewsVO>euclidsimNews=nsh.closeAccToEuclidean(nv);		
		Set<NewsVO>manhattansimNews=nsh.closeAccToManhattan(nv);
		
		if(request.getSession().getAttribute("signedUser")!=null) {
			mav.getModelMap().addAttribute("isUser","true");
			MemberBiz mb=new MemberBiz();
			int memberNo=((MemberVO)request.getSession().getAttribute("signedUser")).getUser_no();
			mb.updateview(current, memberNo);
			if(new NewsBiz().countScrapNews(String.valueOf(memberNo),newsNo)!=0) {
				mav.getModelMap().addAttribute("scrap","true");
			}
		}else {
			mav.getModelMap().addAttribute("isUser","false");
		}
		mav.setViewName("news/newsbody");		
		mav.addObject("newschoice", nv);
		mav.getModelMap().addAttribute("relnews", rellist);
		mav.getModelMap().addAttribute("hashtag", hashtags);
		mav.getModelMap().addAttribute("tagdict", gson.toJson(hashtagdict));
		mav.getModelMap().addAttribute("viewcnt",String.valueOf(viewcnt));
		mav.getModelMap().addAttribute("scrapcnt",String.valueOf(scrapcnt));
		mav.getModelMap().addAttribute("news", gson.toJson(nv));				
		mav.getModelMap().addAttribute("cossimnews",gson.toJson(cossimNews));
		mav.getModelMap().addAttribute("euclidsimnews",gson.toJson(euclidsimNews));
		mav.getModelMap().addAttribute("jaccardsimnews",gson.toJson(jaccardsimNews));		
		mav.getModelMap().addAttribute("manhattansimnews",gson.toJson(manhattansimNews));
		return mav;
	}
	/*
	 * 뉴스 본문을 형태소 분석하여, 명사만 뽑아 빈도수를 리턴한다. 
	 * Map<키워드,빈도수> 형태로 리턴받은 데이터는 view 화면에서 워드클라우드를 통해 시각화된다 - via AJAX 
	 */
	@RequestMapping(value = "/wordcloud.do", method = RequestMethod.POST)
	@ResponseBody
	public void recommendtag(HttpServletRequest request, HttpServletResponse response) {
		HashTagGenerator htg=new HashTagGenerator();
		String newscontent=request.getParameter("news_content");
		String newstitle=request.getParameter("news_title");
		
		NewsVO nv=new NewsVO();
		nv.setNews_title(newstitle);nv.setNews_body(newscontent);
		//형태소 분석
	    Set<String>distinct_nouns=htg.morphologicalAnalysis(nv);
	    Map<String,Integer>tag_dict=new HashMap<>();
	    Gson gson = new Gson();	
	    String bodyFind=newscontent.replaceAll(" ", "");
	    for(String noun:distinct_nouns) {
	    	String wordFind=noun.replaceAll(" ", "");
	    	if(noun.length()>1) {
	    		int cnt=StringUtils.countMatches(bodyFind, wordFind);
				if(cnt>1) {
					tag_dict.put(noun,cnt);
					}
	    	}	
	    }
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(tag_dict));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
