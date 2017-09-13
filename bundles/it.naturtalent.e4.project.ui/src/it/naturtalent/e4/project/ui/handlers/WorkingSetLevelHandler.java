package it.naturtalent.e4.project.ui.handlers;


import it.naturtalent.e4.project.ui.NtPreferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;

/**
 * Schaltet die Anzeige der Elemente auf der obersten Ebene im Navigator
 * 
 * @author apel.dieter
 *
 */
public class WorkingSetLevelHandler
{	
	@Execute
	public void execute(
			MDirectMenuItem directHandleMenu,
			@Preference(nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
			IEclipsePreferences preferences)
	{
		// 'true' WorkingSets anzeigen
		TopLevelMenuUtils.setTopLevelMenus(preferences, directHandleMenu, true);
	}
	
	
	
}
