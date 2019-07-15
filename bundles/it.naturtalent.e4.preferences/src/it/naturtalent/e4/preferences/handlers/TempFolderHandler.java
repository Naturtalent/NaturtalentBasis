package it.naturtalent.e4.preferences.handlers;


import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import it.naturtalent.application.IPreferenceAdapter;

/**
 * Oeffnet ueber den Systembroser ein Verzeichnis fuer temp. Dateien. Das konkrete Verzeichnis im Dateisystem wird in den
 * Preferenzen definiert.
 * Gleichzeitig wird dieser Verzeichnispfad auch im Zwischenverzeichnuis (Clipboard) abgelegt.
 *  
 * @author dieter
 *
 */
public class TempFolderHandler
{
	// der benutzte Zwischenspeicher
	private static final Clipboard clipboard  = new Clipboard(Display.getDefault());
	
	@Execute
	public void execute(
			@Preference(value = IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, nodePath = IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE)
			String tempDir)
	{
		
		// tmpDir im Clipboard speichern
		clipboard.setContents(new Object[]
			{ tempDir }, new Transfer[]
			{ TextTransfer.getInstance() });
		
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