package com.vo;

public class StockVO {
	private String comp_name;
	private String st_date;
	private int open_price;
	private int high_price;
	private int low_price;
	private int close_price;
	public String getComp_name() {
		return comp_name;
	}
	public int getClose_price() {
		return close_price;
	}
	public void setClose_price(int close_price) {
		this.close_price = close_price;
	}
	public void setComp_name(String comp_name) {
		this.comp_name = comp_name;
	}
	public String getSt_date() {
		return st_date;
	}
	public void setSt_date(String st_date) {
		this.st_date = st_date;
	}
	public int getOpen_price() {
		return open_price;
	}
	public void setOpen_price(int open_price) {
		this.open_price = open_price;
	}
	public int getHigh_price() {
		return high_price;
	}
	public void setHigh_price(int high_price) {
		this.high_price = high_price;
	}
	public int getLow_price() {
		return low_price;
	}
	public void setLow_price(int low_price) {
		this.low_price = low_price;
	}
		


}
