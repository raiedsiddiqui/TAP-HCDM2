<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
	
	<script type="text/javascript">
		function printTable(){
			$('.table').printThis();
		}
	</script>
</head>
	
<body>	<!-- 
  <img src="<c:url value="/resources/images/logo.png"/>" /> -->
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">			
			<h2>Survey Results: ${results[0].title}</h2>
			<h4>Completed on: ${results[0].date}</h4>
			<table class="table">
				<tr>
					<th width="20">Id</th>
					<th width="350">Question</th>
					<th width="300">Answer</th>
					<th width="200">Observer Notes</th>
				</tr>				
				<c:forEach items="${results}" var="result">
					
					<tr>
						<td width="20">${result.questionId}</td>
						<td width="350">${result.questionText}</td>
						<td width="300">${result.questionAnswer}</td>
						<td width="200">${result.observerNotes}</td>
					</tr>
					
				</c:forEach>
			</table>
			<a href="<c:url value="/export_csv/${id}"/>" class="btn btn-primary">Export as CSV</a>
		</div>
	</div>
</body>
</html>
