package org.eclipse.help.servlet;

/*
 * (c) Copyright IBM Corp. 2002.
 * All Rights Reserved.
 */

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.core.runtime.*;
import org.eclipse.help.internal.*;

/**
 * Control for a toolbar.
 */
public class ToolbarData extends RequestData {

	ToolbarButton[] buttons;

	public ToolbarData(ServletContext context, HttpServletRequest request) {
		super(context, request);
		loadButtons();
	}

	private void loadButtons() {
		String[] names = request.getParameterValues("name");
		String[] tooltips = request.getParameterValues("tooltip");
		String[] images = request.getParameterValues("image");
		String[] actions = request.getParameterValues("action");
		String[] scripts = request.getParameterValues("script");

		if (names == null
			|| tooltips == null
			|| images == null
			|| actions == null
			|| scripts == null
			|| names.length != tooltips.length
			|| names.length != images.length
			|| names.length != actions.length
			|| names.length != scripts.length) {
			buttons = new ToolbarButton[0];
			return;
		}

		buttons = new ToolbarButton[names.length];
		for (int i = 0; i < buttons.length; i++) {
			if ("".equals(names[i]))
				buttons[i] = new ToolbarButton();
			else
				buttons[i] =
					new ToolbarButton(
						names[i],
						WebappResources.getString(tooltips[i], request),
						getPrefs().getImagesDirectory() + "/" + images[i],
						actions[i],
						scripts[i]);
		}
	}

	public ToolbarButton[] getButtons() {
		return buttons;
	}
}
