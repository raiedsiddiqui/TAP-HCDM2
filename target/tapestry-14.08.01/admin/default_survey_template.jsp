<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Set Default Survey Template</title>
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
	
	<script type="text/javascript">
	function setAction(){
		var group = document.setDefaultSurveyTemplate.survey_template;
		var hasChecked = false;
		
		for (var i=1; i<group.length; i++)
		{
			if (group[i].checked)
			{
				hasChecked = true;
				break;
			}
		}
				
		if (hasChecked == false)
		{
			alert("Please select at least one survey template!");
			return false;
		}
		
		document.getElementById("hAction").value = document.pressed;	
		 return true;
	}
	</script>
</head>
<body>
<div class="content">
	<%@include file="navbar.jsp" %>
	<h2>Defalut Surveys</h2>
	<h5>Select the surveys that should be assigned to patients when their profile is created. Only the selected surveys will be assigned. Surveys that are not checked will need to be manually assigned.</h5>

	<br/>
	<div id="suveyTemplates">
		<form name = "setDefaultSurveyTemplate" id="setDefaultSurveyTemplate" method="post" action="<c:url value="/set_defaultSurveys"/>" onsubmit="return setAction();">		
			<c:forEach items="${survey_templates}" var="st">										 
				 <input type="checkbox" name="survey_template"  value=${st.surveyID} ><c:choose><c:when test="${st.defaultSurvey}"><font color="red">${st.title}</font></c:when><c:otherwise>${st.title}</c:otherwise></c:choose>
				 <br>
			</c:forEach>
		<input id="hAction" name="hAction" type="hidden" value=""/>
		</form>
	</div>
	
	<div class="row">
			<input class="btn btn-primary" form="setDefaultSurveyTemplate" type="submit" name ="setDefault" value="Set Default" onclick="document.pressed=this.value" />
			<input class="btn btn-primary" form="setDefaultSurveyTemplate" type="submit" name ="removeDefault" value="Remove Default" onclick="document.pressed=this.value" />
			
		</div>
</div>
</body>
</html>