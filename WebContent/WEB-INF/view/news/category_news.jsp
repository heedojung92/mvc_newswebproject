<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.util.*" %>
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
<style type="text/css">
span{
color:#727A82;}
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

#main_container{
  width: 1400px; 
  height: 2100px;
  display: flex;
  flex-direction: row;
  border: 1px solid #ccc;
}

</style>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
    <script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>

<script>
var today=moment().format('YYYY-MM-DD');
var date_clicked=today;
var page_clicked=1;
var cat_clicked=<%=request.getAttribute("category")%>;
var newsdata=JSON.parse(JSON.stringify(<%=request.getAttribute("category_news") %>));
function generateDate(date_middle){
	var date_arr=[];
	for(var i=2;i>-3;i--){
		var date=moment(date_middle).add(i,"days").format('YYYY-MM-DD');
		date_arr.push(date);
	}		
	return date_arr;
}
function cleanSpan(){
	var spantags=document.getElementsByClassName("date_span");
	for (var i=0; i<spantags.length; i++) {
		spantags[i].style.fontWeight = "normal";		 
	}
}

function cleanPage(){
	var pagetags=document.getElementsByClassName("page_span");
	for (var i=0; i<pagetags.length; i++) {
		pagetags[i].style.fontWeight = "normal";		 
	}
}


function cleanCat(){
	var cattags=document.getElementsByClassName("cat");
	for (var i=0; i<cattags.length; i++) {
		cattags[i].style.color = "#727A82";	
	}
}


function generateDateTag(div_id,date_arr){
	var date_div=document.getElementById(div_id);
	date_div.innerHTML='';
	for(var date in date_arr){
		var date_tag=document.createElement("SPAN");
		date_tag.addEventListener("click",dateClicked);
		date_tag.innerHTML=date_arr[date];
		date_tag.setAttribute("class","date_span");
		date_tag.id=date_arr[date];
		date_tag.style.cssText ="font-size:30px; margin-right: 30px; ";
		date_div.appendChild(date_tag);
	}
}

function dateClicked(e){
	var date_span=e.target;
	var date_val=date_span.innerHTML;
	if(date_val==moment().format('YYYY-MM-DD')||date_val==moment().add(-1,"days").format('YYYY-MM-DD')){
		cleanSpan();
		date_span.style.fontWeight = "600";
		date_clicked=date_val;
		var cat_name=document.getElementById(cat_clicked).innerHTML;
		document.getElementById("category_name").innerHTML="<h1>"+cat_name+" 카테고리 "+date_clicked+" 일자 뉴스"+"</h1>"
		getNewsData();
	}else{
		cleanSpan();
		generateDateTag("date",generateDate(date_span.innerHTML));
		document.getElementById(date_val).style.fontWeight = "600";
		date_clicked=date_val;
		var cat_name=document.getElementById(cat_clicked).innerHTML;
		document.getElementById("category_name").innerHTML="<h1>"+cat_name+" 카테고리 "+date_clicked+" 일자 뉴스"+"</h1>"
		getNewsData();
	}
}

function getNewsData(){
	$.ajax({
		url:"generate_catnews.do",
		type:"POST",
		data:{
			"category":cat_clicked,
			"date_choice":date_clicked
		},
		dataType:"json",
		success:function(responseData){
			var news_array=JSON.parse(JSON.stringify(responseData));
			news_cnt=news_array.length;
			newsdata=news_array;
			generatePageNum("paging","news_container",1,newsdata);
			pageClicked(newsdata,1,"news_container");
			cleanSpan();
			document.getElementById(date_clicked).style.fontWeight = "600";
		},
		error:function(responseData){
			alert("error!");
		}
		
		
	}
	);
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
		prev_tag.style.cssText ="font-size:30px; margin-right: 30px; margin-left: 30px; font-color:black;";
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
		page_tag.style.cssText ="font-size:30px; margin-right: 30px; margin-left: 30px; font-color:black;";
		page_div.appendChild(page_tag);
	}
	if(news_display>120){
		var next_tag=document.createElement("SPAN");
		next_tag.addEventListener("click",function(){
			start_page=start_page+10;
			generatePageNum(div_id,newscontainer_id,start_page,news_arr);
		});
		next_tag.innerHTML="다음";
		next_tag.style.cssText ="font-size:30px; margin-right: 30px; margin-left: 30px; font-color:black;";
		page_div.appendChild(next_tag);
		
	}
}



function pageClicked(news_arr,pagenum,newscontainer_id){
	document.getElementById(newscontainer_id).innerHTML="";
	cleanPage();
	document.getElementById(pagenum).style.fontWeight = "600";

	var news_container=document.getElementById(newscontainer_id);
	var news_start=(pagenum-1)*12;
	var news_end=news_start+12;
	for(var i=news_start;i<news_end;i++){
		if(news_arr[i]!=null&&news_arr[i].news_imgurl!=null){
			var newsdiv=document.createElement("DIV");
			newsdiv.style.cssText ="height:8.3%;width:1200px;border:1px solid #ccc;display:flex;  flex-direction: row;";
			var newsimg=document.createElement("IMG");
			newsimg.setAttribute("src",news_arr[i].news_imgurl);
			newsimg.style.cssText ="height:150px;width:150px;";
			var newsdesc=document.createElement("DIV");
			newsdesc.innerHTML="<a href='"+"newsbody.do?newsno="+news_arr[i].news_no+"'>"+"<h2>"+news_arr[i].news_title+"</h2>"
								+news_arr[i].news_summ+"</a>";
			newsdesc.style.cssText ="height:150px;width:1050px;text-overflow: ellipsis;";
			newsdiv.appendChild(newsimg);
			newsdiv.appendChild(newsdesc);
			news_container.appendChild(newsdiv);
		}else{
			if(news_arr[i]!=null){
				var newsdesc=document.createElement("DIV");
				newsdesc.innerHTML="<a href='"+"newsbody.do?newsno="+news_arr[i].news_no+"'>"+"<h2>"+news_arr[i].news_title+"</h2>"
								+"<br>"+news_arr[i].news_summ+"</a>";
				newsdesc.style.cssText ="height:150px;width:1200px;";
				news_container.appendChild(newsdesc);
				}
		}		
	}
}

function change_cat(e){
	cat_clicked=e;
	cleanCat();
	cleanSpan();
	document.getElementById(cat_clicked).style.color="blue";
	date_clicked=moment().format('YYYY-MM-DD');
	var cat_name=document.getElementById(cat_clicked).innerHTML;
	document.getElementById("category_name").innerHTML="<h1>"+cat_name+" 카테고리 "+date_clicked+" 일자 뉴스"+"</h1>"
	getNewsData();
}

window.onload = function(){
	var arr=generateDate(moment().add(-2,"days").format('YYYY-MM-DD'));
	generateDateTag("date",arr);
	document.getElementById(cat_clicked).style.color="blue";
	var cat_name=document.getElementById(cat_clicked).innerHTML;
	document.getElementById("category_name").innerHTML="<h1>"+cat_name+" 카테고리 "+date_clicked+" 일자 뉴스"+"</h1>";
	generatePageNum("paging","news_container",1,newsdata);
	pageClicked(newsdata,1,"news_container");
	document.getElementById(date_clicked).style.fontWeight="600";
			
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


<div id="main_container"style="background-color:white;position:relative; left:20%;  ">
<div id="left" style="width: 200px; height: 2100px; border: 1px solid #ccc; ">
<p id="금융"><h2 class="cat" id="259101" onclick="change_cat(this.id);">금융</h2></p>
<p id="증권"><h2 class="cat" id="258101" onclick="change_cat(this.id);">증권</h2></p>
<p id="산업/재계"><h2 class="cat" id="261101" onclick="change_cat(this.id);">산업/재계</h2></p>
<p id="중기/벤처"><h2 class="cat" id="771101" onclick="change_cat(this.id);">중기/벤처</h2></p>
<p id="글로벌 경제"><h2 class="cat" id="262101" onclick="change_cat(this.id);">글로벌 경제</h2></p>
<p id="경제 일반"><h2 class="cat" id="263101" onclick="change_cat(this.id);">경제 일반</h2></p>
<p id="인터넷/SNS"><h2 class="cat" id="226105" onclick="change_cat(this.id);">인터넷/SNS</h2></p>
<p id="IT"><h2 class="cat" id="230105" onclick="change_cat(this.id);">IT</h2></p>

</div>
<div id="right" style="width: 1200px; height: 2100px; border: 1px solid #ccc;">
<div id="category_name" style="width: 1200px; height: 75px; border: 1px solid #ccc;">
</div>
<div id="news_container" style="width: 1200px; height: 1850px; border: 1px solid #ccc;">

</div>
<div id="paging" style="width: 1200px; height: 75px; border: 1px solid #ccc; ">
</div>
<div id="date" style="width: 1200px; height: 100px; border: 1px solid #ccc;">

</div>

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