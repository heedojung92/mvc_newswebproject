package com.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import com.vo.MemberVO;

@Repository("memberdao")
public class MemberDAOImpl implements MemberDAO{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	//유저의 조회뉴스를 업데이트 시켜주는 프로시져
	@Override
	public int updateview(int userno, int newsno) {
		String sql = "{call update_userviewcnt(?,?)}";
		return jdbcTemplate.update(sql, new Object[] { userno, newsno });
	}
	@Override
	public int viewCheck(int userno, int newsno) {
		String sql = "select count(*) from newsview where user_no=? and news_no=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { userno, newsno }, Integer.class).intValue();
	}
	@Override
	public int loginCheck(String email, String pw) {
		String loginCheck="select count(*) from member where user_email=? and user_pw=?";
		return jdbcTemplate.queryForObject(loginCheck, new Object[] { email, pw }, Integer.class).intValue();
	}
	@Override
	public MemberVO getMemberByEmail(String email) {
		String sql = "select * from member where user_email=?";
		RowMapper<MemberVO> mapper = new MemberVORowMapper();
		return jdbcTemplate.queryForObject(sql, new Object[] { email }, mapper);
	}
	@Override
	public int memUpdate(String pw, String name, String email) {
		String memberUpdate = "update member set user_pw=?, user_name=? where user_email=?";
		return jdbcTemplate.update(memberUpdate, new Object[] { pw, name, email });
	}

	public class MemberVORowMapper implements RowMapper<MemberVO> {
		@Override
		public MemberVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			MemberVO mv = new MemberVO();
			mv.setUser_auth(rs.getString("user_auth"));
			mv.setUser_cityno(rs.getString("user_cityno"));
			mv.setUser_email(rs.getString("user_email"));
			mv.setUser_name(rs.getString("user_name"));
			mv.setUser_no(rs.getInt("user_no"));
			mv.setUser_pw(rs.getString("user_pw"));
			mv.setUser_reg_date(rs.getDate("user_reg_date"));
			return mv;
		}
	}	
	@Override
	public int countMember() {
		String sql="select count(*) from member";
		return jdbcTemplate.queryForObject(sql, Integer.class).intValue();
	}

}