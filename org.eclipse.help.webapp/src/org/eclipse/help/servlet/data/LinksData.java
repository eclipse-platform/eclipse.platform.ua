/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet.data;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.help.servlet.ContentUtil;
import org.w3c.dom.*;

/**
 * Helper class for search_results.jsp initialization
 */
public class LinksData extends RequestData {

	// Request parameters
	private String topicHref;
	private String selectedTopicId = "";

	// search results
	Element linksElement;

	// list of search results
	Link[] links;

	/**
	 * Constructs the xml data for the search resuls page.
	 * @param context
	 * @param request
	 */
	public LinksData(ServletContext context, HttpServletRequest request) {
		super(context, request);
		this.topicHref = request.getParameter("topic");
		if (topicHref != null && topicHref.length() == 0)
			topicHref = null;

		linksElement = loadLinks();
		links = getLinks();
	}

	/**
	 * Returns true when there is a search request
	 * @return boolean
	 */
	public boolean isLinksRequest() {
		return (request.getParameter("contextId")!=null);
	}

	private Element loadLinks() {
		ContentUtil content = new ContentUtil(context, request);
		return content.loadLinks(request.getQueryString());
	}

	public Link[] getLinks() {
		if (links != null)
			return links;
			
		// Generate results list
		if (linksElement == null)
			links = new Link[0];
		else if (!linksElement.getTagName().equals("toc"))
			links = new Link[0];
		else {
			NodeList topics = linksElement.getElementsByTagName("topic");
			links = new Link[topics.getLength()];
			for (int i = 0; i < topics.getLength(); i++) {
				Element topic = (Element) topics.item(i);
				
				// the following assume topic numbering as in search_results.jsp
				if (topic.getAttribute("href").equals(topicHref))
					selectedTopicId = "a"+i;
					
				// obtain document score
				String scoreString = topic.getAttribute("score");
				try {
					float score = Float.parseFloat(scoreString);
					NumberFormat percentFormat =
						NumberFormat.getPercentInstance(request.getLocale());
					scoreString = percentFormat.format(score);
				} catch (NumberFormatException nfe) {
					// will display original score string
				}

				links[i] =
					new Link(
						topic.getAttribute("label"),
						topic.getAttribute("href"),
						topic.getAttribute("toc"),
						topic.getAttribute("toclabel"));

			}
		}
		return links;
	}
	
	public String getSelectedTopicId() {
		return selectedTopicId;
	}
}