<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.util.*" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Company News Page</title>
<script src="assets/js/jquery.min.js"></script>
<script src="assets/js/util.js"></script>
<script src="assets/js/jquery.scrolly.min.js"></script>
<script src="assets/js/skel.min.js"></script>
<script src="assets/js/main.js"></script>
<link rel="stylesheet" href="assets/css/main.css" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
<script type="text/javascript" src="/wordcloud/jqcloud-0.2.10.js"></script>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<link rel="stylesheet" type="text/css" href="/wordcloud/mycloudcolor.css" />


<script>
var stockDataSet;
var statDataSet;
var chart;
var compchoice;
var daybegin;
var dayend;
var datechoice="#";
var tag_choice="none";
var chartdataClicked=false;


function generateChartWithArr(chart,arr,div_id,title_text,data_type){
	dps=[];
	for(var key in arr){
		dps.push({label:key,y:Number(arr[key])});
		}
	chart = new CanvasJS.Chart(div_id, {
		title:{
			text: title_text,
			fontSize: 20
			},
			data: [              
				{
					type: data_type,
					dataPoints: dps,
					click:chartClicked
					}]
			});
	chart.render();
};
function chartClicked(e){
	compchoice=e.dataPoint.label;
	chartdataClicked=true;
	searchCompData();
}
function searchCompData(){
	var compname=$('#comp-name').val();
	if(chartdataClicked){
		compname=compchoice;
		chartdataClicked=false;
		$('#comp-news').html('');
	}
	daybegin=$('#day-start').val();
	dayend=$('#day-end').val();
	if(!(compname&&daybegin&&dayend)){
		alert("회사명과 시작/종료 일자를 모두 입력 해주시기 바랍니다. ");
	}else if(moment(dayend,'YYYY-MM-DD').diff(daybegin,'days')<14){
		alert("보다 효과적인 그래프 시각화를 위해, 2 주 이상의 기간을 선택해 주시기 바랍니다");
	}else{
		compchoice=compname;
		datechoice="#";
		tag_choice="none";
		$.ajax({
			url:"update_compnews.do",
			type:"POST",
			data:{
				"compname":compname,
				"daybegin":daybegin,
				"dayend":dayend
			},
			dataType:"json",
			success:function(responseData){
				var stockData=responseData.split("%")[0];
				var stockArr=JSON.parse(stockData);
				var hashTag=JSON.parse(responseData.split("%")[1]);
				generateCloudWithArr(hashTag)
				generateFinancialChart(stockArr,"stock-graph",compchoice+" 주가 데이터")
			},beforeSend:function(){
				document.getElementById("stock-graph" ).innerHTML="";
				var loading=document.createElement("img");
				loading.setAttribute("src","/myimg/loading02.gif");	
				loading.setAttribute("id","loading_ajax");
				loading.style.cssText="width:950px;height:350px;";
				document.getElementById("stock-graph" ).appendChild(loading);
			},
			complete:function(){
				alert("데이터 로딩 완료");
			},
			error:function(responseData){
				alert("error");
			}
			})
	}
}
function generateCloudWithArr(arr){
	word_set=[];
	for(var key in arr){
		word_set.push({text:key,weight:arr[key]
			,handlers:{
			click:function(word){
				tag_choice=word.target.textContent;
				$.ajax({
					url:"compnewstag.do",
					type:"POST",
					data:{"tag_clicked":word.target.textContent,
						"compname":compchoice,
						"datechoice":datechoice,
						"daybegin":daybegin,
						"dayend":dayend},
					dataType : "json",
					success:function(responseData){
						var tagnewsdata=JSON.parse(JSON.stringify(responseData));
						updateNews(tagnewsdata);
					},beforeSend:function(){
						document.getElementById("comp-news" ).innerHTML="";
						var loading=document.createElement("img");
						loading.setAttribute("src","/myimg/loading.gif");	
						loading.setAttribute("id","loading_ajax");
						loading.style.cssText="width:450px;height:600px;";
						document.getElementById("comp-news" ).appendChild(loading);
					},
					complete:function(){
						alert("데이터 로딩 완료");
					},
					error:function(responseData){
						alert("error");
					}
				})
			}
			}
		})
	}
	document.getElementById("news_wc").innerHTML="";
	$("#news_wc").jQCloud(word_set);	
	if(datechoice.includes('#')){
		var title=daybegin+" 부터 "+ dayend+" 까지 "+compchoice+" 뉴스 워드 클라우드";
		makeWCTitle(title);
	}else{
		var title=datechoice+" 일자의 "+compchoice+" 뉴스 워드 클라우드";
		makeWCTitle(title);
	}	
};

function makeWCTitle(title_text){
	var wc_container=document.getElementById("news_wc");
	var title=document.createElement("h3");
	title.innerHTML=title_text;
	wc_container.appendChild(title);
}

function newsDivTitle(){
	if(datechoice.includes('#')){
		return daybegin+" 부터 "+ dayend+" 까지 "+compchoice+" & "+tag_choice+"관련 뉴스 ";
	}
	if(compchoice==tag_choice){
		return datechoice+" 일자의 <span style='color:red'>"+compchoice+"</span> 관련 뉴스 ";
	}
	if(!(tag_choice.includes("none"))){
		return datechoice+" 일자의 <span style='color:red'>"+compchoice+"</span> & <span style='color:red'>#"+tag_choice+"</span>관련 뉴스 ";
	}else{
		return datechoice+" 일자의 <span style='color:red'>"+compchoice+"</span> 관련 뉴스 ";
	}
}

function generateFinancialChart(stockArr,div_id,title_text){
	dps=[];
	var max_min=[];
	for(var i=0;i<stockArr.length;i++){
		var key=stockArr[i].st_date;
		var date=new Date(key.split('.')[0],key.split('.')[1]-1,key.split('.')[2]);
		dps.push({
			x:date,
			y:[stockArr[i].open_price,stockArr[i].high_price,stockArr[i].low_price,stockArr[i].close_price]		
		});
		max_min.push(stockArr[i].open_price);max_min.push(stockArr[i].high_price);
		max_min.push(stockArr[i].low_price);max_min.push(stockArr[i].close_price);		
	}
	var max_val=Math.max.apply(null,max_min);
	var min_val=Math.min.apply(null,max_min);
	var diff=Math.floor((max_val-min_val)/4);
	
	var chart = new CanvasJS.Chart(div_id, {
		title:{
			text: title_text,
			fontSize: 25
		},
		axisX:{
			intervalType: "day"
		},
		axisY:{
			minimum:min_val-diff,
			maximum:max_val+diff,
	        valueFormatString:  "###,###"  
	     },
		data: [              
		{
			type: "candlestick",
			click: compNews,
			dataPoints: dps
		}
		]
	});
	chart.render();	

}


function compNews(e){
	var date=e.dataPoint.x;
	var year=date.getFullYear();
	var month=("0" + (date.getMonth() + 1)).slice(-2);
	var day=("0" + date.getDate()).slice(-2);
	var date_string=year+"."+month+"."+day;
	datechoice=date_string;
	tag_choice="none";
	$.ajax({
		url:"getcompnews.do",
		type:"POST",
		data:{
			"compname":compchoice,
			"datechoice":date_string
		},
		dataType:"json",
		success:function(responseData){
			var newsdiv=document.getElementById("comp-news");
			var compStatDiv=document.getElementById("tag-rank");
			newsdiv.innerHTML="<h3>그래프의 노드를 클릭하면 해당날짜의 기업 뉴스를 볼 수 있습니다.</h3>";
			compStatDiv.innerHTML="<h3>그래프의 노드를 클릭하면 해당날짜에 많이 언급 된 기업의 순위를 볼 수 있습니다.</h3>";

			var newsdata=JSON.parse(JSON.stringify(responseData))[0];
			updateNews(newsdata);
			
			var statdata=JSON.parse(JSON.stringify(responseData))[1];
			updateStat(statdata);
			
			var cloudData=JSON.parse(JSON.stringify(responseData))[2];
			generateCloudWithArr(cloudData);			
		}
		,beforeSend:function(){
			document.getElementById("comp-news" ).innerHTML="";
			var loading=document.createElement("img");
			loading.setAttribute("src","/myimg/loading.gif");	
			loading.setAttribute("id","loading_ajax");
			loading.style.cssText="width:450px;height:600px;";
			document.getElementById("comp-news" ).appendChild(loading);
		},
		complete:function(){
			alert("데이터 로딩 완료");
		},
		error:function(responseData){
			alert("error");
		}
		
	});


}
function updateStat(statdata){
	var chart;
	var statdiv=document.getElementById("tag-rank");
	statdiv.innerHTML="";
	generateChartWithArr(chart,statdata,"tag-rank",datechoice+" 뉴스에 등장한 회사 통계","bar");
}


function updateNews(newsdata){
	var newsdiv=document.getElementById("comp-news");
	newsdiv.innerHTML="";
	var br = document.createElement("BR");
	newsdiv.appendChild(br);	
	var divtitle=document.createElement("h3");
	divtitle.innerHTML=newsDivTitle();
	newsdiv.appendChild(divtitle);
	
	for(var i=0;i<13&&i<newsdata.length;i++){
		var newstitle=newsdata[i].news_title;		
		var newsno=Number(newsdata[i].news_no);
		var atag=document.createElement('A');
		var hrefval="/news/newsbody.do?newsno="+newsno;
		atag.setAttribute("href",hrefval);
		atag.setAttribute("style","color:#727a82;text-align:left; text-decoration:none; font-size:19px;line-height:2.3em; ")
		atag.innerHTML =newstitle;
		newsdiv.appendChild(atag);
		var br = document.createElement("BR");
		newsdiv.appendChild(br);	
		newsdiv.appendChild(br);
	}
	var br = document.createElement("BR");
	newsdiv.appendChild(br);
	var viewMore=document.createElement("A");
	var searchHref="searchnews.do?"+"query="+"기업별 뉴스 - "+compchoice+"&date_start="+daybegin+"&date_end="+dayend;
	viewMore.setAttribute("href",searchHref);
	viewMore.setAttribute("style","color:#727a82; text-decoration:none; font-size:22px; font-weight: bold;");
	viewMore.innerHTML="더보기 >";
	newsdiv.appendChild(viewMore);	
}

$( document ).ready( function() {
	$('#comp-name').click(function(){	
		$('#comp-name' ).removeAttr( 'placeholder' );
	})
 } );


</script>
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
#search-comp{
	width: 1500px; 
	height: 100px; 
	display: flex;
	flex-direction: row;
}
#container{
	width: 1500px; 
	height: 700px; 
	border: 1px solid #ccc;
	display: flex;
	flex-direction: row;
}
.floating { position: absolute; right: 17.5%; top: 302px; text-align:center; width: 124px;}

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


<div id="search-comp" style="width: 1600px; height: 70px;  position: relative; left:15%; margin-left:10px;">
<h3>회사 검색: </h3> <input placeholder="회사명을 입력해주세요" id="comp-name" type="text" style="color:#727a82;height: 30px; width:600px; color:black; margin-left:10px; margin-right:50px; background-color:white; border-radius: 20px; ">
<h3> 시작 일자: </h3> <input id="day-start" type="date" style="color:#727a82;height: 30px; text-align: right; margin-left:10px; margin-right:50px; border-radius: 20px; ">
<h3> 종료 일자: </h3> <input id="day-end" type="date" style="color:#727a82;height: 30px; text-align: right; margin-left:10px; margin-right:10px; border-radius: 20px; ">
<button id="searchcomp" onclick="searchCompData()" style=" width: 150px;margin-right:15px; margin-left:10px;border-radius: 20px;  position:relative; top:-15%; ">검색</button>
</div>


<div id="graph-container" style="width: 970px; height: 700px;">
<div id="stock-graph" style="width: 970px; height: 400px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px; background-color:white;
					position: relative; left:36%;">
<br>
<br>
<br><br><br><br>
<h1>검색한 기업의 주가 데이터가 표시됩니다.</h1>
</div>
<div id="comp-news" style="width: 550px; height: 850px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; 
         position: relative; left:139%; top: -57%;border-left: 1px solid #ccc; border-radius:10px; background-color:white;">
<br>
<br>
<br><br><br><br>
<br><br><br><br>
<br><br><br>
<h1>검색한 기업의 선택 날짜</h1>
<h1>뉴스 목록이 표시됩니다.</h1>
</div>

<div id="news_wc" style="width: 970px; height: 400px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc; border-radius:10px; background-color:white;border-top: 1px solid #ccc; border-left: 1px solid #ccc;
			position: relative; left:36%; top: -115%;">
			<br>
<br>
<br><br><br><br>
<h1>선택한 기간/날짜 동안의 기업 뉴스 키워드를<br> 시각화하여 보여줍니다.</h1>
</div>
</div>

<div class="floating">
<div id="tag-rank" style="width: 350px; height: 850px; border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; border-radius:10px; background-color:white;">
<h3>뉴스에 많이 등장한<br> 회사 순위 (선택 일자 기준)</h3>
</div>
</div>

<footer id="footer" style="margin-top:10%;">
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