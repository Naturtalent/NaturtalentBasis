package it.naturtalent.e4.search;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "it.naturtalent.e4.search.messages"; //$NON-NLS-1$
	public static String SearchDialog_tbtmProject_text;
	public static String SearchDialog_lblNewLabel_text;

	//////////////////////////////////////////////////////public static String SearchDialog_text_text;
	public static String SearchDialog_group_text;
	public static String SearchDialog_btnRadioWorkspace_text;
	public static String SearchDialog_btnRadioWorkingSet_text;
	public static String SearchDialog_btnBrowse_text;
	public static String ProjectSearchComposite_btnCaseSensitive_text;
	//////////////////////
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
