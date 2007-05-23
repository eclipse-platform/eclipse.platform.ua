/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.base;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.ILiveHelpAction;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.help.internal.HelpPlugin;
import org.eclipse.help.internal.base.util.IErrorUtil;
import org.eclipse.help.internal.browser.BrowserManager;
import org.eclipse.help.internal.search.LocalSearchManager;
import org.eclipse.help.internal.search.SearchManager;
import org.eclipse.help.internal.server.WebappManager;
import org.eclipse.help.internal.workingset.WorkingSetManager;
import org.osgi.framework.Bundle;

/**
 * Base Help System.
 */
public final class BaseHelpSystem {
	
	private static final BaseHelpSystem instance = new BaseHelpSystem();
	
	public static final String BOOKMARKS = "bookmarks"; //$NON-NLS-1$
	public static final String WORKING_SETS = "workingSets"; //$NON-NLS-1$
	public static final String WORKING_SET = "workingSet"; //$NON-NLS-1$
	
	public static final int MODE_WORKBENCH = 0;
	public static final int MODE_INFOCENTER = 1;
	public static final int MODE_STANDALONE = 2;

	private int mode = MODE_WORKBENCH;
	
	private SearchManager searchManager;
	private WorkingSetManager workingSetManager;
	private BookmarkManager bookmarkManager;

	private boolean webappStarted = false;
	private boolean webappRunning = false;
	private IErrorUtil defaultErrorMessenger;
	private IBrowser browser;
	private IBrowser internalBrowser;
	private HelpDisplay helpDisplay = null;
	private boolean rtl = false;

	private BaseHelpSystem() {
		super();
		rtl = initializeRTL();
	}

	public static BaseHelpSystem getInstance() {
		return instance;
	}

	/*
	 * Returns the singleton search manager, which is the main interface to the
	 * help system's search capability.
	 */
	public static SearchManager getSearchManager() {
		if (getInstance().searchManager == null) {
			synchronized (BaseHelpSystem.class) {
				if (getInstance().searchManager == null) {
					getInstance().searchManager = new SearchManager();
				}
			}
		}
		return getInstance().searchManager;
	}
	
	/*
	 * Returns the local search manager which deals only with the local content
	 * and is called by the global search manager.
	 */
	public static LocalSearchManager getLocalSearchManager() {
		return getSearchManager().getLocalSearchManager();
	}

	public static synchronized WorkingSetManager getWorkingSetManager() {
		if (getInstance().workingSetManager == null) {
			getInstance().workingSetManager = new WorkingSetManager();
		}
		return getInstance().workingSetManager;
	}

	public static synchronized BookmarkManager getBookmarkManager() {
		if (getInstance().bookmarkManager == null) {
			getInstance().bookmarkManager = new BookmarkManager();
		}
		return getInstance().bookmarkManager;
	}

	/*
	 * Allows Help UI to plug-in a soft adapter that delegates all the work to
	 * the workbench browser support.
	 */
	public synchronized void setBrowserInstance(IBrowser browser) {
		this.browser = browser;
	}

	public static synchronized IBrowser getHelpBrowser(boolean forceExternal) {
		if (!forceExternal && !BrowserManager.getInstance().isAlwaysUseExternal()) {
			if (getInstance().internalBrowser == null) {
				getInstance().internalBrowser = BrowserManager.getInstance().createBrowser(false);
			}
			return getInstance().internalBrowser;
		}
		if (getInstance().browser == null) {
			getInstance().browser = BrowserManager.getInstance().createBrowser(true);
		}
		return getInstance().browser;
	}

	public static synchronized HelpDisplay getHelpDisplay() {
		if (getInstance().helpDisplay == null)
			getInstance().helpDisplay = new HelpDisplay();
		return getInstance().helpDisplay;
	}

	/*
	 * Shuts down the BaseHelpSystem.
	 */
	public static void shutdown() throws CoreException {
		if (getInstance().bookmarkManager != null) {
			getInstance().bookmarkManager.close();
			getInstance().bookmarkManager = null;
		}
		if (getInstance().searchManager != null) {
			getInstance().searchManager.close();
			getInstance().searchManager = null;
		}
		if (getInstance().webappStarted) {
			// stop the web app
			WebappManager.stop("help"); //$NON-NLS-1$
		}
	}

	/**
	 * Called by Platform after loading the plugin
	 */
	public static void startup() {
		try {
			setDefaultErrorUtil(new IErrorUtil() {
				public void displayError(String msg) {
					System.out.println(msg);
				}
				public void displayError(String msg, Thread uiThread) {
					System.out.println(msg);
				}
			});
			HelpBasePlugin.getDefault().getPluginPreferences();
		}
		catch (Exception e) {
			HelpBasePlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, HelpBasePlugin.PLUGIN_ID, 0,
							"Error launching help.", e)); //$NON-NLS-1$
		}
		
		/*
         * Assigns the provider responsible for providing help
         * document content.
         */
		HelpPlugin.getDefault().setHelpProvider(new HelpProvider());
	}

	public static boolean ensureWebappRunning() {
		if (!getInstance().webappStarted) {
			getInstance().webappStarted = true;
			try {
				// start the help web app
				WebappManager.start("help"); //$NON-NLS-1$
			} catch (CoreException e) {
				HelpBasePlugin.logError("The embedded application server could not run help web application.", e); //$NON-NLS-1$
				BaseHelpSystem.getDefaultErrorUtil().displayError(HelpBaseResources.HelpWebappNotStarted);
				return false;
			}
			getInstance().webappRunning = true;
		}
		return getInstance().webappRunning;
	}

	public static URL resolve(String href, boolean documentOnly) {
		String url = null;
		if (href == null || href.indexOf("://") != -1) //$NON-NLS-1$
			url = href;
		else {
			BaseHelpSystem.ensureWebappRunning();
			String base = getBase(documentOnly);
			if (href.startsWith("/")) //$NON-NLS-1$
				url = base + href;
			else
				url = base + "/" + href; //$NON-NLS-1$
		}
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public static URL resolve(String href, String servlet) {
		String url = null;
		if (href == null || href.indexOf("://") != -1) { //$NON-NLS-1$
			url = href;
		}
		else {
			BaseHelpSystem.ensureWebappRunning();
			String base = getBase(servlet);
			if (href.startsWith("/")) { //$NON-NLS-1$
				url = base + href;
			}
			else {
				url = base + "/" + href; //$NON-NLS-1$
			}
		}
		try {
			return new URL(url);
		}
		catch (MalformedURLException e) {
			return null;
		}
	}

	public static String unresolve(URL url) {
		return unresolve(url.toString());
	}

	public static String unresolve(String href) {
		String[] baseVariants = { getBase("/help/topic"), //$NON-NLS-1$
				getBase("/help/nftopic"),  //$NON-NLS-1$
				getBase("/help/ntopic"),  //$NON-NLS-1$
				getBase("/help/rtopic") }; //$NON-NLS-1$
		for (int i = 0; i < baseVariants.length; i++) {
			if (href.startsWith(baseVariants[i])) {
				return href.substring(baseVariants[i].length());
			}
		}
		return href;
	}

	private static String getBase(boolean documentOnly) {
		String servlet = documentOnly ? "/help/nftopic" : "/help/topic";//$NON-NLS-1$ //$NON-NLS-2$
		return getBase(servlet);
	}

	private static String getBase(String servlet) {
		return "http://" //$NON-NLS-1$
				+ WebappManager.getHost() + ":" //$NON-NLS-1$
				+ WebappManager.getPort() + servlet;
	}

	/*
	 * Returns the mode of operation.
	 */
	public static int getMode() {
		return getInstance().mode;
	}

	/*
	 * Sets the mode of operation.
	 */
	public static void setMode(int mode) {
		getInstance().mode = mode;
		HelpSystem.setShared(mode == MODE_INFOCENTER);
	}

	/*
	 * Sets the error messenger
	 */
	public static void setDefaultErrorUtil(IErrorUtil em) {
		getInstance().defaultErrorMessenger = em;
	}

	/*
	 * Returns the default error messenger. When no UI is present, all errors
	 * are sent to System.out.
	 */
	public static IErrorUtil getDefaultErrorUtil() {
		return getInstance().defaultErrorMessenger;
	}

	/**
	 * Obtains name of the Eclipse product
	 * 
	 * @return String
	 */
	public static String getProductName() {
		IProduct product = Platform.getProduct();
		if (product == null) {
			return ""; //$NON-NLS-1$
		}
		String name = product.getName();
		return name == null ? "" : name; //$NON-NLS-1$
	}

	private static boolean initializeRTL() {
		// from property
		String orientation = System.getProperty("eclipse.orientation"); //$NON-NLS-1$
		if ("rtl".equals(orientation)) { //$NON-NLS-1$
			return true;
		} else if ("ltr".equals(orientation)) { //$NON-NLS-1$
			return false;
		}
		// from command line
		String[] args = Platform.getCommandLineArgs();
		for (int i = 0; i < args.length; i++) {
			if ("-dir".equalsIgnoreCase(args[i])) { //$NON-NLS-1$
				if ((i + 1) < args.length
						&& "rtl".equalsIgnoreCase(args[i + 1])) { //$NON-NLS-1$
					return true;
				}
				return false;
			}
		}

		// Check if the user property is set. If not do not
		// rely on the vm.
		if (System.getProperty("osgi.nl.user") == null) //$NON-NLS-1$
			return false;

		// guess from default locale
		String locale = Platform.getNL();
		if (locale == null) {
			locale = Locale.getDefault().toString();
		}
		if (locale.startsWith("ar") || locale.startsWith("fa") //$NON-NLS-1$//$NON-NLS-2$
				|| locale.startsWith("he") || locale.startsWith("iw") //$NON-NLS-1$//$NON-NLS-2$
				|| locale.startsWith("ur")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}
	
	public static boolean isRTL() {
		return getInstance().rtl;
	}

	public static void runLiveHelp(String pluginID, String className, String arg) {	
		Bundle bundle = Platform.getBundle(pluginID);
		if (bundle == null) {
			return;
		}
	
		try {
			Class c = bundle.loadClass(className);
			Object o = c.newInstance();
			//Runnable runnable = null;
			if (o != null && o instanceof ILiveHelpAction) {
				ILiveHelpAction helpExt = (ILiveHelpAction) o;
				if (arg != null)
					helpExt.setInitializationString(arg);
				Thread runnableLiveHelp = new Thread(helpExt);
				runnableLiveHelp.setDaemon(true);
				runnableLiveHelp.start();
			}
		} catch (ThreadDeath td) {
			throw td;
		} catch (Exception e) {
		}
	}
}