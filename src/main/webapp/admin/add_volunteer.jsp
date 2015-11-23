<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Add Volunteer</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

		<style type="text/css">
			.row-fluid{
				margin:10px;
				}
				
			div.dropdown_container {
   					 width:20px;
				}
			
			select.my_dropdown {
			    width:auto;
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
			
			function isUsernameExist(){
		//		alert("hi...");
			}
			var vlcscore;
			var expyears;
			var availhours; 
			var techscore;
			var oldpercep;

		function calctotalscore() {
			vlcscore = document.getElementById('totalVLCScore').value;
			expyears = document.getElementById('numberYearsOfExperience').value;
			availhours = document.getElementById('availabilityPerMonthe').value;
			techscore = document.getElementById('technologySkillsScore').value;
			oldpercep = document.getElementById('perceptionOfOlderAdultScore').value;

			calcvlcscore();
			calcexpyears();
			calcavailhours();
			calctechscore();
			calcoldpercep();

			var finalscore = vlcscore + expyears + availhours + techscore + oldpercep;
			document.getElementById('totalcalculated').value = finalscore;
			
			if (finalscore <= 0.55)
				document.getElementById('experience_level').value = 'Beginner';
			else if(finalscore <= 0.85 && finalscore > 0.55)
				document.getElementById('experience_level').value = 'Intermediate';
			else
				document.getElementById('experience_level').value = 'Experienced';
			}

		function calcvlcscore() {
			if (vlcscore <= 80) {
				vlcscore = 0.15;
			}
			else if (vlcscore >= 81 && vlcscore <= 95) {
				vlcscore = 0.20;
			}

			else if (vlcscore > 95) {
				vlcscore = 0.35;
			}
		}

		function calcexpyears() {
			if (expyears < 1) {
				expyears = 0.02;
			}
			else if (expyears >= 1 && expyears <= 2) {
				expyears = 0.05;
			}

			else if (expyears > 2) {
				expyears = 0.1;
			}
		}

		function calcavailhours() {
			if (availhours < 2) {
				availhours = 0.05;
			}
			else if (availhours >= 2 && availhours <= 4) {
				availhours = 0.15;
			}

			else if (availhours > 4) {
				availhours = 0.20;
			}
		}

		function calctechscore() {
			if (techscore <= 14) {
				techscore = 0.10;
			}
			else if (techscore == 15) {
				techscore = 0.15;
			}

			else if (techscore >= 16) {
				techscore = 0.25;
			}
		}

		function calcoldpercep() {
			oldpercep = oldpercep/parseFloat(100);
			oldpercep = oldpercep*0.1;
		}
	
		
		</script>
	</head>
	
	<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2 class="pagetitleadmin">New Volunteer</h2>
			<div class="row-fluid">
			<c:if test="${not empty userNameExist}">			
				<div class="alert alert-error"><spring:message code="message_username_exist"/></div>
			</c:if>		
			<c:if test="${not empty volunteerExist }">
				<div class="alert alert-error"><spring:message code="message_volunteer_exist"/></div>
			</c:if>
				<form id="add_volunteer" action="<c:url value="/add_volunteer"/>" method="POST">
					<fieldset>						
							<div class="row form-group">
								<div class="col-md-4">
									<label>First Name *</label>
									<input type="text" name="firstname" class="form-control" required/>									
								</div>
								<div class="col-md-4">
									<label>Last Name *</label>
									<input type="text" name="lastname" class="form-control" required/>
								</div>
								<div class="col-md-4">
									<label>Gender</label>
										<select name="gender" form="add_volunteer" class="form-control">
											<option value="M">Male</option>
											<option value="F">Female</option>
											<option value="O">Other</option>
										</select>
								</div>
								
							</div>							

							<div class="row form-group">
								<div class="col-md-4">	
									<label>Apt #</label>
									<input type="text" name="aptnum" class="form-control"/>
								</div>
								<div class="col-md-4">
									<label>Street #</label>
									<input type="text" name="streetnum" class="form-control" />
								</div>
								<div class="col-md-4">	
									<label>Street</label>
									<input type="text" name="street" class="form-control"/>
								</div>								
							</div>

							<div class="row form-group">
								<div class="col-md-4">	
									<label>City</label>
									<input name="city" class="form-control" type="text">
								</div>
								<div class="col-md-4">	
									<label>Province</label>
										<select name="province" form="add_volunteer" class="form-control">
											<option value="AB" >Alberta</option>
											<option value="BC" >British Colunmbia</option>							
											<option value="MB" >Manitoba</option>
											<option value="NB" >New Brunswik</option>
											<option value="NL" >Newfoundland and Labrador</option>
											<option value="NS" >Nova Scotia</option>							
											<option value="ON" selected>Ontario</option>
											<option value="PE" >PrinceEdword Island</option>
											<option value="QC" >Quebec</option>
											<option value="SK" >Saskatchewan</option>							
											<option value="NT" >Northwest Terriotories</option>
											<option value="NU" >Nunavut</option>
											<option value="YT" >Yukon</option>
										</select>
								</div>	
								<div class="col-md-4">	
									<label>Country</label>
										<select name="country" form="add_volunteer" class="form-control">
											<option value="CA" selected>Canada</option>
											<option value="ST">USA</option>
											<option value="CH">China</option>
											<option value="RU">Russia</option>
										</select>
								</div>
							</div>

							<div class="row form-group">
								<div class="col-md-4">	
									<label>Postal Code</label>
									<input name="postalcode" class="form-control" type="text"/>
								</div>
							
							
								<div class="col-md-4">		
									<label >Home Phone *</label>
									<input name="homephone" class="form-control" type="text">
								</div>

								<div class="col-md-4">	
									<label>Cell Phone</label>
									<input name="cellphone" class="form-control" type="text">
								</div>
							</div>

			
							<div class="row form-group">
								<div class="col-md-4">
									<label>Email *</label>
									<input name="email" class="form-control" type="text" required>
								</div>
								<div class="col-md-4">
									<label>Emergency Contact</label>
									<input name="emergencycontact" class="form-control" type="text">
								</div>
								<div class="col-md-4">		
									<label>Emergency #</label>
									<input name="emergencyphone" class="form-control" type="text">
								</div>
							</div>

							<div class="row form-group">
								<div class="col-md-4">
									<label>Mature or Student:</label>
									<!--<input type="text" id="status" name="status" class="form-control" />-->
									<select name="status" id="status" form="add_volunteer" class="form-control">
										<option disabled selected> --- </option>
										<option value="M">Mature</option>
										<option value="S">Student</option>
									</select>	 									
								</div>	
								<div class="col-md-4">
									<label>Date of Birth (YYYY-MM-DD)</label>
									<input id="dob" name="dob" class="form-control" data-format="yyyy-MM-dd"> 				
								</div>
								<div class="col-md-4">
									<label>Language</label>
									<select name="language" id="language" form="add_volunteer" class="form-control">
										<option value="1" selected>English</option>
										<option value="2">Hindi</option>
										<option value="3">Spanish</option>
										<option value="4">French</option>
										<option value="5">Mandarin</option>
										<option value="6">Macedonian</option>
										<option value="7">Punjabi</option>
										<option value="8">Arabic</option>
										<option value="9">Italian</option>
										<option value="10">Chinese</option>
										<option value="11">Dutch</option>
									</select>	 								
								</div>								
							</div>
							

<!-- Adding Collapsible START -->
<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingOne">
      <h4 class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
          Virtual Learning Center
        </a>
      </h4>
    </div>
    <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
      <div class="panel-body">
        	<div class="row form-group">
				<!-- div class="col-md-4">
					<label>Experience:</label>
					<select class="form-control" name="level" form="add_volunteer">
						<option value="E" selected>Experienced</option>
						<option value="I" >Intermediate</option>
						<option value="B" >Beginner</option>
					</select>	
				</div> -->
				<div class="col-md-4">
					<label>VLC ID:</label>
					<input type="text" id="vlcId" name="vlcId" class="form-control" value="0"required/>									
				</div>	
				<div class="col-md-4">
					<label>Total VLC Score(.35):</label>
					<input type="text" id="totalVLCScore" name="totalVLCScore" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="0" required/>									
				</div>
				<div class="col-md-4">
					<label>Number years of experience(.1):</label>
					<input type="text" id="numberYearsOfExperience" name="numberYearsOfExperience" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="0" required/>									
				</div>																
			</div>
							
			<div class="row form-group">
				<div class="col-md-4">
					<label>Volunteer availability(hours/month)(.2):</label>
					<input type="text" id="availabilityPerMonthe" name="availabilityPerMonthe"  class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="0" required/>									
				</div>
				<div class="col-md-4">
					<label>Technology skills score(.25):</label>
					<input type="text" id="technologySkillsScore" name="technologySkillsScore" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="0" required/>									
				</div>
				<div class="col-md-4">
					<label>Perception of older adults score(.2):</label>
					<input type="text" id="perceptionOfOlderAdultScore" name="perceptionOfOlderAdultScore" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="0" required/>									
				</div>	

				<!--  script type="text/javascript">
					var vlcscore;
					var expyears;
					var availhours; 
					var techscore;
					var oldpercep;

				function calctotalscore() {
					vlcscore = document.getElementById('totalVLCScore').value;
					expyears = document.getElementById('numberYearsOfExperience').value;
					availhours = document.getElementById('availabilityPerMonthe').value;
					techscore = document.getElementById('technologySkillsScore').value;
					oldpercep = document.getElementById('perceptionOfOlderAdultScore').value;

					calcvlcscore();
					calcexpyears();
					calcavailhours();
					calctechscore();
					calcoldpercep();

					var finalscore = vlcscore + expyears + availhours + techscore + oldpercep;
					document.getElementById('totalcalculated').value = finalscore;
					
					if (finalscore <= 0.55)
						document.getElementById('experience_level').value = 'Beginer';
					else if(finalscore <= 0.85 && finalscore > 0.55)
						document.getElementById('experience_level').value = 'Intermediate';
					else
						document.getElementById('experience_level').value = 'Experienced';
				}

				function calcvlcscore() {
					if (vlcscore <= 80) {
						vlcscore = 0.15;
					}
					else if (vlcscore >= 81 && vlcscore <= 95) {
						vlcscore = 0.20;
					}

					else if (vlcscore > 95) {
						vlcscore = 0.35;
					}
				}

				function calcexpyears() {
					if (expyears < 1) {
						expyears = 0.02;
					}
					else if (expyears >= 1 && expyears <= 2) {
						expyears = 0.05;
					}

					else if (expyears > 2) {
						expyears = 0.1;
					}
				}

				function calcavailhours() {
					if (availhours < 2) {
						availhours = 0.05;
					}
					else if (availhours >= 2 && availhours <= 4) {
						availhours = 0.15;
					}

					else if (availhours > 4) {
						availhours = 0.20;
					}
				}

				function calctechscore() {
					if (techscore <= 14) {
						techscore = 0.10;
					}
					else if (techscore == 15) {
						techscore = 0.15;
					}

					else if (techscore >= 16) {
						techscore = 0.25;
					}
				}

				function calcoldpercep() {
					oldpercep = oldpercep/parseFloat(100);
					oldpercep = oldpercep*0.1;
				}


				</script>	-->						
			</div>

			<div class="row form-group"> <!-- form-group div start -->			
				<div class="col-md-4">
					<label>Total Calculated Score:</label>
					<input type="text" id="totalcalculated" name ="totalcalculated" class="form-control" readonly="readonly"> <!--  /span>			-->					
				</div>
				<div class="col-md-4">
					<label>Experience:</label>
					<input type="text" id="experience_level" name ="experience_level" class="form-control" readonly="readonly"> <!--  /span>			-->					
				</div>
				<div class="col-md-4">
					<label>VLC Completed</label>
					<select name="vlcCompleted" id="vlcCompleted" form="add_volunteer" class="form-control">
						<option value="1">Yes</option>
						<option value="2" selected>No</option>
					</select>	 								
				</div>
				<div class="col-md-4">
					<label>VLC Completion Date (YYYY-MM-DD)</label>
					<input id="vCDate" name="vCDate" class="datepickera form-control" data-format="yyyy-MM-dd">	
				</div>	
			</div> <!-- form-group div end -->	

      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
       	Volunteer Recruitment Information</a>
      </h4>
    </div>
    <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
      <div class="panel-body">
        <div class="row form-group"> <!-- form-group div start -->
			<div class="col-md-4">
				<label>Source - where they learned about Tapestry:</label>
				<input type="text" id="source" name="source" class="form-control" >		
			</div>
			<div class="col-md-4">
				<label>Interview Completed</label>
				<select name="interviewCompleted" id="interviewCompleted" form="add_volunteer" class="form-control">
					<option value="1">Yes</option>
					<option value="2" selected>No</option>
				</select>								
			</div>		
			<div class="col-md-4">
				<label>Interview Date (YYYY-MM-DD)</label>
				<input type="text" id="interviewDate" name="interviewDate" class="datepickera form-control" data-format="yyyy-MM-dd"> 																
			</div>
		</div><!-- form-group div end -->
		<div class="row form-group">
			<div class="col-md-4">
				<label>Reference Completed</label>
				<select name="referenceCompleted" id="referenceCompleted" form="add_volunteer" class="form-control">
					<option value="1">Yes</option>
					<option value="2" selected>No</option>
				</select>									
			</div>	
			<div class="col-md-4">
				<label>Reference Check - Date (YYYY-MM-DD)</label>
				<input id="rDate" name="rDate" class="datepickera form-control" data-format="yyyy-MM-dd"> 			
			</div>
			<div class="col-md-4">
				<label>Reference 1:</label>
				<input type="text" id="reference1" name="reference1" class="form-control">
			</div>
			<div class="col-md-4">
				<label>Reference 2:</label>
				<input type="text" id="reference2" name="reference2" class="form-control" />
			</div>	

		</div>
		<div class="row form-group">
			<div class="col-md-4">
				<label>Confidentiality Completed</label>
				<select name="confidentialityCompleted" id="confidentialityCompleted" form="add_volunteer" class="form-control">
					<option value="1">Yes</option>
					<option value="2" selected>No</option>
				</select>									
			</div>
			<div class="col-md-4">
				<label>Confidentiality Agreement Date Signed (YYYY-MM-DD)</label>
				<input type="text" id="cAgreementDate" name="cAgreementDate" class="datepickera form-control" data-format="yyyy-MM-dd"> 								
			</div>
			<div class="col-md-4">
				<label>Volunteer Agreement Completed</label>
				<select name="volagreementCompleted" id="volagreementCompleted" form="add_volunteer" class="form-control">
					<option value="1">Yes</option>
					<option value="2" selected>No</option>
				</select>								
			</div>	
			<div class="col-md-4">
				<label>Volunteer Agreement Date Signed (YYYY-MM-DD)</label>
				<input type="text" id="vAgreementDate" name="vAgreementDate" class="datepickera form-control" data-format="yyyy-MM-dd"/>									
			</div>	
			<div class="col-md-4">
				<label>Photo Received</label>
				<select name="photoReceived" id="photoReceived" form="add_volunteer" class="form-control">
					<option value="1">Yes</option>
					<option value="2" selected>No</option>
				</select>	 								
			</div>
			<div class="col-md-4">
				<label>Photo Date Received (YYYY-MM-DD)</label>
				<input id="pDate" name="pDate" class="datepickera form-control" data-format="yyyy-MM-dd"> 			
			</div>
		</div>
			<div class="row form-group">
				<div class="col-md-4">
					<label>Police Completed</label>
					<select name="policeCompleted" id="policeCompleted" form="add_volunteer" class="form-control">
						<option value="1">Yes</option>
						<option value="2" selected>No</option>
					</select> 								
				</div>
				<div class="col-md-4">
					<label>Police Check Date Received (YYYY-MM-DD)</label>
					<input type="text" id="pCheckDate" name="pCheckDate" class="datepickera form-control" data-format="yyyy-MM-dd" > 								
				</div>
				<div class="col-md-4">
					<label>TB Test Completed</label>
					<select name="tbCompleted" id="tbCompleted" form="add_volunteer" class="form-control">
						<option value="1">Yes</option>
						<option value="2" selected>No</option>
					</select>								
				</div>		
				<div class="col-md-4">
					<label>TB Test Received/Date (YYYY-MM-DD)</label>
					<input type="text" id="tbTDate" name="tbTDate" class="datepickera form-control" data-format="yyyy-MM-dd" />									
				</div>
				<div class="col-md-4">
					<label>Application Completed</label>
					<select name="applicationCompleted" id="applicationCompleted" form="add_volunteer" class="form-control">
						<option value="1">Yes</option>
						<option value="2" selected>No</option>
					</select>									
				</div>
				<div class="col-md-4">
					<label>Application Form Completion Date (YYYY-MM-DD)</label>		
					<input id="aCompleteDate" name="aCompleteDate" class="datepickera form-control" data-format="yyyy-MM-dd"> 										
				</div>								
			</div>	

			<div class="row form-group">
				<div class="col-md-12">
					<label>Background Information</label>
					<input type="textarea" class="form-control" maxlength="300" name="background"/>
				</div>
				<div class="col-md-12">
					<label>Volunteer Experience</label>
					<input type="textarea" class="form-control" maxlength="300" name="volExperience"/>
				</div>	
				<div class="col-md-4">
					<label>Comfortable with Technology</label>
					<select name="techComfort" id="techComfort" form="add_volunteer" class="form-control">
						<option value="1">Yes</option>
						<option value="2" selected>No</option>
					</select>				
				</div>								
			</div>							
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingThree">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
          Current Status
        </a>
      </h4>
    </div>
    <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
      <div class="panel-body">
       <div class="row form-group">
			<div class="col-md-4">
				<label>Current Status</label>
				<select name="currentStatus" id="currentStatus" form="add_volunteer" class="form-control">
					<option value="1" selected>Active</option>
					<option value="2">Inactive</option>
					<option value="3">No Response</option>
					<option value="4">Not Accepted</option>
				</select>									
			</div>	
			<div class="col-md-4">
				<label>Reason for Inactivity</label>
				<input type="textarea" class="form-control" maxlength="300" name="reasonInactivity"/>								
			</div>								
		</div>
      </div>
    </div>
  </div>
</div>

<!-- Collapsible END -->		

	<div class="row form-group">
		<!-- div class="col-md-4">
			<label>Site:</label>									
			<select name="site" form="add_volunteer" class="form-control">
				<c:forEach items="${sites}" var="s">
					<option value="${s.siteId}">${s.name}</option>
				</c:forEach>
			</select>
		</div> -->
		<div class="col-md-4">
			<label>Organization</label>									
			<select name="organization" form="add_volunteer" class="form-control">
				<c:forEach items="${organizations}" var="o">
					<option value="${o.organizationId}">${o.name}</option>
				</c:forEach>
			</select>
		</div>
		<c:if test="${showSites}">
			<div class="col-md-4">
				<label>Site:</label>									
				<select name="site" id="site" form="add_volunteer" class="form-control">
					<c:forEach items="${sites}" var="s">
						<option value="${s.siteId}">${s.name}</option>
					</c:forEach>
				</select>
			</div>
		</c:if>
	</div>


						<h2 class="pagetitleadmin">User Account </h2>

							<div class="row form-group">
								<div class="col-md-4">
									<div class="input-group input-group-lg">
										<span class="input-group-addon">Username</span>
								 		<input name="username" type="text" class="form-control" onchange="isUsernameExist();" required>
									</div>
								</div>
								
								<div class="col-md-4">
									<div class="input-group input-group-lg">
								  		<span class="input-group-addon">Password</span>
								  		<input type="password" name="password" class="form-control" value="tapestry" required>
									</div>
								</div>
							</div>

						<h2 class="pagetitleadmin">Availability </h2>
						<%@include file="add_availabilities.jsp" %>			
							</div>
							<!--  br/>-->
					<h2 class="pagetitleadmin"> Comments </h2>
					
					<div class="col-md-10">		
						<input type="textarea" class="form-control" maxlength="300" name="notes"/>
					</div>

						<input type="button" value="Cancel" class="btn btn-primary" onclick="javascript:history.go(-1)">
						<input class="btn btn-primary" type="submit" value="Create" />

					</fieldset>
				</form>
			</div>		
		</div>
	</div>
<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/lib/picker.js"></script>
<script src="${pageContext.request.contextPath}/resources/lib/picker.date.js"></script>
<script src="${pageContext.request.contextPath}/resources/lib/picker.time.js"></script>
<script src="${pageContext.request.contextPath}/resources/lib/legacy.js"></script>
<script type="text/javascript">

	$('.datepickera').pickadate({
	  labelMonthNext: 'Go to the next month',
	  labelMonthPrev: 'Go to the previous month',
	  labelMonthSelect: 'Pick a month from the dropdown',
	  labelYearSelect: 'Pick a year from the dropdown',
	  selectMonths: true,
	  selectYears: true,
	  yearSelector: 100,
	  formatSubmit: 'yyyy-mm-dd',
	  hiddenName: true
	})

</script>
	</body>
</html>