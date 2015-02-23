<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

<!-- 		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  
 	
 	<link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />-->

	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		.bootstrap-datetimepicker-widget{
			z-index:9999;
		}

		div.modal-dialog {
			width:90%;
			height:600px;
		}

		.picker--opened .picker__holder {
			background: none;
		}

		.modal-content {
			height: 100%;
		}
	</style>
	

</head>

<body>
		<%@include file="navbar.jsp" %>

	<div class="content">

		<div class="row">		
			<div class="col-md-9">
				<h2>Appointments</h2>
			</div>
			<div class="col-md-3">
				<a href="<c:url value="/go_scheduler"/>" class="btn btn-primary">Scheduler</a>
		<!-- 		<a href="#bookAppointment" class="btn btn-primary" data-toggle="modal">Book Appointment</a>  -->
		<a href="<c:url value="/book_appointment"/>" class="btn btn-primary">Book Appointment</a>
			</div>
		</div>
		
			<c:if test="${not empty success}">
				<div class="alert alert-info">Appointment has been successfully booked</div>
			 </c:if>
			 <c:if test="${not empty noMachedTime }">
			 	<div id = "noMatchInfo" class="alert alert-info">
			 		<c:out value="${noMachedTime}" />
			 	</div>
			 </c:if>
			<!--<div class="panel-group" id="accordian">
			<c:forEach items="${patients}" var="p">
				
				<div class="panel panel-default">
					<div class="panel-heading">
				    	<h4 class="panel-title">
				    		<a class="accordion-toggle" data-toggle="collapse" href="#collapse${p.firstName}${p.lastName}">
				        	${p.displayName}
				      	</a>
				      </h4>
				    </div>
				    <div id="collapse${p.firstName}${p.lastName}" class="panel-collapse collapse">
				    	<div class="accordion-inner">
				    		<div class="accordion-group">
				    			<c:forEach items="${appointments}" var="a">
				    				<c:if test="${p.patientID == a.patientID}">
										<a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger pull-right btn-sm">Delete</a>
										<a href="<c:url value="/decline_appointment/${a.appointmentID}"/>" class="btn btn-warning pull-right btn-sm">Decline</a>
										
										<a href="<c:url value="/approve_appointment/${a.appointmentID}"/>" class="btn btn-primary pull-right btn-sm">Approve</a>
										<div class="accordion-heading">
									    	<a class="accordion-toggle" data-toggle="collapse" href="#collapse${a.appointmentID}">${a.date} ${a.time}   (${a.status})</a>
									    </div>
									    
									    <div id="collapse${a.appointmentID}" class="accordion-body collapse">
				    						<div class="accordion-inner">
				    							<c:if test="${a.completed}">
					    							<c:choose>
					    								<c:when test="${a.contactedAdmin}">
					    									Volunteer contacted Ernie<br /><br />
					    								</c:when>
					    								<c:otherwise>
					    									Volunteer did not contact Ernie<br /><br />
					    								</c:otherwise>
					    							</c:choose>
					    							<c:if test="${not empty a.comments}">
					    								Comments: ${a.comments} <br /><br />
					    							</c:if>
					    							Activites Completed:<br />
					    							<c:forEach items="${activities}" var="act">
					    								<c:if test="${a.appointmentID == act.appointment}">
					    									<h2>${act.time}: ${act.description}</h2> <br />
					    								</c:if>
					    							</c:forEach>
					    						</c:if>
				    						</div>
				    					</div>
				    				</c:if>
				    			</c:forEach>
				    		</div>
				    	</div>
				    </div>
				</div>
			</c:forEach> -->
			<br />
<!-- 			<a href="#bookAppointment" class="btn btn-primary" data-toggle="modal">Book new appointment</a>
 -->				    									
			<!-- <table class="table">
				<tr>
					<th>Volunteer</th>
					<th>Patient</th>
					<th>Time</th>
					<th>Status</th>
					<th>Incomplete/Complete</th>
					<th>Comments</th>
					<th>Approve</th>
					<th>Decline</th>
					<th>Delete</th>
				</tr>
				<c:forEach items="${appointments}" var="a">
				<tr>
					<td>${a.volunteer}</td>
					<td>${a.patient}</td>
					<td>${a.date} ${a.time}</td>
					<td>${a.status}</td>
					<td><c:choose>
						<c:when test="${!a.completed}">Incomplete</c:when>
						<c:otherwise>Complete</c:otherwise>
						</c:choose></td>
					<td>${a.comments}</td>
					<td><a href="<c:url value="/approve_appointment/${a.appointmentID}"/>" class="btn btn-primary">Approve</a></td>
					<td><a href="<c:url value="/decline_appointment/${a.appointmentID}"/>" class="btn btn-danger">Decline</a></td>
					<td><a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger">Delete</a></td>
				</tr>
				</c:forEach>
			</table>
			<a href="#bookAppointment" class="btn btn-primary" data-toggle="modal">Book new appointment</a>
		</div>
	</div> -->
	<div class="bs-example bs-example-tabs">
    <ul id="myTab" class="nav nav-tabs">
	      <li class="active"><a href="#home" data-toggle="tab">Upcoming Appointments</a></li>
	      <li class=""><a href="#pastappointments" data-toggle="tab">Past Appointments</a></li>
	      <li class=""><a href="#pendingapproval" data-toggle="tab">Pending Approval</a></li>
	<!--       <li class="dropdown">
	        <a href="#" id="myTabDrop1" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
	        <ul class="dropdown-menu" role="menu" aria-labelledby="myTabDrop1">
	          <li><a href="#dropdown1" tabindex="-1" data-toggle="tab">@fat</a></li>
	          <li><a href="#dropdown2" tabindex="-1" data-toggle="tab">@mdo</a></li>
	        </ul>
	      </li> -->
	    </ul>
    <div id="myTabContent" class="tab-content">
      <div class="tab-pane fade active in" id="home">
        <div class="panel-group" id="accordian">
        <c:forEach items="${patients}" var="p">
				
				<div class="panel panel-default">
					<div class="panel-heading">
				    	<h4 class="panel-title">
				    		<a data-toggle="collapse" data-parent="#accordion" href="#collapse${p.firstName}${p.lastName}">
				        	${p.displayName}</a>
				      </h4>
				    </div>
 				    
 				    <div id="collapse${p.firstName}${p.lastName}" class="panel-collapse collapse">		
 				    	<div class="panel-body">		    	
<!-- 					<div class="accordion-inner">
				    		<div class="accordion-group"> -->
				    			<c:forEach items="${appointments}" var="a">
				    				<c:if test="${p.patientID == a.patientID}">
				    					<c:if test="${a.completed != true }">
				    						<a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger pull-right btn-sm">Delete</a>
				    					</c:if>
										<c:if test="${a.status != 'Declined' && a.status != 'Approved'}">
											<a href="<c:url value="/decline_appointment/${a.appointmentID}"/>" class="btn btn-warning pull-right btn-sm">Decline</a>
										</c:if>
										<c:if test="${a.status == 'Awaiting Approval'}">
											<a href="<c:url value="/approve_appointment/${a.appointmentID}"/>" class="btn btn-primary pull-right btn-sm">Approve</a>
										</c:if>
										<div class="panel">
											<div class="panel-heading">
				    							<h5 class="panel-title">
									    			<a data-toggle="collapse" href="#collapse${a.appointmentID}">${a.date} ${a.time}   (${a.status})</a> </h5>
									    	</div>
									    

									    <div id="collapse${a.appointmentID}" class="accordion-body collapse">
				    						<div class="accordion-inner">
				    							<c:if test="${a.completed}">
					    							<c:choose>
					    								<c:when test="${a.contactedAdmin}">
					    									Volunteer contacted Ernie<br /><br />
					    								</c:when>
					    								<c:otherwise>
					    									Volunteer did not contact Ernie<br /><br />
					    								</c:otherwise>
					    							</c:choose>
					    							<c:if test="${not empty a.comments}">
					    								Comments: ${a.comments} <br /><br />
					    							</c:if>
					    							Activites Completed:<br />
					    							<c:forEach items="${activities}" var="act">
					    								<c:if test="${a.appointmentID == act.appointment}">
					    									<h2>${act.time}: ${act.description}</h2> <br />
					    								</c:if>
					    							</c:forEach>
					    						</c:if>
				    						</div>
				    					</div>
				    				</div>
				    				</c:if>
				    			</c:forEach>
				    		</div>				    	
				    </div>				
				</div>
			</c:forEach>
      </div>
  </div>

	<div class="tab-pane fade" id="pastappointments">
	
		<table>
			<tr>
				<th width = "200"> Client</th>
				<th width = "300"> Volunteers</th>
				<th width = "200"> Date</th>
				<th width = "200"> Status</th>				
			</tr>
			<c:forEach items="${pastAppointments}" var="pa">
				<tr>
					<td> <a href="<c:url value="/display_appointment/${pa.appointmentID}"/>">${pa.patient}</a></td>
					<td> ${pa.volunteer}, ${pa.partner}</td>
					<td> ${pa.date}</td>					
					<td>
						<c:if test="${pa.completed eq true}">Completed</c:if>
						<c:if test="${pa.completed eq false}">Incompleted</c:if>						
					</td>					
				</tr>								
			</c:forEach>
		
		</table>
    </div>

    <div class="tab-pane fade" id="pendingapproval">
		<p> Appointments Pending Approval </p>
		<table>
			<tr>
				<th width = "200"> Client</th>
				<th width = "300"> Volunteers</th>
				<th width = "200"> Date</th>
				
			</tr>
			<c:forEach items="${pendingAppointments}" var="pendingapt">
				<tr>
					<td> ${pendingapt.patient}</td>
					<td> ${pendingapt.volunteer}, ${pa.partner}</td>
					<td> ${pendingapt.date}</td>				
									
				</tr>								
			</c:forEach>
		
		</table>
    </div>
 </div>
<!--       <div class="tab-pane fade" id="dropdown1">
        <p>Etsy mixtape wayfarers, ethical wes anderson tofu before they sold out mcsweeney's organic lomo retro fanny pack lo-fi farm-to-table readymade. Messenger bag gentrify pitchfork tattooed craft beer, iphone skateboard locavore carles etsy salvia banksy hoodie helvetica. DIY synth PBR banksy irony. Leggings gentrify squid 8-bit cred pitchfork. Williamsburg banh mi whatever gluten-free, carles pitchfork biodiesel fixie etsy retro mlkshk vice blog. Scenester cred you probably haven't heard of them, vinyl craft beer blog stumptown. Pitchfork sustainable tofu synth chambray yr.</p>
      </div>
      <div class="tab-pane fade" id="dropdown2">
        <p>Trust fund seitan letterpress, keytar raw denim keffiyeh etsy art party before they sold out master cleanse gluten-free squid scenester freegan cosby sweater. Fanny pack portland seitan DIY, art party locavore wolf cliche high life echo park Austin. Cred vinyl keffiyeh DIY salvia PBR, banh mi before they sold out farm-to-table VHS viral locavore cosby sweater. Lomo wolf viral, mustache readymade thundercats keffiyeh craft beer marfa ethical. Wolf salvia freegan, sartorial keffiyeh echo park vegan.</p>
      </div> -->
 
	<!-- OLD Modal -->
<!-- 	<div id="bookAppointment" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
  		<div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Book Appointment</h3>
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
				<div id="dp" class="input-append">
					<input data-format="yyyy-MM-dd" type="text" name="appointmentDate" readonly>
					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
				<label>Time:</label>
				<div id="tp" class="input-append">
					<input data-format="hh:mm:00" type="text" name="appointmentTime" readonly>
				    <span class="add-on">
				    	<i class="icon-time"></i>
				    </span>
				</div>
  			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    		<button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book" form="appt-form" class="btn btn-primary">Book</button>
  		</div>
	</div> -->
<!-- OLD Modal -->


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
				<div id="dp" class="input-append">
  					<!--<input data-format="yyyy-MM-dd" type="text" name="appointmentDate">-->
  					<input class="datepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentDate">

					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
				<label>Time:</label>
				<div id="tp" class="input-append">
  					<!--<input data-format="hh:mm:00" type="text" name="appointmentTime">-->
  					<input data-format="HH:i:00" class="timepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentTime">

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
</div>
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
		    formatSubmit: 'yyyy-mm-dd',
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
		
	</script>-->
  

</body>
</html>