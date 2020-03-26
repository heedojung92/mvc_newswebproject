package com.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;
import com.vo.NewsVO;


public class NewsExporter {
	//뉴스데이터를 일별로 csv format으로 export해주는 메소드
	public void exportToCsv(List<NewsVO> list) {
		String date=list.get(0).getNews_date().split("@")[0];
		File file = new File("C:\\news_csv\\"+date+".csv"); 
		    try { 
		        FileWriter outputfile = new FileWriter(file); 
		        CSVWriter writer = new CSVWriter(outputfile); 		 
		        String[] header = {"news_no","news_date","news_sid","news_title","news_summ","news_body","news_url","news_imgurl","news_pub"}; 
		        writer.writeNext(header); 
		        for(NewsVO nv:list) {
		        	String[]data=new String[] {null,nv.getNews_date(),nv.getNews_sid(),nv.getNews_title(),nv.getNews_summ(),nv.getNews_body(),nv.getNews_url(),nv.getNews_imgurl(),nv.getNews_pub()};
		        	writer.writeNext(data);
		        }		  
		        writer.close(); 
		    } 
		    catch (IOException e) { 
		        e.printStackTrace(); 
		    } 

	}

}
