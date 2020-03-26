<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>키워드 관계분석 - 동시출현 네트워크</title>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.js"></script>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
<script src="/newsview/member/cytoscape.js"></script>
<script type="text/javascript">
var date_start;
var date_end;
var nodeset=[];
var edgeset=[];
var tagset=[];
var buttonClicked=0;
function cleanGraph(){
	document.getElementById("cy").innerHTML="";
}

window.onload = function(){
CanvasJS.addColorSet("myColorSet",
            [
            "#7fffd4",
            "#8fbc8f",
            "#2F4F4F",
            "#008080",
            "#2E8B57",
            "#3CB371",
            "#90EE90"                
            ]);
var isFirst=true;
var chart;
var clickedButton;
var tagClicked;
var cy;
var ranklayout;
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
		prev_tag.style.cssText ="color:#727a82;font-size:15px; margin-right: 15px; margin-left: 15px;";
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
		page_tag.style.cssText ="color:#727a82;font-size:15px; margin-right: 15px; margin-left: 15px;";
		page_div.appendChild(page_tag);
	}
	if(news_display>100){
		var next_tag=document.createElement("SPAN");
		next_tag.addEventListener("click",function(){
			start_page=start_page+10;
			generatePageNum(div_id,newscontainer_id,start_page,news_arr);
		});
		next_tag.innerHTML="다음";
		next_tag.style.cssText ="color:#727a82;font-size:15px; margin-right: 15px; margin-left: 15px;";
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
			newsdiv.style.cssText ="height:66.5px;width:600px;border:1px solid #727a82;border-radius:20px;";
			var newsdesc=document.createElement("DIV");
			newsdesc.innerHTML="<a style='text-decoration:none;color:#727a82;' href='"+"newsbody.do?newsno="+news_arr[i].news_no+"'>"+"<h3>"+news_arr[i].news_title+"</h3>"
								+"<br>"+"</a>";
			newsdiv.appendChild(newsdesc);
			news_container.appendChild(newsdiv);
		}		
	}
}

function generateChartWithArr(chart,arr,div_id,title_text,data_type){
	dps=[];
	for(var key in arr){
		dps.push({label:key,y:Number(arr[key])});
		}
	chart = new CanvasJS.Chart(div_id, {
		colorSet: "myColorSet",
		animationEnabled: true,
		animationDuration: 2000,
		title:{
			text: title_text,
			fontSize: 25,
			},
			data: [              
				{
					type: data_type,
					click: createGraph,
					dataPoints: dps
					}]
			});
	chart.render();
};

function removeElement(id) {
    var elem = document.getElementById(id);
    return elem.parentNode.removeChild(elem);
}

function toGraph(ar){
	ranklayout=false;
	var element=document.getElementById("hash_tag_choices");
	element.innerHTML="";
	var header=document.getElementById('res_header');
	header.innerHTML="";
	generateChartWithArr(chart,ar,"hash_tag_choices","차트 레이아웃","bar");	
	var viewchange=document.createElement("input");
	viewchange.setAttribute("id","viewrank");
	viewchange.setAttribute("type","button");
	viewchange.style.cssText="height:40px; width: 150px;margin-right:15px; margin-left:10px;border-radius: 20px;"
	viewchange.setAttribute("value","레이아웃 변경");
	var container=document.getElementById("tag_rank");
	var brtag=document.createElement("br");
	container.appendChild(brtag);
	container.appendChild(viewchange);
	document.getElementById("viewrank").addEventListener("click",function(e){
		getRank(ar);
	})
	
}
function getRank(myar){
	ranklayout=true;
	if(isFirst){
		cleanGraph();
		isFirst=false;
	}
	var searchspan=document.getElementById("search_res");
	searchspan.innerHTML="<span style='color:#727a82;font-size:26px;'>&nbsp&nbsp"+date_start+" ~ "+date_end+" TOP 뉴스 키워드<br></span><br>"; 	
	var element=document.getElementById("hash_tag_choices");
	element.innerHTML="";	
	var headerset=document.createElement("div");
	headerset.setAttribute("id",'res_header');
	headerset.style.cssText ="width:600px;display:flex;flex-direction:row;";
	headerset.innerHTML="<div style='width:200px;text-align: center;'><span style='color:#727a82;font-size:22px;'>순위</span></div><div style='width:200px;text-align: center;'><span style='color:#727a82;font-size:22px;'>키워드</span></div><div style='width:200px;text-align: center;'><span style='color:#727a82;font-size:22px;'>등장 빈도</span></div>";		
	document.getElementById("search_res").appendChild(headerset);	
	var rank=1;
	for(var key in myar){
		var onerow=document.createElement("div");
		if(rank<4){
			onerow.style.cssText ="font-size:25px; color:#727a82;width:600px;display:flex;flex-direction:row;";
			onerow.innerHTML="<div style='width:200px;text-align: center;'><b>"+rank+" 위</b></div>"+
			"<div class='tag_set' style='width:200px;text-align: center;'><b>"+key+
			"</b></div><div style='width:200px;text-align: center;'><b>"+myar[key]+" 회</b></div>";	
		}else{
			onerow.style.cssText ="font-size:18px; color:#727a82;width:600px;display:flex;flex-direction:row;";
			onerow.innerHTML="<div style='width:200px;text-align: center;'>"+rank+" 위</div>"+
			"<div class='tag_set' style='width:200px;text-align: center;'>"+key+
			"</div><div style='width:200px;text-align: center;'>"+myar[key]+" 회</div><br>";
		}
		element.appendChild(onerow);
		rank++;																
	}
	var tagspan=document.getElementsByClassName("tag_set");
	for (var i = 0; i < tagspan.length; i++) {
		tagspan[i].addEventListener('click', createGraph, false);
	};
	if(document.getElementById("viewrank")!=null){
		removeElement("viewrank");
	}	
	var viewchange=document.createElement("input");
	viewchange.setAttribute("id","viewchange");
	viewchange.setAttribute("type","button");
	viewchange.style.cssText="height:40px; width: 150px;margin-right:15px; margin-left:10px;border-radius: 20px;"
	viewchange.setAttribute("value","레이아웃 변경");
	
	var brtag=document.createElement("br");
	element.appendChild(brtag);element.appendChild(brtag);
	element.appendChild(viewchange);
	document.getElementById("viewchange").addEventListener("click",function(e){
		toGraph(myar);
	})
}

$("#date_submit").click(function(){
	if($('#date_input').val()==""||$('#date_input02').val()==""){
		alert("시작과 끝 날짜를 정확하게 입력해 주시기 바랍니다.")
	}else{
		date_start=$('#date_input').val();
		date_end=$('#date_input02').val();
		$.ajax({
			  url:"recommendtag.do",
			  type: "POST",
			  data : {
					"date_start" : $('#date_input').val(),
					"date_end": $('#date_input02').val()
				},
				dataType : "json",
				success:function(responseData){
					resetData();
					var mydata=responseData.res;
					var my=JSON.stringify(mydata);
					var myar=JSON.parse(my);
					getRank(myar)										
				},
				error:function(responseData){
					alert("error");
				}
		  })		
	}	  
});

function updateNews(tagone,tagtwo){
	var start=$('#date_input').val();
	var end=$('#date_input02').val();
	var titlediv=document.getElementById("title_box");
	titlediv.innerHTML="<h2 style='color:#727a82;'>&nbsp;&nbsp;&nbsp;From "+start+" &nbsp;&nbsp; To "+end+"<br>&nbsp;&nbsp;&nbsp;<span style='color:red;'>"+tagone+"</span> & <span style='color:red'>"+tagtwo+"</span> 관련 뉴스</h2>";
	$.ajax({
		url:"news_from_tagedge.do",
		 type: "POST",
		 data : {
					"date_start" : $('#date_input').val(),
					"date_end": $('#date_input02').val(),
					"tag_one":tagone,
					"tag_two":tagtwo
					},
			dataType : "json",
			success:function(responseData){
				var news_arr=JSON.parse(JSON.stringify(responseData));
				updateNewsDiv(news_arr);
			},
			error:function(responseData){
				alert("error!");
			}
	})
}
function updateNewsDiv(news_arr){
	var newsdiv=document.getElementById("news_box");
	newsdiv.innerHTML="";
	generatePageNum("news_paging","news_box",1,news_arr);
	pageClicked(news_arr,1,"news_box");
}
function updateNodeColor(cy){
	for(var i=0;i<tagset.length;i++){
		var targetid="[id='"+tagset[i]+"']";
		cy.nodes(targetid).style('background-color', 'green');
	}	
}


function addNode(arr){
	nodeset.push({
		data:{
			id:arr[0],
			name:arr[0]		
		}
	})
	for(var i=1;i<arr.length;i++){
		nodeset.push({
			data:{
				id:arr[i],
				name:arr[i]
			}
		});
	}
	
	for(var i=1;i<arr.length;i++){
		edgeset.push({
			data:{
				source:arr[0],
				target:arr[i],
				id:arr[0]+"++"+arr[i]
			}
		})
	}
	cy = cytoscape({
        container: document.getElementById('cy'),
        elements: {
        	nodes:nodeset,
        	edges:edgeset
        },
        style: [
            {
                selector: 'node',
                style: {
                	"height": 20,
                    "width": 20,
                    "font-size": "10px",
                    'background-color': '#727a82',
                    label: 'data(id)'
                }
            }],
            layout: {
                name: 'cose'
              },

      });
	cy.on('tap','edge',function(e){
		var edge=e.target;
		var tagone=edge.id().split("++")[0];
		var tagtwo=edge.id().split("++")[1];
		updateNews(tagone,tagtwo);
	});
	
	cy.on('tap','node',function(e){
		 var node=e.target;
		 var newsrc=node.id();
		 if(tagset.includes(newsrc)){
			 alert("이미 선택한 키워드 입니다.");
			 return;
		 }
		 if(buttonClicked==7){
				alert("효과적인 시각화를 위해 그래프 초기화 후, 다시 선택해주시기 바랍니다");
				return;
			}
		 buttonClicked++;		 
		 updatetargetkeyword(newsrc);
		 tagset.push(newsrc);
		 
		 $.ajax({
			 url:"moreNode.do",
			 type: "POST",
			data : {
					"date_start" : $('#date_input').val(),
					"date_end": $('#date_input02').val(),
					"tag_clicked":node.id()
					},
			dataType : "json",
			success:function(responseData){			
				var moretags=JSON.parse(JSON.stringify(responseData));
				moretags.unshift(newsrc);
				addNode(moretags);				
			},beforeSend:function(){
				var loading=document.createElement("img");
				loading.setAttribute("src","/myimg/loading.gif");	
				loading.setAttribute("id","loading_gif");
				loading.style.cssText="position:fixed;top:490px;width:1000px;height:790px;";
				document.getElementById("cy").appendChild(loading);
				},
			complete:function(){
				$("img").remove("#loading_gif");
				alert("관계 분석 완료!");
				},
			
			error:function(responseData){
				alert("fail");
			}											 
		 })
		 
	});
	
	updateNodeColor(cy);
}

function updatetargetkeyword(toAdd){
	var keywordset=document.getElementById("word_set");
	var current=keywordset.innerHTML;
	if(buttonClicked==1){
		keywordset.innerHTML=toAdd;
		return;
	}
	keywordset.innerHTML=current+", "+toAdd;
}

function createGraph(e){	
	if(buttonClicked==7){
		alert("효과적인 시각화를 위해 그래프 초기화 후, 다시 선택해주시기 바랍니다");
		return;
	}	
	var tag_choice;
	if(ranklayout){
		tag_choice=e.target.innerHTML.trim();
	}else{
		tag_choice=e.dataPoint.label.trim();
	}
	 if(tagset.includes(tag_choice)){
		 alert("이미 선택한 키워드 입니다.");
		 return;
	 }	
	buttonClicked++;
	updatetargetkeyword(tag_choice);
	tagset.push(tag_choice);
	$.ajax({
		url:"addNode.do",
		type: "POST",
		data : {
				"date_start" : date_start,
				"date_end": date_end,
				"tag_chosen":tag_choice
			},
		dataType : "json",
		success:function(responseData){
			var tags=JSON.parse(JSON.stringify(responseData));
			tags.unshift(tag_choice.replace("#","").trim());
			addNode(tags);},
		beforeSend:function(){
			var loading=document.createElement("img");
			loading.setAttribute("src","/myimg/loading.gif");	
			loading.setAttribute("id","loading_gif");
			loading.style.cssText="position:fixed;top:490px;width:1000px;height:790px;";
			document.getElementById("cy").appendChild(loading);
			},
		complete:function(){
			$("img").remove("#loading_gif");
			alert("관계 분석 완료!");
			},	
		error:function(responseData){
			alert("error!");
			}	
		})}

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

.date_choice{
  width: 100%;
  height: 100px;
  display: flex;
  flex-direction: row;
  justify-content: center;
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

<form>
<div class="date_choice" style="position:relative;">
<div style="position: relative; right:8.4%;"><h3 style="color:#727a82;">시작 일자 : </h3><input type="date" id="date_input" style="text-align:right;height:35px;position: absolute; right:-180%; top:15%; border-radius: 20px; "></div>
<div style="position: relative; right:-1.5%; top:1%;"><h3 style="color:#727a82;">종료 일자 : </h3><input type="date" id="date_input02" style=" text-align:right;height:35px; position: absolute; right:-180%; top:15%; border-radius: 20px;"></div>
<div style="position: relative; left:13%; top:12%;"><input type="button" id="date_submit" value="분석" style="height:40px; width: 150px;margin-right:15px; margin-left:10px;border-radius: 20px;  position:relative; top:-6%;"></div> 
</div>
</form>


<div id="main_container" style="width:2000px;  position: relative; left:6%;"> 
 
<div id="bottom" style="width: 2000px;display: flex;flex-direction: row;background:white;">
<div id="wrapper">
<div id="cy_title" style="width: 1000px;background:white;position:absolute;height: 100px; border-radius:20px;border: 1px solid #ccc;border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; ">
<div id="row1" style="display: flex;flex-direction: row;position:absolute;left:20px;top:-8px;">
<h2 style="color:#727a82;">동시 출현 네트워크 구축을 통한 키워드(해시태그) 관계 분석</h2>
<div><input type="button" id="date_submit" value="그래프 초기화" onclick="resetData()" style="height:40px; width:150px;position:absolute;left:820px;top:20px;border-radius: 20px; "></div>
<script>
function resetData(){
	cleanGraph();
	tagset=[];
	nodeset=[];
	edgeset=[];
	document.getElementById("word_set").innerHTML="";
	buttonClicked=0;
}
</script>
</div>
<div id="row2"  style="display: flex;flex-direction: row;position:absolute;top:40px;left:20px;">
<h2 style="color:#727a82;">선택된 단어 집합<span style='color:red;'>:{ <span id="word_set"></span> }</span></h2>
</div>

</div>

<div id='cy' style="width: 1000px; position:absolute;background:white;top:150px;height: 790px; border: 1px solid #ccc; border-radius:20px;border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; ">
<h2 style="color:#727a82;">
<ul>
<li> 시작일자와 종료일자를 선택하면, 선택된 기간 내에 가장 자주 등장했던<br><br>
키워드,빈도수,그리고 순위를 볼 수 있습니다. <br><br><br></li>
<li> 키워드(노드)를 클릭하면, 선택된 키워드의 연관 키워드들을 볼 수 있습니다.<br><br><br></li>
<li> 키워드간의 연관성은 뉴스 동시출현빈도(co-ocurrence)를 기반으로 계산되었습니다.<br><br>
(최대 7개의 키워드를 선택하여, 동시출현 네트워크(co-occurrence network)를<br><br>구축할 수 있습니다.)<br><br><br></li>
<li>키워드(노드)를 잇는 엣지를 클릭하면, 엣지로 연결된 두 키워드가 동시에 등장한 <br><br>뉴스 리스트를 볼 수 있습니다.<br><br></li>
</ul>
</h2>
</div>

</div>
      
<div id="tag_rank" style="width: 600px; height: 940px;background:white;position:absolute;left:52%;border: 1px solid #ccc; border-radius:20px;border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; ">
<h3><span id="search_res" style="height:25px;"></span></h3>
<div id="hash_tag_choices" style=" width: 580px;height: 750px;">
<h2 style="color:#727a82;">
<ul>
<li>
선택된 기간 동안 자주 등장한 <br><br>키워드,빈도수,그리고 순위를 보여줍니다.<br><br><br>
</li>
<li>
키워드 t<sub>1</sub>의 빈도수는	&Sigma;TF(d<sub>n</sub>,t<sub>1</sub>)로<br><br> 계산 되었습니다.<br><br><br><br><br><br><br><br><br><br>
d<sub>n</sub>:= n<sup>th</sup>뉴스 <br> TF(d<sub>n</sub>,t<sub>1</sub>):=n<sup>th</sup>뉴스에서 t<sub>1</sub> 등장횟수
</li>
</ul>
</h2>
</div>
</div>

<div id="right_container" style="width: 600px; height: 900px;position:absolute;right:-15% ">
<div id="title_box" style="background:white;position:absolute;width: 600px; height: 100px;border: 1px solid #ccc; border-radius:20px;border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; ">
<span style="color:#727a82;font-size:25px;font-weight:600;">&nbsp&bull;선택된 엣지가 연결하고 있는 두 키워드<br>&nbsp(노드)를 보여줍니다.</span>
</div>
<div id="news_box" style="background:white;position:absolute;top:150px;width: 600px; height: 680px; border: 1px solid #ccc; border-radius:20px;border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; ">
<h2 style="color:#727a82;">
<ul>
<li>선택된 기간동안 수집된 뉴스 중, 두 키워드가 <br><br>&nbsp함께 등장하는 뉴스 리스트를 보여줍니다.</li>
</ul>
</h2>
</div>
<div id="news_paging" style="background:white;position:absolute;top:860px;width: 600px; height: 80px; border: 1px solid #ccc; border-radius:20px;border-right: 3px solid #ccc;  border-bottom: 3px solid #ccc;border-top: 1px solid #ccc; border-left: 1px solid #ccc; "><h5></h5></div>
</div>
</div>
</div>
</body>
</html>