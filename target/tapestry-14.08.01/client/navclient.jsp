
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

		/*tr:hover{
			background-color:#D9EDF7;
		}*/

		.navbar {
			width: 100%;

		}
		.navbar-nav {
			float:right;
		}

		#mainnavbar {
			width: 100%;
		}

		header {
			background-color: #444;
		}

		.navbar-brand {
			padding: 0px;
		}
		.navbar-toggle .icon-bar {
			background-color: white;
		}
		 .helpbtn {
			  /* position:fixed; */
			  float:right;
			  position:fixed;
			  background-color: #30b033;
			  border: 1px solid #30b033;
			  color:white;
			  padding: 5px 10px 5px 10px;
			  font-size: 20px;
			  /*text-transform: uppercase;*/
			  right: 0px;
			  z-index: 1;
			  border-radius: 0;
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

	<!-- <div id="headerholder">	
		<div class="row">
			<div class="col-md-3 tpurple logoheight">
				<img id="logo" src="<c:url value="/resources/images/logow.png"/>" />
			</div>

		<div class="col-md-9 tblack logoheight" style="padding-left:0px;padding-right:0px;">
		

		<div class="navbar">      
			<ul id="mainnavbar" class="nav navbar-nav">	
	    		<li id="navhome"><a href="<c:url value="/"/>">Home</a></li>
	    		<li id="navclient"><a href="<c:url value="/client"/>">Clients</a></li>    	
	     		<li><a href="#bookAppointment" data-toggle="modal">Book Visit</a></li>    	 
	 			<li><a href="<c:url value="/book_appointment"/>">Book Visit</a></li>
 	    		<li><a href="<c:url value="/view_activity"/>">Journal</a> </li>
	    		<li><a href="<c:url value="/view_narratives"/>">Narratives</a></li>
	      		<li><a href="<c:url value="/view_mySurveys"/>">MySurveys</a></li>
	    		<li><a href="<c:url value="/inbox"/>">Inbox <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
	    		<li><a href="<c:url value="/profile"/>">Profile</a></li>
				<li><a href="<c:url value="/logout"/>">Log Out</a></li>

		    </ul>
		 </div>	
		</div>

		</div>
	</div> -->



	
<header class="navbar navbar-static-top bs-docs-nav" id="top" role="banner">
  <div class="container">
    <div class="navbar-header">
      <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a href=""<c:url value="/"/>" class="navbar-brand"> 
      <img id="logo" src="<c:url value="/resources/images/hcdmlogo.png"/>" /></a>
    </div>
    <nav class="collapse navbar-collapse bs-navbar-collapse">
    	
      <ul class="nav navbar-nav">
				<li id="navhome"><a href="<c:url value="/"/>">Home</a></li>
				<li><a href="https://maple.myoscar.org:8384/myoscar_ui/login.jsf" target="_blank">kindredPHR</a></li>
	    		<!--<li id="navclient"><a href="<c:url value="/client"/>">Clients</a></li>    	
	 			<li><a href="<c:url value="/book_appointment/0"/>">Book Visit</a></li>
 	    		<li><a href="<c:url value="/view_activity"/>">Journal</a> </li>
	    		<li><a href="<c:url value="/view_narratives"/>">Narratives</a></li> -->
	<!-- 		<li><a href="<c:url value="/view_mySurveys"/>">MySurveys</a></li> 
	    		<li><a href="<c:url value="/inbox"/>">Inbox <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>-->
	    		<li><a href="<c:url value="/cprofile"/>">Profile</a></li> 
	    		<li><a href="<c:url value="/tipps"/>">Resources</a></li>
				<li><a href="<c:url value="/logout"/>">Log Out</a></li>
      </ul>
    </nav>
  </div>
</header>

<!-- Button trigger modal -->
<button class="helpbtn btn" data-toggle="modal" data-target="#myModal" style="position:fixed">
<span class="glyphicon glyphicon-info-sign"></span> Help
<br>
I'm stuck! </button>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="myModalLabel">Contact Information</h4>
      </div>
      <div class="modal-body">
      <h2>Don't Panic!</h2>
        <p style="font-size:25px;">If at any time you run into difficulty, you can contact the Volunteer Coordinator, Anne, at 905-523-4444 ext. 101 to connect you with your volunteers, or the Health TAPESTRY research team at 905-525-9140 ext. 28493</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
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

