package it.naturtalent.e4.project.ui.parts;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.contexts.IContext;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.swt.widgets.Label;


@Deprecated
public class ProjectView
{

	public static final String ID = "it.naturtalent.e4.project.ui.part.ProjectView";
	
	private ProjectComposite projectComposite;
	
	private List selectedObjects;
	private NtProject ntProject;
	//private ProjectData projectData;
	
	private Composite parent;	

	// alle dynamisch installierten Widgests
	private List<Widget>dynamicWidgets = new ArrayList<Widget>();

	
	
	
	// alle definierten Adaptercomposites auflisten
	private List<Composite>adapterComposites = new ArrayList<Composite>();
	
	private boolean inuse = false;

	@Optional @Inject INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	@Inject @Optional private IEventBroker eventBroker;
	@Inject @Optional private IProjectDataFactory projectDataFactory;
	@Inject @Optional private IEclipseContext context;
	
	@Inject @Optional private EPartService partService;
	
	
	@Inject
	@Optional
	private ESelectionService selectionService;
	
	
	
	// Projektdaten des momentan selektierten Projekts (Adaptername, ProjectData)
	Map<String, Object>projectDataRegister = new HashMap<String, Object>();
	
	// modifizierte Projektdaten (Adaptername, ProjectData)
	Map<String, Object>modifiedProjectDataRegister = new HashMap<String, Object>();
		
	// der benutzte Zwischenspeicher
	private Clipboard clipboard;

	// Action IDs und Registry
	public enum ViewActionID
	{
		OPEN_FILEEXPLORER,
		COPY_PROJECT_PATH,
		SAVE_ACTION,
		ADD_PROJECT
	}
	private Map<ViewActionID, Action>actionRegistry = new HashMap<ViewActionID, Action>();

	// Aenderungen von Projektdaten ueberwachen
	private EventHandler modifyProjectPropertyHandler = new EventHandler()
	{
		@Override
		public void handleEvent(Event event)
		{
			if (StringUtils.equals(event.getTopic(),INtProjectProperty.PROJECT_PROPERTY_EVENT_SET_PROPERTY))
			{
				String [] dataArray = (String []) event.getProperty(IEventBroker.DATA);				
				if((ntProject != null) && (StringUtils.equals(ntProject.getId(), dataArray[0])))
				{
					System.out.println("SET eventBroker ProjectView");
					initWidgets();
				}
				return;
			}
			
			if (StringUtils.equals(event.getTopic(),INtProjectProperty.PROJECT_PROPERTY_EVENT_UNSET_PROPERTY))
			{
				String [] dataArray = (String []) event.getProperty(IEventBroker.DATA);		
				if((ntProject != null) && (StringUtils.equals(ntProject.getId(), dataArray[0])))
				{
					System.out.println("UNSET eventBroker ProjectView");
					initWidgets();
				}
				return;
			}
		}
	};	

	// Aenderungen von Projektdaten ueberwachen
	private EventHandler modifyProjectHandler = new EventHandler()
	{
		@Override
		public void handleEvent(Event event)
		{
			if (StringUtils.equals(event.getTopic(),
					IProjectData.PROJECT_EVENT_MODIFY_PROJECTDATA))
			{
				// Savestatus aktualisieren
				Object modifiedProjectData = event.getProperty(IEventBroker.DATA);	
				
				// Hyperlinktexte anpassen
				updateWidgets(modifiedProjectData);
				
				// Savestatus anpassen 
				updateSaveState(modifiedProjectData);
				return;
			}
			
			// ein externes Model hat ProjektDaten gespeichert
			if (StringUtils.equals(event.getTopic(),
					IProjectData.PROJECT_EVENT_SAVE_EXTERMODEL))
			{
				// die Widgets erneut initialisieren
				initWidgets();
				updateStates();
			}
		}
	};	
	
	// Selektionen im ResourceNavigator ueberwachen
	private EventHandler navigatorSelectionHandler = new EventHandler()
	{
		@Override
		public void handleEvent(final Event event)
		{
			if(StringUtils.equals(event.getTopic(), ResourceNavigator.NAVIGATOR_EVENT_SELECTED))
			{
				// eine neue Selektion im ResourceNavigator bearbeiten
				ntProject = null;				
				selectedObjects = (List) event.getProperty(IEventBroker.DATA);
				if (!selectedObjects.isEmpty())
				{
					// sollen bisher getaetigte Aenderungen gespeichert werden
					if(actionRegistry.get(ViewActionID.SAVE_ACTION).isEnabled())
					{						
						if (MessageDialog.openQuestion(Display.getDefault()
								.getActiveShell()," ","Änderung der Projektdaten speichern ?"))
						{
							// Speicheraktion ausfuehren
							actionRegistry.get(ViewActionID.SAVE_ACTION).run();							
						}						
					}
					
					//MPart thisPart = partService.findPart(ID);
					partService.activate(partService.findPart(ID));
									
					// das im ResourceNavigator selektierte Projekt
					Object selectedObject = ((List) event
							.getProperty(IEventBroker.DATA)).get(0);
					if (selectedObject instanceof IResource)
						ntProject = new NtProject(((IResource) selectedObject).getProject());
					
					// die Daten des selektierten Projekts in den Widgets darstellen
					initWidgets();	
				}
								
				updateStates();
			}			
		}
	};
	
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	//private Composite composite_1;
	private ScrolledForm scrldfrmAdapterComposites;
	//private Section sctnNewSection;
	//private Section sctnNewSection_1;
	//private Composite composite_2;
	private ImageHyperlink mghprlnkTesthyper;
	private Label lblNewLabel;
	
	public ProjectView()
	{		
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent)
	{
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		
		Section sctnProject = formToolkit.createSection(parent, Section.TITLE_BAR);
		sctnProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		formToolkit.paintBordersFor(sctnProject);
		
		projectComposite = new ProjectComposite(parent, SWT.NONE);
		GridData gd_projectComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_projectComposite.heightHint = 198;
		projectComposite.setLayoutData(gd_projectComposite);
		projectComposite.setEventBroker(eventBroker);
		
		
		scrldfrmAdapterComposites = formToolkit.createScrolledForm(parent);
		GridData gd_scrldfrmNewScrolledform = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_scrldfrmNewScrolledform.heightHint = 69;
		scrldfrmAdapterComposites.setLayoutData(gd_scrldfrmNewScrolledform);
		formToolkit.paintBordersFor(scrldfrmAdapterComposites);
		scrldfrmAdapterComposites.setText("Eigenschaften");
		scrldfrmAdapterComposites.getBody().setLayout(new GridLayout(1, false));
						
		createSectionToolbar(sctnProject);
				
		eventBroker.subscribe(INtProjectProperty.PROJECT_PROPERTY_EVENT+"*",modifyProjectPropertyHandler);
		eventBroker.subscribe(IProjectData.PROJECT_EVENT+"*",modifyProjectHandler);
		eventBroker.subscribe(ResourceNavigator.NAVIGATOR_EVENT+"*",navigatorSelectionHandler);
		
		// Clipboard aktivieren
		clipboard = new Clipboard(Display.getDefault());

		// ggf. mit dem im Navigator selektierten Projekt initialisieren 
		Object selObj = selectionService.getSelection(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if (selObj instanceof IProject)
		{
			ntProject = new NtProject((IProject) selObj);			
			initWidgets();				
		}
		
	}
	
	// die Widgets auf das selektierte Projekt einstellen
	private void initWidgets()
	{
		
		// fruehere dyn. eingefuegte Widgests ausschalten		
		for(Widget widget : dynamicWidgets)
			widget.dispose();
		dynamicWidgets.clear();
		
		
		List<INtProjectProperty>projectProperties = NtProjektPropertyUtils.getProjectProperties(
				ntProjektDataFactoryRepository, ntProject.getIProject());
		if (projectProperties != null)
		{
			for (INtProjectProperty projectProperty : projectProperties)
			{
				if (StringUtils.equals(projectProperty.getClass().getName(),
						NtProjectProperty.class.getName()))
				{

				}
				else
				{
					if (projectProperty.init() != null)
					{
						/*
						final Action action = projectProperty.createAction();
						if (action != null)
						{
							String hyperlinkText = projectProperty.getLabel()
									+ ":" + projectProperty.toString();
							mghprlnkTesthyper = formToolkit
									.createImageHyperlink(
											scrldfrmAdapterComposites.getBody(),
											SWT.TOP);

							mghprlnkTesthyper.setLayoutData(new GridData(
									SWT.FILL, SWT.CENTER, false, false, 1, 1));
							formToolkit.paintBordersFor(mghprlnkTesthyper);
							mghprlnkTesthyper.setText(hyperlinkText);

							mghprlnkTesthyper
									.addHyperlinkListener(new HyperlinkAdapter()
									{
										@Override
										public void linkActivated(
												HyperlinkEvent e)
										{
											action.run();
										}

									});

							// mghprlnkTesthyper.setData(adapter);
							dynamicWidgets.add(mghprlnkTesthyper);
						}
						*/
					}
				}
			}
		}		
		
		

		// die Daten des selektierten Projekts lesen
		initProjectData(ntProject);

		for(Iterator<String> iterator = projectDataRegister.keySet().iterator(); iterator.hasNext();)
		{
			String id = (String)iterator.next();
			IProjectDataAdapter	adapter = ProjectDataAdapterRegistry.getProjectDataAdapter(id);
			if(adapter != null)
			{						
				Object projektData = adapter.load(ntProject.getId());
				if (projektData != null)
				{					
					if(projektData instanceof ProjectData)
					{
						// Label Erstellungsdatum bei DefaultProjectData
						ProjectData projectData = (ProjectData) projektData; 
						projectComposite.setNtProject((IProjectData) projectData.clone());
						
						IProject iProject = ntProject.getIProject();
						String stgDate = iProject.getName().substring(0,
								iProject.getName().indexOf('-'));
						Date date = new Date(NumberUtils.createLong(stgDate));
						String labelText = "erstellt am: "+DateFormatUtils.format(date, "dd.MM.yyyy");
						
						lblNewLabel = formToolkit.createLabel(scrldfrmAdapterComposites.getBody(), labelText, SWT.NONE);
						lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
						dynamicWidgets.add(lblNewLabel);
						
						continue;
					}
					
					adapter.setProjectData(projektData);
					final Action action = adapter.getAction(context);
					if(action != null)
					{
						adapter.setProject(ntProject);
						String[] text = adapter.toText(projektData);
						if (ArrayUtils.isNotEmpty(text))
						{
							StringBuilder hyperlinktext = new StringBuilder(text[0]);
							hyperlinktext.append(":");

							for (int n = 1; n < text.length; n++)
								hyperlinktext.append(" " + text[n]);

							mghprlnkTesthyper = formToolkit
									.createImageHyperlink(
											scrldfrmAdapterComposites.getBody(),
											SWT.TOP);
							mghprlnkTesthyper.setLayoutData(new GridData(
									SWT.FILL, SWT.CENTER, false, false, 1, 1));
							formToolkit.paintBordersFor(mghprlnkTesthyper);
							mghprlnkTesthyper.setText(hyperlinktext.toString());
							mghprlnkTesthyper.addHyperlinkListener(new HyperlinkAdapter()
							{
								@Override
								public void linkActivated(HyperlinkEvent e)
								{
									action.run();
								}
								
							});
							mghprlnkTesthyper.setData(adapter);
							dynamicWidgets.add(mghprlnkTesthyper);

						}
					}
				} 
				
				else projectComposite.setNtProject(null);
			}
		}

		/*	
		lblNewLabel = formToolkit.createLabel(scrldfrmAdapterComposites.getBody(), "erstellt", SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		dynamicWidgets.add(lblNewLabel);
		*/
		
		// TestHyperlink
		/*
		mghprlnkTesthyper = formToolkit.createImageHyperlink(scrldfrmAdapterComposites.getBody(), SWT.TOP);
		mghprlnkTesthyper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		formToolkit.paintBordersFor(mghprlnkTesthyper);
		mghprlnkTesthyper.setText("TestHyper");
		dynamicWidgets.add(mghprlnkTesthyper);
		*/

		
		/*
		
		// frueher dyn. eingefuegte Widgests ausschalten
		for(Composite composite : adapterComposites)
		{
			if(composite != null)
				composite.dispose();
		}
		adapterComposites = new ArrayList<Composite>();
				
		// die Daten des selektierten Projekts lesen
		initProjectData(ntProject);
		
		// Default Projektcomposite initialisieren (Name des Projekts u. Notizen eintragen)
		ProjectData projectData = (ProjectData) projectDataRegister.get(ProjectData.PROP_PROJECTDATACLASS);
		if(projectData != null)
		{
			projectData.clone();
			projectComposite.setNtProject((IProjectData) projectData.clone());
		}
		else projectComposite.setNtProject(null);
		
		*/

		/*
		
		// adapterdefinierte Composites dynamisch hinzufuegen
		for(Iterator<String> iterator = projectDataRegister.keySet().iterator(); iterator.hasNext();)
		{
			String id = (String)iterator.next();
			IProjectDataAdapter	adapter = ProjectDataAdapterRegistry.getProjectDataAdapter(id);
			if(adapter != null)
			{													
				adapter.setProject(ntProject);							
				Composite adapterComposite = adapter.createComposite(scrldfrmAdapterComposites.getBody());
				if(adapterComposite != null)
					adapterComposites.add(adapterComposite);
			}
			
			// Set MinimumSize nach dyn. Einfuegen von Widgets
			scrldfrmAdapterComposites.layout(true, true);
			scrldfrmAdapterComposites.setMinSize(scrldfrmAdapterComposites.computeSize(SWT.DEFAULT, SWT.DEFAULT));								
		}
		
		*/
		
		scrldfrmAdapterComposites.layout(true, true);
		scrldfrmAdapterComposites.setMinSize(scrldfrmAdapterComposites.computeSize(SWT.DEFAULT, SWT.DEFAULT));								
	}

	private void updateWidgets(Object projectData)
	{
		for (Widget widget : dynamicWidgets)
		{
			if (widget instanceof ImageHyperlink)
			{
				ImageHyperlink hyperlink = (ImageHyperlink) widget;
				IProjectDataAdapter adapter = (IProjectDataAdapter) widget.getData();
				
				if((adapter != null) && (adapter.getProjectDataClass().equals(projectData.getClass())))
				{
					String[] text = adapter.toText(projectData);					
					StringBuilder hyperlinktext = new StringBuilder(text[0]);
					hyperlinktext.append(":");

					for (int n = 1; n < text.length; n++)
						hyperlinktext.append(" " + text[n]);

					hyperlink.setText(hyperlinktext.toString());
					return;
				}				
			}
					
		}
	}
	
	private void createSectionToolbar(Section section)
	{
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);		
		section.setTextClient(toolbar);

		Action action;
		
		// Save Action
		action = new Action()
		{
			@Override
			public void run()
			{				
				// Modifizierte Daten speichern
				for (Iterator<String> modifiedDataIterator = modifiedProjectDataRegister
						.keySet().iterator(); modifiedDataIterator.hasNext();)
				{
					// zu jedem Datensatz den zustaendigen Adapter suchen und ueber diesen speichern
					String adapterId = modifiedDataIterator.next();
					IProjectDataAdapter adapter =  ProjectDataAdapterRegistry
						.getProjectDataAdapter(adapterId);
					Object modProjectData = modifiedProjectDataRegister.get(adapterId);
					
					// Daten im Adapter aktualisieren
					adapter.setProject(ntProject);
					adapter.setProjectData(modProjectData);
					adapter.save();
					//adapter.saveProjectData(modProjectData);	
				}
				
				// Savestatus zuruecksetzen
				actionRegistry.get(ViewActionID.SAVE_ACTION).setEnabled(false);
				
				// register mit modifizierten Daten loeschen
				modifiedProjectDataRegister.clear();
			}
		};
		action.setImageDescriptor(Icon.COMMAND_SAVE.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		action.setToolTipText("Änderungen speichern");
		action.setEnabled(false);
		actionRegistry.put(ViewActionID.SAVE_ACTION, action);
		toolBarManager.add(action);
		
		// Open Fileexplorer
		action = new Action()
		{
			@Override
			public void run()
			{				
				if((selectedObjects != null) && (!selectedObjects.isEmpty()))
				{
					Object selObject = selectedObjects.get(0);
					if(selObject instanceof IResource)
					{
						try
						{
							IResource iResource = (IResource) selObject;
							String destPath = iResource.getLocation().toOSString();
							
							//os = System.getProperty("os.name");
							if (SystemUtils.IS_OS_LINUX)
								Runtime.getRuntime().exec("nautilus " + destPath);
							else
								Runtime.getRuntime().exec("explorer " + destPath);

						} catch (Exception exp)
						{
							exp.printStackTrace();
						}

					}
				}
			}
		};
		action.setImageDescriptor(Icon.ICON_FOLDER.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		action.setToolTipText("Fileexplorer öffen");
		action.setEnabled(false);
		actionRegistry.put(ViewActionID.OPEN_FILEEXPLORER, action);
		toolBarManager.add(action);

		
		// Copy Clipboard Action
		action = new Action()
		{
			@Override
			public void run()
			{
				
				if((selectedObjects != null) && (!selectedObjects.isEmpty()))
				{
					Object selObject = selectedObjects.get(0);
					if(selObject instanceof IResource)
					{
						IResource iResource = (IResource) selObject;
						clipboard.setContents(new Object[]
								{ iResource.getLocation()
										.toOSString() }, new Transfer[]
								{ TextTransfer.getInstance() });
					}
				}
			}
		};
		action.setImageDescriptor(Icon.MENU_COPY.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		action.setToolTipText("Projektpath in Zwischenablage");
		action.setEnabled(false);
		actionRegistry.put(ViewActionID.COPY_PROJECT_PATH, action);
		toolBarManager.add(action);
		
		
		
		toolBarManager.update(true);
	}
	
	private void updateStates()
	{
		
		actionRegistry.get(ViewActionID.COPY_PROJECT_PATH).setEnabled(false);
		actionRegistry.get(ViewActionID.OPEN_FILEEXPLORER).setEnabled(false);
		if(!selectedObjects.isEmpty())
		{
			Object selObject = selectedObjects.get(0);

			// Copy ProjektPath Clipboard Action
			actionRegistry.get(ViewActionID.COPY_PROJECT_PATH).setEnabled(
					selObject instanceof IResource);
						
			actionRegistry.get(ViewActionID.OPEN_FILEEXPLORER)
					.setEnabled(selObject instanceof IResource);
		}

	}

				
	// alle Daten eines Projekts zusammenfassen
	private void initProjectData(NtProject ntProject)
	{
		projectDataRegister.clear();
		modifiedProjectDataRegister.clear();
		
		if (ntProject != null)
		{
			List<IProjectDataAdapter> adapters = ProjectDataAdapterRegistry
					.getProjectDataAdapters();
			for (IProjectDataAdapter adapter : adapters)
			{
				Object obj = adapter.load(ntProject.getId());				
				if(obj != null)
					projectDataRegister.put(adapter.getId(), obj);
			}
		}
	}

	// Savestatus aktualisieren
	private void updateSaveState(Object modifiedData)
	{
		for(Iterator<Object> iterator = projectDataRegister.values().iterator(); iterator.hasNext();)
		{
			Object projectData = iterator.next();			
			if (StringUtils.equals(modifiedData.getClass().getName(), projectData.getClass().getName()))
			{		
				boolean modified = !modifiedData.equals(projectData);
				
				// modifizierten Datensatz mit Originaldatensatz vergleichen
				actionRegistry.get(ViewActionID.SAVE_ACTION).setEnabled(modified);
								
				// ueber die Datenklasse den zugehorigen Adapter ermitteln
				if(modified)
				{
					for (Iterator<String> adapterNamesIterator = projectDataRegister
							.keySet().iterator(); adapterNamesIterator
							.hasNext();)
					{
						String adapterID = adapterNamesIterator.next();
						Object check = projectDataRegister.get(adapterID);
						if (StringUtils.equals(modifiedData.getClass()
								.getName(), check.getClass().getName()))
						{
							modifiedProjectDataRegister.put(adapterID,
									modifiedData);
							break;
						}
					}
				}
				
				return;
			}
		}
	}
	
	@PreDestroy
	public void dispose()
	{
		eventBroker.unsubscribe(modifyProjectPropertyHandler);
		eventBroker.unsubscribe(modifyProjectHandler);
		eventBroker.unsubscribe(navigatorSelectionHandler);
		
		clipboard.dispose();
		clipboard = null;
	}

	@Focus
	public void setFocus()
	{
		// TODO	Set the focus to control
	}
	
}
