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
									<label>Experience:</label>
									<select class="form-control" name="level" form="add_volunteer">
										<option value="E" selected>Experienced</option>
										<option value="I" >Intermediate</option>
										<option value="B" >Beginner</option>
									</select>	
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

								<script type="text/javascript">
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
							</div>
							<div class="row form-group">
								<div class="col-md-4">
									<label>VLC ID</label>
									<input type="text" id="vlcId" name="vlcId" class="form-control" value="0"required/>									
								</div>	
								<div class="col-md-4">
									<label>Total Calculated Score</label>
									<input id="totalcalculated" class="form-control" disabled> </span>								
								</div>	
							</div>
							<div class="row form-group">
								<div class="col-md-4">
									<label>Site:</label>									
									<select name="site" form="add_volunteer" class="form-control">
										<c:forEach items="${sites}" var="s">
											<option value="${s.siteId}">${s.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="col-md-4">
									<label>Organization:</label>									
									<select name="organization" form="add_volunteer" class="form-control">
										<c:forEach items="${organizations}" var="o">
											<option value="${o.organizationId}">${o.name}</option>
										</c:forEach>
									</select>
								</div>
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
	
	</body>
</html>