<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

	<title>Modify Narrative</title>
	<%@include file="volunteer_head.jsp" %>
</head>

<body>
 <%@ include file="subNavi.jsp" %>

	<div class="content">
		<div class="row-fluid">
			<div class="span12">
			<form id="modifyNarrative" action="<c:url value="/update_narrative"/>" method="POST">
			<table width="900">
				<tr><td colspan="2"><a href="<c:url value="/view_narratives"/>"><h4>My Narrative:   </h4></td></a></tr>
				<tr><td><label>Title : </label><input name="mNarrativeTitle" type="text" value="${narrative.title }" required></td>
					<td><label>EditDate : </label><input name="mNarrativeDate" type="text" value="${narrative.editDate}" readonly></td></tr>
				<tr><td colspan ="2"><hr/></td></tr>
				<tr><td colspan = "2"><textarea name="mNarrativeContent" rows="10" cols="100" >${narrative.contents}</textarea></td></tr>
				<tr><td colspan="2"><input type="hidden" name="narrativeId" value="${narrative.narrativeId}"/></td></tr>
				
			</table>
			
			</form>
				
			</div>
		</div>
		<div>
		<a href="<c:url value="/view_narratives"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
		<button id="newNarrativeButton" data-loading-text="Loading..." type="submit"  form="modifyNarrative" class="btn btn-primary">Save Changes</button>
		
		</div>
		
	</div>
	
	

</body>
</html>
