package org.eclipse.help.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import org.w3c.dom.*;

/*
 * (c) Copyright IBM Corp. 2002.
 * All Rights Reserved.
 */

public class LayoutData {

	private String query = "";
	private WebappPreferences prefs;

	private HttpServletRequest req;
	private ServletContext context;

	public LayoutData(ServletContext context, HttpServletRequest req) {
		this.context = context;
		this.req = req;

		prefs = (WebappPreferences) context.getAttribute("WebappPreferences");

		// initialize the query string
		String qs = req.getQueryString();
		if (qs != null && qs.length() > 0)
			query = "?" + qs;
	}

	public String getQuery() {
		return query;
	}

	public String getBannerURL() {
		String banner = prefs.getBanner();
		if (banner != null) {
			if (banner.trim().length() == 0)
				banner = null;
			else
				banner = UrlUtil.getHelpURL(banner);
		}
		return banner;
	}

	public String getBannerHeight() {
		return prefs.getBannerHeight();
	}

	public String getContentURL() {
		ContentsData contents = new ContentsData(context, req);
		String topic = contents.getSelectedTopic();
		String help_home = prefs.getHelpHome();
		
		if (topic != null)
			help_home = topic;
		else
			help_home = UrlUtil.getHelpURL(help_home);
			
		return help_home;
	}
	
	/**
	 * Return array of length 0 if no views
	 */
	public View[] getViews() {
		View[] views = new View[] {
			new View("content", "contents.jsp", "images/contents_view.gif", true),
			new View("search", "search_results.jsp", "images/search_results_view.gif", false),
			new View("links", "links.jsp", "images/links_view.gif", false),
			new View("bookmarks", "bookmarks.jsp", "images/bookmarks_view.gif", false)
		};
		return views;
	}

	public String getVisibleView() {
		String view = req.getParameter("tab");
		if (view != null && view.length() > 0)
			return view;
		else
			return "content";
	}
}
