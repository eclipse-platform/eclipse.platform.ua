<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	LayoutData data = new LayoutData(application,request);
	WebappPreferences prefs = data.getPrefs();
	View[] views = data.getViews();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=WebappResources.getString("Tabs", request)%></title>
    
<base target="ViewsFrame">
</head>
   
<body bgcolor="#D4D0C8" text="#000000" link="#0000FF" vlink="#0000FF" alink="#0000FF">
<b>
<%
	for (int i=0; i<views.length; i++) 
	{
		String title = WebappResources.getString(views[i].getName(), request);
%>
	     <a  href='<%="view.jsp?view="+views[i].getName()%>' 
	         <img alt="<%=title%>" 
	              title="<%=title%>" 
	              src="<%=views[i].getImageURL()%>"
	         >
	     <%=title%>
	     </a>
	     &nbsp;
<%
	}
%>
</b>
</body>
</html>

