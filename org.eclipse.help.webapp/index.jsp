<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%
	String agent=request.getHeader("User-Agent").toLowerCase();
	boolean opera = (agent.toLowerCase().indexOf("opera")>=0);
	boolean ie   = (agent.indexOf("msie") != -1)&&!opera;
	boolean mozilla  = (!ie && (agent.indexOf("mozilla/5")!=-1))&&!opera;

	if(ie || mozilla){
		request.getRequestDispatcher("/advanced/index.jsp").forward(request, response);
	}else{
		request.getRequestDispatcher("/basic/index.jsp").forward(request, response);
	}
%>
