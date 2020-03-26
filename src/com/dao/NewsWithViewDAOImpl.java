package com.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import com.dao.NewsDAOImpl.NewsVORowMapper;
import com.vo.NewsWithViewVO;


@Repository("newswithviewdao")
public class NewsWithViewDAOImpl implements NewsWithViewDAO{
	@Autowired
	private JdbcTemplate jdbcTemplate;	
	@Override
	public List<NewsWithViewVO> getMostViewNews(String date) {
		String sql="select * from (select a.news_no,a.news_sid,a.news_title,a.news_date,a.news_summ,a.news_body,a.news_pub,"
				+ "a.news_url,a.news_imgurl,b.cnt"
				+ " from news a, viewcnt b where a.news_no=b.news_no and a.news_date like ? order by b.cnt desc) where rownum<=5";
		RowMapper<NewsWithViewVO> rowMapper = new NewsWithViewVORowMapper();
		return jdbcTemplate.query(sql, new Object[] {date+"%" }, rowMapper);
	}
	//Method Overloading
	@Override
	public List<NewsWithViewVO> getMostViewNews() {
		String sql="select * from (select a.news_no,a.news_sid,a.news_title,a.news_date,a.news_summ,a.news_body,a.news_pub,"
				+ "a.news_url,a.news_imgurl,b.cnt"
				+ " from news a, viewcnt b where a.news_no=b.news_no order by b.cnt desc) where rownum<=10";
		RowMapper<NewsWithViewVO> rowMapper = new NewsWithViewVORowMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	public class NewsWithViewVORowMapper implements RowMapper<NewsWithViewVO> {
		@Override
		public NewsWithViewVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			NewsWithViewVO nv = new NewsWithViewVO();
			LobHandler lobHandler=new DefaultLobHandler();
			nv.setNews_no(rs.getInt("news_no"));
			nv.setNews_sid(rs.getString("news_sid"));		
			nv.setNews_title(rs.getString("news_title"));
			nv.setNews_date(rs.getString("news_date"));
			nv.setNews_summ(rs.getString("news_summ"));
			nv.setNews_body(lobHandler.getClobAsString(rs,"news_body"));
			nv.setNews_pub(rs.getString("news_pub"));			
			nv.setNews_url(rs.getString("news_url"));
			nv.setNews_imgurl(rs.getString("news_imgurl"));
			nv.setViewcnt(rs.getInt("cnt"));
			return nv;
		}

	}
}
