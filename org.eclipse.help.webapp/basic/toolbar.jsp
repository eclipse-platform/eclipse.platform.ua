<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	ToolbarData data = new ToolbarData(application,request);
	WebappPreferences prefs = data.getPrefs();
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=WebappResources.getString("Toolbar", request)%></title>
</head>
 
<body bgcolor="#D4D0C8" text="#000000">
	<b><%=data.getTitle()%></b>
    <iframe name="liveHelpFrame" frameborder="no" width="0" height="0" scrolling="no">
    </iframe>

</body>     
</html>

