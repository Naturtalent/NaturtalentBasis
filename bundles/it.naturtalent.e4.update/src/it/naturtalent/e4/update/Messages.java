package it.naturtalent.e4.update;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "it.naturtalent.e4.update.messages"; //$NON-NLS-1$
	public static String InstallLocationDialog_lblNewLabel_text;
	public static String InstallLocationDialog_this_title;
	public static String InstallLocationDialog_this_message;
	public static String InstallLocationDialog_btnNewButton_text;
	////////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	////////////////////////////////////////////////////////////////////////////
	private Messages() {
		// do not instantiate
	}
	////////////////////////////////////////////////////////////////////////////
	//
	// Class initialization
	//
	////////////////////////////////////////////////////////////////////////////
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
