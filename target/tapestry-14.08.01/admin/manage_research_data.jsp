<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
<title>Rearch Data Download</title>
</head>
<body>
<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Research Data Download</h2>
			<h5>You can download de-identified Research Data. Click the Download Data button below</h5>
		</div>
		
		<div>
			<table>
				<th width="500">	Site/Organization	</th>
				<th>	Download			</th>
				<c:forEach items="${sites}" var="s">
					<tr>
						<td>${s.name }</td>					 	
					 	<td><a href="<c:url value="/download_researchData/${s.siteId}?name=${s.name}"/>">Download Data</a> </td>
					</tr>						
					<c:if test="${s.siteId == 3}"> 
						<tr colspan=2><td><a href="<c:url value="/download_caregiver_researchData/3"/>">Download UBC CareGiver Data</a> </td></tr>	
				</c:if>				
				</c:forEach>
				
			</table>
		</div>
</div>		

</body>
</html>