<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Add Client</title>
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
</head><!-- Modal -->
<body>
	<div class="container">
		<%@include file="navbar.jsp" %>
		<div class="row">
			<h3>Create Client Profile</h3>
		</div>
		<div class="row">
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
		<div class="row">
			<input class="btn btn-primary" form="newPatient" type="submit" value="Add" />
		</div>
	</div>
</body>
</html>