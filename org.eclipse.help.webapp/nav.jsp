<%@ page import="java.net.URLEncoder,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Navigation</title>

<style type="text/css">

/* need this one for Mozilla */
HTML { 
	width:100%;
	height:100%;
	margin:0px;
	padding:0px;
	border:0px;
 }

BODY {
	margin:0px;
	padding:0px;
	border-right:1px solid WindowText;
	/* Mozilla does not like width:100%, so we set height only */
	height:100%;
}

IFRAME {
	width:100%;
	height:100%;
}

.hidden {
	visibility:hidden;
	width:0;
	height:0;
}

.visible {
	visibility:visible;
	width:100%;
	height:100%;
}

</style>

</head>
   
<body>
<%
	NavData nav = new NavData(application, request);
%>
 <iframe src='<%=nav.getContentsPageURL()%>' frameborder="0" class="hidden"  name="toc" id="toc" ></iframe> 
 <iframe src='<%=nav.getSearchResultsPageURL()%>' frameborder="0"  class="hidden"  name="search" id="search" ></iframe> 
<% 
	if (nav.getLinksPageURL() != null) {
%>
 <iframe src='<%=nav.getLinksPageURL()%>' frameborder="0" class="hidden" name="links" id="links"></iframe>
<%
	}
	if (nav.getBookmarksPageURL() != null) {
%>
 <iframe src='<%=nav.getBookmarksPageURL()%>' frameborder="0" class="hidden" name="bookmarks" id="bookmarks"></iframe>
<%
	}
%>
 <iframe frameborder="0" class="hidden" name="temp" id="temp"></iframe>
</body>
</html>

