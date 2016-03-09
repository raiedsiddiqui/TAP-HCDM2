<%@page contentType="text/html" import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

	<title>Modify Activity Log</title>
	<%@include file="volunteer_head.jsp" %>
</head>

<body>
	<%@ include file="subNavi.jsp" %>

	<div class="content">
		<form id="modifyActivity" action="<c:url value="/update_activity"/>" method="post">
		<div class="row-fluid">
			<div class="col-md-6">
				<h4>Modify Activity Log</h4>
			</div>
		</div>

		<div class="row-fluid">
			<div class="col-md-3">
				<label>Date:</label>
					<div id="dp1" class="input-append">
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

						<input id="activityStartTime" data-format="hh:mm:00" class="timepickera form-control" type="text" placeholder="Select Start Time" name="activityStartTime" value="${activityLog.startTime}">
			    		<span class="add-on">
			    			<!-- <i class="icon-time"></i> -->
			   			 </span>
					</div>
			</div>

			<div class="col-md-3">
				<label>End Time:</label>
					<div id="tp2" class="input-append" role="dialog">
						<!-- <input data-format="hh:mm:00" type="text" name="activityEndTime" id="activityEndTime" value="${activityLog.endTime}"> -->

						<input id="activityEndTime" data-format="hh:mm:00" class="timepickerb form-control" type="text" placeholder="Select End Time" name="activityEndTime" value="${activityLog.endTime}">

			    		<span class="add-on">
			    			<!-- <i class="icon-time"></i> -->
			   			 </span>
					</div>
			</div>
		</div>
				
		<div class="row-fluid">
			<div class="col-md-3">
				<h3>Activity Description:</h3>
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="col-md-3">
				<textarea name="activityDesc"  maxlength="500">${activityLog.description}</textarea>
				<input type="hidden" name="activityId" value="${activityLog.activityId}"/>	
			</div>
		</div>
		</form>
				
			</div>
		</div>

		<div class="row-fluid">
			<div class="col-md-3">	
				<input type="button" value="Cancel" class="btn btn-primary" onclick="javascript:history.go(-1)">
				<button id="mActivityLog" data-loading-text="Loading..." type="submit"  form="modifyActivity" class="btn btn-primary">Save Changes</button>
			</div>
		</div>
		
	</div>
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
</body>
</html>