<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	LayoutData data = new LayoutData(application,request);
	WebappPreferences prefs = data.getPrefs();
%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=WebappResources.getString("Help", request)%></title>
<script language="JavaScript">
var isMozilla = navigator.userAgent.indexOf('Mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var isMozilla10 = isMozilla && navigator.userAgent.indexOf('rv:1') != -1;
var isIE = navigator.userAgent.indexOf('MSIE') != -1;

/**
 * Views can call this to set the title on the content toolbar
 */
function setContentToolbarTitle(title)
{
	if(parent.ContentFrame.ToolbarFrame && parent.ContentFrame.ToolbarFrame.setTitle ){
		parent.ContentFrame.ToolbarFrame.setTitle(title);
	}
}

/**
 * Views can call this to set the title on the navigation toolbar
 */
function setNavToolbarTitle(title)
{
	if(ToolbarFrame && ToolbarFrame.setTitle ){
		ToolbarFrame.setTitle(title);
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
	showView("toc");

	var selected = false;
	/**** HARD CODED VIEW NAME *********/
	if (ViewsFrame.toc.selectTopic)
		selected = ViewsFrame.toc.selectTopic(topic);

	if (!selected) {
		// save the current navigation, so we can retrieve it when synch does not work
		saveNavigation();
		// we are using the full URL because this API is exposed to clients
		// (content page may want to autosynchronize)
		var tocURL = window.location.protocol + "//" +window.location.host  + "<%=request.getContextPath()%>" + "/toc.jsp";
		ViewsFrame.toc.location.replace(tocURL + "?topic="+topic+"&synch=yes");			
	}
}

function saveNavigation()
{
	/**** HARD CODED VIEW NAME *********/
	if (ViewsFrame.toc.location.href.indexOf("tocs.jsp") == -1) {
					
		if (ViewsFrame.toc.oldActive) {
			tempActive = ViewsFrame.toc.oldActive;
			tempActiveClass = ViewsFrame.toc.oldActiveClass;
		}
		// on mozilla, we will not preserve selection, the object is no longer valid.
		// in the future, we could look up the topic, but this should suffice for now
		// Note: need newer mozilla version
		if (isMozilla){
			tempActive.className ="";
			tempActive=null;
		}
			
		if (isIE)
			temp = ViewsFrame.toc.document.body.innerHTML;
		else if (isMozilla)
			temp = ViewsFrame.toc.getElementById("toc").contentDocument.documentElement.innerHTML;
	} else {
		temp = null;
	}
}

function restoreNavigation()
{	
	// turn to the right tab
	var oldTab = tempView;
	
	showView(tempView);

	/**** HARD CODED VIEW NAME *********/	
	if (temp && (isIE || isMozilla10)){
		// Restore old navigation
		if (isIE)
			ViewsFrame.toc.document.body.innerHTML = temp;
		else if (isMozilla10)
			ViewsFrame.toc.document.getElementById("toc").contentDocument.documentElement.innerHTML = temp;
		
		if (tempActive) {
			ViewsFrame.toc.oldActive = tempActive;
			ViewsFrame.toc.oldActiveClass = tempActiveClass;
		}
	}else {
		// Show bookshelf
		ViewsFrame.toc.location.replace("toc.jsp");
	}
}

</script>
</head>

<frameset onload="showView('<%=data.getVisibleView()%>')" id="navFrameset" rows="*,24"  framespacing="0" border="0"  frameborder="0" spacing="0"  scrolling="no">
   <frame name="ViewsFrame" src='<%="views.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
   <frame name="TabsFrame" src='<%="tabs.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
</frameset>

</html>