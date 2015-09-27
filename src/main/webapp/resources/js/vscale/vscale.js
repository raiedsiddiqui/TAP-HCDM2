// if ($('#qtext:contains(Question 2 of 7)'))
// {
//     alert('found Question 2');    
// }

$(document).ready(function(){
	//CaregiverBackground and FollowUp	
	if ($("#qtext:contains(How would you rate your health at the moment?)").length) {
		$("#externalsurveycode").load("../resources/js/vscale/caregivervscale.html");
	}

	//CarerQol	
	if ($("#qtext:contains(How happy do you feel at the moment?)").length) {
		$("#externalsurveycode").load("../resources/js/vscale/carerqolvscale.html");
	}

	//VIA (McGill)
	var via = $("#qID").val()
	if (via.indexOf("VIA") != -1) {
		$("#externalsurveycode").load("../resources/js/vscale/VIA.html");
	}

	//Satisfaction with Healtcare
	if (via.indexOf("SWH1") != -1) {
		$("#externalsurveycode").load("../resources/js/vscale/SWH.html");
	}
	
});