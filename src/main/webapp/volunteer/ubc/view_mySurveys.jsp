<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Volunteer Survey for UBC</title>
	<%@include file="../volunteer_head.jsp" %>

	<style type="text/css">
		html,body{
			height:100%;
		}
		
	.pagetitle {
		margin-left: -30px;
	}

	.visitcompletebox {
		padding-top: 20px;
		float:right;
	}
	.panel-heading {
		padding: 0;
	}

	.surveybtn {
			background-color:#1FA8CF;
		}
	</style>
</head>
<body>
	<%@include file="../subNavi.jsp" %>
	
	<div class="container">
		<div id="myTest" ></div>
	<div class="row">	
		<h3 class="pagetitle">My Surveys <span class="pagedesc">Hi ${volunteerName}, below you will find surveys that need to be completed by you</span></h3>	
	</div>		

	<div class="row-fluid">
	</div>
	<c:if test="${not empty completed}">
		<p class="alert alert-success">Completed survey: ${completed}</p>
	</c:if>
	<c:if test="${not empty inProgress}">
		<p class="alert alert-warning">Exited survey: ${inProgress}</p>
	</c:if>
   	<div class="row">
		<c:forEach items="${inProgressVolunteerSurveys}" var="ips">
	    	<div class="col-xs-12 col-sm-6 col-md-6 col-lg-4">
		    	<a href="<c:url value="/open_volunteerSurvey/${ips.resultID}"/>" class="surveybtn btn">
						${ips.surveyTitle}<br/>
						<span class="surveydesc">${ips.description}</span>
				</a>
			</div>
		</c:forEach>
	</div>
 				
<h4 class="pagetitle" style="float:none">Completed Surveys <span class="pagedesc"></span></h4>

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
	<c:forEach items="${completedVolunteerSurveys}" var="csr">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingOne">
      			<h4 class="panel-title">
      				<a class="surveybtnc btn accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#collapse${csr.surveyID}">
      					${csr.surveyTitle} 
      				</a>
      			</h4>
      		</div>

			<div id="collapse${csr.surveyID}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
				<div class="panel-body">
					<table class="table">
						<tr>
							<th>Question</th>
							<th>Answer</th>																	
						</tr>	
						<c:forEach items="${displayResults}" var="dr">	
							<c:if test="${dr.surveyId == csr.surveyID}">														      			
			      				<tr>										      			
					      			<td width="50%" height="40">${dr.questionText }</td>					      					
					      			<td width="50%">${dr.questionAnswer}</td>
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
</body>
</html>