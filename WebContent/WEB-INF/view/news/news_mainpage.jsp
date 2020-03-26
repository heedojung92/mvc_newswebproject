<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="com.vo.*" %>
<%@page import="com.util.*" %>
<%
List<NewsVO>recentNews=(List<NewsVO>)request.getAttribute("recentNews");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%=new DateStringHelper().getCurrentDate()+" 일자 뉴스 " %></title>
    <script src="assets/js/jquery.min.js"></script>
			<script src="assets/js/util.js"></script>
			<script src="assets/js/jquery.scrolly.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/main.js"></script>
<style>
body{
background-color:#fafafa;
}
.header_section{
  width: 48%;
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

}
#main_content{
width: 100%;
  display: flex;
  flex-direction: row;

}
</style>
    <link rel="stylesheet" type="text/css" href="/wordcloud/mycloudcolor.css" />
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
    <script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>

<script>
var hashtag=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("hashtag") %>));
var mostviewnews=JSON.parse(JSON.stringify(<%=request.getAttribute("mostviewedtoday") %>));  
function generateChartWithArr(chart,arr,div_id,title_text,data_type){
	dps=[];
	var cnt=0;
	for(var key in arr){
		cnt++;
		if(cnt==15)
			break;
		dps.push({label:key,y:Number(arr[key])});
		}
	chart = new CanvasJS.Chart(div_id, {
		animationEnabled: true,
		animationDuration: 2000,
		title:{
			text: title_text,
			fontSize: 15,
			},
			data: [              
				{
					type: data_type,
					dataPoints: dps
					}]
			});
	chart.render();
};    
window.onload = function(){
	var chart;	
	generateChartWithArr(chart,hashtag,"graph_stat","오늘의 키워드 통계","bar");
	function generateCloudWithArr(arr){
		word_set=[];
		for(var key in arr){
			word_set.push({text:key,weight:arr[key]})
		}
		$('#hash_tag_today').empty();
		$('#hash_tag_today').jQCloud(word_set);		
	};
	generateCloudWithArr(hashtag);
	var mostviewed=document.getElementById("most_viewed");
	for(var i=0;i<mostviewnews.length&&i<6;i++){
		var row=document.createElement("span");
		row.innerHTML="<a href='"+"newsbody.do?newsno="+mostviewnews[i].news_no+"'style='text-decoration:none;'>"+"<h5>"+mostviewnews[i].news_title+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp조회수: "+mostviewnews[i].viewcnt+" </h5></a>";
		mostviewed.appendChild(row);
	}
	document.body.style.zoom="125%";
	}

</script>
<link rel="stylesheet" href="assets/css/main.css" />
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
<div class="header_section" style="border-radius: 15px; border-right: 3px solid #ccc; border-bottom: 3px solid #ccc; border-top: 1px solid #ccc; border-left: 1px solid #ccc;">
<h7><a href="index.jsp" style="color:#727a82; text-decoration:none;">홈으로</a></h7>
<h7><a href="news_main.do" style="color:#727a82; text-decoration:none;">뉴스홈</a></h7>
<h7><a href='cat_news.do' style="color:#727a82; text-decoration:none;">카테고리별 뉴스</a></h7>
<h7><a href='comp_news.do' style="color:#727a82; text-decoration:none;">기업별 뉴스</a></h7>
<h7><a href='news_stat.do' style="color:#727a82; text-decoration:none;">뉴스 통계 보기</a></h7>
<h7><a href="hashtag_relation.do" style="color:#727a82; text-decoration:none;">키워드 관계 분석</a></h7>
</div>

<div  style="width: 700px; height: 910px;  background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; text-decoration:none; position:relative; left:20%;">

<br>
<h2>헤드라인 뉴스</h2>
<%for(int i=0;i<14&&i<recentNews.size();i++){{
		out.print("<a href='/news/newsbody.do?newsno="+recentNews.get(i).getNews_no()+"'style='color:#727a82; text-decoration:none; font-weight: bold''>");
}
		out.print("<br>");
		out.print(recentNews.get(i).getNews_title());	
		out.print("</a>");
		out.print("<br>");
		
}
%>

</div>

<div id="most_viewed" style="width: 630px; height: 330px;  background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; position: relative; left:60%; top:-910px;overflow:hidden;">
<br><h2>Today 뉴스 조회 TOP</h2>
</div>

<div id="graph_stat" style="width: 630px; height: 250px; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; position: relative;top:350px; left:60%;top:-870px;">
</div>
<div id="hash_tag_today" style="width: 630px; height: 250px;  background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; position: relative; top:-830px; left:60%;">

</div>

<footer id="footer" style="margin-top: -35%;">
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