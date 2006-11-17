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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Locale;
import java.util.StringTokenizer;

/*
 * A temporary class with content copied from HTMLDocParser in order to read
 * the charset of an XHTML document (in 3.2.2 only). Cannot use HTMLDocParser
 * because it is in a higher plugin. See bug 160969.
 */
public class XHTMLCharsetParser {
	// maximum number of characters that will be searched
	// from the beginning of HTML document to charset declaration
	public static final int MAX_OFFSET = 2048;

	// elements, atributes and values contstants
	private final static String ELEMENT_META = "META"; //$NON-NLS-1$
	private final static String ELEMENT_BODY = "body"; //$NON-NLS-1$
	private final static String ELEMENT_HEAD = "head"; //$NON-NLS-1$
	private final static String ATTRIBUTE_HTTP = "http-equiv"; //$NON-NLS-1$
	private final static String ATTRIBUTE_HTTP_VALUE = "content-type"; //$NON-NLS-1$
	private final static String ATTRIBUTE_CONTENT = "content"; //$NON-NLS-1$

	// states for parsing elements
	private final static int STATE_ELEMENT_START = 0;
	private final static int STATE_ELEMENT_AFTER_LT = 1;
	private final static int STATE_ELEMENT_AFTER_LT_SLASH = 2;
	private final static int STATE_ELEMENT_META = 3;
	// states for parsing HTTP-EQUIV attribute
	private final static int STATE_HTTP_START = 0;
	private final static int STATE_HTTP_AFTER_NAME = 1;
	private final static int STATE_HTTP_AFTER_EQ = 2;
	private final static int STATE_HTTP_DONE = 3;
	// states for parsing CONTENT attribute
	private final static int STATE_CONTENT_START = 0;
	private final static int STATE_CONTENT_AFTER_NAME = 1;
	private final static int STATE_CONTENT_AFTER_EQ = 2;
	private final static int STATE_CONTENT_DONE = 3;

	/**
	 * Private. Parses HTML to extract document encoding specified in HTTP
	 * equivalent META tag in the document header. Example of such META tag is
	 * <META HTTP-EQUIV="content-type" CONTENT="text/html; charset=UTF-8">
	 * 
	 * @return String or null if encoding not found
	 */
	public static String getCharsetFromHTML(InputStream is) {
		// Set up an ascii reader for the document (documents should not use
		// other characters before encoding is defined)
		Reader asciiReader = new ASCIIReader(is, MAX_OFFSET);
		StreamTokenizer tokenizer = new StreamTokenizer(asciiReader);

		tokenizer.lowerCaseMode(false);
		tokenizer.ordinaryChar('\''); // default quote char
		tokenizer.ordinaryChar('/'); // default comment character

		String charset = getCharsetFromHTMLTokens(tokenizer);
		if (asciiReader != null) {
			try {
				asciiReader.close();
			} catch (IOException ioe) {
			}
		}
		return charset;
	}
	public static String getCharsetFromHTMLTokens(StreamTokenizer tokenizer) {
		// keeps track of content attribute attribute until parsing
		// of the meta tag is complete
		String contentValue = null;

		// initialize states
		int stateContent = STATE_HTTP_START;
		int stateElement = STATE_ELEMENT_START;
		int stateHttp = STATE_HTTP_START;

		try {
			// in the worst case, process tokens until end of file
			for (int token = tokenizer.nextToken(); token != StreamTokenizer.TT_EOF; token = tokenizer
					.nextToken()) {
				// process input based depending on current state
				switch (stateElement) {
					case STATE_ELEMENT_START :
						if (token == '<') {
							stateElement = STATE_ELEMENT_AFTER_LT;
						} // else do nothing, cannot be beginning of META tag
						break;
					case STATE_ELEMENT_AFTER_LT :
						if (token == StreamTokenizer.TT_WORD) {
							// some element opened
							if (ELEMENT_META.equalsIgnoreCase(tokenizer.sval)) {
								// META element opened
								stateElement = STATE_ELEMENT_META;
								// initialize state of attributes
								stateHttp = STATE_HTTP_START;
								stateContent = STATE_CONTENT_START;
								contentValue = null;
							} else if (ELEMENT_BODY
									.equalsIgnoreCase(tokenizer.sval)) {
								// body element opened, we are too far, stop
								// processing input
								return null;
							} else {
								// some other element opened, start from initial
								// state
								stateElement = STATE_ELEMENT_START;
							}
						} else if (token == '/') {
							// can be begging of head closing
							stateElement = STATE_ELEMENT_AFTER_LT_SLASH;
						} else {
							// not an element opened, could be openning of
							// declaration
							// or element closing e.t.c.
							stateElement = STATE_ELEMENT_START;
						}
						break;
					case STATE_ELEMENT_AFTER_LT_SLASH :
						if (token == StreamTokenizer.TT_WORD
								&& ELEMENT_HEAD
										.equalsIgnoreCase(tokenizer.sval)) {
							// head element closed, we are too far, stop
							// processing input
							return null;
						}
						stateElement = STATE_ELEMENT_START;
						break;
					default : // STATE_META_IN :
						switch (token) {
							case '>' :
								// no longer inside META, start from initial
								// state
								stateElement = STATE_ELEMENT_START;
								break;
							case StreamTokenizer.TT_WORD :
								// string inside META tag, can be attribute name
								if (ATTRIBUTE_HTTP
										.equalsIgnoreCase(tokenizer.sval)) {
									// found HTTP-EQUIV attribute name
									stateHttp = STATE_HTTP_AFTER_NAME;
								} else if (ATTRIBUTE_CONTENT
										.equalsIgnoreCase(tokenizer.sval)) {
									// found CONTENT attribute name
									stateContent = STATE_CONTENT_AFTER_NAME;
								} else if (stateHttp == STATE_HTTP_AFTER_EQ
										&& ATTRIBUTE_HTTP_VALUE
												.equalsIgnoreCase(tokenizer.sval)) {
									// value of HTTP-EQUIV attribute (unquoted)
									// we found <META ...
									// HTTP-EQUIV=content-type
									stateHttp = STATE_HTTP_DONE;
								} else {
									// some other attribute name or string,
									// reset states of seeked attributes,
									// unless successfully processed earlier
									if (stateHttp != STATE_HTTP_DONE) {
										stateHttp = STATE_HTTP_START;
									}
									if (stateContent != STATE_CONTENT_DONE) {
										stateContent = STATE_CONTENT_START;
									}
								}
								break;
							case '=' :
								// = inside META tag, can separate interesing us
								// attribute names from values
								if (stateHttp == STATE_HTTP_AFTER_NAME) {
									// we have HTTP-EQUIV=
									stateHttp = STATE_HTTP_AFTER_EQ;
								} else if (stateContent == STATE_CONTENT_AFTER_NAME) {
									// we have CONTENT=
									stateContent = STATE_CONTENT_AFTER_EQ;
								} else {
									// equal sign after some other attribute
									// name or string,
									// reset states of seeked attributes,
									// unless successfully processed earlier
									if (stateHttp != STATE_HTTP_DONE) {
										stateHttp = STATE_HTTP_START;
									}
									if (stateContent != STATE_CONTENT_DONE) {
										stateContent = STATE_CONTENT_START;
									}
								}
								break;
							case '\"' :
								// quoted string inside META tag, can be
								// attribute value
								if (stateHttp == STATE_HTTP_AFTER_EQ) {
									// value of HTTP-EQUIV attribute
									if (ATTRIBUTE_HTTP_VALUE
											.equalsIgnoreCase(tokenizer.sval)) {
										// we found <META ...
										// HTTP-EQUIV="content-type"
										stateHttp = STATE_HTTP_DONE;
									}
								} else if (stateContent == STATE_CONTENT_AFTER_EQ) {
									// value of CONTENT attribute
									stateContent = STATE_CONTENT_DONE;
									// save the value of the attribute
									// if attribue HTTP-EQUIV="content-type" is
									// found
									// in the same META tag, this value might
									// have
									// Content-type entity header
									contentValue = tokenizer.sval;
								} else {
									// value for the attribute is missing
									// reset states of seeked attributes
									stateHttp = STATE_HTTP_START;
									stateContent = STATE_CONTENT_START;
								}
								break;
							default :
								// other unexpected token inside META tag
								// reset states of seeked attributes,
								// unless successfully processed earlier
								if (stateHttp != STATE_HTTP_DONE) {
									stateHttp = STATE_HTTP_START;
								}
								if (stateContent != STATE_CONTENT_DONE) {
									stateContent = STATE_CONTENT_START;
								}
								break;
						}
						break;
				}
				if (contentValue != null && stateHttp == STATE_HTTP_DONE
						&& stateContent == STATE_CONTENT_DONE) {
					// <META HTTP-EQUIV="content-type" CONTENT="*******"
					// parse vale of content attribute to extract encoding
					return getCharsetFromHTTP(contentValue);
				}

			}
		} catch (IOException ioe) {
			return null;
		}
		// end of file
		return null;
	}
	/**
	 * Parses HTTP1.1 Content-Type entity-header field for example,
	 * Content-Type: text/html; charset=ISO-8859-4, and extracts charset
	 * parameter value of the media sub type.
	 * 
	 * @return value of charset parameter, for example ISO-8859-4 or null if
	 *         parameter does not exist
	 */
	public static String getCharsetFromHTTP(String contentValue) {
		StringTokenizer t = new StringTokenizer(contentValue, ";"); //$NON-NLS-1$
		while (t.hasMoreTokens()) {
			String parameter = t.nextToken().trim();
			if (parameter.toLowerCase(Locale.ENGLISH).startsWith("charset=")) { //$NON-NLS-1$
				String charset = parameter
						.substring("charset=".length()).trim(); //$NON-NLS-1$
				if (charset.length() > 0) {
					return charset;
				}
			}
		}
		return null;
	}
}
