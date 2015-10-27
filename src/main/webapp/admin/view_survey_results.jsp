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
	
<body>	<!-- 
  <img src="<c:url value="/resources/images/logo.png"/>" /> -->
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">			
			<h2>Survey Results: ${results[0].title}</h2>
			<h4>Completed on: ${results[0].date}</h4>
			<div id="srvData" >
			<table class="table">
				<tr>
					<td width="20"><b>Id</b></td>
					<td width="350"><b>Question</b></td>
					<td width="300"><b>Answer</b></td>
					<td width="200"><b>Observer Notes</b></td>
				</tr>				
				<c:forEach items="${results}" var="result">	
					
					<tr>
						<td width="20">${result.questionId}</td>
						<td width="350">${result.questionText}</td>
						<td width="300">${result.questionAnswer}</td>
						<td width="200">${result.observerNotes}</td>
					</tr>
					
				</c:forEach>
			</table>
			<!-- <a href="<c:url value="/export_csv/${id}"/>" class="btn btn-primary">Export as CSV</a> -->
			<a href="#" class="export">Export Table data into Excel</a>


		</div>
	</div>


	<script type="text/javascript">
$(document).ready(function () {

    function exportTableToCSV($table, filename) {

        var $rows = $table.find('tr:has(td)'),

            // Temporary delimiter characters unlikely to be typed by keyboard
            // This is to avoid accidentally splitting the actual contents
            tmpColDelim = String.fromCharCode(11), // vertical tab character
            tmpRowDelim = String.fromCharCode(0), // null character

            // actual delimiter characters for CSV format
            colDelim = '","',
            rowDelim = '"\r\n"',

            // Grab text from table into CSV formatted string
            csv = '"' + $rows.map(function (i, row) {
                var $row = $(row),
                    $cols = $row.find('td');

                return $cols.map(function (j, col) {
                    var $col = $(col),
                        text = $col.text();

                    return text.replace(/"/g, '""'); // escape double quotes

                }).get().join(tmpColDelim);

            }).get().join(tmpRowDelim)
                .split(tmpRowDelim).join(rowDelim)
                .split(tmpColDelim).join(colDelim) + '"',

            // Data URI
            csvData = 'data:application/csv;charset=utf-8,' + encodeURIComponent(csv);

        $(this)
            .attr({
            'download': filename,
                'href': csvData,
                'target': '_blank'
        });
    }

    // This must be a hyperlink
    $(".export").on('click', function (event) {
        // CSV
        exportTableToCSV.apply(this, [$('#srvData>table'), 'export.csv']);
        
        // IF CSV, don't do event.preventDefault() or return false
        // We actually need this to be a typical hyperlink
    });
});


	</script>
</body>
</html>
