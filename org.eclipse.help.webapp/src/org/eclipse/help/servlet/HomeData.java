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
 * Helper class for contents.jsp initialization
 */
public class HomeData {
	private ServletContext context;
	private HttpServletRequest request;
	
	// Request parameters
	private String tocHref;
	private String topicHref;
	
	// List of TOC's
	private Element[] tocs;


	/**
	 * Constructs the xml data for the contents page.	 * @param context	 * @param request	 */
	public HomeData(ServletContext context, HttpServletRequest request) {
		this.context = context;
		this.request = request;
		this.tocHref = request.getParameter("toc");
		this.topicHref = request.getParameter("topic");
		if (tocHref != null && tocHref.length() == 0)
			tocHref = null;
		if (topicHref != null && topicHref.length() == 0)
			topicHref = null;
	}
	

	/**
	 * Returns the topic to display.
	 * If there is a TOC, return its topic description.
	 * Return null if no topic is specified and there is no toc description.	 * @return String	 */
	public String getTopicHref() {
		if (topicHref != null && topicHref.length() > 0)
			return UrlUtil.getHelpURL(topicHref);
		else {
			Element toc = getRequestedToc();
			if (toc == null)
				return null;
			String tocDescription = toc.getAttribute("topic");
			return UrlUtil.getHelpURL(tocDescription);
		} 
	}
	
	/**
	 * Returns a list of all the TOC's as xml elements.
	 * Individual TOC's are not loaded yet.	 * @return Element[]	 */
	private Element[] getTocs() {
		if (tocs == null) {

			ContentUtil content = new ContentUtil(context, request);
			Element tocsElement = content.loadTocs();
			if (tocsElement == null)
				return new Element[0];
			NodeList tocsElements = tocsElement.getElementsByTagName("toc");
			tocs = new Element[tocsElements.getLength()];
			for (int i = 0; i < tocs.length; i++)
				tocs[i] = (Element) tocsElements.item(i);
		}
		return tocs;
	}

	/**
	 * Returns the selected TOC, without loading its content.	 * @return Element	 */
	private Element getRequestedToc() {
		if (tocHref == null || tocHref.length() == 0)
			return null;
		Element[] tocs = getTocs();
		for (int i=0; i<tocs.length; i++)
			if (tocHref.equals(tocs[i].getAttribute("href")))
				return tocs[i];
		return null;
	}

}