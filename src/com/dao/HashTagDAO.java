package com.dao;

import java.util.List;

import com.vo.HashTagVO;

public interface HashTagDAO {
	public int insertOne(HashTagVO htv);
	public void insertBatch(List<HashTagVO> taglist);	
	public List<HashTagVO> getTagsByDate(String from,String to);
	public HashTagVO getTagByDate(String date);

}
