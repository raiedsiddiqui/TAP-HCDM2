<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Modify Organization</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

		<style type="text/css">
			.row-fluid{
				margin:10px;
			}
		</style>
				
	</head>
<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Organization </h2>
			<div class="row-fluid">
				<form id="modify_organization" action="<c:url value="/update_organization/${o.organizationId}"/>" method="POST">
					<div class="row form-group">					
							<div class="col-md-4">
								<label>Name:</label>
								<input type="text" name="name" class="form-control" value="${o.name}" required/>
							</div>
							<div class="col-md-4">
							</div>
							<div class="col-md-4">
							</div>
						</div>	
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Primary Contact:</label>
								<input type="text" name="primaryContact" class="form-control" value="${o.primaryContact}" required/>
							</div>
							<div class="col-md-4">
								<label>Secondary Contact:</label>
								<input type="text" name="secondaryContact" class="form-control" value="${o.secondaryContact}"/>
							</div>
							<div class="col-md-4">
								
							</div>
						</div>	
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Phone Number:</label>
								<input type="text" name="primaryPhone" class="form-control" value="${o.primaryPhone}" required/>
							</div>
							<div class="col-md-4">
								<label>Secondary #:</label>
								<input type="text" name="secondaryPhone" class="form-control" value="${o.secondaryPhone}" />
							</div>							
							<div class="col-md-4">
							</div>
						</div>	
						<h3>Address</h3>
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Street:</label>
								<input type="text" name="streetName" class="form-control" value="${o.streetName}" required/>
							</div>
							<div class="col-md-4">
								<label>Street #:</label>
								<input type="text" name="streetNumber" class="form-control" value="${o.streetNumber}" required/>
							</div>
							<div class="col-md-4">
								<label>City :</label>
								<input type="text" name="city" class="form-control" value="${o.city}" required/>
							</div>
						</div>	
						<div class="row form-group">					
							<div class="col-md-4">
								<label>Province:</label>
								<select name="province" form="modify_organization" class="form-control">								
									<option value="AB" <c:if test="${o.province eq 'AB'}">selected</c:if>>Alberta</option>
									<option value="BC" <c:if test="${o.province eq 'BC'}">selected</c:if>>British Colunmbia</option>							
									<option value="MB" <c:if test="${o.province eq 'MB'}">selected</c:if>>Manitoba</option>
									<option value="NB" <c:if test="${o.province eq 'NB'}">selected</c:if>>New Brunswik</option>
									<option value="NL" <c:if test="${o.province eq 'NL'}">selected</c:if>>Newfoundland and Labrador</option>
									<option value="NS" <c:if test="${o.province eq 'NS'}">selected</c:if>>Nova Scotia</option>							
									<option value="ON" <c:if test="${o.province eq 'ON'}">selected</c:if>>Ontario</option>
									<option value="PE" <c:if test="${o.province eq 'PE'}">selected</c:if>>PrinceEdword Island</option>
									<option value="QC" <c:if test="${o.province eq 'QC'}">selected</c:if>>Quebec</option>
									<option value="SK" <c:if test="${o.province eq 'SK'}">selected</c:if>>Saskatchewan</option>							
									<option value="NT" <c:if test="${o.province eq 'NT'}">selected</c:if>>Northwest Terriotories</option>
									<option value="NU" <c:if test="${o.province eq 'NU'}">selected</c:if>>Nunavut</option>
									<option value="YT" <c:if test="${o.province eq 'YT'}">selected</c:if> >Yukon</option>											
								</select>
							</div>
							<div class="col-md-4">
								<label>Country:</label>
								<select name="country" form="modify_organization" class="form-control">
									<option value="CA" <c:if test="${o.country eq 'CA'}">selected</c:if>>Canada</option>
									<option value="ST" <c:if test="${o.country eq 'ST'}">selected</c:if>>USA</option>
									<option value="CH" <c:if test="${o.country eq 'CH'}">selected</c:if>>China</option>
									<option value="RU" <c:if test="${o.country eq 'RU'}">selected</c:if>>Russia</option>
								</select>
							</div>
							<div class="col-md-4">
								<label>Postal Code:</label>
								<input type="text" name="postCode" class="form-control" value="${o.postCode}" required/>
							</div>
						</div>	

						<input type="button" value="Cancel" class="btn btn-primary" align='right' onclick="javascript:history.go(-1)">
						<input class="btn btn-primary" type="submit" align='right' value="Save Changes" />
				</form>
			</div>
		</div>
	
	</div>

</body>
</html>