<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="com.vo.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%=(String)request.getAttribute("input") %> 검색 결과</title>
<script src="assets/js/jquery.min.js"></script>
			<script src="assets/js/util.js"></script>
			<script src="assets/js/jquery.scrolly.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/main.js"></script>
			<link rel="stylesheet" href="assets/css/main.css" />
<style type="text/css">
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

</style>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
    <script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script type="text/javascript">
var searchtag="<%=(String)request.getAttribute("input") %>";
var datebegin="<%=(String)request.getAttribute("date_start") %>";
var dateend="<%=(String)request.getAttribute("date_end") %>";
function cleanPage(){
	var pagetags=document.getElementsByClassName("page_span");
	for (var i=0; i<pagetags.length; i++) {
		pagetags[i].style.fontWeight = "normal";		 
	}
}

function generatePageNum(div_id,newscontainer_id,start_page,news_arr){
	var total_news=news_arr.length;
	var prev_news_total=(start_page-1)*12;
	var news_display=total_news-prev_news_total;
	var page_display=Math.floor(news_display/12)+1;
	
	var page_div=document.getElementById(div_id);
	page_div.innerHTML="";
	if(start_page>10){
		var prev_tag=document.createElement("SPAN");
		prev_tag.addEventListener("click",function(){
			start_page=start_page-10;
			generatePageNum(div_id,newscontainer_id,start_page,news_arr)
		});
		prev_tag.innerHTML="이전";
		prev_tag.style.cssText ="font-size:30px; margin-right: 30px; margin-left: 30px;";
		page_div.appendChild(prev_tag);
	}
	for(var i=0;i<page_display&&i<10;i++){
		var page_tag=document.createElement("SPAN");		
		page_tag.innerHTML=start_page+i;
		page_tag.id=start_page+i;
		page_tag.setAttribute("class","page_span");
		page_tag.addEventListener("click",function(e){
			pageClicked(news_arr,e.target.innerHTML,newscontainer_id);
		});		
		page_tag.style.cssText ="font-size:30px; margin-right: 30px; margin-left: 30px;";
		page_div.appendChild(page_tag);
	}
	if(news_display>120){
		var next_tag=document.createElement("SPAN");
		next_tag.addEventListener("click",function(){
			start_page=start_page+10;
			generatePageNum(div_id,newscontainer_id,start_page,news_arr);
		});
		next_tag.innerHTML="다음";
		next_tag.style.cssText ="font-size:30px; margin-right: 30px; margin-left: 30px;";
		page_div.appendChild(next_tag);
		
	}
}
function pageClicked(news_arr,pagenum,newscontainer_id){
	//alert(news_arr.length);
	document.getElementById(newscontainer_id).innerHTML="";
	//cleanSpan();
	cleanPage();
	document.getElementById(pagenum).style.fontWeight = "600";

	var news_container=document.getElementById(newscontainer_id);
	var news_start=(pagenum-1)*12;
	var news_end=news_start+12;
	for(var i=news_start;i<news_end;i++){
		if(news_arr[i]!=null&&news_arr[i].news_imgurl!=null){
			var newsdiv=document.createElement("DIV");
			newsdiv.style.cssText ="height:100%; width:1198px;border-top:1px solid #ccc;display:flex; flex-direction: row; text-decoration:none;";
			var newsimg=document.createElement("IMG");
			newsimg.setAttribute("src",news_arr[i].news_imgurl);
			newsimg.style.cssText ="height:140px;width:150px;";
			var newsdesc=document.createElement("DIV");
			newsdesc.innerHTML="<a href='"+"newsbody.do?newsno="+news_arr[i].news_no+"'style='text-decoration:none; color:#727a82'>"+"<h2 style='text-decoration:none; font-size:25px'>"+news_arr[i].news_title+"</h2>"
								+"<br>"+news_arr[i].news_summ+"</a>";
			newsdesc.style.cssText ="height:150px;width:1050px; border-bottom:1px solid #ccc;text-decoration:none; color:#727a82";
			newsdiv.appendChild(newsimg);
			newsdiv.appendChild(newsdesc);
			news_container.appendChild(newsdiv);
		}else{
			if(news_arr[i]!=null){
				var newsdesc=document.createElement("DIV");
				newsdesc.innerHTML="<a href='"+"newsbody.do?newsno="+news_arr[i].news_no+"'style='text-decoration:none; color:#727a82'>"+"<h2 style='text-decoration:none; font-size:25px'>"+news_arr[i].news_title+"</h2>"
								+"<br>"+news_arr[i].news_summ+"</a>";
				newsdesc.style.cssText ="height:100%px;width:1198px;border-bottom:1px solid #ccc;text-decoration:none;";
				news_container.appendChild(newsdesc);
				}
		}
		
	}
}

var newsdata=JSON.parse(JSON.stringify(<%=request.getAttribute("search_res") %>));
var search_res=document.getElementById("search_result");
function replaceAll(str, searchStr, replaceStr) {
	return str.split(searchStr).join(replaceStr);
}
window.onload = function(){	

		
	generatePageNum("paging","search_result",1,newsdata);
	pageClicked(newsdata,1,"search_result");
	$("#search_btn").click(function(){
		document.getElementById("search_text").style.display="none";
		document.getElementById("hidden").style.display="block";
		$("#search_news").click(function(){
			var searchtype=$("#search_type").val();
			var startdate=$('#begin_date').val();
			var enddate=$('#end_date').val();
			var query=$("#query").val();
			if(searchtype==""||startdate==""||enddate==""||query==""){
				alert("제대로 입력");
			}
			location.href="searchnews.do?query="+searchtype+"-"+query+"&date_start="+replaceAll(startdate,"-",".")+"&date_end="+replaceAll(enddate,"-",".");
		});
	})
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

<br>
<div id="new_search">

<div id="search_text">
<h3><span id="date_start" style="color:#727a82;position:relative; left:-8%; top:5%;"><%=(String)request.getAttribute("date_start") %> ~ </span>
<span id="date_end"style="color:#727a82;position:relative; left:-8%;top:5%;"><%=(String)request.getAttribute("date_end") %></span>
<span><h2>【 <%=(String)request.getAttribute("input") %> 】  <%=request.getAttribute("search_type") %>  뉴스  검색 결과</b></h2><input id="search_btn" type="button" value="새로 검색 하기"> </span></h3>
</div>

<div id="hidden" style="display:none;position:relative; left:38%; top:20%;">
<div style="display: flex;flex-direction: row;">
<div class="date_choice">
<h3 style="color:#727a82;">시작 일자 : <input type="date" id="begin_date" style="color:#727a82;text-align:right;position: relative;border-radius: 20px; "></h3>
<h3>검색방법 선택: </h3>
<select name="search_type" id="search_type" size="4" style="height:60px;">
  <option value="검색어" style="font-size:22px;color:black;">검색어로 검색</option>
  <option id="news_cat" value="분야" style="font-size:22px;color:black;">카테고리 검색</option>
  <option value="언론사" style="font-size:22px;color:black;">언론사별 검색</option>
  <option value="기업" style="font-size:22px;color:black;">기업별 검색</option>
</select>
</div> 
<div style="margin-left:20px;">
<h3 style="color:#727a82;">&nbsp&nbsp&nbsp 종료 일자 : <input type="date" id="end_date" style="color:#727a82; text-align:right; position: relative;border-radius: 20px;"></h3>
<h3>검색어 입력: </h3>
<input type="text" id="query" name="query" value="" style="height:60px;font-size:20px;color:#727a82;"><br>
<input type="button" value="검색" id="search_news" style="background-color:#727a82;position:fixed; top:38.8%;left:47%;height:40px; width: 150px; border-radius: 20px; ">
</div>
</div>
</div>

</div>


</div>
<br><br>
<div id="search_result" style="width: 1200px; height: 100%; position:relative; left: 25%; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; ">

</div>
<div id="paging" style="color:#727a82; position:relative; left:2%; top:10px;">

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