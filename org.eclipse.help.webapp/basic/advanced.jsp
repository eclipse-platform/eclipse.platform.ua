<form action="searchView.jsp" method="get" accept-charset="UTF-8" target="_self">

	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<b>
				<%=WebappResources.getString("SearchExpression", request)%>
				</b>
			</td>
		</tr>
		<tr>
			<td nowrap>
				<input type="text" id="searchWord" name="searchWord" value='<%=data.getSearchWord()%>' maxlength=256 alt='<%=WebappResources.getString("SearchExpression", request)%>'>
          	  	<input type="hidden" name="maxHits" value="500" >
          	  	<input type="hidden" name="scopedSearch" value="true" >
				<input id="searchButton" type="submit" value='<%=WebappResources.getString("Search", request)%>' alt='<%=WebappResources.getString("Search", request)%>'>
        	</td>
        </tr>
        <tr>
        	<td>
        		<%=WebappResources.getString("expression_label", request)%>
        	</td>
        </tr>
		<tr>
			<td>
				<hr>
			</td>
		</tr>
    	<tr>
  			<td>
  				<b>
				<%=WebappResources.getString("Select", request)%>
				</b>
			</td>
		</tr>
  				
<% 
IToc[] tocs = data.getTocs();
for (int i=0; i<tocs.length; i++)
{
	String label = UrlUtil.htmlEncode(tocs[i].getLabel());
%>
  		<tr>
  			<td nowrap>
				<input type="checkbox" name='scope' value='<%=tocs[i].getHref()%>' alt="<%=label%>"><%=label%>
			</td>
		</tr>
<%
}		
%>
		<tr>
			<td>
				<hr>
			</td>
		</tr>
	</table>
 </form>
