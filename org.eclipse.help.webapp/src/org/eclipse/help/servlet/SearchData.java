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
 * Helper class for search_results.jsp initialization
 */
public class SearchData extends RequestData{

	// Request parameters
	private String topicHref;

	// List of TOC's
	private Element[] tocs;

	/**
	 * Constructs the xml data for the contents page.
	 * @param context
	 * @param request
	 */
	public SearchData(ServletContext context, HttpServletRequest request) {
		super(context, request);
		this.topicHref = request.getParameter("topic");
		if (topicHref != null && topicHref.length() == 0)
			topicHref = null;
	}

	/**
	 * Returns true when there is a search request	 * @return boolean	 */
	public boolean isSearchRequest() {
		return (
			request.getParameter("searchWord") != null
				|| request.getParameter("searchWordJS13") != null);
	}

	/**
	 * Returns the topic to display.
	 * If there is a TOC, return its topic description.
	 * Return null if no topic is specified and there is no toc description.
	 * @return String
	 */
	public String getSelectedTopic() {
		if (topicHref != null && topicHref.length() > 0)
			return UrlUtil.getHelpURL(topicHref);
		else {
			Element toc = getSelectedToc();
			if (toc == null)
				return null;
			String tocDescription = toc.getAttribute("topic");
			return UrlUtil.getHelpURL(tocDescription);
		}
	}

}