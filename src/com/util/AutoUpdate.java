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
			//�ֽĽ��� ���� �ð��� 15�� 30�� �����̸�, ������ �ְ� �����͸� ũ�Ѹ� �Ѵ�.
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
	
		//������ ������Ʈ ���� ������ �ƴ� �� 
		if(!lastNewsDate.equals(todayAsString)) {
			List<String>datesBetween=new ArrayList<>();
			try {
				datesBetween=dsh.getDaysBetweenExclusive(lastNewsDate,todayAsString);
			} catch (ParseException e) {
				e.printStackTrace();
			}		
			//������ ������Ʈ ���� ������ �ƴ� �� 
			if(!datesBetween.isEmpty()) {
				for(String date:datesBetween) {
					newsdata=crawler.crawlNews(date);
					htv=htg.getTagsFromList(newsdata);
					htv.setTag_date(date+"@2359");
					nb.insertBatch(newsdata);
					res=htb.insertOne(htv);
				}				
			}
			//������ ���� ũ�Ѹ�
			newsdata=crawler.crawlNewsByDateAndTime(lastNewsUpdate);
			htv=htg.getTagsFromList(newsdata);
			System.out.println(htv.getTotal_tags());
			htv.setTag_date(lastNewsDate+"@2359");
			nb.insertBatch(newsdata);
			res=htb.insertOne(htv);													
		}else {
			//������ ������Ʈ ��¥�� ������ ���
			//����, ������Ʈ�� �ð��� 3�ð� �̻��̸� ������Ʈ �ϵ��� �Ѵ�.
			int lastupdateTime=Integer.valueOf(lastNewsUpdate.split("@")[1].trim());
			int currentTime=Integer.valueOf(dsh.getCurrentTime());
			//19�� ���Ŀ��� ������ �ö���¾��� ������ �پ���.
			//���� 9�� �����̶��, ������Ʈ �� ������ ���� ���� Ȯ���� ����
			if(lastupdateTime>=1900||currentTime<900) {
				return;
			}
			//�ð��� 3�ð� �̻� ������ �ʾ��� ���
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
