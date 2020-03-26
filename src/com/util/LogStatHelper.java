package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.vo.LogVO;

public class LogStatHelper {

	// 로그 데이터를 읽어와서 필요한 데이터만 나눈 뒤 항목별로 분철하여 VO 에 넣는다.
	public LogVO splitLogDataToVo(String line) {
		LogVO vo = new LogVO();
		String ipAddress = line.split(" ")[0];
		String requestPage = line.split(" ")[5];
		String requestMethod = line.split(" ")[4];
		String httpStatus = line.split(" ")[3];

		int intHttpStatus = 0;
		try {
			intHttpStatus = Integer.parseInt(httpStatus);
			if (intHttpStatus != 200) {
				if ((!requestPage.contains(".do")) || (!requestPage.contains(".jsp"))) {
					return null;
				}
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		// 날짜 연산
		String stringDate = line.split(" ")[1].split(":")[0].replace("[", "").replace("/", ".");
		String[] pattern = line.split(" ")[1].split(":");
		String stringTime = pattern[1] + ":" + pattern[2] + ":" + pattern[3].split(" ")[0];

		DateStringHelper log = new DateStringHelper();
		Map<String, String> month = log.monthMapping();

		String[] splitDate = stringDate.split("\\.");
		String months = month.get(splitDate[1]);

		stringDate = splitDate[2] + "-" + months + "-" + splitDate[0];

		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;

		try {
			date = transFormat.parse(stringDate + " " + stringTime);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("실패");
		}

		vo.setAccessorIpAddress(ipAddress);
		vo.setDate(date);
		vo.setHttpStatus(httpStatus);
		vo.setStringDate(stringDate);
		vo.setRequestMethod(requestMethod);
		vo.setStringTime(stringTime);
		vo.setRequestPage(requestPage);

		return vo;
	}

	public List<Map<String,Integer>> runLogAnalyze(String startDate) {
		List<String> dateList = new ArrayList<>();

		Date today = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		String todayAsString = df.format(today);

		try {
			DateStringHelper dateLog = new DateStringHelper();
			dateList = dateLog.getDaysBetween(startDate, todayAsString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		LogStatHelper log = new LogStatHelper();
		List<LogVO> logVoList = new ArrayList<>();
		
		List<Map<String,Integer>>hourMaps=new ArrayList<>();
		List<Map<String,Integer>>dayMaps=new ArrayList<>();
		for (String date : dateList) {
			logVoList = log.readLogFiles(date);
			if (logVoList != null) {
				Map<String, Integer> HourlyMR = new TreeMap<>(log.getVisitorPerHour(logVoList));
				//System.out.println(HourlyMR);
				hourMaps.add(HourlyMR);
				

				Map<String, Integer> dailyMR = new TreeMap<>(log.getVisitorPerDay(logVoList));
				dayMaps.add(dailyMR);
				//System.out.println(dailyMR);
			}
		}
		Map<String,Integer>res=new HashTagGenerator().mergeMap(hourMaps);
		Map<String,Integer>res002=new HashTagGenerator().mergeMap(dayMaps);
		Map<String,Integer>res02=new HashMap<>();
		for(String prevKey:res002.keySet()) {
			res02.put(prevKey.replaceAll("-", "."),res002.get(prevKey));
		}
		Map<String,Integer>hourlyres=new TreeMap<>(res);
		Map<String,Integer>dayres=new TreeMap<>(res02);
		List<Map<String,Integer>>logstat=new ArrayList<>();
		logstat.add(hourlyres);logstat.add(dayres);

		return logstat;
		
	}

	// 시작일부터 오늘까지.
	// 로그 파일의 분석할 데이터만 잘라서 VO List 에 담아서 가져온다.
	public List<LogVO> readLogFiles(String fileName) {
		LogStatHelper log = new LogStatHelper();
		LogVO vo = new LogVO();

		List<LogVO> list = new ArrayList<LogVO>();

		BufferedReader br = null;

		String fileOneRep = fileName.replace(".", "-");
		String oneFileName = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\logs\\localhost_access_log."
				+ fileOneRep + ".txt";
		try {
			File file = new File(oneFileName);
			br = new BufferedReader(new FileReader(oneFileName));

			String line = null;

			while ((line = br.readLine()) != null) {
				vo = log.splitLogDataToVo(line);

				if (vo != null) {
					list.add(vo);
				} else {
					continue;
				}
			}

		} catch (FileNotFoundException fnfe) {
			//System.out.println(fileName + "일자의 파일을 찾을 수 없습니다.");
			//System.out.println("지정된 파일이 없으므로 건너뜁니다.");
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	// IP 별 탐색하는 사이트 내 주소 집합
	public Map<String, List<String>> getMovementsPerIP(List<LogVO> list) {

		// 키: 아이피 값:해당 키(아이피)가 이동한 페이지
		Map<String, List<String>> ipMovements = new HashMap<>();

		// 한 아이피가 이동한 페이지를 담을 배열
		List<String> value = new ArrayList<>();

		// 전에 추가한 아이피가 또 다음 키값으로 동일하게 들어올 수도 있기때문에 검사할 수 있는 체커
		Map<String, Integer> checker = new HashMap<>();

		// LogVO 의 리스트 인덱스 만큼 반복
		for (int index = 0; index <= list.size() - 1; index++) {
			// 아이피가 바뀔때마다 배열을 청소
			value = new ArrayList<String>();

			// 키벨류의 키 값은 해당 인덱스의 아이피 주소
			String key = list.get(index).getAccessorIpAddress();

			// 키 값이라고 key에 정의된 값이 이미 checker에 키값으로 있으면 다음 반복문으로 스킵.
			if (checker.containsKey(key)) {
				continue;
			} else {// 그렇지 않을 경우 새로운 키가 들어온 것이라 인지하고, 추가시키고 진행.
				checker.put(key, 1);
//					value.add(list.get(index).getRequestPage());
				// 시작 인덱스가 계속 바뀌는 것에 유동적 한계치를 정의
				// LogVO 의 요소수 :6
				// 아이피의 값이 같은지 다른지를 비교

				// 17 부터 1이 될때까지 하나씩 감소
				for (int i = list.size() - 1; i >= 1; i--) {
					String baseIp = list.get(index).getAccessorIpAddress();
					String varIp = list.get(i).getAccessorIpAddress();
					if (baseIp.equals(varIp)) { // 아이피의 값이 같으면 해당 키의 값추가
						String element = list.get(i).getRequestPage();
//						System.out.println("element" + element);
						value.add(element);
					}
				}
				ipMovements.put(key, value);
			}
		}
		// 아이피별 방문 페이지 기록을 볼 수 묵음.
		System.out.println(ipMovements);
		return ipMovements;
	}

	public List<String> getValidDate(List<LogVO> list) {
		Map<String, Integer> checker = new HashMap<>();
		List<String> selectedList = new ArrayList<>();
		for (LogVO data : list) {
			if (checker.containsKey(data.getStringDate())) {
				continue;
			} else {
				selectedList.add(data.getStringDate());
				checker.put(data.getStringDate(), 1);
			}
		}
		return selectedList;
	}

	public Map<String, Integer> getVisitorPerHour(List<LogVO> list) {

		LogVO vo = new LogVO();

		// 시간별 방문자 수 카운팅.
		Map<String, Integer> visitorPerHour = new HashMap<>();
		Map<String, Integer> checker = null;
		String stringHour = null;

		LogStatHelper log = new LogStatHelper();
		List<String> DateList = log.getValidDate(list);

		for (String date : DateList) {
			checker = new HashMap<>();
			int ipCount = 0;
			for (int hour = 0; hour <= 23; hour++) {
				ipCount = 0;
				for (int index = 0; index <= list.size() - 1; index++) {
					String elementHour = list.get(index).getStringTime().split(":")[0];
					String ipUnique = list.get(index).getAccessorIpAddress();

					if (checker.containsKey(ipUnique)) {
						checker.put(ipUnique, checker.get(ipUnique) + 1);
						continue;

					} else {
//						System.out.println("오호라");
//						System.out.println(elementHour);
//						System.out.println(hour);
						if (elementHour.equals(String.valueOf(hour))) {
//							System.out.println("증감");
							checker.put(ipUnique, 1);
							ipCount++;
						}
					}
				}
				if (hour > 9) {
					stringHour = String.valueOf(hour);
				} else {
					stringHour = String.valueOf("0" + hour);
				}

				//visitorPerHour.put(date + "@" + stringHour, ipCount);
				visitorPerHour.put(stringHour, ipCount);
			}
		}
		return visitorPerHour;
	}

	// 일자별 방문자 수 파악.
	public Map<String, Integer> getVisitorPerDay(List<LogVO> list) {
		Map<String, Integer> result = new HashMap<>();
		Map<String, Integer> checker = null;

		int diffIpCount = 0;
		LogStatHelper log = new LogStatHelper();
		List<String> DateList = log.getValidDate(list);
		
		for (String date : DateList) {
			checker = new HashMap<>();
			diffIpCount = 0;
			for (LogVO logVo : list) {
				if (checker.containsKey(logVo.getAccessorIpAddress())) {
					continue;
				} else {
					String IP = logVo.getAccessorIpAddress();
					checker.put(IP, 1);
					diffIpCount++;
				}
			}
			result.put(date, diffIpCount);
		}
		return result;
	}

	public static void main(String[] args) {
		LogStatHelper log = new LogStatHelper();
		List<Map<String,Integer>>res=log.runLogAnalyze("2019.11.23");
		System.out.println(res.get(0));
		System.out.println(res.get(1));
	}
}
