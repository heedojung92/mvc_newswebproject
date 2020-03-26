package com.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsVO implements Serializable{
	private int news_no;
	private String news_date;
	private String news_sid;
	private String news_title;
	private String news_summ;
	private String news_body;
	private String news_url;
	private String news_imgurl;
	private String news_pub;		
	public NewsVO() {
		super();
	}
	public int getNews_no() {
		return news_no;
	}
	public void setNews_no(int news_no) {
		this.news_no = news_no;
	}
	public String getNews_date() {
		return news_date;
	}
	public void setNews_date(String news_date) {
		this.news_date = news_date;
	}
	public String getNews_sid() {
		return news_sid;
	}
	public void setNews_sid(String news_sid) {
		this.news_sid = news_sid;
	}
	public String getNews_title() {
		return news_title;
	}
	public void setNews_title(String news_title) {
		this.news_title = news_title;
	}
	public String getNews_summ() {
		return news_summ;
	}
	public void setNews_summ(String news_summ) {
		this.news_summ = news_summ;
	}
	public String getNews_body() {
		return news_body;
	}
	public void setNews_body(String news_body) {
		this.news_body = news_body;
	}
	public String getNews_url() {
		return news_url;
	}
	public void setNews_url(String news_url) {
		this.news_url = news_url;
	}
	public String getNews_imgurl() {
		return news_imgurl;
	}
	public void setNews_imgurl(String news_imgurl) {
		this.news_imgurl = news_imgurl;
	}
	public String getNews_pub() {
		return news_pub;
	}
	public void setNews_pub(String news_pub) {
		this.news_pub = news_pub;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((news_title == null) ? 0 : news_title.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewsVO other = (NewsVO) obj;
		if (news_title == null) {
			if (other.news_title != null)
				return false;
		} else if (!news_title.equals(other.news_title))
			return false;
		return true;
	}	
		
}
