package it.naturtalent.e4.preferences;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.application.ChooseWorkspaceData;
import it.naturtalent.application.IPreferenceAdapter;
import it.naturtalent.application.IPreferenceNode;;

/**
 * Praeferenzen einer Applikation
 * 
 * -temporaeres Verzeichnis
 * 
 * - Workspaces
 * 
 * @author dieter
 *
 */
public class ApplicationPreferenceAdapter extends AbstractPreferenceAdapter
{
	// unter diesem Knoten werden alle Applikationspreferenzen gespeichert
	//public static final String ROOT_APPLICATION_PREFERENCES_NODE = "it.naturtalent.application"; //$NON-NLS-1$
	
	// Key der Preferenz eines temporaeren Verzeichnisses 
	//public static final String PREFERENCE_APPLICATION_TEMPDIR_KEY = "preferenceapplicationtempdir"; //$NON-NLS-1$	
	//private String key = PREFERENCE_APPLICATION_TEMPDIR_KEY;
	
	// Key der Workspaces Praeferenz  
	//public static final String PREFERENCE_APPLICATION_WORKSPACE_KEY = "preferenceapplicationworkspace"; //$NON-NLS-1$	
	
	private Text tempDirEditor;
	
	private Text loggerFileText;
	
	private ListEditorComposite listWorkspaces;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public ApplicationPreferenceAdapter()
	{
		// Praeferenzknoten fuer diesen Adapter
		instancePreferenceNode = InstanceScope.INSTANCE.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
		defaultPreferenceNode = DefaultScope.INSTANCE.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
	}

	@Override
	public String getLabel()
	{	
		return "Applikation";
	}

	@Override
	public String getNodePath()
	{
		return null;
	}

	@Override
	public void restoreDefaultPressed()
	{
		// temporares Verzeichnis
		String value = defaultPreferenceNode.get(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, null);
		if(StringUtils.isNotEmpty(value))
			tempDirEditor.setText(value);
		
		// LoggerFile (kein Restore erforderlich, Variable ist statisch = SystemProperty "nt.programm.home")
		
		// Defaultworkspace = current Workspace @see PreferenceAddOn
		value = getDefaultPreference().get(IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY,null);	
		listWorkspaces.setValues(value);	
	}

	@Override
	public void appliedPressed()
	{
		// Praeferenz temporaeres Verzeichnis
		String value = tempDirEditor.getText();
		if(StringUtils.isNotEmpty(value))
			instancePreferenceNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, value);
		
		// Praeferenz Loggerfile (SystemProperty 'nt.programm.home' = Variable des Loggerfile Directory)
		value = loggerFileText.getText();
			instancePreferenceNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_LOGGERFILE_KEY, value);
		
		// Praeferenz WorkingSpaces
		value = listWorkspaces.getValues();
		if(StringUtils.isNotEmpty(value))
			instancePreferenceNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY, value);
		
		try
		{
			// Praeferenzen festschreiben
			instancePreferenceNode.flush();
		} catch (Exception e)
		{			
			log.error(e);	
		}
		
	}

	@Override
	public void okPressed()
	{
		appliedPressed();
	}
	
	private static final String CMD_DATA = "-data";

	@Override
	public Composite createNodeComposite(IPreferenceNode parentNodeComposite)
	{
		String workspaceLoc;
		
		// Titel
		parentNodeComposite.setTitle(getLabel());
		
		ApplicationPreferenceComposite referenceComposite = new ApplicationPreferenceComposite(
				parentNodeComposite.getParentNode(), SWT.NONE);	
		
		// Prefaerenz temporaeres Verzeichnis
		tempDirEditor = referenceComposite.getTempDirEditor();
		String value = getInstancePreference().get(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, null);
		if(StringUtils.isEmpty(value))
			value = getDefaultPreference().get(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, null);		
		if(StringUtils.isNotEmpty(value))
			tempDirEditor.setText(value);
		
		// Praeferenz Loggerfile
		loggerFileText = referenceComposite.getTextLoggerFile();
		value = getInstancePreference().get(IPreferenceAdapter.PREFERENCE_APPLICATION_LOGGERFILE_KEY, null);		
		if(StringUtils.isEmpty(value))
		{
			value = System.getProperty(it.naturtalent.application.Activator.NT_PROGRAM_HOME);
			value = value+File.separator+"logs"+File.separator+"naturtalent.log";
		}
		loggerFileText.setText(value);
		
		// Prefaerenz Workspaces
		listWorkspaces = referenceComposite.getListWorkspaces();
		value = instancePreferenceNode.get(IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY,null);
		if(StringUtils.isEmpty(value))			
			value = getDefaultPreference().get(IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY,null);	
		if(StringUtils.isNotEmpty(value))
			listWorkspaces.setValues(value);		
		else // keine Defaultpraeferenz gespeichert
		{
			// Versuch Workspacelocation aus der Eclipse-Kommandozeile zu extrahieren
			String systemCommandLine = System.getProperty("eclipse.commands"); //$NON-NLS-N$
			
			int start = StringUtils.indexOf(systemCommandLine, CMD_DATA);
			if(start > 0)
			{
				// der Workspaceeintrag beginnt hinter dem CMD_DATA
				start = start + StringUtils.length(CMD_DATA);				
				workspaceLoc = StringUtils.substring(systemCommandLine, start);
				
				// der Workspaceeintrag endet am Beginn des naechsten Commands
				int end = StringUtils.indexOf(workspaceLoc, '-');
				if(end > 0)
					workspaceLoc = StringUtils.substring(workspaceLoc, 1, end);

				// aktuelle Workspace als Defaultpraeferenz speichern und in der Liste eintragen
				getDefaultPreference().put(IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY, workspaceLoc);
				listWorkspaces.setValues(workspaceLoc);				
			}
			
		}
		
		return referenceComposite;
	}

}
