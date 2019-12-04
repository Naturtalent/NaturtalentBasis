 
package it.naturtalent.e4.project.ui.handlers.emf;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.emf.CheckNtKontakteDialog;

/**
 * Hilfsklasse dient der einmaligen Umstellung von ProjectData auf PropertyData
 * 
 * @author dieter
 *
 */
public class CheckNtKontaktHandler
{	
	
	private final static String PROJECTPROPERTYFILE = "propertyData.xml";
	private final static String PROJECTDATAFILE = "projectData.xml";
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL)@Optional Shell shell )
	{
		CheckNtKontakteDialog checkDialog = new CheckNtKontakteDialog(shell);
		checkDialog.open();
	}
	
	@CanExecute
	public boolean canExecute()
	{
		NtProjects ntProjects = Activator.getNtProjects();
		return (ntProjects != null);
	}
	
	
	private void doMigrate()
	{		
	}
}