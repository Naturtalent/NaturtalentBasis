package it.naturtalent.e4.project.ui.actions;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizard;

/**
 * Aktion zum Oeffnen oder Aendern eines Projekts.
 * 
 * sollte momentan ein Folder selektiert sein (expand/collapse)
 * ist eine Datei selektiert, wird diese mit dem Systemeditor geoeffnet.
 * 
 * @author dieter
 *
 */
public class OpenProjectAction extends Action
{

	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	@Optional @Inject private IEclipseContext context;
	@Inject @Optional private ESelectionService selectionService;
	
	
	//private IProject iProject;
	//private IWorkingSet iWorkingSet;
	
	private IResource iResource;
	
	private Shell shell;
	
	
	

	@PostConstruct
	public void postConstruct(@Named(IServiceConstants.ACTIVE_SHELL) @Optional Shell shell)
	{
		this.shell = shell;
				
		iResource = null;		
		Object selObject = selectionService.getSelection(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if(selObject instanceof IResource)
			iResource = (IResource)selObject;				
	}

	@Override
	public void run()
	{		
		if ((iResource != null) && (iResource.getType() == IResource.PROJECT))
		{
			// ProjectWizard erzeugen
			ProjectPropertyWizard projectPropertyWizard = ContextInjectionFactory
					.make(ProjectPropertyWizard.class, context);

			projectPropertyWizard.setiProject((IProject) iResource);

			// die dem Projekt zugeordneten PropertyFactories uebergeben
			/*
			List<INtProjectPropertyFactory> propertyFactories = NtProjektPropertyUtils
					.getProjectPropertyFactories(ntProjektDataFactoryRepository,
							(IProject) iResource);
							*/
			List<INtProjectPropertyFactory> propertyFactories = 
					ntProjektDataFactoryRepository.getAllProjektDataFactories();
						
			if ((propertyFactories != null) && (!propertyFactories.isEmpty()))
				projectPropertyWizard.setPropertyFactories(propertyFactories);

			// Projekt oeffnen
			WizardDialog wizardDialog = new WizardDialog(shell,projectPropertyWizard);
			wizardDialog.open();
		}
	}

}
