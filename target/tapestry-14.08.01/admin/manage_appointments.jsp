<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
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
		<a href="<c:url value="/book_appointment/0"/>" class="btn btn-primary">Book Appointment</a>
			</div>
		</div>
		
			<c:if test="${not empty success}">
				<div class="alert alert-success">Appointment has been successfully booked</div>
			 </c:if>
			 <c:if test="${not empty noMachedTime }">
			 	<div id = "noMatchInfo" class="alert alert-warning">
			 		<c:out value="${noMachedTime}" />
			 	</div>
			 </c:if>
	
			<br />

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
<div class="input-group"> <span class="input-group-addon">Filter</span>
	<input id="filter" type="text" class="form-control" placeholder="Type here...">
</div>

    <div id="myTabContent" class="tab-content">
    	<div class="tab-pane fade active in" id="home">
    		
      		<table id="myTable" class="table table-striped searchable">
        		<tr>
        			<th>Client</th>
        			<th>Date</th>
        			<th>Time</th>
        			<th>Volunteer Pair</th>
        			<th>Status</th>
        			<th width=500>Comments</th>
        			<!--<th>Activities</th>-->
        			<td>Approve/Decline</td>
        			<td>Delete</td>
        		</tr>
        		<c:forEach items="${upcomingAppointments}" var="a">
        			<tr>
				    	<td>
				    		<!--  a>${p.firstName} ${p.lastName}</a>-->
				    		<a href="<c:url value="/display_appointment/${a.appointmentID}"/>">${a.patient}</a>
					    </td>
					   	<td>	    				
							${a.date}  				    				
					   	</td>
					   	<td>
					   		${a.time}
					   	</td>
					  	<td>
					   		${a.volunteer}, ${a.partner}
					   	</td>
				    	<td>
							${a.status}
				    	</td>
				    	<td>
				    		<c:if test="${not empty a.comments}">
		    					${a.comments}
		    				</c:if>
				    	</td>
				    	<!--<td>
				    		${act.time}: ${act.description}
				    	</td>-->
				    	<td>
				    		<c:if test="${a.status == 'Approved'}">
			    				<a href="<c:url value="/decline_appointment/${a.appointmentID}"/>" class="btn btn-warning">Decline</a>
				    		</c:if>	
				    		<c:if test="${a.status == 'Declined'}">
								<a href="<c:url value="/approve_appointment/${a.appointmentID}"/>" class="btn btn-primary">Approve</a>
				    		</c:if>	
				    	</td>
						<td><a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="">Delete</a></td>
				    </tr>					  
				</c:forEach>      		
        	</table>

		</div>

	<div class="tab-pane fade" id="pastappointments">
		<table class="table searchable">
			<tr>
				<th width = "200"> Client</th>
				<th width = "300"> Volunteers</th>
				<th width = "200"> Date</th>
				<th width=500>Comments</th>
				<th width = "200"> Status</th>				
			</tr>
			<c:forEach items="${pastAppointments}" var="pa">			
				<tr>					
					<td> <a href="<c:url value="/display_appointment/${pa.appointmentID}"/>">${pa.patient}</a></td>
					<td> ${pa.volunteer}, ${pa.partner}</td>
					<td> ${pa.date}</td>
					<td>
				   		<c:if test="${not empty pa.comments}">
		    				${pa.comments}
		    			</c:if>
					</td>					
					<td>
						<c:if test="${pa.completed eq true}"><span style="color:green">Completed</span></c:if>
						<c:if test="${pa.completed eq false}"><span style="color:red">Incompleted</span></c:if>						
					</td>					
				</tr>
			</c:forEach>		
		</table>
    </div>

    <div class="tab-pane fade" id="pendingapproval">
		<p> Appointments Pending Approval </p>
		<table class="table">
			<tr>
				<th width = "200"> Client</th>
				<th width = "300"> Volunteers</th>
				<th width = "200"> Date</th>
				<th width = "200"> Approve/Decline</th>
				
			</tr>
			<c:forEach items="${pendingAppointments}" var="pendingapt">
				<tr>
					<td> ${pendingapt.patient}</td>
					<td> ${pendingapt.volunteer}, ${pa.partner}</td>
					<td> ${pendingapt.date}</td>	
					<td>
			    		<a href="<c:url value="/approve_appointment/${pendingapt.appointmentID}"/>" class="btn btn-primary">Approve</a>
				    </td>			
									
				</tr>								
			</c:forEach>
		
		</table>
    </div>
 </div>


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

	<script type="text/javascript">
$(document).ready(function () {

    (function ($) {

        $('#filter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('.searchable tr').hide();
            $('.searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        })

    }(jQuery));

});

	</script>
</body>
</html>