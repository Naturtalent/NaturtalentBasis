package it.naturtalent.e4.preferences.handlers;


import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;

import it.naturtalent.application.IPreferenceAdapter;

public class TempFolderHandler
{
	@Execute
	public void execute(
			@Preference(value = IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, nodePath = IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE)
			String tempDir)
	{
		try
		{				
			if (SystemUtils.IS_OS_LINUX)
				Runtime.getRuntime().exec("nautilus " + tempDir);
			else
				Runtime.getRuntime().exec("explorer " + tempDir);

		} catch (Exception exp)
		{
			if (SystemUtils.IS_OS_LINUX)
				try
				{
					Runtime.getRuntime().exec("nemo " + tempDir);
					return;
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			exp.printStackTrace();
		}

	}

	@CanExecute
	public boolean canExecute()
	{
		// TODO Your code goes here
		return true;
	}

}