package org.eclipse.help.servlet;

/*
 * (c) Copyright IBM Corp. 2002.
 * All Rights Reserved.
 */

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.core.runtime.*;
import org.eclipse.help.internal.*;

/**
 * This class calls eclipse API's directly, so it should only be
 * instantiated in the workbench scenario, not in the infocenter.
 */
public class BookmarksData {

	private HttpServletRequest req;
	private ServletContext context;

	public BookmarksData(ServletContext context, HttpServletRequest req) {
		this.context = context;
		this.req = req;
	}

	public void addBookmark() {
		String bookmarkURL = req.getParameter("add");
		if (bookmarkURL != null && bookmarkURL.length() > 0) {
			String title = UrlUtil.getRequestParameter(req, "title");
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);
			// separate the url and title by vertical bar
			bookmarks = bookmarks + "," + bookmarkURL + "|" + title;
			prefs.setValue(HelpSystem.BOOKMARKS, bookmarks);
			HelpPlugin.getDefault().savePluginPreferences();
		}
	}

	public void removeBookmark() {
		String bookmarkURL = req.getParameter("remove");
		if (bookmarkURL != null && bookmarkURL.length() > 0) {
			String title = UrlUtil.getRequestParameter(req, "title");
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);
			String removeString = "," + bookmarkURL + "|" + title;
			int i = bookmarks.indexOf(removeString);
			if (i == -1)
				return;
			bookmarks =
				bookmarks.substring(0, i)
					+ bookmarks.substring(i + removeString.length());
			prefs.setValue(HelpSystem.BOOKMARKS, bookmarks);
			HelpPlugin.getDefault().savePluginPreferences();
		}
	}

	public Topic[] getBookmarks() {
		// sanity test for infocenter, but this could not work anyway...
		if (context.getAttribute("org.eclipse.help.servlet.eclipse") == null) {
			// this is workbench
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);
			StringTokenizer tokenizer = new StringTokenizer(bookmarks, ",");
			Topic[] topics = new Topic[tokenizer.countTokens()];
			for (int i = 0; tokenizer.hasMoreTokens(); i++) {
				String bookmark = tokenizer.nextToken();
				// url and title are separated by vertical bar
				int separator = bookmark.indexOf('|');

				String label = bookmark.substring(separator + 1);
				String href =
					separator < 0 ? "" : bookmark.substring(0, separator);
				topics[i] = new Topic(label, href);
			}
			return topics;
		}
		return new Topic[0];
	}
}
