<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<script type="text/javascript">
		function showAssignSurvey(){
			document.getElementById("assignSurveyDiv").style.display="block";
		}
	</script>
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
</head>

<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Surveys</h2>
			<c:if test="${not empty failed}">
				<div class="alert alert-error">Failed to assign survey: You must select at least one patient</div>
			</c:if>
			
			<div class="panel-group" id="accordian">
			<c:forEach items="${patients}" var="p">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title">
				    		<a class="accordion-toggle" data-toggle="collapse" href="#collapse${p.patientID}">
				        	${p.firstName} ${p.lastName}
				      		</a>
				      	</h4>
				    </div>
				    
				    <div id="collapse${p.patientID}" class="panel-collapse collapse">
				    	<div class="panel-body">
					      	<table class="table">
								<tr>
									<th>Survey</th>
									<th>Patient</th>
									<th>Date Started</th>
									<th>Status</th>
									<th>Complete Date/Last Edit</th>
									<th>Remove</th>
									<th>View/Export</th>
								</tr>
					      		<c:forEach items="${surveys}" var="s">
					      			<c:if test="${p.patientID == s.patientID}">
					      				<tr>
											<td><a href="<c:url value="/show_survey/${s.resultID}"/>">${s.surveyTitle}</a></td>
											<td>${s.patientName}</td>
											<td>${s.startDate}</td>
											<td><c:choose>
													<c:when test="${!s.completed}">Incomplete</c:when>
													<c:otherwise>Complete</c:otherwise>
												</c:choose></td>
											<td>${s.editDate}</td>
											<td><a href="<c:url value="/delete_survey/${s.resultID}"/>" class="btn btn-danger">Remove</a></td>
											<td>
												<c:choose>
													<c:when test="${s.completed}">
													<a href="<c:url value="/view_survey_results/${s.resultID}"/>" class="btn btn-success">View Results</a>
													</c:when>
													<c:otherwise>
													<a href="#" class="btn btn-success disabled">View Results</a>
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
			<!--<table class="table">
				<tr>
					<th>Survey</th>
					<th>Patient</th>
					<th>Date Assigned</th>
					<th>Status</th>
					<th>Last Edit Date</th>
					<th>Remove</th>
					<th>View/Export</th>
				</tr>
				<c:forEach items="${surveys}" var="s">
				<tr>
					<td><a href="<c:url value="/show_survey/${s.resultID}"/>">${s.surveyTitle}</a></td>
					<td>${s.patientName}</td>
					<td>${s.startDate}</td>
					<td><c:choose>
							<c:when test="${!s.completed}">Incomplete</c:when>
							<c:otherwise>Complete</c:otherwise>
						</c:choose></td>
					<td>${s.editDate}</td>
					<td><a href="<c:url value="/delete_survey/${s.resultID}"/>" class="btn btn-danger">Remove</a></td>
					<td>
						<c:choose>
							<c:when test="${s.completed}">
							<a href="<c:url value="/view_survey_results/${s.resultID}"/>" class="btn btn-success">View Results</a>
							</c:when>
							<c:otherwise>
							<a href="#" class="btn btn-success disabled">View Results</a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				</c:forEach>
			</table> -->
			<a class="btn btn-primary" onClick="showAssignSurvey()">Assign Surveys</a>
		</div>

		<div class="row-fluid" id="assignSurveyDiv" style="display:none";>
			<form action="assign_surveys" method="post">
				<fieldset>
					<legend>Assign Surveys</legend>
					<label>Survey</label>
					<select name="surveyID">
						<c:forEach items="${survey_templates}" var="st">
						<option value="${st.surveyID}">${st.title}</option>
						</c:forEach>
					</select><br />
					<label>Patient(s):</label>
					<select multiple name="patients[]">
						<c:forEach items="${patients}" var="p">
						<option value="${p.patientID}">${p.firstName} ${p.lastName}</option>
						</c:forEach>
					</select><br />
					<input class="btn btn-primary" type="submit" value="Assign" />
				</fieldset>
			</form>
		</div>
	</div>
</body>
</html>
