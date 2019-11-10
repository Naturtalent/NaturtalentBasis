package it.naturtalent.e4.search;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "it.naturtalent.e4.search.messages"; //$NON-NLS-1$
	
	public static String PatternConstructor_error_escape_sequence;
	public static String PatternConstructor_error_hex_escape_sequence;
	public static String PatternConstructor_error_line_delim_position;
	public static String PatternConstructor_error_unicode_escape_sequence;
	
	public static String SearchDialog_tbtmProject_text;
	public static String SearchDialog_lblNewLabel_text;

	//////////////////////////////////////////////////////public static String SearchDialog_text_text;
	public static String SearchDialog_group_text;
	public static String SearchDialog_btnRadioWorkspace_text;
	public static String SearchDialog_btnRadioWorkingSet_text;
	public static String SearchDialog_btnBrowse_text;
	public static String ProjectSearchComposite_btnCaseSensitive_text;
	public static String ProjectSearchComposite_checkCaseSensitiv;
	public static String ProjectSearchComposite_checkRegularExpression;
	public static String ProjectSearchComposite_lblNewLabel_text;
	public static String PropertySearchComposite_group_text;
	public static String PropertySearchComposite_btnRadioDate_text;
	public static String PropertySearchComposite_btnRadioSince_text;
	public static String PropertySearchComposite_btnRadioBefore_text;
	public static String PropertySearchComposite_btnRadioBetween_text;
	public static String PropertySearchComposite_btnRadioButton_text;
	public static String PropertySearchComposite_btnRadioButton_1_text;
	public static String PropertySearchComposite_dateTime_pattern;
	
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
