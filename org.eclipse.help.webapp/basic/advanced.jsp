<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	SearchData data = new SearchData(application, request);
	WebappPreferences prefs = data.getPrefs();
%>


<html>
<head>
<title><%=WebappResources.getString("Advanced", request)%></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

</head>

<body>

<form name="searchForm">
	<table width="100%" cellspacing=0 cellpading=0 border=0 align=center >
		<tr><td><%=WebappResources.getString("SearchExpression", request)%>
		</td></tr>
		<tr><td><input type="text" id="searchWord" name="searchWord" value='<%=data.getSearchWord()%>' maxlength=256 alt='<%=WebappResources.getString("SearchExpression", request)%>'>
          	  	<input type="hidden" name="maxHits" value="500" >
        </td></tr>
        <tr><td><%=WebappResources.getString("expression_label", request)%>
        </td></tr>
    </table>
  
  	<table width="100%" cellspacing=0 cellpading=0 border=0 align=center>
		<tr><td><%=WebappResources.getString("Select", request)%>
		</td></tr>
		<tr><td>
<% 
Element[] tocs = data.getTocs();
for (int i=0; i<tocs.length; i++)
{
	String label = UrlUtil.htmlEncode(tocs[i].getAttribute("label"));
%>
				<input type="checkbox" name='<%=tocs[i].getAttribute("href")%>' alt="<%=label%>"><%=label%>
<%
}		
%>
		</td></tr>
	</table>
	<table valign="bottom" align="right">
		<tr id="buttonsTable" valign="bottom"><td valign="bottom" align="right">
  			<table cellspacing=10 cellpading=0 border=0 align=right>
				<tr>
					<td>
						<input id="searchButton"  type="button" value='<%=WebappResources.getString("Search", request)%>' alt='<%=WebappResources.getString("Search", request)%>'>
					</td>
					<td>
					  	<input type="button"  type="button"  value='<%=WebappResources.getString("Cancel", request)%>' alt='<%=WebappResources.getString("Cancel", request)%>'>
					</td>
				</tr>
  			</table>
		</td></tr>
	</table>
 </form>

</body>
</html>
