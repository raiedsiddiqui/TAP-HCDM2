<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
	<%@include file="volunteer_head.jsp" %>
	<style type="text/css">
		html,body{
			height:100%;
		}

		label {
			font-size: 25px;
			font-family: 'Open Sans', sans-serif;
			font-weight: 300;
		}

		h3 {
			font-family: 'Open Sans', sans-serif;

		}

		.form-control {
			height: 60px;
		}
	</style>
	
	<script type="">
		function checkPass()
		{
		    //Store the password field objects into variables ...
		    var pass1 = document.getElementById('pass1');
		    var pass2 = document.getElementById('pass2');
		    //Store the Confimation Message Object ...
		    var message = document.getElementById('confirmMessage');
		    //Set the colors we will be using ...
		    var goodColor = "#66cc66";
		    var badColor = "#ff6666";
		    //Compare the values in the password field 
		    //and the confirmation field
		    if(pass1.value == pass2.value){
		        //The passwords match. 
		        //Set the color to the good color and inform
		        //the user that they have entered the correct password 
		        pass2.style.backgroundColor = goodColor;
		        pass1.style.backgroundColor = goodColor;
		        message.style.color = goodColor;
		        message.innerHTML = "Passwords Match!"
		        document.getElementById("changePasswordButton").disabled = false; 
		        document.getElementById("changePasswordButton").style.backgroundColor="#4A307A";
		    }else{
		        //The passwords do not match.
		        //Set the color to the bad color and
		        //notify the user.
		        pass2.style.backgroundColor = badColor;
		        pass1.style.backgroundColor = badColor;
		        message.style.color = badColor;
		        message.innerHTML = "Passwords Do Not Match!";
		        document.getElementById("changePasswordButton").disabled = true; 
		        document.getElementById("changePasswordButton").style.backgroundColor="gray";
		    }
		}  
	</script>
	
</head>
	
<body>
<div id="headerholder">	
 <%@include file="subNavi.jsp" %>
</div>
<div class="content">
	<div class="row">
		<div class="col-md-4" style="padding-top: 20px;">
			<a class="lgbtn" href="<c:url value="/view_mySurveys"/>">MySurveys</a>
		</div>
	</div>
	<h3 class="pagetitle">Password Change <span class="pagedesc"> Enter your old passowrd then enter your new password twice </span> </h3>
	<c:if test="${not empty errors}">
		<c:choose>
			<c:when test="${errors == 'confirm'}">
				<div class="alert alert-error"><spring:message code="message_passwordNoMatch"/></div>
			</c:when>
			<c:when test="${errors == 'current'}">
				<div class="alert alert-error"><spring:message code="message_passwordIncorrect"/></div>
			</c:when>			
		</c:choose>
	</c:if>
	<c:if test="${not empty success}">
		<div class ="alert alert-info"><spring:message code="message_passwordChangedSuccessfully"/></div>
	</c:if>						
	<c:if test="${not empty availability }">
		<div class ="alert alert-info"><spring:message code="message_availabilityChangedSuccessfully"/></div>
	</c:if>
	<div class="tab-content">
		<div class="row">
			<div class="col-md-12">				
				<form id="changePassword" action="<c:url value="/change_password/${loggedInUserId}"/>" method="POST" >
						<fieldset>

							<div class="row">
								<div class="col-md-10">								
		  							<label>Current password:</label> 
		  							<br>
									<input type="password" name="currentPassword" class="form-control" required />
								</div>
							</div>

							<div class="row">
								<div class="col-md-10">
									<label>New password:</label>
									<br>
									<input id="pass1" type="password" name="newPassword" class="form-control" required />
								</div>
							</div>

							<div class="row">
								<div class="col-md-10">
									<label>Confirm password:</label>
									<br>
									<input id="pass2" type="password" name="confirmPassword" onkeyup="checkPass(); return false;" class="form-control" required />
									<span id="confirmMessage" class="confirmMessage"></span>
								</div>

							</div>
							<br />
							
						</fieldset>
	  				</form>		
	  				<button class="btn btn-info" onClick="history.back()" class="pull-left">Cancel</button>
	  				<button id="changePasswordButton" data-loading-text="Loading..." type="submit"  form="changePassword" class="pull-right lgbtn">Change Password</button>			
			</div>
		</div>
		
		<div id= "vAvailability">		
			<form id="updateAvailability" action="<c:url value="/updateVolunteerAvailability/${volunteer.volunteerId}"/>" method="POST" >
				<h2>Availability </h2>
				<c:set var="availability" value="${volunteer.availability}"/>						
				<%@include file="../admin/edit_availabilities.jsp" %>
			</form>
			<button class="btn btn-info" onClick="history.back()" class="pull-left">Cancel</button>
	  		<button id="updateAvailability" data-loading-text="Loading..." type="submit"  form="updateAvailability" class="pull-right lgbtn">Update Availability</button>			
		</div>
	</div>
</div>







<!--<div id="headerholder">	
  <img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
     <img id="logofam" src="<c:url value="/resources/images/fammed.png"/>" /> 
	<img id="logofhs" src="<c:url value="/resources/images/fhs.png"/>" />
        <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>
  		<div class="navbar">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					<div class="nav-collapse collapse">
						<ul class="nav">
     					<li><a class="brand" href="<c:url value="/"/>">Appointments</a></li>
     					
							<li class="dropdown">
 								<a href="#" class="dropdown-toggle" data-toggle="dropdown">Appointments<b class="caret"></b></a>
								<ul class="dropdown-menu">
								<c:forEach items="${patients}" var="p">
									<c:choose>
										<c:when test="${not empty p.preferredName}">
											<li><a href="<c:url value="/patient/${p.patientID}"/>">${p.preferredName}</a></li>
										</c:when>
										<c:otherwise>
											<li><a href="<c:url value="/patient/${p.patientID}"/>">${p.displayName}</a></li>
										</c:otherwise>
									</c:choose>
								</c:forEach>
								</ul>
							</li>
							<li class="active"><a href="<c:url value="/profile"/>">My Profile</a></li>
							<li><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
							<li><a href="<c:url value="/logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
	</div>
 	breadcrumb START
	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>"><img src="${pageContext.request.contextPath}/resources/images/home.png" height="20" width="20" />My Clients</a> </li>
		</ul>	
	</div>
	breadcrumb END
	<div class="content">
	</div>
		
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span4">
					<h2>${vol.name}'s Profile</h2>	
					<form id="volunteer-info" action="<c:url value="/update_user" />" method="POST">
						<fieldset>
							<legend>Edit details</legend>
							<label>Name</label>
							<input type="text" name="volName" value="${vol.name}" required />
							<label>Username</label>
							<input type="text" name="volUsername" value="${vol.username}" required />
							<label>Email</label>
							<input type="email" name="volEmail" value="${vol.email}" required />
							<br />
							<input type="submit" class="btn btn-primary" value="Save changes" />
						</fieldset>
					</form>
<!-- 					<form id="change-password" method="post" action="<c:url value="/change_password/${vol.userID}"/>">
						<fieldset>
							<c:if test="${not empty errors}">
							<c:choose>
								<c:when test="${errors == 'confirm'}">
								<div class="alert alert-error">Passwords do not match</div>
								</c:when>
								<c:when test="${errors == 'current'}">
								<div class="alert alert-error">Password is incorrect</div>
								</c:when>
							</c:choose>
							</c:if>
							<c:if test="${not empty success}">
								<div class="alert alert-info">Password successfully changed</div>
							</c:if>
							<legend>Change password</legend>
	  						<label>Current password:</label>
							<input type="password" name="currentPassword" required />
							<label>New password:</label>
							<input type="password" name="newPassword" required />
							<label>Confirm password:</label>
							<input type="password" name="confirmPassword" required />
							<br />
							<input type="submit" class="btn btn-primary" value="Save changes" />
						</fieldset>
	  				</form> 
				</div>
				
 				<div class="span8">
					<h2>Pictures</h2>
					<form id="uploadPic" action="<c:url value="upload_picture_to_profile" />" method="POST" enctype="multipart/form-data">
						<label>Upload picture</label>
  						<input form="uploadPic" type="file" name="pic" accept="image/*" required /><br/>
  						<input form="uploadPic" type="submit" value="Upload" />
					</form>
					<c:choose>
					<c:when test="${not empty pictures}">
					<ul class="thumbnails">
						<c:forEach items="${pictures}" var="pic">
					 	<li>
    						<a href="#${fn:replace(pic.path, ".", "-")}" data-toggle="lightbox">
      							<img class="thumbnail" src="<c:url value="/uploads/${pic.path}"/>"/>
    						</a>
    						<a href="<c:url value="/remove_picture/${pic.pictureID}"/>" class="btn btn-danger" style="width:92%;">Remove</a>
    						<div id="${fn:replace(pic.path, ".", "-")}" class="lightbox hide fade" role="dialog" aria-hidden="true" tab-index="-1">
    							<div class="lightbox-content">
    								<img src="<c:url value="/uploads/${pic.path}"/>">
    							</div>
    						</div>
  						</li>
						</c:forEach>
					</ul>
					</c:when>
					<c:otherwise>
					<p>No pictures uploaded</p>
					</c:otherwise>
					</c:choose>
				</div> 
			</div>
		</div>-->


</body>
</html>
