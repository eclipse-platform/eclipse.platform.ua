<%@ page import="java.util.*,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);

	BookmarksData bookmarksData = new BookmarksData(application,request);
	// see if anything is to be added
	bookmarksData.addBookmark();
	// see if anything is to be removd
	bookmarksData.removeBookmark();
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

<title><%=WebappResources.getString("Bookmarks", request)%></title>

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
 

<table id='list'  cellspacing='0' >

<%
	Topic[] bookmarks = bookmarksData.getBookmarks();
	for (int i=0; i<bookmarks.length; i++) 
	{
%>

<tr class='list' id='r<%=i%>'>
	<td align='left' class='label' nowrap>
		<a id='a<%=i%>' href='<%=bookmarks[i].getHref()%>' onclick='parent.parent.setToolbarTitle(" ")' title="<%=UrlUtil.htmlEncode(bookmarks[i].getLabel())%>"><img src="images/bookmark_obj.gif"><%=UrlUtil.htmlEncode(bookmarks[i].getLabel())%></a>
	</td>
</tr>

<%
	}
%>

</table>

<%

%>

</body>
</html>
