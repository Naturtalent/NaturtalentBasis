 
package it.naturtalent.e4.preferences.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.preferences.ApplicationPreferenceAdapter;
import it.naturtalent.e4.preferences.PreferenceDialog;

/**
 * Handler oeffnet Dialog mit den definierten Praeferenzen.
 * 
 * @author dieter
 *
 */
public class PreferenceHandler
{
	
	// ID des Handlers 
	public static final String PREFERENCEHANDLER_ID = "it.naturtalent.e4.preferences.handler.0";
	
	@Inject
	@Optional
	private IEclipseContext context;
	
	// Workbench sichern, falls ein PreferenceAdaper den Neustart der Application realisieren moechte
	@Inject @Optional private IWorkbench workbench;
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
		PreferenceDialog dialog = ContextInjectionFactory.make(PreferenceDialog.class, context);
		dialog.open();
	}

	public IWorkbench getWorkbench()
	{
		return workbench;
	}
	
	

}