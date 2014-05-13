
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
			$('#tp1').datetimepicker({
				pickDate: false,
				pickSeconds: false
			});
			
			$('#tp2').datetimepicker({
				pickDate: false,
				pickSeconds: false
			});
			
			$('#dp').datetimepicker({
				pickTime: false,
				startDate: new Date()
  			});
  			
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
			<div class="col-md-2">
				<img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
			</div>

		<div class="col-md-10">
		<div class="navbar">      
			<ul class="nav navbar-nav">	    	
	    		<li><a class="brand" href="<c:url value="/"/>">Appointments</a></li>
	    		<li class="active"><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
	    		<li><a href="<c:url value="/view_activityLogs"/>">Activity Logs</a></li>
	    		<li><a href="<c:url value="/view_narratives"/>">Narratives</a></li>
				<li><a href="<c:url value="/logout"/>">Log Out</a></li>

		    </ul>
		 </div>	
		</div>

		</div>
	</div>

