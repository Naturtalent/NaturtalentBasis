package it.naturtalent.e4.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import it.naturtalent.application.Activator;
import it.naturtalent.application.IPreferenceAdapter;

public class ApplicationPreferences
{

	public static void initPreferences()
	{
		String progDir = System.getProperty(Activator.NT_PROGRAM_HOME);
		File file = new File(progDir,"temp"); //$NON-NLS-N$
				
		if(!file.exists())
			file.mkdir();
		
		if(file.exists())
		{			
			IEclipsePreferences defaultNode = DefaultScope.INSTANCE
					.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
			defaultNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, file.getPath());
			
			IEclipsePreferences instanceNode = InstanceScope.INSTANCE
					.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
			instanceNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, file.getPath());
		}

		// Loggerfilepfad ermitteln und als Praeferenz speichern
		IEclipsePreferences instancePreferenceNode = InstanceScope.INSTANCE.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
		//String logFilePath = System.getProperty(it.naturtalent.application.Activator.NT_PROGRAM_HOME);
		String logFilePath = progDir+File.separator+"logs"+File.separator+"naturtalent.log";
		instancePreferenceNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_LOGGERFILE_KEY, logFilePath);

	}

}
