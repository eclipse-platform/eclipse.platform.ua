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

<base target="ContentViewFrame">
</head>


<body bgcolor="#FFFFFF" text="#000000">
<%
	IToc[] tocs = data.getTocs();
	for (int i=0; i<tocs.length; i++) 
	{
%>
		<b><nobr><img src="<%=prefs.getImagesDirectory()%>/toc_obj.gif"><a id="b<%=i%>" href="<%="tocView.jsp?toc="+tocs[i].getHref()%>" target='_self'>&nbsp;<%=tocs[i].getLabel()%></a></nobr></b>
<%
		// Only generate the selected toc
		if (selectedToc != null &&
		    selectedToc.getHref().equals(tocs[i].getHref()))
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

