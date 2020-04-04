package com.util;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.vo.HashTagVO;
import com.vo.NewsVO;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

public class HashTagGenerator implements Serializable{	
	static Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);    
	private static final HashTagGenerator htg=new HashTagGenerator();
	
	public static HashTagGenerator getInstance() {
		return htg;
	}
	
	public List<String>listedComps(){
		List<String> compNames = new ArrayList<>();
		File file = new File("C:\\SpringWork\\FinalProject\\src\\etc\\stockcode.csv");
		try {
			FileReader inputfile = new FileReader(file);
			CSVReader reader = new CSVReader(inputfile);
			String[] nextRecord;
			reader.readNext();
			while ((nextRecord = reader.readNext()) != null) {
				String comp_name = nextRecord[2];
				compNames.add(comp_name);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return compNames;
	}
	
	//상장되어있는 회사+빈번히 등장하는 회사들의 목록을 읽어온다
	public List<String> getCompNames() {
		List<String> compNames = new ArrayList<>();
		List<String>compAvoid=new ArrayList<>(
				(Arrays.asList("상보", "태양", "남성", "크리스탈", "대상", "나노", "레이", "아시아경제", "뉴시스", "수성", "디딤", "코디", "엠로",
						"한국경제TV", "테스", "NEW", "메카로", "선진", "신흥", "대원", "디오", "배럴", "한창", "대모", "서산", "연우", "신원"
						,"LF","CS","디자인","GV","대유")));
		File file = new File("C:\\SpringWork\\FinalProject\\src\\etc\\stockcode.csv");
		try {
			FileReader inputfile = new FileReader(file);
			CSVReader reader = new CSVReader(inputfile);
			String[] nextRecord;
			reader.readNext();
			while ((nextRecord = reader.readNext()) != null) {
				String comp_name = nextRecord[2];
				if (compAvoid.indexOf(comp_name) == -1) {
					compNames.add(comp_name);
				}
			}
			//뉴스에 자주 등장하지만, 상장기업리스트에 추가되지 않은
			compNames.add("구글"); compNames.add("유튜브"); compNames.add("화웨이"); compNames.add("테슬라");
			compNames.add("넷플릭스");compNames.add("애플");compNames.add("디즈니");compNames.add("인스타그램");
			compNames.add("페이스북");compNames.add("아마존");compNames.add("구글LLC");compNames.add("알리바바");
			compNames.add("CNBC");compNames.add("게임빌");compNames.add("카카오페이지");compNames.add("토스");
			compNames.add("우리은행");compNames.add("현대차");compNames.add("TSMC");compNames.add("블록원");
			compNames.add("우리금융");compNames.add("하나은행");compNames.add("하나금융");compNames.add("신한금융");
			compNames.add("금융감독원");compNames.add("제일은행");compNames.add("현대캐피탈");compNames.add("라임자산운용");
			compNames.add("쿠팡");compNames.add("이베이코리아");
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return compNames;
	}
	public List<String>readJosa(){
		List<String>josas=new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File("C:\\SpringWork\\FinalProject\\src\\etc\\josa.txt")); 
			while(sc.hasNext()) {
				josas.add(sc.nextLine());			
			}
			sc.close();
		}catch(Exception e) {
			e.printStackTrace();		
		}
		return josas;
	}
	public List<String>readStopWords(){
		List<String>stopWords=new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File("C:\\SpringWork\\FinalProject\\src\\etc\\stopwords.txt")); 
			while(sc.hasNext()) {
				stopWords.add(sc.nextLine());			
			}
			sc.close();
		}catch(Exception e) {
			e.printStackTrace();		
		}
		return stopWords;
	}	
	public List<String>readKeyWords(){
		List<String>keyWords=new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File("C:\\SpringWork\\FinalProject\\src\\etc\\keywords.txt")); 
			while(sc.hasNext()) {
				keyWords.add(sc.nextLine());			
			}
			sc.close();
		}catch(Exception e) {
			e.printStackTrace();		
		}
		return keyWords;
	}
	
	/*NewsVO객체에서 키워드 및 빈도수를 추출한다.
	Map<K,V> K:News Keyword, V:Corresponding Frequency
	*/
	public Map<String,Integer>getHashtags(NewsVO nv){
		String news_title=nv.getNews_title().replaceAll("[,||'||’||‘||'||\"]", " ");		
		List<String>tags=htg.keyWordCandidates(nv);	

		Set<String>tagres=new HashSet<>(tags);
		String newscontent=news_title+" "+nv.getNews_body();
		
		Map<String, Integer> hashtags = new HashMap<>();
		for(String tag:tagres) {
			String tagFind=tag.replaceAll(" ", "");
			String bodyFind=newscontent.replaceAll(" ", "").replaceAll("\n", "");
			if(tagFind.length()>1) {
				int tagcnt=StringUtils.countMatches(bodyFind, tagFind);
				if(tagcnt>1) {
					hashtags.put(tag,tagcnt);
				}
			}			
		}
		
		List<String>tagList=new ArrayList<>(hashtags.keySet());
		Collections.sort(tagList,(u,v)->(-1)*(u.length()-v.length()));
		int listLen=tagList.size();
		for(int i=0;i<listLen;i++) {
			String substr=tagList.get(i);
			for(int j=i+1;j<listLen;j++) {
				String longerStr=tagList.get(j);
				if(longerStr.contains(substr)) {
					hashtags.remove(substr);
					break;
				}
			}
		}
		
		Map<String,Integer>res=htg.getTopNFromMap(hashtags, 4);				
		return res;
	}

	public Map<String, Integer> getTopNFromMap(Map<String, Integer> unsorted,int n) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(unsorted.entrySet());
		Collections.sort(list, (e1, e2) -> -1 * (e1.getValue() - e2.getValue()));
		Map<String, Integer> sortedMap = new LinkedHashMap<>();
		int cntmax = 0;
		for (Iterator<Map.Entry<String, Integer>> iter = list.iterator(); iter.hasNext() && cntmax < n;) {
			Map.Entry<String, Integer> entry = iter.next();
			sortedMap.put(entry.getKey(), entry.getValue());
			cntmax++;
		}
		return sortedMap;
	}

	public Map<String, Integer> hashTagsFromNewsList(List<NewsVO> list) {		
		HashTagGenerator htg = new HashTagGenerator();
		List<Map<String,Integer>>mapFromList=new ArrayList<>();
		for(NewsVO nv:list) {
			mapFromList.add(htg.getHashtags(nv));
		}
		return htg.mergeMap(mapFromList);		
	}
	
	public Map<String,Integer>hashTagsFromNewsListSpark(List<NewsVO> list){
		MapReduce mr=new MapReduce();
		Map<String, Integer> dict=mr.hashTagMap(list);
		return dict;
	}
		
	public Map<String, Integer> NhashTagsFromNewsList(List<NewsVO> todayNews, int n) {
		return htg.getTopNFromMap(htg.hashTagsFromNewsList(todayNews),n);
	}
	
	public Map<String,Integer>stringToMap(String jsonString){
		Map<String,Integer>map=new HashMap<>();
		if(jsonString!=null&&jsonString.contains(",")) {
			String content=jsonString.substring(1,jsonString.length()-1);
			String[] dictInside=content.split(",");
			for(String kv:dictInside) {
				if(kv.split("=").length>1) {
					String key=kv.split("=")[0];
					int value=Integer.valueOf(kv.split("=")[1]);
					map.put(key,value);
				}			
			}
		}
		return map;		
	}
	public Map<String,Integer> mergeMap(List<Map<String,Integer>>maps){
		Map<String,Integer>merged=new HashMap<>();
		for(Map<String,Integer>map:maps) {
			for(String key:map.keySet()) {
				if(merged.containsKey(key)) {
					merged.put(key,merged.get(key)+map.get(key));
				}else {
					merged.put(key,map.get(key));
				}
			}
		}	
		return merged;
	}
	
	public HashTagVO getTagsFromList(List<NewsVO>newslist) {
		HashTagGenerator htg=new HashTagGenerator();
		List<Map<String,Integer>>financetaglist=new ArrayList<>();
		List<Map<String,Integer>>stocktaglist=new ArrayList<>();
		List<Map<String,Integer>>industrytaglist=new ArrayList<>();
		List<Map<String,Integer>>venturetaglist=new ArrayList<>();
		List<Map<String,Integer>>globecontaglist=new ArrayList<>();
		List<Map<String,Integer>>genecontaglist=new ArrayList<>();
		List<Map<String,Integer>>internettaglist=new ArrayList<>();
		List<Map<String,Integer>>ittaglist=new ArrayList<>();
		List<Map<String,Integer>>totaltaglist=new ArrayList<>();
		for(NewsVO nv:newslist) {
			Map<String,Integer>tags=htg.getHashtags(nv);
			switch(nv.getNews_sid()) {
			case "259101":financetaglist.add(tags);totaltaglist.add(tags);break;
			case "258101":stocktaglist.add(tags);totaltaglist.add(tags);break;
			case "261101":industrytaglist.add(tags);totaltaglist.add(tags);break;
			case "771101":venturetaglist.add(tags);totaltaglist.add(tags);break;
			case "262101":globecontaglist.add(tags);totaltaglist.add(tags);break;
			case "263101":genecontaglist.add(tags);totaltaglist.add(tags);break;
			case "226105":internettaglist.add(tags);totaltaglist.add(tags);break;
			case "230105":ittaglist.add(tags);totaltaglist.add(tags);break;
			}
		}
		HashTagVO htv=new HashTagVO();
		htv.setFinance_tags(htg.mergeMap(financetaglist));
		htv.setStock_tags(htg.mergeMap(stocktaglist));
		htv.setIndustry_tags(htg.mergeMap(industrytaglist));
		htv.setVenture_tags(htg.mergeMap(venturetaglist));
		htv.setGlobecon_tags(htg.mergeMap(globecontaglist));
		htv.setGenecon_tags(htg.mergeMap(genecontaglist));
		htv.setInternet_tags(htg.mergeMap(internettaglist));
		htv.setIt_tags(htg.mergeMap(ittaglist));
		htv.setTotal_tags(htg.mergeMap(totaltaglist));
		return htv;
	}
	public HashTagVO combineTag(List<NewsVO> newslist,HashTagVO toadd) {
		HashTagGenerator htg=new HashTagGenerator();
		List<Map<String,Integer>>financetaglist=new ArrayList<>();
		financetaglist.add(toadd.getFinance_tags());
		List<Map<String,Integer>>stocktaglist=new ArrayList<>();
		stocktaglist.add(toadd.getStock_tags());
		List<Map<String,Integer>>industrytaglist=new ArrayList<>();
		industrytaglist.add(toadd.getIndustry_tags());
		List<Map<String,Integer>>venturetaglist=new ArrayList<>();
		venturetaglist.add(toadd.getVenture_tags());
		List<Map<String,Integer>>globecontaglist=new ArrayList<>();
		globecontaglist.add(toadd.getGlobecon_tags());
		List<Map<String,Integer>>genecontaglist=new ArrayList<>();
		genecontaglist.add(toadd.getGenecon_tags());
		List<Map<String,Integer>>internettaglist=new ArrayList<>();
		internettaglist.add(toadd.getInternet_tags());
		List<Map<String,Integer>>ittaglist=new ArrayList<>();
		ittaglist.add(toadd.getIt_tags());
		List<Map<String,Integer>>totaltaglist=new ArrayList<>();
		totaltaglist.add(toadd.getTotal_tags());
		for(NewsVO nv:newslist) {
			Map<String,Integer>tags=htg.getHashtags(nv);
			switch(nv.getNews_sid()) {
			case "259101":financetaglist.add(tags);totaltaglist.add(tags);break;
			case "258101":stocktaglist.add(tags);totaltaglist.add(tags);break;
			case "261101":industrytaglist.add(tags);totaltaglist.add(tags);break;
			case "771101":venturetaglist.add(tags);totaltaglist.add(tags);break;
			case "262101":globecontaglist.add(tags);totaltaglist.add(tags);break;
			case "263101":genecontaglist.add(tags);totaltaglist.add(tags);break;
			case "226105":internettaglist.add(tags);totaltaglist.add(tags);break;
			case "230105":ittaglist.add(tags);totaltaglist.add(tags);break;
			}
		}
		HashTagVO htv=new HashTagVO();
		htv.setFinance_tags(htg.mergeMap(financetaglist));
		htv.setStock_tags(htg.mergeMap(stocktaglist));
		htv.setIndustry_tags(htg.mergeMap(industrytaglist));
		htv.setVenture_tags(htg.mergeMap(venturetaglist));
		htv.setGlobecon_tags(htg.mergeMap(globecontaglist));
		htv.setGenecon_tags(htg.mergeMap(genecontaglist));
		htv.setInternet_tags(htg.mergeMap(internettaglist));
		htv.setIt_tags(htg.mergeMap(ittaglist));
		htv.setTotal_tags(htg.mergeMap(totaltaglist));
		return htv;		
	}
	public Set<String>morphologicalAnalysis(NewsVO nv){
		Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
		String newscontent=nv.getNews_body();
		KomoranResult analyzeResultList = komoran.analyze(newscontent);
	    List<String>nouns=analyzeResultList.getNouns();
	    List<String>possibleTags=htg.keyWordCandidates(nv);
	    possibleTags.addAll(nouns);
	    List<String>NGram=htg.NGram(possibleTags);
	    Set<String>distinct_nouns=new HashSet<>(possibleTags);
	    distinct_nouns.addAll(NGram);
	    return distinct_nouns;
	}
	
	public List<String>keyWordCandidates(NewsVO nv){
		List<String>tags=new ArrayList<>();
		String content=nv.getNews_title()+" "+nv.getNews_body();		
		String[] betweenWordsOne=StringUtils.substringsBetween(content, "'", "'");
		String[] betweenWordsTwo=StringUtils.substringsBetween(content, "\"", "\"");
		String[] betweenWordsThree=StringUtils.substringsBetween(content, "(", ")");
		String[] betweenWordsFour=StringUtils.substringsBetween(content, "‘", "’");
		if(betweenWordsOne!=null) {
			tags.addAll(Arrays.asList(betweenWordsOne));
		}
		if(betweenWordsTwo!=null) {
			tags.addAll(Arrays.asList(betweenWordsTwo));
		}
		if(betweenWordsThree!=null) {
			tags.addAll(Arrays.asList(betweenWordsThree));
		}
		if(betweenWordsFour!=null) {
			tags.addAll(Arrays.asList(betweenWordsFour));
		}				
		List<String> compNames =htg.getCompNames();
		for(String comp:compNames) {
			if(content.contains(comp)) {
				tags.add(comp);
			}
		}
		List<String>keyWords=htg.readKeyWords();
		for(String word:keyWords) {
			if(content.contains(word)) {
				tags.add(word);
			}
		}

		if (nv.getNews_title().contains(",")) {
			tags.add(nv.getNews_title().split(",")[0]);
		}
		
		
		tags.addAll(htg.removeJosa(nv.getNews_title()));
		List<String> stoptest = htg.readStopWords();
		for (String stopword : stoptest) {
			tags.remove(stopword);
		}
		tags.removeIf(s->s.matches("[+|-]*[0-9]+[.]*[0-9]*[개|달러|만|월|년|분기|%|원|위|일|명|시|분|호|세|억|조]*[0-9]*"));
		tags.removeIf(s->s.length()<2);
		return tags.stream().map(s -> s.trim()).collect(Collectors.toList());
	}
	
	

	
	public List<String>removeJosa(String content){

		String[]words=content.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"," ").split(" ");
		List<String>cleaned=new ArrayList<>(Arrays.asList(words));
		cleaned.removeIf(s->s.replaceAll(" ", "").equals(""));
		List<String>josas=htg.readJosa();
		List<String>josaRemoved=new ArrayList<>();
		for(String word:cleaned) {
			String res="";
			for(String josa:josas) {
				if(word.endsWith(josa)) {
					res=StringUtils.removeEnd(word, josa);
					break;
				}
				res=StringUtils.removeEnd(word, josa);
			}
			josaRemoved.add(res);
		}
		return josaRemoved;
	}
	
	public String removeJosaStr(String content) {
		List<String>removed=htg.removeJosa(content);
		return StringUtils.join(removed, " ");
	}
	
	// N-Gram with N=2; Bi-Gram
	public List<String>NGram(List<String>content){
		List<String>res=new ArrayList<>();		
		content.removeIf(s->s.length()<2);
		int length=content.size();
		for(int i=0;i<length-1;i++) {
			String twoWord=content.get(i)+" "+content.get(i+1);
			res.add(twoWord);
		}
		return res;
	}





}
