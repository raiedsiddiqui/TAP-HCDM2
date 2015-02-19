<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
  	<%@include file="volunteer_head.jsp" %>


	<style type="text/css">
		html,body{
			height:100%;
		}


	</style>
</head>

<body>	
<div id="headerholder">	
<%@include file="subNavi.jsp" %>
</div>

	<!-- 	breadcrumb START-->	
<!-- 	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>"><img src="${pageContext.request.contextPath}/resources/images/home.png" height="20" width="20" />My Clients</a> </li>

		</ul>	
	</div> -->
	<!-- 	breadcrumb END-->	
	
	<div class="content">
		<h3 class="pagetitle">My Clients<span class="pagedesc">These are the clients you are assigned to. Select a client name to view their appointments</span></h3>
		<div class="row-fluid">
				
				<div class="tab-content">
					<div class="tab-pane active" id="all">
						<c:forEach items="${clients}" var="c">
							<div class="pname">
								<c:choose>
									<c:when test="${not empty c.preferredName}">
										<button type="button" class="cbutton" onclick="location.href='<c:url value="/?patientId=${c.patientID}"/>'">${c.preferredName}</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="cbutton" onclick="location.href='<c:url value="/?patientId=${c.patientID}"/>'">${c.displayName}</button>
									</c:otherwise>
								</c:choose>
							</div>
						</c:forEach>
					</div>
				</div>
		</div>
	</div>
</body>
</html>