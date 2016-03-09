<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
			overflow-x:auto;
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;
		}

	</style>
</head>
	
<body>	<!-- 
  <img src="<c:url value="/resources/images/logo.png"/>" />  -->
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<div class="span12" style="padding:0px 15px;">
				<ul class="breadcrumb">
					<li><h2><a href="<c:url value="/inbox"/>">Inbox</a> <span class="divider">/</span> </h2></li>
					<li><h2><a href="#">${message.subject}</a></h2></li>
				</ul>
				<div class="well">
					<p>From: ${message.sender}</p>
					<p>${message.text}</p>
					<a href="<c:url value="/delete_message/${message.messageID}?isRead=true"/>" class="btn btn-danger">Delete</a>
					<button onclick="document.getElementById('replyForm').style.display='block'" class="btn btn-primary">Reply</a></button>
				</div>
			</div>
		</div>
	</div>


	<div id="replyForm" style="display:none;" class="span12">
		<form id="reply" action="<c:url value="/reply_to/${message.messageID}"/>" method="post">
			<label>Message:</label>
			<textarea name="msgBody"></textarea><br/>
			<input type="submit" class="btn btn-primary" value="Send"/>
		</form>
	</div>
</body>
</html>
