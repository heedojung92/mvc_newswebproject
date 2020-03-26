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
		Set<String> tokens = new HashSet<>(); // Union Set�� ����� ���� �غ�
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
		Set<String> tokens = new HashSet<>(); // BagOfWords�� ����� ���� �غ�
		tokens.addAll(Arrays.asList(myTitle.split(" ")));
		tokens.addAll(Arrays.asList(anotherTitle.split(" ")));
		tokens.removeIf(s -> s.replaceAll(" ","").equals(""));
		// ������ Set�� ���ҵ��� HashCode�������� ������ ������ ��������,
		// 100%�� �ƴϱ� ������ ������ ����Ǵ� List�� ���ҵ��� �ֵ��� �Ѵ�.
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
		Set<String> tokens = new HashSet<>(); // BagOfWords�� ����� ���� �غ�
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

	// ��Ŭ���� �Ÿ��� ��� �Ÿ��� ��������(���� ��������) ���� ���絵�� ������.
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

	// ��ī�� ���絵 �� �ڻ��� ���絵�� ���� Ŭ���� ���� ���絵�� ������
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

	// Input���� ���� NewsVO ��ü�� ���� ���� ���� ������ ��, �ڻ��� ���絵�� 0.25 �̻��� �������� �����Ѵ�.
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

	// Input���� ���� NewsVO ��ü�� ���� ���� ���� ������ ��, ��ī�� ���絵�� 0.25 �̻��� �������� �����Ѵ�.
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

	// ���� ������ �߿���, Input���� ���� NewsVO ��ü�� ��Ŭ���� �Ÿ��� 3.0 ������ �������� �����Ѵ�.
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
	
	// ���� ������ �߿���, Input���� ���� NewsVO ��ü�� ����ź �Ÿ��� 10 ������ �������� �����Ѵ�.
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
