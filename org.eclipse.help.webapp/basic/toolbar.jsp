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
 
<body>
	&nbsp;<%=data.getTitle()%>
    <iframe name="liveHelpFrame" frameborder="no" width="0" height="0" scrolling="no">
    </iframe>

</body>     
</html>

