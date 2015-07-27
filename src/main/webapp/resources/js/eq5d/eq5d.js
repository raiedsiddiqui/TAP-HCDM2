$(document).ready(function(){

	//Question 1
	if ($("#qtext:contains(MOBILITY)").length) {
		$("#qtext").load("../resources/js/eq5d/q1.html");
	}

	//Question 2
	if ($("#qtext:contains(SELF-CARE)").length) {
		$("#qtext").load("../resources/js/eq5d/q2.html");
	}

	//Question 3
	if ($("#qtext:contains(USUAL-ACTIVITIES)").length) {
		$("#qtext").load("../resources/js/eq5d/q3.html");
	}

	//Question 4
	if ($("#qtext:contains(PAIN/DISCOMFORT)").length) {
		$("#qtext").load("../resources/js/eq5d/q4.html");
	}

	//Question 5
	if ($("#qtext:contains(ANXIETY/DEPRESSION)").length) {
		$("#qtext").load("../resources/js/eq5d/q5.html");
	}

	//Question 6	
	if ($("#qtext:contains(This scale is numbered from 0 to 100.)").length) {
		$("#qtext").load("../resources/js/eq5d/q6.html");
	}
	
});