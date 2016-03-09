<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
	<title>LogOut</title>
	<%@include file="volunteer/volunteer_head.jsp" %>

</head>
	
<body>
	<div id="headerholder"> 
		<div class="row">
		  <div class="col-md-3 tpurple logoheight">
	        <img id="logo" src="<c:url value="/resources/images/logow.png"/>" />
	      </div>
    	<div class="col-md-9 tblack" style="height:63px;">
    </div>
  </div>
</div>

<div id="logouttext">
	<p id="logoutheader">Are you sure you want to log out?</p>
	<button class="btn btn-info" onClick="history.back()" style="float:left">Cancel</button>
	<a href="<c:url value="/j_spring_security_logout"/>" class="btn btn-primary logoutbtn">Yes</a>
</div>
</body>
</html>
