/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

/**
 * Helper class for search_results.jsp initialization
 */
public class SearchData extends RequestData {

	// Request parameters
	private String topicHref;
	private String selectedTopicId = "";

	// search results
	Element resultsElement;

	// list of search results
	Hit[] hits;

	/**
	 * Constructs the xml data for the search resuls page.
	 * @param context
	 * @param request
	 */
	public SearchData(ServletContext context, HttpServletRequest request) {
		super(context, request);
		this.topicHref = request.getParameter("topic");
		if (topicHref != null && topicHref.length() == 0)
			topicHref = null;

		resultsElement = loadSearchResults();
		hits = getHits();
	}

	/**
	 * Returns true when there is a search request
	 * @return boolean
	 */
	public boolean isSearchRequest() {
		return (
			request.getParameter("searchWord") != null
				|| request.getParameter("searchWordJS13") != null);
	}

	/**
	 * Return indexed completion percentage
	 */
	public boolean isProgressRequest() {
		if (resultsElement == null)
			return false; 
		else 
			return (resultsElement.getTagName().equals("progress"));
	}

	/**
	 * Return indexed completion percentage
	 */
	public String getIndexedPercentage() {
		if (resultsElement == null)
			return "0"; // this should not happen

		if (!resultsElement.getTagName().equals("toc"))
			return resultsElement.getAttribute("indexed");
		else
			return "100";
	}

	private Element loadSearchResults() {
		// Load the results
		ContentUtil content = new ContentUtil(context, request);
		String sQuery = request.getQueryString();
		sQuery =
			UrlUtil.changeParameterEncoding(
				sQuery,
				"searchWordJS13",
				"searchWord");
		sQuery = UrlUtil.changeParameterEncoding(sQuery, "scopeJS13", "scope");
		return content.loadSearchResults(sQuery);

	}

	public Hit[] getHits() {
		if (hits != null)
			return hits;
			
		// Generate results list
		if (resultsElement == null)
			hits = new Hit[0];
		else if (!resultsElement.getTagName().equals("toc"))
			hits = new Hit[0];
		else {
			NodeList topics = resultsElement.getElementsByTagName("topic");
			hits = new Hit[topics.getLength()];
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

				hits[i] =
					new Hit(
						topic.getAttribute("label"),
						topic.getAttribute("href"),
						scoreString,
						topic.getAttribute("toc"),
						topic.getAttribute("toclabel"));

			}
		}
		return hits;
	}
	
	public String getSelectedTopicId() {
		return selectedTopicId;
	}
}