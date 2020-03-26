package com.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.vo.StockVO;

@Repository("stockdao")
public class StockDAOImpl implements StockDAO{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public List<StockVO> getStockList(String compName, String dayStart, String dayEnd) {
		String sql="select * from stock where comp_name=? and st_date between ? and ?";
		RowMapper<StockVO>mapper=new StockVORowMapper();
		return jdbcTemplate.query(sql, mapper, new Object[] {compName,dayStart,dayEnd});
	}
	@Override
	public String getMaxDate() {
		String sql="select max(st_date) from stock";
		return (String)jdbcTemplate.queryForObject(sql,String.class);
	}
	public class StockVORowMapper implements RowMapper<StockVO> {
		@Override
		public StockVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			StockVO sv = new StockVO();			
			sv.setComp_name(rs.getString("comp_name"));
			sv.setSt_date(rs.getString("st_date"));
			sv.setOpen_price(rs.getInt("open_price"));
			sv.setHigh_price(rs.getInt("high_price"));
			sv.setLow_price(rs.getInt("low_price"));
			sv.setClose_price(rs.getInt("close_price"));
			return sv;
		}
	}
	@Override
	public void insertBatch(List<StockVO> stocklist){
		  String sql = "INSERT INTO stock values(?,?,?,?,?,?)";
		  jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
		    @Override
		    public void setValues(PreparedStatement ps, int i) throws SQLException {
		    	StockVO sv=stocklist.get(i);
		    	 ps.setString(1,sv.getComp_name());
		    	 ps.setString(2,sv.getSt_date());
		    	 ps.setInt(3, sv.getOpen_price());
		    	 ps.setInt(4, sv.getHigh_price());
		    	 ps.setInt(5, sv.getLow_price());
		    	 ps.setInt(6, sv.getClose_price());		  
		    }
		    @Override
		    public int getBatchSize() {
		        return stocklist.size();
		    }
		  });
		}
}
