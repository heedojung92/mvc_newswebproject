package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.biz.StockBiz;
import com.opencsv.CSVReader;
import com.vo.StockVO;



public class StockCrawler {
	final String path="C:\\SpringWork\\FinalProject\\src\\etc\\stockcode.csv";	
	public String getStockCode(String compName) {
		BufferedReader csvReader;
		String row="";
		String code="";
		try {
			csvReader = new BufferedReader(new FileReader(path));
			while((row=csvReader.readLine())!=null) {
				String[] data=row.split(",");
				if(data[2].equals(compName)) {
					code=data[1];
					}
				}
			if(code.length()>0&&code.length()<6) {
					String temp="0000000"+code;
					code=temp.substring(temp.length()-6);
				}
			if(code.length()==0)
				code="notfound";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}
		
	public List<StockVO> crawlByStockCode(String compName) {
		String stockCode=new StockCrawler().getStockCode(compName);
		String stockPriceUrl = "http://finance.naver.com/item/sise_day.nhn?code=";
		String pg = "&page=1";
		String dayPriceUrl = stockPriceUrl + stockCode + pg;
		Document doc = null;
		try {
			doc = Jsoup.connect(dayPriceUrl).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Elements element = doc.select("span[class^=tah p]");
		int count = 0;
		List<StockVO> list = new ArrayList<StockVO>();
		StockVO temp = new StockVO();
		for (Element el : element) {
			switch(count) {
			case 0: temp.setComp_name(compName);temp=new StockVO();temp.setSt_date(el.text());count++;break;
			case 1: temp.setClose_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
			case 3: temp.setOpen_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
			case 4: temp.setHigh_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
			case 5: temp.setLow_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
			case 6: list.add(temp);count=0;break;
			default: count++;break;
			}
		} 
		return list;
	}
	
	public List<String> getCompNames() {
		List<String> compNames = new ArrayList<>();
		File file = new File(path);
		try {
			FileReader inputfile = new FileReader(file);
			CSVReader reader = new CSVReader(inputfile);
			String[] nextRecord;
			reader.readNext();
			while ((nextRecord = reader.readNext()) != null) {
				String comp_name = nextRecord[2];
				compNames.add(comp_name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return compNames;
	}
	//Date Inclusive
	public List<StockVO>crawlAllListedComp(String dateFrom,String dateTo){
		StockCrawler sc=new StockCrawler();
		List<String>listedComps=sc.getCompNames();
		List<StockVO>res=new ArrayList<>();
		for(String comp:listedComps) {
			System.out.println(comp);
			List<StockVO>part=sc.crawlByStockCodeAndDate(comp,dateFrom,dateTo);
			res.addAll(part);
		}
		return res;
	}
	
	public List<StockVO>crawlByStockCodeAndDate(String compName,String dateFrom,String dateTo){
		int page=1;
		String stockCode=new StockCrawler().getStockCode(compName);
		String stockPriceUrl = "http://finance.naver.com/item/sise_day.nhn?code=";
		List<StockVO> list = new ArrayList<StockVO>();
		boolean stopCrawl=false;
		while(!stopCrawl) {			
			String dayPriceUrl = stockPriceUrl + stockCode + "&page="+page;
			Document doc = null;
			try {
				doc = Jsoup.connect(dayPriceUrl).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Elements element = doc.select("span[class^=tah p]");
			int count = 0;
			StockVO temp = new StockVO();
			for (Element el : element) {
				boolean nextRow=false;
				switch(count) {
				case 0: if(el.text().compareTo(dateTo)>0) {
					count++;break;
				}else{
					if(el.text().compareTo(dateFrom)<0) {
						return list;
					}
					temp=new StockVO();temp.setComp_name(compName);temp.setSt_date(el.text());count++;break;
				}
				case 1: temp.setClose_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
				case 3: temp.setOpen_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
				case 4: temp.setHigh_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
				case 5: temp.setLow_price(Integer.valueOf(el.text().replaceAll(",", "")));count++;break;
				case 6: if(temp.getSt_date()!=null) {list.add(temp);};count=0;break;
				default: count++;break;
				}		
			}
			page++;	
		}
		return list;
	}
	
}
