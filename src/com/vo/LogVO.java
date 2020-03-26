package com.vo;

import java.util.Date;

public class LogVO {
	private String accessorIpAddress;
	private Date date;
	private Date time;
	private String requestPage;
	private String pageReferedFrom;
	private String stringTime;
	private String stringDate;
	private String requestMethod;
	private String httpStatus;
	public String getAccessorIpAddress() {
		return accessorIpAddress;
	}

	public void setAccessorIpAddress(String accessorIpAddress) {
		this.accessorIpAddress = accessorIpAddress;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getStringTime() {
		return stringTime;
	}

	public void setStringTime(String stringTime) {
		this.stringTime = stringTime;
	}

	public String getStringDate() {
		return stringDate;
	}

	public void setStringDate(String stringDate) {
		this.stringDate = stringDate;
	}

	public String getRequestPage() {
		return requestPage;
	}

	public void setRequestPage(String requestPage) {
		this.requestPage = requestPage;
	}

	public String getPageReferedFrom() {
		return pageReferedFrom;
	}

	public void setPageReferedFrom(String pageReferedFrom) {
		this.pageReferedFrom = pageReferedFrom;
	}

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	@Override
	public String toString() {
		String info = "���� IP: " + getAccessorIpAddress() + " ���� ����: " + getStringDate() + " ���� �ð�: " + getStringTime()
				+ " ��û ���: " + getRequestMethod() + " ��û ����Ʈ: " + getRequestPage();
		return info;
	}

}
