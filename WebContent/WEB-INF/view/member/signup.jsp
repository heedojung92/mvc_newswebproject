<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>회원 가입</title>
<meta charset="UTF-8">
<link rel="stylesheet" href="assets/css/login.css" />
<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>

<script type="text/javascript" src="http://code.jquery.com/jquery-3.4.1.min.js"></script>
<script type="text/javascript">
var emailValidation = false;
$(document).ready(
	function(e) {
		$('#email').focusout(
				function() {
					$.ajax({
						url : "/news/idCheck.do",
						type : "GET",
						data : {"email" : $('#email').val()},
						success : function(responseData) {
							var data = JSON.parse(responseData);
							if (data == 0&&data!=-1&& $('#email').val() != '') {
								$('#idCheck').html('<h6 style="color:blue">사용가능합니다.</h6>');
								emailValidation = true;}
							else if($('#email').val() == ''){
								$('#idCheck').html('<h6 style="color:red">이메일을 입력해주세요</h6>');}
							else if(data==-1){
								$('#idCheck').html('<h6 style="color:red">이메일 형식을 올바르게 입력해주세요</h6>');
								} else {
									$('#idCheck').html('<h6 style="color:red">이미 중복된 아이디입니다. 사용불가능합니다.</h6>');
									}
							},
							error : function(responseData) {
								alert("에러");
								}
							})
					});
		$('#name').focusout(function() {
			$.ajax({
				url : "/news/nameCheck.do",
				type : "GET",
				data : {"name" : $('#name').val()},
				success : function(responseData) {
					var data = JSON.parse(responseData);
					if (data == 0&& $('#name').val() != '') {
						$('#nameCheck').html('<h6 style="color:blue">사용 가능합니다.</h6>');
						if (emailValidation) {
							document.getElementById("register").disabled = false;
							}
						}else if($('#name').val() == ''){
							$('#nameCheck').html('<h6 style="color:red">이름을 입력해주시기 바랍니다.</h6>');
						}else {
							$('#nameCheck').html('<h6 style="color:red;">이미 중복된 닉네임(이름)입니다. </h6>');
							document.getElementById("register").disabled = true;
							}
					},
					error : function(responseData) {
						alert("에러");
						}
					});
			});
		$('#passwordbox').keyup(function ad() {
			var password = $('#pwbefore').val();
			var password2 = $('#pw').val();
			if (password == ""|| password2 == "") {
				
			} else {
				if (password != password2) {
					$('#passwordCheckMessage').html('<h6 style="color:red">비밀번호가 일치하지 않습니다.</h6>');
					} else {
						$('#passwordCheckMessage').html('<h6 style="color:blue">비밀번호가 일치합니다.</h6>');
						}
				}
			});
		});
</script>
</head>
<body>
<div class="back">

</div>
<div class="grad" style="margin-top:50px; margin-left:10px;"><a href="index.jsp"><img id="img" src="images/icon.png" height="60" width="65"/></a></div>
		<div class="header">
			<div>MIN<span>EWS</span></div>
		</div>
		
		<br>
		<div class="login" style="position:relative; top:250px;">
				<div class="container-login100">
			<div class="wrap-login100 p-l-85 p-r-85 p-t-55 p-b-55">
				<form class="login100-form validate-form flex-sb flex-w"
					action="/news/signupcontrol.do" method="get">

					<!-- 여기 제목 -->
					<h1 style="font-size:40px;"> Register </h1>
					<br>
					<!-- 여기는 이메일 입력칸 -->
					<h3> Email </h3>
					<div class="wrap-input100 validate-input m-b-36"
						data-validate="Username is required">
						<input class="input100" type="text" id="email" name="email"
							placeholder="5~20 글자  영 소문자, 숫자"> <span
							class="focus-input100"></span>
						<div class="eheck_font" id="id_check"></div>
						<p id="email_div"></p>
						<div id="idCheck" style="font-size:4px;"></div>
					</div>


					<!-- 여기는 비밀번호 입력칸 -->
					<div id="passwordbox" style="width:500px">
					<h3> Password </h3>
					<div class="wrap-input100 validate-input m-b-36"
						data-validate="Password is required">
						<span class="btn-show-pass"> <i class="fa fa-eye"></i>
						</span> <input class="input100" type="password" style="width:250px;"
							placeholder="4~12 글자  숫자, 문자" id="pwbefore"
							onkeyup="ad();"> <span
							class="focus-input100"></span>
					</div>
					
					<h3> Password 확인 </h3>
					<div class="wrap-input100 validate-input m-b-36"
						data-validate="Password is required">
						<span class="btn-show-pass"> <i class="fa fa-eye"></i>
						</span> <input class="input100" type="password" id="pw" name="pw"
							placeholder="비밀번호를 다시 한번 입력하세요." onkeyup="ad();" style="background: transparent;"> <span
							class="focus-input100"></span>
					</div>
					<div id="passwordCheckMessage" style="font-size:8px;"></div>
					</div>

					<!-- 여기는 이름 입력칸 -->
					<h3> 이름 </h3>
					<div class="wrap-input100 validate-input m-b-36"
						data-validate="Name is required">
						<input class="input100" type="text" id="name" name="name"
							placeholder="한글만 입력 가능합니다."> <span class="focus-input100"></span>
						<div class="eheck_font" id="id_check"></div>
						<div id="nameCheck"></div>
					</div>
					<h3> 주민등록번호 </h3>
					<div class="wrap-input100 validate-input m-b-36"
						data-validate="Birth is required">
						<input class="input100" type="text" id="city_no" name="city_no"
							placeholder="7자리 입력  ex)9501011 -제외"> <span
							class="focus-input100"></span>
						<div class="eheck_font" id="id_check"></div>
					</div>
					<button type="submit" id="register" style="background:7a7a7a; border-radius:20px; width:100px; height: 30px; position:relative; top:10px; color:black;">회원가입</button>
				</form>				
			</div>
		</div>
		</div>
</body>
</html>