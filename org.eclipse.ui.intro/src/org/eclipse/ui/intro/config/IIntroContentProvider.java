/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.intro.config;

import java.io.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.*;

/**
 * A content provider for dynamic intro content. It is initialized with the
 * content provider site because a typical a content provider would to update
 * its contents dynamically, at runtime.
 */
public interface IIntroContentProvider {
    /**
     * Initializes the content provider. An IIntroContentProviderSite is passed,
     * which will be called on to recompute or layout the content when the
     * content becomes stale.
     * 
     * @param parent
     *            the parent of this IIntroContentProvider
     */
    public void init(IIntroContentProviderSite parent);

    /**
     * Create HTML content in the provided PrintWriter. This content will be
     * included in the generated HTML page.
     * 
     * @param id
     * @param out
     */
    public void createContent(String id, PrintWriter out);

    /**
     * Create SWT content in the provided Composite.
     * 
     * @param id
     * @param parent
     * @param toolkit
     */
    public void createContent(String id, Composite parent, FormToolkit toolkit);


    /**
     * Returns the id of the Intro contentProvider tag that defined this content
     * provider. This can/should be cached from the init() method above and
     * returned by this method.
     * 
     * @return
     */
    public String getId();

    /**
     * Dispose of the ContentProvider
     */
    public void dispose();

}