package org.eclipse.help.servlet;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
public class View {
	private String name;
	private String url;
	private String imageURL;
	private boolean visible;
	
	public View(String name, String url, String imageURL, boolean visible) {
		this.name = name;
		this.url = url;
		this.imageURL = imageURL;
		this.visible = visible;
	}
	
	public String getName() {
		return name;
	}
	
	public String getURL() {
		return url;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
