<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	BookmarksData data = new BookmarksData(application,request);
	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<title><%=WebappResources.getString("Bookmarks", request)%></title>

<base target="ContentViewFrame">

</head>


<body bgcolor="#FFFFFF" text="#000000">
 
<table id='list'  cellspacing='0' >

<%
	Topic[] bookmarks = data.getBookmarks();
	for (int i=0; i<bookmarks.length; i++) 
	{
%>

<tr id='r<%=i%>'>
	<td align='left' nowrap>
		<a id='a<%=i%>' 
		   href='<%=bookmarks[i].getHref()%>' 
		   title="<%=UrlUtil.htmlEncode(bookmarks[i].getLabel())%>">
		   <img src="<%=prefs.getImagesDirectory()%>/bookmark_obj.gif">
		   		<%=UrlUtil.htmlEncode(bookmarks[i].getLabel())%>
		</a>
	</td>
</tr>

<%
	}
%>

</table>
</body>
</html>
