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
public class RequestData {
	protected ServletContext context;
	protected HttpServletRequest request;
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
		
		String agent=request.getHeader("User-Agent").toLowerCase(Locale.US);
		this.isIE   = (agent.indexOf("msie") != -1);
		this.isMozilla  = (!isIE && (agent.indexOf("mozilla/5")!=-1));
	}

}