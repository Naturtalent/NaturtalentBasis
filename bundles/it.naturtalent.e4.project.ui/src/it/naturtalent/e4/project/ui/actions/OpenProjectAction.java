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
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
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
	
	private IResourceNavigator resourceNavigator;
	
	private Shell shell;
	
	
	

	@PostConstruct
	public void postConstruct(@Named(IServiceConstants.ACTIVE_SHELL) @Optional Shell shell)
	{
		this.shell = shell;
				
		iResource = null;		
		Object selObject = selectionService.getSelection(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if(selObject instanceof IResource)
			iResource = (IResource)selObject;		
		
		MApplication application = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		EModelService modelService = (EModelService) context.get(EModelService.class.getName());
		MPart part = (MPart) modelService.find(ResourceNavigator.RESOURCE_NAVIGATOR_ID, application);
		resourceNavigator = (IResourceNavigator)part.getObject();		
	}

	@Override
	public void run()
	{		
		if (iResource != null)
		{
			if (iResource.getType() == IResource.PROJECT)
			{
				// ProjectWizard erzeugen
				ProjectPropertyWizard projectPropertyWizard = ContextInjectionFactory
						.make(ProjectPropertyWizard.class, context);

				projectPropertyWizard.setiProject((IProject) iResource);

				// die dem Projekt zugeordneten PropertyFactories uebergeben
				List<INtProjectPropertyFactory> propertyFactories = NtProjektPropertyUtils
						.getProjectPropertyFactories(
								ntProjektDataFactoryRepository,
								(IProject) iResource);
				if ((propertyFactories != null)
						&& (!propertyFactories.isEmpty()))
					projectPropertyWizard.setPropertyFactories(propertyFactories);

				// Projekt oeffnen
				WizardDialog wizardDialog = new WizardDialog(shell,projectPropertyWizard);
				wizardDialog.open();
			}
			else
			{				
				if (iResource.getType() == IResource.FILE)
				{
					// momentan ist eine Dateien selektiert
					SystenOpenEditorAction action = ContextInjectionFactory
							.make(SystenOpenEditorAction.class, context);
					action.run();
				}
				else
				{					
					if (iResource.getType() == IResource.FOLDER)
					{
						// momentan ist ein Verzeichnis selektiert
						TreeViewer treeViewer = resourceNavigator.getViewer();
						if (!treeViewer.getExpandedState(iResource))
							treeViewer.expandToLevel(iResource, 1);
						else
							treeViewer.collapseToLevel(iResource, 1);
					}
				}
			}
		}
	}
	
}
