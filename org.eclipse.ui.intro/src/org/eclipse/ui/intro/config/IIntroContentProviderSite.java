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

/**
 * An interface between a content provider and its parent container. A content
 * provider is responsible for creating dynamic intro content, while the content
 * provider site is responsible for reflowing the new content in the intro part.
 * An intro content provider site may have more than one content provider. The
 * id of the content provider can be used to distinguish the source of the
 * reflow.
 * <p>
 * This interface is not intended to be implemented or extended by clients.
 */
public interface IIntroContentProviderSite {
    /**
     * This method will be called when the IIntroContentProvider is notified
     * that it's content has become stale. For an HTML presentation, the whole
     * page should be regenerated. An SWT presentation should cause the page's
     * layout to be updated.
     * 
     * @param provider
     * @param incremental
     */
    public void reflow(IIntroContentProvider provider, boolean incremental);
}