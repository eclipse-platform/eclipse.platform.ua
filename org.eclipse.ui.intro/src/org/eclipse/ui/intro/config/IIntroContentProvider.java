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
 * content provider site because a typical a content provider would need to
 * update its contents dynamically at runtime. And so, the site can be informed
 * of a need to redraw its content through a call to its reflow method.
 * <p>
 * The life cycle of an IIntroContentProvider is as follows:
 * <ul>
 * <li>a content provider is defined in the Intro content markup file (ie:
 * introContent.xml file) as follows:
 * 
 * <p>
 * &lt;contentProvider id=&quot;roles&quot;
 * class=&quot;x.y.z.ContentProvider&quot;&gt; <br>
 * &lt;text&gt;Some alternate text for dynamic content&lt;/text&gt; <br>
 * &lt;/contentProvider&gt;
 * </p>
 * 
 * This defines the content provider as part of the intro content for that page.
 * </li>
 * <li>init() is called to initialize the instance of the class on load of the
 * page. This is where the provider site can be cacched for later reuse.</li>
 * <li>then createContent is actually called to give the content provider a
 * chance to generate the dynamic content. This is when the dynamic content can
 * be cached for later reuse when the same page is shown again.</li>
 * <li>finally, when the intro view is closed, dispose will be called on all
 * this content provider to give it a chance to clean up.</li>
 * 
 * @since 3.0.1
 */
public interface IIntroContentProvider {
    /**
     * Initializes the content provider. An IIntroContentProviderSite is passed,
     * which will be called on to recompute or layout the content when the
     * content becomes stale.
     * 
     * @param site
     *            the site of this IIntroContentProvider
     */
    public void init(IIntroContentProviderSite site);


    /**
     * Creates HTML content in the provided PrintWriter. This content will be
     * included in the generated HTML page when embedded HTML widget is used to
     * render intro content.
     * 
     * @param id
     *            the unique identifier of the content element. The same content
     *            provider class can be reused for several elements and the id
     *            can be used to tell them apart.
     * @param out
     *            the output print writer to generate HTML content into
     */
    public void createContent(String id, PrintWriter out);

    /**
     * Creates SWT content in the provided Composite. This method is called when
     * Eclipse Forms are used to render intro content.
     * 
     * @param id
     *            the unique identifier of the content element
     * @param parent
     *            the parent composite that should be used when creating SWT
     *            widgets
     * @param toolkit
     *            the form toolkit that should be used when creating new widgets
     */
    public void createContent(String id, Composite parent, FormToolkit toolkit);


    /**
     * Dispose of the ContentProvider. This will only be called when the Intro
     * view is closed. In other words, the content provider will not be disposed
     * of until the last possible minute. This gives the implementor the chance
     * to cache content and avoid regenerating content on every page switch.
     */
    public void dispose();

}