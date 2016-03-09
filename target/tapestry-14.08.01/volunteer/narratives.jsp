<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>


	<title>Tapestry</title>


  	<%@include file="volunteer_head.jsp" %>


	<style type="text/css">
		html,body{
			height:100%;
		}
		.bootstrap-datetimepicker-widget{
			z-index:9999;
		}
	</style>
	
<script type="text/javascript">
	function activenav() {
		var x = document.getElementById("navhome");
		x.style.backgroundColor="rgb(100, 100, 100)";
	}


	</script>
</head>
<body onload="activenav()">
<%@include file="subNavi.jsp" %>
	
<div class="content">
	<div class="row">
			<div class="col-md-8">
				<h3 class="pagetitle">My Narratives <span class="pagedesc">Select client name and then select visit date to which you want to attach a narrative</span> </h3>
			</div>
			<!-- <div class="col-md-4">	
				<a href="<c:url value="/add_narrative"/>" class="pull-right lgbtn" data-toggle="modal" onclick="return false;">New Narrative</a>
			</div> -->
		</div>
	</div>

<div class="container">	
	<c:if test="${not empty NarrativeCreated}">
		<div class="alert alert-info"><spring:message code="message_newNarrative"/></div>
	</c:if>
	<c:if test="${not empty narrativeDeleted }">
		<div class ="alert alert-info"><spring:message code="message_removeNarrative"/></div>
	</c:if>
	<c:if test="${not empty narrativeUpdate }">
		<div class ="alert alert-info"><spring:message code="message_modifyNarrative"/></div>
	</c:if>
	


	<div class="row-fluid">
		<div class="panel-group" id="accordian">
			<c:forEach items="${patients}" var="p">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title">
				    		<a class="accordion-toggle" data-toggle="collapse" href="#collapse${p.patientID}">
				        	${p.displayName}
				      		</a>
				      	</h4>
				    </div>
				    
				    <div id="collapse${p.patientID}" class="panel-collapse collapse">
				    	<div class="panel-body">
					      	<table class="table">
								<tr>
									<th>Status</th>
									<th>Visit Date</th>
									<th>Volunteer  Partner</th>
									<th>Edit/Add</th>									
								</tr>
					      		<c:forEach items="${appointments}" var="a">
					      			<c:if test="${p.patientID == a.patientID}">
					      				<tr>	
					      					<td width="40" height="40">
												<c:choose>
												      <c:when test="${a.hasNarrative}">												      	
												      	 <img style="display:block;" id="checkmark" src="/tapestry/resources/images/checkmark_40x40.png" width="100%" height="100%"/> 
												      </c:when>												
												      <c:otherwise>
												      	<img style="display:block;" id="xmark" src="/tapestry/resources/images/xmark_40x40.png" width="100%" height="100%" /> 
												      </c:otherwise>
												</c:choose>
											</td>					      					
					      					<td>${a.date}</td>
					      					<td>${a.volunteer}, ${a.partner}</td>
											<td>
												<c:choose>
												      <c:when test="${a.hasNarrative}">
												      	<a href="<c:url value="/edit_narrative/${a.appointmentID}"/>">Edit</a>
												      </c:when>												
												      <c:otherwise>
												      	<a href="<c:url value="/add_narrative/${a.appointmentID}"/>">Add</a>
												      </c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:if>
								</c:forEach>
							</table>
				    	</div>
					</div>
				</div>
			</c:forEach>
		
		</div>
	</div>
</div>


</body>
</html>