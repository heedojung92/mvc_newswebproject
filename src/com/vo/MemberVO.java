package com.vo;

import java.util.Date;

public class MemberVO {
	private int user_no;
	private String user_email;
	private String user_pw;
	private String user_name;
	private String user_cityno;
	private Date user_reg_date;
	private String user_auth;
	private Date user_disband_date;
	
	public MemberVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MemberVO(int user_no, String user_email, String user_pw, String user_name, String user_cityno,
			Date user_reg_date, String user_auth, Date user_disband_date) {
		super();
		this.user_no = user_no;
		this.user_email = user_email;
		this.user_pw = user_pw;
		this.user_name = user_name;
		this.user_cityno = user_cityno;
		this.user_reg_date = user_reg_date;
		this.user_auth = user_auth;
		this.user_disband_date = user_disband_date;
	}

	public Date getUser_disband_date() {
		return user_disband_date;
	}

	public void setUser_disband_date(Date user_disband_date) {
		this.user_disband_date = user_disband_date;
	}

	public int getUser_no() {
		return user_no;
	}

	public void setUser_no(int user_no) {
		this.user_no = user_no;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUser_pw() {
		return user_pw;
	}

	public void setUser_pw(String user_pw) {
		this.user_pw = user_pw;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_cityno() {
		return user_cityno;
	}

	public void setUser_cityno(String user_cityno) {
		this.user_cityno = user_cityno;
	}

	public Date getUser_reg_date() {
		return user_reg_date;
	}

	public void setUser_reg_date(Date user_reg_date) {
		this.user_reg_date = user_reg_date;
	}

	public String getUser_auth() {
		return user_auth;
	}

	public void setUser_auth(String user_auth) {
		this.user_auth = user_auth;
	}

}
