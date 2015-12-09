<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Tapestry</title>
  		<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
    <%@include file="volunteer/volunteer_head.jsp" %>
    <link rel="shortcut icon" href="/tapestry/resources/images/favicon.ico" />
    <link rel="apple-touch-icon" sizes="144x144" href="/tapestry/resources/images/apple-touch-icon-144x144.png">

<!--     <link rel="apple-touch-icon" sizes="57x57" href="favicons/apple-touch-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="favicons/apple-touch-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="favicons/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="favicons/apple-touch-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="favicons/apple-touch-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="favicons/apple-touch-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="favicons/apple-touch-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="favicons/apple-touch-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="favicons/apple-touch-icon-180x180.png">
    <link rel="icon" type="image/png" href="favicons/favicon-32x32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="favicons/android-chrome-192x192.png" sizes="192x192">
    <link rel="icon" type="image/png" href="favicons/favicon-96x96.png" sizes="96x96">
    <link rel="icon" type="image/png" href="favicons/favicon-16x16.png" sizes="16x16">
    <link rel="manifest" href="favicons/manifest.json">
    <link rel="mask-icon" href="favicons/safari-pinned-tab.svg" color="#5bbad5">
    <meta name="msapplication-TileColor" content="#da532c">
    <meta name="msapplication-TileImage" content="favicons/mstile-144x144.png">
    <meta name="theme-color" content="#ffffff"> -->

    <style type="text/css">
    body {
      background-color: #444;
      padding-right: 0px;
      padding-left: 0px;
    }
/*      #footer {
        position: absolute;
        bottom: 0;
        background-color: #444;
        width: 100%;
        padding: 30px;
        height:100px;
        border-top: 5px solid white;
        }*/
        
        #content {
          background-color: #4A307A;
          padding-top:20px;
          border-bottom: 
        }

        #headerholder {
          margin-bottom: 5px;
          } 

        .smallicon {
          display: inline;
          background-color: #F7D600;
          /* height: 70px; */
          color:#4A307A;
          padding: 26px 25px 21px 25px;
          font-size: 20px;
        }

        #logo {
          float: none;
          margin: 0 auto;
        }

        .tpurple {
          border-right: none;
          text-align: center;
          padding-top: 5px;
          border-bottom: 5px solid white;
        }

        h2 {
          color:white;
          font-family: Open Sans;
          font-weight: 200;
        }

        .form-horizontal {
          width: 70%;
          margin:0 auto;
          text-align: center;
        }

        /*Mobile Phone Custom CSS*/
        @media only screen and (min-device-width : 320px) and (max-device-width : 568px) and (orientation : portrait) { 
          .form-horizontal {
            width:100%;
          }

          .smallicon {
            padding: 21px 4px 26px 10px;
          }

          #logininput {
            font-size: 15px;
          }
          #loginbtn {
            height: 50px;
            width:100%;
          }
          h2 {
            font-size: 20px;
          }

         }

    </style>
 	</head>
 	<body>
  <div id="headerholder"> 
    <div class="row">
      <div class="col-md-12 tpurple logoheightlogin">
        <img id="logo" src="<c:url value="/resources/images/logow.png"/>" />
      </div>

<!--     <div class="col-md-9 tblack" style="height:63px;">
 -->
    </div>
  </div>


    <div id="content">
  	<!-- 	<img id="logo" src="${pageContext.request.contextPath}/resources/images/logo.png" />
        <img id="logofam" src="${pageContext.request.contextPath}/resources/images/fammed.png"/> 
        <img id="logofhs" src="${pageContext.request.contextPath}/resources/images/fhs.png"/>
        <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>-->
   			<div class="row">
          <div class="col-md-12">
					 <c:if test="${not empty error}">
					<div class="alert alert-danger">Login failed <br /> Caused :
							${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
          </div>
					</c:if>
					<c:if test="${not empty usernameChanged}">
					<div class="alert alert-info">Your username has changed. Please log in again using the new credentials.
          </div>
					</c:if>
        </div>
      </div>

    <div>
      <form class="form-horizontal" action="<c:url value="j_spring_security_check" />" method="POST">
          <div class="col-md-12">
            <h2>ENTER YOUR USERNAME and PASSWORD TO LOGIN TO THE APP</h2>
          </div>

          <br>

        <div class="row">
            <div class="col-md-12">
              <div class="smallicon">
                <span class="glyphicon glyphicon-user"></span>
              </div>
              <input id="logininput" type="text" name="j_username" placeholder="Your Username" />
            </div>
        </div>
        <br>

        <div class="row">
          <div class="col-md-12">
            <div class="smallicon">
              <span class="glyphicon glyphicon-lock"></span>
            </div>
            <input id="logininput" type="password" name="j_password" placeholder="Your Password" />
          </div>
        </div>
        <br>

        <div class="row">
          <div class="col-md-12">
          <input id="loginbtn" type="submit" value="LOGIN" style="margin-bottom:10px;"></input>
          </div>
        </div>
      </form>
  </div>
  <div class="row" id="footer">
    <div class="col-md-12">
        <img id="logofhs" src="${pageContext.request.contextPath}/resources/images/fhswhite.png"/>
        <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degrootewhite.png"/>
    </div>
  </div>
</div>



<!--      				<div class="area">
	      				<form class="form-horizontal" action="<c:url value="j_spring_security_check" />" method="POST">
       						<div class="heading">
        						<h4 class="form-heading">Sign In Please</h4>
       						</div>
       						<div class="control-group">
	        					<label class="control-label" for="username">Username</label>
        						<div class="controls">
	         						<input id="logininput" type="text" name="j_username" placeholder="username" />
        						</div>
       						</div>
       						<div class="control-group">
								<label class="control-label" for="password">Password</label>
        						<div class="controls">
	         						<input id="logininput" type="password" name="j_password" placeholder="password" />
        						</div>
       						</div>
       						<div class="control-group">
	        					<div class="controls">
									<input id="logininput" type="submit" value="Login" style="margin-bottom:10px;"></input>
									<p>Tapestry 13.11.14</p>
        						</div>
       						</div>
      					</form>
     				</div> -->
          
 	</body>
</html>
