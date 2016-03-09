<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
	<script type="text/javascript">
			function confirmDelete()
			{
			  var x = confirm("Are you sure you want to delete?");
			  if (x)
			      return true;
			  else
			    return false;
			}
			
		</script>
	
</head>

<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		
		
		<div class="row-fluid">
			<h2>Survey Templates</h2>
			<c:if test="${not empty failed}">
				<div class="alert alert-error">Failed to delete survey template: survey results still exist that use the survey template</div>
			</c:if>
			<a href="#addSurvey" data-toggle="modal" class="btn btn-primary">Add new</a>
			<a href="<c:url value="/assign_survey"/>"  class="btn btn-primary"> Assign Survey</a>
			<table class="table">
				<tr>
					<th>Title</th>
					<th>Description</th>
					<th>Type</th>
					<th>Priority</th>
					<th>Remove</th>
				</tr>
				<c:forEach items="${survey_templates}" var="st">
				<tr>
					<td>${st.title}</td>
					<td>${st.description}</td>
					<td>${st.type}</td>
					<td>${st.priority}</td>									
					<c:if test="${ st.showDelete}">
						<td><a href="<c:url value="/delete_survey_template/${st.surveyID}"/>" Onclick="return confirmDelete()" class="btn btn-danger">Remove</a></td>
					</c:if>
				</tr>
				</c:forEach>
			</table>
		<!-- <a href="#addSurvey" data-toggle="modal" class="btn btn-primary">Add new</a> -->
		</div>
	
	<!-- OLD Modal 
	<div id="addSurvey" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
  		<div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add Survey</h3>
  		</div>
  		<div class="modal-body">
  			<form id="uploadSurveyForm" action="upload_survey_template" method="post" enctype="multipart/form-data">
				<fieldset>
					<legend>Add new survey</legend>
					<label>Title:</label>
					<input type="text" name="title" required/>
					<label>Description:</label>
					<input type="text" name="desc"/>
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
			<input class="btn btn-primary" form="uploadSurveyForm" type="submit" value="Upload" />
  		</div>
	</div>-->

<div class="modal fade" id="addSurvey" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add Survey</h3>
  		</div>
  		<div class="modal-body">
  			<form id="uploadSurveyForm" action="upload_survey_template" method="post" enctype="multipart/form-data">
				<fieldset>
					<legend>Add new survey</legend>
					<label>Title:</label>
					<input type="text" name="title" required/>
					<label>Description:</label>
					<input type="text" name="desc"/>
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
					
					<input type="hidden" name="MAX_FILE_SIZE" value="2000000">
					<input type="file" accept="text/*" name="file" required/>
				</fieldset>
			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			<input class="btn btn-primary" form="uploadSurveyForm" type="submit" value="Upload" />
  		</div>
	</div>
	</div>
</div>
</body>
</html>
