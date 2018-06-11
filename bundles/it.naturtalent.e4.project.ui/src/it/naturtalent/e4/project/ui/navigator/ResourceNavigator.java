package it.naturtalent.e4.project.ui.navigator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkingSet;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NaturtalentConstants;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.NtPreferences;
import it.naturtalent.e4.project.ui.WorkbenchImages;
import it.naturtalent.e4.project.ui.actions.OpenProjectAction;
import it.naturtalent.e4.project.ui.actions.SystenOpenEditorAction;
import it.naturtalent.e4.project.ui.datatransfer.LocalSelectionTransfer;
import it.naturtalent.e4.project.ui.datatransfer.MailTransfer;
import it.naturtalent.e4.project.ui.datatransfer.NavigatorDragAdapter;
import it.naturtalent.e4.project.ui.datatransfer.NavigatorDropAdapter;
import it.naturtalent.e4.project.ui.datatransfer.PluginTransfer;
import it.naturtalent.e4.project.ui.datatransfer.ResourceTransfer;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.filters.ClosedProjectFilter;
import it.naturtalent.e4.project.ui.filters.DefaultResourceSorter;
import it.naturtalent.e4.project.ui.filters.HiddenResourceFilter;
import it.naturtalent.e4.project.ui.filters.ResourceFilterProvider;
import it.naturtalent.e4.project.ui.filters.ResourceWorkingSetFilter;
import it.naturtalent.e4.project.ui.handlers.CopyResourceHandler;
import it.naturtalent.e4.project.ui.handlers.DeleteResourceHandler;
import it.naturtalent.e4.project.ui.handlers.NewFolderHandler;
import it.naturtalent.e4.project.ui.handlers.PasteResourceHandler;
import it.naturtalent.e4.project.ui.handlers.ProjectLevelHandler;
import it.naturtalent.e4.project.ui.handlers.RefreshHandler;
import it.naturtalent.e4.project.ui.handlers.RenameResourceHandler;
import it.naturtalent.e4.project.ui.handlers.SetCloseProjectFilterHandler;
import it.naturtalent.e4.project.ui.handlers.TestProjectHandler;
import it.naturtalent.e4.project.ui.handlers.TopLevelMenuUtils;
import it.naturtalent.e4.project.ui.handlers.WorkingSetConfigurationHandler;
import it.naturtalent.e4.project.ui.handlers.WorkingSetLevelHandler;
import it.naturtalent.e4.project.ui.parts.emf.ProjectView;
import it.naturtalent.e4.project.ui.ws.AggregateWorkingSet;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSet;
import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;

public class ResourceNavigator implements IResourceNavigator
{
	
	// ID ExtensionPoint 'natures'
	//public static final String NATURE_ID = "it.naturtalent.projekt.ProjectNature";
	
	//public static final String NATURE_ID = "it.naturtalent.telekom.TelekomNature";
	
	// mit dieser ID im ApplicatonModel eingetragen
	public static final String RESOURCE_NAVIGATOR_ID = "it.naturtalent.e4.project.ui.part.explorer"; //$NON-NLS-1$

	// ID des PopUpMenues
	public static final String RESOURCE_NAVIGATOR_POPUPMENUE_ID = "it.naturtalent.e4.project.popupmenu.explorer"; //$NON-NLS-1$
	public static final String RESOURCE_NAVIGATOR_OPENWITHMENUE_ID = "it.naturtalent.e4.project.ui.menu.openwith"; //$NON-NLS-1$	
	

	@Inject
	@Optional
	private IEclipseContext context;
	
	@Inject
	@Optional
	private ESelectionService selectionService;

	@Inject
	@Optional
	private EPartService partService;
	
	@Inject
	@Preference(value = NtPreferences.STORE_WORKING_SET, nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
	String prefStoredWorkingSet;
	
	@Optional 
	@Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	private MPart partNavigator;
	
	// der benutzte Zwischenspeicher
	private Clipboard clipboard;

	private List<NtProject>projects;
	
	protected TreeViewer treeViewer;
	private Tree tree;
	
	// die verwendeten Filter
	private ResourceFilterProvider filterProvider;
	private ResourceWorkingSetFilter workingSetFilter;
	
	
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	private IWorkingSetManager workingSetManager = Activator.getWorkingSetManager();
	
	// Inhalt des Viewers (in einem einzelnen WorkingSet aggregierte Ressourcen)	
	private AggregateWorkingSet aggregateResourceSet;
	
	// WorkingSet-Name der aggregierten Ressourcen mit diesem Namen in den Settings gespeichert 
	private String aggregatedResourceSetName; 
	
	// Key-Konstante mit dem das WorkingSet gespeichert wird 
	private static final String STORE_WORKING_SET = "ResourceWorkingSetFilter.STORE_WORKING_SET"; //$NON-NLS-1$
	
	// Array der aggergierten WorkingSets
	private IWorkingSet [] aggregatedWorkingSets;
	
	private static WorkingSetRoot workingSetRoot = new WorkingSetRoot();
	
		
	private Composite parent;
	
	private TreeColumn trclmnNewColumn; 
	
	private Listener dragDetectListener;
	
	private boolean dragDetected;
	
		
	@Inject
	@Optional
	private IEventBroker eventBroker;

	private EventHandler eventHandler;


	
    
    
    /**
 	 * Marks whether the working set we're using is currently empty. In this
 	 * event we're effectively not using a working set.
 	 */
     private boolean emptyWorkingSet = false;

	private boolean topLevelFlag;
	
	private EventHandler navigatorEventHandler = new EventHandler()
	{ 
		@Override
		public void handleEvent(Event event)
		{
			Object eventData = event.getProperty(IEventBroker.DATA);
			if(StringUtils.equals(event.getTopic(), IResourceNavigator.NAVIGATOR_EVENT_IMPORTED))
			{
				if(eventData instanceof Set)
				{
					Set<IProject>importedProjects = (Set<IProject>) event.getProperty(IEventBroker.DATA);
					
					/*
					if(aggregateResourceSet == null)
						initDefaultResources();
					*/
				}
			}
			
			if(StringUtils.equals(event.getTopic(), IResourceNavigator.NAVIGATOR_EVENT_SELECT_PROJECT))
			{
				if (eventData instanceof String)
				{
					String projectKey = (String) eventData;									
					IAdaptable [] adaptables = getAggregateWorkingSet().getElements();
					if(ArrayUtils.isNotEmpty(adaptables))
					{
						for(IAdaptable adaptable : adaptables)
						{
							if (adaptable instanceof IProject)
							{
								IProject project = (IProject) adaptable;
								if(StringUtils.equals(projectKey, project.getName()))
								{
									if(getTopLevelStatus())
									{
										IWorkingSet[] workingSets = getWindowWorkingSets();
										for (IWorkingSet workingSet : workingSets)
										{
											IAdaptable[] elements = workingSet.getElements();
											for (IAdaptable element : elements)
											{
												if (element.equals(adaptable))
												{
													treeViewer.expandToLevel(workingSet, 1);
													break;
												}
											}
										}
									}
									
									treeViewer.setSelection(new StructuredSelection(adaptable),true);
									break;
								}								
							}
						}
					}					
				}				
			}
			
		}
	};

	private IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			String property = event.getProperty();
			Object newValue = event.getNewValue();
			Object oldValue = event.getOldValue();

		
			if (IWorkingSetManager.CHANGE_WORKING_SET_ADD.equals(property)
					&& !isWindowWorkingMember((IWorkingSet) newValue))
			{
				System.out.println("WorkingSet ADD"); //$NON-NLS-1$
			}
		

			if (IWorkingSetManager.CHANGE_WORKING_SET_REMOVE.equals(property)
					&& isWindowWorkingMember((IWorkingSet) newValue))
			{
				
				System.out.println("WorkingSet REMOVE"); //$NON-NLS-1$
				
				/*
				treeViewer
						.remove(getWindowWorkingSetByID(((IWorkingSet) newValue)
								.getId()));
								*/
			}
			else if (IWorkingSetManager.CHANGE_WORKING_SET_NAME_CHANGE
					.equals(property)
					&& isWindowWorkingMember((IWorkingSet) newValue))
			{
				// geanderten WorkingSetNamen anzeigen
				/*
				treeViewer.update(
						getWindowWorkingSetByID(((IWorkingSet) newValue)
								.getId()), null);
								*/
				System.out.println("WorkingSet NameChange"); //$NON-NLS-1$
			}
			else if (IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE
					.equals(property)
					&& isWindowWorkingMember((IWorkingSet) newValue))
			{
				/*
				treeViewer
						.refresh(getWindowWorkingSetByID(((IWorkingSet) newValue)
								.getId()));
								*/
				System.out.println("WorkingSet ContentChange"); //$NON-NLS-1$
			}
		}
	};
	
	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(final Composite parent,
			@Preference(value = NtPreferences.WORKINGSET_AS_TOPLEVEL, 
			nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
			
	boolean topLevelFlag, IResourceUtilities<?> resourceUtilities,EMenuService service, final MPart part, final EPartService partService)
	{
		this.parent = parent;		
		parent.setLayout(new GridLayout(1, false));
		
		this.partNavigator = part;
		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI);
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				// Reaktion auf Doppelclick
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();		
				Object selObject = selection.getFirstElement(); 
				
				if(selObject instanceof IWorkingSet)
				{
					// WorkingSet wird expandiert/kollabiert
					if(!treeViewer.getExpandedState(selObject))
						treeViewer.expandToLevel(selObject, 1);
					else
						treeViewer.collapseToLevel(selObject, 1);
				}
				else
				{
					switch (((IResource)selObject).getType())
						{
							case IResource.PROJECT:

								// Datei mit dem ProjectWizard oeffnen
								OpenProjectAction openProject = ContextInjectionFactory.make(OpenProjectAction.class, context);
								openProject.run();

								break;
								
							case IResource.FOLDER:
								
								// Folder wird expandiert/kollabiert
								if(!treeViewer.getExpandedState(selObject))
									treeViewer.expandToLevel(selObject, 1);
								else
									treeViewer.collapseToLevel(selObject, 1);
								
								break;
								
							case IResource.FILE:
								
								// Datei mit dem Systemeditor oeffnen
								SystenOpenEditorAction systemEditor = ContextInjectionFactory.make(SystenOpenEditorAction.class, context);
								systemEditor.run();

								break;

							default:
								break;
						} 
					
				}
				
				
				
				// Oeffnen der selektierten Resource
				//OpenResourceHandler openHandler = ContextInjectionFactory.make(OpenResourceHandler.class, context);
				//openHandler.execute(parent.getShell(),part);

			}
		});
		
		tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{		
				if (selectionService != null)
				{					
					autocommit();
					
					IStructuredSelection selection = ((IStructuredSelection) event.getSelection());
					Object selObj = selection.getFirstElement();
					if (selObj != null)
					{
						selectionService.setSelection(selObj);
						// @see SystemOpenHandler

						if (eventBroker != null)
							eventBroker.send(NAVIGATOR_EVENT_SELECTED,
									selection.toList());
						
						if (selObj instanceof IResource)
						{
							IResource iResource = (IResource) selObj;
							IProject iProject = iResource.getProject();
							Activator.projectQueue.addLast(iProject.getName());
						}
					}
				}					
			}
		});
		
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		trclmnNewColumn = treeViewerColumn.getColumn();
		//trclmnNewColumn.setText("test");
		trclmnNewColumn.setWidth(500);

		
		DefaultResourceSorter sorter = new DefaultResourceSorter(
				treeViewer, treeViewerColumn);
		sorter.setSorter(sorter, DefaultResourceSorter.ASC);
		treeViewer.setComparator(sorter);
		

		treeViewer.setContentProvider(new WorkbenchContentProvider());
		//treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		
	
		treeViewer.setLabelProvider(new DecoratingLabelProvider(
				new WorkbenchLabelProvider(), new WorkBenchLabelDecorator()));
	
		// popup Menue in Viewer eintragen
		service.registerContextMenu(treeViewer.getTree(),
				NaturtalentConstants.EXPLORER_POPUPMENU_ID);

		
		// Clipboard aktivieren
		clipboard = new Clipboard(Display.getDefault());
		
		initDragAndDrop();
		
		initFilter();
		
		initWindowWorkingSet(settings, STORE_WORKING_SET);
		this.topLevelFlag = topLevelFlag;
		setWorkingSets(aggregatedWorkingSets);
		
		if(eventBroker != null)	
			eventBroker.subscribe(IResourceNavigator.NAVIGATOR_EVENT+"*",navigatorEventHandler); 

		
		//refreshViewer(topLevelFlag);
		//initPopUp(service, part);		
		//setWorkingSets(workingSetManager.getWorkingSets());
		//setWorkingSets(workingSetManager.getWorkingSets());
		//workingSetManager.addPropertyChangeListener(propertyChangeListener);

	}
	
	/**
	 * Alle dem Projekt zugeordneten Properties speichern. Wenn ein Projekt selektiert wird, werden die
	 * Properties des bisher selektierten Projekts gespeichert. Ab jetzt ist auch kein 'undo' mehr moeglich.
	 * Als 'bisherig' wird das im Detailfenster 'ProjctView' angezeigte definiert. 
	 */
	private void autocommit()
	{
		// wird im 'ProjectView' momentan die Eigenschaft eines Projekts angezeigt 
		ProjectView projectView = (ProjectView) partService.findPart(ProjectView.ID).getObject();
		if (projectView != null)
		{
			NtProject ntProject = (NtProject) projectView.getNtProject();
			if (ntProject != null)
			{
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(ntProject.getId());
				List<INtProjectPropertyFactory> propertyFactories = NtProjektPropertyUtils
						.getProjectPropertyFactories(
								ntProjektDataFactoryRepository, iProject);

				if (propertyFactories != null)
				{
					for (INtProjectPropertyFactory propertyFactory : propertyFactories)
					{
						INtProjectProperty projectProperty = propertyFactory
								.createNtProjektData();
						projectProperty.setNtProjectID(iProject.getName());
						projectProperty.commit();

						// 'altes' Project im Navigator aktualisieren (Name
						// koennte ja geandert worden sein)
						if (projectProperty instanceof NtProjectProperty)
							treeViewer.refresh(iProject);
					}
				}
			}
		}
	}

	@PostConstruct
	void hookEvents(ESelectionService selectionService)
	{
		if(this.selectionService == null)
			this.selectionService = selectionService;
			
		/*
		if (eventBroker != null)
		{
			eventHandler = new EventHandler()
			{
				public void handleEvent(final Event event)
				{
					Object obj  = event.getProperty(WorkspaceEventConstants.WORKSPACEMODIFY_NEW_RESOURCES);
				}
			};
			eventBroker.subscribe(WorkspaceEventConstants.WORKSPACEMODIFY, eventHandler);
		}
		*/	
	}
	
	private void initFilter()
	{
		filterProvider = new ResourceFilterProvider();

		// WorkingSetFilter				
		workingSetFilter = new ResourceWorkingSetFilter();		
		filterProvider.addFilter(workingSetFilter);		

		filterProvider.addFilter(new ClosedProjectFilter());
		filterProvider.addFilter(new HiddenResourceFilter());
		//filterProvider.addFilter(new ProjectNatureFilter(NtProject.NATURE_ID));
		treeViewer.setFilters(filterProvider.getFilters());	
	}
	
	private void initDragAndDrop()
	{
		 int ops = DND.DROP_COPY | DND.DROP_MOVE  | DND.DROP_LINK;
		 
		 Transfer[] transfers = new Transfer[] {
	                LocalSelectionTransfer.getInstance(),
	                ResourceTransfer.getInstance(), FileTransfer.getInstance(),
	                PluginTransfer.getInstance(), MailTransfer.getInstance() };		 
		 treeViewer.addDragSupport(ops, transfers, new NavigatorDragAdapter(treeViewer));
		 
		 NavigatorDropAdapter adapter = new NavigatorDropAdapter(treeViewer);
	        adapter.setFeedbackEnabled(false);
	        treeViewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers, adapter);
		dragDetectListener = new org.eclipse.swt.widgets.Listener()
		{
			public void handleEvent(org.eclipse.swt.widgets.Event event)
			{
				dragDetected = true;
			}
		};
		treeViewer.getControl().addListener(SWT.DragDetect, dragDetectListener);
		 
	}

	// Wird aufgerufen durch Preference Tracking
	// 'topLevelFlag' aktualisieren und davon abhaengig den Viewer erneut initialisieren
	@Inject
	public void refreshViewer(@Preference(value = NtPreferences.WORKINGSET_AS_TOPLEVEL, nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
	boolean topLevelFlag)
	{		
		if((treeViewer == null) || (tree.isDisposed()))
			return;
				
		this.topLevelFlag = topLevelFlag;
		refreshViewer();
	}
	
	/*
	 * Initialisiert den Viewer abhaengig vom 'topLevelFlag' mit Resourcen oder WorkingSets 
	 */
	public void refreshViewer()	
	{
		// WorkingSetFilter setzen (unabhaengig vom 'topLevelFlag')
		workingSetFilter.setWorkingSet(aggregateResourceSet);
		trclmnNewColumn.setText(Messages.ResourceNavigator_ProjectLabel);
				
		if (!topLevelFlag)
		{	
			// Explorer zeigt Projekte direkt
			treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());	
		}
		else
		{	
			trclmnNewColumn.setText(Messages.ResourceNavigator_WorkingSetLabel);			
			
			// Explorer zeigt Projekte in WorkingSets
			workingSetRoot.setWorkingSets( (aggregatedWorkingSets != null) ? aggregatedWorkingSets :  new IWorkingSet[]{});
			treeViewer.setInput(workingSetRoot);
		}
	}	

	@PostConstruct
	public void init(MPart part,
			@Preference(value = NtPreferences.WORKINGSET_AS_TOPLEVEL, nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
			boolean topLevel)
	{
		// TopLevelMenue erzeugen und updaten		
		List<String>handlerURIs = new ArrayList<String>();
		handlerURIs.add(Activator.getResourceURI(ProjectLevelHandler.class));
		handlerURIs.add(Activator.getResourceURI(WorkingSetLevelHandler.class));		
		List<MDirectMenuItem> menus = TopLevelMenuUtils.createTopLevelMenus(part, handlerURIs);		
		TopLevelMenuUtils.updateTopLevelMenus(menus, topLevel);	
		
		// Menu WorkingSet konfigurieren
		List<MMenu> menues = part.getMenus();
		MMenu mainMenu = menues.get(0);
		MMenuSeparator  separator = MMenuFactory.INSTANCE.createMenuSeparator();
		mainMenu.getChildren().add(separator);
		
		MDirectMenuItem  workingSetConfigure = MMenuFactory.INSTANCE.createDirectMenuItem();
		workingSetConfigure.setLabel(Messages.ResourceNavigator_ConfigureWorkingSet);
		workingSetConfigure.setContributionURI(Activator.getResourceURI(WorkingSetConfigurationHandler.class));		
		mainMenu.getChildren().add(workingSetConfigure);		
		mainMenu.getChildren().add(MMenuFactory.INSTANCE.createMenuSeparator());
		
		workingSetConfigure = MMenuFactory.INSTANCE.createDirectMenuItem();
		workingSetConfigure.setLabel(Messages.ResourceNavigator_FilterCloseProject);	
		workingSetConfigure.setContributionURI(Activator.getResourceURI(SetCloseProjectFilterHandler.class));
		workingSetConfigure.setType(ItemType.CHECK);		
		mainMenu.getChildren().add(workingSetConfigure);
	}

	/**
	 * wird nicht benutzt
	 * 
	 */
	public void initPopUp(EMenuService service, MPart part)
	{
		// Liste der PartMenus ermitteln
		List<MMenu> partMenus = part.getMenus();
		if (partMenus != null)
		{
			for (MMenu partMenu : partMenus)
			{
				// PartPopup 'EXPLORER_POPUPMENU_ID' herausfiltern
				if (StringUtils.equals(partMenu.getElementId(),
						NaturtalentConstants.EXPLORER_POPUPMENU_ID))
				{					
					// PartPopup erweitern 
					List<MMenuElement> popupChildren = partMenu.getChildren();
					
					// Menu 'Neu'
					MMenu newMenu = MMenuFactory.INSTANCE.createMenu();
					newMenu.setLabel(Messages.ResourceNavigator_NewMenau);
					popupChildren.add(newMenu);					
					List<MMenuElement> newMenuechildren = newMenu.getChildren();

					// SubMenu 'Ordner'
					MDirectMenuItem newOrdnerMenu = MMenuFactory.INSTANCE
							.createDirectMenuItem();
					newOrdnerMenu.setLabel(Messages.ResourceNavigator_NewFolder);					
					newOrdnerMenu.setIconURI(Activator.getPlatformURI()+Activator.ICONS_RESOURCE_FOLDER+"/"+WorkbenchImages.IMG_PROJECT_FOLDER);					 //$NON-NLS-1$
					newOrdnerMenu.setContributionURI(Activator
							.getResourceURI(NewFolderHandler.class));					
					newMenuechildren.add(newOrdnerMenu);

					// Delete
					MDirectMenuItem deleteMenu = MMenuFactory.INSTANCE
							.createDirectMenuItem();
					deleteMenu.setLabel(Messages.ResourceNavigator_MenuDelete);															
					deleteMenu.setContributionURI(Activator
							.getResourceURI(DeleteResourceHandler.class));					
					popupChildren.add(deleteMenu);
					
					// Separator
					MMenuSeparator separatorMenu = MMenuFactory.INSTANCE
							.createMenuSeparator();
					popupChildren.add(separatorMenu);
					
					// Copy
					MDirectMenuItem copyMenu = MMenuFactory.INSTANCE
							.createDirectMenuItem();
					copyMenu.setLabel(Messages.ResourceNavigator_MenuCopy);															
					copyMenu.setContributionURI(Activator
							.getResourceURI(CopyResourceHandler.class));					
					popupChildren.add(copyMenu);

					// Paste
					MDirectMenuItem pasteMenu = MMenuFactory.INSTANCE
							.createDirectMenuItem();
					pasteMenu.setLabel(Messages.ResourceNavigator_MenuPaste);															
					pasteMenu.setContributionURI(Activator
							.getResourceURI(PasteResourceHandler.class));					
					popupChildren.add(pasteMenu);

					// Rename
					MDirectMenuItem renameMenu = MMenuFactory.INSTANCE
							.createDirectMenuItem();
					renameMenu.setLabel(Messages.ResourceNavigator_MenuRename);															
					renameMenu.setContributionURI(Activator
							.getResourceURI(RenameResourceHandler.class));					
					popupChildren.add(renameMenu);					
					
					// Refresh
					MDirectMenuItem refreshMenu = MMenuFactory.INSTANCE
							.createDirectMenuItem();
					refreshMenu.setLabel(Messages.ResourceNavigator_MenuRefresh);															
					refreshMenu.setContributionURI(Activator
							.getResourceURI(RefreshHandler.class));					
					popupChildren.add(refreshMenu);
					
					
					// Separator
					separatorMenu = MMenuFactory.INSTANCE
							.createMenuSeparator();
					popupChildren.add(separatorMenu);

					// Test
					MDirectMenuItem testMenu = MMenuFactory.INSTANCE
							.createDirectMenuItem();
					testMenu.setLabel("Test");															 //$NON-NLS-1$
					testMenu.setContributionURI(Activator
							.getResourceURI(TestProjectHandler.class));					
					popupChildren.add(testMenu);
					


	
					
					// PartPopUp via EMenuService dem SWT control zuordnen					
					service.registerContextMenu(treeViewer.getTree(),
							NaturtalentConstants.EXPLORER_POPUPMENU_ID);
							
					
					return;
				}
			}
		}
	}

	
	@PreDestroy
	public void dispose()
	{
		// Eventhandler abmelden
		eventBroker.unsubscribe(navigatorEventHandler);

		if (clipboard != null)
		{
			clipboard.dispose();
			clipboard = null;
		}
			
		//workingSetManager.removePropertyChangeListener(propertyChangeListener);
	}

	@PreDestroy
	void unhookEvents(
			@Preference(nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
			IEclipsePreferences preferences)
	{
		if (eventBroker != null && eventHandler != null)
		{
			eventBroker.unsubscribe(eventHandler);
		}
		
		 //workingSetManager.removePropertyChangeListener(propertyChangeListener);
	}

	@Focus
	public void setFocus(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
		// TODO	Set the focus to control
	}
	
	public void initWindowWorkingSet()
	{
		initWindowWorkingSet(settings, STORE_WORKING_SET);
	}
	
    /**
     * Die aggregierten Resourcen aller WorkingSets befinden sich mit NamenPrefix 'TAG_WINDOW_AGGREGATE' in einem
     * speziellen WorkingsSet. 
     *  
     * @param settings
     * @param settingKey
     */
    private void initWindowWorkingSet(IDialogSettings settings, String settingKey)
	{		    	
    	aggregateResourceSet = null;
    	aggregatedWorkingSets = null;
    	
    	// den gespeicherten Namen des aggregierten WorkingSets aus den Settings laden
		aggregatedResourceSetName = settings.get(settingKey);		
		if(StringUtils.isEmpty(aggregatedResourceSetName))
		{
			// nichts gespeichert - neuen Namen generieren und speichern
			aggregatedResourceSetName = IWorkingSetManager.TAG_WINDOW_AGGREGATE + System.currentTimeMillis(); 
			settings.put(settingKey, aggregatedResourceSetName);
			
			// Defaultinitialisierung mit den Workspace-Projekten
			initDefaultResources();			
		}
		else
		{
			// die Namen der zu aggregierenden WorkingSets ermitteln
			String[] aggregatedWorkingSetNames = workingSetManager
					.getAggregateWorkingSetNames(aggregatedResourceSetName);
			if (ArrayUtils.isNotEmpty(aggregatedWorkingSetNames))
			{
				// die WorkingSets in einem Array zusammenfassen
				for (String name : aggregatedWorkingSetNames)
				{
					IWorkingSet workingSet = workingSetManager
							.getWorkingSet(name);
					if (workingSet != null)
						aggregatedWorkingSets = (WorkingSet[]) ArrayUtils.add(
								aggregatedWorkingSets, workingSet);
				}

				// die Ressourcen der aggregierten WorkingSets in einem
				// separaten WorkingSet zusammenfassen
				aggregateResourceSet = (AggregateWorkingSet) workingSetManager
						.createAggregateWorkingSet(
								aggregatedResourceSetName,
								Messages.WorkbenchPage_workingSet_default_label,
								aggregatedWorkingSets);
			}
			else
			{
				// das gespeicherte, aggregierte WorkingSet ist 'leer'
				// alle bisher definierten WorkingSets aggregieren
				aggregatedWorkingSets = workingSetManager.getWorkingSets();
				if (aggregatedWorkingSets != null)
				{					
					aggregateResourceSet = (AggregateWorkingSet) workingSetManager
							.createAggregateWorkingSet(
									aggregatedResourceSetName,
									Messages.WorkbenchPage_workingSet_default_label,
									workingSetManager.getWorkingSets());
				}
				else
				{
					// es gibt noch keine WorkingSets
					// Defaultinitialisierung mit den Workspace-Projekten
					initDefaultResources();
				}
			}
		}
		//workingSetManager.updateOthers(aggregateResourceSet.getElements());
		
		workingSetManager.updateOthers();
		
	}
    
    /*
     *  Die Workspace-Projecte in ein ResourceSet einlesen, keine WorkingSets verfuegbar.
     *  
     */
    public void initDefaultResources()
	{    	
    	aggregatedWorkingSets = null;
		IAdaptable [] adapts = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		IWorkingSet allWS = new WorkingSet(
				aggregatedResourceSetName,
				Messages.WorkbenchPage_workingSet_default_label,
				adapts);		
		IWorkingSet [] initWS = new IWorkingSet[]{allWS};
		aggregateResourceSet = new AggregateWorkingSet(
				aggregatedResourceSetName,
				Messages.WorkbenchPage_workingSet_default_label,
				initWS);				
	}
    
    /*
     *  wurde ein geschlossenes Projekt wiedergeoffnet, muss dies nach
     *  'open()' in OpenProject auch in den Workingsets des Navigators
     *  aktualisiert werden
     */
    public void updateAfterReOpen(IAdaptable [] adapts)
	{    	    
    	IWorkingSet [] wsArray;
		for(IWorkingSet ws : aggregatedWorkingSets)
		{
			if(StringUtils.equals(ws.getName(),IWorkingSetManager.OTHER_WORKINGSET_NAME))
			{			
				wsArray = new IWorkingSet []{ws}; 
				for(IAdaptable adapt : adapts)
					workingSetManager.addToWorkingSets(adapt, wsArray);
				break;
			}
		}
	}
    
	/**
	 * Returns the tree viewer which shows the resource hierarchy.
	 * 
	 * @return the tree viewer
	 * @since 2.0
	 */
	public TreeViewer getTreeViewer()
	{
		return treeViewer;
	}

	@Override
	public IWorkingSet[] getWindowWorkingSets()
	{		
		return aggregatedWorkingSets;
	}

	@Override
	public IWorkingSet[] getWorkingSets()
	{		
		return workingSetManager.getWorkingSets();
	}
	
	@Override
	public IWorkingSet getAggregateWorkingSet()
	{
		return aggregateResourceSet;
	}

	@Override
	public void setWorkingSets(IWorkingSet[] workingSets)
	{
		if(StringUtils.isNotEmpty(aggregatedResourceSetName) && ArrayUtils.isNotEmpty(workingSets))
		{
			// die WorkingSets intern speichern
			aggregatedWorkingSets = workingSets;

			// das existierende ResourceSet entfernen
			IWorkingSet resourceSet = workingSetManager.getWorkingSet(aggregatedResourceSetName);
			if(resourceSet != null)
				workingSetManager.removeWorkingSet(resourceSet);
			
			// ein neues ResourceSet erzeugen 
			aggregateResourceSet =  (AggregateWorkingSet) workingSetManager
					.createAggregateWorkingSet(
							aggregatedResourceSetName,
							Messages.WorkbenchPage_workingSet_default_label,
							workingSets);

			// das aktualisierte Ws speichern
			workingSetManager.addWorkingSet(aggregateResourceSet);
			
			
		}
		
		refreshViewer();
		
	}
	
	/*
	 * Prueft, ob 'workingSet' ein Mitglied der aggregierten WorkingSets ist.  
	 */
	private boolean isWindowWorkingMember(IWorkingSet workingSet)
	{
		if ((workingSet != null) && (aggregatedWorkingSets != null))
		{
			List<String> checkWSids = new ArrayList<String>();
			for (IWorkingSet checkWorkingSet : aggregatedWorkingSets)
				checkWSids.add(checkWorkingSet.getId());
			
			return checkWSids.contains(workingSet.getId());
		}
		return false;
	}
	
	/*
	 * In den aggregierten WorkingSets ueber die Id suchen.
	 * Suche ueber Name versagt wenn dieser mittlerweile editiert wurde.
	 */
	private IWorkingSet getWindowWorkingSetByID(String id)
	{
		if(aggregatedWorkingSets != null)
		{
			for (IWorkingSet checkWorkingSet : aggregatedWorkingSets)
			{
				if(StringUtils.equals(id, checkWorkingSet.getId()))
					return checkWorkingSet;
			}
		}

		return null;
	}


	@Override
	public TreeViewer getViewer()
	{		
		return treeViewer;
	}

	@Override
	public Clipboard getClipboard()
	{
		return clipboard;
	}
	
   public ResourceFilterProvider getFilterProvider()
	{
		return filterProvider;
	}

	/**
     * Selects and reveals the specified elements.
     */
    public void selectReveal(ISelection selection) {
        StructuredSelection ssel = convertSelection(selection);
        if (!ssel.isEmpty()) {
            treeViewer.getControl().setRedraw(false);
            treeViewer.setSelection(ssel, true);
            treeViewer.getControl().setRedraw(true);
        }
    }
    
    public void setSelection(IAdaptable adaptable)
	{
		if (adaptable != null)
		{			
			if (getTopLevelStatus())
			{
				IWorkingSet[] workingSets = getWindowWorkingSets();
				for (IWorkingSet workingSet : workingSets)
				{
					IAdaptable[] elements = workingSet.getElements();
					for (IAdaptable element : elements)
					{
						if (element.equals(adaptable))
						{
							treeViewer.expandToLevel(workingSet, 1);
							break;
						}
					}
				}
			}
			
			Object obj = adaptable.getAdapter(Project.class);
			if(obj != null)
				treeViewer.expandToLevel(obj, 1);
			treeViewer.setSelection(new StructuredSelection(adaptable), true);	
		}
	
	}

    /**
     * Converts the given selection into a form usable by the viewer,
     * where the elements are resources.
     */
    private StructuredSelection convertSelection(ISelection selection) {
        ArrayList list = new ArrayList();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            for (Iterator i = ssel.iterator(); i.hasNext();) {
                Object o = i.next();
                IResource resource = null;
                if (o instanceof IResource) {
                    resource = (IResource) o;
                } else {
                    if (o instanceof IAdaptable) {
                        resource = (IResource) ((IAdaptable) o)
                                .getAdapter(IResource.class);
                    }
                }
                if (resource != null) {
                    list.add(resource);
                }
            }
        }
        return new StructuredSelection(list);
    }

	@Override
	public boolean getTopLevelStatus()
	{
		return topLevelFlag;
	}

	/**
	 * Das uebergebene IProjekt im ResourceNavigator aktualisieren.
	 * Wenn (iProjekt == null) wird das momentan selektierte Projekt aktualisiert 
	 * @param iProject
	 */
	@Inject
	@Optional
	public void handleNavigatorUpdateEvent(@UIEventTopic(IResourceNavigator.NAVIGATOR_EVENT_UPDATE_REQUEST) IProject iProject)
	{
		if(iProject != null)	
			treeViewer.update(iProject, null);
		else
		{
			// das momentan selektierte Projekt aktualisieren
			IStructuredSelection selection = treeViewer.getStructuredSelection();
			Object selObj = selection.getFirstElement();
			if (selObj instanceof IResource)
			{
				IResource iResource  = (IResource) selObj;
				treeViewer.update(iResource.getProject(), null);				
			}
		}
	}

	/**
	 * Projekt im ResourceNavigator selektieren
	 *  
	 * @param iProject
	 */
	@Inject
	@Optional
	public void handleNavigatorSelectEvent(@UIEventTopic(IResourceNavigator.NAVIGATOR_EVENT_SELECT_REQUEST) IResource iResource)
	{
		if (iResource instanceof IResource)
			treeViewer.setSelection(new StructuredSelection(iResource));
	}

}
