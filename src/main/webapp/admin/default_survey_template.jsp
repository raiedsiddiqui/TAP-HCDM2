<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Set Default Survey Template</title>
<script src="${pageContext.request.contextPath}/resources/js/tapestryUtils.js"></script>	
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		.right
			{
			position:absolute;
			right:0px;
			
			}
	</style>
</head>
<body>
<div class="content">
	<%@include file="navbar.jsp" %>
	<h2>Defalut Surveys</h2>
	<h5>Select the surveys that should be assigned to patients when their profile is created. Only the selected surveys will be assigned. Surveys that are not checked will need to be manually assigned.</h5>

	<br/>
	<div id="suveyTemplates">
		<form id="setDefaultSurveyTemplate" method="post" action="<c:url value="/set_defaultSurveys"/>">
			<c:forEach items="${survey_templates}" var="st">										 
				 <input type="checkbox" name="survey_template" value=${st.surveyID} ><c:choose><c:when test="${st.defaultSurvey}"><font color="red">${st.title}</font></c:when><c:otherwise>${st.title}</c:otherwise></c:choose>
				 <br>
			</c:forEach>
		
		</form>
	</div>
	
	<div class="row">
			<input class="btn btn-primary" form="setDefaultSurveyTemplate" type="submit" value="Save Settings" />
			
		</div>
</div>
</body>
</html>