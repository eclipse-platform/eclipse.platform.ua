<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ page import="java.util.*,org.eclipse.help.servlet.*,org.eclipse.help.servlet.data.*" errorPage="/advanced/err.jsp" contentType="text/html; charset=UTF-8"%>
<script language="JavaScript">
function liveActionInternal(topHelpWindow, pluginId, className, argument)
{
<%
	RequestData data = new RequestData(application,request);
	if(data.getMode() == data.MODE_INFOCENTER){
%>
	alert("You must run help locally to perform active help actions.");
	return;
<%
	}else{
%>
	// construct the proper url for communicating with the server
	var url= window.location.href;
	var i = url.indexOf("content/help:");
	if(i < 0)
		i = url.lastIndexOf("/")+1;

	url=url.substring(0, i);
	url=url+"livehelp/?pluginID="+pluginId+"&class="+className+"&arg="+escape(argument)+"&nocaching="+Math.random();
	
	
	// to do

<%
	}
%>
}
function showTopicInContentsInternal(topHelpWindow, topic) {
}

</script>
