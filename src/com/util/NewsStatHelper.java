package com.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vo.NewsVO;

public class NewsStatHelper {
	static NewsStatHelper nsh=new NewsStatHelper();
	private List<NewsVO> newsList;
	public NewsStatHelper() {
		super();
	}
	public NewsStatHelper(List<NewsVO> newsList) {
		this.newsList = newsList;
	}
	public List<NewsVO> getNewsList() {
		return newsList;
	}
	public void setNewsList(List<NewsVO> newsList) {
		this.newsList = newsList;
	}
	//ī�װ��� �Է¹��� ��, �����Ǵ� sid���� �������ش�.
	public String catConverter(String category) {
		String sid="";
		switch (category) {
		case "����":
			sid= "259101";
			break;
		case "����":
			sid = "258101";
			break;
		case "���/���":
			sid = "261101";
			break;
		case "�߱�/��ó":
			sid = "771101";
			break;
		case "�۷ι� ����":
			sid = "262101";
			break;
		case "���� �Ϲ�":
			sid = "263101";
			break;
		case "���ͳ�/SNS":
			sid = "226105";
			break;
		case "IT":
			sid = "230105";
			break;
		default: sid="000000";
		}
		return sid;
	}

	public String sidConverter(String sid) {
		String category = "";
		switch (sid) {
		case "259101":
			category = "����";
			break;
		case "258101":
			category = "����";
			break;
		case "261101":
			category = "���/���";
			break;
		case "771101":
			category = "�߱�/��ó";
			break;
		case "262101":
			category = "�۷ι� ����";
			break;
		case "263101":
			category = "���� �Ϲ�";
			break;
		case "226105":
			category = "���ͳ�/SNS";
			break;
		case "230105":
			category = "IT";
			break;
		default: category="��Ÿ";
		}
		return category;
	}

	public Map<String, Integer> getTopN(Map<String, Integer> unsorted, int n) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(unsorted.entrySet());
		Collections.sort(list, (e1, e2) -> -1 * (e1.getValue() - e2.getValue()));
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		int cntmax = 0;
		for (Iterator<Map.Entry<String, Integer>> iter = list.iterator(); iter.hasNext() && cntmax < n;) {
			Map.Entry<String, Integer> entry = iter.next();
			sortedMap.put(entry.getKey(), entry.getValue());
			cntmax++;
		}
		return sortedMap;
	}
	
	public Map<String, Integer> getTimeSeriesData(String data_choice, String chart_choice) {
		Map<String, Integer>map=new HashMap<>(); 	
		if (chart_choice.contains("��л�")) {
			newsList.stream().filter(nv->nv.getNews_pub().equals(data_choice));
			for(NewsVO nv:newsList) {
				String date=nv.getNews_date().split("@")[0];
				if(map.containsKey(date)) {
					map.put(date,map.get(date)+1);
					}
				else {
					map.put(date,1);
						}
				}
			}
		else if (chart_choice.contains("���")) {
			newsList.stream().filter(nv->nv.getNews_body().contains(data_choice));
			for(NewsVO nv:newsList) {
				String date=nv.getNews_date().split("@")[0];
				if(map.containsKey(date)) {
					map.put(date,map.get(date)+1);
					}else {
						map.put(date,1);
						}
				}
		} else {
			newsList.stream().filter(nv->nv.getNews_sid().equals(nsh.catConverter(data_choice)));
			for(NewsVO nv:newsList) {
				String date=nv.getNews_date().split("@")[0];
				if(map.containsKey(date)) {
					map.put(date,map.get(date)+1);
					}else {
						map.put(date,1);
						}
				}
		}     
		Map<String, Integer> treeMap = new TreeMap<>(map);
		for (String str : treeMap.keySet()) {
		    System.out.println(str);
		};		
		return treeMap;
	}

	public Map<String, Integer> timeSeriesData(String data_choice, String chart_choice) {
		Map<String, Integer>map=new HashMap<>(); 		
		if (chart_choice.contains("��л�")) {
			newsList.stream().filter(nv->nv.getNews_pub().contains(data_choice));
			for(NewsVO nv:newsList) {
				String date=nv.getNews_date();
				String value=nv.getNews_pub();
				if(map.containsKey(date)) {
					map.put(date,map.get(date)+1);
					}else {
						map.put(date,1);
					}
			}
		} else if (chart_choice.contains("���")) {
			newsList.stream().filter(nv->nv.getNews_body().contains(data_choice));
			for(NewsVO nv:newsList) {
				String date=nv.getNews_date();
				String value=nv.getNews_pub();
				if(map.containsKey(date)) {
					map.put(date,map.get(date)+1);
					}else {
						map.put(date,1);
					}
			}
		} else {
			newsList.stream().filter(nv->nv.getNews_sid().equals(nsh.catConverter(data_choice)));
			for(NewsVO nv:newsList) {
				String date=nv.getNews_date();
				String value=nv.getNews_pub();
				if(map.containsKey(date)) {
					map.put(date,map.get(date)+1);
					}else {
						map.put(date,1);
					}
			}
		}     
		Map<String, Integer> treeMap = new TreeMap<>(map);
		for (String str : treeMap.keySet()) {
		    System.out.println(str);
		};		
		return treeMap;
	}

	public Map<String, Integer> newsPubStat() {
		Map<String, Integer> map = new HashMap<>();
		for (NewsVO nv : newsList) {
			String news_pub = nv.getNews_pub();
			if (map.containsKey(news_pub)) {
				map.put(news_pub, map.get(news_pub) + 1);
			} else {
				map.put(news_pub, 1);
			}
		}
		return map;
	}

	public Map<String, Integer> newsCatStat() {
		Map<String, Integer> map = new HashMap<>();
		for (NewsVO nv : newsList) {
			String news_cat = nsh.sidConverter(nv.getNews_sid());
			if (map.containsKey(news_cat)) {
				map.put(news_cat, map.get(news_cat) + 1);
			} else {
				map.put(news_cat, 1);
			}
		}
		return map;
	}

	public Map<String, Integer> newsCompStat() {
		List<String> compNames = new HashTagGenerator().getCompNames();
		System.out.println(compNames.size());
		Map<String, Integer> map = new HashMap<>();
		for (NewsVO nv : newsList) {
			String news_body = nv.getNews_body();
			for (String comp : compNames) {
				if (news_body.contains(comp)) {
					if (map.containsKey(comp)) {
						map.put(comp, map.get(comp) + 1);
					} else {
						map.put(comp, 1);
					}
				}
			}
		}
		return map;
	}
}
