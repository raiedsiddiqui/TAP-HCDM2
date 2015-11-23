<head>

<%@include file="admin_head.jsp" %>

</head>

<div id="adminheader">
  <img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
  <!-- <img id="logofam" src="<c:url value="/resources/images/fammed.png"/>" /> -->
  <img id="logofhs" src="<c:url value="/resources/images/fhs.png"/>" />
  <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>
</div>
<nav class="navbar navbar-default" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
<!--           <a class="navbar-brand" href="#">TAPESTRY</a>
 -->        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
          	<li <c:if test="${pageContext.request.requestURI.contains('view_volunteers')}">class="active"</c:if>><a href="<c:url value="/view_volunteers"/>"><span class="glyphicon glyphicon-user"></span> Volunteers</a></li>
          	<!-- <li class="dropdown">
	        	<a href="" class="dropdown-toggle" data-toggle="dropdown">Clients<span class="caret"></span></a>
	        	<ul class="dropdown-menu" role="menu">
					<li <c:if test="${pageContext.request.requestURI.contains('manage_patients')}">class="active"</c:if>><a href="<c:url value="/manage_patients"/>"><span class="glyphicon glyphicon-plus"></span>Add/Edit Client</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('view_clients_admin')}">class="active"</c:if>><a href="<c:url value="/view_clients_admin"/>">Manage Clients </a></li>
	          	</ul> 
			</li>-->
			
			<li <c:if test="${pageContext.request.requestURI.contains('view_clients')}">class="active"</c:if>><a href="<c:url value="/view_clients_admin"/>"><span class=""></span>Clients</a></li>

			<li <c:if test="${pageContext.request.requestURI.contains('manage_appointments')}">class="active"</c:if>><a href="<c:url value="/manage_appointments"/>"><span class="glyphicon glyphicon-time"></span> Appointments</a></li>

			<!-- <li <c:if test="${pageContext.request.requestURI.contains('manage_surveys')}">class="active"</c:if>><a href="<c:url value="/manage_surveys"/>">Survey</a></li> -->
			
			<li <c:if test="${pageContext.request.requestURI.contains('inbox')}">class="active"</c:if>><a href="<c:url value="/inbox"/>"><span class="glyphicon glyphicon-envelope"></span> Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a> </li>
			
			<li <c:if test="${pageContext.request.requestURI.contains('user_logs')}">class="active"</c:if>><a href="<c:url value="/user_logs/1"/>"><span class="glyphicon glyphicon-stats"> User Logs </a><li>

					
			<li class="dropdown">
	          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Other<span class="glyphicon glyphicon-user"></span><span class="caret"></span></a>
	          <ul class="dropdown-menu" role="menu">
				
				<li <c:if test="${pageContext.request.requestURI.contains('manage_users')}">class="active"</c:if>><a href="<c:url value="/manage_users"/>">Manage Users</a></li>

				<li <c:if test="${pageContext.request.requestURI.contains('view_organizations')}">class="active"</c:if>><a href="<c:url value="/view_organizations"/>">Organizations <span class="glyphicon glyphicon-tower"></span></a></li>

				<li <c:if test="${pageContext.request.requestURI.contains('manage_survey')}">class="active"</c:if>><a href="<c:url value="/manage_survey"/>">Manage Client Surveys </a></li>
				
				<li <c:if test="${pageContext.request.requestURI.contains('set_default_survey_templates')}">class="active"</c:if>><a href="<c:url value="/set_default_survey_templates"/>">Default Client Surveys </a></li>
				
				<li <c:if test="${pageContext.request.requestURI.contains('view_research_data')}">class="active"</c:if>><a href="<c:url value="/view_research_data"/>">Research Data Download</a></li>

				<li <c:if test="${pageContext.request.requestURI.contains('manage_clinics')}">class="active"</c:if>><a href="<c:url value="/manage_clinics"/>">Clinics</a></li>
				
				<li <c:if test="${pageContext.request.requestURI.contains('manage_volunteer_survey')}">class="active"</c:if>><a href="<c:url value="/manage_volunteer_survey"/>">Manage Volunteer Surveys </a></li> 

				<li <c:if test="${pageContext.request.requestURI.contains('view_activity_admin')}">class="active"</c:if>><a href="<c:url value="/view_activity_admin"/>">Volunteer Activity Log <span class="glyphicon glyphicon-pencil"></a></li>
			
				<li <c:if test="${pageContext.request.requestURI.contains('view_adminActivities')}">class="active"</c:if>><a href="<c:url value="/view_adminActivities"/>">Admin Activity Log <span class="glyphicon glyphicon-pencil"></a></li>
				<li <c:if test="${pageContext.request.requestURI.contains('setDefault_existingClient')}">class="active"</c:if>><a href="<c:url value="/setDefault_existingClient"/>">Set Default Username and Password for Client <span class="glyphicon glyphicon-pencil"></a></li> 
			
	          </ul>
	        </li>	

			       
			        
				<!-- 
					<li <c:if test="${pageContext.request.requestURI.contains('manage_survey_templates')}">class="active"</c:if>><a href="<c:url value="/manage_survey_templates"/>">Survey Management... <span class="glyphicon glyphicon-list"></a></li>
				 -->
							
			<li><a href="<c:url value="/logout"/>">Log Out <span class="glyphicon glyphicon-log-out"></a></li> 
	
          </ul>
        </div><!--/.nav-collapse -->
</nav>



<!-- 
<div class="navbar">
	<div class="navbar-inner">
		<div class="container">
			<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
    			<span class="icon-bar"></span>
        		<span class="icon-bar"></span>
       			<span class="icon-bar"></span>
     		</a>
     					
     		<a class="brand" href="<c:url value="/"/>">Home</a>
     		<div class="nav-collapse collapse">
				<ul class="nav">
					<li <c:if test="${pageContext.request.requestURI.contains('view_volunteers')}">class="active"</c:if>><a href="<c:url value="/view_volunteers"/>">Volunteers</a></li>
					
					<li <c:if test="${pageContext.request.requestURI.contains('manage_users')}">class="active"</c:if>><a href="<c:url value="/manage_users"/>">Manage Users</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_patients')}">class="active"</c:if>><a href="<c:url value="/manage_patients"/>">Manage Patients</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_appointments')}">class="active"</c:if>><a href="<c:url value="/manage_appointments"/>">Manage Appointments</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_survey_templates')}">class="active"</c:if>><a href="<c:url value="/manage_survey_templates"/>">Manage Survey Templates</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_surveys')}">class="active"</c:if>><a href="<c:url value="/manage_surveys"/>">Manage Surveys</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_surveys')}">class="active"</c:if>><a href="<c:url value="/"/>">Activity Log</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('inbox')}">class="active"</c:if>><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('user_logs')}">class="active"</c:if>><a href="<c:url value="/user_logs/1"/>">User Logs</a><li>
					<li><a href="<c:url value="/logout"/>">Log Out</a></li>
				</ul>
			</div>
		</div>	
	</div>
</div> -->