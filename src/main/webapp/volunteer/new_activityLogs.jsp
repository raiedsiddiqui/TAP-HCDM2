<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import='java.text.SimpleDateFormat'%>
<%@ page import='java.util.Date' %>
<%SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String currentDate = sdf.format(new Date()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tapestry Volunteer Add Activity for Appointment</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
<%@include file="volunteer_head.jsp" %>


		<style type="text/css">
		html,body{
			height:100%;
		}
		
	</style>

</head>
<body>
<%@ include file="subNavi.jsp" %>
<div class="content">
			<form id="newActivityLog" action="<c:url value="/add_activity"/>" method="post">
				<div class="row">
					<div class="col-md-6">
						<h1>New Journal Entry</h1>
					</div>
				</div>

				<div class="row">
					<div class="col-md-3">
						<label>Date:</label>
							<div id="dp" class="input-append" role="dialog">
								<!-- <input id="activityDate" name="activityDate" data-format="yyyy-MM-dd" type="text" value = "${activityLog.date}" required> -->

								<input id="activityDate" class="datepickera form-control" data-format="yyyy-MM-dd" type="text" placeholder="Select Date" name="activityDate" value = "${activityLog.date}" required>
								<span class="add-on">
									<!-- <i class="icon-calendar"></i> -->
								</span>
							</div>
					</div>

					<div class="col-md-3">						
						<label>Start Time:</label>
							<div id="tp1" class="input-append" role="dialog">
								<!-- <input data-format="hh:mm:00" type="text" name="activityStartTime" id="activityStartTime" value="${activityLog.startTime}" > -->

								<input id="activityStartTime" data-format="hh:mm:00" class="timepickera form-control" type="text" placeholder="Select Time" name="activityStartTime" value="${activityLog.startTime}" required>
					    		<span class="add-on">
						    			<!-- <i class="icon-time"></i> -->
					   			 </span>
							</div>
					</div>

					<div class="col-md-3">
						<label>End Time:</label>
							<div id="tp2" class="input-append" role="dialog">
								<!-- <input data-format="hh:mm:00" type="text" name="activityEndTime" id="activityEndTime" value="${activityLog.endTime}"> -->


								<input id="activityEndTime" data-format="hh:mm:00" class="timepickerb form-control" type="text" placeholder="Select Time" name="activityEndTime" value="${activityLog.endTime}" required>
					    		<span class="add-on">
						    			<!-- <i class="icon-time"></i> -->
					   			 </span>
							</div>
					</div>
				</div>
				
			<div class="row">
				<div class="col-md-12">
					<h3>Journal Entry Description:</h3>
				</div>
			</div>
			
			<div class="row">
				<div class="col-md-12">
					<textarea id="activitytxt" name="activityDesc"  maxlength="500">${activityLog.description}</textarea>
					<input type="hidden" name="activityId" value="${activityLog.activityId}"/>	
				</div>
			</div>
		</form>
		</div>
	</div>

		<div class="row-fluid">
			<div class="col-md-12">
			<input type="button" value="Cancel" class="btn btn-primary" onclick="javascript:history.go(-1)">
				<button id="newActivityLogButton" data-loading-text="Loading..." type="submit"  form="newActivityLog" class="lgbtn pull-right">Save</button>
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
    // Escape any âruleâ characters with an exclamation mark (!).
    format: 'You selecte!d: dddd, dd mmm, yyyy',
    formatSubmit: 'yyyy-mm-dd',
    hiddenName: true
   	// hiddenPrefix: 'prefix__',
    // hiddenSuffix: '__suffix'
	})
	

	$('.timepickera').pickatime({
	    // Escape any âruleâ characters with an exclamation mark (!).
	    formatSubmit: 'HH:i:00',
	   	hiddenName: true,
	   	min: [8,0],
	   	max: [17,0]

	    // hiddenPrefix: 'prefix__',
	    // hiddenSuffix: '__suffix'
	})

	$('.timepickerb').pickatime({
	    // Escape any âruleâ characters with an exclamation mark (!).
	    formatSubmit: 'HH:i:00',
	   	hiddenName: true,
	   	min: [8,0],
	   	max: [17,0]

	    // hiddenPrefix: 'prefix__',
	    // hiddenSuffix: '__suffix'
	})

		
	</script>


	<!--

	<script type="text/javascript">
		$(function(){
  			
 			$('#newActivityLogButton').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });
		});


			$('.datepickera').pickadate({
		    // Escape any “rule” characters with an exclamation mark (!).
		    format: 'You selecte!d: dddd, dd mmm, yyyy',
		    formatSubmit: 'yyyy-mm-dd',
		    hiddenName: true
		   	// hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
			})
		

		$('.timepickera').pickatime({
		    // Escape any “rule” characters with an exclamation mark (!).
		    formatSubmit: 'HH:i:00',
		   	hiddenName: true,
		   	min: [8,0],
		   	max: [17,0]

		    // hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
		})

		$('.timepickerb').pickatime({
		    // Escape any “rule” characters with an exclamation mark (!).
		    formatSubmit: 'HH:i:00',
		   	hiddenName: true,
		   	min: [8,0],
		   	max: [17,0]

		    // hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
		})
	</script>
-->
</body>
</html>