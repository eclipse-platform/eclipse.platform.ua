<%@ page import="java.util.*,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);

	BookmarksData bookmarksData = new BookmarksData(application,request);
	// see if anything is to be added
	bookmarksData.addBookmark();
	// see if anything is to be removd
	bookmarksData.removeBookmark();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<title><%=WebappResources.getString("Bookmarks", request)%></title>

<style type="text/css">
BODY {
	background-color: Window;
	font: icon;
	margin-top:5px;
	margin-left:5px;
	padding:0;
	border:0;
	cursor:default;
}

A {
	text-decoration:none; 
	color:WindowText; 
	padding:0px;
	white-space: nowrap;
}

A:hover {
	text-decoration:underline; 
}

IMG {
	border:0px;
	margin:0px;
	padding:0px;
	margin-right:4px;
}

TABLE {
	background-color: Window;
	font: icon;
	width:100%;
}

.list {
	background-color: Window;
	padding:2px;
}
     
.active { 
	background:ButtonFace;
	width:100%;
	height:100%;
}

.label {
	margin-left:4px;
}

#menu {
	position:absolute;
	display:none;
	background:ButtonFace;
	border:2px outset;
	padding:2px 0px;
}

#removeBookmark {
	padding-left:10px;
	padding-right:10px;
}

.selected {
	background:Highlight;
	color:HighlightText;
}

.unselected {
	background:ButtonFace;
	color:WindowText;
}

</style>

<base target="ContentViewFrame">

<script language="JavaScript" src="list.js"></script>

<script language="JavaScript">		
var extraStyle = "";
if (isMozilla)
	extraStyle = "<style type='text/css'>.active, A.active:hover {background:WindowText;color:Window;} </style>";
 
document.write(extraStyle);

 
// bookmark to remove
var bookmark;


/**
 * Removes bookmark 
 */
function removeBookmark() 
{
	if (!bookmark) 
		return;
		
	// Note: bookmark is an anchor "a"
	var url = bookmark.href;
	var i = url.indexOf("content/help:/");
	if (i >=0 )
		url = url.substring(i+13);
	// remove any query string
	i = url.indexOf("?");
	if (i >= 0)
		url = url.substring(0, i);
		
	var title = bookmark.title;
	if (title == null || title == "")
		title = url;
			
	bookmark = null;
	window.location.replace("bookmarks.jsp?remove="+url+"&title="+escape(title));
}


/**
 * Popup a menu on right click over a bookmark.
 * This handler assumes the list.js script has been loaded.
 */
function contextMenuHandler(e)
{
	// hide popup if open
	var menu = document.getElementById("menu");
	if (menu.style.display == "block")
		menu.style.display = "none";

	if (isIE)
		e = window.event;
		
  	var clickedNode;
  	if (isMozilla)
  		clickedNode = e.target;
  	else if (isIE)
   		clickedNode = e.srcElement;

  	if (!clickedNode)
  		return true;
  	
  	// call the click handler to select node
  	mouseClickHandler(e);
  	
  	if(clickedNode.tagName == "A")
  		bookmark = clickedNode;
  	else if (clickedNode.parentNode.tagName == "A")
  		bookmark = clickedNode.parentNode;
  	else
  		return true;
	
	// show the menu
	var x = 0;
	var y = 0;
	if (isIE) {
		x = window.event.clientX;
		y = window.event.clientY;
		window.event.cancelBubble = true;
	}
	else if (isMozilla) {
		x = e.clientX;
		y = e.clientY;
		e.cancelBubble = true;
	}
	
	menu.style.left = x+1;
	menu.style.top = y+1;
	menu.style.display = "block";
	
	return false;
}

</script>


</head>


<body>
 

<table id='list'  cellspacing='0' >

<%
	Topic[] bookmarks = bookmarksData.getBookmarks();
	for (int i=0; i<bookmarks.length; i++) 
	{
%>

<tr class='list' id='r<%=i%>'>
	<td align='left' class='label' nowrap>
		<a id='a<%=i%>' 
		   href='<%=bookmarks[i].getHref()%>' 
		   onclick='parent.parent.setToolbarTitle(" ")' 
		   oncontextmenu="contextMenuHandler(event);return false;"
		   title="<%=UrlUtil.htmlEncode(bookmarks[i].getLabel())%>">
		   <img src="images/bookmark_obj.gif">
		   		<%=UrlUtil.htmlEncode(bookmarks[i].getLabel())%>
		</a>
	</td>
</tr>

<%
	}
%>

</table>
<div id="menu">
	<div id="removeBookmark" class="unselected" onmouseover="this.className='selected'" onmouseout="this.className='unselected'" onclick="removeBookmark()"><%=WebappResources.getString("RemoveBookmark",request)%></div>
</div>

</body>
</html>
