package it.naturtalent.application.handlers;
 

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;

import it.naturtalent.application.ChooseWorkspaceData;
import it.naturtalent.application.IPreferenceAdapter;
import it.naturtalent.application.dialogs.ChooseWorkspaceDialog;


public class SwitchWorkspaceHandler
{	
	@Optional @Inject private Logger logger;
	@Optional @Inject private IWorkbench workbench;
	
	@Execute
	public void execute(IEclipseContext context,
			@Preference(nodePath = IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE, value = IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY) String prefList) 
	{		
		

		try
		{
			ChooseWorkspaceData chooseWorkspaceData = new ChooseWorkspaceData();
			File file  = chooseWorkspaceData.getLauncherIniFile();
			
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		logger.info("S Y S C O M M A N D L I N E: "+System.getProperty("eclipse.commands"));

		
		File file = new ChooseWorkspaceData().getLauncherIniFile();
		logger.info("I N I F I L E: "+file);
		

		String workspace = new ChooseWorkspaceData().getCurrentWorkspaceLocation();
		logger.info("S Y S T E M - W O R K S P A C E: "+workspace);
		
		try
		{
			String [] workspaces = new ChooseWorkspaceData().getWorkspaceLocations();
			logger.info("I N I - W O R K S P A C E: "+workspaces);
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		// Workspaceauswahldialog aufrufen
		ChooseWorkspaceDialog dialog = ContextInjectionFactory.make(ChooseWorkspaceDialog.class, context);
		dialog.create();
		dialog.setComboItems(prefList);
		if (dialog.open() == ChooseWorkspaceDialog.OK)
		{
			// Abbruch, wenn die selektierte Workspaceloation gleich der
			// momentan genutzten ist
			String workspaceLocation = dialog.getSelectedWorkspaceLocation();			
			if (!StringUtils.equals(workspaceLocation,new ChooseWorkspaceData().getCurrentWorkspaceLocation()))
			{
				// neue WorkspaceLocation als Praeferenz speichern 
				String [] preferenceArray = StringUtils.split(prefList, ",");
				if(!ArrayUtils.contains(preferenceArray, workspaceLocation))
				{
					preferenceArray = ArrayUtils.add(preferenceArray, workspaceLocation);
					prefList = StringUtils.join(preferenceArray, ",");
					IEclipsePreferences instancePreferenceNode = InstanceScope.INSTANCE.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
					instancePreferenceNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY, prefList);
					try
					{
						instancePreferenceNode.flush();
					} catch (BackingStoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error(e);
					}
				}
				workbench.restart();
			}
		}
		
	} 
	
	
	@CanExecute 
	public boolean canExecute(MPart part)
	{
		// eclipse.ini der IDE Eclipse darf nicht veraendert werden		
		File file  =  new ChooseWorkspaceData().getLauncherIniFile();
		return (file != null);
	}

	
	/**
	 * Rueckgabe des Inifiles.
	 * 
	 * @return
	 */
	
	/*
	private static final String CMD_LAUNCHER = "-launcher"; //$NON-NLS-1$
	private static final String CMD_NAME = "-name"; //$NON-NLS-1$
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$
	private static final String CMD_DATA = "-data"; //$NON-NLS-1$
	private static final String COMMAND_PREFIX = "-"; //$NON-NLS-1$

	
	public String getInitCommandLine(String systemCommandLine) throws Exception
	{
		File file = getLauncherIniFile(systemCommandLine);
		if (file.exists())
		{			
			String property = FileUtils.readFileToString(file);
			
			return property;
		}
		return null;
	}
	

	
	public File getLauncherIniFile(String commandLine)
	{
		String baseName;
		File launcherIniFile = null;				
		
		String property = getProperty(commandLine, CMD_LAUNCHER);		
		if(StringUtils.isNotEmpty(property))
		{
			baseName = FilenameUtils.getBaseName(property)+".ini";			
			File launcherFile = new File(property);			
			if(launcherFile.isDirectory())
				launcherIniFile = new File(launcherFile,baseName);
			else
			{				
				String path = launcherFile.getParent();				
				launcherIniFile = new File(path,baseName);
			}

			if(!launcherIniFile.exists())
				return null;
		}
		
		return launcherIniFile;
	}

	public String getProperty(String commandLine, String command)
	{
		String prop = null;
		
		int pos = StringUtils.indexOf(commandLine, command);
		prop = StringUtils.substring(commandLine, pos + command.length());
		if(prop.startsWith(NEW_LINE))
			prop = StringUtils.substring(prop, 1);
		pos = StringUtils.indexOf(prop,NEW_LINE);
		if(pos != (-1))
			prop = StringUtils.substring(prop, 0, pos);		
		return prop;
	}
	
	*/
	
	

}