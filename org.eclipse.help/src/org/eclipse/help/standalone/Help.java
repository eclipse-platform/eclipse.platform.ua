/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.standalone;

import java.io.File;
import java.util.*;

import org.eclipse.help.internal.standalone.StandaloneHelp;

/**
 * This is a standalone help system. It takes care of 
 * launching the eclipse with its help system implementation,
 * and controling it.
 * This class can be used instantiated and used in a Java program,
 * or can be launched from command line to execute single help action.
 * 
 * Usage as a Java component: 
 * <ul>
 * <li> create an instantance of this class and then hold onto 
 * this instance for the duration of your application</li>
 * <li> call start() </li>
 * <li> call displayHelp(...) or displayContext(..) any number of times </li>
 * <li> at the end, call shutdown(). </li>
 * </ul>
 */
public class Help {
	private StandaloneHelp help;
	
	/**
	* Constructs help system
	* @param options array of String options and their values
	* 	Option <code>-eclipseHome dir</code> specifies Eclipse
	*  installation directory.
	*  It must be provided, when current directory is not the same
	*  as Eclipse installation directory.
	*  Additionally, most options accepted by Eclipse execuable are supported.
	*/
	public Help(String[] options) {
		help = new StandaloneHelp(options);
	}
	/**
	 * This contstructs the stand alone help.
	 * @param pluginsDir directory containing Eclipse plugins
	 */
	public Help(String pluginsDir) {
		File plugins = new File(pluginsDir);
		String install = plugins.getParent();
		ArrayList options = new ArrayList(2);
		if (install != null) {
			options = new ArrayList(2);
			options.add("-eclipseHome");
			options.add(install);
		}
		String[] args = new String[options.size()];
		options.toArray(args);
		help = new StandaloneHelp(args);
	}
	/**
	 * Starts the infocenter application.
	 */
	public void start() {
		help.start();
	}
	/**
	 * Shuts-down the infocenter application.
	 */
	public void shutdown() {
		help.shutdown();
	}
	/**
	 * Displays help.
	 */
	public void displayHelp() {
		help.displayHelp();
	}

	/**
	 * Displays specified help resource.
	 * @param href the href of the table of contents
	 */
	public void displayHelp(String href) {
		help.displayHelp(href);
	}

	/**
	 * Displays context sensitive help.
	 * @param contextId context id
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void displayContext(String contextId, int x, int y) {
		help.displayContext(contextId, x, y);
	}

	/**
	 * Displays context sensitive help in infopop.
	 * @param contextId context id
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void displayContextInfopop(String contextId, int x, int y) {
		help.displayContextInfopop(contextId, x, y);
	}

	/**
	 * Controls standalone help system from command line.
	 * @param args array of String containng options
	 *  Options are:
	 * 	<code>-command start | shutdown | (displayHelp [href]) [-eclipsehome eclipseInstallPath] [platform options] [-vmargs [Java VM arguments]]</code>
	 *  where
	 *  <ul>
	 *  <li><code>href</code> is the URL of the help resource to display,</li>
	 * 	<li><code>dir</code> specifies Eclipse installation directory;
	 * 	  it must be provided, when current directory is not the same
	 *    as Eclipse installation directory,</li>
	 *   <li><code>platform options</code> are other options that are supported by Eclipse Executable.</li>
	 *  <ul>
	 */
	public static void main(String[] args) {
		StandaloneHelp.main(args);
	}
}
