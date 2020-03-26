<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="com.vo.*" %>
<%@page import="com.util.*" %>
<!DOCTYPE html>
<html>
<head>
<title><%=new DateStringHelper().getCurrentDate()+" 뉴스 수집 통계 " %></title>
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
#main_content{
width: 100%;
  display: flex;
  flex-direction: row;

}
#chart_option_change{
width: 100%;
  display: flex;
  flex-direction: row;
}

*{margin:0;padding:0;}
    ul,li{list-style:none; }
    .slide{height:600px;overflow:hidden;position:relative; width:1700px; left:13%;}
    .slide ul{width:calc(100% * 3);display:flex;transition:1s;}
    .slide li{width:calc(100% / 3);height:600px;}
    .slide li:nth-child(1)
    .slide li:nth-child(2)
    .slide li:nth-child(3)
    .slide input{display:none;}
    .slide .bullet{position:absolute;bottom:20px;left:0;right:0;text-align:center;z-index:10;}
    .slide .bullet label{width:10px;height:10px;border-radius:10px;border:2px solid #666;display:inline-block;background:#fff;font-size:0;transition:0.5s;cursor:pointer;}
    /* 슬라이드 조작 */
    #pos1:checked ~ ul{margin-left:-1.5%;}
    #pos2:checked ~ ul{margin-left:-101.5%;}
    #pos3:checked ~ ul{margin-left:-200%;}
    /* bullet 조작 */
    #pos1:checked ~ .bullet label:nth-child(1),
    #pos2:checked ~ .bullet label:nth-child(2),
    #pos3:checked ~ .bullet label:nth-child(3){background:#666;}
</style>

        <script src="assets/js/jquery.min.js"></script>
			<script src="assets/js/util.js"></script>
			<script src="assets/js/jquery.scrolly.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/main.js"></script>
			<link rel="stylesheet" href="assets/css/main.css" />
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script>
var pubStat=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("pubStat") %>));
var catStat=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("catStat") %>));
var compStat=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("compStat") %>));
var today=moment().format('YYYY년  MM월  DD일');
window.onload = function(){	
	var chart;

	function generateChartWithArr(arr,div_id,title_text,data_type){
		dps=[];					
		for(var key in arr){
			dps.push({label:key,y:Number(arr[key])});
		}
		chart = new CanvasJS.Chart(div_id, {
			animationEnabled: true,
			animationDuration: 2000,
			title:{
				text: title_text,
				fontSize: 26,
			},
			dataPointWidth:60,
			data: [              
			{
				type: data_type,
				click: chartClicked,
				dataPoints: dps
			}
			]
		});
		chart.render();
	}
	
	function generateTimeSeriesChart(arr,div_id,title_text){
		dps=[];					
		for(var key in arr){
			var date=new Date(key.split('.')[0],key.split('.')[1]-1,key.split('.')[2]);
			var value=arr[key];
			dps.push({x:date,y:Number(value)});
		}
		chart = new CanvasJS.Chart(div_id, {
			animationEnabled: true,
			animationDuration: 2000,
			title:{
				text: title_text,
				fontSize: 26,
			},
			data: [              
			{
				type: "line",
				click: searchNews,
				dataPoints: dps
			}
			]
		});
		chart.render();		
	}
	function searchNews(e){
		var query=e.chart.title.text.split(":")[0];
		var date=e.dataPoint.x;
		var mon=("0" + (date.getMonth() + 1)).slice(-2);
		var twodigit=("0" + date.getDate()).slice(-2);
		var date_query=date.getFullYear()+"."+mon+"."+twodigit;
		location.href="searchnews.do?"+"query="+query+"&date_start="+date_query+"&date_end="+date_query;		
	}	
	function chartClicked(e){
		var s=e.chart.title.text;
		var new_title=s.substring(16,s.indexOf("수"));
		$.ajax({
			url: "time_series_analysis.do",
			type:"POST",
			data:{"data_clicked":e.dataPoint.label,
					"chart":e.chart.title.text	
			},
			success:function(responseData){
				var ar=JSON.parse(JSON.stringify(responseData.time_data));
				generateTimeSeriesChart(ar,"sort_by_date",new_title.concat(" - ",e.dataPoint.label," : 최근 한 달 수집현황 보기"));				
			},
			beforeSend:function(){
				document.getElementById("sort_by_date").innerHTML="";
				var loading=document.createElement("img");
				loading.setAttribute("src","/myimg/loading02.gif");	
				loading.setAttribute("id","loading_ajax");
				loading.style.cssText="width:1700px;height:600px;";
				document.getElementById("sort_by_date").appendChild(loading);
			},
			complete:function(){
				//alert("로딩 완료");
			},
			error:function(responseData){
				alert("error");
			}
			
		})
	}	
	generateChartWithArr(pubStat,"sort_by_pub",today+" 언론사별 뉴스 수집 통계","column");
	generateChartWithArr(catStat,"sort_by_cat",today+" 분야별 뉴스 수집 통계","doughnut");
	generateChartWithArr(compStat,"sort_by_comp",today+" 기업별 뉴스 수집 통계","pyramid");		
}
</script>
</head>
<body style='background-color:#fafafa;'>
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
<div>

<div class="header_section">
<h7><a href="index.jsp" style="color:#727a82; text-decoration:none;">홈으로</a></h7>
<h7><a href="news_main.do" style="color:#727a82; text-decoration:none;">뉴스홈</a></h7>
<h7><a href='cat_news.do' style="color:#727a82; text-decoration:none;">카테고리별 뉴스</a></h7>
<h7><a href='comp_news.do' style="color:#727a82; text-decoration:none;">기업별 뉴스</a></h7>
<h7><a href='news_stat.do' style="color:#727a82; text-decoration:none;">뉴스 통계 보기</a></h7>
<h7><a href="hashtag_relation.do" style="color:#727a82; text-decoration:none;">키워드 관계 분석</a></h7>
</div>



</div>
<br>




<div class="slide" style="border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:5px; ">
    <input type="radio" name="pos" id="pos1" checked>
    <input type="radio" name="pos" id="pos2">
    <input type="radio" name="pos" id="pos3">
    <ul>
      <li><div id="sort_by_pub" style="width: 1700px; height: 600px;">

</div>
      </li>
      <li><div id="sort_by_comp" style="width: 1700px; height: 600px;"></div></li>
      <li>
<div id="sort_by_cat" style="width: 1700px; height: 600px;">

</div>
      </li>
    </ul>
    <p class="bullet">
      <label for="pos1">1</label>
      <label for="pos2">2</label>
      <label for="pos3">3</label>
    </p>
  </div>
<br>

<div id="sort_by_date" style="width: 1700px; height: 600px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px; background-color:white; position: relative; left:13%; bottom:10%;">
<br><br><br><br><br><br><br><br><br>
<h1>그래프를 클릭하면 선택한 데이터의 최근 한 달간의 수집 통계를 보여 줍니다</h1>
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