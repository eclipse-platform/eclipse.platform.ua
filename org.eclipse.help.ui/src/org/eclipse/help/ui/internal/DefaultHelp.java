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
package org.eclipse.help.ui.internal;
import org.eclipse.help.*;
import org.eclipse.help.internal.base.*;
import org.eclipse.help.ui.internal.util.*;
import org.eclipse.ui.help.*;

/**
 * This class is an implementation of the pluggable help support.
 * In is registered into the support extension point, and all 
 * requests to display help are delegated to this class.
 * The methods on this class interact with the actual
 * UI component handling the display.
 * <p>Most methods are inherited from the default hep support class; only
 * the UI specific ones are overriden.</p>
 */
public class DefaultHelp extends AbstractHelpUI {
	private static DefaultHelp instance;
	private ContextHelpDialog f1Dialog = null;

	/**
	 * BaseHelpViewer constructor.
	 */
	public DefaultHelp() {
		super();
		instance = this;
	}

	/**
	 * Singleton method
	 */
	public static DefaultHelp getInstance() {
		return instance;
	}

	/**
	 * Displays help.
	 */
	public void displayHelp() {
		BaseHelpSystem.getHelpSupport().displayHelp();
	}
	/**
	 * Displays a help resource specified as a url. 
	 * <ul>
	 *  <li>a URL in a format that can be returned by
	 * 	{@link  org.eclipse.help.IHelpResource#getHref() IHelpResource.getHref()}
	 * 	<li>a URL query in the format format <em>key=value&amp;key=value ...</em>
	 *  The valid keys are: "tab", "toc", "topic", "contextId".
	 *  For example, <em>toc="/myplugin/mytoc.xml"&amp;topic="/myplugin/references/myclass.html"</em>
	 *  is valid.
	 * </ul>
	 */
	public void displayHelpResource(String href) {
		BaseHelpSystem.getHelpSupport().displayHelpResource(href);
	}
	/**
	 * Displays context-sensitive help for specified context
	 * @param contexts the context to display
	 * @param x int positioning information
	 * @param y int positioning information
	 */
	public void displayContext(IContext context, int x, int y) {
		if (f1Dialog != null)
			f1Dialog.close();
		if (context == null)
			return;
		f1Dialog = new ContextHelpDialog(context, x, y);
		f1Dialog.open();
		// if any errors or parsing errors have occurred, display them in a pop-up
		ErrorUtil.displayStatus();
	}

	/**
	 * Returns <code>true</code> if the context-sensitive help
	 * window is currently being displayed, <code>false</code> if not.
	 */
	public boolean isContextHelpDisplayed() {
		if (f1Dialog == null) {
			return false;
		}
		return f1Dialog.isShowing();
	}
}
