/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.standalone;

import org.eclipse.help.internal.standalone.StandaloneInfocenter;

/**
 * This program is used to start or stop Eclipse
 * Infocenter application.
 * It should be launched from command line.
 */
public class Infocenter {
	/**
	 * Private constructor
	 */
	private Infocenter() {
	}
	/**
	 * Controls start up and shut down of infocenter from command line.
	 * @param args array of String containng options
	 *  Options are:
	 * 	<code>-command start | shutdown [-eclipsehome eclipseInstallPath] [platform options] [-vmargs [Java VM arguments]]</code>
	 *  where
	 *  <ul>
	 * 	<li><code>dir</code> specifies Eclipse installation directory;
	 * 	  it must be provided, when current directory is not the same
	 *    as Eclipse installation directory,</li>
	 *   <li><code>platform options</code> are other options that are supported by Eclipse Executable.</li>
	 *  <ul>
	 */
	public static void main(String[] args) {
		StandaloneInfocenter.main(args);
	}
}
