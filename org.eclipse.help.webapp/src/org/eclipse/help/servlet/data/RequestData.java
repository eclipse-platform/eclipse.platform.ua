/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet.data;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.help.internal.HelpSystem;
import org.eclipse.help.servlet.WebappPreferences;

/**
 * Helper class for contents.jsp initialization
 */
public class RequestData {
	public final static int MODE_WORBENCH = HelpSystem.MODE_WORBENCH;
	public final static int MODE_INFOCENTER = HelpSystem.MODE_INFOCENTER;
	public final static int MODE_STANDALONE = HelpSystem.MODE_STANDALONE;
	
	protected ServletContext context;
	protected HttpServletRequest request;
	protected String locale;
	protected boolean isIE;
	protected boolean isMozilla;

	/**
	 * Constructs the data for a request.
	 * @param context
	 * @param request
	 */
	public RequestData(ServletContext context, HttpServletRequest request) {
		this.context = context;
		this.request = request;

		String agent = request.getHeader("User-Agent").toLowerCase(Locale.US);
		this.isIE = (agent.indexOf("msie") != -1);
		this.isMozilla = (!isIE && (agent.indexOf("mozilla/5") != -1));

		if ((HelpSystem.getMode() == HelpSystem.MODE_INFOCENTER)
			&& request != null)
			locale = request.getLocale().toString();
		else
			locale = Locale.getDefault().toString();
	}

	/**
	 * Returns the preferences object
	 */
	public WebappPreferences getPrefs() {
		return (WebappPreferences) context.getAttribute("WebappPreferences");
	}

	public boolean isIE() {
		return isIE;
	}

	public boolean isMozilla() {
		return isMozilla;
	}
	
	public String getLocale() {
		return locale;
	}
	
	public int getMode(){
		return HelpSystem.getMode();
	}
}