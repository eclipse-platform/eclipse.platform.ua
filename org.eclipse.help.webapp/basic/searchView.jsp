<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>
<%@ page import="org.eclipse.help.internal.search.*"%>

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
<%
if (data.isProgressRequest()) {
%>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<%=WebappResources.getString("Indexing", request)%>
		</td>
	</tr>
	<tr>
		<td>
			<%=data.getIndexedPercentage()%>% <%=WebappResources.getString("complete", request)%>
		</td>
	</tr>
	<tr>
		<td>
			<br>
			<%=WebappResources.getString("IndexingPleaseWait", request)%>
		</td>
	</tr>
</TABLE>
</body>
</html>
<%
	return;
} else {
%>
	<%@ include file="advanced.jsp"%>
<%
	if (data.isSearchRequest()) {
		if (data.getHits().length == 0){
			out.write(WebappResources.getString("Nothing_found", request));
		} else {	
			SearchHit[] hits = data.getHits();
%>

<table border="0" cellpadding="0" cellspacing="0">

<%
		for (int i = 0; i < hits.length; i++) 
		{
			String title = hits[i].getToc() != null ? hits[i].getToc().getLabel() : "";
%>

<tr id='r<%=i%>'>
	<td align='right'><%=data.getFormattedScore(hits[i])%></td>
	<td align='left' nowrap>
	   &nbsp;
		<a id='a<%=i%>' 
		   href='<%=UrlUtil.getHelpURL(hits[i].getHref())%>' 
		   title="<%=UrlUtil.htmlEncode(title)%>">
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
	}
}
%>
</body>
</html>

