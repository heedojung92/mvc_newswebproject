<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="com.vo.*" %>
<%
MemberVO mv=(MemberVO)session.getAttribute("signedUser");
%>
<!DOCTYPE html>
<html>
<head>
	<title>회원 정보수정</title>
	<meta charset="UTF-8">
<link rel="stylesheet" href="assets/css/login.css" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.js"></script>
 <script type="text/javascript">
</script> 
</head>
<body>
<div class="back">
</div>
<div class="grad" style="margin-top:50px; margin-left:10px;"><a href="index.jsp"><img id="img" src="images/icon.png" height="60" width="65"/></a></div>
		<div class="header">
			<div>MIN<span>EWS</span></div>
		</div>
<div class="login" style="position:relative; top:350px;">
		<div class="container-login100">
			<div class="wrap-login100 p-l-85 p-r-85 p-t-55 p-b-55">
				<form class="login100-form validate-form flex-sb flex-w" action="updateProcess.do" method="get">					
					<h3> Email </h3>
					<div class="wrap-input100 validate-input m-b-36" data-validate = "Username is required">
						<input class="input100" type="text" id="update_email" name="update_email" value="<%=mv.getUser_email() %>" readonly="readonly">
						<span class="focus-input100"></span>
						<div class="eheck_font" id="id_check"></div>
					</div>

					<h3>비밀번호</h3>
					<div class="wrap-input100 validate-input m-b-12" data-validate = "Password is required" >
						<span class="btn-show-pass">
							<i class="fa fa-eye"></i>
						</span>
						<input class="input100" type="password" id="password" name="password" placeholder="4~12 글자  숫자, 문자">
						<span class="focus-input100"></span>
					</div>
					
					<h3> 이름 </h3>
					<div class="wrap-input100 validate-input m-b-36" data-validate = "Username is required">
						<input class="input100" type="text" id="name" name="name" placeholder="2~4 글자 한글로만 입력">
						<span class="focus-input100"></span>
					</div>
					<button type="submit" style="background:7a7a7a; border-radius:20px; width:100px; height: 30px; position:relative; top:10px; color:black;">회원 수정</button>

</form>
</div>
</div>
</div>
<!--===============================================================================================-->
	<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/animsition/js/animsition.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/bootstrap/js/popper.js"></script>
	<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/daterangepicker/moment.min.js"></script>
	<script src="vendor/daterangepicker/daterangepicker.js"></script>
<!--===============================================================================================-->
	<script src="vendor/countdowntime/countdowntime.js"></script>
<!--===============================================================================================-->
	<script src="js/main.js"></script>

</body>
</html>