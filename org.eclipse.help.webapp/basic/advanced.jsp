<form action="searchView.jsp" method="get" accept-charset="UTF-8" target="_self">
	<b>
		<%=WebappResources.getString("SearchExpression", request)%>
	</b>
	<br>
				<input type="text" id="searchWord" name="searchWord" value='<%=data.getSearchWord()%>' maxlength=256 alt='<%=WebappResources.getString("SearchExpression", request)%>'>
          	  	<input type="hidden" name="maxHits" value="500" >
				<input id="searchButton" type="submit" value='<%=WebappResources.getString("Search", request)%>' alt='<%=WebappResources.getString("Search", request)%>'>
	<br>
        		<%=WebappResources.getString("expression_label", request)%>
    <br>
  	<br>
    <b>
				<%=WebappResources.getString("Select", request)%>
    </b>
    <br>
<% 
Element[] tocs = data.getTocs();
for (int i=0; i<tocs.length; i++)
{
	String label = UrlUtil.htmlEncode(tocs[i].getAttribute("label"));
%>
				<input type="checkbox" name='<%=tocs[i].getAttribute("href")%>' alt="<%=label%>"><%=label%>
			<br>
<%
}		
%>
 </form>
