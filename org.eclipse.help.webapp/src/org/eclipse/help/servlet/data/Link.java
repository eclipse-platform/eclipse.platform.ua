package org.eclipse.help.servlet.data;

import java.net.URLEncoder;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
public class Link extends Topic {
	private String score;
	private String tocLabel;

	public Link(
		String label,
		String href,
		String toc,
		String tocLabel) {
			
		super(label, href);
		this.tocLabel = tocLabel;

		if (href.indexOf('?') == -1)
			href += "?toc=" + URLEncoder.encode(toc);
		else
			href += "&toc=" + URLEncoder.encode(toc);
	}

	public String getTocLabel() {
		return tocLabel;
	}
}
