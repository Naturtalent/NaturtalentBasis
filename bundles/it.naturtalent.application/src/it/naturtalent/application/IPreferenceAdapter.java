package it.naturtalent.application;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Composite;

/**
 * @author dieter
 *
 */
public interface IPreferenceAdapter
{
	
	// unter diesem Knoten werden alle Applikationspreferenzen gespeichert
	public static final String ROOT_APPLICATION_PREFERENCES_NODE = "it.naturtalent.application"; //$NON-NLS-1$
	
	// Key der Preferenz eines temporaeren Verzeichnisses 
	public static final String PREFERENCE_APPLICATION_TEMPDIR_KEY = "preferenceapplicationtempdir"; //$NON-NLS-1$	
	//private String key = PREFERENCE_APPLICATION_TEMPDIR_KEY;

	// Key der Logger Praeferenz  
	public static final String PREFERENCE_APPLICATION_LOGGERFILE_KEY = "preferenceapplicationloggerfile"; //$NON-NLS-1$	

	// Key der Workspaces Praeferenz  
	public static final String PREFERENCE_APPLICATION_WORKSPACE_KEY = "preferenceapplicationworkspace"; //$NON-NLS-1$	

	
	public String getLabel();
	
	//public String getKey ();
	
	public String getNodePath ();
	
	public IEclipsePreferences getDefaultPreference ();
	
	public void setDefaultPreference (IEclipsePreferences defaultPreference);

	public IEclipsePreferences getInstancePreference ();
	
	public void setInstancePreference (IEclipsePreferences instancePreference);

	public void restoreDefaultPressed ();
	
	public void appliedPressed ();
	
	public void okPressed ();
	
	public void cancelPressed ();
	
	public Composite createNodeComposite(IPreferenceNode parentNodeComposite);
}
