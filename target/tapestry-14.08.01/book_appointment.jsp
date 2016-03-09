<div class="content">
	<h3 class="pagetitle">Book Appointment <span class="pagedesc">Select the client name, date and time of the visit to book their next appointment</span></h3>
	<div>
		 <form id="book-appointment-form" method="post" action="<c:url value="/book_appointment"/>">
			<h4>With patient:</h4>
		<!-- search <input type="text" id="realtxt"/> -->
			<select id="selectpatient" name="patient" form="book-appointment-form" class="searchable form-control">
			<option disabled selected> -- select an client -- </option>
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

// document.getElementById('realtxt').onkeyup = searchSel;
// function searchSel() 
//     {
//       var input = document.getElementById('realtxt').value.toLowerCase();
       
//           len = input.length;
//           output = document.getElementById('selectpatient').options;
//       for(var i=0; i<output.length; i++)
//           if (output[i].text.toLowerCase().indexOf(input) != -1 ){
//           output[i].selected = true;
//               break;
//           }
//       if (input == '')
//         output[0].selected = true;
//     }

//     $(document).ready(function () {
	
// 	//$('.searchable option').wrap('<span id="filterspan"/>');
    
//     (function ($) {
//     	//When a keyup occurs, run this function
//         $('#realtxt').keyup(function () {

//         	//Get the value from the input field and save it as regexp
//             var rex = new RegExp($(this).val(), 'i');
            
//             //hide adds a display:none to each option item
//             //$('.searchable option').detach();
            
//             //Goes through the option list and returns the typed in value and if it is, it will show that value (display:inline)
//             $('.searchable option').filter(function () {
//             	//tests to see if the reg exp is true and if it is, returns the text value
            	
//             	if (rex.test($(this).text())) {
//             		//$(this).prop('disabled', false);
//             		$(this).show();
//             	}

//             	else {
//             		$(this).hide();
// 					//$(this).prop('disabled', true);

//        	    		//$('#filterspan').hide();
//             		//$(this).unwrap();
//             	}
//                 //return rex.test($(this).text());	
//             }) //.appendTo('.searchable');

//         })

//     }(jQuery));

// });
		
	</script>

<script type="text/javascript">
$(document).ready(function() {
  $("#selectpatient").select2();
});
</script>


<html>
<head>
	<title></title>
	<!-- <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script> -->
	<link href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/css/select2.min.css" rel="stylesheet" />
	<script src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/js/select2.min.js"></script>


</head>
</html>
