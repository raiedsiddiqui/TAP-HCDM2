<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin/ Survey Management</title>
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
		function disablePatientCheckbox()
		{
			var inputs = document.getElementsByTagName("input");
	//		var elements = document.forms[0].elements;	
			var assignToAll = document.getElementById("toAll");
			
			for (var i = 0; i < inputs.length; i++) 
			{  
				  if (inputs[i].type == "checkbox" && inputs[i].name != "assignAllClinets")	
					  inputs[i].disabled = assignToAll.checked;					
			}
			
			if (assignToAll.checked)
			{
				document.getElementById("searchPatient").disabled = true; 
				document.getElementById('searchPatientName').disabled = true;			
			}				
			else
			{
				document.getElementById("searchPatient").disabled = false;
				document.getElementById('searchPatientName').disabled = false;		
			}
		}
		
		function goAction(){
			var site = document.getElementById("sites");		
			var siteId = site.options[site.selectedIndex].value;
			
			if (siteId == 000)
			{
				alert("Please select a site first!");
				return false;
			}			
			else
			{
				document.getElementById("hActionAssignSurvey").value = document.pressed;					
				var action = document.pressed;
				
				if (action ==="Search")
				{					
					if (document.getElementById("searchPatientName").value =="")
					{
						alert("Please type full or partial of name you want to search!");
						return false;
					}					
				}
				
				if (action==="Assign")
				{
					var st = document.getElementById("survey_template");
					var value = st.options[st.selectedIndex].value;
					
					if (value === "")
					{
						alert("Please select at least one survey template!");
						return false;
					}
				}
				
				
				return true;
			}
		}
		
		
	</script>
</head>

<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<a href="<c:url value="/manage_survey"/>" >Survey Management</a> > Assign Survey<br/>
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
		<form id="assignSurveyForm" action="<c:url value="/assign_selectedSurvey"/>" method="post" onsubmit="return goAction();">
			<c:if test="${not empty showSites}">
				<label>Site: </label>
				<select name="sites" id="sites" form="assignSurveyForm" class="form-control"  onchange="this.form.submit()">
					<option value="000"></option>
					<c:forEach items="${sites}" var="s">						
						<option value="${s.siteId}" ${selectedSite == s.siteId ? 'selected' : ''}>${s.name}</option>
					</c:forEach>
				</select>
			</c:if>
		
			<label>Select Survey : </label><br/>
			<select multiple id="survey_template" name="surveyTemplates" class="form-control" style="max-width:50%;">
				<c:forEach items="${surveyTemplates}" var="st">
					<option value="${st.surveyID}">${st.title}</option>
				</c:forEach>
			</select><br/>
			<input type ="hidden" value="${patient}" name = "patient" />
			<input id="hActionAssignSurvey" name="hActionAssignSurvey" type="hidden" value=""/>
			<div id="clients">			
				<c:if test="${empty hideClients}">   									
					<label>Select Client : </label><br/>			
					<input type="checkbox" name="assignAllClinets" id="toAll" style="margin-bottom:10px;" onclick="disablePatientCheckbox()" value="true" >Assign to All clients</input><br/>
					<div class="right">					
						<input type="text" id = "searchPatientName" name="searchPatientName" value="${searchPatientName}"  />
						<input class="btn btn-primary" type="submit" id = "searchPatient" name="searchPatient" value="Search" onclick="document.pressed=this.value"/>				
					</div>
					<div style="height:106px; overflow:auto">
						<table border="1" cellspacing="0" cellpadding = "0">			
							<tr>
								<th width="5%"></th>
								<th width="15%">Name </th>
								<th width="10%">DOB </th>
								<th width="5%">Age </th>
								<th width="5%">Gender </th>
								<th width="20%">Clinic </th>
								<th width="15%">MRP </th>
								<th width="10%">City </th>
								<th width="15%">Phone Number</th>
							</tr>				
								<c:forEach items="${patients}" var="p">
									<tr>
										<td style="text-align:center;"><input type="checkbox" id ="patientId" name="patientId" value="${p.patientID}" /></td>
										<td>${p.firstName} ${p.lastName} </td>
										<td>${p.bod}</td>
										<td>${p.age}</td>
										<td>${p.gender}</td>
										<td>${p.clinicName}</td>
										<td>${p.mrp}</td>
										<td>${p.city}</td>
										<td>${p.homePhone}</td>
									</tr>	
								</c:forEach>							
						</table>
					</div>
				</c:if>
			</div>
			<br/><br/>
			<div class="right">
				<input type="submit" class="btn btn-primary" name="assignSurvey" value="Assign" onclick="document.pressed=this.value"/>
			</div>	
			<input id="hSelectSite" name="hSelectSite" type="hidden" value=""/>	 
		</form>		
	</div>

</body>
</html>