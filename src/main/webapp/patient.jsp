<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
	<!--
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/tapestryUtils.js"></script>	-->
	<title>Patient Surveys</title>
	<%@include file="volunteer/volunteer_head.jsp" %>

	<style type="text/css">
		html,body{
			height:100%;
		}
		
	.pagetitle {
		margin-left: -30px;
		float:left;
		width:auto;
		
	}

	.visitcompletebox {
		padding-top: 20px;
		float:right;
	}
	.panel-heading {
		padding: 0;
	}
	#elderabuse, #crisislines, #selfharm {
		color:red;
	}

	#SOSbtn {
		float:left;
		background-color:#B33A3A;
		margin-top:14px;
		margin-left:10px;
	}

	#alertsbox {
		float:left;
		background-color:#B33A3A;
		margin-top:14px;
		margin-left:10px;
		padding:9px 11px 9px 11px;
	}
	</style>
	<script type="text/javascript">
		function confirmAuthenticatPHR()
		{
			var x = confirm("Have you verified the client with a photo ID?");
			if (x)
				return true;
			else
				return false;
		}
		
		function confirmSOS()
		{
			var x = confirm("SOS - Message Alert sent to Volunteer Coordinator. Please call your Volunteer Coordinator to follow up.");
			if (x)
				return true;
			else
				return false;
		}
		
		function goalsPrompt()
		{
			var x = confirm("The Goals Survey should be completed last. Ensure there are no remaining surveys before proceeding.Tap [OK] to open the Goals survey. Tap [Cancel] to go back and complete another survey.");
			if (x)
				return true;
			else
				return false;
		}
		
		//Clear Local Storage - For Goals
		$(document).ready(function(){
			localStorage.clear();
		});
		
	</script>
	
</head>
	
<body>
	<%@include file="volunteer/subNavi.jsp" %>
	<div id="displayAuthenticationDiv" class ="alert alert-info" style="display:none">
		<spring:message code="message_authenticatePHR"/>
	</div>
	<c:if test="${not empty showAuthenticationMsg}">
			<div class ="alert alert-info"><spring:message code="message_authenticatePHR"/></div>
	</c:if>	
	<c:if test="${not empty goalsMsg}">		
			<div class ="alert alert-info"><spring:message code="message_goalsMsg"/></div>
	</c:if>
	
	<div class="container">
	<div id="myTest" ></div>
	<div class="row">
		<div class="col-md-7">
			<c:if test="${not empty patient}">
				<c:choose>
					<c:when test="${not empty patient.preferredName}">
						<h4 class="pagetitle">${patient.preferredName} <span class="pagedesc">${appointment.date}</span></h4>
					</c:when>
					<c:otherwise>
						<h4 class="pagetitle">${patient.displayName} <span class="pagedesc">${appointment.date}</span></h4>
					</c:otherwise>
				</c:choose>
			</c:if>
			<a id="SOSbtn" href="#modalSOS" role="button" class="lgbtn" data-toggle="modal">SOS</a>

			<c:if test="${not empty patient.notes}">
				<a id="alertsbox" href="#modalNotes" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal"><i class="icon-info-sign icon-white"></i></a>
			</c:if>

			
		</div>
		<div class="col-md-5">
			

			<div class="visitcompletebox">
<!-- 				<a href="<c:url value="/goMyOscarAuthenticate/${appointment.appointmentID}"/>" role="button" class="lgbtn">Authenticate PHR</a>-->	
					<c:if test="${patient.myoscarVerified == 0}">
			  			<a href="<c:url value="/authenticatePHR"/>" Onclick="return confirmAuthenticatPHR()" role="button" class="lgbtn">Authenticate PHR</a>		
					
					</c:if>
					<c:if test="${not empty appointment}">
						<a href="<c:url value="/visit_complete/${appointment.appointmentID}"/>" role="button" class="lgbtn">Visit Complete</a>
					</c:if>
			</div>
		</div>
	</div>
			<!-- <a href="#bookAppointment" role="button" class="btn btn-primary pull-right lgbtn" data-toggle="modal">Book appointment</a> -->


		<div class="row-fluid">
<!-- 				<div class="span3">
				<c:choose>
					<c:when test="${not empty patient.preferredName}">
						<h2>${patient.preferredName} (${patient.gender})</h2>
					</c:when>
					<c:otherwise>
						<h2>${patient.displayName} (${patient.gender})</h2>
					</c:otherwise>
				</c:choose>
			</div> -->
			<!-- <div id="visitandbook" class="span12 btn-group">
				<c:if test="${not empty patient.notes}">
					<a href="#modalNotes" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal"><i class="icon-info-sign icon-white"></i></a>
				</c:if>
				<c:if test="${not empty appointment}">
					<a href="<c:url value="/visit_complete/${appointment.appointmentID}"/>" role="button" class="btn btn-primary pull-right lgbtn">Visit Complete</a>
				</c:if>
				<a href="#bookAppointment" role="button" class="btn btn-primary pull-right lgbtn" data-toggle="modal">Book appointment</a>
			</div> -->
		</div>
			<c:if test="${not empty completed}">
				<p class="alert alert-success">Completed survey: ${completed}</p>
			</c:if>
			<c:if test="${not empty inProgress}">
				<p class="alert alert-warning">Exited survey: ${inProgress}</p>
			</c:if>
			       
			<!-- <p class="pageheader">Active Surveys</p> -->


			        			      <!-- </a>
			    </div>
			    <div id="collapseOne" class="accordion-body collapse">
			      <div class="accordion-inner"> -->
	      	<div class="row">
	        <c:forEach items="${inProgressSurveys}" var="ips">
	        		<div class="col-xs-12 col-sm-6 col-md-6 col-lg-4">
	        		<c:choose>
					      <c:when test="${ips.surveyTitle=='Goals'}">
					      	<a href="<c:url value="/open_survey/${ips.resultID}"/>" Onclick="return goalsPrompt()" class="surveybtn btn">
								${ips.surveyTitle}<br/>
								<span class="surveydesc">${ips.description}</span>
							</a>	
					      </c:when>
							
					      <c:otherwise>					      
					      	<a href="<c:url value="/open_survey/${ips.resultID}"/>" class="surveybtn btn">
								${ips.surveyTitle}<br/>
								<span class="surveydesc">${ips.description}</span>
							</a>
					      </c:otherwise>
					</c:choose>
				<!--  	<a href="<c:url value="/open_survey/${ips.resultID}"/>" class="surveybtn btn">
						${ips.surveyTitle}<br/>
						<span class="surveydesc">${ips.description}</span>-->
					</a>
					</div>
			</c:forEach>
			</div>
<!-- 			      </div>
			    </div>
			  </div> -->
			  
<!-- 			  <div class="accordion-group">
			    <div class="accordion-heading"> -->
<!-- 			      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordionSurveys" href="#collapseTwo">
 -->			
 				
<h4 class="pagetitle" style="float:none">Completed Surveys <span class="pagedesc"></span></h4>

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
	<c:forEach items="${completedSurveys}" var="csr">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingOne">
      			<h4 class="panel-title">
      				<a class="surveybtnc btn accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#collapse${csr.surveyID}">
      					${csr.surveyTitle}
      				</a>
      			</h4>
      		</div>

			<div id="collapse${csr.surveyID}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
				<div class="panel-body">
					<table class="table">
						<tr>
							<th>Question</th>
							<th>Answer</th>																	
						</tr>	
						<c:forEach items="${displayResults}" var="dr">	
							<c:if test="${dr.surveyId == csr.surveyID}">														      			
			      				<tr>										      			
					      			<td width="50%" height="40">${dr.questionText }</td>					      					
					      			<td width="50%">${dr.questionAnswer}</td>
								</tr>
							</c:if>
						</c:forEach>
					</table>
				</div>
			</div>

		</div>
	</c:forEach>
</div>


<!-- 			      
				</a>
			    </div> -->
<!-- 			    <div id="collapseTwo" class="accordion-body collapse">
			    <div class="row">  
 						<c:forEach items="${completedSurveys}" var="cs">
							<div class="col-xs-12 col-sm-6 col-md-6 col-lg-4">
								<a href="#" class="surveybtnc btn">${cs.surveyTitle}<br/>
									<span class="surveycomp">${cs.description}</span>
								</a><br/>
								
							<span class="surveycomp">complete</span> 
					 		${cs.description} 
							</div>
 						</c:forEach>
 					</div> -->
<!-- 			    </div>
			  </div>
 -->			  
			  <!-- <div class="accordion-group">
			    <div class="accordion-heading">
			      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordionSurveys" href="#collapseThree">
			        <div class="sheading">Assign Survey</div>
			      </a>
			    </div>
			    <div id="collapseThree" class="accordion-body collapse">
			      <c:forEach items="${surveys}" var="s">
					<div class="row-fluid">
						<a href="<c:url value="/assign_survey/${s.surveyID}/${patient.patientID}"/>" class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
							<b>${s.title}</b><br/>
							${s.description}
						</a>
					</div>
				  </c:forEach>
			    </div>
			  </div> -->
			
			<!--
			<div class="sheading">Incomplete Surveys</div>
			<c:forEach items="${inProgressSurveys}" var="ips">
				<div class="row-fluid">
					<a href="<c:url value="/open_survey/${ips.resultID}"/>" class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
						<b>${ips.surveyTitle}</b><br/>
						${ips.description}
					</a>
				</div>
			</c:forEach>
			
			<div class="sheading">Completed Surveys</div>
			<c:forEach items="${completedSurveys}" var="cs">
				<div class="row-fluid">
					<div class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
						<b>${cs.surveyTitle}</b><br/>
						${cs.description}
					</div>
				</div>
			</c:forEach>
			
			<div class="sheading">Assign Survey</div>
			<c:forEach items="${surveys}" var="s">
				<div class="row-fluid">
					<a href="<c:url value="/assign_survey/${s.surveyID}/${patient.patientID}"/>" class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
						<b>${s.title}</b><br/>
						${s.description}
					</a>
				</div>
			</c:forEach>
			-->
			

		<!--
		<div class="span8">
			<h2>Pictures</h2>
			<form id="uploadPic" action="<c:url value="/upload_picture_for_patient/${patient.patientID}" />" method="POST" enctype="multipart/form-data">
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
		-->

<div class="modal fade" id="modalNotes" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">
			<c:choose>
				<c:when test="${not empty patient.preferredName}">
					${patient.preferredName}
				</c:when>
				<c:otherwise>
					${patient.displayName}
				</c:otherwise>
			</c:choose>
        </h4>
      </div>
      <div class="modal-body">
        <p class="text-warning">${patient.notes}</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>


<div class="modal fade" id="modalSOS" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">
        	SOS
        </h4>
      </div>
      <div class="modal-body">
    	<a class="lgbtn" href="<c:url value="/call_coordinator_SOS/${patient.displayName}?type=1"/>" Onclick="return confirmSOS()">Elder Abuse</a>
    	<a class="lgbtn" href="<c:url value="/call_coordinator_SOS/${patient.displayName}?type=2"/>" Onclick="return confirmSOS()">Self Harm</a>
    	<a class="lgbtn" href="<c:url value="/call_coordinator_SOS/${patient.displayName}?type=3"/>" Onclick="return confirmSOS()">Crisis Lines</a>


      <h3 id="elderabuse">If You Suspect Elder Abuse</h3>
      <h4>CALL the Volunteer Coordinator immediately.</h4>

		<h4>WARNING SIGNS:</h4>
		<ul>
			<li>Frequent arguments or tension between the caregiver and the elderly person</li>
			<li>Changes in personality or behaviour in the elder</li>
		</ul>
		<h4>Elder abuse may include</h4>
		<ul>
			<li>Physical abuse</li>
			<li>Emotional or psychological abuse (e.g. intimidation, humiliation, isolating from supports)</li>
			<li>Sexual abuse</li>
			<li>Neglect or abandonment by a caretaker</li>
			<li>Financial exploitation (e.g. misusing an elder's checks, credit  cards, or accounts; forging an elder's signature; stealing) </li>
		</ul> 
		<hr>

		<h3 id="selfharm">If You Suspect a Client is At Risk of Harming Themselves </h3>

			<h4>CALL the Volunteer Coordinator immediately.</h4>

			<p>IF you ever feel unsafe in a situation, LEAVE immediately.</p>
				<ul>
					<li>Stay calm,</li> 
					<li>Be clear in your language, </li>
					<li>Don't use judgmental phrases or tones,</li> 
					<li>Give the client personal space.</li> 
				</ul>
			

				<p>If a client expresses:</p>
					<ul>
						<li>Some sort of suicidal thoughts or ideation</li>
						<li>A plan for harming themselves</li>
					</ul>


				<p>THEN, </p>

				<p>Let the client know that you will be mentioning what they said (or implied) to the Volunteer Coordinator and their clinic.</p>

				<p>If you feel comfortable, you can 'contract against self-harm' by asking a question like 
				"Will you agree not to hurt yourself until you are contacted by your clinic?" </p> 

				<p>If a client says that they will not be safe after you leave, You can ask if they want to call their clinic, a crisis line, or 911 while you are present </p>

			<hr>
			<h3 id="crisislines">Local 24-Hour Crisis Lines</h3>
			<ul>
			<li>Crisis Outreach and Response Team (COAST), St Joseph's Healthcare - 905-972-8338 - also has a mobile team that can respond to calls from 8am-1am daily</li>
			<li>Good Shepherd Barrett Centre for Crisis Support - 905-529-7878 - also has a 10-bed centre for brief stays for individuals in mental health crisis</li>
			<li>Sexual Assault Centre Hamilton &amp; Area (SACHA) - 905-525-4162 - sexual assault specific, also has other counselling and advocacy services</li>
			</ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>


		<!-- Modal 
<div class="modal fade" id="bookAppointment" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Book Appointment</h4>
      </div>
      <div class="modal-body">
        
        <form id="appt-form" method="post" action="<c:url value="/book_appointment"/>">
  				<label>With patient:</label>
				<select name="patient" form="appt-form">
					<c:forEach items="${patients}" var="p">
					<option value="${p.patientID}">${p.displayName}</option>
					</c:forEach>
				</select><br />
				<label>Date:</label>		
				<div id="dp" class="input-append">-->
<!--  					<input data-format="yyyy-MM-dd" type="text" name="appointmentDate">-->
  					<!--<input class="datepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentDate">

					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
				<label>Time:</label>
				<div id="tp" class="input-append">-->
<!--  					<input data-format="hh:mm:00" type="text" name="appointmentTime">--> 
 					<!--<input data-format="HH:i:00" class="timepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentTime">

				    <span class="add-on">
				    	<i class="icon-time"></i>
				    </span>
				</div>
  			</form>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book" form="appt-form" class="btn btn-primary">Book</button>
      </div>
    </div>
  </div>
</div>-->
<!--

	<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script>window.jQuery||document.write('<script src="tests/jquery.2.0.0.js"><\/script>')</script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.date.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.time.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/legacy.js"></script>


	<script type="text/javascript">
		$(function(){
			// $('#tp').datetimepicker({
			// 	pickDate: false,
			// 	pickSeconds: false
			// });
			// $('#dp').datetimepicker({
			// 	pickTime: false,
			// 	startDate: new Date()
  	// 		});
  			
  			$('#bookAppt').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });


		});

		    $('.datepicker').pickadate({
		    // Escape any “rule” characters with an exclamation mark (!).
		    format: 'You selecte!d: dddd, dd mmm, yyyy',
		    formatSubmit: 'yyyy/mm/dd',
		    hiddenName: true
		   	// hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
			})
		

		$('.timepicker').pickatime({
		    // Escape any “rule” characters with an exclamation mark (!).
		    formatSubmit: 'HH:i:00',
		   	hiddenName: true

		    // hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
		})
		
	</script> -->
  
</div>
</body>
</html>
