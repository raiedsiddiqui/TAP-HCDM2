<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
	<%@include file="../volunteer/volunteer_head.jsp" %>
	<style type="text/css">
	
	.tips_links {
		color:#428bca;
		text-decoration: none;
		padding: 10px;
		font-size: 1.8em;
	}

	.tips_links:hover {
		background-color: #428bca;
		text-decoration: none;
		color:white;
		padding: 10px;
	}
	</style>

	
</head>
	
<body>
<div id="headerholder">	
 <%@include file="navclient.jsp" %>
</div>
<div class="content">
<h1>Tip Sheet/s</h1>
<h4>Click on a Tip sheet below</h4>

<h2>Diabetes</h2>
<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/1.Managing%20Your%20Blood%20Glucose.pdf"/>"/>Managing Your Blood Glucose</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/2.Blood%20Glucose%20Log.pdf"/>"/>Blood Glucose Log</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/3.Setting%20Reminders%20for%20Checking%20Blood%20Glucose%20Levels.pdf"/>"/>Setting Reminders for Checking Blood Glucose Levels</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/4.Lessening%20the%20Pain%20from%20Fingertip%20Testing.pdf"/>"/>Lessening the Pain from Fingertip Testing</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/5.Lows%20and%20Highs%20-%20Blood%20Glucose%20Levels.pdf"/>"/>Lows and Highs - Blood Glucose Levels</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/6.What%20A1C%20Should%20I%20Target.pdf"/>"/>What A1C Should I Target</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/14.Diabetes%20and%20Foot%20Care.pdf"/>"/>Diabetes and Foot Care</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/31.Diabetes%20and%20Eye%20Care.pdf"/>"/>Diabetes and Eye Care</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/33.Complications%20of%20Diabetes.pdf"/>"/>Complications of Diabetes</a></p>
<!-- <p><a class="tips_links" href="<c:url value="/resources/tips_sheets/7.Blank%20A1c%20RBG%20Lipids%20Lab%20Request.pdf"/>"/>7.Blank A1c RBG Lipids Lab Request</a></p> -->

<h2>Blood Pressure/Hypertension</h2>
<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/15.Monitoring%20Your%20Blood%20Pressure%20At%20Home.pdf"/>"/>Monitoring Your Blood Pressure At Home</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/17.Managing%20Your%20Blood%20Pressure.pdf"/>"/>Managing Your Blood Pressure</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/30.Complications%20related%20to%20Hypertension.pdf"/>"/>Complications related to Hypertension</a></p>

<h2>Height, Weight and BMI</h2>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/8.Managing%20Weight.pdf"/>"/>Managing Weight</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/9.Body%20Mass%20Index.pdf"/>"/>Body Mass Index</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/13.Healthy%20Waists.pdf"/>"/>Healthy Waists</a></p>

<h2>Physical Activity & Flexibility</h2>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/10.Physical%20Activity%20for%20People%20with%20Diabetes.pdf"/>"/>Physical Activity for People with Diabetes</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/20.Physical%20Activity%20for%20People%20with%20Hypertension.pdf"/>"/>Physical Activity for People with Hypertension</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/12.Community%20Programs%20for%20Fitness%20and%20Nutrition.pdf"/>"/>Community Programs for Fitness and Nutrition</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/27.Flexibility%20Exercises%20When%20Standing.pdf"/>"/>Flexibility Exercises When Standing</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/28.Flexibility%20Exercises%20When%20Sitting.pdf"/>"/>Flexibility Exercises When Sitting</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/29.Muscle%20Strengthening%20Activities.pdf"/>"/>Muscle Strengthening Activities</a></p>

<h2>Nutrition</h2>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/11.Nutrition%20for%20People%20with%20Diabetes.pdf"/>"/>Nutrition for People with Diabetes</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/18.DASH%20Diet.pdf"/>"/>DASH Diet</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/19.Why%20to%20Monitor%20Cholesterol.pdf"/>"/>Why to Monitor Cholesterol</a></p>


<h2>Sleep</h2>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/21.Good%20Sleep%20Habits%20-%20Tips%20for%20an%20Improved%20Sleep.pdf"/>"/>Good Sleep Habits - Tips for an Improved Sleep</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/22.Tips%20for%20Over%20the%20Counter%20Sleep%20Aids.pdf"/>"/>Tips for Over the Counter Sleep Aids</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/23.Relaxation%20Exercises%20for%20Falling%20Asleep.pdf"/>"/>Relaxation Exercises for Falling Asleep</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/24.When%20to%20See%20Your%20Doctor%20for%20Sleep-Related%20Issues.pdf"/>"/>When to See Your Doctor for Sleep-Related Issues</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/25.Nocturia%20or%20Frequent%20Urination%20at%20Night.pdf"/>"/>Nocturia or Frequent Urination at Night</a></p>

<h2>Other</h2>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/16.McMaster%20Optimal%20Aging%20Portal.pdf"/>"/>McMaster Optimal Aging Portal</a></p>


<!-- <p><a class="tips_links" href="<c:url value="/resources/tips_sheets/26.Fitbit%20101.pdf"/>"/>26.Fitbit 101</a></p>

<p><a class="tips_links" href="<c:url value="/resources/tips_sheets/32.Personal%20Health%20Record.pdf"/>"/>32.Personal Health Record User Manual</a></p>
 -->


</div>
</body>
</html>
