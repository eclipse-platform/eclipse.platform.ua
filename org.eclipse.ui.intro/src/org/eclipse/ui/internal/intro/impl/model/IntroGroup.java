/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.intro.impl.model;

import java.util.Enumeration;
import java.util.Map;

import org.eclipse.ui.intro.config.IntroElement;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * An intro div.
 */
public class IntroGroup extends AbstractIntroContainer {

    protected static final String TAG_GROUP = "group"; //$NON-NLS-1$
    private static final String ATT_LABEL = "label"; //$NON-NLS-1$
    private static final String ATT_COMPUTED = "computed"; //$NON-NLS-1$
    private static final String ATT_EXPANDABLE = "expandable"; //$NON-NLS-1$
    private static final String ATT_EXPANDED = "expanded"; //$NON-NLS-1$
    private static final String P_UPPERCASE = "capitalizeTitles"; //$NON-NLS-1$
    private String label;
    /**
     * @param element
     */
    IntroGroup(Element element, Bundle bundle, String base) {
        super(element, bundle, base);
    }
    
    protected void loadFromParent() {
    }
    
    private void resolve() {
    	// reinitialize if there are variables in the value.
    	if (label==null) {
    		label = getAttribute(element, ATT_LABEL);
    		if (label!=null) {
    			IntroModelRoot root = getModelRoot();
    			if (root!=null && root.getTheme()!=null) {
    				Map props = root.getTheme().getProperties();
    				String value = (String)props.get(P_UPPERCASE);
    				if (value!=null && value.equalsIgnoreCase("true")) //$NON-NLS-1$
    					label = label.toUpperCase();
    			}
    		}
   		}
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {
    	resolve();
        return label;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.intro.impl.model.IntroElement#getType()
     */
    public int getType() {
        return AbstractIntroElement.GROUP;
    }
    
    public boolean isExpandable() {
    	String value=getAttribute(element, ATT_EXPANDABLE);
    	return value!=null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
    }
    
    public boolean isExpanded() {
    	String value=getAttribute(element, ATT_EXPANDED);
    	return value!=null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
    }
    
    protected void loadChildren() {
    	String value = getAttribute(element, ATT_COMPUTED);
    	if (value!=null && value.equalsIgnoreCase("true")) //$NON-NLS-1$
    		loadDynamicNodes();
    	super.loadChildren();
    }

    private void loadDynamicNodes() {
    	IntroModelRoot root = getModelRoot();
    	if (root==null)
    		return;
    	AbstractIntroPage page = getParentPage();
    	String pageId = page.getId();
    	IntroElement [] nodes = root.getConfigurer().getGroupChildren(pageId, getId());
    	addDynamicNodes(this.element, nodes);
    }
  
    private void addDynamicNodes(Element target, IntroElement [] nodes) {
    	for (int i=0; i<nodes.length; i++) {
    		IntroElement node = nodes[i];
    		addDynamicNode(target, node);
    	}
    }
    private void addDynamicNode(Element target, IntroElement node) {
    	// clone node itself
    	Element clone = target.getOwnerDocument().createElement(node.getName());
    	// set attributes
    	Enumeration atts = node.getAttributes();
    	for (;atts.hasMoreElements();) {
    		String aname = (String)atts.nextElement();
    		String avalue = node.getAttribute(aname);
    		clone.setAttribute(aname, avalue);
    	}
    	// set value
    	String value = node.getValue();
    	if (value!=null) {
    		Text textNode = target.getOwnerDocument().createTextNode(value);
    		clone.appendChild(textNode);
    	}
    	// clone children
    	IntroElement [] cnodes = node.getChildren();
    	if (cnodes.length>0)
    		addDynamicNodes(clone, cnodes);
    	// add the clone to the target
    	target.appendChild(clone);
    }
}