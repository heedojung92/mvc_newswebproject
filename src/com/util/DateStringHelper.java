package com.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DateStringHelper {
	
	public String getTimeWithFormat() {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd@HHmm");
		Date timenow = Calendar.getInstance().getTime();
		String currentTime = df.format(timenow);
		return currentTime;
	}
	public String getCurrentDate() {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		Date today = Calendar.getInstance().getTime();
		String todayAsString = df.format(today);
		return todayAsString;
	}
	public String getCurrentTime() {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd@HHmm");
		Date timenow = Calendar.getInstance().getTime();
		String currentTime = df.format(timenow).split("@")[1];
		return currentTime;
	}
	public int dateDiff(String from,String to) {
		int cnt=-1;
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		Date dateFrom;
		String date="";
		try {
			dateFrom = df.parse(from);
			cal.setTime(dateFrom);
			while (!date.equals(to)) {
				Date mydate = cal.getTime();
				date = df.format(mydate).toString();
				cal.add(Calendar.DATE, 1);
				cnt++;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cnt;
	}
	
	// String from &to's format-> yyyy.mm.dd
	// inclusive (both end)
	public List<String> getDaysBetween(String from, String to) throws ParseException {
		List<String> dates = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		Date dateFrom = df.parse(from);
		cal.setTime(dateFrom);
		String s = "";
		while (!s.equals(to)) {
			Date mydate = cal.getTime();
			s = df.format(mydate).toString();
			cal.add(Calendar.DATE, 1);
			dates.add(s);
		}
		return dates;
	}

	// String from &to's format-> yyyy.mm.dd
	// exclusive (both end)
	public List<String> getDaysBetweenExclusive(String from, String to) throws ParseException {
		List<String> dates = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		Date dateFrom = df.parse(from);
		cal.setTime(dateFrom);
		cal.add(Calendar.DATE, 1);
		String s = "";
		while (true) {
			Date mydate = cal.getTime();
			s = df.format(mydate).toString();
			cal.add(Calendar.DATE, 1);
			if (s.equals(to)) {
				return dates;
			} else {
				dates.add(s);
			}
		}
	}
	
	public Map<String, String> monthMapping() {
		String[] monthLetter = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		String[] monthNumber = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
		Map<String, String> month = new HashMap<>();
		for (int index = 0; index < monthLetter.length - 1; index++) {
			month.put(monthLetter[index], monthNumber[index]);
		}
		return month;
	}
	//date는 yyyy.MM.dd format
	//n일 전의 날짜를 yyyy.MM.dd format으로 리턴
	public String nDaysBefore(String date,int n) {
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		Date dateInput=null;
		try {
			dateInput = df.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.setTime(dateInput);
		cal.add(Calendar.DATE, (-1)*n);
		Date mydate = cal.getTime();
		String s = df.format(mydate).toString();
		return s;
		
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(new DateStringHelper().dateDiff("2019.01.01", "2019.01.01"));
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String str = dayTime.format(new Date(time));
		//System.out.println(str);

		DateStringHelper dsh=new DateStringHelper();
		//System.out.println(dsh.nDaysBefore("2018.12.25", -1));

		/*
		List<String> datestr = new DateStringHelper().getDaysBetweenExclusive("2018.12.25", "2018.12.26");
		System.out.println(datestr.isEmpty());
		for (String s : datestr) {
			System.out.println(s);
		}
		System.out.println(new DateStringHelper().getTimeWithFormat() );
		String t=new DateStringHelper().getCurrentTime();
		System.out.println(900<Integer.valueOf(t));
		
		//System.out.println(todayAsString.substring(10));
		 * 
		 */
	}

}
