<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	SearchData data = new SearchData(application, request);
	WebappPreferences prefs = data.getPrefs();
	LayoutData ldata = new LayoutData(application,request);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<%
if (data.isProgressRequest()) {
%>
 <meta HTTP-EQUIV="REFRESH" CONTENT="2;
 URL=<%="searchView.jsp?"+request.getQueryString()%>">
<%
}
%>

<title><%=WebappResources.getString("SearchResults", request)%></title>

<base target="ContentViewFrame">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<%@ include file="advanced.jsp"%>

<%
if (data.isProgressRequest()) {
%>

<%=WebappResources.getString("Indexing", request)%>
</body>
</html>

<%
	return;
} else if (data.getHits().length == 0){
	out.write(WebappResources.getString("Nothing_found", request));
} else {
		
	Hit[] hits = data.getHits();
%>

<table id='list'  cellspacing='0' >

<%
	for (int i = 0; i < hits.length; i++) 
	{
%>

<tr id='r<%=i%>'>
	<td align='right'><%=hits[i].getScore()%></td>
	<td align='left' nowrap>
		<a id='a<%=i%>' 
		   href='<%=hits[i].getHref()%>' 
		   title="<%=UrlUtil.htmlEncode(hits[i].getTocLabel())%>">
		   <%=UrlUtil.htmlEncode(hits[i].getLabel())%>
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
</body>
</html>

