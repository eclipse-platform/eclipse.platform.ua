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

<style type="text/css">
<%@ include file="list.css"%>
</style>

<base target="ContentViewFrame">
<script language="JavaScript" src="list.js"></script>

</head>


<body>
 
<%
if(!data.isLinksRequest()) {
	out.write(WebappResources.getString("pressF1", request));
} else if (data.getLinks().length == 0){
	out.write(WebappResources.getString("Nothing_found", null));
} else {
	Link[] links = data.getLinks();
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
		   onclick='parent.parent.setContentToolbarTitle("<%=UrlUtil.JavaScriptEncode(links[i].getTocLabel())%>")' 
		   title="<%=UrlUtil.htmlEncode(links[i].getTocLabel())%>">
		   <img src="<%=prefs.getImagesDirectory()%>/topic.gif">
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
	selectTopicById('<%=data.getSelectedTopicId()%>');
</script>

</body>
</html>
