package com.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import com.vo.NewsSeqVO;
import com.vo.NewsVO;

import oracle.jdbc.OracleTypes;

@Repository("newsdao")
public class NewsDAOImpl implements NewsDAO{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void insertBatch(List<NewsVO> newslist){
		  String sql = "INSERT INTO news " +
		    "(news_no,news_sid,news_title,news_date,news_summ,news_body,news_pub,news_url,news_imgurl) "
		    + "VALUES (news_seq.nextval,?,?,?,?,?,?,?,?)";
		  LobHandler lobHandler=new DefaultLobHandler();
		  jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
		    @Override
		    public void setValues(PreparedStatement ps, int i) throws SQLException {		    	
		        NewsVO nv = newslist.get(i);
		        ps.setString(1, nv.getNews_sid());
		        ps.setString(2, nv.getNews_title());
		        ps.setString(3, nv.getNews_date());
		        ps.setString(4, nv.getNews_summ());
		        lobHandler.getLobCreator().setClobAsString(ps, 5, nv.getNews_body());
		        ps.setString(6, nv.getNews_pub());
		        ps.setString(7, nv.getNews_url());
		        ps.setString(8, nv.getNews_imgurl());		        
		    }
		    @Override
		    public int getBatchSize() {
		        return newslist.size();
		    }
		  });
		}
	
	@Override
	public List<NewsVO>getNewsByDate(String date){
		String sql="select * from news where news_date like ? order by news_date desc";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] {date+"%"});
	}
		
	@Override
	public List<NewsVO>getNewsByDateAndSid(String date,String sid){
		String sql="select * from news where news_date like ? "
				+ "and news_sid=? order by news_date desc";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] {date+"%",sid});
	}
	
	@Override
	public List<NewsVO>getNewsBetweenDates(String from,String to){
		String sql="select * from news where news_date between ? and ?";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] {from+"@0000",to+"@2359"});
	}
	@Override
	public List<NewsVO>getNewsBetweenDatesWithTag(String from,String to,String tag){
		String sql="select * from news where news_date between ? and ? and news_body like ?";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] {from+"@0000",to+"@2359","%"+tag+"%"});
	}	
	@Override
	public List<NewsVO>getAllNews(){
		String sql="select * from news";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}	
	@Override
	public List<NewsVO>newsSearchFromDateAndTag(String date,String tag){
		String sql="select * from news where news_date like ? and news_body like ?";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] {date+"%","%"+tag+"%"});
	}
	
	@Override
	public int countScrapNews(String userNo,String newsNo) {
		String query="select count(*) from scrap where user_no=? and news_no=?";
		return jdbcTemplate.queryForObject(query, new Object[] { userNo, newsNo }, Integer.class).intValue();		
	}
	@Override
	public int countScrap(int newsNo) {
		String query="select count(*) from scrap where user_no=? and news_no=?";
		return jdbcTemplate.queryForObject(query, new Object[] {newsNo }, Integer.class).intValue();	
	}
	
	@Override
	public List<NewsVO> getRelNews(int newsno) {
		String sql="select * from news where news_no in "
				+ "(select * from (select next_news_no from newssequence "
				+ "where prev_news_no=? order by rel_count desc)"
				+ "where rownum<=5)";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, new Object[] { newsno }, rowMapper);
	}
	@Override
	public String getMaxNewsDate() {
		String sql="select max(news_date) from news";
		return (String) jdbcTemplate.queryForObject(sql,String.class);
	}
		
	@Override
	public List<NewsVO>newsSearchFromDateAndTagAndSid(String date,String tag,String sid){
		String sql="select * from news where news_date like ? and news_body like ? and news_sid=?";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] {date+"%","%"+tag+"%",sid});
	}
	@Override
	public List<NewsVO>newsSearchFromDateAndPub(String date,String pub){
		String sql="select * from news where news_date like ? and news_pub=?";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] {date+"%",pub});
	}
	
	@Override
	public List<NewsVO> getViewNews(String userNo) {
		String sql="select * from news where news_no in (select news_no from newsview where user_no=?)";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(sql, new Object[] { userNo }, rowMapper);
	}
		
	@Override
	public List<NewsVO> getRealNews(int newsNo) {
		String realNews = "select * from news where news_no = ?";
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		return jdbcTemplate.query(realNews, rowMapper, new Object[] { newsNo });
	}

	@Override
	public List<NewsVO> getScrapNews(String userNo) {
		RowMapper<NewsVO> rowMapper = new NewsVORowMapper();
		String viewScrapNews ="select * from news where news_no in (select news_no from scrap where user_no=?)";
		return jdbcTemplate.query(viewScrapNews, new Object[] { userNo }, rowMapper);
	}
	
	@Override
	public int doScrapNews(String userNo, String newsNo) {
		String addScrapNews = "insert into scrap values(?,?)";
		return jdbcTemplate.update(addScrapNews, new Object[] { userNo, newsNo });
	}
	
	@Override
	public int deleteScrapNews(String userNo, String newsNo) {
		String delScrapNews = "delete from scrap where user_no = ? and news_no = ?";
		return jdbcTemplate.update(delScrapNews, new Object[] { userNo, newsNo });
	}

	public class NewsVORowMapper implements RowMapper<NewsVO> {
		@Override
		public NewsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			NewsVO nv = new NewsVO();
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
			return nv;
		}

	}
		
	
	
}
