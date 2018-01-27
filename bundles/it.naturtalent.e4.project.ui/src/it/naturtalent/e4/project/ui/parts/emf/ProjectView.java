package it.naturtalent.e4.project.ui.parts.emf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.emf.ecp.core.util.observer.ECPObserverBus;
import org.eclipse.emf.ecp.core.util.observer.ECPProjectContentChangedObserver;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.actions.emf.DeleteProjectAction;
import it.naturtalent.e4.project.ui.actions.emf.SaveProjectAction;
import it.naturtalent.e4.project.ui.actions.emf.SyncNavigatorAction;
import it.naturtalent.e4.project.ui.actions.emf.UndoProjectAction;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;




public class ProjectView
{
	public static final String ID = "it.naturtalent.e4.project.ui.part.emf.ProjectView";

	public static final String OPEN_PROJECTPATH_ACTION_ID = "openprojectpath";
	public static final String COPYCLIPBOARD_PROJECTPATH_ACTION_ID = "copyclipboardprojectpath";
	
	private @Inject @Optional  IEclipseContext context;
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	private ProjectViewDetailsComposite projectDetailsComposite;
	
	private IEventBroker eventBroker;
	
	private CommandStackListener commandListener;
	private EditingDomain domain;
	
	// das im Detail dargestellte Projekt
	private NtProject ntProject;
	
	// modifizierts Projekt (wird CommandStackListener gesetzt)
	private NtProject modifiedProject;
	
	public enum ViewActionID
	{
		UNDO_ACTION,
		IMPORT_SOCKETS,
		EXPORT_SOCKETS,
		SOCKET_SWITCHER,
		RECONNECT_TASK,
		SOCKETHACKER_TASK,		
		SAVE_ACTION,
		SYNC_NAVIGATOR_ACTION,
	}
	private Map<ViewActionID, Action>actionRegistry = new HashMap<ViewActionID, Action>();
	
	private ProjectModelChangeObserver projectModelChangedObserver;
	
	/*
	 * 'ECPProjectContentChangedObserver' Überwacht Aenderungen im Modell
	 */
	private class ProjectModelChangeObserver implements ECPProjectContentChangedObserver
	{
		@Override
		public Collection<Object> objectsChanged(ECPProject project,Collection<Object> objects)
		{
			// Aenderungen am Modell werden vie Broker weitergemeldet	
			eventBroker.send(UndoProjectAction.PROJECTCHANGED_MODELEVENT, "projectModelData changed");			
			return null;
		}

	}
	
	
	/**
	 * 
	 */
	public ProjectView()
	{
		
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent,
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject,
			@Optional IEventBroker eventBroker, 
			@Optional INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository,
			@Optional EPartService partService,
			@Optional ECPObserverBus eCPObserverBus)
	{
		this.eventBroker = eventBroker;
		
		projectDetailsComposite = new ProjectViewDetailsComposite(parent, SWT.NONE);
		projectDetailsComposite.setNtProjektDataFactoryRepository(ntProjektDataFactoryRepository);
			
		MPart mPart = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		ResourceNavigator navigator = (ResourceNavigator) mPart.getObject();
		TreeViewer treeViewer = navigator.getTreeViewer();
		
		// Toolbar Aktionen hinzufuegen	
		Section sectionComposite = projectDetailsComposite.getSectionComposite();			
		createSectionToolbar(sectionComposite);
				
		projectModelChangedObserver = new ProjectModelChangeObserver();
		ECPUtil.getECPObserverBus().register(projectModelChangedObserver);
	}
	
	private void createSectionToolbar(Section section)
	{
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);		
		section.setTextClient(toolbar);
		
		final Cursor handCursor = new Cursor(Display.getCurrent(),SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (handCursor.isDisposed() == false)				
					handCursor.dispose();				
			}
		});
	
		Action action;
		if (context != null) // Reminiszenz an windowsbuilder
		{
			// UNDO
			action = ContextInjectionFactory.make(UndoProjectAction.class, context);			
			toolBarManager.add(action);
			actionRegistry.put(ViewActionID.UNDO_ACTION, action);
			
			// SAVE
			action = ContextInjectionFactory.make(SaveProjectAction.class,context);
			toolBarManager.add(action);
			actionRegistry.put(ViewActionID.SAVE_ACTION, action);
			
			// SYNC Navigator
			action = ContextInjectionFactory.make(SyncNavigatorAction.class,context);
			toolBarManager.add(action);
			actionRegistry.put(ViewActionID.SYNC_NAVIGATOR_ACTION, action);

		}	

		toolBarManager.update(true);		
	}
	
	/**
	 * Die im Navigator selektierte Resource wird gemeldet.
	 *  
	 * @param selectedResource
	 */
	@Inject
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION)@Optional IResource selectedResource)
	{
		if(selectedResource != null)
		{
			IProject iProject = selectedResource.getProject();
			ntProject = Activator.findNtProject(iProject.getName());
			projectDetailsComposite.showDetails(ntProject);
		}
	}
	
	@Inject
	@Optional
	public void handleDeleteProjectEvent(@UIEventTopic(DeleteProjectAction.DELETE_PROJECT_EVENT) String message)
	{
		System.out.println("ProjectView.handleDeleteProjectEvent()  "+"ProjectView-306- gelöscht wurde: "+message);
		projectDetailsComposite.showDetails(null);
	}
	
	@PreDestroy
	public void dispose()
	{
		formToolkit.dispose();	
		
		if((commandListener != null) && (domain != null))
			domain.getCommandStack().removeCommandStackListener(commandListener);
	}

	@Focus
	public void setFocus()
	{
		// TODO	Set the focus to control
	}

	public NtProject getNtProject()
	{
		return ntProject;
	}
	
	
	
}
