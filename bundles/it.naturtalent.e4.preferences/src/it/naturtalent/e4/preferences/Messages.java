package it.naturtalent.e4.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "it.naturtalent.e4.preferences.messages"; //$NON-NLS-1$

	public static String ListPreferenceComposite_Edit;

	public static String ListPreferenceComposite_InputErrorEmptyField;

	public static String ListPreferenceComposite_InputLabel;

	public static String ListPreferenceComposite_InputLabelTitel;

	public static String ListPreferenceComposite_Remove;

	public static String PreferenceDialog_WindowTitle;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
