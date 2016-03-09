<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Add Organization</title>
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
		
		
	</head>
	
<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<h4><a href="<c:url value="/view_organizations"/>">Organization</a> > New Organization</h4>
		<div class="row-fluid">		
			<h2>Orgnaization </h2>			
				<form id="add_organization" action="<c:url value="/add_organization"/>" method="POST">			
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Name:</label>
								<input type="text" name="name" class="form-control" required/>
							</div>
							<div class="col-md-4">
							</div>
							<div class="col-md-4">
							</div>
						</div>	
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Primary Contact:</label>
								<input type="text" name="primaryContact" class="form-control" required/>
							</div>
							<div class="col-md-4">
								<label>Secondary Contact:</label>
								<input type="text" name="secondaryContact" class="form-control" />
							</div>
							<div class="col-md-4">
								
							</div>
						</div>	
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Phone Number:</label>
								<input type="text" name="primaryPhone" class="form-control" required/>
							</div>
							<div class="col-md-4">
								<label>Secondary #:</label>
								<input type="text" name="secondaryPhone" class="form-control" />
							</div>							
							<div class="col-md-4">
							</div>
						</div>	
						<h3>Address</h3>
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Street:</label>
								<input type="text" name="streetName" class="form-control" required/>
							</div>
							<div class="col-md-4">
								<label>Street #:</label>
								<input type="text" name="streetNumber" class="form-control" required/>
							</div>
							<div class="col-md-4">
								<label>City :</label>
								<input type="text" name="city" class="form-control" required/>
							</div>
						</div>	
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Province:</label>
								<select name="province" form="add_organization" class="form-control">
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
								<label>Country:</label>
								<select name="country" form="add_organization" class="form-control">
									<option value="CA" selected>Canada</option>
									<option value="ST">USA</option>
									<option value="CH">China</option>
									<option value="RU">Russia</option>
								</select>
							</div>
							<div class="col-md-4">
								<label>Postal Code:</label>
								<input type="text" name="postCode" class="form-control" required/>
							</div>
						</div>	

						<input type="button" value="Cancel" class="btn btn-primary" align='right' onclick="javascript:history.go(-1)">
						<input class="btn btn-primary" type="submit" align='right' value="Create Organization" />

				</form>			
		</div>
	</div>	
	</body>
</html>