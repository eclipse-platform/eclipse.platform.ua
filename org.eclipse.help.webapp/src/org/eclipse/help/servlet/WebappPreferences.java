package org.eclipse.help.servlet;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import javax.servlet.*;
import javax.servlet.http.*;
import org.w3c.dom.*;

/**
 * Uses a resource bundle to load images and strings from
 * a property file in a documentation plugin
 */
public class WebappPreferences {

	private String banner = null;
	private String banner_height = "45";
	private String help_home = null;
	private String bookmarksView = null;
	private String bookmarks = null;
	private String linksView = null;

	private ServletContext context;

	/**
	 * Resources constructort.
	 */
	protected WebappPreferences(ServletContext context) {
		this.context = context;
		loadPreferences();
	}

	public String getBanner() {
		return banner;
	}

	public String getBannerHeight() {
		return banner_height;
	}

	public String getHelpHome() {
		return help_home;
	}

	public boolean isBookmarksView() {
		return "true".equals(bookmarksView);
	}

	public boolean isLinksView() {
		return "true".equals(linksView);
	}

	/**
	 * Loads preferences 
	 */
	private void loadPreferences() {
		ContentUtil content = new ContentUtil(context, null);
		Element prefsElement = content.loadPreferences();

		if (prefsElement != null) {
			NodeList prefs = prefsElement.getElementsByTagName("pref");
			for (int i = 0; i < prefs.getLength(); i++) {
				Element pref = (Element) prefs.item(i);
				String name = pref.getAttribute("name");
				if (name.equals("banner"))
					banner = pref.getAttribute("value");
				else if (name.equals("banner_height"))
					banner_height = pref.getAttribute("value");
				else if (name.equals("help_home"))
					help_home = pref.getAttribute("value");
				else if (name.equals("bookmarksView"))
					bookmarksView = pref.getAttribute("value");
				else if (name.equals("bookmarks"))
					bookmarks = pref.getAttribute("value");
				else if (name.equals("linksView"))
					linksView = pref.getAttribute("value");
			}
		}
		if (banner != null) {
			if (banner.trim().length() == 0)
				banner = null;
			else
				banner = UrlUtil.getHelpURL(banner);
		}

	}
}