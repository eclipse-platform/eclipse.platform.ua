<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	TocData data = new TocData(application,request);
	WebappPreferences prefs = data.getPrefs();
	IToc selectedToc = data.getSelectedToc();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=WebappResources.getString("Content", request)%></title>

<style type="text/css">
<%@ include file="tree.css"%>
</style>  
    
<base target="ContentViewFrame">
<script language="JavaScript">

// Preload images
minus = new Image();
minus.src = "<%=prefs.getImagesDirectory()%>"+"/minus.gif";
plus = new Image();
plus.src = "<%=prefs.getImagesDirectory()%>"+"/plus.gif";

folder_img = new Image();
folder_img.src = "<%=prefs.getImagesDirectory()%>"+"/container_obj.gif";
topic_img = new Image();
topic_img.src = "<%=prefs.getImagesDirectory()%>"+"/topic.gif";
</script>

<script language="JavaScript" src="toc.js"></script>
<script language="JavaScript"> 
 
/**
 * Loads the specified table of contents
 */		
function loadTOC(tocHref)
{
	// navigate to this toc, if not already loaded
	if (window.location.href.indexOf("tocView.jsp?toc="+tocHref) != -1)
		return;
	window.location.replace("tocView.jsp?toc="+tocHref);
}

var tocTitle = "";
var tocId = "";
	
function onloadHandler()
{
<%
	if (data.getSelectedToc() != null)
	{
%>
	tocTitle = '<%=UrlUtil.JavaScriptEncode(data.getSelectedToc().getLabel())%>';
	
	// set title on the content toolbar
	parent.parent.parent.setContentToolbarTitle(tocTitle);
		
	// select specified topic, or else the book
	var topic = '<%=data.getSelectedTopic()%>';
	if (topic != "about:blank")
	{
		if (topic.indexOf(window.location.protocol) != 0)
			topic = window.location.protocol + "//" +window.location.host +"<%=request.getContextPath()%>" + "/"+ topic;
		selectTopic(topic);
	}
	else
		selectTopicById(tocId);

<%
	} else if ("yes".equals(request.getParameter("synch"))) {
%>
	alert('<%=UrlUtil.JavaScriptEncode(WebappResources.getString("CannotSync", request))%>');
	// when we don't find the specified toc, we just restore navigation
	parent.parent.parent.restoreNavigation();
<%
	}
%>
}
		
</script>
</head>


<body onload="onloadHandler()">
	<ul class='expanded' id='root'>
<%
	IToc[] tocs = data.getTocs();
	for (int i=0; i<tocs.length; i++) 
	{
%>
		<li>
		<nobr><img src="<%=prefs.getImagesDirectory()%>/toc_obj.gif"><a id="b<%=i%>" style="font-weight: bold;" href="<%=UrlUtil.getHelpURL(tocs[i].getTopic(null).getHref())%>" onclick='loadTOC("<%=tocs[i].getHref()%>")'><%=tocs[i].getLabel()%></a></nobr>
<%
		// Only generate the selected toc
		if (selectedToc != null &&
		    selectedToc.getHref().equals(tocs[i].getHref()))
		{
			data.generateToc(tocs[i], out);
			// keep track of the selected toc id
%>
			<script language="JavaScript">tocId="b"+<%=i%></script>
<%
		}
%>
		</li>	
<%
	}
%>		
	</ul>

</body>
</html>

