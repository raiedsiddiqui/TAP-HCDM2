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
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
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
				<input type="radio" name="sos_button" value="1" />Enable
				<input type="radio" name="sos_button" value="0" />Disable
				<br/>
				
				<label>2 Select which local administrator should receive a message when the SOS button is clicked</label>		<br/>	
				
				<c:forEach items="${localAdmins}" var="la">										 
					 <input type="checkbox" name="local_admin"  value=${la.userID} />${la.name}	<br/>
				</c:forEach>
				<br/>
				<label>3 Create SOS buttons and Text Content(add up 3)</label><br/>
				<label>Button 1 </label>
				<input type="text" name="elder_abuse_button" /><br/>
				<label>Button 1 - Content</label>
				<textarea name="elder_abuse_content" rows="5" cols="100" ></textarea><br/>		
				<label>Button 2 </label>	
				<input type="text" name="self_harm_button"/><br/>
				<label>Button 2 - Content</label>
				<textarea name="self_harm_content" rows="5" cols="100"></textarea><br/>
				<label>Button 3</label>
				<input type="text" name="crisis_lines_button"/><br/>
				<label>Button 3 - Content</label>
				<textarea name="crisis_lines_content" rows="5" cols="100"></textarea><br/>				
				<input type="hidden" name="settingAction" value="New"/>
			</form>	   
			<div class="row">
				<input class="btn btn-primary" form="newSosSettingsFm" type="submit" name ="setSOS" value="Save SOS Settings" />
			</div> 
	      </c:when>	
	      <c:otherwise>
	      	<form name = "updateSosSettingsFm" id="updateSosSettingsFm" method="post" action="<c:url value="/set_sosSettings"/>" >		
				<label>Up1 SOS button: </label>
				<input type="radio" name="sos_button" value="1" <c:if test="${preference.sosButton == 1}">checked</c:if> />Enable
				<input type="radio" name="sos_button" value="0" <c:if test="${preference.sosButton == 0}">checked</c:if>/>Disable
				<br/>
				
				<label>2 Select which local administrator should receive a message when the SOS button is clicked</label>		<br/>	
				
				<c:forEach items="${localAdmins}" var="la">		
					<c:set var="la_id" value="${la.userID}"/>							 
					<input type="checkbox" name="local_admin"  value="${la.userID}" <c:if test="${fn:contains(preference.sosReceiver, la_id)}">checked</c:if>/>${la.name}	<br/>
				</c:forEach>
				<br/>
				<label>3 Create SOS buttons and Text Content(add up 3)</label><br/>
				<label>Button 1 </label>
				<input type="text" name="elder_abuse_button" value="${preference.elderAbuseButton}"/><br/>
				<label>Button 1 - Content</label>
				<textarea name="elder_abuse_content" rows="5" cols="100" >${preference.elderAbuseContent}</textarea><br/>		
				<label>${preference.elderAbuseContent}</label><br/>
				<label>Button 2 </label>	
				<input type="text" name="self_harm_button" value="${preference.selfHarmButton}"/><br/>
				<label>Button 2 - Content</label>
				<textarea name="self_harm_content" rows="5" cols="100">${preference.selfHarmContent}</textarea><br/>
				<label>${preference.selfHarmContent}</label><br/>
				<label>Button 3</label>
				<input type="text" name="crisis_lines_button" value="${preference.crisisLinesButton}"/><br/>
				<label>Button 3 - Content</label>				
				<textarea name="crisis_lines_content" rows="5" cols="100">${preference.crisisLinesContent}</textarea><br/>		
				<label>${preference.crisisLinesContent}</label>
				<input type="hidden" name="settingAction" value="Update"/>		
			</form>	    
			<div class="row">
				<input class="btn btn-primary" form="updateSosSettingsFm" type="submit" name ="setSOS" value="Save SOS Settings" />
			</div>
      </c:otherwise>
</c:choose>
		
	</div>
	
	
</div>
</body>
</html>