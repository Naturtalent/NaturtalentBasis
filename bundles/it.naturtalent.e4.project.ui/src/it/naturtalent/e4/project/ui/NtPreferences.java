package it.naturtalent.e4.project.ui;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class NtPreferences
{
	
	public static final String ROOT_PREFERENCES_NODE = "it.naturtalent.e4.application"; //$NON-NLS-1$
	
	// WorkingSets Preferences
	public static final String WORKINGSET_AS_TOPLEVEL = "WORKINGSET_AS_TOPLEVEL";	//$NON-NLS-1$
	public static final String STORE_WORKING_SET = "STORE_WORKING_SET"; //$NON-NLS-1$
	
	// Vorlagen bei der Erzeugung neuer Dateien (s. NewFileDialog) 
	public static final String FILE_TEMPLATE_PREFERENCE = "filetemplatepreference"; //$NON-NLS-1$
	public static final String DEFAULT_FILE_TEMPLATE = "Text,.txt"; //$NON-NLS-1$
	
	public static void initialize()
	{
		IEclipsePreferences defaultNode = DefaultScope.INSTANCE
				.getNode(ROOT_PREFERENCES_NODE);
		defaultNode.putBoolean(WORKINGSET_AS_TOPLEVEL, false);
		
		String defaultValue = defaultNode.get(NtPreferences.FILE_TEMPLATE_PREFERENCE, null);
		defaultValue = StringUtils.isNotEmpty(defaultValue) ? defaultValue
				+ DEFAULT_FILE_TEMPLATE : DEFAULT_FILE_TEMPLATE; 
		defaultNode.put(FILE_TEMPLATE_PREFERENCE, defaultValue);
		
		
	}
}
