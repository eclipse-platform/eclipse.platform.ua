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
<script language="JavaScript">
var isMozilla = navigator.userAgent.indexOf('Mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var isMozilla10 = isMozilla && navigator.userAgent.indexOf('rv:1') != -1;
var isIE = navigator.userAgent.indexOf('MSIE') != -1;

/**
 * Views can call this to set the title on the main toolbar
 */
function setToolbarTitle(title)
{
	if(parent.DisplayFrame.ToolbarFrame){
		parent.DisplayFrame.ToolbarFrame.setTitle(title);
	}
}

/**
 * Shows specified view. Called from actions that switch the view */
function showView(view)
{
	// Note: assumes the same id shared by tabs and iframes
	ViewsFrame.showView(view);
	TabsFrame.showTab(view);
}

var temp;
var tempActive;
var tempActiveClass;
var tempView = "";

/**
 * Shows the TOC frame, loads appropriate TOC, and selects the topic
 */
function displayTocFor(topic)
{
	tempView = ViewsFrame.lastView;
	/******** HARD CODED VIEW NAME *********/
	showView("content");
	// remove the query, if any
	var i = topic.indexOf('?');
	if (i != -1)
		topic = topic.substring(0, i);

	var selected = false;
	/**** HARD CODED VIEW NAME *********/
	if (ViewsFrame.content.selectTopic)
		selected = ViewsFrame.content.selectTopic(topic);

	if (!selected) {
		// save the current navigation, so we can retrieve it when synch does not work
		saveNavigation();
		// we are using the full URL because this API is exposed to clients
		// (content page may want to autosynchronize)
		var tocURL = window.location.protocol + "//" +window.location.host  + "<%=request.getContextPath()%>" + "/contents.jsp";
		ViewsFrame.content.location.replace(tocURL + "?topic="+topic+"&synch=yes");			
	}
}

function saveNavigation()
{
	/**** HARD CODED VIEW NAME *********/
	if (ViewsFrame.content.location.href.indexOf("tocs.jsp") == -1) {
					
		if (ViewsFrame.content.oldActive) {
			tempActive = ViewsFrame.content.oldActive;
			tempActiveClass = ViewsFrame.content.oldActiveClass;
		}
		// on mozilla, we will not preserve selection, the object is no longer valid.
		// in the future, we could look up the topic, but this should suffice for now
		// Note: need newer mozilla version
		if (isMozilla){
			tempActive.className ="";
			tempActive=null;
		}
			
		if (isIE)
			temp = ViewsFrame.content.document.body.innerHTML;
		else if (isMozilla)
			temp = ViewsFrame.content.getElementById("toc").contentDocument.documentElement.innerHTML;
	} else {
		temp = null;
	}
}

function restoreNavigation()
{	
	// turn to the right tab
	var oldTab = tempTab;
	
	showView(tempTab);

	/**** HARD CODED VIEW NAME *********/	
	if (temp && (isIE || isMozilla10)){
		// Restore old navigation
		if (isIE)
			ViewsFrame.content.document.body.innerHTML = temp;
		else if (isMozilla10)
			ViewsFrame.content.document.getElementById("toc").contentDocument.documentElement.innerHTML = temp;
		
		if (tempActive) {
			ViewsFrame.content.oldActive = tempActive;
			ViewsFrame.content.oldActiveClass = tempActiveClass;
		}
	}else {
		// Show bookshelf
		ViewsFrame.content.location.replace("contents.jsp");
	}
}

</script>
</head>

<frameset onload="showView('<%=layout.getVisibleView()%>')" id="navFrameset" rows="24,*,24"  framespacing="0" border="0"  frameborder="0" spacing="0"  scrolling="no">
   <frame name="ToolbarFrame" src='<%="navToolbar.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
   <frame name="ViewsFrame" src='<%="views.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
   <frame name="TabsFrame" src='<%="tabs.jsp"+layout.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
</frameset>

</html>