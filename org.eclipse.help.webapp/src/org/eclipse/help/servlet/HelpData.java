package org.eclipse.help.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import org.w3c.dom.*;

/*
 * (c) Copyright IBM Corp. 2002.
 * All Rights Reserved.
 */

public class HelpData {

	private String query = "";
	private WebappPreferences prefs;

	private HttpServletRequest req;
	private ServletContext context;

	public HelpData(ServletContext context, HttpServletRequest req) {
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
}
