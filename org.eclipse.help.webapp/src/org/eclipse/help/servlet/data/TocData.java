/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet.data;
import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.help.*;
import org.eclipse.help.internal.HelpSystem;
import org.eclipse.help.servlet.*;

/**
 * Helper class for tocView.jsp initialization
 */
public class TocData extends RequestData {

	// Request parameters
	private String tocHref;
	private String topicHref;

	// Selected TOC
	private IToc selectedToc;
	private IToc loadedToc;

	// List of TOC's
	private IToc[] tocs;

	// images directory
	private String imagesDirectory;

	/**
	 * Constructs the xml data for the contents page.
	 * @param context
	 * @param request
	 */
	public TocData(ServletContext context, HttpServletRequest request) {
		super(context, request);

		this.tocHref = request.getParameter("toc");
		this.topicHref = request.getParameter("topic");
		if (tocHref != null && tocHref.length() == 0)
			tocHref = null;
		if (topicHref != null && topicHref.length() == 0)
			topicHref = null;

		WebappPreferences pref =
			(WebappPreferences) context.getAttribute("WebappPreferences");
		imagesDirectory = pref.getImagesDirectory();
	}

	/**
	 * Returns a list of all the TOC's as xml elements.
	 * Individual TOC's are not loaded yet.
	 * @return Element[]
	 */
	public IToc[] getTocs() {
		if (tocs == null) {
			tocs = HelpSystem.getTocManager().getTocs(getLocale());
		}
		return tocs;
	}

	/**
	 * Returns the selected TOC, without loading its content.
	 * @return Element
	 */
	public IToc getSelectedToc() {

		if (selectedToc == null) {

			// Find the requested TOC
			if (tocHref != null && tocHref.length() > 0) {
				tocs = getTocs();
				for (int i = 0; selectedToc == null && i < tocs.length; i++)
					if (tocHref.equals(tocs[i].getHref()))
						selectedToc = tocs[i];
			} else {
				// try obtaining the TOC from the topic
				selectedToc = findTocContainingTopic(topicHref);
			}
		}
		return selectedToc;
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
			IToc toc = getSelectedToc();
			if (toc == null)
				return null;
			ITopic tocDescription = toc.getTopic(null);
			if (tocDescription != null)
				return UrlUtil.getHelpURL(tocDescription.getHref());
			else
				return UrlUtil.getHelpURL(null);
		}
	}


	/**
	 * Generates the HTML code (a tree) for a TOC.
	 * @param toc
	 * @param out
	 * @throws IOException
	 */
	public void generateToc(IToc toc, Writer out) throws IOException {

		ITopic[] topics = toc.getTopics();
		for (int i = 0; i < topics.length; i++) {
			generateTopic(topics[i], out);
		}
	}

	private void generateTopic(ITopic topic, Writer out) throws IOException {

		out.write("<li>");

		boolean hasNodes = topic.getSubtopics().length > 1;
		if (hasNodes) {
			out.write("<nobr>");
			out.write("<img src='");
			out.write(imagesDirectory);
			out.write("/plus.gif' class='collapsed' >");
			out.write(
				"<a href='"
					+ UrlUtil.getHelpURL(topic.getHref())
					+ "' title='"
					+ UrlUtil.htmlEncode(topic.getLabel())
					+ "'>");
			out.write("<img src='");
			out.write(imagesDirectory);
			out.write("/container_obj.gif'>");
			out.write(UrlUtil.htmlEncode(topic.getLabel()));
			out.write("</a>");
			out.write("</nobr>");

			out.write("<ul class='collapsed'>");

			ITopic[] topics = topic.getSubtopics();
			for (int i = 0; i < topics.length; i++) {
				generateTopic(topics[i], out);
			}

			out.write("</ul>");
		} else {
			out.write("<nobr>");
			out.write("<img src='");
			out.write(imagesDirectory);
			out.write("/plus.gif' style='visibility:hidden;' >");
			out.write(
				"<a href='"
					+ UrlUtil.getHelpURL(topic.getHref())
					+ "' title='"
					+ UrlUtil.htmlEncode(topic.getLabel())
					+ "'>");
			out.write("<img src='");
			out.write(imagesDirectory);
			out.write("/topic.gif'>");
			out.write(UrlUtil.htmlEncode(topic.getLabel()));
			out.write("</a>");
			out.write("</nobr>");
		}

		out.write("</li>");
	}

	/**
	 * Generates the HTML code (a tree) for a TOC.
	 * @param toc
	 * @param out
	 * @throws IOException
	 */
	public void generateBasicToc(IToc toc, Writer out) throws IOException {
		ITopic[] topics = toc.getTopics();
		for (int i = 0; i < topics.length; i++) {
			generateBasicTopic(topics[i], out);
		}
		
	}

	private void generateBasicTopic(ITopic topic, Writer out)
		throws IOException {

		out.write("<li>");

		boolean hasNodes = topic.getSubtopics().length > 1;
		if (hasNodes) {
			out.write("<nobr>");
			out.write(
				"<a href='"
					+ UrlUtil.getHelpURL(topic.getHref())
					+ "' title='"
					+ UrlUtil.htmlEncode(topic.getLabel())
					+ "'>");
			out.write("<img src='");
			out.write(imagesDirectory);
			out.write("/container_obj.gif' border=0>");
			out.write("&nbsp;" +UrlUtil.htmlEncode(topic.getLabel()));
			out.write("</a>");
			out.write("</nobr>");

			out.write("<ul>");

			ITopic[] topics = topic.getSubtopics();
			for (int i = 0; i < topics.length; i++) {
					generateBasicTopic(topics[i], out);
			}

			out.write("</ul>");
		} else {
			out.write("<nobr>");
			out.write(
				"<a href='"
					+ UrlUtil.getHelpURL(topic.getHref())
					+ "' title='"
					+ UrlUtil.htmlEncode(topic.getLabel())
					+ "'>");
			out.write("<img src='");
			out.write(imagesDirectory);
			out.write("/topic.gif' border=0>");
			out.write("&nbsp;" +UrlUtil.htmlEncode(topic.getLabel()));
			out.write("</a>");
			out.write("</nobr>");
		}

		out.write("</li>");
	}

	/**
	 * Finds a TOC that contains specified topic
	 * @param topic the topic href
	 */
	private IToc findTocContainingTopic(String topic) {
		if (topic == null || topic.equals(""))
			return null;

		int index = topic.indexOf("help:/");
		if (index != -1)
			topic = topic.substring(index + 5);
		index = topic.indexOf('?');
		if (index != -1)
			topic = topic.substring(0, index);

		if (topic == null || topic.equals(""))
			return null;

		tocs = getTocs();
		for (int i = 0; i < tocs.length; i++)
			if (tocs[i].getTopic(topic) != null)
				return tocs[i];

		// nothing found
		return null;
	}
}