package com.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import com.util.HashTagGenerator;
import com.vo.HashTagVO;

@Repository("hashtagdao")
public class HashTagDAOImpl implements HashTagDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static HashTagGenerator htg = new HashTagGenerator();

	@Override
	public int insertOne(HashTagVO htv) {
		String sql = "INSERT INTO hashtag "
				+ "(tag_date,finance_tags,stock_tags,industry_tags,venture_tags,globecon_tags,genecon_tags,internet_tags,it_tags,total_tags) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
		LobHandler lobHandler = new DefaultLobHandler();
		return jdbcTemplate.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, htv.getTag_date());
				lobHandler.getLobCreator().setClobAsString(ps, 2, htv.getFinance_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 3, htv.getStock_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 4, htv.getIndustry_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 5, htv.getVenture_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 6, htv.getGlobecon_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 7, htv.getGenecon_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 8, htv.getInternet_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 9, htv.getIt_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 10, htv.getTotal_tags().toString());
			}
		});
	}

	@Override
	public void insertBatch(List<HashTagVO> taglist) {
		String sql = "INSERT INTO hashtag "
				+ "(tag_date,finance_tags,stock_tags,industry_tags,venture_tags,globecon_tags,genecon_tags,internet_tags,it_tags,total_tags) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
		LobHandler lobHandler = new DefaultLobHandler();
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				HashTagVO htv = taglist.get(i);
				ps.setString(1, htv.getTag_date());
				lobHandler.getLobCreator().setClobAsString(ps, 2, htv.getFinance_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 3, htv.getStock_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 4, htv.getIndustry_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 5, htv.getVenture_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 6, htv.getGlobecon_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 7, htv.getGenecon_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 8, htv.getInternet_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 9, htv.getIt_tags().toString());
				lobHandler.getLobCreator().setClobAsString(ps, 10, htv.getTotal_tags().toString());
			}

			@Override
			public int getBatchSize() {
				return taglist.size();
			}
		});
	}

	@Override
	public List<HashTagVO> getTagsByDate(String from,String to) {
		String sql = "select * from hashtag where tag_date between ? and ?";
		HashTagVORowMapper rowMapper = new HashTagVORowMapper();
		return jdbcTemplate.query(sql, new Object[] { from+"@0000",to+"@2359" }, rowMapper);
	}
	@Override
	public HashTagVO getTagByDate(String date) {
		String sql = "select * from hashtag where tag_date= (select max(tag_date) from hashtag where tag_date like ?)";
		HashTagVORowMapper rowMapper = new HashTagVORowMapper();
		return jdbcTemplate.query(sql, rowMapper, new Object[] { date + "%" }).get(0);
	}
	
	public class HashTagVORowMapper implements RowMapper<HashTagVO> {
		@Override
		public HashTagVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			HashTagVO htv = new HashTagVO();
			LobHandler lobHandler = new DefaultLobHandler();
			htv.setTag_date(rs.getString("tag_date"));
			htv.setFinance_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "finance_tags")));
			htv.setStock_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "stock_tags")));
			htv.setIndustry_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "industry_tags")));
			htv.setVenture_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "venture_tags")));
			htv.setGlobecon_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "globecon_tags")));
			htv.setGenecon_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "genecon_tags")));
			htv.setInternet_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "internet_tags")));
			htv.setIt_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "it_tags")));
			htv.setTotal_tags(htg.stringToMap(lobHandler.getClobAsString(rs, "total_tags")));
			return htv;

		}
	}

}
