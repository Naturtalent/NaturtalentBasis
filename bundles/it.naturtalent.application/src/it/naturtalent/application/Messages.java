package it.naturtalent.application;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "it.naturtalent.application.messages"; //$NON-NLS-1$
	public static String NewWizardDialog_this_title;
	public static String Activator_IncorrectWorkspaceStructure;
	public static String ApplicationPreferenceComposite_lblTemp_text;
	public static String ApplicationPreferenceComposite_txtTemp_text;
	public static String ApplicationPreferenceComposite_btnSelect_text;
	public static String ApplicationPreferenceComposite_lbl_text;
	public static String ApplicationPreferenceComposite_lblWorkspace_text;
	public static String ApplicationPreferenceComposite_btnNewButton_text;
	public static String ApplicationPreferenceComposite_lblLogger_text;

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
