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
	private Element loadedToc;

	// List of TOC's
	private Element[] tocs;

	/**
	 * Constructs the xml data for the contents page.
	 * @param context
	 * @param request
	 */
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

	/**
	 * Returns a list of all the TOC's as xml elements.
	 * Individual TOC's are not loaded yet.
	 * @return Element[]
	 */
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

	/**
	 * Returns the selected TOC, without loading its content.
	 * @return Element
	 */
	public Element getSelectedToc() {

		if (selectedToc == null) {

			// Find the requested TOC
			if (tocHref != null && tocHref.length() > 0) {
				Element[] tocs = getTocs();
				for (int i = 0; selectedToc == null && i < tocs.length; i++)
					if (tocHref.equals(tocs[i].getAttribute("href")))
						selectedToc = tocs[i];
			} else {
				// try obtaining the TOC from the topic
				ContentUtil content = new ContentUtil(context, request);
				Element tocsElement =
					content.loadTocsContainingTopic(topicHref);
				if (tocsElement != null) {
					NodeList tocsElements =
						tocsElement.getElementsByTagName("toc");
					if (tocsElements.getLength() > 0)
						selectedToc = (Element) tocsElements.item(0);
				}
			}
		}
		return selectedToc;
	}

	/**
	 * Returns the selected TOC. If not loaded, it will load it first.
	 * @return Element
	 */
	public Element loadSelectedToc() {
		if (loadedToc == null) {
			Element toc = getSelectedToc();
			if (toc != null) {
				ContentUtil content = new ContentUtil(context, request);
				// load TOC that contains specified topic
				loadedToc = content.loadTOC(toc.getAttribute("href"));
			}
		}
		return loadedToc;
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

	/**
	 * Returns the href of the specified TOC.
	 * @param toc
	 * @return String
	 */
	public String getTocHref(Element toc) {
		return toc.getAttribute("href");
	}

	/**
	 * Returns the description topic for specified TOC.
	 * @param toc
	 * @return String
	 */
	public String getTocDescriptionTopic(Element toc) {
		if (toc == null)
			return "about:blank"; // should this return the help home ?
		String tocDescription = toc.getAttribute("topic");
		return UrlUtil.getHelpURL(tocDescription);
	}

	/**
	 * Returns the label (title) of the specified TOC
	 * @param toc
	 * @return String
	 */
	public String getTocLabel(Element toc) {
		return toc.getAttribute("label");
	}

	/**
	 * Generates the HTML code (a tree) for a TOC.
	 * @param toc
	 * @param out
	 * @throws IOException
	 */
	public void generateToc(Element toc, Writer out) throws IOException {
		// Only generate the selected toc
		if (getSelectedToc() == null)
			return;
		if (!getSelectedToc().getAttribute("href").equals(toc.getAttribute("href")))
			return;

		// load the toc first
		if (loadSelectedToc() == null)
			return;

		// Note: if we were to generate all the TOCS, then we have to load them first
		NodeList topics = loadedToc.getChildNodes();
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
					+ UrlUtil.getHelpURL(topic.getAttribute("href"))
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
			out.write(
				"<img src='images/plus.gif' style='visibility:hidden;' >");
			out.write(
				"<a href='"
					+ UrlUtil.getHelpURL(topic.getAttribute("href"))
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

}