package com.dao;

import java.util.List;

import com.vo.NewsWithViewVO;

public interface NewsWithViewDAO {
	List<NewsWithViewVO> getMostViewNews(String date);

	List<NewsWithViewVO> getMostViewNews();

}
