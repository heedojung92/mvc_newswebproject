<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
	<head>
		<title>MINEWS</title>
<%
if(request.getParameter("logout")!=null){
	request.getSession().invalidate();
}
%>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link rel="stylesheet" href="assets/css/main.css" />
	</head>
	<body>		
		<!-- Header -->
			<header id="header">			
				<nav class="left">
					<a href="#menu"><span>Menu</span></a>
				</nav>
				<img src="images/news.png" height="60" width="65" style="position:relative; top:10px;"/>
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
		<!-- Banner -->
			<section id="banner">
			<video id="video" preload="auto" autoplay="true" loop="loop" muted="muted" volume="0">
      			<source src="images/banner.mp4">
      			</video>
				<div class="content">
					<h1 style="color:7a7a7a;">경제 이슈, 간편하고 쉽게 알 수 없을까??</h1>
					<p style="color:7a7a7a;  font-weight:bold;">뉴스 핵심 키워드와 그래프를 활용한 경제 뉴스 빅데이터 분석 서비스 #Minews</p>
					<ul class="actions">
						<li><a href="#one" class="button scrolly">뉴스 보러가기</a></li>
					</ul>
				</div>
			</section>
		<!-- One -->
			<section id="one" class="wrapper">			
				<div class="inner flex flex-3" style="position: relative; top:80px;">
					<div class="flex-item image fit round" >
						<a href="index.jsp"><img src="images/home.jpg" alt="" /></a>
								<div>
							<div id="item_font" style="position: relative; left:90px;">#HOME</div>
						</div>
					</div>
					<div class="flex-item image fit round">
						<a href="/news/news_main.do"><img src="images/newsimg.jpg" alt="" /></a>
						<div id="item_font" style="position: relative; left:90px">#NEWS</div>
					</div>
					<div class="flex-item image fit round">
						<a href="/news/connect_mypage.do"><img src="images/only.jpg" alt="" /></a>
						<div id="item_font" style="position: relative; left:90px;">#MYPAGE</div>
					</div>
				</div>
			</section>	
		<!-- Footer -->
			<footer id="footer" style="position: relative; top:0px;">
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
		<!-- Scripts -->
			<script src="assets/js/jquery.min.js"></script>
			<script src="assets/js/jquery.scrolly.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/util.js"></script>
			<script src="assets/js/main.js"></script>
	</body>
</html>