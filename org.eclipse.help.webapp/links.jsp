<%@ page import="java.net.URLEncoder,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);
	
	LinksData linksData = new LinksData(application, request);
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<title><%=WebappResources.getString("Links", request)%></title>

<style type="text/css">
BODY {
	background-color: Window;
	font: icon;
	margin-top:5px;
	margin-left:5px;
	padding:0;
	border:0;
	cursor:default;
}

A {
	text-decoration:none; 
	color:WindowText; 
	padding:0px;
	white-space: nowrap;
}

A:hover {
	text-decoration:underline; 
}

IMG {
	border:0px;
	margin:0px;
	padding:0px;
	margin-right:4px;
}

TABLE {
	background-color: Window;
	font: icon;
	width:100%;
}

.list {
	background-color: Window;
	padding:2px;
}
     
.active { 
	background:ButtonFace;
	width:100%;
	height:100%;
}

.label {
	margin-left:4px;
}

</style>

<base target="ContentViewFrame">
<script language="JavaScript" src="list.js"></script>
<script language="JavaScript">		
var extraStyle = "";
if (isMozilla)
	extraStyle = "<style type='text/css'>.active, A.active:hover {background:WindowText;color:Window;} </style>";
 
document.write(extraStyle);
</script>

</head>


<body>
 
<%
if(!linksData.isLinksRequest()) {
	out.write(WebappResources.getString("pressF1", request));
} else if (linksData.getLinks().length == 0){
	out.write(WebappResources.getString("Nothing_found", null));
} else {
	Link[] links = linksData.getLinks();
%>

<table id='list'  cellspacing='0' >

<%
	for (int i = 0; i < links.length; i++) 
	{
%>

<tr class='list' id='r<%=i%>'>
	<td align='left' class='label' nowrap>
		<a id='a<%=i%>' 
		   href='<%=links[i].getHref()%>' 
		   onclick='parent.parent.setToolbarTitle("<%=UrlUtil.JavaScriptEncode(links[i].getTocLabel())%>")' 
		   title="<%=UrlUtil.htmlEncode(links[i].getTocLabel())%>">
		   <img src="images/topic.gif">
		   <%=UrlUtil.htmlEncode(links[i].getLabel())%>
		 </a>
	</td>
</tr>

<%
	}
%>

</table>

<%

}

%>

<script language="JavaScript">
	selectTopicById('<%=linksData.getSelectedTopicId()%>');
</script>

</body>
</html>
