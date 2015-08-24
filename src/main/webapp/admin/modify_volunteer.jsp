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
									<label>First Name:</label>
									<input type="text" name="firstname" class="form-control" value="${volunteer.firstName}" required/>
								</div>
								<div class="col-md-4">
									<label>Last Name:</label>
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
									<label>Experience:</label>
									<select class="form-control" name="level" form="modify_volunteer">
										<option value='E' <c:if test="${volunteer.experienceLevel eq 'Experienced'}">selected</c:if>>Experienced</option>
										<option value='I' <c:if test="${volunteer.experienceLevel eq 'Intermediate'}">selected</c:if>>Intermediate</option>
										<option value='B' <c:if test="${volunteer.experienceLevel eq 'Beginer'}">selected</c:if> >Beginner</option>
									</select>	
								</div>
								<div class="col-md-4">
									<label>Total VLC Score(.35):</label>
									<input type="text" id="totalVLCScore" name="totalVLCScore" class="form-control" value="${volunteer.totalVLCScore}" onchange="checkNumericInput(this.id)" required/>									
								</div>
								<div class="col-md-4">
									<label>Number years of experience(.1):</label>
									<input type="text" id="numberYearsOfExperience" name="numberYearsOfExperience" class="form-control" value="${volunteer.numYearsOfExperience}" onchange="checkNumericInput(this.id)" required/>									
								</div>	
							</div>	
							<div class="row form-group">
								<div class="col-md-4">
									<label>Volunteer availability(hours/month)(.2):</label>
									<input type="text" id="availabilityPerMonthe" name="availabilityPerMonthe" class="form-control" value="${volunteer.availabilityPerMonth}" onchange="checkNumericInput(this.id)" required/>									
								</div>
								<div class="col-md-4">
									<label>Technology skills score(.25):</label>
									<input type="text" id="technologySkillsScore" name="technologySkillsScore" class="form-control" value="${volunteer.technologySkillsScore}" onchange="checkNumericInput(this.id)" required/>									
								</div>
								<div class="col-md-4">
									<label>Perception of older adults score(.2):</label>
									<input type="text" id="perceptionOfOlderAdultScore" name="perceptionOfOlderAdultScore" class="form-control" value="${volunteer.perceptionOfOlderAdultsScore}" onchange="checkNumericInput(this.id)" required/>									
								</div>								
							</div>
							<div class="row form-group">
								<div class="col-md-4">
									<label>Organization:</label>
										<select name="organization" form="modify_volunteer" class="form-control">
											<c:forEach items="${organizations}" var="o">
												<option value="${o.organizationId}" <c:if test="${o.organizationId eq volunteer.organizationId}">selected</c:if>>${o.name}</option>
											</c:forEach>
										</select>
								</div>
								<div class="col-md-4">
									<label>VLC ID:</label>
									<input type="text" id="vlcId" name="vlcId" value="${volunteer.vLCID}" class="form-control" required/>									
								</div>	
								<div class="col-md-4">
									<label>Source - where they learned about Tapestry:</label>
									<input type="text" id="source" name="source" value="${volunteer.source}" class="form-control" > 								
								</div>								
							</div>
							
							<div class="row form-group">
								<div class="col-md-4">
									<label>Interview Date:</label>
									<input type="text" id="interviewDate" name="interviewDate" value="${volunteer.interviewDate}" class="form-control" > 																
								</div>
								<div class="col-md-4">
									<label>Mature or Student:</label>
									<input type="text" id="status" name="status" value="${volunteer.status}" class="form-control" />									
								</div>	
								<div class="col-md-4">
									<label>Date of Birth:</label>
									<input id="dob" name="dob" value="${volunteer.dOB}" class="form-control" > 				
								</div>								
							</div>
							<div class="row form-group">
								<div class="col-md-4">
									<label>Reference 1:</label>
									<input type="text" id="reference1" name="reference1" value="${volunteer.reference1}" class="form-control" > 								
								</div>
								<div class="col-md-4">
									<label>Reference 2:</label>
									<input type="text" id="reference2" name="reference2" value="${volunteer.reference2}" class="form-control" />									
								</div>	
								<div class="col-md-4">
									<label>Reference Check - Date:</label>
									<input id="rDate" name="rDate" value="${volunteer.referenceCheckDate}" class="form-control" > 				
								</div>								
							</div>
							<div class="row form-group">
								<div class="col-md-4">
									<label>Police Check Date Received?</label>
									<input type="text" id="pCheckDate" name="pCheckDate" value="${volunteer.policeCheckDate}" class="form-control" > 								
								</div>
								<div class="col-md-4">
									<label>TB Test Received/Date?</label>
									<input type="text" id="tbTDate" name="tbTDate" value="${volunteer.tBTestDate}" class="form-control" />									
								</div>	
								<div class="col-md-4">
									<label>VLC Completion Date:</label>
									<input id="vCDate" name="vCDate" value="${volunteer.vLCCompletionDate}" class="form-control" > 				
								</div>								
							</div>
							
							<div class="row form-group">
								<div class="col-md-4">
									<label>Confidentiality Agreement Date Signed:</label>
									<input type="text" id="cAgreementDate" name="cAgreementDate" value="${volunteer.cAgreementDate}" class="form-control" > 								
								</div>
								<div class="col-md-4">
									<label>Volunteer Agreement Date Signed:</label>
									<input type="text" id="vAgreementDate" name="vAgreementDate" value="${volunteer.vAgreementDate}" class="form-control" />									
								</div>	
								<div class="col-md-4">
									<label>Photo Date Received:</label>
									<input id="pDate" name="pDate" value="${volunteer.photoDate}" class="form-control" > 				
								</div>								
							</div>
							<div class="row form-group">
								<div class="col-md-4">
									<label>Application Form Completion Date:</label>		
									<input id="aCompleteDate" name="aCompleteDate" value="${volunteer.applicationFormCompletionDate}" class="form-control" > 										
								</div>
							</div>

						<h2>User Account </h2>

								<div class="row form-group">

								<div class="col-md-4">
									<div class="input-group input-group-lg">
										<span class="input-group-addon">Username</span>
								 		<input name="username" type="text" class="form-control" value="${volunteer.userName}">
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
	
	</body>
</html>