package com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("viewcntdao")
public class ViewCntDAOImpl implements ViewCntDAO{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//(��������,��������) pair�� �󵵼��� ������Ʈ�ϴ� ���ν����� ->"�� ������ �� ������� ���̺� ����" �����͸� ����
	@Override
	public void updateSeq(int prev_news_no,int next_news_no) {
		String sql="{call update_newssequence(?,?)}";
		jdbcTemplate.update(sql,new Object[] {prev_news_no,next_news_no});
	}
	//������ ��ȸ���� ������Ʈ�ϴ� ���ν�����
	@Override
	public void updateView(int news_no) {
		String sql="{call update_viewcnt(?)}";
		jdbcTemplate.update(sql,new Object[] {news_no});
	}
	//Ư�� ������ ��ũ�� �� ����
	@Override
	public int countScrap(int newsNo) {
		String query="select count(*) from scrap where news_no=?";
		return jdbcTemplate.queryForObject(query, new Object[] { newsNo }, Integer.class).intValue();		
	}
	//Ư�� ������ ��ȸ�� ����
	@Override
	public int countView(int newsNo) {
		String query="select cnt from viewcnt where news_no=?";
		return jdbcTemplate.queryForObject(query, new Object[] { newsNo }, Integer.class).intValue();	
	}	
}
