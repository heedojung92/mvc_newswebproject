package com.vo;
//필요없는 클래스
public class NewsSeqVO {
	private int prev_news_no;
	private int next_news_no;
	private int rel_count;
	
	public int getRel_count() {
		return rel_count;
	}
	public void setRel_count(int rel_count) {
		this.rel_count = rel_count;
	}
	public int getPrev_news_no() {
		return prev_news_no;
	}
	public void setPrev_news_no(int prev_news_no) {
		this.prev_news_no = prev_news_no;
	}
	public int getNext_news_no() {
		return next_news_no;
	}
	public void setNext_news_no(int next_news_no) {
		this.next_news_no = next_news_no;
	}

	

}
