<div class="content">	
	<h3 class="pagetitle">Book Appointment <span class="pagedesc">Select the client name, date and time of the visit to book their next appointment</span></h3>
	<div>
		 <form id="book-appointment-form" method="post" action="<c:url value="/book_appointment"/>">
			<h4>With patient:</h4>
			<select id="selectpatient" name="patient" form="book-appointment-form" class="form-control">
				<c:forEach items="${patients}" var="p">
				<option value="${p.patientID}">${p.firstName} ${p.lastName}</option>
				</c:forEach>
			</select><br />
			
				<label>Date:</label>		
				<div id="dp" class="input-append" role="dialog">
					<input id="appointmentDate" class="datepickera form-control" data-format="yyyy-MM-dd" type="text" placeholder="Select Date" name="appointmentDate" value = "${appointment.date}" required>
					<span class="add-on">
						<!-- <i class="icon-calendar"></i> -->
					</span>
				</div>
			
				<label>Time:</label>
				<div id="tp" class="input-append" role="dialog">
 					<input id="appointmentTime" data-format="hh:mm:00" class="timepickera form-control" type="text" placeholder="Select Time" name="appointmentTime" value="${appointment.time}">
		    		<span class="add-on">
			    			<!-- <i class="icon-time"></i> -->
		   			 </span>
				</div>
				
  			</form>
	</div>
	<div class="modal-footer">
		<a href = "<c:url value="/out_book_appointment"/>" class="btn btn-default" data-dismiss="modal">Cancel</a>  
        <button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book" form="book-appointment-form" class="btn btn-primary">Book</button>
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

		
	</script>
