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

<frameset id="helpFrameset" cols="25%,*">
   <frame name="ViewsFrame" src='<%="view.jsp?view="+data.getVisibleView()+"&"+request.getQueryString()%>'>
	<frame name="ContentViewFrame" src='<%=data.getContentURL()%>'>
</frameset>

</html>

