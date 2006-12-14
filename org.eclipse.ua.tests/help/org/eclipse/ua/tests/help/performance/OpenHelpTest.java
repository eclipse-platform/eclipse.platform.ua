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
package org.eclipse.ua.tests.help.performance;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.help.internal.HelpPlugin;
import org.eclipse.help.internal.appserver.WebappManager;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceTestCase;
import org.eclipse.ui.PlatformUI;

public class OpenHelpTest extends PerformanceTestCase {

	private Shell shell;
	
	/*
	 * Returns an instance of this Test.
	 */
	public static Test suite() {
		return new TestSuite(OpenHelpTest.class);
	}

	public void testOpenHelp() throws Exception {
		tagAsGlobalSummary("Open help", Dimension.ELAPSED_PROCESS);

		// warm-up
		for (int i=0;i<3;++i) {
			openHelp();
			closeHelp();
		}
		
		// run the tests
		for (int i=0;i<50;++i) {
			startMeasuring();
			openHelp();
			stopMeasuring();
			closeHelp();
		}
		
		commitMeasurements();
		assertPerformance();
	}
	
	private void openHelp() throws Exception {
		// make sure we're not using cached tocs
		HelpPlugin.getTocManager().reset();

		// start the webapp
		BaseHelpSystem.ensureWebappRunning();
		
		// open a browser
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		shell = new Shell(parent);
		shell.setLayout(new FillLayout());
		shell.setSize(parent.getSize());
		Browser browser = new Browser(shell, SWT.NONE);
		shell.open();
		
		// open help url
		final boolean[] done = new boolean[] { false };
		final String url = "http://" + WebappManager.getHost() + ":" + WebappManager.getPort() + "/help/index.jsp";
		browser.addLocationListener(new LocationAdapter() {
			public void changed(LocationEvent event) {
				if (url.equals(event.location)) {
					done[0] = true;
				}
			}
		});
		browser.setUrl(url);
		
		// wait until the browser finishes loading
		Display display = Display.getDefault();
		while (!done[0]) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		flush();
	}
	
	private void closeHelp() throws Exception {
		if (shell != null) {
			shell.dispose();
			shell = null;
		}
	}
	
	private static void flush() {
		Display display = Display.getCurrent();
		while (display.readAndDispatch()) {
		}
	}
}
