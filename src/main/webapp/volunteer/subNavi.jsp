
<head>
	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
			overflow-x:auto;
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;
		}

		tr:hover{
			background-color:#D9EDF7;
		}

		.navbar {
			width: 100%;
		}
	</style>
	<script type="text/javascript">
		$(function(){
			// $('#tp1').datetimepicker({
			// 	pickDate: false,
			// 	pickSeconds: false
			// });
			
			// $('#tp2').datetimepicker({
			// 	pickDate: false,
			// 	pickSeconds: false
			// });
			
			// $('#dp').datetimepicker({
			// 	pickTime: false,
			// 	startDate: new Date()
  	// 		});
  			
 			$('#newActivityLogButton').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });
		});
	</script>
</head>

	<div id="headerholder">	
		<div class="row">
			<div class="col-md-3 tpurple logoheight">
				<img id="logo" src="<c:url value="/resources/images/logow.png"/>" />
			</div>

		<div class="col-md-9 tblack logoheight">
		<div class="navbar">      
			<ul id="mainnavbar" class="nav navbar-nav">	
	    		<li id="navhome"><a href="<c:url value="/"/>">Home</a></li>
	    		<li id="navclient"><a href="<c:url value="/client"/>">Clients</a></li>    	
	 <!--    		<li><a href="#bookAppointment" data-toggle="modal">Book Visit</a></li>    	--> 
	 			<li><a href="<c:url value="/book_appointment"/>">Book Visit</a></li>
 	    		<li><a href="<c:url value="/view_activity"/>">Journal</a> </li>
	    		<li><a href="<c:url value="/view_narratives"/>">Narratives</a></li>
	    		<li><a href="<c:url value="/inbox"/>">Inbox <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
				<li><a href="<c:url value="/logout"/>">Log Out</a></li>

		    </ul>
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
-->

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

		
	</script>

