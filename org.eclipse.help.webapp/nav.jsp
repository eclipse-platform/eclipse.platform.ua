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

<script language="Javascript">

var titleArray = new Array ();
<%
	View[] views = layout.getViews();
	for (int i=0; i<views.length; i++) 
	{	
%>
	titleArray['<%=views[i].getName()%>'] = '<%=WebappResources.getString(views[i].getName(), request)%>';
<%
	}
%>

var tocTitle = null;
var lastTab = "";


/* 
 * Switch tabs.
 */ 
function switchTab(nav, newTitle)
{ 	
	if (nav == lastTab) 
		return;
		
	lastTab = nav;
	
	// set the title on the navigation toolbar to match the tab
  	if (newTitle)
     	ToolbarFrame.document.getElementById("titleText").innerHTML = newTitle;
    else
    	ToolbarFrame.document.getElementById("titleText").innerHTML = titleArray[nav];
       	
	// show appropriate frame
 	var iframes = ViewsFrame.document.body.getElementsByTagName("IFRAME");
 	for (var i=0; i<iframes.length; i++)
 	{			
  		if (iframes[i].id != nav)
   			iframes[i].className = "hidden";
  		else
   			iframes[i].className = "visible";
 	}
 
 	// show the appropriate pressed tab
  	var buttons = TabsFrame.document.body.getElementsByTagName("TD");
  	for (var i=0; i<buttons.length; i++)
  	{
  		if (buttons[i].id == nav) // Note: assumes the same id shared by tabs and iframes
			buttons[i].className = "pressed";
		else if (buttons[i].className == "pressed")
			buttons[i].className = "tab";
 	 }
}
</script>

</head>

<frameset id="navFrameset" rows="24,*,24"  framespacing="0" border="0"  frameborder="0" spacing="0"  scrolling="no">
   <frame name="ToolbarFrame" src='<%="navToolbar.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
   <frame name="ViewsFrame" src='<%="views.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
   <frame name="TabsFrame" src='<%="tabs.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
</frameset>

</html>