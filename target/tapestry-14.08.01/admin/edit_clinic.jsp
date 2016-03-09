<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
	<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
<title>Modify Clinic</title>
</head>
<body>
<div class="content">
		<%@include file="navbar.jsp" %>
		<h4><a href="<c:url value="/manage_clinics"/>">Clinic</a> > Modify Clinic</h4>
		<div class="row-fluid">		
			<h2>Clinic </h2>			
				<form id="edit_clinic" action="<c:url value="/modify_clinic/${clinic.clinicId}"/>" method="POST">			
						<div class="row form-group">					
							<div class="col-md-6">
								<label>Name:</label>
								<input type="text" name="name" value="${clinic.clinicName }" class="form-control" required/>
							</div>
							<div class="col-md-6">
								<label>Address:</label>
								<input type="text" name="address" value="${clinic.address }" class="form-control" />
							</div>
							<div class="col-md-6">
								<label>Phone:</label>
								<input type="text" name="phone" value="${clinic.phone }" class="form-control" />
							</div>
							<div class="col-md-6">
								<c:if test="${not empty sites}">
									<label>Site:</label>
									<select name="site" id="site" form="edit_clinic" class="form-control">
										<option value=""></option>
										<c:forEach items="${sites}" var="s">
											<option value="${s.siteId}" <c:if test="${s.siteId eq clinic.siteId}">selected</c:if>>${s.name}</option>
										</c:forEach>									
									</select>
								</c:if>	
							</div>
						</div>	

						<input type="button" value="Cancel" class="btn btn-primary" align='right' onclick="javascript:history.go(-1)">
						<input class="btn btn-primary" type="submit" align='right' value="Edit Clinic" />

				</form>			
		</div>
	</div>	
</body>
</html>