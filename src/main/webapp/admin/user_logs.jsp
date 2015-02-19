<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
	
	<script type="text/javascript">
		function printTable(){
			$('.table').printThis();
		}
	</script>
</head>
	
<body>	
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>User Log <button class="btn btn-primary pull-right" onclick="printTable()" style="margin-right:25px;">Print</button></h2>
			<div class="row-fluid">
				<form action="user_logs" method="post">
					<fieldset>
					<!--  <legend>Search Logs</legend>-->	
						<label>Name:</label>
						<input type="text" name="name" required/>
						<input class="btn btn-primary" type="submit" value="Search" />
					</fieldset>
				</form>
			</div>

			<div class="input-group"> <span class="input-group-addon">Filter</span>

			    <input id="filter" type="text" class="form-control" placeholder="Type here...">
			</div>

			<table class="table">
				<thead>
					<tr>
						<th>User Name</th>
						<th>Activity</th>
						<th>Date</th>
					</tr>
				<thead>
				<tbody class="searchable">
				<c:forEach items="${logs}" var="l">
					<tr>
						<td>${l.userName}</td>
						<td>${l.description}</td>
						<td>${l.date}</td>
					</tr>
				</c:forEach>
				<tbody>
			</table>
			<div class="pagination text-center">
				<ul class="pagination">
					<c:forEach begin="1" end="${numPages}" var="i">
					<li><a href="<c:url value="/user_logs/${i}"/>">${i}</a></li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>

	<script type="text/javascript">
$(document).ready(function () {

    (function ($) {

        $('#filter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('.searchable tr').hide();
            $('.searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        })

    }(jQuery));

});

	</script>
</body>
</html>
