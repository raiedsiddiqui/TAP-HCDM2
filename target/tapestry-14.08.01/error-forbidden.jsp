<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
</head>
	
<body>
	<img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="container">
		<h1>Access Denied</h1>
		<h2>You don't have permission to view this page<h2>
		<button class="btn btn-info" onClick="history.back()">Go back</button>
		<a href="<c:url value="/j_spring_security_logout"/>" class="btn btn-primary">Change user</a>
	</div>
</body>
</html>
