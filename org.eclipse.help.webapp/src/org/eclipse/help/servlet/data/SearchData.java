/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet.data;
import java.io.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.core.runtime.*;
import org.eclipse.help.*;
import org.eclipse.help.internal.*;
import org.eclipse.help.internal.search.*;
import org.eclipse.help.internal.util.*;
import org.eclipse.help.servlet.*;

/**
 * Helper class for searchView.jsp initialization
 */
public class SearchData extends RequestData {

	// Request parameters
	private String topicHref;
	private String selectedTopicId = "";
	private String searchWord;

	// search results
	SearchHit[] hits;

	// percentage of indexing completion
	private int indexCompletion = 100;

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

		String sQuery = request.getQueryString();
		sQuery =
			UrlUtil.changeParameterEncoding(
				sQuery,
				"searchWordJS13",
				"searchWord");
		searchWord = UrlUtil.getRequestParameter(sQuery, "searchWord");

		// try loading search results or get the indexing progress info.
		if (isSearchRequest()) {
			loadSearchResults();

			if (!isProgressRequest()) {
				for (int i = 0; i < hits.length; i++) {
					// the following assume topic numbering as in searchView.jsp
					if (hits[i].getHref().equals(topicHref)) {
						selectedTopicId = "a" + i;
						break;
					}
				}
			}
		}
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
		return (hits == null && indexCompletion != 100);
	}

	/**
	 * Return the number of links
	 * @return int
	 */
	public int getResultsCount() {
		return hits.length;
	}
	
	public String getSelectedTopicId() {
		return selectedTopicId;
	}

	public String getTopicHref(int i) {
		return UrlUtil.getHelpURL(hits[i].getHref());
	}

	public String getTopicLabel(int i) {
		return UrlUtil.htmlEncode(hits[i].getLabel());
	}

	public String getTopicScore(int i) {
		try {
			float score = hits[i].getScore();
			NumberFormat percentFormat =
				NumberFormat.getPercentInstance(request.getLocale());
			return percentFormat.format(score);
		} catch (NumberFormatException nfe) {
			// will display original score string
			return String.valueOf(hits[i].getScore());
		}
	}
	
	public String getTopicTocLabel(int i) {
		if (hits[i].getToc() != null)
			return UrlUtil.htmlEncode(hits[i].getToc().getLabel());
		else
			return "";
	}
	
	/**
	 * Return indexed completion percentage
	 */
	public String getIndexedPercentage() {
		return String.valueOf(indexCompletion);
	}

	public String getSearchWordParamName() {
		if (isMozilla())
			return "searchWord";
		else
			return "searchWordJS13";
	}

	public String getScopeParamName() {
		if (isMozilla())
			return "scope";
		else
			return "scopeJS13";
	}

	/**
	 * Returns the search query
	 */
	public String getSearchWord() {
		if (searchWord == null)
			return "";
		else
			return searchWord;
	}


	/**
	 * Returns the list of selected TOC's as a comma-separated list
	 */
	public String getSelectedTocsList() {
		String[] books = UrlUtil.getRequestParameters(request, "scope");
		StringBuffer booksList = new StringBuffer();
		if (books.length > 0) {
			booksList.append('"');
			booksList.append(UrlUtil.JavaScriptEncode(books[0]));
			booksList.append('"');
			for (int i = 1; i < books.length; i++) {
				booksList.append(',');
				booksList.append('"');
				booksList.append(UrlUtil.JavaScriptEncode(books[i]));
				booksList.append('"');
			}
		}
		return booksList.toString();
	}

	/**
	 * Returns true if book is within a search scope
	 */
	public boolean isTocSelected(int toc) {
		TocData tocData = new TocData(context, request);
		String href=tocData.getTocHref(toc);
		String[] books = UrlUtil.getRequestParameters(request, "scope");
		for(int i=0; i<books.length; i++){
			if(books[i].equals(href)){
				return true;
			}
		}
		return false;
	}

	/**
	* Call the search engine, and get results or the percentage of 
	* indexed documents.
	*/
	private void loadSearchResults() {
		// Load the results
		String query = request.getQueryString();
		query =
			UrlUtil.changeParameterEncoding(
				query,
				"searchWordJS13",
				"searchWord");
		query = UrlUtil.changeParameterEncoding(query, "scopeJS13", "scope");

		// create a SearchURL directly. 
		// *** this code should eventually be cleaned ***
		//return content.loadSearchResults(sQuery);

		// The url string should contain the search parameters.
		try {
			SearchProgressMonitor pm =
				SearchProgressMonitor.getProgressMonitor(
					searchWord,
					getLocale());
			if (pm.isDone()) {
				this.indexCompletion = 100;

				SearchRequest sQuery = new SearchRequest(query);
				SearchResults results =
					new SearchResults(
						sQuery.getScope(),
						sQuery.getMaxHits(),
						getLocale());

				HelpSystem.getSearchManager().search(sQuery, results, pm);
				hits = results.getSearchHits();
				if (hits == null) {
					Logger.logError(Resources.getString("index_is_busy"), null);
				}
				return;
			} else {
				// progress
				this.indexCompletion = pm.getPercentage();
				return;
			}
		} catch (Exception e) {
			this.indexCompletion = 0;
		}

	}

}