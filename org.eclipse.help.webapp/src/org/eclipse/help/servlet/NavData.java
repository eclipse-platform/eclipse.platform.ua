/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

/**
 * Helper class for nav.jsp initialization
 */
public class NavData {
	private ServletContext context;
	private HttpServletRequest request;
	private String query = "";
	private Element prefs;

	public NavData(ServletContext context, HttpServletRequest request) {
		this.context = context;
		this.request = request;
		
		if (request.getQueryString() != null && request.getQueryString().length() > 0)
			this.query = "?" + request.getQueryString();
		
		ContentUtil content = new ContentUtil(context, request);
		prefs = content.loadPreferences();
	}

	public String getContentsPageURL() {
		return "contents.jsp" + query;
	}
	
	public String getSearchResultsPageURL() {
		return "search_results.jsp" + query;
	}
		
	public String getLinksPageURL() {
		if ("true".equals(getPreference("linksView")))
			return "links.jsp" + query;
		else
			return null;
	}
	
	public String getBookmarksPageURL() {
		if ("true".equals(getPreference("bookmarksView")))
			return "bookmarks.jsp" + query;
		else
			return null;
	}

	private String getPreference(String prefName) {
			
		if (prefs != null) {
			NodeList prefsList = prefs.getElementsByTagName("pref");
			for (int i = 0; i < prefsList.getLength(); i++) {
				Element pref = (Element) prefsList.item(i);
				String name = pref.getAttribute("name");
				if (name.equals(prefName))
					return pref.getAttribute("value");
			}
		}
		return null;
	}
}