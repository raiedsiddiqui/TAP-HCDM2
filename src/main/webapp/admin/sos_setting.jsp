<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Set Settings</title>
<script src="${pageContext.request.contextPath}/resources/js/tapestryUtils.js"></script>	

<script type="text/javascript">
function enableOrDisableSos(action){
			
	var boxes = document.getElementsByName("local_admin");
	var i;
	var bool;
	
	if (action==1)
	{
		bool = false;
	//	document.getElementById("sos_sets").style.display = 'block';
	}		
	else
	{
		bool = true;
//		document.getElementById("sos_sets").style.display = 'none';
	}
		
	
	for (i = 0; i < boxes.length; i++) {
	    boxes[i].disabled = bool;
	}
	
	document.getElementById("elder_abuse_button").disabled = bool;
	document.getElementById("elder_abuse_content").disabled = bool;
	document.getElementById("self_harm_button").disabled = bool;
	document.getElementById("self_harm_content").disabled = bool;
	document.getElementById("crisis_lines_button").disabled = bool;
	document.getElementById("crisis_lines_content").disabled = bool;	
	
	
}

function enableOrDisableTextField(action, tagId){
	var bool;		
	if (action==1)
	{
		bool = false;
//		document.getElementById(tagId).style.display = 'block';
	}		
	else
	{
		bool = true;
	//	document.getElementById(tagId).style.display = 'none';
	}
		
	
	document.getElementById(tagId).disabled = bool;
}


</script>
	
	<style type="text/css">
		.row-fluid{	margin:10px;}
		.right
		{
			position:absolute;
			right:0px;
			
		}
	</style>
	
	
</head>
<body>
<div class="content">
	<%@include file="navbar.jsp" %>
	<h2>SOS Settings</h2>
	<br/>
	<div id="sosSettings">		
		<c:choose>
	    	<c:when test="${sos_setting_action == 'new'}">
	    		<form name = "newSosSettingsFm" id="newSosSettingsFm" method="post" action="<c:url value="/set_sosSettings"/>" >		
				<label>1 SOS button: </label>
				<input type="radio" name="sos_button" value="1" Onclick="enableOrDisableSos(1)"/>Enable
				<input type="radio" name="sos_button" value="0" Onclick="enableOrDisableSos(0)"/>Disable
				<br/>
				
				<div id="sos_sets">				
					<label>2 Select which local administrator should receive a message when the SOS button is clicked</label>		<br/>	
					
					<c:forEach items="${localAdmins}" var="la">										 
						 <input type="checkbox" name="local_admin"  value=${la.userID} />${la.name}	<br/>
					</c:forEach>
					<br/>
					<label>3 Create SOS buttons and Text Content(add up 3)</label><br/>
					<label>Button 1 </label>
					<input type="text" id="elder_abuse_button" name="elder_abuse_button" /><br/>
					<label>Button 1 - Content</label>
					<textarea id="elder_abuse_content" name="elder_abuse_content" rows="5" cols="100" ></textarea><br/>		
					<label>Button 2 </label>	
					<input type="text"  id="self_harm_button" name="self_harm_button"/><br/>
					<label>Button 2 - Content</label>
					<textarea id="self_harm_content" name="self_harm_content" rows="5" cols="100"></textarea><br/>
					<label>Button 3</label>
					<input type="text" id="crisis_lines_button" name="crisis_lines_button"/><br/>
					<label>Button 3 - Content</label>
					<textarea id="crisis_lines_content" name="crisis_lines_content" rows="5" cols="100"></textarea><br/>				
				</div>
				<h2>Notification Settings</h2>
				<h4>Appointment Notifications</h4>
				<label>2 Select which local administrator should receive a message when volunteer books a new appointment</label>	<br/>					
				<c:forEach items="${localAdmins}" var="la">										 
					 <input type="checkbox" name="local_admin_appointment"  value=${la.userID} />${la.name}	<br/>
				</c:forEach>
				<br/>
				<h4>Report Notifications</h4>
				<label>2 Select which local administrator should receive a message when a new report is generated</label>	<br/>					
				<c:forEach items="${localAdmins}" var="la">										 
					 <input type="checkbox" name="local_admin_report"  value=${la.userID} />${la.name}	<br/>
				</c:forEach>				
				<br/>
				
				<h2>Social Context Settings</h2>
				<input type="radio" name="social_context_onReport" value="1" Onclick='enableOrDisableTextField(1,"social_context_content")'/>Enable
				<input type="radio" name="social_context_onReport" value="0" Onclick='enableOrDisableTextField(0,"social_context_content")'/>Disable<br/>
				<label>Add an example for Social Context below. Please note: the social context appears only on the first visit</label><br/>
				<textarea id="social_context_content" name="social_context_content" rows="3" cols="100"></textarea>	
				<br/>
				<br/>
				<h2>Alerts Page(Key Observations) Settings</h2>
				<input type="radio" name="alerts_onReport" value="1" Onclick='enableOrDisableTextField(1,"alerts_content")'/>Enable
				<input type="radio" name="alerts_onReport" value="0" Onclick='enableOrDisableTextField(0,"alerts_content")'/>Disable<br/>
				<label>Add text for alerts page</label><br/>
				<textarea id="alerts_content" name="alerts_content" rows="3" cols="100"></textarea>	
				
				<input type="hidden" name="settingAction" value="New"/>
			</form>	   
			<div class="row">
				<input class="btn btn-primary" form="newSosSettingsFm" type="submit" name ="setSOS" value="Add Preference Settings" />
			</div> 
	      </c:when>	
	      <c:otherwise>
	      	<form name = "updateSosSettingsFm" id="updateSosSettingsFm" method="post" action="<c:url value="/set_sosSettings"/>" >		
				<label>1 SOS button: </label>
				<input type="radio" name="sos_button" value="1" <c:if test="${preference.sosButton == 1}">checked</c:if> Onclick="enableOrDisableSos(1)"/>Enable
				<input type="radio" name="sos_button" value="0" <c:if test="${preference.sosButton == 0}">checked</c:if> Onclick="enableOrDisableSos(0)"/>Disable
				<br/>
				<div id="sos_sets">		
					<label>2 Select which local administrator should receive a message when the SOS button is clicked</label>		<br/>	
					
					<c:forEach items="${localAdmins}" var="la">		
						<c:set var="la_id" value="${la.userID}"/>							 
						<input type="checkbox" name="local_admin"  value="${la.userID}" <c:if test="${fn:contains(preference.sosReceiver, la_id)}">checked</c:if>/>${la.name}	<br/>
					</c:forEach>
					<br/>
					<label>3 Create SOS buttons and Text Content(add up 3)</label><br/>
					<label>Button 1 </label>
					<input type="text" id="elder_abuse_button" name="elder_abuse_button" value="${preference.elderAbuseButton}"/><br/>
					<label>Button 1 - Content</label>
					<textarea id="elder_abuse_content" name="elder_abuse_content" rows="3" cols="100" >${preference.elderAbuseContent}</textarea>
					<a  href="#modalElderAbuse" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal">?</a>	<br/>	
					
					<label>Button 2 </label>	
					<input type="text" id="self_harm_button" name="self_harm_button" value="${preference.selfHarmButton}"/><br/>
					<label>Button 2 - Content</label>
					<textarea id="self_harm_content" name="self_harm_content" rows="3" cols="100">${preference.selfHarmContent}</textarea>
					<a  href="#modalSelfHarm" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal">?</a><br/>
					
					<label>Button 3</label>
					<input type="text" id="crisis_lines_button" name="crisis_lines_button" value="${preference.crisisLinesButton}"/><br/>
					<label>Button 3 - Content</label>				
					<textarea id="crisis_lines_content" name="crisis_lines_content" rows="3" cols="100">${preference.crisisLinesContent}</textarea>	
					<a  href="#modalCrisisLines" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal">?</a><br/>	
				</div>
				<h2>Notification Settings</h2>
				<h4>Appointment Notifications</h4>
				<label>Select which local administrator should receive a message when volunteer books a new appointment</label>	<br/>					
				<c:forEach items="${localAdmins}" var="la">	
				<c:set var="la_id" value="${la.userID}"/>									 
					 <input type="checkbox" name="local_admin_appointment"  value="${la.userID}"  <c:if test="${fn:contains(preference.apptNotiReceiver, la_id)}">checked</c:if>  />${la.name}	<br/>
				</c:forEach>
				<br/>
				<h4>Report Notifications</h4>
				<label>Select which local administrator should receive a message when a new report is generated</label>	<br/>					
				<c:forEach items="${localAdmins}" var="la">	
					<c:set var="la_id" value="${la.userID}"/>											 
					<input type="checkbox" name="local_admin_report"  value="${la.userID}"<c:if test="${fn:contains(preference.reportNotiReceiver, la_id)}">checked</c:if> />${la.name}	<br/>
				</c:forEach>
				<br/>
				
				<h2>Social Context Settings</h2>
				<input type="radio" name="social_context_onReport" value="1" <c:if test="${preference.socialContextOnReport == 1}">checked</c:if> Onclick='enableOrDisableTextField(1,"social_context_content")'/>Enable
				<input type="radio" name="social_context_onReport" value="0" <c:if test="${preference.socialContextOnReport == 0}">checked</c:if> Onclick='enableOrDisableTextField(0,"social_context_content")'/>Disable<br/>
				<div id="social_context_set">
					<label>Add an example for Social Context below. Please note: the social context appears only on the first visit</label><br/>
					<textarea id="social_context_content" name="social_context_content" rows="3" cols="100">${preference.socialContextContent}</textarea>	
				</div>
				<br/>
				<br/>
				<h2>Alerts Page(Key Observations) Settings</h2>
				<input type="radio" name="alerts_onReport" value="1" <c:if test="${preference.alertsOnReport == 1}">checked</c:if> Onclick='enableOrDisableTextField(1,"alerts_content")' />Enable
				<input type="radio" name="alerts_onReport" value="0" <c:if test="${preference.alertsOnReport == 0}">checked</c:if> Onclick='enableOrDisableTextField(0,"alerts_content")'/>Disable<br/>
				<div id="alerts_set">
				<label>Add text for alerts page</label><br/>
				<textarea id="alerts_content" name="alerts_content" rows="3" cols="100">${preference.alertsText}</textarea>	
				</div>
				
				<input type="hidden" name="settingAction" value="Update"/>		
			</form>	    
			<div class="row">
				<input class="btn btn-primary" form="updateSosSettingsFm" type="submit" name ="setSOS" value="Update Preference Settings" />
			</div>
      	</c:otherwise>
	</c:choose>		
</div>
	<div class="modal fade" id="modalElderAbuse" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
	  		<div class="modal-content">
	      		<div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			        <h4 class="modal-title" id="myModalLabel">
						Elder Abuse
			        </h4>
	      		</div>
	      		<div class="modal-body">
	       			<p class="text-warning">${preference.elderAbuseContent}</p>
	      		</div>
	      		<div class="modal-footer">
	        		<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      		</div>
	    	</div>
  		</div>
	</div>
	
	<div class="modal fade" id="modalCrisisLines" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
	  		<div class="modal-content">
	      		<div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			        <h4 class="modal-title" id="myModalLabel">
						Crisis Lines
			        </h4>
	      		</div>
	      		<div class="modal-body">
	       			<p class="text-warning">${preference.crisisLinesContent}</p>
	      		</div>
	      		<div class="modal-footer">
	        		<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      		</div>
	    	</div>
  		</div>
	</div>
	
	<div class="modal fade" id="modalSelfHarm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
	  		<div class="modal-content">
	      		<div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			        <h4 class="modal-title" id="myModalLabel">
						Self Harm
			        </h4>
	      		</div>
	      		<div class="modal-body">
	       			<p class="text-warning">${preference.selfHarmContent}</p>
	      		</div>
	      		<div class="modal-footer">
	        		<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      		</div>
	    	</div>
  		</div>
	</div>
	
</div>
</body>
</html>