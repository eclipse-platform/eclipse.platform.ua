<%@ page import="java.util.*,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);

	LayoutData layout = new LayoutData(application,request);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=WebappResources.getString("Help", request)%></title>
</head>

<frameset id="helpFrameset" cols="25%,*"  framespacing="1" border="1"  frameborder="1" spacing="1"  scrolling="no">
   <frame name="NavFrame" src='<%="nav.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
   <frame name="ContentFrame" src='<%="content.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
</frameset>

</html>

