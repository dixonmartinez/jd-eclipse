/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.ide.eclipse;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @project Java Decompiler Eclipse Plugin
 * @version 0.1.4
 */
public class JavaDecompilerPlugin extends AbstractUIPlugin {
	// The plug-in IDs
	public  static final String PLUGIN_ID = "jd.ide.eclipse";
	public static final String EDITOR_ID = PLUGIN_ID + ".editors.JDClassFileEditor";	
	
	// Versions
	public static final String VERSION_JD_ECLIPSE = "2.0.0";
	public static final String VERSION_JD_CORE    = "1.0.7";

	// Preferences
	public static final String PREF_ESCAPE_UNICODE_CHARACTERS   = PLUGIN_ID + ".prefs.EscapeUnicodeCharacters";
	public static final String PREF_REALIGN_LINE_NUMBERS        = PLUGIN_ID + ".prefs.RealignLineNumbers";
	public static final String PREF_SHOW_LINE_NUMBERS           = PLUGIN_ID + ".prefs.ShowLineNumbers";
	public static final String PREF_SHOW_METADATA               = PLUGIN_ID + ".prefs.ShowMetadata";

	// URLs
	public static final String URL_JDECLIPSE = "https://github.com/java-decompiler/jd-eclipse";
	
	// The shared instance
	private static JavaDecompilerPlugin plugin;
	
	
	/**
	 * The constructor
	 */
	public JavaDecompilerPlugin() {}
	
	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// Setup ".class" file associations
		Display.getDefault().syncExec(new SetupClassFileAssociationRunnable());
	}

	/*
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static JavaDecompilerPlugin getDefault() {
		return plugin;
	}		
}