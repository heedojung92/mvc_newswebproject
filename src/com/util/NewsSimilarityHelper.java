package com.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.biz.NewsBiz;
import com.vo.NewsVO;

public class NewsSimilarityHelper {
	static NewsBiz nb = new NewsBiz();
	static HashTagGenerator htg=new HashTagGenerator();
	private NewsVO nv;

	public NewsSimilarityHelper() {
	}

	public NewsSimilarityHelper(NewsVO nv) {
		this.nv = nv;
	}

	public NewsVO getNv() {
		return nv;
	}

	public void setNv(NewsVO nv) {
		this.nv = nv;
	}
	
	public double jaccardSimilarity(NewsVO another) {
		String[] myTitle = nv.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ").split(" ");
		List<String>myTitleWords=new ArrayList<>(Arrays.asList(myTitle));
		myTitleWords.removeIf(s -> s.replaceAll(" ","").equals("")||s.length()<2);		
		myTitleWords=htg.removeJosa(StringUtils.join(myTitleWords));		
		String[] anotherTitle = another.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ")
				.split(" ");
		List<String>anotherTitleWords=new ArrayList<>(Arrays.asList(anotherTitle));
		anotherTitleWords.removeIf(s -> s.replaceAll(" ","").equals("")||s.length()<2);		
		anotherTitleWords=htg.removeJosa(StringUtils.join(anotherTitleWords));				
		Set<String> tokens = new HashSet<>(); // Union Set을 만들기 위한 준비
		tokens.addAll(myTitleWords);
		tokens.addAll(anotherTitleWords);
		double unionSize = (double) tokens.size();
		int myTitleWordsSize = myTitleWords.size();
		int anotherTitleWordsSize = anotherTitleWords.size();
		return (myTitleWordsSize + anotherTitleWordsSize - unionSize) / unionSize;
	}
	
	public double manhattanDistance(NewsVO another) {
		String myTitle =  htg.removeJosaStr(nv.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " "));
		String anotherTitle = htg.removeJosaStr(another.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " "));
		Set<String> tokens = new HashSet<>(); // BagOfWords를 만들기 위한 준비
		tokens.addAll(Arrays.asList(myTitle.split(" ")));
		tokens.addAll(Arrays.asList(anotherTitle.split(" ")));
		tokens.removeIf(s -> s.replaceAll(" ","").equals(""));
		// 보통은 Set의 원소들은 HashCode값에따라 순서가 정해져 나오지만,
		// 100%가 아니기 때문에 순서가 보장되는 List에 원소들을 넣도록 한다.
		List<String> wordList = new ArrayList<>(tokens);
		List<Integer> bowOne = new ArrayList<>();
		List<Integer> bowTwo = new ArrayList<>();
		for (String word : wordList) {
			int count = StringUtils.countMatches(myTitle,word);
			bowOne.add(count);
		}
		for (String word : wordList) {
			int count = StringUtils.countMatches(anotherTitle,word);
			bowTwo.add(count);
		}
		double dist = 0;
		for (int i = 0; i < bowOne.size(); i++) {
			dist += Math.abs((bowOne.get(i) - bowTwo.get(i)));
		}
		return dist;
	}

	public double euclideanDistance(NewsVO another) {
		String myTitle = htg.removeJosaStr(nv.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " "));
		String anotherTitle = htg.removeJosaStr(another.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " "));
		Set<String> tokens = new HashSet<>();
		tokens.addAll(Arrays.asList(myTitle.split(" ")));
		tokens.addAll(Arrays.asList(anotherTitle.split(" ")));
		tokens.removeIf(s ->s.replaceAll(" ","").equals(""));
		List<String> wordList = new ArrayList<>(tokens);
		List<Integer> bowOne = new ArrayList<>();
		List<Integer> bowTwo = new ArrayList<>();
		for (String word : wordList) {
			int count = StringUtils.countMatches(myTitle,word);
			bowOne.add(count);
		}
		for (String word : wordList) {
			int count = StringUtils.countMatches(anotherTitle,word);
			bowTwo.add(count);
		}
		double dist = 0;
		for (int i = 0; i < bowOne.size(); i++) {
			dist += Math.pow((bowOne.get(i) - bowTwo.get(i)), 2);
		}
		return Math.sqrt(dist);
	}

	public double cosSimilarity(NewsVO another) {
		String myTitle = htg.removeJosaStr(nv.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " "));
		String anotherTitle = htg.removeJosaStr(another.getNews_title().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " "));
		Set<String> tokens = new HashSet<>(); // BagOfWords를 만들기 위한 준비
		tokens.addAll(Arrays.asList(myTitle.split(" ")));
		tokens.addAll(Arrays.asList(anotherTitle.split(" ")));
		tokens.removeIf(s ->s.replaceAll(" ","").equals("")||s.length()<2);
		List<String> wordList = new ArrayList<>(tokens);
		List<Integer> bowOne = new ArrayList<>();
		List<Integer> bowTwo = new ArrayList<>();
		for (String word : wordList) {
			int count = StringUtils.countMatches(myTitle,word);
			bowOne.add(count);
		}
		for (String word : wordList) {
			int count = StringUtils.countMatches(anotherTitle,word);
			bowTwo.add(count);
		}
		double top = 0;
		double bowOneNorm = 0;
		double bowTwoNorm = 0;
		for (int i = 0; i < bowOne.size(); i++) {
			bowOneNorm += Math.pow((bowOne.get(i)), 2);
			bowTwoNorm += Math.pow((bowTwo.get(i)), 2);
			top += (bowOne.get(i) * bowTwo.get(i));
		}
		return top / Math.sqrt(bowOneNorm * bowTwoNorm);
	}

	// 유클리드 거리의 경우 거리가 가까울수록(값이 작을수록) 높은 유사도를 가진다.
	public Map<NewsVO, Double> topNReverseSimilarity(Map<NewsVO, Double> notSorted, int n) {
		List<Map.Entry<NewsVO, Double>> list = new LinkedList<>(notSorted.entrySet());
		Collections.sort(list, (e1, e2) -> (int) (e2.getValue()*1000 - e1.getValue()*1000));
		Map<NewsVO, Double> sortedMap = new LinkedHashMap<>();
		int cntmax = 0;
		for (Iterator<Map.Entry<NewsVO, Double>> iter = list.iterator(); iter.hasNext() && cntmax < n;) {
			Map.Entry<NewsVO, Double> entry = iter.next();
			sortedMap.put(entry.getKey(), entry.getValue());
			cntmax++;
		}
		return sortedMap;
	}

	// 자카드 유사도 및 코사인 유사도는 값이 클수록 높은 유사도를 가진다
	public Map<NewsVO, Double> topNSimilarity(Map<NewsVO, Double> notSorted, int n) {
		List<Map.Entry<NewsVO, Double>> list = new LinkedList<>(notSorted.entrySet());
		Collections.sort(list, (e1, e2) -> (int) (e1.getValue()*1000 - e2.getValue()*1000));
		Map<NewsVO, Double> sortedMap = new LinkedHashMap<>();
		int cntmax = 0;
		for (Iterator<Map.Entry<NewsVO, Double>> iter = list.iterator(); iter.hasNext() && cntmax < n;) {
			Map.Entry<NewsVO, Double> entry = iter.next();
			sortedMap.put(entry.getKey(), entry.getValue());
			cntmax++;
		}
		return sortedMap;
	}

	// Input으로 받은 NewsVO 객체와 같은 날에 나온 뉴스들 중, 코사인 유사도가 0.25 이상인 뉴스들을 리턴한다.
	public Set<NewsVO> cosSimNews(NewsVO nv) {
		setNv(nv);
		List<NewsVO> todaynews = nb.getNewsByDate(nv.getNews_date().split("@")[0]);
		Map<NewsVO, Double> cossim = new HashMap<>();
		for (NewsVO n : todaynews) {
			double cossimval = this.cosSimilarity(n);
			if (!n.getNews_title().trim().equals(nv.getNews_title().trim()) && cossimval >= 0.26) {
				cossim.put(n, cossimval);
			}
		}
		Map<NewsVO, Double> sorted = new NewsSimilarityHelper().topNSimilarity(cossim, 8);
		Set<NewsVO> newsset = sorted.keySet();
		return newsset;
	}

	// Input으로 받은 NewsVO 객체와 같은 날에 나온 뉴스들 중, 자카드 유사도가 0.25 이상인 뉴스들을 리턴한다.
	public Set<NewsVO> jaccardSimNews(NewsVO nv) {
		setNv(nv);
		List<NewsVO> todaynews = nb.getNewsByDate(nv.getNews_date().split("@")[0]);
		Map<NewsVO, Double> jaccardsim = new HashMap<>();
		String target=nv.getNews_title().trim();
		
		for (NewsVO n : todaynews) {
			double jaccardsimval = this.jaccardSimilarity(n);
			if (!n.getNews_title().trim().equals(target) && jaccardsimval >= 0.15) {
				jaccardsim.put(n, jaccardsimval);
			}
		}
		Map<NewsVO, Double> sorted = new NewsSimilarityHelper().topNSimilarity(jaccardsim, 8);
		Set<NewsVO> newsset = sorted.keySet();
		return newsset;
	}

	// 오늘 뉴스들 중에서, Input으로 받은 NewsVO 객체와 유클리드 거리가 3.0 이하인 뉴스들을 리턴한다.
	public Set<NewsVO> closeAccToEuclidean(NewsVO nv) {
		setNv(nv);
		List<NewsVO> todaysidnews = nb.getNewsByDate(nv.getNews_date().split("@")[0]);
		Map<NewsVO, Double> closeNews = new HashMap<>();
		String target=nv.getNews_title().trim();
		for (NewsVO n : todaysidnews) {
			double eucldist = this.euclideanDistance(n);
			if (!n.getNews_title().trim().equals(target) && eucldist <= 3.0) {
				closeNews.put(n, eucldist);
			}
		}
		Map<NewsVO, Double> sorted = new NewsSimilarityHelper().topNReverseSimilarity(closeNews, 8);
		Set<NewsVO> newsset = sorted.keySet();
		return newsset;
	}
	
	// 오늘 뉴스들 중에서, Input으로 받은 NewsVO 객체와 맨하탄 거리가 10 이하인 뉴스들을 리턴한다.
		public Set<NewsVO> closeAccToManhattan(NewsVO nv) {
			setNv(nv);
			List<NewsVO> todaynews = nb.getNewsByDate(nv.getNews_date().split("@")[0]);
			Map<NewsVO, Double> closeNews = new HashMap<>();
			String target=nv.getNews_title().trim();
			for (NewsVO n : todaynews) {
				double manhattandist = this.manhattanDistance(n);
				if (!n.getNews_title().trim().equals(target) && manhattandist <= 9) {
					closeNews.put(n, manhattandist);
				}
			}
			Map<NewsVO, Double> sorted = new NewsSimilarityHelper().topNReverseSimilarity(closeNews, 8);
			Set<NewsVO> newsset = sorted.keySet();
			return newsset;
		}

}
