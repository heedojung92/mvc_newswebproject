package com.dao;

public interface ViewCntDAO {
	void updateView(int news_no);

	int countScrap(int newsNo);

	int countView(int newsNo);

	void updateSeq(int prev_news_no, int next_news_no);

}
