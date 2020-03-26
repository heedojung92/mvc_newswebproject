package com.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.storage.StorageLevel;

import com.biz.NewsBiz;
import com.vo.NewsVO;

//JavaRDD를 사용하여 MR작업을 한다.
public class MapReduce implements Serializable {
	static HashTagGenerator htg = new HashTagGenerator();
	
	class KeyWordNonCommutativeRelation implements Function<Map<String, Integer>, Map<String, Map<String, Double>>> {
		@Override
		public Map<String, Map<String, Double>> call(Map<String, Integer> m1) throws Exception {
			Map<String, Map<String, Double>> res = new HashMap<>();
			List<String> keyList = new ArrayList<>(m1.keySet());
			int listLength = keyList.size();
			for (int i = 0; i < listLength; i++) {
				String keyOne = keyList.get(i);
				Integer valOne = m1.get(keyOne);
				Map<String, Double> temp = new HashMap<>();
				for (int j = 0; j < listLength; j++) {
					if (i != j) {
						String keyTwo = keyList.get(j);
						Integer valTwo = m1.get(keyTwo);
						double valTwoDouble = (double) valTwo.intValue();
						Double relation = valTwoDouble / valOne;
						temp.put(keyTwo, relation);
					}
				}
				res.put(keyOne, temp);
			}
			return res;
		}
	}
	
	public Map<String, Map<String, Double>> hashTagRelNonCommutative(List<NewsVO> list) {
		SparkConf conf = new SparkConf().setAppName("spark_mapreduce").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<NewsVO> newsdata = sc.parallelize(list);
		JavaRDD<Map<String, Integer>> keywords = newsdata.map(new MapKeyword());
		JavaRDD<Map<String, Map<String, Double>>> keywordRel = keywords.map(new KeyWordNonCommutativeRelation());
		Map<String, Map<String, Double>> res = keywordRel.reduce(new NonCommutativeRelationReducer());
		sc.close();
		return res;
	}
	

	class CommutativeRelationReducer implements
			Function2<Map<String, Map<String, Integer>>, Map<String, Map<String, Integer>>, Map<String, Map<String, Integer>>> {
		@Override
		public Map<String, Map<String, Integer>> call(Map<String, Map<String, Integer>> m1,
				Map<String, Map<String, Integer>> m2) throws Exception {
			for (String key : m2.keySet()) {
				if (m1.containsKey(key)) {
					Map<String, Integer> base = m1.get(key);
					Map<String, Integer> toadd = m2.get(key);
					for (String keyTwo : toadd.keySet()) {
						if (base.containsKey(keyTwo)) {
							base.put(keyTwo, base.get(keyTwo) + toadd.get(keyTwo));
						} else {
							base.put(keyTwo, toadd.get(keyTwo));
						}
						m1.put(key, base);
					}
				} else {
					m1.put(key, m2.get(key));
				}
			}
			return m1;
		}
	}

	class KeyWordCommutativeRelation implements Function<Map<String, Integer>, Map<String, Map<String, Integer>>> {
		@Override
		public Map<String, Map<String, Integer>> call(Map<String, Integer> m1) throws Exception {
			Map<String, Map<String, Integer>> res = new HashMap<>();
			List<String> keyList = new ArrayList<>(m1.keySet());
			int listLength = keyList.size();
			for (int i = 0; i < listLength; i++) {
				String keyOne = keyList.get(i);
				Map<String, Integer> temp = new HashMap<>();
				for (int j = 0; j < listLength; j++) {
					if (i != j) {
						String keyTwo = keyList.get(j);
						temp.put(keyTwo, 1);
					}
				}
				res.put(keyOne, temp);
			}
			return res;
		}
	}

	public Map<String, Map<String, Integer>> hashTagRelCommutative(List<NewsVO> list) {
		SparkConf conf = new SparkConf().setAppName("spark_mapreduce").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<NewsVO> newsdata = sc.parallelize(list);
		JavaRDD<Map<String, Integer>> keywords = newsdata.map(new MapKeyword());
		JavaRDD<Map<String, Map<String, Integer>>> keywordRel = keywords.map(new KeyWordCommutativeRelation());
		Map<String, Map<String, Integer>> res = keywordRel.reduce(new CommutativeRelationReducer());
		sc.close();
		return res;
	}

	class MapKeyword implements Function<NewsVO, Map<String, Integer>> {
		@Override
		public Map<String, Integer> call(NewsVO nv) throws Exception {
			return htg.getHashtags(nv);
		}
	}

	class ReduceKeyWord implements Function2<Map<String, Integer>, Map<String, Integer>, Map<String, Integer>> {
		@Override
		public Map<String, Integer> call(Map<String, Integer> m1, Map<String, Integer> m2) throws Exception {
			for (String key : m2.keySet()) {
				if (m1.containsKey(key)) {
					m1.put(key, m1.get(key) + m2.get(key));
				} else {
					m1.put(key, m2.get(key));
				}
			}
			return m1;
		}
	}

	public Map<String, Integer> hashTagMap(List<NewsVO> list) {
		SparkConf conf = new SparkConf().setAppName("spark_mapreduce").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<NewsVO> newsdata = sc.parallelize(list);
		JavaRDD<Map<String, Integer>> keywords = newsdata.map(new MapKeyword());
		/*
		Alternatively, Reduce함수만 사용하는 것도 가능하다.
		List<Map<String,Integer>>keyword_dict=new ArrayList<>();
		for(NewsVO nv:list) {
			keyword_dict.add(htg.getHashtags(nv));
		}
		JavaRDD<Map<String, Integer>> keywords=sc.parallelize(keyword_dict);
		Map<String, Integer> res = keywords.reduce(new ReduceKeyWord());
		sc.close();
		*/
		Map<String, Integer> res = keywords.reduce(new ReduceKeyWord());
		sc.close();
		return res;
	}


	class NonCommutativeRelationReducer implements
			Function2<Map<String, Map<String, Double>>, Map<String, Map<String, Double>>, Map<String, Map<String, Double>>> {
		@Override
		public Map<String, Map<String, Double>> call(Map<String, Map<String, Double>> m1,
				Map<String, Map<String, Double>> m2) throws Exception {
			for (String key : m2.keySet()) {
				if (m1.containsKey(key)) {
					Map<String, Double> base = m1.get(key);
					Map<String, Double> toadd = m2.get(key);
					for (String keyTwo : toadd.keySet()) {
						if (base.containsKey(keyTwo)) {
							base.put(keyTwo, (base.get(keyTwo) + toadd.get(keyTwo)) / 2);
						} else {
							base.put(keyTwo, toadd.get(keyTwo));
						}
					}
					m1.put(key, base);
				} else {
					m1.put(key, m2.get(key));
				}
			}
			return m1;
		}
	}

}
