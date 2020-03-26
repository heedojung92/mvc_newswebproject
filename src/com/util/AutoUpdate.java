package com.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.biz.HashTagBiz;
import com.biz.NewsBiz;
import com.biz.StockBiz;
import com.vo.HashTagVO;
import com.vo.NewsVO;
import com.vo.StockVO;

public class AutoUpdate {	
	public void updateStock() {
		StockBiz sb=new StockBiz();
		StockCrawler sc=new StockCrawler();
		String lastStockUpdate=sb.getMaxDate();
		DateStringHelper dsh=new DateStringHelper();
		String currentDate=dsh.getCurrentDate();
		List<StockVO>part=new ArrayList<>();
		List<StockVO>res=new ArrayList<>();
		if(lastStockUpdate.equals(currentDate)) {
			return;
		}else {
			String dateFrom=null;
			String dateTo=null;
			dateFrom=dsh.nDaysBefore(lastStockUpdate, -1);
			//주식시장 폐장 시간인 15시 30분 이후이면, 오늘의 주가 데이터를 크롤링 한다.
			if(dsh.getCurrentTime().split("@")[1].compareTo("1530")>0) {
				part=sc.crawlAllListedComp(dateFrom, currentDate);
				sb.insertBatch(part); return;
			}else {
				dateTo=dsh.nDaysBefore(currentDate, 1);
				part=sc.crawlAllListedComp(dateFrom,dateTo);
				sb.insertBatch(part); return;
			}			
		}		
	}
	public void updateNewsAndTag() {
		DateStringHelper dsh=new DateStringHelper();
		NewsBiz nb=new NewsBiz();
		HashTagBiz htb=new HashTagBiz();
		String lastNewsUpdate=nb.getMaxNewsDate();
		String lastNewsDate=lastNewsUpdate.split("@")[0].trim();
		String todayAsString=dsh.getCurrentDate();
		HashTagGenerator htg=new HashTagGenerator();
		Crawler crawler=new Crawler();		
		List<NewsVO>newsdata=null;
		HashTagVO htv=null;
		int res=0;
	
		//마지막 업데이트 날이 오늘이 아닐 때 
		if(!lastNewsDate.equals(todayAsString)) {
			List<String>datesBetween=new ArrayList<>();
			try {
				datesBetween=dsh.getDaysBetweenExclusive(lastNewsDate,todayAsString);
			} catch (ParseException e) {
				e.printStackTrace();
			}		
			//마지막 업데이트 날이 어제가 아닐 때 
			if(!datesBetween.isEmpty()) {
				for(String date:datesBetween) {
					newsdata=crawler.crawlNews(date);
					htv=htg.getTagsFromList(newsdata);
					htv.setTag_date(date+"@2359");
					nb.insertBatch(newsdata);
					res=htb.insertOne(htv);
				}				
			}
			//어제자 뉴스 크롤링
			newsdata=crawler.crawlNewsByDateAndTime(lastNewsUpdate);
			htv=htg.getTagsFromList(newsdata);
			System.out.println(htv.getTotal_tags());
			htv.setTag_date(lastNewsDate+"@2359");
			nb.insertBatch(newsdata);
			res=htb.insertOne(htv);													
		}else {
			//마지막 업데이트 날짜가 오늘인 경우
			//만약, 업데이트된 시간이 3시간 이상이면 업데이트 하도록 한다.
			int lastupdateTime=Integer.valueOf(lastNewsUpdate.split("@")[1].trim());
			int currentTime=Integer.valueOf(dsh.getCurrentTime());
			//19시 이후에는 뉴스가 올라오는양이 현저히 줄어든다.
			//오전 9시 이전이라면, 업데이트 된 뉴스가 많지 않을 확률이 높다
			if(lastupdateTime>=1900||currentTime<900) {
				return;
			}
			//시간이 3시간 이상 지나지 않았을 경우
			if((currentTime/100)-(lastupdateTime/100)<3) {
				return;
			}else {
				newsdata=crawler.crawlNewsByDateAndTime(lastNewsUpdate);
				htv=htg.getTagsFromList(newsdata);
				htv.setTag_date(dsh.getTimeWithFormat());
				nb.insertBatch(newsdata);
				res=htb.insertOne(htv);							
			}
		}		
	}

}
