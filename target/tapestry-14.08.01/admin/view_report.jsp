<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Tapestry Admin -- Report</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		
		h2 span{
			background-color: black;
			font-weight: bold;
			text-align:center;
			color: white;
			width:1200px;
			}		
			
			
		table{
			border-collapse: collapse;
				width: 100%;
		}
		
		table, td, th
		{		 
		   border-bottom:1pt solid black;
		}
		
		table.summary_tools_whole{
			border-bottom:1pt solid black;
			border-style: solid;
		}
		
		table.functional_status{
			border-style: solid;
    		border-left-width: 15px;
		}
		
		
	</style>
</head>
<body>

	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
		<H2>Patient Information</H2><br>
		<table class ="patient_info">
			<tr>
				<td><label>Patient:</label> ${patient.firstName} ${patient.lastName}</td>
				<td><label>Address:</label> ${patient.address }</td>			
			</tr>
			<tr>
				<td><label>MRP:</label></td>
				<td><label>Date of visit:</label> ${appointment.date }</td>				
			</tr>
			<tr>
				<td><label>Time: </label> ${appointment.time }</td>
				<td><label>Visit: </label> ${appointment.strType }</td>				
			</tr>
		</table>
		
			
			
			<hr/>
			
			<H1><b>TAPESTRY REPORT:  ${patient.firstName} ${patient.lastName} ( ${patient.bod }) </b></H1><br/>
			<div id ="patientGoals">
				<h2 align="center"><span>PATIENT GOAL(S)</span></h2><br>
				<table>
					<c:forEach items="${report.healthGoals}" var="h">					
							<tr>						
								<td valign = "top" width="15"> ${h.key}</td>
								<td>${h.value}</td>
							</tr>
						</c:forEach>
				</table>	
			</div>
			<h2 align="center"><span>ALERTS: Consider Case Review with IP-TEAM</span></h2><br>
			<table>
				<c:forEach items="${report.alerts}" var="a">
					<tr>
						<td ><h3>${a}</h3></td>						
					</tr>
				</c:forEach>
			</table>
			
			<h2 align="center"><span>KEY OBSERVATIONS </span></h2><br>
			<div class="col-md-10">		
				<input type="textarea" class="form-control" maxlength="50" name="keyObservation" value="${appointment.keyObservation }"/>
			</div>
			<br/><hr/>
			
			<h2 align="center"><span>PLAN</span></h2><br>
			<table border="1">
				<c:forEach items="${plans}" var="p">
					<tr>
						<td width="30">${p.key}</td>
						<td>${p.value}</td>
					</tr>
				</c:forEach>
			</table>
			<br/><hr/>
		</div>
		
		<h2 align="center"><span>ADDITIONAL INFORMATION</span></h2><br>
		<div id="additional_infos">
			<table border="1">
				<c:forEach items="${report.additionalInfos}" var="a">					
						<tr>
							<td valign = "top" width="930"> ${a.key}</td>
							<td align = "center">${a.value}</td>
						</tr>
						
					</c:forEach>
			</table>	
		</div><br/>
		<div id="summary_tools">
			<table class="summary_tools_whole" width="1050">
				<tr>
					<td colspan="3" align="center"><h2>Summary of TAPESTRY Tools</h2></td>
				</tr>
				<tr>
					<td align="center" width="300">DOMAIN</td>
					<td align="center" width="350">SCORE</td>
					<td align="center" width="400">DESCRIPTION</td>
				</tr>
				<tr>
					<td><h3>Functional Status</h3></td>
					<td><table class="functional_status">
							<tr><td>clock drawing test: ${scores.clockDrawingTest}</td></tr>
							<tr><td>Timed up-and -go test scroe = ${scores.timeUpGoTest} </td></tr>
							<tr><td>Edmonton Frail Scale score = ${scores.edmontonFrailScale}</td></tr>
						</table>
					</td>
					<td><table border="0" width="350">
							<tr><td>Edmonton Frail Scale (Score Key):</td></tr>
							<tr><td>Robust: 0-4 </td></tr>
							<tr><td>Apparently Vulneravle: 5-6</td></tr>
							<tr><td>Frail: 7-17</td></tr>
						</table>
					</td>					
				</tr>
				<tr>
					<td><h3>Nutritional Status</h3></td>
					<td>Screen II scroe = ${scores.nutritionScreen} </td>
					<td><table border="0" width="400">
							<tr><td>Screen II Nutrition Screening Tool :</td></tr>
							<tr><td>Max Score = 64 </td></tr>
							<tr><td>High Risk < 50</td></tr>							
						</table>
					</td>					
				</tr>
				<tr>
					<td><h3>Social Supports</h3></td>
					<td><table border="0" width="350">
							<tr><td>Satisfaction score = ${scores.socialSatisfication}</td></tr>
							<tr><td>Network scroe = ${scores.socialNetwork}</td></tr>							
						</table>
					</td>
					<td><table border="0" width="400">
							<tr><td>Satisfaction score range: 6-18</td></tr>
							<tr><td>(Score < 10 risk cut off)</td></tr>
							<tr><td>Received satisfaction with behavioural or</td></tr>
							<tr><td>emotional support obtained from this network</td></tr>
							<tr><td>Network score range 4-12:</td></tr>
							<tr><td>Size and structure of social network</td></tr>
						</table>
					</td>					
				</tr>
				<tr>
					<td><table width="300">
							<tr><td><h3>Mobility</h3></td></tr>
							<tr><td>Walking 2.0 km</td></tr>
							<tr><td>Walking 0.5 km </td></tr>
							<tr><td>Climbing Starirs </td></tr>
						</table></td>
					<td><table border="1" width="350">
							<tr><td>&nbsp</td></tr>
							<tr><td>${scores.mobilityWalking2}</td></tr>
							<tr><td>${scores.mobilityWalkingHalf}</td></tr>
							<tr><td>${scores.mobilityClimbing} </td></tr>
						</table>
					</td>
					<td><table border="0" width="400">
							<tr><td>MANTY:</td></tr>
							<tr><td>No Limitation </td></tr>
							<tr><td>Preclinical Limitation</td></tr>
							<tr><td>Minor Manifest Limitation</td></tr>
							<tr><td>Major Manifest Limitation</td></tr>
						</table>
					</td>					
				</tr>
				<tr>
					<td><h3>Physical Activity</h3></td>
					<td>Score = ${scores.physicalActivity}</td>
					<td><table border="0" width="50">							
							<tr><td>Rapid Assessment of Physical Activity(RAPA)</td></tr>
							<tr><td>Score range: 1--7</td></tr>
							<tr><td>Score < 6 Suboptimal Activity(Aerobic)</td></tr>
						</table>
					</td>					
				</tr>
			</table>
		</div>	
			
		<h2 align="center"><span>TAPESTRY QUESTIONS </span></h2><br>		
		<table >
			<c:forEach items="${report.dailyActivities}" var="d">					
					<tr>
						<td valign = "top" width="30"> ${d.key}</td>
						<td>${d.value}</td>
					</tr>
					
				</c:forEach>
		</table>
		<h2 align="center"><span>VOLUNTEER INFORMATION & NOTES </span></h2><br>	
		<table border="1">
			<c:forEach items="${report.volunteerInformations}" var="v">					
					<tr>
						<td valign = "top" width="30"> ${v.key}</td>
						<td>${v.value}</td>
					</tr>
					
				</c:forEach>
		</table>
	</div>
</body>
</html>