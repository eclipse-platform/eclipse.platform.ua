<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	LayoutData data = new LayoutData(application,request);
	WebappPreferences prefs = data.getPrefs();
	View[] views = data.getViews();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=WebappResources.getString("Tabs", request)%></title>
    
<base target="ViewsFrame">
</head>
   
<body>

  <table>
   <tr>
<%
	for (int i=0; i<views.length; i++) 
	{
		String title = WebappResources.getString(views[i].getName(), request);
%>
	<td  title="<%=views[i].getName()%>" 
	     align="center"  
	     id="<%=views[i].getName()%>"
	     > 
	     <a  href='<%="view.jsp?view="+views[i].getName()%>' 
	         <img alt="<%=title%>" 
	              title="<%=title%>" 
	              src="<%=views[i].getImageURL()%>"
	         >
	     <%=title%>
	     </a>
	</td>
<%
	}
%>
 
   </tr>
   </table>

</body>
</html>

