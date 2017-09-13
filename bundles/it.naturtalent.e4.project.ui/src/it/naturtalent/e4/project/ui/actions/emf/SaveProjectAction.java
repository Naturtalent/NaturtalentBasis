package it.naturtalent.e4.project.ui.actions.emf;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.emf.ProjectModelEventKey;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.parts.emf.ProjectView;
import it.naturtalent.emf.model.actions.DefaultModelAction;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class SaveProjectAction extends Action
{

	@Inject
	private IEventBroker eventBroker;
	
	@Optional @Inject 
	private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	
	private ESelectionService selectionService;
	
	private IEclipseContext context;
	
	private EPartService partService;
	
	private Log log = LogFactory.getLog(SaveProjectAction.class);
	
	
	public SaveProjectAction()
	{
		setImageDescriptor(Icon.COMMAND_SAVE.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		setEnabled(false);
	}
	
	@PostConstruct
	private void postConstruct(IEclipseContext context, EPartService partService,  ESelectionService selectionService)
	{
		this.context = context;
		this.partService = partService;
		this.selectionService = selectionService;
	}

	//@Override
	public void run()
	{		
		// wird im 'ProjectView' momentan die Eigenschaft eines Projekts angezeigt 
		ProjectView projectView = (ProjectView) partService.findPart(ProjectView.ID).getObject();
		NtProject ntProject = projectView.getNtProject();
		try
		{
			// die Projekteigenschaft 'name' im Workspaceprojekt 'IProject' persistent uebernehmen 			
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getName());			
			iProject.setPersistentProperty(INtProject.projectNameQualifiedName, ntProject.getName());
			
		} catch (Exception e)
		{
			log.error(e);
		}
		
		// gesamte ECP-Projekt wird gespeichert - ein entsprechendes Event wird abgestzt
		ECPHandlerHelper.saveProject(Activator.getECPProject());		
		eventBroker.send(UndoProjectAction.PROJECTCHANGED_MODELEVENT,"Model saved");
				
		// Projekt im Navigator updaten
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		EPartService partService  = currentApplication.getContext().get(EPartService.class);
		MPart mPart = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		ResourceNavigator resourceNavogator = (ResourceNavigator) mPart.getObject();
		resourceNavogator.getTreeViewer().refresh();
		
	}
	
	@Inject @Optional
	public void  getModelChangeEvent(@UIEventTopic(UndoProjectAction.PROJECTCHANGED_MODELEVENT) String message) 
	{		
		setEnabled(Activator.getECPProject().hasDirtyContents());		
	}
}
