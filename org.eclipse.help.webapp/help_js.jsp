<%@ page import="org.eclipse.help.servlet.*" errorPage="err.jsp" contentType="text/js; charset=UTF-8"%>

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
 
var isMozilla = navigator.userAgent.indexOf('Mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var isMozilla10 = isMozilla && navigator.userAgent.indexOf('rv:1') != -1;
var isIE = navigator.userAgent.indexOf('MSIE') != -1;

// Identifier
var EclipseHelpSystem = true;

// Global var for the webapp address
var contextPath = "<%=request.getContextPath()%>";

var framesLoaded = false;
var args = parseQueryString();

var temp;
var tempActive;
var tempTab = "";

/**
 * Notification when frames are loaded
 */
function onloadFrameset()
{
	framesLoaded = true;
}

    
/**
 * Parses the parameters passed to the url
 */
function parseQueryString (str) 
{
    str = str ? str : window.location.href;
    var longquery = str.split("?");
    if (longquery.length <= 1) return "";
    var query = longquery[1];
    var args = new Object();
    if (query) 
    {
        var fields = query.split('&');
        for (var f = 0; f < fields.length; f++) 
        {
            var field = fields[f].split('=');
            args[unescape(field[0].replace(/\+/g, ' '))] = unescape(field[1].replace(/\+/g, ' '));
        }
    }
    return args;
}

