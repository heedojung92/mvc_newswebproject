<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.vo.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login Page</title>
<link rel="stylesheet" href="assets/css/login.css" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.js"></script>
<script>
window.onload = function(){
	var updatedmember=JSON.parse(JSON.stringify(<%=(Integer)request.getAttribute("memberupdated")%>));
	if(updatedmember!=null){
		alert("성공적으로 정보가 수정되었습니다! ");
		alert("변경된 정보로 다시 로그인 해 주시기 바랍니다.");
	}
	$("#login").click(function(){
		if($("#email").val()!=""&&$("#password").val()!=""){
			$.ajax({
				url:"login_check.do",
				type:"POST",
				data:{
					"email":$('#email').val(),
					"password":$('#password').val()
				},
				dataType : "json",
				success:function(responseData){
					var logincheck=responseData.res;
					if(logincheck=="1"){
						alert("login success");
						location.href = "connect_mypage.do";
					}else if(logincheck=="2"){
						alert("관리자 권한으로 접속합니다")
						location.href="admin_login.do"
					}else if(Number(logincheck)>2){
						location.href="newsbody.do?newsno="+logincheck;
					}
					else{
						alert("이메일 혹은 비밀번호를 다시 확인해 주시기 바랍니다.");
					}
					},
				error:function(responseData){
					alert("error!");
				}
			});
		}else{
			alert("이메일과 비밀번호를 입력해주시기 바랍니다.");
		}
	});	
};

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
		<div class="login">
				<input type="text" placeholder="username" id="email"/><br/>
				<input type="password" placeholder="password" id="password"/><br/>
				<input type="submit" value="로그인" id="login" style="background:7a7a7a; border-radius:20px; width:100px; height: 30px; position:relative; top:10px;">
				<button id="signup" type='submit' style="font-color:black; background:7a7a7a; border-radius:20px; width:100px; height: 30px; position:relative; top:10px; left:30px;"><a href="/news/signuprequest.do" style="text-decoration:none;">회원가입</a></button>
		</div>
<div>
</div>
</body>
</html>
