<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
	<script type="text/javascript">
		function checkNumericInput(id)
		{				
			var element = document.getElementById(id);
			
			if (isNaN(element.value)) 
			  {
			    alert("Please input numeric data" );
			    element.value="";
			  }
		}
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
		                  document.getElementById('volunteer2').options.length = 0;
	                	  
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
		<c:if test="${not empty misMatchedVolunteer}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_misMatchedVolunteer"/></div>
		</c:if>	
		<c:if test="${not empty sameVolunteer}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_sameVolunteer"/></div>
		</c:if>	
		<c:if test="${not empty updatePatientSuccessfully}">			
			<div class="alert alert-info"><spring:message code="message_patient_updateSuccessful"/></div>
		</c:if>	
		<c:if test="${not empty createPatientSuccessfully}">			
			<div class="alert alert-info"><spring:message code="message_patient_createSuccessful"/></div>
		</c:if>
		<c:if test="${not empty emptyPatients}">
					<div class ="alert alert-info"><spring:message code="message_emptyPatients"/></div>
				</c:if>
		<div class="row-fluid">
			<h2>Patients</h2>
			<table>
				<tr>
					<td align="left">
						<form id="searchPatient" action="<c:url value="/search_patient"/>" method="POST" onsubmit="return validateVolunteer()">
							<fieldset>
								<label>Volunteer:</label>
								<select name="search_volunteer_forPatient" form="searchPatient" id="search_volunteer" >
									<c:forEach items="${volunteers}" var="v">				
										<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq selectedVolunteer}">selected</c:if> >${v.displayName}</option>
									</c:forEach>
								</select>
								<input class="btn btn-primary" type="submit" value="Search" />
							</fieldset>
						</form>
					</td>
					<td align="right">
						<div class="col-md-3">
							<a href="#addPatient" class="btn btn-primary" data-toggle="modal">Add New</a>
						</div>
					</td>
				</tr>
			</table>				
			
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Preferred Name</th>
					<th>Volunteer1</th>
					<th>Volunteer2</th>
					<th>Edit</th>
					<!-- <th>Remove</th> -->
				</tr>
                <c:forEach items="${patients}" var="p">
	                <tr>
	                    <td>${p.firstName} ${p.lastName} (${p.gender})</td>
	                    <td>${p.preferredName}</td>
	                    <td>${p.volunteerName}</td>
	                    <td>${p.partnerName}</td>
	                    <td><a href="<c:url value="/edit_patient/${p.patientID}"/>" class="btn btn-info">Edit</a></td>
	                    <!-- Disabling the ability to delete patients as data relating to a patient should not be deleted -->
	                    <!-- <td><a href="<c:url value="/remove_patient/${p.patientID}"/>" class="btn btn-danger">Remove</a></td> -->
	                </tr>
                </c:forEach>
			</table>
			<!--  a href="#addPatient" class="btn btn-primary" data-toggle="modal">Add New</a>-->
		</div>
	</div>
	
	<!-- Modal -->

<div class="modal fade" id="addPatient" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add Patient</h3>
  		</div>
  		<div class="modal-body">
  			<form id="newPatient" method="post" action="<c:url value="/add_patient"/>">
  				<div class="row form-group">
  					<div class="col-md-6">
						<label>First Name:</label>
						<input type="text" name="firstname" class="form-control" required/>
					</div>
					<div class="col-md-6">
						<label>Last Name:</label>
						<input type="text" name="lastname" class="form-control" required/>
					</div>
					<div class="col-md-6">
						<label>Preferred Name:</label>
						<input type="text" name="preferredname" class="form-control"/>
					</div>
					<div class="col-md-6">
					<label>Gender:</label>
						<select name="gender" form="newPatient" class="form-control">
							<option value="M">Male</option>
							<option value="F">Female</option>
							<option value="O">Other</option>
						</select>
					</div>
					<div class="col-md-6">
						<label>Volunteer1:</label>
						<select name="volunteer1" id="volunteer1" form="newPatient" class="form-control">
							<option value=""></option>
							<c:forEach items="${volunteers}" var="v">
								<option value="${v.volunteerId}">${v.displayName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-md-6">
						<label>Volunteer2:</label>
						<select name="volunteer2" id="volunteer2" form="newPatient" class="form-control">
							<option value=""></option>
						</select>
					</div>	
					<div class="col-md-6">
						<label>Username in PHR:</label>
						<input type="text" name="username_myoscar" class="form-control" required/>
					</div>
					<div class="col-md-6">
						<label>MRP:</label>
						<input type="text" id="mrp" name="mrp" class="form-control" onchange="checkNumericInput(this.id);" required/>
					</div>		
					<div class="col-md-6">
						<label>MRP Firstname:</label>
						<input type="text" name="mrp_firstname" class="form-control" required/>
					</div>
					<div class="col-md-6">
						<label>MRP Lastname:</label>
						<input type="text" name="mrp_lastname" class="form-control" required/>
					</div>						
				</div>		
					<label>PHR verified? </label>
					<input type="radio" name="myoscar_verified" value="1" />Yes
					<input type="radio" name="myoscar_verified" value="0" checked/>No
					<br/>
					<label>Clinic:</label>
					<select name="clinic" form="newPatient" class="form-control">					
						<option value=""></option>
						<c:forEach items="${clinics}" var="c">
							<option value="${c.clinicId}">${c.clinicName}</option>
						</c:forEach>
					</select>
					
					<!-- 
					<label>Availability:</label><br/>
					<%@include file="add_availabilities.jsp" %>   
					-->
					<label>Notes</label>
					<textarea name="notes" class="form-control"></textarea>
					<label>Alerts</label>
					<textarea name="alerts" class="form-control"></textarea>
			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			<input class="btn btn-primary" form="newPatient" type="submit" value="Add" />
  		</div>
  	</div>
  </div>
</div>
</body>
</html>
