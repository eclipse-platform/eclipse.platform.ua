<%@ page import="java.util.*,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);

	HelpData data = new HelpData(application,request);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->

<html>
<head>
		
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Help</title>

<script language="Javascript">
	// Identifier
	var EclipseHelpSystem = true;
	
	// Global var for the webapp address
	var contextPath = "<%=request.getContextPath()%>";
	
	// Global for the nav frame script
	var titleArray = new Array();
	titleArray["toc"] = '<%=WebappResources.getString("Content", request)%>';
	titleArray["search"] = '<%=WebappResources.getString("SearchResults", request)%>';
	titleArray["links"] = '<%=WebappResources.getString("Links", request)%>';
	titleArray["bookmarks"] = '<%=WebappResources.getString("Bookmarks", request)%>';
	
	// Global for the help home page
	var help_home = "<%=data.getContentURL()%>";
	
</script>

<script language="JavaScript" src="help.js"></script>
	
</head>

<frameset onload="onloadFrameset()"  rows="<%=data.getBannerURL()!=null?data.getBannerHeight()+",":""%>24,*"  frameborder="0" framespacing="0" border=0 spacing=0 style="border:1px solid WindowText;">
<%
	if (data.getBannerURL() != null){
%>
	<frame name="BannerFrame" src='<%=data.getBannerURL()%>'  marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
<%
	}
%>
	<frame name="SearchFrame" src='<%="search.jsp"+data.getQuery()%>'  marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
	<frameset id="helpFrameset" cols="25%,*"  framespacing="1" border="1"  frameborder="1" spacing="1"  scrolling="no">
		<frameset name="navFrameset" rows="24,*,24" marginwidth="0" marginheight="0" frameborder="0" framespacing=0 scrolling="no" >
		        <frame name="NavToolbarFrame" src='<%="navToolbar.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
		        <frame name="NavFrame" tabindex="1" src='<%="nav.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
		        <frame name="TabsFrame" src='<%="tabs.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
		</frameset>
        <frameset id="contentFrameset" rows="24,*", frameborder=0 framespacing=0 border=0 scrolling="no">
        	<frame name="ToolbarFrame" src='<%="toolbar.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
             <frame name="MainFrame" tabindex="2" src="<%=data.getContentURL()%>" marginwidth="10" marginheight="10" scrolling="auto"  frameborder="0" resize="yes">
        </frameset>
     </frameset>
 </frameset>

</html>

