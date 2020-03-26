<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="com.vo.*" %>
<% MemberVO user=(MemberVO)request.getSession().getAttribute("signedUser");%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%=user.getUser_name()%> 님의 마이 페이지</title>
<script type="text/javascript" src="/newsview/member/statframe.js" ></script>  
<script src="assets/js/jquery.min.js"></script>
<script src="assets/js/util.js"></script>
<script src="assets/js/jquery.scrolly.min.js"></script>
<script src="assets/js/skel.min.js"></script>
<script src="assets/js/main.js"></script>
<link rel="stylesheet" href="assets/css/main.css" />
<script type="text/javascript" src="/newsview/member/interestframe.js" ></script>  
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
<script type="text/javascript" src="/newsview/member/chart.js" ></script>
<link rel="stylesheet" type="text/css" href="/wordcloud/mycloudcolor.css" />
<script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>  
<script type="text/javascript">


/* Controller에서 전달받은 Model을 JavaScript변수로 변환 시켜준다.
 * scrapNews, viewNews는 Java의 리스트 객체 (List<NewsVO>) 모델이며,
 * scrapStat, viewStat은 조회/스크랩뉴스 통계를 보여주기 위한 Map<K,V> 모델이다.
 * (Key는 카테고리, Value는 조회 및 스크랩 수)
 */
 
var scrapNews=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("scrapNews")%>));
var viewNews=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("viewNews")%>));
var scrapStat=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("scrapStat") %>));
var viewStat=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("viewStat") %>));
var viewNewsDate=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("viewNewsDate") %>));
var viewTag=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("viewKeyWord")%>));
var scrapTag=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("scrapKeyWord")%>));

function cleanPage(){
	var pagetags=document.getElementsByClassName("page_span");
	for (var i=0; i<pagetags.length; i++) {
		pagetags[i].style.fontWeight = "normal";		 
	}
}

function generatePageNum(div_id,newscontainer_id,start_page,news_arr){
	var total_news=news_arr.length;
	var prev_news_total=(start_page-1)*10;
	var news_display=total_news-prev_news_total;
	var page_display=Math.floor(news_display/10)+1;	
	var page_div=document.getElementById(div_id);
	page_div.innerHTML="";
	if(start_page>10){
		var prev_tag=document.createElement("SPAN");
		prev_tag.addEventListener("click",function(){
			start_page=start_page-10;
			generatePageNum(div_id,newscontainer_id,start_page,news_arr)
		});
		prev_tag.innerHTML="이전";
		prev_tag.style.cssText ="font-size:20px; margin-right: 20px; margin-left: 20px;";
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
		page_tag.style.cssText ="font-size:20px; margin-right: 20px; margin-left: 20px;";
		page_div.appendChild(page_tag);
	}
	if(news_display>100){
		var next_tag=document.createElement("SPAN");
		next_tag.addEventListener("click",function(){
			start_page=start_page+10;
			generatePageNum(div_id,newscontainer_id,start_page,news_arr);
		});
		next_tag.innerHTML="다음";
		next_tag.style.cssText ="font-size:20px; margin-right: 20px; margin-left: 20px;";
		page_div.appendChild(next_tag);		
	}
}

function pageClicked(news_arr,pagenum,newscontainer_id){
	document.getElementById(newscontainer_id).innerHTML="";
	cleanPage();
	document.getElementById(pagenum).style.fontWeight = "600";
	var news_container=document.getElementById(newscontainer_id);
	var news_start=(pagenum-1)*10;
	var news_end=news_start+10;
	for(var i=news_start;i<news_end;i++){
		if(news_arr[i]!=null){
			var newsdiv=document.createElement("DIV");			
			newsdiv.style.cssText ="textAlign:left;padding-top:7px;height:40px;width:862px;border-bottom:1px solid #ccc;display:flex; flex-direction: row;";
			var newsdesc=document.createElement("DIV");
			newsdesc.innerHTML="<a href='"+"newsbody.do?newsno="+news_arr[i].news_no+"'style='text-decoration:none; position:relative; left:5%;'>"+"<h5 style='postion:relative; left:10%;'>"+news_arr[i].news_title+"</h5>"
								+"<br>"+"</a>";
			newsdesc.style.cssText ="textAlign:left;height:40px;width:835px;";
			var scrapchoice=document.createElement("DIV");
			scrapchoice.innerHTML="<h6 id='"+news_arr[i].news_no+"' onclick=updateScrap(this)>스크랩 취소</h6>";
			scrapchoice.style.cssText ="textAlign:left;height:40px;width:80px;border:1px solid #ccc;padding-top:10px; border-radius:20px; position:relative; top:-7.5px; ";
			newsdiv.appendChild(newsdesc);
			newsdiv.appendChild(scrapchoice);
			news_container.appendChild(newsdiv);
		}		
	}
}
//유저가 마이페이지에서 스크랩 취소를 선택했을 시, AJAX를 통해 동적으로 화면을 업데이트 시켜준다 ->스크랩 뉴스 통계 및 스크랩 뉴스 개수
function updateScrap(e){
	$.ajax({
		url:"updatescrap.do",
		type:"POST",
		data:{"newsno":e.id},
		success:function(responseData){			
			var newsarr=JSON.parse(JSON.stringify(responseData))[0];
			scrapNews=newsarr;			
			generatePageNum("scrap_paging","scrap_news",1,scrapNews);
			pageClicked(scrapNews,1,"scrap_news");
			var statdata=JSON.parse(JSON.stringify(responseData))[1];
			var chart;
			scrapStat=statdata;
			generateChartWithArr(chart,scrapStat,"scrap_cat_stat","카테고리별 스크랩 뉴스현황","bar");
			//스크랩 키워드 업데이트
			var scrapKeyWord=JSON.parse(JSON.stringify(responseData))[2];
			scrapTag=scrapKeyWord;
			
			var scrapdata=document.getElementById("scrapcnt");
			var prevdata=scrapdata.innerHTML;
			scrapdata.innerHTML=prevdata-1;
			if(prevdata-1==0){
				$("#scrap_cat_stat").empty();
				$("#scrap_cat_stat").html("<br><br><h2>스크랩된 뉴스가 없습니다<br><br><a style='color:blue;' href='news_main.do'> &#8594; 뉴스 보러가기 &#8592;</a></h2>");
			}
			
		},
		error:function(responseData){
			alert("fail!");
		}
	})	
}
function generateCloudWithArr(arr,divid){
	word_set=[];
	for(var key in arr){
		word_set.push({text:key,weight:arr[key]})
	}
	$(divid).jQCloud(word_set);		
};

window.onload=function(){
	if(scrapNews!=null){
		generatePageNum("scrap_paging","scrap_news",1,scrapNews);
		pageClicked(scrapNews,1,"scrap_news");
		$("#scrapcnt").html(scrapNews.length);
		generateChartWithArr(chart,scrapStat,"scrap_cat_stat","카테고리별 스크랩 뉴스현황","bar");
	}	
	function generateScrapNews(scrapArr){
		var scrapdiv=document.getElementById("scrap_news");
		scrapdiv.innerHTML="";
	}	
	var chart;	
	function generateDayChartWithArr(arr,div_id,title_text,data_type){		
		dps=[];					
		for(var key in arr){
			var date=new Date(key.split('.')[0],key.split('.')[1]-1,key.split('.')[2]);
			var value=Number(arr[key]);
			dps.push({x:date,y:value});
		}
		chart = new CanvasJS.Chart(div_id, {
			animationEnabled: true,
			animationDuration: 2000,
			title:{
				text: title_text,
				fontSize: 25,
			},
			data: [              
			{
				type: data_type,
				dataPoints: dps
			}
			]
		});
		chart.render();
	}	
	if(viewNews!=null){
		generateDayChartWithArr(viewNewsDate,"view_news_date","일별 뉴스 조회 현황","line");	
		$("#viewcnt").html(viewNews.length);
		generateChartWithArr(chart,viewStat,"view_cat_stat","카테고리별 조회 뉴스현황","bar");
	}
	

	$("#logout").click(function(){
		alert("로그 아웃!");
		location.href="index.jsp?logout=true";
	});	

	
	$("#view_news_title").click(function(){
		var divtext=$("#view_news_title").html();
		if(!divtext.includes("키워드")){
			$("#view_news_title").html("<h2>조회 뉴스 키워드</h2>");
			$("#view_cat_stat").empty();
			generateCloudWithArr(viewTag,"#view_cat_stat");
		}else{
			$("#view_cat_stat").empty();
			$("#view_news_title").html("<h2>조회 뉴스 통계</h2>");
			generateChartWithArr(chart,viewStat,"view_cat_stat","카테고리별 조회 뉴스현황","bar");
		}
	});
	$("#scrap_news_title").click(function(){
		var divtext=$("#scrap_news_title").html();
		if(!divtext.includes("키워드")){
			$("#scrap_news_title").html("<h2>스크랩 뉴스 키워드</h2>");
			$("#scrap_cat_stat").empty();
			generateCloudWithArr(scrapTag,"#scrap_cat_stat");
		}else{
			$("#scrap_cat_stat").empty();
			$("#scrap_news_title").html("<h2>스크랩 뉴스 통계</h2>");
			generateChartWithArr(chart,scrapStat,"scrap_cat_stat","카테고리별 조회 뉴스현황","bar");
		}
	});	
	if(viewNews==null||viewNews.length<1){
		$("#scrap_cat_stat").empty();
		$("#scrap_cat_stat").html("<br><br><h2>스크랩된 뉴스가 없습니다<br><br><a style='color:blue;' href='news_main.do'> &#8594; 뉴스 보러가기 &#8592;</a></h2>");
		$("#view_cat_stat").empty();
		$("#view_cat_stat").html("<br><br><h2>조회된 뉴스가 없습니다<br><br><a style='color:blue;' href='news_main.do'> &#8594; 뉴스 보러가기 &#8592;</a></h2>");
		$("#view_news_date").empty();
	}else if(scrapNews==null||scrapNews.length<1){
		$("#scrap_cat_stat").empty();
		$("#scrap_cat_stat").html("<br><br><h2>스크랩된 뉴스가 없습니다<br><br><a style='color:blue;' href='news_main.do'> &#8594; 뉴스 보러가기 &#8592;</a></h2>");
	}
	
	
	document.body.style.zoom="110%";	
};
</script>
<style>
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
  
h7{ 
  color: #727a82;
  font-weight: 700;
  font-family: "Montserrat", sans-serif;
  line-height: 1.5;
  margin: 0 0 1em 0;
  font-size: 1.25em;
  padding: 0 .3em;
  transition: all 1.0s;}
  
h7:hover {
  color: #fff;
  background-color: #c0c0c0;
  border-radius:10px;
}
#container{
	width: 1600px;
	display: flex;
	flex-direction: row;
}

</style>
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
<div id="right-header" style="position: relative; left:37%; width: 700px; height: 100px; text-align:center; padding-top:24px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px; background-color:white;">
	<h2><%=user.getUser_name()%> 님의 마이 페이지</h2>
</div>
<button id="info_change" style="position: relative; left:17.7%; top:10px;" onclick="location.href='memberUpdate.do'">정보 수정하기</button>
<button id="logout" style="position: relative; left:1%; top:10px;">로그아웃하기</button>
	<h4 style="position: relative; left:-2%; top:90px;">총 조회뉴스 : <span id="viewcnt"></span>건</h4>

<div id="view_news_title"  style="position: relative; left:13%; top:10px; width: 300px; height: 60px; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; padding-top:6px;"><h2>조회 뉴스 통계</h2></div>
<div id="scrap_news_title" style="position: relative; left:53%; top:-50px; width: 300px; height: 60px; background-color:white; border-radius: 20px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; padding-top:6px;"><h2>스크랩 뉴스 통계</h2></div>
<div id="view_cat_stat" style="position: relative; left:13%; top:-40px;width: 870px; height: 490px;  border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 3px solid #ccc; border-radius:10px;"></div>
<div id="view_news_date" style="position: relative; left:13%; top:-30px; width: 870px; height: 490px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 3px solid #ccc; border-radius:10px;"></div>
	<h4 style="position: relative; left:38%; top:-1050px;">총 스크랩뉴스: <span id="scrapcnt"></span>건</h4>
<div id="scrap_cat_stat" style="position: relative; left:53%; top:-1060px;width: 870px; height: 490px;  border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 3px solid #ccc; border-radius:10px;"></div>
<div id="scrap_news_container">
	<div id="scrap_news" style="textAlign:left;background:white;position: relative; left:53%; top:-1052px;width: 870px; height: 490px;  border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 3px solid #ccc; border-radius:10px;"></div>
	<div id="scrap_paging" style="color: black;background:white;position: relative; left:53%; top:-1040px; margin-bottom:-30%;width: 870px; height: 39px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 3px solid #ccc; border-radius:10px;">페이징 처리</div>
</div>

<footer id="footer" style="position:relative; top:-300px;">
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