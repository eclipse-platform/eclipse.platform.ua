<%@ page import="java.net.URLEncoder,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=WebappResources.getString("Content", request)%></title>

<style type="text/css">

BODY {
	background-color: Window;
	font: icon;
	margin:0;
	padding:0;
	border:0;
}

UL { 
	border-width:0; 
	margin-left:20px; 
}

#root {
	margin-top:5px;
	margin-left:5px;
}
  
UL.expanded {
	display:block; 
}

UL.collapsed { 
	display: none;
}

LI { 
	margin-top:3px; 
	list-style-image:none;
	list-style-type:none;
}

IMG {
	border:0px;
	margin:0px;
	padding:0px;
	margin-right:4px;
}


A {
	text-decoration:none; 
	color:WindowText;
	padding-right:2px;
	/* this works in ie5.5, but not in ie5.0  */
	white-space: nowrap;
}

A:hover{
	text-decoration:underline; 
}

A.active{ 
	background:ButtonFace;
	width:100%;
}

A.active:hover{
	text-decoration:underline; 
	background:ButtonFace;
	width:100%;
}
  
   
</style>  
    
<base target="ContentFrame">
<script language="JavaScript" src="toc.js"></script>
 
<script language="JavaScript"> 

if (isMozilla)
 	document.write("<style type='text/css'>UL { margin-left:-20px;} #root{ margin-left:-35px; margin-top:5px;} A.active, A.active:hover {background:WindowText;color:Window;} </style>");
 
 
/**
 * Loads the specified table of contents
 */		
function loadTOC(tocId)
{
	// navigate to this toc, if not already loaded
	if (window.location.href.indexOf("contents.jsp?toc="+tocId) != -1)
		return;
	window.location.replace("contents.jsp?toc="+tocId);
}

</script>

</head>


<body>
	<ul class='expanded' id='root'>
<%
	ContentsData contents = new ContentsData(application, request);
	Element selectedToc = contents.getSelectedToc();
	String id = "";
	Element[] tocs = contents.getTocs();
	for (int i=0; i<tocs.length; i++) 
	{
%>
		<li>
		<nobr><img src="images/toc_obj.gif"><a id="b<%=i%>" style="font-weight: bold;" href="<%=contents.getTocDescriptionTopic(tocs[i])%>" onclick='loadTOC("<%=contents.getTocHref(tocs[i])%>")'><%=contents.getTocLabel(tocs[i])%></a></nobr>
<%
		// Only generate the selected toc
		if (selectedToc == null)
			continue;
		if (!selectedToc.getAttribute("href").equals(tocs[i].getAttribute("href")))
			continue;
		contents.generateToc(tocs[i], out);
		// keep track of the selected toc id
		id = "b"+i;
%>
		</li>	
<%
	}
%>		

	</ul>

<%
	if (contents.getSelectedToc() != null)
	{
%>
<script language="JavaScript">

	// set title on the content toolbar
	parent.parent.setToolbarTitle('<%=UrlUtil.JavaScriptEncode(contents.getTocLabel(contents.getSelectedToc()))%>');
		
	// select specified topic, or else the book
	var topic = '<%=contents.getSelectedTopic()%>';
	if (topic != "about:blank")
	{
		if (topic.indexOf(window.location.protocol) != 0)
			topic = window.location.protocol + "//" +window.location.host +"<%=request.getContextPath()%>" + "/"+ topic;
		selectTopic(topic);
	}
	else
		selectTopicById("<%=id%>");

</script>

<%
	}
%>
</body>
</html>

