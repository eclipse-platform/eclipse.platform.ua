<%@ page import="java.util.*,org.eclipse.help.servlet.*"  contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);

	LayoutData layout = new LayoutData(application,request);
	View[] views = layout.getViews();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=WebappResources.getString("Tabs", request)%></title>
    
<style type="text/css">
 
BODY {
	margin:0px;
	padding:0px;
	background:WindowText;
	height:100%;
}

/* tabs at the bottom */
.tab {
	background:ButtonFace;
	margin:0px;
	padding:0px;
 	border-top:1px solid WindowText;
	cursor:default;
	align:center;
}

.pressed {
	background:Window;
	margin:0px;
	padding:0px;
	border-top:1px Window solid;
	cursor:default;
	align:center;
}

.separator {
	margin:0px;
	padding:0px;
	border:0px;
	height:100%;
	/*background-color:ThreeDShadow;*/
	background-color:WindowText;
}

A {
	text-decoration:none;
	margin:0px;
	padding:0px;
	border:0px;
	align:center;
}

IMG {
	border:0px;
	margin:0px;
	padding:0px;
	align:center;
}
</style>
 
<script language="JavaScript">

var isMozilla = navigator.userAgent.indexOf('Mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var extraStyle = "";
if (isMozilla)
 	extraStyle = "<style type='text/css'>BODY { height:21px;} </style>";
document.write(extraStyle);

var lastTab = "";
/* 
 * Switch tabs.
 */ 
function showTab(tab)
{ 	
	if (tab == lastTab) 
		return;
		
	lastTab = tab;
	
 	// show the appropriate pressed tab
  	var buttons = document.body.getElementsByTagName("TD");
  	for (var i=0; i<buttons.length; i++)
  	{
  		if (buttons[i].id == tab) 
			buttons[i].className = "pressed";
		else if (buttons[i].className == "pressed")
			buttons[i].className = "tab";
 	 }
}
</script>

</head>
   
<body>

  <table cellspacing="0" cellpadding="0" border="0" width="100%" height="100%">
   <tr>
<%
	for (int i=0; i<views.length; i++) 
	{
		String title = WebappResources.getString(views[i].getName(), request);
%>
	<td  title="<%=views[i].getName()%>" 
	     align="center"  
	     class="tab" 
	     id="<%=views[i].getName()%>" 
	     onclick="parent.showView('<%=views[i].getName()%>')" 
	     onmouseover="window.status='<%=views[i].getName()%>';return true;" 
	     onmouseout="window.status='';">
	     <a  href='javascript:parent.showView("<%=views[i].getName()%>");' 
	         onclick='this.blur()' 
	         onmouseover="window.status='<%=title%>';return true;" 
	         onmouseout="window.status='';">
	         <img alt="<%=title%>" 
	              title="<%=title%>" 
	              src="<%=views[i].getImageURL()%>"
	         >
	     </a>
	</td>
    <td width="1px" class="separator"></td>
<%
	}
%>
 
   </tr>
   </table>

</body>
</html>

