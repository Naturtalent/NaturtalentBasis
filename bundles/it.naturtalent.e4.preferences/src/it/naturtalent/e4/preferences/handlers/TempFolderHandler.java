package it.naturtalent.e4.preferences.handlers;


import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;

import it.naturtalent.application.IPreferenceAdapter;
import it.naturtalent.e4.preferences.ApplicationPreferenceAdapter;


public class TempFolderHandler
{
	@Execute
	public void execute(
			@Preference(value = IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, nodePath = IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE)
			String tempDir)
	{
		try
		{
			String os = System.getProperty("os.name");					
			if(StringUtils.containsIgnoreCase(os,"linux"))
				Runtime.getRuntime().exec("nautilus " + tempDir);
			else
				Runtime.getRuntime().exec("explorer " + tempDir);
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@CanExecute
	public boolean canExecute()
	{
		// TODO Your code goes here
		return true;
	}

}