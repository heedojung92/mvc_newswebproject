<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="com.vo.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title id="title_disp"></title>
<script src="assets/js/jquery.min.js"></script>
			<script src="assets/js/util.js"></script>
			<script src="assets/js/jquery.scrolly.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/main.js"></script>
			<link rel="stylesheet" href="assets/css/main.css" />
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
<link rel="stylesheet" type="text/css" href="/wordcloud/mycloudcolor.css" />
<script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>
<%
List<NewsVO>relNews=(List<NewsVO>)(request.getAttribute("relnews"));
Set<NewsVO>simNews=(Set<NewsVO>)(request.getAttribute("cossim"));
%>    
<style>
.floating { text-align:left; position:absolute;  top:20%; left:72%; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; overflow: hidden;" 
}
button {
	color: white;
	box-shadow: rgba(30, 22, 54, 0.4) 0 0px 0px 2px inset;
		-webkit-transition: all 200ms cubic-bezier(0.390, 0.500, 0.150, 1.360);
	-moz-transition: all 200ms cubic-bezier(0.390, 0.500, 0.150, 1.360);
	-ms-transition: all 200ms cubic-bezier(0.390, 0.500, 0.150, 1.360);
	-o-transition: all 200ms cubic-bezier(0.390, 0.500, 0.150, 1.360);
	transition: all 200ms cubic-bezier(0.390, 0.500, 0.150, 1.360);
	display: block;
	margin: 20px auto;
	max-width: 180px;
	text-decoration: none;
	border-radius: 4px;
	padding: 20px 30px;

}

button:hover {
	font-color: white;
	box-shadow: rgba(30, 22, 54, 0.7) 0 0px 0px 40px inset;
	
}

body{
background-color:#fafafa;
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
#ul_line {
  padding: 0;
  margin: 0 0 0 0;
}
.li_line {
  display: inline-block;
}
</style>
<script type="text/javascript">
var tagdict=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("tagdict") %>));
var newsvo=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("news") %>));	
var isMember=<%=(String)request.getAttribute("isUser") %>;
var cossimNews=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("cossimnews") %>));
var jaccardsimNews=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("jaccardsimnews") %>));
var euclidsimNews=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("euclidsimnews") %>));
var manhattansimNews=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("manhattansimnews") %>));


var choice="코사인";
$('#title_disp').html(newsvo.news_title);
function generateSimNews(){
	var newslist;
	var simtitle=document.getElementById("sim_title");
	var simnews_div=document.getElementById("simnews");
	simnews_div.innerHTML="";
	if(choice.includes("코사인")){
		newslist=cossimNews;
		simtitle.innerHTML="코사인 유사도 기반 추천 뉴스";
	}else if(choice.includes("자카드")){
		newslist=jaccardsimNews;
		simtitle.innerHTML="자카드 유사도 기반 추천 뉴스";
	}else if(choice.includes("유클리드")){
		newslist=euclidsimNews;
		simtitle.innerHTML="유클리드 거리 기반 추천 뉴스";
	}else{
		newslist=manhattansimNews;
		simtitle.innerHTML="맨하탄 거리 기반 추천 뉴스";
	}	
	for(var i=0;i<newslist.length&&i<8;i++){
		var brtag=document.createElement("br");
		var atag=document.createElement("a");
		atag.style.cssText="text-decoration:none;font-size:19px;line-height:45px;";
		atag.setAttribute("href","/news/newsbody.do?newsno="+newslist[i].news_no);
		atag.innerHTML=newslist[i].news_title;
		simnews_div.appendChild(atag);
		simnews_div.appendChild(brtag);
		
	}
}

function generateCloudWithArr(arr){
	word_set=[];
	for(var key in arr){
		word_set.push({text:key,weight:arr[key]})
	}
	$("#keyword_freq").hide();
	$("#keyword_stat").jQCloud(word_set);		
};
function scrapHandler(e){
	var htmlText=e.innerHTML;
	if(htmlText.includes("취소")){
		delScrap();
	}else{
		if(isMember!=true){
			alert("로그인이 필요한 기능입니다.");
			location.href="/news/connect_mypage.do?fromnewsbody="+newsvo.news_no;
		}else{
			doScrap();
			var prevcnt=Number($("#scrapcnt").html());
			$("#scrapcnt").html(prevcnt+1);
			$("#scrap").html("<h4 style:'color:#727a82;'>스크랩<br>취소</h4>");
		}
	}
}
function doScrap(){
	$.ajax({
		url:"doscrap.do",
		type:"POST",
		data:{"newsno":newsvo.news_no},
		success:function(responseData){
			alert("스크랩 되었습니다");
			$("#scrap").html("<h4 style:'color:#727a82;'>스크랩 취소</h4>");
		},
		error:function(responseData){
			alert("error");
		}
	})
}
function delScrap(){
	$.ajax({
		url:"delscrap.do",
		type:"POST",
		data:{"newsno":newsvo.news_no},
		success:function(responseData){
			alert("스크랩 취소 되었습니다");
			$("#scrap").html("<h4 style:'color:#727a82;'>스크랩</h4>");
			var prevcnt=Number($("#scrapcnt").html());
			$("#scrapcnt").html(prevcnt-1);
		},
		error:function(responseData){
			alert("error");
		}
	})
}
function generateChartWithArr(arr,div_id,title_text,data_type){
	dps=[];					
	for(var key in arr){
		dps.push({label:key,y:Number(arr[key])});
	}
	chart = new CanvasJS.Chart(div_id, {
		title:{
			text: title_text,
			fontSize: 25,
		},
		data: [              
		{
			type: data_type,
			click: chartClicked,
			dataPoints: dps
		}
		]
	});
	chart.render();
	var outer=document.getElementById("keyword_stat");	
	var viewchange=document.createElement("button");
	viewchange.setAttribute("id","viewchange");
	viewchange.style.cssText="background-color:white;position:fixed;top:830px;right:200px;";
	viewchange.innerHTML="<h3 style:'black; margin-left:-15px;'>형태소 분석</h3>";
	outer.appendChild(viewchange);
	document.getElementById("viewchange").addEventListener("click",function(e){
		if(e.target.innerHTML.includes("형태소")){
			e.target.innerHTML="<h4 style:'black; margin-left:-15px;'>주요 키워드</h4>";
			toWC(newsvo.news_title+" "+newsvo.news_body);
		}else{
			e.target.innerHTML="<h3 style:'black; margin-left:-15px;'>형태소 분석</h3>";
			$("#keyword_stat").empty();	
			var div=document.createElement("div");
			div.setAttribute("id","keyword_freq");
			div.style.cssText="width: 500px; height: 500px; top:25%;left:72%;border: 1px solid #ccc; position:fixed;";
			document.getElementById("keyword_stat").appendChild(div);
			generateChartWithArr(tagdict,"keyword_freq","키워드 빈도수","column");
			$("#keyword_freq").show();
		}
		
	})
	
}
function toWC(content){
	$.ajax({
		url:"wordcloud.do",
		type:"POST",
		data:{
			"news_content":content,
			"news_title":newsvo.news_title
		},
		dataType:"json",
		success:function(responseData){
			var arr=JSON.parse(JSON.stringify(responseData));
			generateCloudWithArr(arr);
		},
		error:function(responseData){
			alert("error!");
		}
	})
}

function chartClicked(e){
	var initials=document.getElementsByTagName("span");
	for (var i=0; i<initials.length; i++) {
		  initials[i].style.color = "black";
		  initials[i].style.fontSize = "medium";
		  initials[i].style.fontWeight = "normal";	 
	}			
	var tag=e.dataPoint.label;
	var matches = document.getElementsByClassName(tag);
	for (var i=0; i<matches.length; i++) {
	  matches[i].style.color = "blue";
	  matches[i].style.fontSize = "large";
	  matches[i].style.fontWeight = "600";
	}
}
function replaceAll(str, searchStr, replaceStr) {
	return str.split(searchStr).join(replaceStr);
}

window.onload = function(){
	if(newsvo.news_imgurl==null){
		document.getElementById("news_img").style.display="none";
	}else{
		var imgdiv=document.getElementById("news_img");
		var img=document.createElement("IMG");
		img.setAttribute("src",newsvo.news_imgurl);
		img.style.cssText ="width: 95%; height: 80%; border-radius:25px; padding-top:1%; padding-bottom:1%";
		imgdiv.appendChild(img);
	}
	document.getElementById("news_title").innerHTML="<a href='"+newsvo.news_url+"'>"+"【 "+newsvo.news_pub+" 】 "+newsvo.news_title+"</a>";
	var datestring=newsvo.news_date;
	var date=datestring.split("@")[0];
	var time=datestring.split("@")[1];
	document.getElementById("news_date").innerHTML=date+" "+time.substring(0,2)+ "시"+time.substring(2)+"분";
	var tagcnt=0;
	var mybody=newsvo.news_body;
	for(var key in tagdict){
		tagcnt++;
		mybody=replaceAll(mybody, key,"<span class='"+key+"'>"+key+"</span>");		
	}	
	document.getElementById("news_body").innerHTML=mybody;
	if(tagcnt!=0){
		generateChartWithArr(tagdict,"keyword_freq","키워드 빈도수","column");
	}else{
		var statdiv=document.getElementById("keyword_freq");
		statdiv.remove();
	}
	generateSimNews();	
	var simchoices = document.getElementsByClassName("simchoice");
	for (var i = 0; i < simchoices.length; i++) {
		simchoices[i].addEventListener('click',function(e){
			choice=e.target.innerHTML;
			generateSimNews();
		});
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

<div id="desc-left" style="width: 900px; height: 100px; margin-left:10px; position:relative; left:31.7%;
 margin-right:50px; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc;">
<br>
<h3 id="news_title"></h3>
<div style="display: flex;flex-direction: row;">
<h4 id="news_date" style="position:relative; left:75%; margin-top:-10px;"></h4>

</div>
</div>
<div id="desc-right" style="width: 290px; height: 100px; border: 1px solid #ccc; display: flex; flex-direction: row; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; position:relative; left:56%; margin-top:1%;">
<button id="scrap" onClick="scrapHandler(this)" style="width: 100px; height: 60px; border-radius:25px; background-color:white; position:relative; left:7%;">
<%
if(request.getAttribute("scrap")!=null){
	out.print("<h4 style:'color:#727a82;'>스크랩<br>취소</h4>");
}else{
	out.print("<h4 style:'color:#727a82;'>스크랩</h4>");
}
%>
</button>
<p style="color:#727a82; font-weight:bold; position:relative; left:15.5%; top:22%; width:120px;">
조회수 : <%= (String)request.getAttribute("viewcnt")%></p>
<p style="color:#727a82; font-weight:bold; position:relative; top:50%; right:17.5%; width:120px;">스크랩수 :<span id="scrapcnt"> <%=(String)request.getAttribute("scrapcnt")%></span></p>
</div>

<div id="news_img" style=" position:relative; left:32%; margin-top:1%; width: 900px; height: 55%; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc;"></div>
<div id="sim_news_div" style="position:absolute; top:57%;bottom:26%;left:5.5%; width: 650px; height: 600px; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc;"">
<h2 id="sim_title">코사인 유사도 기반 추천 뉴스</h2>
<ul id="ul_line">
<h4><h3>[ 유사도 선택 ]</h3><li class="li_line"><h4 id="cossim" class="simchoice">코사인 유사도</h4></li>&nbsp&nbsp&nbsp&nbsp&nbsp<li class="li_line"><h4 id="jacsim" class="simchoice">자카드 유사도</h4></li>&nbsp&nbsp&nbsp&nbsp&nbsp<li class="li_line"><h4 id="euclid" class="simchoice">유클리드 거리</h4></li>&nbsp&nbsp&nbsp&nbsp&nbsp<li class="li_line"><h4 id="manhattan" class="simchoice">맨하탄 거리</h4></li></h4>  
</ul>

<div id="simnews">
</div>

</div>
<div id="news_body" style="position:relative; left:32%;margin-top:1%;  width: 900px; height: 100%; color:#727a82; font-size:20px; padding: 1.5%; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc;">
본문 들어갈 곳
</div>
<div class="floating" >
<div id="keyword_stat" style="background-color:white;width: 600px; height: 670px; left:70%;border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; position:fixed;">
<div id="keyword_freq" style="border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc;width: 500px; height: 500px; top:25%;left:72%;border: 1px solid #ccc; position:fixed;"></div></div>

</div>
<div id="right-1" style=" position:absolute; top:19%; left:5.5%;width: 650px; height: 400px; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc;">
<h2>이 뉴스를 조회한 사람들이 많이 본 뉴스</h2>
<%
for(int i=0;i<relNews.size();i++){
	out.print("<a style='text-decoration:none;' href='/news/newsbody.do?newsno="+relNews.get(i).getNews_no()+"'>");
	out.print("<h4>");
	out.print(relNews.get(i).getNews_title());	
	out.print("</h4></a>");
}
%>
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