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
package org.eclipse.help.internal;
import org.eclipse.core.runtime.*;
import org.eclipse.help.*;
import org.eclipse.help.internal.context.*;
import org.eclipse.help.internal.toc.*;
import org.eclipse.help.internal.util.*;

/**
 * The actual implementation of the help system plugin.
 */
public final class HelpCore {
	protected static final HelpCore instance = new HelpCore();

	public final static String BASE_TOCS_KEY = "baseTOCS";

	protected TocManager tocManager;
	protected ContextManager contextManager;
	private IHelp helpSupport = null;

	/**
	 * HelpCore constructor comment.
	 */
	private HelpCore() {
		super();
	}
	/**
	 * Used to obtain Context Manager
	 * returns an instance of ContextManager
	 */
	public static ContextManager getContextManager() {
		if (getInstance().contextManager == null)
			getInstance().contextManager = new ContextManager();
		return getInstance().contextManager;
	}

	public static HelpCore getInstance() {
		return instance;
	}
	/**
	 * Used to obtain Toc Naviagiont Manager
	 * @return instance of TocManager
	 */
	public static TocManager getTocManager() {
		if (getInstance().tocManager == null) {
			synchronized (HelpCore.class) {
				if (getInstance().tocManager == null) {
					getInstance().tocManager = new TocManager();
				}
			}
		}
		return getInstance().tocManager;
	}
	/**
	 */
	public HelpCore newInstance() {
		return null;
	}
}
