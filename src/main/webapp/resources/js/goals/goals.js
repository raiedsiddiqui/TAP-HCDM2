// if ($('#qtext:contains(Question 2 of 7)'))
// {
//     alert('found Question 2');    
// }

$(document).ready(function(){
	//Question 1	
	if ($("#qtext:contains(Please explain the things that matter to you MOST in all parts of your life.)").length) {
		$("#externalsurveycode").load("../resources/js/goals/goalsq1.html");
	}

	//Question 2	
	if ($("#qtext:contains(What are some specific goals that you have in your LIFE?)").length) {
		$("#externalsurveycode").load("../resources/js/goals/goalsq2.html");
	}
	
	//Question 3
	if ($("#qtext:contains(What are some specific goals that you have for your HEALTH?)").length) {
		$("#externalsurveycode").load("../resources/js/goals/goalsq3.html");
	}

	//Question 4
	if ($("#qtext:contains(Of the list of both life and health goals we just went through, can you pick 3 that you would like to focus on in the next 6 months?)").length) {
		$("#externalsurveycode").load("../resources/js/goals/goalsq4.html");
	}

	//Question 5
	if ($("#qtext:contains(Goal 1:)").length) {
		$("#qtext").load("../resources/js/goals/goalsq5.html");
	}

	//Question 6
	if ($("#qtext:contains(Goal 2:)").length) {
		$("#qtext").load("../resources/js/goals/goalsq6.html");
	}

	//Question 7
	if ($("#qtext:contains(Goal 3:)").length) {
		$("#qtext").load("../resources/js/goals/goalsq7.html");
	}

	//Question 8
	if ($("#qtext:contains(Of these goals, which one are you most willing to work on over the next 6 months)").length) {
		$("#externalsurveycode").load("../resources/js/goals/goalsq8.html");
	}

});