<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Modify Volunteer</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

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
				    alert("Must input numbers" );
				    element.value="";
				}
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
		</script>
	
	</head>
	
	<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Volunteers </h2>
			<div class="row-fluid">
				<form id="modify_volunteer" action="<c:url value="/update_volunteer/${volunteer.volunteerId}"/>" method="POST">
					<fieldset>
							<div class="row form-group">
								<div class="col-md-4">
									<label>First Name*</label>
									<input type="text" name="firstname" class="form-control" value="${volunteer.firstName}" required/>
								</div>
								<div class="col-md-4">
									<label>Last Name*</label>
									<input type="text" name="lastname" class="form-control" value="${volunteer.lastName}" required/>
								</div>
								<div class="col-md-4">
									<label>Gender:</label>
										<select name="gender" form="modify_volunteer" class="form-control">
											<option value='M' <c:if test="${volunteer.gender eq 'M'}">selected</c:if>>Male</option>
											<option value='F' <c:if test="${volunteer.gender eq 'F'}">selected</c:if>>Female</option>
											<option value="O" <c:if test="${volunteer.gender eq 'O'}">selected</c:if>>Other</option>
										</select>
								</div>
							</div>
						

							<div class="row form-group">
								<div class="col-md-4">	
									<label>Apt #:</label>
									<input type="text" name="aptnum" class="form-control" value="${volunteer.aptNumber}"/>
								</div>
								<div class="col-md-4">
									<label>Street #:</label>
									<input type="text" name="streetnum" class="form-control" value="${volunteer.streetNumber}" />
								</div>
								<div class="col-md-4">	
									<label>Street:</label>
									<input type="text" name="street" class="form-control" value="${volunteer.street}"/>
								</div>								
							</div>		
							<div class="row form-group">
								<div class="col-md-4">	
									<label>City:</label>
									<input name="city" class="form-control" value="${volunteer.city}" type="text">
								</div>
								<div class="col-md-4">	
									<label>Province:</label>
									<select name="province" form="modify_volunteer" class="form-control">
										<option value='AB' <c:if test="${volunteer.province eq 'AB'}">selected</c:if>>Alberta</option>
										<option value='BC' <c:if test="${volunteer.province eq 'BC'}">selected</c:if>>British Colunmbia</option>							
										<option value='MB' <c:if test="${volunteer.province eq 'MB'}">selected</c:if>>Manitoba</option>
										<option value='NB' <c:if test="${volunteer.province eq 'NB'}">selected</c:if>>New Brunswik</option>
										<option value='NL' <c:if test="${volunteer.province eq 'NL'}">selected</c:if>>Newfoundland and Labrador</option>
										<option value='NS' <c:if test="${volunteer.province eq 'NS'}">selected</c:if>>Nova Scotia</option>							
										<option value='ON' <c:if test="${volunteer.province eq 'ON'}">selected</c:if>>Ontario</option>
										<option value='PE' <c:if test="${volunteer.province eq 'PE'}">selected</c:if>>PrinceEdword Island</option>
										<option value='QC' <c:if test="${volunteer.province eq 'QC'}">selected</c:if>>Quebec</option>
										<option value='SK' <c:if test="${volunteer.province eq 'SK'}">selected</c:if>>Saskatchewan</option>							
										<option value='NT' <c:if test="${volunteer.province eq 'NT'}">selected</c:if>>Northwest Terriotories</option>
										<option value='NU' <c:if test="${volunteer.province eq 'NU'}">selected</c:if>>Nunavut</option>
										<option value='YT' <c:if test="${volunteer.province eq 'YT'}">selected</c:if>>Yukon</option>											
									</select>
								</div>	
								<div class="col-md-4">	
									<label>Country:</label>
									<select name="country" form="modify_volunteer" class="form-control"s>
										<option value="CA" <c:if test="${volunteer.country eq 'CA'}">selected</c:if>>Canada</option>
										<option value="ST" <c:if test="${volunteer.country eq 'ST'}">selected</c:if>>USA</option>
										<option value="CH" <c:if test="${volunteer.country eq 'CH'}">selected</c:if>>China</option>
										<option value="RU" <c:if test="${volunteer.country eq 'RU'}">selected</c:if>>Russia</option>
									</select>
								</div>								
							</div>
							<div class="row form-group">
								<div class="col-md-4">	
									<label>Postal Code:</label>
									<input name="postalcode" class="form-control" type="text" value="${volunteer.postalCode}"/>
								</div>							
								<div class="col-md-4">		
									<label >Home Phone:</label>
									<input name="homephone" class="form-control" type="text" value="${volunteer.homePhone}">
								</div>
								<div class="col-md-4">	
									<label>Cell Phone:</label>
									<input name="cellphone" class="form-control" type="text" value="${volunteer.cellPhone}">
								</div>
							</div>			
							<div class="row form-group">
								<div class="col-md-4">
									<label>Email:</label>
									<input name="email" class="form-control" type="text" value="${volunteer.email}"  required>
								</div>
								<div class="col-md-4">
									<label>Emergency Contact:</label>
									<input name="emergencycontact" class="form-control" type="text" value="${volunteer.emergencyContact}">
								</div>
								<div class="col-md-4">		
									<label>Emergency #:</label>
									<input name="emergencyphone" class="form-control" type="text" value="${volunteer.emergencyPhone}">
								</div>
							</div>	
						<div class="row form-group">
							<div class="col-md-4">
									<label>Mature or Student:</label>
									<!-- <input type="text" id="status" name="status" value="${volunteer.status}" class="form-control" /> -->	
									<select name="status" id="status" form="modify_volunteer" class="form-control">
										<option value=""> --- </option>
										<option value="M" <c:if test="${volunteer.status eq 'M'}">selected</c:if>>Mature</option>
										<option value="S" <c:if test="${volunteer.status eq 'S'}">selected</c:if>>Student</option>
									</select>								
								</div>	
								<div class="col-md-4">
									<label>Date of Birth (YYYY-MM-DD):</label>
									<input id="dob" name="dob" value="${volunteer.dOB}" class="form-control" data-format="yyyy-MM-dd" > 				
								</div>	

								<div class="col-md-4">
									<label>Language</label>
									<select name="language" id="language" form="modify_volunteer" class="form-control">
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
						<!-- 	<div class="row form-group">
								div class="col-md-4">
									<label>Experience:</label>
									<select class="form-control" name="level" form="modify_volunteer">
										<option value='E' <c:if test="${volunteer.experienceLevel eq 'Experienced'}">selected</c:if>>Experienced</option>
										<option value='I' <c:if test="${volunteer.experienceLevel eq 'Intermediate'}">selected</c:if>>Intermediate</option>
										<option value='B' <c:if test="${volunteer.experienceLevel eq 'Beginer'}">selected</c:if> >Beginner</option>
									</select>	
								</div>
								
								<div class="col-md-4">
									<label>Total VLC Score(.35):</label>
									<input type="text" id="totalVLCScore" name="totalVLCScore" class="form-control" value="${volunteer.totalVLCScore}" onchange="checkNumericInput(this.id);calctotalscore();" required/>									
								</div>
								<div class="col-md-4">
									<label>Number years of experience(.1):</label>
									<input type="text" id="numberYearsOfExperience" name="numberYearsOfExperience" class="form-control" value="${volunteer.numYearsOfExperience}" onchange="checkNumericInput(this.id);calctotalscore();" required/>									
								</div>	
									<div class="col-md-4">
									<label>Volunteer availability(hours/month)(.2):</label>
									<input type="text" id="availabilityPerMonthe" name="availabilityPerMonthe" class="form-control" value="${volunteer.availabilityPerMonth}" onchange="checkNumericInput(this.id);calctotalscore();" required/>									
								</div>
							</div>	-->
						<!--	<div class="row form-group">
							
								<div class="col-md-4">
									<label>Technology skills score(.25):</label>
									<input type="text" id="technologySkillsScore" name="technologySkillsScore" class="form-control" value="${volunteer.technologySkillsScore}" onchange="checkNumericInput(this.id);calctotalscore();" required/>									
								</div>
								<div class="col-md-4">
									<label>Perception of older adults score(.2):</label>
									<input type="text" id="perceptionOfOlderAdultScore" name="perceptionOfOlderAdultScore" class="form-control" value="${volunteer.perceptionOfOlderAdultsScore}" onchange="checkNumericInput(this.id);calctotalscore();" required/>									
								</div>
								<div class="col-md-4">
									<label>VLC ID:</label>
									<input type="text" id="vlcId" name="vlcId" value="${volunteer.vLCID}" class="form-control" required/>									
								</div>							
							</div> -->
							<div class="row form-group">
								<div class="col-md-4">
									<label>Organization:</label>
										<select name="organization" form="modify_volunteer" class="form-control">
											<c:forEach items="${organizations}" var="o">
												<option value="${o.organizationId}" <c:if test="${o.organizationId eq volunteer.organizationId}">selected</c:if>>${o.name}</option>
											</c:forEach>
										</select>
								</div>				
							</div>
							
							
					
							
							
							


ADDING COLLAPSIBLE START
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
					<select class="form-control" name="level" form="modify_volunteer">
						<option value="E" selected>Experienced</option>
						<option value="I" >Intermediate</option>
						<option value="B" >Beginner</option>
					</select>	
				</div> -->
				<div class="col-md-4">
					<label>VLC ID:</label>
					<input type="text" id="vlcId" name="vlcId" class="form-control" value="${volunteer.vLCID}"required/>									
				</div>	
				<div class="col-md-4">
					<label>Total VLC Score(.35):</label>
					<input type="text" id="totalVLCScore" name="totalVLCScore" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="${volunteer.totalVLCScore}" required/>									
				</div>
				<div class="col-md-4">
					<label>Number years of experience(.1):</label>
					<input type="text" id="numberYearsOfExperience" name="numberYearsOfExperience" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="${volunteer.numYearsOfExperience}" required/>									
				</div>																
			</div>
							
			<div class="row form-group">
				<div class="col-md-4">
					<label>Volunteer availability(hours/month)(.2):</label>
					<input type="text" id="availabilityPerMonthe" name="availabilityPerMonthe"  class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="${volunteer.availabilityPerMonth}" required/>									
				</div>
				<div class="col-md-4">
					<label>Technology skills score(.25):</label>
					<input type="text" id="technologySkillsScore" name="technologySkillsScore" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="${volunteer.technologySkillsScore}" required/>									
				</div>
				<div class="col-md-4">
					<label>Perception of older adults score(.2):</label>
					<input type="text" id="perceptionOfOlderAdultScore" name="perceptionOfOlderAdultScore" class="form-control" onchange="checkNumericInput(this.id);calctotalscore();" value="${volunteer.perceptionOfOlderAdultsScore}" required/>									
				</div>						
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
					<select name="vlcCompleted" id="vlcCompleted" form="modify_volunteer" class="form-control">
						<option value="1" <c:if test="${volunteer.vlcCompleted eq '1'}">selected</c:if>>Yes</option>
						<option value="2" <c:if test="${volunteer.vlcCompleted eq '2'}">selected</c:if>>No</option>
					</select>	 								
				</div>
				<div class="col-md-4">
					<label>VLC Completion Date (YYYY-MM-DD)</label>
					<input id="vCDate" name="vCDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.vLCCompletionDate}">	
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
				<input type="text" id="source" name="source" class="form-control" value="${volunteer.source}" >		
			</div>
			<div class="col-md-4">
				<label>Interview Completed</label>
				<select name="interviewCompleted" id="interviewCompleted" form="modify_volunteer" class="form-control">
					<option value="1" <c:if test="${volunteer.interviewCompleted eq '1'}">selected</c:if>>Yes</option>
					<option value="2" <c:if test="${volunteer.interviewCompleted eq '2'}">selected</c:if>>No</option>
				</select>								
			</div>		
			<div class="col-md-4">
				<label>Interview Date (YYYY-MM-DD)</label>
				<input type="text" id="interviewDate" name="interviewDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.interviewDate}"> 																
			</div>
		</div><!-- form-group div end -->
		<div class="row form-group">
			<div class="col-md-4">
				<label>Reference Completed</label>
				<select name="referenceCompleted" id="referenceCompleted" form="modify_volunteer" class="form-control">
					<option value="1" <c:if test="${volunteer.referenceCompleted eq '1'}">selected</c:if>>Yes</option>
					<option value="2" <c:if test="${volunteer.referenceCompleted eq '2'}">selected</c:if>>No</option>
				</select>									
			</div>	
			<div class="col-md-4">
				<label>Reference Check - Date (YYYY-MM-DD)</label>
				<input id="rDate" name="rDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.referenceCheckDate}"> 			
			</div>
			<div class="col-md-4">
				<label>Reference 1:</label>
				<input type="text" id="reference1" name="reference1" class="form-control" value="${volunteer.reference1}">
			</div>
			<div class="col-md-4">
				<label>Reference 2:</label>
				<input type="text" id="reference2" name="reference2" class="form-control" value="${volunteer.reference2}"/>
			</div>	

		</div>
		<div class="row form-group">
			<div class="col-md-4">
				<label>Confidentiality Completed</label>
				<select name="confidentialityCompleted" id="confidentialityCompleted" form="modify_volunteer" class="form-control">
					<option value="1" <c:if test="${volunteer.confidentialityCompleted eq '1'}">selected</c:if>>Yes</option>
					<option value="2" <c:if test="${volunteer.confidentialityCompleted eq '2'}">selected</c:if>>No</option>
				</select>									
			</div>
			<div class="col-md-4">
				<label>Confidentiality Agreement Date Signed (YYYY-MM-DD)</label>
				<input type="text" id="cAgreementDate" name="cAgreementDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.cAgreementDate}"> 								
			</div>
			<div class="col-md-4">
				<label>Volunteer Agreement Completed</label>
				<select name="volagreementCompleted" id="volagreementCompleted" form="modify_volunteer" class="form-control">
					<option value="1" <c:if test="${volunteer.volagreementCompleted eq '1'}">selected</c:if>>Yes</option>
					<option value="2" <c:if test="${volunteer.volagreementCompleted eq '2'}">selected</c:if>>No</option>
				</select>								
			</div>	
			<div class="col-md-4">
				<label>Volunteer Agreement Date Signed (YYYY-MM-DD)</label>
				<input type="text" id="vAgreementDate" name="vAgreementDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.vAgreementDate}"/>									
			</div>	
			<div class="col-md-4">
				<label>Photo Received</label>
				<select name="photoReceived" id="photoReceived" form="modify_volunteer" class="form-control">
					<option value="1" <c:if test="${volunteer.photoReceived eq '1'}">selected</c:if>>Yes</option>
					<option value="2" <c:if test="${volunteer.photoReceived eq '2'}">selected</c:if>>No</option>
				</select>	 								
			</div>
			<div class="col-md-4">
				<label>Photo Date Received (YYYY-MM-DD)</label>
				<input id="pDate" name="pDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.photoDate}"> 			
			</div>
		</div>
			<div class="row form-group">
				<div class="col-md-4">
					<label>Police Completed</label>
					<select name="policeCompleted" id="policeCompleted" form="modify_volunteer" class="form-control">
						<option value="1" <c:if test="${volunteer.policeCompleted eq '1'}">selected</c:if>>Yes</option>
						<option value="2" <c:if test="${volunteer.policeCompleted eq '2'}">selected</c:if>>No</option>
					</select> 								
				</div>
				<div class="col-md-4">
					<label>Police Check Date Received (YYYY-MM-DD)</label>
					<input type="text" id="pCheckDate" name="pCheckDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.policeCheckDate}"> 		
				</div>
				<div class="col-md-4">
					<label>TB Test Completed</label>
					<select name="tbCompleted" id="tbCompleted" form="modify_volunteer" class="form-control">
						<option value="1" <c:if test="${volunteer.tbCompleted eq '1'}">selected</c:if>>Yes</option>
						<option value="2" <c:if test="${volunteer.tbCompleted eq '2'}">selected</c:if>>No</option>
					</select>								
				</div>		
				<div class="col-md-4">
					<label>TB Test Received/Date (YYYY-MM-DD)</label>
					<input type="text" id="tbTDate" name="tbTDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.tBTestDate}"/>									
				</div>
				<div class="col-md-4">
					<label>Application Completed</label>
					<select name="applicationCompleted" id="applicationCompleted" form="modify_volunteer" class="form-control">
						<option value="1" <c:if test="${volunteer.applicationCompleted eq '1'}">selected</c:if>>Yes</option>
						<option value="2" <c:if test="${volunteer.applicationCompleted eq '2'}">selected</c:if>>No</option>
					</select>									
				</div>
				<div class="col-md-4">
					<label>Application Form Completion Date (YYYY-MM-DD)</label>		
					<input id="aCompleteDate" name="aCompleteDate" class="datepickera form-control" data-format="yyyy-MM-dd" value="${volunteer.applicationFormCompletionDate}"> 										
				</div>								
			</div>	

			<div class="row form-group">
				<div class="col-md-12">
					<label>Background Information</label>
					<input type="textarea" class="form-control" maxlength="300" name="background" value="${volunteer.background}"/>
				</div>
				<div class="col-md-12">
					<label>Volunteer Experience</label>
					<input type="textarea" class="form-control" maxlength="300" name="volExperience" value="${volunteer.volExperience}"/>
				</div>	
				<div class="col-md-4">
					<label>Comfortable with Technology</label>
					<select name="techComfort" id="techComfort" form="modify_volunteer" class="form-control">
						<option value="1" <c:if test="${volunteer.techComfort eq '1'}">selected</c:if>>Yes</option>
						<option value="2" <c:if test="${volunteer.techComfort eq '2'}">selected</c:if>>No</option>
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
				<select name="currentStatus" id="currentStatus" form="modify_volunteer" class="form-control">
					<option value="1" <c:if test="${volunteer.currentStatus eq '1'}">selected</c:if>>Active</option>
					<option value="2" <c:if test="${volunteer.currentStatus eq '2'}">selected</c:if>>Inactive</option>
					<option value="3" <c:if test="${volunteer.currentStatus eq '3'}">selected</c:if>>No Response</option>
					<option value="4" <c:if test="${volunteer.currentStatus eq '4'}">selected</c:if>>Not Accepted</option>
				</select>									
			</div>	
			<div class="col-md-4">
				<label>Reason for Inactivity</label>
				<input type="textarea" class="form-control" maxlength="300" name="reasonInactivity" value="${volunteer.reasonInactivity}"/>								
			</div>								
		</div>
      </div>
    </div>
  </div>
</div>
ADDING COLLAPSIBLE END



						<h2>User Account </h2>

								<div class="row form-group">

								<div class="col-md-4">
									<div class="input-group input-group-lg">
										<span class="input-group-addon">Username</span>
								 		<input name="username" type="text" class="form-control" value="${volunteer.userName}" readonly="readonly">
									</div>
								</div>
								<div class="col-md-4">
									<label>Password</label>
									<p>Go to Other > Manage Users to change volunteer password. </p>
								</div>
								<!-- 
								<div class="col-md-4">
									<div class="input-group input-group-lg">
								  		<span class="input-group-addon">Password</span>
								  		<input type="password" name="password" class="form-control" value="${volunteer.password}">
									</div>
								</div>
										 -->													
								
							</div>
						<h2>Availability </h2>
						<c:set var="availability" value="${volunteer.availability}"/>						
						<%@include file="edit_availabilities.jsp" %>

					<h2> Comments </h2>
					<div class="col-md-10">		
						<input type="textarea" class="form-control" maxlength="50" name="notes" value="${volunteer.notes}"/>
					</div>				

				<!--  	<a href="<c:url value="/view_volunteers"/>" class="btn btn-primary" data-toggle="modal">Cancel</a> -->	
						<input type="button" value="Cancel" class="btn btn-primary" onclick="javascript:history.go(-1)">	
						<input class="btn btn-primary" type="submit" value="Save Change" />
						<input id="volunteerId" name="volunteerId" type="hidden" value="${volunteer.volunteerId}"/>
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

$(window).bind("load", function() {
   calctotalscore();
});
</script>	
	</body>

</html>