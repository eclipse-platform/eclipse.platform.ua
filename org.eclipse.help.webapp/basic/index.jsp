<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	LayoutData data = new LayoutData(application,request);
%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=WebappResources.getString("Help", request)%></title>
</head>

<frameset rows="44,*">
   <frame name="TabsFrame" src='<%="basic/tabs.jsp"+data.getQuery()%>'>
	<frame name="HelpFrame" src='<%="basic/help.jsp"+data.getQuery()%>'>
</frameset>

</html>

