<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
<title>Rearch Data Download</title>
</head>
<body>
<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Research Data Download</h2>
			<h5>You can download de-identified Research Data. Click the Download Data button below</h5>
		</div>
		
		<div>
			<table>
				<th width="500">	Site/Organization	</th>
				<th>	Download			</th>
				<c:forEach items="${sites}" var="s">
					<tr>
						<td>${s.name }</td>
					 	<!-- td><a href="<c:url value="/download_research_data/${s.siteId}?name=${s.name}"/>">Download Data</a> </td>	 -->
					 	<td><a href="<c:url value="/download_researchData/${s.siteId}?name=${s.name}"/>">Download Data</a> </td>
					</tr>
				</c:forEach>
			</table>
		</div>
<!-- 	
		<table class="table">
			<tr>
				<th>PId</th>
				<th>EQ5D1</th>
				<th>EQ5D2</th>
				<th>EQ5D3</th>
				<th>EQ5D4</th>
				<th>EQ5D5</th>
				<th>EQ5D5-notes</th>
				<th>EQ5D6</th>
				<th>EQ5D6-notes</th>
				<th>DSS1</th>
				<th>DSS2</th>
				<th>DSS3</th>
				<th>DSS4</th>
				<th>DSS5</th>
				<th>DSS6</th>
				<th>DSS7</th>
				<th>DSS8</th>
				<th>DSS9</th>
				<th>DSS10</th>
				<th>DSS11</th>	
				<th>DSS_notes</th>				
				<th>Goals1</th>	
				<th>Goals2</th>	
				<th>Goals3</th>	
				<th>Goals4</th>	
				<th>Goals51</th>	
				<th>Goals52</th>	
				<th>Goals53</th>	
				<th>Goals61</th>	
				<th>Goals62</th>	
				<th>Goals63</th>	
				<th>Goals71</th>	
				<th>Goals72</th>	
				<th>Goals73</th>	
				<th>Goals8</th>	
				<th>Goals_notes</th>	
							
			</tr>
			<c:forEach items="${surveyDatas}" var="sd">
				<tr>
					<td>${sd.patientId}</td>
					<td>${sd.eQ5D1_Mobility_TO}</td>
					<td>${sd.eQ5D2_2Selfcare_TO}</td>
					<td>${sd.eQ5D3_Usualact_TO}</td>
					<td>${sd.eQ5D4_Pain_TO}</td>
					<td>${sd.eQ5D5_Anxdep_TO}</td>
					<td>${sd.eQ5D5_notes_TO}</td>
					<td>${sd.eQ5D6_Healthstate_TO}</td>
					<td>${sd.eQ5D6_Healthstate_notes_TO}</td>
					<td>${sd.dSS1_role_TO}</td>
					<td>${sd.dSS2_under_TO}</td>
					<td>${sd.dSS3_useful_TO}</td>
					<td>${sd.dSS4_listen_TO}</td>
					<td>${sd.dSS5_happen_TO}</td>
					<td>${sd.dSS6_talk_TO}</td>
					<td>${sd.dSS7_satisfied_TO}</td>
					<td>${sd.dSS8_nofam_TO}</td>
					<td>${sd.dSS9_timesnotliving_TO}</td>
					<td>${sd.dSS10_timesphone_TO}</td>
					<td>${sd.dSS1_role_TO}</td>
					<td>${sd.dSS_notes_TO}</td>					
					<td>${sd.goals1Matter_TO}</td>
					<td>${sd.goals2Life_TO}</td>
					<td>${sd.goals3Health_TO}</td>
					<td>${sd.goals4List_TO}</td>
					<td>${sd.goals5FirstSpecific_TO}</td>
					<td>${sd.goals6FirstBaseline_TO}</td>
					<td>${sd.goals7FirstTaget_TO}</td>
					<td>${sd.goals5SecondSpecific_TO}</td>
					<td>${sd.goals6SecondBaseline_TO}</td>
					<td>${sd.goals7SecondTaget_TO}</td>
					<td>${sd.goals5ThirdSpecific_TO}</td>
					<td>${sd.goals6ThirdBaseline_TO}</td>
					<td>${sd.goals7ThirdTaget_TO}</td>
					<td>${sd.goals8pPriority_TO}</td>
					<td>${sd.goalsDiscussion_notes_TO}</td>
				</tr>
			</c:forEach>
		</table>
		
		<a href="<c:url value="/download_research_data"/>" class="btn btn-primary">Download Data</a> -->	
</div>		

</body>
</html>