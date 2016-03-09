<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin/ Assign Volunteer Survey</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
			
			.right
			{
			position:absolute;
			right:0px;
			
			}
		}
	</style>
	
	<script type="text/javascript">
		function disableVolunteerCheckbox()
		{
			var inputs = document.getElementsByTagName("input");
//			var elements = document.forms[0].elements;	
			var assignToAll = document.getElementById("toAll");
			
			for (var i = 0; i < inputs.length; i++) 
			{  
				  if (inputs[i].type == "checkbox" && inputs[i].name != "assignAllVolunteers")	
					  inputs[i].disabled = assignToAll.checked;					
			}
			
			if (assignToAll.checked)
			{
				document.getElementById("searchVolunteer").disabled = true; 
				document.getElementById('searchVolunteerName').disabled = true;			
			}				
			else
			{
				document.getElementById("searchVolunteer").disabled = false;
				document.getElementById('searchVolunteerName').disabled = false;		
			}
				
		}
		
	</script>
</head>

<body>
	<div class="content">
		<%@include file="../navbar.jsp" %>
		<a href="<c:url value="/manage_volunteer_survey"/>">Volunteer Survey Management</a>  Assign Volunteer Survey<br/>
		<c:if test="${not empty no_survey_selected}">
			<div class ="alert alert-info"><spring:message code="message_noSurveySelected"/></div>
		</c:if>
		<c:if test="${not empty no_patient_selected}">
			<div class ="alert alert-info"><spring:message code="label_patient_ID_null"/></div>
		</c:if>
		<c:if test="${not empty successful}">
			<div class ="alert alert-info"><spring:message code="message_assign_survey_successful"/></div>
		</c:if>
		<h2>Assign Survey</h2><br/>
		<form id="assignVolunteerSurveyForm" action="<c:url value="/assign_volunteerSurvey"/>" method="post" >
			<label>Select Volunteer Survey : </label><br/>
			<select multiple id="survey_template" name="volunteerSurveyTemplates" class="form-control" style="max-width:50%;">
				<c:forEach items="${volunteerSurveyTemplates}" var="st">
					<option value="${st.surveyID}">${st.title}</option>
				</c:forEach>
			</select><br/>
			<input type ="hidden" value="${volunteer}" name = "volunteer" />
			<div id="volunteers">			
				<c:if test="${empty hideVolunteers}">   									
					<label>Select Volunteer : </label><br/>			
					<input type="checkbox" name="assignAllVolunteers" id="toAll" style="margin-bottom:10px;" onclick="disableVolunteerCheckbox()" value="true" >Assign to All Volunteers</input><br/>
					<div class="right">					
						<input type="text" id = "searchVolunteerName" name="searchVolunteerName" value="${searchVolunteerName}" />
						<input class="btn btn-primary" type="submit" id = "searchVolunteer" name="searchVolunteer" value="Search" />				
					</div>
					<div style="height:106px; overflow:auto">
						<table border="1" cellspacing="0" cellpadding = "0">			
							<tr>
								<th width="5%"></th>
								<th width="15%">Name </th>								
								<th width="20%">Organization </th>								
								<th width="15%">Phone Number</th>
							</tr>				
								<c:forEach items="${volunteers}" var="v">
									<tr>
										<td style="text-align:center;"><input type="checkbox" id ="volunteerId" name="volunteerId" value="${v.volunteerId}" /></td>
										<td>${v.firstName} ${v.lastName} </td>										
										<td>${v.organization}</td>										
										<td>${v.homePhone}</td>
									</tr>	
								</c:forEach>							
						</table>
					</div>
				</c:if>
			</div>
			<br/><br/>
			<div class="right">
				<input type="submit" class="btn btn-primary" name="assignVolunteerSurvey" value="Assign" />
			</div>		 
		</form>		
	</div>

</body>
</html>