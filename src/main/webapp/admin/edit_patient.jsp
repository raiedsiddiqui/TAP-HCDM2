<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}		
	</style>
	
	<script type="text/javascript">
		function validateVolunteer(){
			var selectedVolunteer = document.getElementById("search_volunteer");
			var vValue =selectedVolunteer.options[selectedVolunteer.selectedIndex].value;
			
			if (vValue == 0)
			{
				alert("Please select a volunteer first!");
				return false;
			}
		}
		 function getVolunteers(){			
		        $.getJSON( 
		             "volunteerList.html", 
		             {volunteerId: $('#volunteer1').val()},
		             function(data) {
		                  var html = '';
		                  var len = data.length;
		                  
		                  //clear second dropdown
		                  document.getElementById('volunteer22').options.length = 0;
	                	  
		                  //append data from DB
		                  for(var i=0; i<len; i++){
		                       html += '<option value="' + data[i].volunteerId + '">' + data[i].displayName + '</option>';
		                   }
		                  $('select#volunteer2').append(html);		                  
		             }
		          );
		 }

		 $(document).ready(function() {
		         $('#volunteer1').change(function()		        		 
		          {
		        	 getVolunteers();
		          });
		      });
	</script>
	
</head>
	
<body>	
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Edit Patient</h2>
		  	<form id="editPatient" method="post" action="<c:url value="/submit_edit_patient/${patient.patientID}"/>" >

		  	<div class="row">
					<div class="col-md-6">
						<label>Research ID:</label>
						<input type="text" name="researchid" class="form-control" value="${patient.researchID}" maxlength="10" required />
					</div>	
				</div>
				
				<div class="row form-group">
  					<div class="col-md-6">
						<label>First Name:</label>
						<input type="text" name="firstname" class="form-control" value="${patient.firstName}" required/>
					</div>
					<div class="col-md-6">
						<label>Last Name:</label>
						<input type="text" name="lastname" class="form-control" value="${patient.lastName}" required/>
					</div>
					<div class="col-md-6">
						<label>Preferred Name:</label>
						<input type="text" name="preferredname" class="form-control" value="${patient.preferredName}"/>
					</div>
					<div class="col-md-6">
						<label>Gender</label>
						<select name="gender" form="editPatient" class="form-control">
							<option value="M" <c:if test="${patient.gender eq 'M'}">selected</c:if>>Male</option>	
							<option value="F" <c:if test="${patient.gender eq 'F'}">selected</c:if>>Female</option>
							<option value="O" <c:if test="${patient.gender eq 'O'}">selected</c:if>>Other</option>
						</select>
					</div>
				</div>	

				<div class="row form-group">						
					<div class="col-md-6">
						<label>Volunteer1:</label>
						<select name="volunteer1" id="volunteer1" form="editPatient" class="form-control">
							<c:forEach items="${volunteers}" var="v">
								<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq patient.volunteer}">selected</c:if>>${v.displayName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-md-6">
						<label>Volunteer2:</label>
						<select name="volunteer2" id="volunteer2" form="editPatient" class="form-control">
							<c:forEach items="${volunteers}" var="v">
								<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq patient.partner}">selected</c:if>>${v.displayName}</option>
							</c:forEach> 	
						</select><br />
					</div>
					
				</div>
				
				<!-- <h2>User Account </h2>
					<div class="row form-group">
						<div class="col-md-4">
							<div class="input-group input-group-lg">
								<span class="input-group-addon">Tap Username</span>
						 		<input name="tap_username" type="text" class="form-control" value="${patient.tapUsername}" >
							</div>
						</div>
						<div class="col-md-4">
							<label>Password</label>
							<p>Go to Other > Manage Users to change client password. </p>
						</div> -->
						<!-- 
						<div class="col-md-4">
							<div class="input-group input-group-lg">
						  		<span class="input-group-addon">Password</span>
						  		<input type="password" name="password" class="form-control" value="${volunteer.password}">
							</div>
						</div>-->
					<!-- </div> -->
				<div class="row form-group">	
					<div class="col-md-6">
						<label>PHR verified? </label>
						<input type="radio" name="myoscar_verified" value="1" <c:if test="${patient.myoscarVerified eq 1}">checked</c:if>/>Yes
						<input type="radio" name="myoscar_verified" value="0" <c:if test="${patient.myoscarVerified eq 0}">checked</c:if>/>No
					</div>
					<div class="col-md-6">
						<label>Clinic:</label>						
						<select name="clinic" form="editPatient" class="form-control">
							<c:forEach items="${clinics}" var="c">
								<option value="${c.clinicId}" <c:if test="${c.clinicId eq patient.clinic}">selected</c:if>>${c.clinicName}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="row form-group">
					<div class="col-md-6">
						<label>MRP:</label>
						<input type="text" name="mrp" class="form-control" value="${patient.mrp}" required/>
					</div>		
					<div class="col-md-6">
						<label>MRP Firstname:</label>
						<input type="text" name="mrp_firstname" class="form-control" value="${patient.mrpFirstName}" required/>
					</div>
					<div class="col-md-6">
						<label>MRP Lastname:</label>
						<input type="text" name="mrp_lastname" class="form-control" value="${patient.mrpLastName}" required/>
					</div>		
				</div>

				
				
	<!--  			<label>Availability:</label><br/>
				<%@include file="edit_availabilities.jsp" %> 
			-->	

				<div class="row form-group">	
					<div class="col-md-10">
						<label>Notes</label>
						<textarea name="notes" class="form-control">${patient.notes}s</textarea>
					</div>
				</div>	
				<div class="row form-group">	
					<div class="col-md-10">
						<label>Alerts</label>
						<textarea name="alerts" class="form-control">${patient.alerts}s</textarea>
					</div>
				</div>	
				<a href="<c:url value="/manage_patients"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
				<input type="submit" value="Save changes" class="btn btn-primary"/>
			</form>
		</div>
	</div>
</body>
</html>
