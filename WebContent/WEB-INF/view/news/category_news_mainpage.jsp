<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="com.vo.*" %>
<%@page import="com.util.*" %>
<%
List<NewsVO>c1=(List<NewsVO>)request.getAttribute("259101");
List<NewsVO>c2=(List<NewsVO>)request.getAttribute("258101");
List<NewsVO>c3=(List<NewsVO>)request.getAttribute("261101");
List<NewsVO>c4=(List<NewsVO>)request.getAttribute("771101");
List<NewsVO>c5=(List<NewsVO>)request.getAttribute("262101");
List<NewsVO>c6=(List<NewsVO>)request.getAttribute("263101");
List<NewsVO>c7=(List<NewsVO>)request.getAttribute("226105");
List<NewsVO>c8=(List<NewsVO>)request.getAttribute("230105");
%>    

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%=new DateStringHelper().getCurrentDate()+" 분야별 뉴스 " %></title>
<script src="assets/js/jquery.min.js"></script>
			<script src="assets/js/util.js"></script>
			<script src="assets/js/jquery.scrolly.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/main.js"></script>
			<link rel="stylesheet" href="assets/css/main.css" />			
    <link rel="stylesheet" type="text/css" href="/wordcloud/mycloudcolor.css" />
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
    <script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
    
<style>
body{
background-color:#fafafa;
}
.row{
  width: 1800px;
  display: flex;
  flex-direction: row;
}
.header_section{
  width: 38%;
  position:relative;
  left: 33%;
  display: flex;
  flex-direction: center;
  justify-content: center;
  margin-top:1%;
  margin-bottom:1%;
  background:white;
  align-items: center;
  padding-top:25px;
  border-bottom: solid 3px;
  border-right: solid 3px;
  border-radius: 15px;
  }
  h7 {
  
  		color: #727a82;
		font-weight: 700;
		font-family: "Montserrat", sans-serif;
		line-height: 1.5;
		margin: 0 0 1em 0;
		font-size: 1.25em;
  		padding: 0 .3em;
  		transition: all 1.0s;
}
h7:hover {
  color: #fff;
  background-color: #c0c0c0;
  border-radius:10px;
}

</style>
<script>
var tagdict=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("tagdict") %>));
var hashtag01=tagdict.finance_tags;
var hashtag02=tagdict.stock_tags;
var hashtag03=tagdict.industry_tags;
var hashtag04=tagdict.venture_tags;
var hashtag05=tagdict.globecon_tags;
var hashtag06=tagdict.genecon_tags;
var hashtag07=tagdict.internet_tags;
var hashtag08=tagdict.it_tags;
var newsdata=JSON.parse(JSON.stringify(<%=request.getAttribute("maincategory_news") %>));

function getSid(catno){
	var sid;
	switch(catno){
	case "cat1" : sid="259101"; break;
	case "cat2" : sid="258101"; break;
	case "cat3" : sid="261101"; break;
	case "cat4" : sid="771101"; break;
	case "cat5" : sid="262101"; break;
	case "cat6" : sid="263101"; break;
	case "cat7" : sid="226105"; break;
	case "cat8" : sid="230105"; break;
	default : sid="000000";
	}
	return sid;
}

window.onload = function(){   
	
function generateCloudWithArr(arr,id,category){
	word_set=[];
	for(var key in arr){
		word_set.push({text:key,weight:arr[key],handlers:{
			click:function(word){
				$.ajax({
					url:"cat_news_from_tag.do",
					type:"POST",
					data:{"tag_clicked":word.target.textContent,
						"sid_choice":getSid(category)	
					},
					dataType : "json",
					success:function(responseData){
						var title_id=category+"_title";
						var cat=document.getElementById(title_id).innerHTML;
						if(cat.includes("카테고리에서")){
							cat=cat.split("카테고리에서")[0];
						}
						if(cat.includes("오늘의")){
							cat=cat.split("오늘의")[1].trim();
						}
						document.getElementById(category+"_title").innerHTML=cat+" 카테고리에서 선택된<br><span style='color:red;'> #"+ word.target.textContent+ "</span> 관련 뉴스";
						addNews(category+"_content",JSON.parse(JSON.stringify(responseData)));						
					},
					error:function(responseData){
						alert("error");
					}
				})
			}
		}})
	}
	$(id).empty();
	$(id).jQCloud(word_set);	
}

function addNews(containerid,arr){
	document.getElementById(containerid).innerHTML="";
	var mydiv=document.getElementById(containerid);
	for(var key in arr){
		var atag=document.createElement('A');
		atag.innerHTML = arr[key];
		var att=document.createAttribute('href');
		att.value="/news/newsbody.do?newsno="+key;
		atag.setAttributeNode(att);
		atag.style.cssText="text-decoration:none;";
		var br = document.createElement("BR");
		atag.append(br);
		mydiv.append(atag);
		var br02 = document.createElement("BR");
		mydiv.append(br02);
	}

}
    
generateCloudWithArr(hashtag01,"#cat1_wc","cat1");
generateCloudWithArr(hashtag02,"#cat2_wc","cat2");
generateCloudWithArr(hashtag03,"#cat3_wc","cat3");
generateCloudWithArr(hashtag04,"#cat4_wc","cat4");
generateCloudWithArr(hashtag05,"#cat5_wc","cat5");
generateCloudWithArr(hashtag06,"#cat6_wc","cat6");
generateCloudWithArr(hashtag07,"#cat7_wc","cat7");
generateCloudWithArr(hashtag08,"#cat8_wc","cat8");

for(var i=1;i<=8;i++){
	var id="cat"+i+"_title";
	var cat=document.getElementById(id).innerHTML.split("뉴스")[0].trim();
	var test=document.createElement("h2");
	test.innerHTML=cat+" 키워드";
	var wctitle="cat"+i+"_wc";
	document.getElementById(wctitle).appendChild(test);
}
}

</script>

</head>
<body>

<header id="header">			
				<nav class="left">
					<a href="#menu"><span>Menu</span></a>
				</nav>
				<img src="images/news.png" height="60" width="65"/>
				<a href="index.jsp" class="logo">#MINEWS</a>
				<nav class="right">
					<a href="#" class="button alt">Log in</a>
				</nav>
		
			</header>
		<!-- Menu -->
			<nav id="menu">
				<ul class="links">
					<li><a href="index.jsp"># HOME</a></li>
					<li><a href="/news/news_main.do"># NEWS</a></li>
					<li><a href="/news/connect_mypage.do"># MYPAGE</a></li>
				</ul>
				<ul class="actions vertical">
					<li><a href="#" class="button fit" >Login</a></li>
				</ul>
				
				

				
			</nav>

<div class="header_section">
<h7><a href="index.jsp" style="color:#727a82; text-decoration:none;">홈으로</a></h7>
<h7><a href="news_main.do" style="color:#727a82; text-decoration:none;">뉴스홈</a></h7>
<h7><a href='cat_news.do' style="color:#727a82; text-decoration:none;">카테고리별 뉴스</a></h7>
<h7><a href='comp_news.do' style="color:#727a82; text-decoration:none;">기업별 뉴스</a></h7>
<h7><a href='news_stat.do' style="color:#727a82; text-decoration:none;">뉴스 통계 보기</a></h7>
<h7><a href="hashtag_relation.do" style="color:#727a82; text-decoration:none;">키워드 관계 분석</a></h7>
</div>
<div id="main_content" style=" position:relative; left:15%; " >
<div id="cat1" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat1_news" style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat1_title" style="align-text: center;">오늘의 금융 뉴스</h1>
<div id="cat1_content" >

<%for(int i=0;i<8&&i<c1.size();i++){
		out.print("<a href='/news/newsbody.do?newsno="+c1.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c1.get(i).getNews_title());		
		out.print("</a>");
		out.print("<br>");		
}
%>
</div>
<br>
<p align="right"><a href="news_in_category.do?cat=259101" style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>
<div id="cat1_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>


<br>
<div id="cat2" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat2_news" style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat2_title" style="align-text: center;">오늘의 증권 뉴스</h1>
<div id="cat2_content">
<%for(int i=0;i<8&&i<c2.size();i++){
		out.print("<a href='/news/newsbody.do?newsno="+c2.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c2.get(i).getNews_title());			
		out.print("</a>");
		out.print("<br>");		
}
%>
</div>
<p align="right"><a href="news_in_category.do?cat=258101" style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>

<div id="cat2_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>


<br>

<div id="cat3" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat3_news"style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat3_title" style="align-text: center;">오늘의 산업/재계 뉴스</h1>
<div id="cat3_content">
<%for(int i=0;i<8&&i<c3.size();i++){

		out.print("<a href='/news/newsbody.do?newsno="+c3.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c3.get(i).getNews_title());
		
		out.print("</a>");
		out.print("<br>");
		
}
%>
</div>
<p align="right"><a href="news_in_category.do?cat=261101"  style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>
<div id="cat3_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>


<br>
<div id="cat4" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat4_news" style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat4_title" style="align-text: center;">오늘의 중기/벤처 뉴스</h1>
<div id="cat4_content">
<%for(int i=0;i<8&&i<c4.size();i++){

		out.print("<a href='/news/newsbody.do?newsno="+c4.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c4.get(i).getNews_title());
		
		out.print("</a>");
		out.print("<br>");
		
}
%>
</div>
<p align="right"><a href="news_in_category.do?cat=771101"  style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>
<div id="cat4_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>


<br>
<div id="cat5" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat5_news" style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat5_title" style="align-text: center;">오늘의 글로벌 경제 뉴스</h1>
<div id="cat5_content">
<%for(int i=0;i<8&&i<c5.size();i++){

		out.print("<a href='/news/newsbody.do?newsno="+c5.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c5.get(i).getNews_title());	
		out.print("</a>");
		out.print("<br>");
		
}
%>
</div>
<p align="right"><a href="news_in_category.do?cat=262101"  style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>
<div id="cat5_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>

<br>

<div id="cat6" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat6_news" style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat6_title" style="align-text: center;">오늘의 경제 일반 뉴스</h1>
<div id="cat6_content">
<%for(int i=0;i<8&&i<c6.size();i++){

		out.print("<a href='/news/newsbody.do?newsno="+c6.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c6.get(i).getNews_title());	
		out.print("</a>");
		out.print("<br>");
		
}
%>
</div>
<p align="right"><a href="news_in_category.do?cat=263101"  style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>
<div id="cat6_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>


<br>
<div id="cat7" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat7_news" style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat7_title" style="align-text: center;">오늘의 인터넷/SNS 뉴스</h1>
<div id="cat7_content">
<%for(int i=0;i<8&&i<c7.size();i++){

		out.print("<a href='/news/newsbody.do?newsno="+c7.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c7.get(i).getNews_title());	
		out.print("</a>");
		out.print("<br>");
		
}
%>
</div>
<p align="right"><a href="news_in_category.do?cat=226105"  style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>
<div id="cat7_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>

<br>

<div id="cat8" class="row" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px;"> 

<div id="cat8_news" style="width: 900px; height: 500px; border: 1px solid #ccc; overflow:auto; background-color:white;">
<h1 id="cat8_title" style="align-text: center;">오늘의 IT 뉴스</h1>
<div id="cat8_content">
<%for(int i=0;i<8&&i<c8.size();i++){

		out.print("<a href='/news/newsbody.do?newsno="+c8.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold; font-size:20px;'>");
		out.print("<br>");
		out.print(c8.get(i).getNews_title());	
		out.print("</a>");
		out.print("<br>");
		
}
%>
</div>
<p align="right"><a href="news_in_category.do?cat=230105"  style='color:#727a82; text-decoration:none; font-weight: bold; font-size:24px; position: relative; left:40%;'>더보기 ></a></p>
</div>
<div id="cat8_wc" style="width: 900px; height: 500px; border: 1px solid #ccc;"></div>

</div>



</div>

<footer id="footer" style="margin-top:5%;">
				<div class="inner">
					<h2>#뉴스 정보의 중심 '자신'</h2>
					<ul class="actions">
						<li><span class="icon fa-phone"></span> <a href="#">#(010) 0000-0000</a></li>
						<li><span class="icon fa-envelope"></span> <a href="#">#heedo5252@gmail.com</a></li>
						<li><span class="icon fa-map-marker"></span>#서울, #남부터미널, #엔코아 플레이데이터</li>
					</ul>
				</div>
				<div class="copyright">
					우리 <a href="https://templated.co">4조</a>. 화이팅
				</div>
			</footer>
</body>
</html>