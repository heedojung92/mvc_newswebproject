package com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("viewcntdao")
public class ViewCntDAOImpl implements ViewCntDAO{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//(이전뉴스,다음뉴스) pair의 빈도수를 업데이트하는 프로시져문 ->"이 뉴스를 본 사람들이 많이본 뉴스" 데이터를 위함
	@Override
	public void updateSeq(int prev_news_no,int next_news_no) {
		String sql="{call update_newssequence(?,?)}";
		jdbcTemplate.update(sql,new Object[] {prev_news_no,next_news_no});
	}
	//뉴스의 조회수를 업데이트하는 프로시져문
	@Override
	public void updateView(int news_no) {
		String sql="{call update_viewcnt(?)}";
		jdbcTemplate.update(sql,new Object[] {news_no});
	}
	//특정 뉴스의 스크랩 수 리턴
	@Override
	public int countScrap(int newsNo) {
		String query="select count(*) from scrap where news_no=?";
		return jdbcTemplate.queryForObject(query, new Object[] { newsNo }, Integer.class).intValue();		
	}
	//특정 뉴스의 조회수 리턴
	@Override
	public int countView(int newsNo) {
		String query="select cnt from viewcnt where news_no=?";
		return jdbcTemplate.queryForObject(query, new Object[] { newsNo }, Integer.class).intValue();	
	}	
}
