<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자님 환영합니다</title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
    <script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script>
function getDateByValue(object, value) {
	var dateToFind='';
    for (var prop in object) { 
        if (object.hasOwnProperty(prop)) { 
            if (object[prop] === value) 
            dateToFind=prop;
        } 
    }
    return dateToFind;
}

var dayStat=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("dayStat") %>));
var monthStat=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("monthStat") %>));
var dailyVisitor=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("dailyVisitorStat") %>));
var hourlyVisitor=JSON.parse(JSON.stringify(<%=(String)request.getAttribute("hourlyVisitorStat") %>));
var mostviewnews=JSON.parse(JSON.stringify(<%=request.getAttribute("mostviewed") %>));
var membercnt=JSON.stringify(<%=request.getAttribute("membercnt") %>);
window.onload = function(){

	var chart;
	var daySum=0;
	var dayArr=[];
	var monthSum=0;
	var monthArr=[];
	var totalVisitor=0;
	
	function generateDayChartWithArr(arr,div_id,title_text,data_type){		
		dps=[];					
		for(var key in arr){
			var date=new Date(key.split('.')[0],key.split('.')[1]-1,key.split('.')[2]);
			var value=Number(arr[key]);
			dps.push({x:date,y:value});
			daySum+=value;
			dayArr.push(value);
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
	
	function generateMonthChartWithArr(arr,div_id,title_text,data_type){
		dps=[];					
		for(var key in arr){
			var date=new Date(key.split('.')[0],key.split('.')[1]-1);
			var value=Number(arr[key]);
			dps.push({x:date,y:value});
			monthSum+=value;
			monthArr.push(value);
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
	function generateHourChartWithArr(arr,div_id,title_text,data_type){
		dps=[];					
		for(var key in arr){
			var value=Number(arr[key]);
			totalVisitor+=value;
			dps.push({x:key,y:value});
		}
		chart = new CanvasJS.Chart(div_id, {

			animationEnabled: true,
			animationDuration: 2000,
			title:{
				text: title_text,
				fontSize: 25,
			},
			axisX:{
				valueFormatString: "#시",
			       interval: 1,
			       minimum:0,
			       maximum:23
			     },
			data: [              
			{
				// Change type to "doughnut", "line", "splineArea", etc.
				type: data_type,
				dataPoints: dps
			}
			]
		});
		chart.render();
		document.getElementById("total_visitor").innerHTML=totalVisitor+"명";
	}
		
	document.getElementById("total_member").innerHTML=membercnt+"명";	
	generateMonthChartWithArr(monthStat,"graph-month","월별 뉴스 수집 현황","bar");
	generateDayChartWithArr(dayStat,"graph-day","일별 뉴스 수집 현황","line");
	document.getElementById("max_news_day").innerHTML=Math.max.apply(null,dayArr)+" 건("+getDateByValue(dayStat,Math.max.apply(null,dayArr))+")";
	document.getElementById("min_news_day").innerHTML=Math.min.apply(null,dayArr)+" 건("+getDateByValue(dayStat,Math.min.apply(null,dayArr))+")";
	document.getElementById("avg_news_day").innerHTML=Math.floor(daySum/dayArr.length)+" 건 / 일";
	document.getElementById("max_news_month").innerHTML=Math.max.apply(null,monthArr)+" 건("+getDateByValue(monthStat,Math.max.apply(null,monthArr))+")";
	document.getElementById("min_news_month").innerHTML=Math.min.apply(null,monthArr)+" 건("+getDateByValue(monthStat,Math.min.apply(null,monthArr))+")";
	document.getElementById("avg_news_month").innerHTML=Math.floor(monthSum/monthArr.length)+" 건/ 월";
	document.getElementById("total_news").innerHTML=monthSum+" 건";
	generateDayChartWithArr(dailyVisitor,"visitor-by-date","일자별 방문자 통계","line");
	generateHourChartWithArr(hourlyVisitor,"visitor-by-time","시간대별 방문자 통계","line");
		
	var mostviewed=document.getElementById("most-viewed");
	var title=document.createElement("h3");
	title.innerHTML="최다 조회 뉴스";
	title.style.cssText="text-align:center;";
	mostviewed.appendChild(title);
	for(var i=0;i<mostviewnews.length&&i<8;i++){
		var title='';
		if(mostviewnews[i].news_title.length<40){
			title=mostviewnews[i].news_title+" . . . ";
		}else{
			title=(mostviewnews[i].news_title.substring(0,35))+" . . . ";
		}
		
		var row=document.createElement("span");
		row.innerHTML="<a style='color:#727a82; text-decoration:none;' href='"+"newsbody.do?newsno="+mostviewnews[i].news_no+"'>"+
				"<h5>"+title+"&nbsp&nbsp&nbsp&nbsp조회수: "
				+mostviewnews[i].viewcnt+" </h5></a> ";
		row.style.cssText="text-align:center;";		
		mostviewed.appendChild(row);
	}
	
}
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
  border-radius: 15px;
  border-right: 3px solid #ccc;
  border-bottom: 3px solid #ccc;
  border-top: 1px solid #ccc; 
  border-left: 1px solid #ccc;
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

#news-container{
	width: 1500px;
	height:700px;
	display: flex;
	flex-direction: row;
}

#news-stat{
	width: 1500px;
	height:700px;
	border: 1px solid #ccc;
	display: flex;
	flex-direction: row;
}


#news-graph{
	width: 1000px;
	height:700px;

}
#websitestat-graph{
	width: 1500px; 
	display: flex;
	flex-direction: row;
}

#visitor-by-time{
	width: 900px; 
	height: 400px; 
	border: 1px solid #ccc;
}
#visitor-by-date{
	width: 900px; 
	height: 400px; 
	border: 1px solid #ccc;
}
#visitor-by-day{
	width: 750px; 
	height: 400px; 
	border: 1px solid #ccc;
}
</style>


</head>
<body>
<div id="header" style="width: 1400px; height: 100px; "><a href="index.jsp"><img src="/myimg/headerimg.jpg" style="width: 2500px; height: 97px;"></a></div>
<div class="header_section">
<h7><a href="index.jsp" style="color:#727a82; text-decoration:none;">홈으로</a></h7>
<h7><a href="news_main.do" style="color:#727a82; text-decoration:none;">뉴스홈</a></h7>
<h7><a href='cat_news.do' style="color:#727a82; text-decoration:none;">카테고리별 뉴스</a></h7>
<h7><a href='comp_news.do' style="color:#727a82; text-decoration:none;">기업별 뉴스</a></h7>
<h7><a href='news_stat.do' style="color:#727a82; text-decoration:none;">뉴스 통계 보기</a></h7>
<h7><a href="hashtag_relation.do" style="color:#727a82; text-decoration:none;">키워드 관계 분석</a></h7>
</div>

<div id="main_container" style="width:2000px;  position: relative; left:2%;"> 
<div id="news-container">

<div id="news-graph">
<div id="graph-day" style="position:absolute;width: 900px; height: 400px; border: 1px solid #ccc;">
일별 뉴스 그래프
</div>

<div id="graph-month" style="position:absolute;top:61%;width: 900px; height: 400px; border: 1px solid #ccc;">
월별 뉴스 그래프

</div>


</div>

<div id="graph-desc">
<div id="stat-day" style="position:absolute;background:white;width: 500px;height: 400px;left:48%;border: 1px solid #ccc;border-radius: 15px; border-right: 3px solid #ccc; border-bottom: 3px solid #ccc; border-top: 1px solid #ccc; border-left: 1px solid #ccc;">
<h2 style="text-align:center;">뉴스 수집 통계</h2>
<h3 style="color:#727a82;text-align:center;">총 뉴스 수집량 : <span id="total_news" >00건</span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp일일 평균 : <span id="avg_news_day" >00건</span></h3>
<h3 style="color:#727a82;text-align:center;">최다 / 최소 수집일:<br> <span id="max_news_day" style="line-height:37px;"></span>/ <span id="min_news_day" ></span></h3>
<h3 style="color:#727a82;text-align:center;">달별 평균: <span id="avg_news_month"></span><br></h3>
<h3 style="color:#727a82;text-align:center;">최다 / 최소 수집달:<br> <span id="max_news_month" style="line-height:37px;" ></span>/<span id="min_news_month" ></span><br></h3>
<h2 style="text-align:center;">웹 사이트 통계</h2>
<h3 style="color:#727a82;text-align:center;">총 회원: <span id="total_member">00명</span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp총 방문자:<span id="total_visitor" style="color:#727a82;">00명</span></h3> 
</div>
<div id="most-viewed" style="background:white;position:absolute;top:61%;width: 500px; left:48%;height: 400px; border: 1px solid #ccc;border-radius: 15px; border-right: 3px solid #ccc; border-bottom: 3px solid #ccc; border-top: 1px solid #ccc; border-left: 1px solid #ccc;">

</div>

</div>

</div>

<div id="websitestat-graph">
<div id="visitor-by-time"style="position:absolute;right:-20%;border: 1px solid #ccc;top:0%;">
시간대별 방문자 수

</div>

<div id="visitor-by-date" style="position:absolute;right:-20%;border: 1px solid #ccc;top:61%;">
일자별 방문자 수

</div>
</div>


</div>


</body>
</html>