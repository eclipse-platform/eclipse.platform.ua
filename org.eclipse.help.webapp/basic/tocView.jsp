<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	TocData data = new TocData(application,request);
	WebappPreferences prefs = data.getPrefs();
	Element selectedToc = data.getSelectedToc();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=WebappResources.getString("Content", request)%></title>

<base target="ContentViewFrame">
</head>


<body >
<%
	Element[] tocs = data.getTocs();
	for (int i=0; i<tocs.length; i++) 
	{
%>
		<b><nobr><img src="<%=prefs.getImagesDirectory()%>/toc_obj.gif"><a id="b<%=i%>" href="<%="tocView.jsp?toc="+data.getTocHref(tocs[i])%>" target='_self'><%=data.getTocLabel(tocs[i])%></a></nobr></b>
<%
		// Only generate the selected toc
		if (selectedToc != null &&
		    selectedToc.getAttribute("href").equals(tocs[i].getAttribute("href")))
		{
%>		
	<ul>
<%
			data.generateBasicToc(tocs[i], out);
			// keep track of the selected toc id
%>		
	</ul>
<%
		}
	}
%>		

</body>
</html>

