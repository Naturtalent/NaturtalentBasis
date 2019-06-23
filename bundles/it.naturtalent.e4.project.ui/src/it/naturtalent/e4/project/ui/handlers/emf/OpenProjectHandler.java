 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ui.wizards.OpenProjectWizard;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizard;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizardPage;

/**
 * @author A682055
 * 
 * Handler zum Oeffnen bestehender Projekte.
 */
@Deprecated //see it.naturtalent.e4.project.ui.actions,OpenProjectAction
public class OpenProjectHandler
{
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	@Optional @Inject private IEclipseContext context;
	
	@Execute
	public void execute(Shell shell, @Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject)
	{	
		// ProjectWizard erzeugen
		ProjectPropertyWizard openWizard = ContextInjectionFactory.make(ProjectPropertyWizard.class, context);
	
		if(iProject != null)		
			openWizard.setiProject(iProject); // null - da neues Projekt
		
		// die projekteigenen PropertyFactories an WizardPage uebergeben
		List<INtProjectPropertyFactory> propertyFactories = NtProjektPropertyUtils
				.getProjectPropertyFactories(ntProjektDataFactoryRepository,iProject);		
		if((propertyFactories != null) && (!propertyFactories.isEmpty()))
			openWizard.setPropertyFactories(propertyFactories);
		
		// Wizard oeffnen
		WizardDialog wizardDialog = new WizardDialog(shell, openWizard);
		wizardDialog.open();

	}
	
	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject)
	{		
		return (iProject != null);
	}
	
}