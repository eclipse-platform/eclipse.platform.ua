package org.eclipse.help.servlet.data;

import java.net.URLEncoder;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
public class Hit extends Topic {
	private String score;
	private String tocLabel;

	public Hit(
		String label,
		String href,
		String score,
		String toc,
		String tocLabel) {
			
		super(label, href);
		this.score = score;
		this.tocLabel = tocLabel;

		if (href.indexOf('?') == -1)
			href += "?toc=" + URLEncoder.encode(toc);
		else
			href += "&toc=" + URLEncoder.encode(toc);
	}

	public String getScore() {
		return score;
	}

	public String getTocLabel() {
		return tocLabel;
	}
}
