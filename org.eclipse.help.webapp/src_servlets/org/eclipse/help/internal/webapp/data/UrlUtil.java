/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.webapp.data;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import org.eclipse.core.boot.*;
import org.eclipse.help.internal.*;
import org.eclipse.help.internal.util.*;

public class UrlUtil {
	// XML escaped characters mapping
	private static final String invalidXML[] = { "&", ">", "<", "\"" };
	private static final String escapedXML[] =
		{ "&amp;", "&gt;", "&lt;", "&quot;" };
	// Default locale to use for serving requests to help
	private static String defaultLocale;
	// Locales that infocenter can serve in addition to the default locale.
	// null indicates that infocenter can serve every possible client locale.
	private static Collection locales;

	/**
	 * Decodes strings encoded with Javascript 1.3 escape
	 * Handles DBCS charactes that escape encoded as %uHHLL.
	 */
	public static String unescape(String encodedURL) {
		if (encodedURL == null)
			return null;
		int len = encodedURL.length();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < len;) {
			ByteArrayOutputStream tempOs;
			switch (encodedURL.charAt(i)) {
				case '%' :
					if ((len > i + 1) && encodedURL.charAt(i + 1) != 'u') {
						// byte encoded as %XX
						if (len >= i + 3) {
							tempOs = new ByteArrayOutputStream(1);
							tempOs.write(
								Integer.parseInt(
									encodedURL.substring(i + 1, i + 3),
									16));
							try {
								buf.append(
									new String(
										tempOs.toByteArray(),
										"ISO8859_1"));
							} catch (UnsupportedEncodingException uee) {
								return null;
							}
						}
						i += 3;

					} else {
						// char escaped to the form %uHHLL
						if (len >= i + 6) {
							tempOs = new ByteArrayOutputStream(2);
							tempOs.write(
								Integer.parseInt(
									encodedURL.substring(i + 2, i + 4),
									16));
							tempOs.write(
								Integer.parseInt(
									encodedURL.substring(i + 4, i + 6),
									16));
							try {
								buf.append(
									new String(
										tempOs.toByteArray(),
										"UnicodeBigUnmarked"));
							} catch (UnsupportedEncodingException uee) {
								return null;
							}
						}
						i += 6;

					}

					break;
				case '+' : //exception from standard
					buf.append(' ');
					i++;
					break;
				default :
					tempOs = new ByteArrayOutputStream(1);
					tempOs.write(encodedURL.charAt(i++));
					try {
						buf.append(new String(tempOs.toByteArray(), "UTF8"));
					} catch (UnsupportedEncodingException uee) {
						return null;
					}

					break;
			}

		}
		return buf.toString();
	}
	/**
	 * Obtains parameter from request without decoding it
	 */
	public static String getRawRequestParameter(
		HttpServletRequest request,
		String parameterName) {
		String[] values = getRawRequestParameters(request, parameterName);
		if (values.length > 0) {
			return values[0];
		}
		return null;
	}
	/**
	 * Obtains values of a parameter from request query string
	 * withoud decoding them
	 * @return String[]
	 */
	public static String[] getRawRequestParameters(
		HttpServletRequest request,
		String parameterName) {
		String query = request.getQueryString();
		if (query == null || "".equals(query)) {
			return new String[0];
		}
		List values = new ArrayList();
		StringTokenizer stok = new StringTokenizer(query, "&");
		while (stok.hasMoreTokens()) {
			String nameEqValue = stok.nextToken();
			int equalsPosition = nameEqValue.indexOf("=");
			if (equalsPosition >= 0
				&& parameterName.equals(
					nameEqValue.substring(0, equalsPosition))) {
				String val = nameEqValue.substring(equalsPosition + 1);
				values.add(val);
			}
		}
		return (String[]) values.toArray(new String[values.size()]);
	}
	/**
	 * Encodes string for embedding in JavaScript source
	 */
	public static String JavaScriptEncode(String str) {
		char[] wordChars = new char[str.length()];
		str.getChars(0, str.length(), wordChars, 0);
		StringBuffer jsEncoded = new StringBuffer();
		for (int j = 0; j < wordChars.length; j++) {
			int unicode = (int) wordChars[j];
			// to enhance readability, do not encode A-Z,a-z
			if (((int) 'A' <= unicode && unicode <= (int) 'Z')
				|| ((int) 'a' <= unicode && unicode <= (int) 'z')) {
				jsEncoded.append(wordChars[j]);
				continue;
			}
			// encode the character
			String charInHex = Integer.toString(unicode, 16).toUpperCase();
			switch (charInHex.length()) {
				case 1 :
					jsEncoded.append("\\u000").append(charInHex);
					break;
				case 2 :
					jsEncoded.append("\\u00").append(charInHex);
					break;
				case 3 :
					jsEncoded.append("\\u0").append(charInHex);
					break;
				default :
					jsEncoded.append("\\u").append(charInHex);
					break;
			}
		}
		return jsEncoded.toString();
	}

	/**
	 * Encodes string for embedding in html source.
	 */
	public static String htmlEncode(String str) {

		for (int i = 0; i < invalidXML.length; i++)
			str = TString.change(str, invalidXML[i], escapedXML[i]);
		return str;
	}

	public static boolean isLocalRequest(HttpServletRequest request) {
		String reqIP = request.getRemoteAddr();
		if ("127.0.0.1".equals(reqIP)) {
			return true;
		}

		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			InetAddress[] addr = InetAddress.getAllByName(hostname);
			for (int i = 0; i < addr.length; i++) {
				// test all addresses retrieved from the local machine
				if (addr[i].getHostAddress().equals(reqIP))
					return true;
			}
		} catch (IOException ioe) {
		}
		return false;
	}

	/**
	 * Returns a URL that can be loaded from a browser.
	 * This method is used for all url's except those from the webapp plugin.
	 * @param url
	 * @return String
	 */
	public static String getHelpURL(String url) {
		if (url == null || url.length() == 0)
			url = "about:blank";
		else if (url.startsWith("http:/"));
		else if (url.startsWith("file:/"))
			url = "../topic/" + url;
		else
			url = "../topic" + url;
		return url;
	}

	public static boolean isGecko(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent").toLowerCase();
		return agent.indexOf("gecko") >= 0;
	}

	public static boolean isIE(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent").toLowerCase();
		return (agent.indexOf("msie") >= 0);
	}

	public static boolean isKonqueror(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent").toLowerCase();
		return agent.indexOf("konqueror") >= 0;
	}

	public static boolean isMozilla(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent").toLowerCase();
		return agent.indexOf("mozilla/5") >= 0;
	}

	public static String getMozillaVersion(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent").toLowerCase();
		if (agent.indexOf("mozilla/5") < 0)
			return "0";
		int start = agent.indexOf("rv:") + "rv:".length();
		if (start < "rv:".length() || start >= agent.length())
			return "0";
		int end = agent.indexOf(")", start);
		if (end <= start)
			return "0";
		return agent.substring(start, end);
	}

	public static boolean isOpera(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent").toLowerCase();
		return agent.indexOf("opera") >= 0;
	}

	public static Locale getLocaleObj(HttpServletRequest request) {
		String localeStr = getLocale(request);
		if (localeStr.length() >= 5) {
			return new Locale(
				localeStr.substring(0, 2),
				localeStr.substring(3, 5));
		} else if (localeStr.length() >= 2) {
			return new Locale(localeStr.substring(0, 2), "");
		} else {
			return Locale.getDefault();
		}
	}
	
	public static String getLocale(HttpServletRequest request) {
		if (defaultLocale == null) {
			initializeLocales();
		}
		if ((HelpSystem.getMode() != HelpSystem.MODE_INFOCENTER)
			|| request == null) {
			return defaultLocale;
		}
		if (locales == null) {
			// serving in client locale
			return request.getLocale().toString();
		}

		// match client locales with one of infocenter locales
		for (Enumeration e = request.getLocales(); e.hasMoreElements();) {
			String locale = ((Locale) e.nextElement()).toString();
			if (locale.length() >= 5) {
				String ll_CC = locale.substring(0, 5);
				if (locales.contains(ll_CC)) {
					// client locale available
					return ll_CC;
				}
			}
			if (locale.length() >= 2) {
				String ll = locale.substring(0, 2);
				if (locales.contains(ll)) {
					// client language available
					return ll;
				}
			}
		}
		// no match
		return defaultLocale;
	}
	/**
	 * If locales for infocenter specified in prefernces
	 * or as command line parameters, this methods
	 * stores these locales in locales local variable for later access.
	 */
	private static synchronized void initializeLocales() {
		if (defaultLocale != null) {
			// already initialized
			return;
		}
		// initialize default locale
		defaultLocale = BootLoader.getNL();
		if (defaultLocale == null) {
			defaultLocale = Locale.getDefault().toString();
		}
		if (HelpSystem.getMode() != HelpSystem.MODE_INFOCENTER) {
			return;
		}

		// locale strings as passed in command line or in preferences
		List infocenterLocales = null;

		// first check if locales passed as command line arguments
		String[] args = BootLoader.getCommandLineArgs();
		boolean localeOption = false;
		for (int i = 0; i < args.length; i++) {
			if ("-locales".equalsIgnoreCase(args[i])) {
				localeOption = true;
				infocenterLocales = new ArrayList();
				continue;
			} else if (args[i].startsWith("-")) {
				localeOption = false;
				continue;
			}
			if (localeOption) {
				infocenterLocales.add(args[i]);
			}
		}
		// if no locales from command line, get them from preferences
		if (infocenterLocales == null) {
			StringTokenizer tokenizer =
				new StringTokenizer(
					HelpPlugin.getDefault().getPluginPreferences().getString(
						"locales"),
					" ,\t");
			while (tokenizer.hasMoreTokens()) {
				if (infocenterLocales == null) {
					infocenterLocales = new ArrayList();
				}
				infocenterLocales.add(tokenizer.nextToken());
			}
		}

		// format locales and collect in a set for lookup
		if (infocenterLocales != null) {
			locales = new HashSet(10, 0.4f);
			for (Iterator it = infocenterLocales.iterator(); it.hasNext();) {
				String locale = (String) it.next();
				if (locale.length() >= 5) {
					locales.add(
						locale.substring(0, 2).toLowerCase()
							+ "_"
							+ locale.substring(3, 5).toUpperCase());

				} else if (locale.length() >= 2) {
					locales.add(locale.substring(0, 2).toLowerCase());
				}
			}
		}
	}
}
