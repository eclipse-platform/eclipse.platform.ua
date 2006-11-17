/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.xhtml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*
 * Finds and replaces the charset in the meta tag with UTF-8, if it
 * exists. If not, does nothing.
 */
public class UACharsetProcessor {
	
	private static final String ELEMENT_HTML = "html"; //$NON-NLS-1$
	private static final String ELEMENT_HEAD = "head"; //$NON-NLS-1$
	private static final String ELEMENT_META = "meta"; //$NON-NLS-1$
	private static final String ATTRIBUTE_CONTENT = "content"; //$NON-NLS-1$
	private static final String PREFIX_CHARSET = "text/html; charset="; //$NON-NLS-1$
	private static final String ENCODING_UTF8 = "UTF-8"; //$NON-NLS-1$
	
	public void processCharset(Document document) {
		Element html = document.getDocumentElement();
		if (ELEMENT_HTML.equalsIgnoreCase(html.getNodeName())) {
			Node head = html.getFirstChild();
			while (head != null) {
				if (head.getNodeType() == Node.ELEMENT_NODE && ELEMENT_HEAD.equalsIgnoreCase(head.getNodeName())) {
					Node meta = head.getFirstChild();
					while (meta != null) {
						if (meta.getNodeType() == Node.ELEMENT_NODE && ELEMENT_META.equalsIgnoreCase(meta.getNodeName())) {
							Element element = (Element)meta;
							String content = element.getAttribute(ATTRIBUTE_CONTENT);
							if (content.startsWith(PREFIX_CHARSET)) {
								element.setAttribute(ATTRIBUTE_CONTENT, PREFIX_CHARSET + ENCODING_UTF8);
								return;
							}
						}
						meta = meta.getNextSibling();
					}
					return;
				}
				head = head.getNextSibling();
			}
		}
	}
}
