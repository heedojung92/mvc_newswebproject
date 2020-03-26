package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.biz.MemberBiz;
import com.biz.NewsBiz;
import com.google.gson.Gson;
import com.util.HashTagGenerator;
import com.util.NewsStatHelper;
import com.vo.MemberVO;
import com.vo.NewsVO;

@Controller
public class MyPageController {
	static MemberBiz mb = new MemberBiz();
	static NewsStatHelper nsh = new NewsStatHelper();
	static NewsBiz nb = new NewsBiz();
	

	// 유저가 마이페이지에서 스크랩 취소를 선택했을 시, AJAX를 통하여 동적으로 데이터를 업데이트 한다.
	@RequestMapping(value = "/updatescrap.do", method = RequestMethod.POST)
	@ResponseBody
	public void updateScrap(HttpServletRequest request, HttpServletResponse response) {
		String userNo = String.valueOf(((MemberVO) request.getSession().getAttribute("signedUser")).getUser_no());
		String newsNo = request.getParameter("newsno");
		int r = new NewsBiz().deleteScrapNews(userNo, newsNo);
		MemberVO mv = (MemberVO) request.getSession().getAttribute("signedUser");
		List<NewsVO> scrapNews = nb.getScrapNews(String.valueOf(mv.getUser_no()));
		Gson gson = new Gson();
		List<Object> container = new ArrayList<>();
		Map<String, Integer> scrapStat = new HashMap<>();
		for (NewsVO newsview : scrapNews) {
			String category = nsh.sidConverter(newsview.getNews_sid());
			if (scrapStat.containsKey(category)) {
				scrapStat.put(category, scrapStat.get(category) + 1);
			} else {
				scrapStat.put(category, 1);
			}
		};
		Map<String,Integer>scrapKeyWord=new HashTagGenerator().NhashTagsFromNewsList(scrapNews, Math.min(scrapNews.size()*8, 80));
		// 업데이트된 스크랩뉴스들과 (카테고리별)스크랩 뉴스 통계 Model 전달
		container.add(scrapNews);
		container.add(scrapStat);
		container.add(scrapKeyWord);
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(gson.toJson(container));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 뉴스 본문페이지에서 스크랩 취소를 선택했을 시, 매핑되는 컨트롤러 메소드
	@RequestMapping(value = "/delscrap.do", method = RequestMethod.POST)
	@ResponseBody
	public void deleteScrap(HttpServletRequest request, HttpServletResponse response) {
		String userNo = String.valueOf(((MemberVO) request.getSession().getAttribute("signedUser")).getUser_no());
		String newsNo = request.getParameter("newsno");
		int r = nb.deleteScrapNews(userNo, newsNo);
		JSONObject obj = new JSONObject();
		obj.put("res", String.valueOf(r));
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 뉴스 본문페이지에서 뉴스스크랩을 선택했을 시, 매핑되는 컨트롤러 메소드
	@RequestMapping(value = "/doscrap.do", method = RequestMethod.POST)
	@ResponseBody
	public void doScrap(HttpServletRequest request, HttpServletResponse response) {
		String userNo = String.valueOf(((MemberVO) request.getSession().getAttribute("signedUser")).getUser_no());
		String newsNo = request.getParameter("newsno");
		int r = nb.doScrapNews(userNo, newsNo);
		JSONObject obj = new JSONObject();
		obj.put("res", String.valueOf(r));
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 유저의 마이페이지로 보내질 모델 (스크랩 뉴스 및 카테고리 별 스크랩/조회 뉴스 통계 데이터)
	@RequestMapping("/connect_mypage.do")
	public ModelAndView myPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		if (request.getSession().getAttribute("signedUser") == null) {
			if (request.getParameter("fromnewsbody") != null) {
				request.getSession().setAttribute("fromnewsbody", request.getParameter("fromnewsbody"));
			}
			mav.setViewName("member/loginpage");
		} else {
			MemberVO mv = (MemberVO) request.getSession().getAttribute("signedUser");
			HashTagGenerator htg=new HashTagGenerator();
			List<NewsVO> viewNews = nb.getViewNews(String.valueOf(mv.getUser_no()));		
			Map<String,Integer>viewKeyWord=htg.NhashTagsFromNewsList(viewNews, Math.min(viewNews.size()*4, 80));			
			
			List<NewsVO> scrapNews = nb.getScrapNews(String.valueOf(mv.getUser_no()));
			Map<String,Integer>scrapKeyWord=htg.NhashTagsFromNewsList(scrapNews, Math.min(scrapNews.size()*8, 80));	
			Gson gson = new Gson();
			mav.getModelMap().addAttribute("viewNews", gson.toJson(viewNews));
			mav.getModelMap().addAttribute("viewKeyWord", gson.toJson(viewKeyWord));
			
			
			mav.getModelMap().addAttribute("scrapNews", gson.toJson(scrapNews));
			mav.getModelMap().addAttribute("scrapKeyWord", gson.toJson(scrapKeyWord));
			Map<String, Integer> viewStat = new HashMap<>();
			Map<String, Integer> viewNewsDate = new HashMap<>();
			for (NewsVO newsview : viewNews) {
				String category = nsh.sidConverter(newsview.getNews_sid());
				if (viewStat.containsKey(category)) {
					viewStat.put(category, viewStat.get(category) + 1);
				} else {
					viewStat.put(category, 1);
				}
				String date = newsview.getNews_date().split("@")[0];
				if (viewNewsDate.containsKey(date)) {
					viewNewsDate.put(date, viewNewsDate.get(date) + 1);
				} else {
					viewNewsDate.put(date, 1);
				}
			}
			mav.getModelMap().addAttribute("viewStat", gson.toJson(viewStat));
			Map<String, Integer> scrapStat = new HashMap<>();
			for (NewsVO newsview : scrapNews) {
				String category = nsh.sidConverter(newsview.getNews_sid());
				if (scrapStat.containsKey(category)) {
					scrapStat.put(category, scrapStat.get(category) + 1);
				} else {
					scrapStat.put(category, 1);
				}
			}
			TreeMap<String, Integer> dateSort = new TreeMap<>(viewNewsDate);
			mav.getModelMap().addAttribute("viewNewsDate", gson.toJson(dateSort));
			mav.getModelMap().addAttribute("scrapStat", gson.toJson(scrapStat));
			mav.setViewName("member/mypage");
		}
		return mav;
	}

	// 로그인 체크 메소드 - 로그인 성공 시, 세션 등록
	@RequestMapping(value = "/login_check.do", method = RequestMethod.POST)
	@ResponseBody
	public void loginCheck(HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		int r = mb.loginCheck(email, password);
		MemberVO mv = null;
		if (r > 0) {
			if (email.equals("big4@gmail.com")) {
				r = 2;
			} else {
				if (request.getSession().getAttribute("fromnewsbody") != null) {
					r = Integer.valueOf((String) request.getSession().getAttribute("fromnewsbody"));
					request.getSession().removeAttribute("fromnewsbody");
				}
				mv = mb.getMemberByEmail(email);
				request.getSession().setAttribute("signedUser", mv);
			}
		}
		JSONObject obj = new JSONObject();
		obj.put("res", String.valueOf(r));
		try {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().print(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 마이페이지 - 회원정보수정 클릭시 정보수정 페이지 view로 매핑시켜주는 컨트롤러 메소드
	@RequestMapping("memberUpdate.do")
	public ModelAndView requestUpdate(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("member/memberupdate");
		return mav;
	}

	// 회원 정보 수정페이지에서 넘어온 모델들로 회원 정보를 업데이트 한다.
	@RequestMapping("updateProcess.do")
	public ModelAndView updateInfo(HttpServletRequest request) {
		String email = request.getParameter("update_email");
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		ModelAndView mav = new ModelAndView();
		int r = mb.memUpdate(password, name, email);
		if (r > 0) {
			mav.getModelMap().addAttribute("memberupdated", 1);
			request.getSession().invalidate();
			mav.setViewName("member/loginpage");
		}
		return mav;
	}
}
