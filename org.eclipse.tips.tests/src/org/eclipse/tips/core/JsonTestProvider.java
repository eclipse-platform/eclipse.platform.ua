/*******************************************************************************
 * Copyright (c) 2018 Remain Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     wim.jongman@remainsoftware.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.tips.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.tips.json.JsonTipProvider;

public class JsonTestProvider extends JsonTipProvider {

	public JsonTestProvider() throws MalformedURLException {
		URL resource = getClass().getResource("twittertips.json"); // $NON-NLS-1$
		if (resource != null) {
			setJsonUrl(resource.toString());
		}
	}

	@Override
	public String getID() {
		return getClass().getName();
	}
}