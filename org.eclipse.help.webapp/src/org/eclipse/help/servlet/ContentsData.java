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
public class ContentsData {
	private ServletContext context;
	private HttpServletRequest request;
	
	// Request parameters
	private String tocHref;
	private String topicHref;
	
	// Selected TOC
	private Element selectedToc;
	private boolean selectedTocLoaded = false;
		
	// List of TOC's
	private Element[] tocs;


	public ContentsData(ServletContext context, HttpServletRequest request) {
		this.context = context;
		this.request = request;
		this.tocHref = request.getParameter("toc");
		this.topicHref = request.getParameter("topic");
		if (tocHref != null && tocHref.length() == 0)
			tocHref = null;
		if (topicHref != null && topicHref.length() == 0)
			topicHref = null;
	}

	public Element[] getTocs() {
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

	public Element getSelectedToc() {
		if (!selectedTocLoaded) {
			ContentUtil content = new ContentUtil(context, request);
			if (tocHref == null)
				selectedToc = content.loadTOCcontainingTopic(topicHref);
			else
				selectedToc = content.loadTOC(tocHref);
			selectedTocLoaded = true;
		}
		return selectedToc;
	}

	public String getTocHref(Element toc) {
		return toc.getAttribute("href");
	}
	
	public String getTocDescriptionTopic(Element toc) {
		String tocDescription = toc.getAttribute("topic");
		return getHelpURL(tocDescription);
	}

	public String getTocLabel(Element toc) {
		return toc.getAttribute("label");
	}

	public void generateToc(Element toc, Writer out) throws IOException {
		// Only generate the selected toc
		if (getSelectedToc() == null)
			return;
			
		if (!toc.getAttribute("href").equals(getSelectedToc().getAttribute("href")))
			return;
		
		// Note: if we were to generate all the TOCS, then we have to load them first
		NodeList topics = getSelectedToc().getChildNodes();
		for (int i = 0; i < topics.getLength(); i++) {
			Node n = topics.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
				generateTopic((Element) n, out);
		}
	}

	private void generateTopic(Element topic, Writer out) throws IOException {

		out.write("<li>");

		boolean hasNodes = topic.hasChildNodes();
		if (hasNodes) {
			out.write("<nobr>");
			out.write("<img src='images/plus.gif' class='collapsed' >");
			out.write(
				"<a href='"
					+ getHelpURL(topic.getAttribute("href"))
					+ "' title='"
					+ UrlUtil.htmlEncode(topic.getAttribute("label"))
					+ "'>");
			out.write("<img src='images/container_obj.gif'>");
			out.write(UrlUtil.htmlEncode(topic.getAttribute("label")));
			out.write("</a>");
			out.write("</nobr>");

			out.write("<ul class='collapsed'>");

			NodeList topics = topic.getChildNodes();
			for (int i = 0; i < topics.getLength(); i++) {
				Node n = topics.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE)
					generateTopic((Element) n, out);
			}

			out.write("</ul>");
		} else {
			out.write("<nobr>");
			out.write("<img src='images/plus.gif' style='visibility:hidden;' >");
			out.write(
				"<a href='"
					+ getHelpURL(topic.getAttribute("href"))
					+ "' title='"
					+ UrlUtil.htmlEncode(topic.getAttribute("label"))
					+ "'>");
			out.write("<img src='images/topic.gif'>");
			out.write(UrlUtil.htmlEncode(topic.getAttribute("label")));
			out.write("</a>");
			out.write("</nobr>");
		}

		out.write("</li>");
	}

	private String getHelpURL(String url) {
		if (url == null || url.length() == 0)
			url = "about:blank";
		else if (url.startsWith("file:/"))
			url = "content/" + url;
		else
			url = "content/help:" + url;
		return url;
	}

}