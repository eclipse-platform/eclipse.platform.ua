<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 	
	LinksData data = new LinksData(application, request);
	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<title><%=WebappResources.getString("Links", request)%></title>

<base target="ContentViewFrame">
</head>


<body bgcolor="#FFFFFF" text="#000000">
 
<%
if(!data.isLinksRequest()) {
	out.write(WebappResources.getString("pressF1", request));
} else if (data.getLinks().length == 0){
	out.write(WebappResources.getString("Nothing_found", null));
} else {
	Link[] links = data.getLinks();
%>

<table border="0" cellpadding="0" cellspacing="0">

<%
	for (int i = 0; i < links.length; i++) 
	{
%>

<tr id='r<%=i%>'>
	<td align='left' nowrap>
		<a id='a<%=i%>' 
		   href='<%=links[i].getHref()%>' 
		   title="<%=UrlUtil.htmlEncode(links[i].getTocLabel())%>">
		   <img src="<%=prefs.getImagesDirectory()%>/topic.gif" border=0>
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
</body>
</html>
