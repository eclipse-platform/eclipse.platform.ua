<%@ page import="java.net.URLEncoder,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);
	
	LayoutData layout = new LayoutData(application,request);
	View[] views = layout.getViews();
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

<script language="Javascript">

var titleArray = new Array ();
<%
	for (int i=0; i<views.length; i++) 
	{	
%>
	titleArray['<%=views[i].getName()%>'] = '<%=WebappResources.getString(views[i].getName(), request)%>';
<%
	}
%>


var lastView = "";
/**
 * Switches to specified view
 */
function showView(view)
{ 	
	if (view == lastView) 
		return;
		
	lastView = view;
	
	// set the title on the navigation toolbar to match the tab
  	parent.ToolbarFrame.document.getElementById("titleText").innerHTML = titleArray[view];
       	
	// show appropriate frame
 	var iframes = parent.ViewsFrame.document.body.getElementsByTagName("IFRAME");
 	for (var i=0; i<iframes.length; i++)
 	{			
  		if (iframes[i].id != view)
   			iframes[i].className = "hidden";
  		else
   			iframes[i].className = "visible";
 	}
}
</script>

</head>
   
<body>
<%
	for (int i=0; i<views.length; i++) 
	{
		String className = layout.getVisibleView().equals(views[i].getName()) ? "visible" : "hidden";
%>
 	<iframe frameborder="0" 
 		    class="<%=className%>"  
 		    name="<%=views[i].getName()%>" 
 		    id="<%=views[i].getName()%>" 
 		    src="<%=views[i].getURL()+layout.getQuery()%>" >
 	</iframe> 
<%
	}
%>	

 <iframe frameborder="0" class="hidden" name="temp" id="temp"></iframe>
 
</body>
</html>

