<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

		<style type="text/css">
			.row-fluid{
				margin:10px;
			}		
		</style>		
		<title>Edit Survey Template</title>
	</head>
	
	<body>
		<div class="content">
			<%@include file="navbar.jsp" %>
			<div class="row-fluid">
				<h2>Edit Survey Template</h2>
				<form id="editSurveyTemplate" method="post" action="<c:url value="/edit_surveyTemplate/${surveyTemplate.surveyID}"/>">		
					<label>Title:</label>
					<input type="text" name="title" class="form-control" value="${surveyTemplate.title}"/>
					
					<label>Description:</label>
					<input type="text" name="description" class="form-control" value="${surveyTemplate.description}"/>
					
					<br/>				
					
					<input type="button" value="Cancel" class="btn btn-primary" align='right' onclick="javascript:history.go(-1)">
					<input type="submit" value="Save changes" class="btn btn-primary"/>
				</form>
			</div>
		</div>
	
	</body>
</html>