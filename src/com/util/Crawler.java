package com.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.biz.HashTagBiz;
import com.biz.NewsBiz;
import com.vo.HashTagVO;
import com.vo.NewsVO;

public class Crawler {
	static HashTagGenerator htg = new HashTagGenerator();

	public void setNewsBody(NewsVO nv) {
		String url = nv.getNews_url();
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String month_day = nv.getNews_date();
		Elements elements = doc.getElementsByClass("t11");
		for (Element e : elements) {
			String[] timestamp = e.text().split("오");
			String date = timestamp[0].trim();
			String mydate = date.substring(0, date.length() - 1);
			String datetime = "";
			if (timestamp[1].startsWith("후")) {
				String[] timearr = timestamp[1].split("후")[1].trim().split(":");
				int hr = Integer.valueOf(timearr[0]);
				if (hr != 12) {
					hr += 12;
				}
				String min = timearr[1];
				String time = hr + min;
				nv.setNews_date(mydate + "@" + time);
				break;
			} else {
				String[] timearr = timestamp[1].split("전")[1].trim().split(":");
				String hr = "";
				if (timearr[0].length() < 2) {
					hr = "0" + timearr[0];
				} else {
					hr = timearr[0];
					if (timearr[0].equals("12")) {
						hr = "00";
					}
				}
				String min = timearr[1];
				nv.setNews_date(mydate + "@" + hr + min);
				break;
			}
		}

		Element el = doc.getElementsByClass("end_photo_org").first();
		String s;
		if (el != null) {
			s = el.select("img").attr("src");
			nv.setNews_imgurl(s);
		}
		Elements els = doc.select("div._article_body_contents");
		els.select("a").remove();
		els.select("strong.media_end_summary").remove();
		els.select("em.img_desc").remove();
		els.select("span").remove();
		els.select("table").remove();
		els.select("div").remove();
		String[] bodies = els.html().toString().split("pt>");
		String body = "";
		if (bodies.length > 1) {
			body = els.html().toString().split("pt>")[1];
		}

		// String body = els.text();

		if (!body.contains(".") && doc.getElementsByAttributeValue("class", "vod_area").isEmpty()) {
			// System.out.println("dont add");
		} else {
			if (body.split("[.*].").length > 10) {
				nv.setNews_body(cleanBody(body).replaceAll("\n", "<br>"));
			}
		}
	}

	public static String cleanBody(String body) {
		String[] res = body.split("<br>");
		StringBuffer sb = new StringBuffer();
		for (String s : res) {
			s = s.replace("&nbsp;", "");
			s = s.replace("&#x2219;", "/");
			s = s.replace("&amp;", "&");
			if (s.length() > 5) {
				if (s.contains("재배포") || s.contains("<!")) {
					break;
				} else if (s.contains("기자") && s.contains("@")) {
					sb.append(s);
					break;
				} else if (s.contains("@") && s.contains(".") && s.contains("co")) {
					sb.append(s);
					break;
				}
				if (s.contains("<")) {
					s = s.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
					sb.append(s);
				} else {
					sb.append(s);
				}
			}
		}
		return sb.toString();
	}

	//시간까지 함께 고려해서 크롤링하는 메소드
	//입력받은 시간 이후의 당일 뉴스들을 크롤링한다. ex) if input=2020.02.02@2300, then 2020.02.02날짜의 2300이후 뉴스들만 크롤링
	public List<NewsVO> crawlNewsByDateAndTime(String datetime) {
		String date = datetime.split("@")[0];
		Set<NewsVO> list = new HashSet<>();
		String[] sidArr = { "259101", "258101", "261101", "771101", "262101", "263101", "226105", "230105" };
		String urlDate = date.replaceAll("\\.", "");
		Document doc = null;
		int totalcount = 0;
		Crawler contentCrawler = new Crawler();
		for (String sid : sidArr) {
			boolean stopcrawl = false;
			String first_title = "";
			int page = 1;
			int count = 0;
			String sid1 = sid.substring(0, 3);
			String sid2 = sid.substring(3);
			while (!stopcrawl) {
				count = 0;
				String searchURL = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm" + "&sid2=" + sid1 + "&sid1="
						+ sid2 + "&date=" + urlDate + "&page=" + page;
				try {
					doc = Jsoup.connect(searchURL).get();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Elements elements = doc.getElementsByClass("list_body newsflash_body").select("li").select("dl");

				for (Element el : elements) {
					NewsVO nv = new NewsVO();
					String title = el.select("dt").select("a").text();
					if (count == 0) {
						if (first_title.equals(title))
							stopcrawl = true;
						else
							first_title = title;
					}
					if (!(title.contains("[")) && !(title.contains("↑") && title.contains(":"))
							&& !(title.contains("↑") && title.contains(":")) && !(title.contains("동영상기사"))) {
						nv.setNews_sid(sid);
						nv.setNews_date(date);
						nv.setNews_title(el.select("dt").select("a").text());
						nv.setNews_url(el.select("dt").select("a").attr("href"));
						nv.setNews_summ(el.select("dd").get(0).getElementsByClass("lede").text());
						if (!nv.getNews_url().contains("sports")) {
							contentCrawler.setNewsBody(nv);
							nv.setNews_pub(el.select("dd").get(0).getElementsByClass("writing").text());
						}
						if (nv.getNews_body() != null && nv.getNews_body().length() > 200) {
							totalcount++;
							System.out.println(nv.getNews_date());
							if (datetime.compareTo(nv.getNews_date()) > 0) {
								System.out.println("dontadd");
								stopcrawl = true;
							} else {
								list.add(nv);
							}
						}
					}
					count++;
				}
				if (count != 20)
					stopcrawl = true;
				page++;
			}
		}
		return new ArrayList<>(list);
	}

	// input param date의 format은 YYYY.MM.DD형태이어야한다.
	public List<NewsVO> crawlNews(String date) {
		// List<NewsVO> list = new ArrayList<NewsVO>();
		Set<NewsVO> list = new HashSet<>();
		String[] sidArr = { "259101", "258101", "261101", "771101", "262101", "263101", "226105", "230105" };
		String urlDate = date.replaceAll("\\.", "");
		Document doc = null;
		int totalcount = 0;
		Crawler contentCrawler = new Crawler();
		for (String sid : sidArr) {
			boolean stopcrawl = false;
			String first_title = "";
			int page = 1;
			int count = 0;
			String sid1 = sid.substring(0, 3);
			String sid2 = sid.substring(3);
			while (!stopcrawl) {
				count = 0;
				String searchURL = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm" + "&sid2=" + sid1 + "&sid1="
						+ sid2 + "&date=" + urlDate + "&page=" + page;
				try {
					doc = Jsoup.connect(searchURL).get();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Elements elements = doc.getElementsByClass("list_body newsflash_body").select("li").select("dl");

				for (Element el : elements) {
					NewsVO nv = new NewsVO();
					String title = el.select("dt").select("a").text();
					if (count == 0) {
						if (first_title.equals(title))
							stopcrawl = true;
						else
							first_title = title;
					}
					if (!(title.replaceAll(" ", "").contains("동영상기사"))) {
						nv.setNews_sid(sid);
						nv.setNews_date(date);
						nv.setNews_title(el.select("dt").select("a").text());
						nv.setNews_url(el.select("dt").select("a").attr("href"));
						nv.setNews_summ(el.select("dd").get(0).getElementsByClass("lede").text());
						if (!nv.getNews_url().contains("sports")) {
							contentCrawler.setNewsBody(nv);
							nv.setNews_pub(el.select("dd").get(0).getElementsByClass("writing").text());

						}
						if (nv.getNews_body() != null && nv.getNews_body().length() > 200) {
							totalcount++;
							System.out.println(nv.getNews_date() + ": " + nv.getNews_url());
							list.add(nv);
						}
					}
					count++;
				}
				if (count != 20)
					stopcrawl = true;
				page++;
			}
		}
		return new ArrayList<>(list);
	}

	public static void main(String[] args) throws ParseException {
		List<String> dates = new DateStringHelper().getDaysBetween("2020.02.21", "2020.02.24");
		HashTagGenerator htg = new HashTagGenerator();
		HashTagBiz htb = new HashTagBiz();
		NewsBiz nb = new NewsBiz();
		Crawler c = new Crawler();
		List<NewsVO>list=c.crawlNewsByDateAndTime("2020.02.21@2300");
		System.out.println("begin");
		for(NewsVO nv:list) {
			System.out.println(nv.getNews_date());
		}


	}

}
