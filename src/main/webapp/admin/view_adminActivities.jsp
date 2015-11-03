<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Admin Activity Logs</title>	
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
	<style type="text/css">
		html,body{
			height:100%;
		}

		.pagetitle {
			margin-left: -30px;
		}
	</style>
</head>	
<body>	
<div id="headerholder">	
 <%@ include file="navbar.jsp"%>
</div>
<div class="content">

	<div class="row">
		<div class="col-md-8">
			<h3 >Admin Activity Journal  </h3><h4>You can add a new journal entry or edit an existing one</h4>
		</div>
		<div class="col-md-4">	
			<a href="<c:url value="/new_AdminActivity"/>" class="pull-right lgbtn" data-toggle="modal">New Entry</a>
		</div>
	</div>

	<c:if test="${not empty activityCreated}">					
		<div class ="alert alert-info"><spring:message code="message_newActivity"/></div>
	</c:if>
	<c:if test="${not empty activityDeleted }">
		<div class ="alert alert-info"><spring:message code="message_removeActivity"/></div>
	</c:if>
	<c:if test="${not empty activityUpdate }">
		<div class ="alert alert-info"><spring:message code="message_modifyActivity"/></div>
	</c:if>
	<c:if test="${not empty emptyActivityLogs}">
					<div class ="alert alert-info"><spring:message code="message_emptyActivityLogs"/></div>
	</c:if>
	<div class="tab-content">
		<div class="tab-pane active" id="all">						
			<table class="table">
				<tr>
					<th>Date</th>
					<th>Time</th>
					<th>Description</th>
					<th></th>
				</tr>

				<c:forEach items="${activityLogs}" var="al">
					<tr>
						<td><a href="<c:url value="/modify_adminActivity/${al.activityId}"/>">${al.date}</a></td>
						
						<td>${al.time}</td>
						<td width = 260>${al.description}</td>
						<td><a href="<c:url value="/delete_adminActivity/${al.activityId}"/>" class="btn btn-danger">Delete</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>

</body>
</html>