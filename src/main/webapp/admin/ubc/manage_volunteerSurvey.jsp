<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin/ Volunteer Survey Management </title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
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
		<%@include file="../navbar.jsp" %>
		<c:if test="${not empty surveyTemplateCreated}">
			<div class ="alert alert-info"><spring:message code="message_newSurveyTemplate"/></div>
		</c:if>
		<c:if test="${not empty surveyTemplateDeleted }">
			<div class ="alert alert-info"><spring:message code="message_removeSurveyTemplate"/></div>
		</c:if>
		<c:if test="${not empty surveyTemplateUpdated }">
			<div class ="alert alert-info"><spring:message code="message_modifySurveyTemplate"/></div>
		</c:if>			
		<a href="<c:url value="/manage_volunteer_survey"/>" >Survey Management</a> ><br/>
		<table >
			<tr>
				<td>
					<div class="row-fluid">
						<form action="<c:url value="/search_volunteer_survey"/>" method="post">
							<fieldset>
								<label>Title:</label>
								<input type="text" name="searchTitle" value="${searchTitle}" required />
								<input class="btn btn-primary" type="submit" value="Search" />
							</fieldset>
						</form>
					</div>		
				</td>
				<td class="right">
					<a href="<c:url value="/go_assign_survey/0"/>"  class="btn btn-primary"> Assign Survey</a>
					<a href="#addSurvey" data-toggle="modal" class="btn btn-primary">Add Survey</a>
				</td>
			</tr>
		</table>
				
			<c:if test="${not empty failed}">
				<div class="alert alert-error">Failed to delete survey template: survey results still exist that use the survey template</div>
			</c:if>
			
			
			<table class="table">
				<tr>
					<th>Title</th>
					<th>Description</th>
					<th>Type</th>
					<th>Priority</th>				
					<th>Date Added</th>				
					
				</tr>
				<c:forEach items="${survey_templates}" var="st">
				<tr>					
					<td><a href="<c:url value="/modify_volunteerSurveyTemplate/${st.surveyID}"/>">${st.title}</a></td>
					<td width="200">${st.description}</td>
					<td>${st.type}</td>
					<td>${st.priority}</td>					
					<td>${st.createdDate }</td>	
				</tr>
				</c:forEach>
			</table>
		<!-- <a href="#addSurvey" data-toggle="modal" class="btn btn-primary">Add new</a> -->
		</div>

<div class="modal fade" id="addSurvey" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add Survey</h3>
  		</div>
  		<div class="modal-body">
  			<form id="uploadSurveyForm" action="upload_volunteer_survey_template" method="post" enctype="multipart/form-data">
				<fieldset>
					<legend>New survey</legend>
					<label><h3>Title:</h3></label>
					<input type="text" name="title" required/>
					<label>Description:</label>
					<input type="textarea" class="form-control" maxlength="50" name="desc"/>
					<label>Type:</label>
					<select name="type">
						<option value="MUMPS">MUMPS</option>
					</select>
					<label>Priority: (Higher numbers will be above lower numbers on the patient page)</label>
					<select name="priority">
						<c:forEach begin="0" end="9" varStatus="loop">
						<option value="${loop.index}">${loop.index}</option>
						</c:forEach>
					</select>									
					<label>File:</label>
					<input type="hidden" name="MAX_FILE_SIZE" value="2000000">
					<input type="file" accept="text/*" name="file" required/>
				</fieldset>
			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			<input class="right" form="uploadSurveyForm" type="submit" value="Add Survey" />
  		</div>
	</div>
	</div>
</div>
</body>
</html>
