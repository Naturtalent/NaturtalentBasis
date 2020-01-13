
package it.naturtalent.e4.project.ui.parts.emf;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTView;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.commands.EMFStoreBasicCommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.osgi.service.component.annotations.Deactivate;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.model.project.DynPropertyItem;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProperty;
import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.actions.emf.SaveAction;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSet;

public class NtProjectView
{	
	// im 'fragment.e4xmi' definierte ID's
	public static final String NTPROJECT_VIEW_ID = "iit.naturtalent.e4.project.ui.part.emf.NtProjectView";
	public static final String SAVE_TOOLBAR_ID = "it.naturtalent.e4.project.ui.directtoolitem.speichern";
	public static final String UNDO_TOOLBAR_ID = "it.naturtalent.e4.project.ui.directtoolitem.undo";
	public static final String SYNC_TOOLBAR_ID = "it.naturtalent.e4.project.ui.directtoolitem.sync";
	
	public final static String UPDATE_PROJECTVIEW_REQUEST = "updateprojectviewrequest";
	
	private final static String FEATURE_NTPRPJECTNAME = "name";
	
	private ScrolledComposite projectComposite;
	private ScrolledComposite propertyComposite;
	
	private EObject selectedNtProject;
	
	// aktuell editiertes NtProjekt
	private NtProject editedNtProject;
	
	private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	private EventBroker eventBroker;
		
		
	@PostConstruct
	public void postConstruct(Composite composite, Shell shell,
			final EPartService partService, final EModelService modelService,
			@Optional INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository,
			@Optional EventBroker eventBroker)
	{
		this.ntProjektDataFactoryRepository = ntProjektDataFactoryRepository;
		this.eventBroker = eventBroker;
		
		projectComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL);
		projectComposite.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		projectComposite.setBackgroundMode(SWT.INHERIT_FORCE);
	
		propertyComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL);
		propertyComposite.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		propertyComposite.setBackgroundMode(SWT.INHERIT_FORCE);
		
		
		// Aenderungen im NtProject ueberwachen
		EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(Activator.getECPProject());
		EMFStoreBasicCommandStack commandStack = (EMFStoreBasicCommandStack) domain.getCommandStack();		
		domain.getCommandStack().addCommandStackListener(new CommandStackListener()
		{			
			@Override
			public void commandStackChanged(EventObject event)
			{
				// Modellaenderungen enablen die Toolbaraktionen undo und save
				EMFStoreBasicCommandStack commandStack = (EMFStoreBasicCommandStack) event.getSource();
				Command command = commandStack.getMostRecentCommand();
				if(command instanceof SetCommand)				
				{
					MPart mPart = partService.findPart(NTPROJECT_VIEW_ID);
					List<MToolItem> items = modelService.findElements(mPart, SAVE_TOOLBAR_ID, MToolItem.class,null, EModelService.IN_PART);
					MToolItem item = items.get(0);
					item.setEnabled(true);
					
					items = modelService.findElements(mPart, UNDO_TOOLBAR_ID, MToolItem.class,null, EModelService.IN_PART);
					item = items.get(0);
					item.setEnabled(true);

					// aktuell editiertes NtProjekt zwischenspeichern
					EStructuralFeature eStructuralFeature = ((SetCommand) command).getFeature();					
					if(StringUtils.equals(eStructuralFeature.getName(), FEATURE_NTPRPJECTNAME))
					{
						List<Object> result = new ArrayList<Object>();
						result.addAll(command.getResult());
						Object eObject = result.get(0);
						if (eObject instanceof NtProject)					
							editedNtProject = (NtProject) eObject;
					}
				} 				
			}
		});
	}

	/**
	 * WorkingSet wurde selektiert - Projekt-Properties ausblenden
	 * @param selectedResource
	 */
	@Inject
	@Optional
	public void handleWorkingSetSelection(
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional WorkingSet workingSet)
	{	
		if (workingSet != null)
		{
			//EClass loginClass = ProjectPackage.eINSTANCE.getNtProject();
			//NtProject ntProject = (NtProject) EcoreUtil.create(loginClass);
			//showDetails(ntProject);
			showDetails(null);
		}
	}

	/**
	 * Die Selektion einer Resource im ResourceNavigator wurde empfangen.
	 * Aenderungen den dem letzten selektierten NtProject werden festgeschrieben
	 * 
	 * @param selectedResource
	 */
	@Inject
	@Optional
	public void handleResourceSelection(
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IResource resource, 
			EPartService partService, EModelService modelService)
	{	
		if (resource instanceof IResource)
		{
			IProject iProject = ((IResource) resource).getProject();
			
			// festschreiben der letzten Selektion
			commitLastSelection(iProject, partService, modelService);
			
			EObject eObject = Activator.findNtProject(iProject.getName());
			if (eObject != null)
			{
				selectedNtProject = eObject;
				showDetails(selectedNtProject);
			}
		}
	}

	/**
	 * Die Detailseite NtProjektView soll aktualisiert werden.
	 * Parameter eObject == null zeigt ein 'leeres' Projekt an.
	 * Parameter eObject != null zeigt das momentan selektierte Projekt an (eObject ist irrelevant)
	 * @param eObject
	 */
	@Inject
	@Optional
	public void handleModelChangedEvent(@UIEventTopic(UPDATE_PROJECTVIEW_REQUEST) EObject eObject)
	{	
		if(eObject == null)
		{
			// ein 'leeres' NtProject anzeigen
			EClass projectClass = ProjectPackage.eINSTANCE.getNtProject();
			NtProject ntProject = (NtProject) EcoreUtil.create(projectClass);
			showDetails(ntProject);
		}
		
		else showDetails(selectedNtProject);
	}
	
	/*
	 * Aenderungen am Projekt festschreiben.
	 * 
	 * Im Prinzip wird bei jeder Selektionsaenderung (auch die Deselektion ist eine Aenderung) geprueft, ob eine 
	 * Aenderung am Modell eingetreten ist und wenn ja, wird diese festgeschrieben. 
	 * 
	 */
	private void commitLastSelection(IProject iProject, EPartService partService, EModelService modelService)
	{
		EObject eObject = Activator.findNtProject(iProject.getName());
		if(eObject != null)
		{
			if (selectedNtProject != null)
			{
				// gab es ueberhaupt eine Modellaenderung
				if (Activator.getECPProject().hasDirtyContents())
				{
					NtProject ntProject = (NtProject) selectedNtProject;
					iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getId());
					if (iProject.exists())
					{
						try
						{
							// Name des 'alten' selektierten Projects in
							// IProject schreiben und im Navigator updaten
							iProject.setPersistentProperty(INtProject.projectNameQualifiedName,ntProject.getName());
							MPart mPart = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
							ResourceNavigator navigator = (ResourceNavigator) mPart.getObject();
							//navigator.getTreeViewer().update(iProject, null);
							eventBroker.post(IResourceNavigator.NAVIGATOR_EVENT_UPDATE_REQUEST, iProject);

							// Daten des 'alte' selektierte Projects im Modell speichern
							mPart = partService.findPart(NTPROJECT_VIEW_ID);
							List<MToolBarElement> items = modelService.findElements(mPart, NtProjectView.SAVE_TOOLBAR_ID, MToolBarElement.class,null, EModelService.IN_PART);
							MToolBarElement toolItem = items.get(0);
							if (toolItem instanceof MContribution)
							{
								MContribution directTool = (MContribution) toolItem;
								Object obj = directTool.getObject();
								if (obj instanceof SaveAction)
								{
									SaveAction saveAction = (SaveAction) obj;
									saveAction.execute(partService,modelService, mPart);									
								}
							}

						} catch (CoreException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	

	/**
	 * Das Object 'eObject' (NtProject) anzeigen.
	 * 
	 * Seite ist unsichtbar, wenn Parameter 'null'
	 * 
	 * @param eObject
	 */
	public void showDetails(EObject eObject)
	{
		ECPSWTView render = null;
		if(eObject != null)
		{			
			try
			{		
				// NtProjekt (Name und Beschreibung)
				/*
				ViewModelContext vmc = ViewModelContextFactory.INSTANCE.createViewModelContext(
						ViewProviderHelper.getView(eObject, null),
						eObject, new DefaultReferenceService());
				
				render = ECPSWTViewRenderer.INSTANCE.render(projectComposite,vmc);
				*/
				
				render = ECPSWTViewRenderer.INSTANCE.render(projectComposite, eObject);
				
				projectComposite.setVisible(true);				
				projectComposite.setExpandHorizontal(true);
				projectComposite.setExpandVertical(true);
				projectComposite.setContent(render.getSWTControl());
				//projectComposite.setMinSize(render.getSWTControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));	
				
				// eine neues Modell 'NtProperty' anlegen
				EClass propertyClass = ProjectPackage.eINSTANCE.getNtProperty();
				NtProperty property = (NtProperty) EcoreUtil.create(propertyClass);
				
				//Properties
				if(eObject instanceof NtProject)
				{
					String projectID = ((NtProject)eObject).getId();
					if(StringUtils.isNotEmpty(projectID))
						setPropertyData((NtProject) eObject, property);
				}
				
				// NtProperties anzeigen
				render = ECPSWTViewRenderer.INSTANCE.render(propertyComposite,property);
				
				propertyComposite.setVisible(true);				
				propertyComposite.setExpandHorizontal(true);
				propertyComposite.setExpandVertical(true);
				propertyComposite.setContent(render.getSWTControl());
				
			}

			catch (ECPRendererException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			// Seite (alle Composites) nicht sichtbar
			projectComposite.setVisible(false);
			propertyComposite.setVisible(false);
		}
	}
	
	/*
	 * Die dynamischen Properties (werden ueber den jeweiligen Adapter geladen) 
	 * des NtProjects erzeugen. 
	 */
	private void setPropertyData(NtProject ntProject, NtProperty property)
	{		
		// ProjectID
		property.setId(ntProject.getId());
		
		// WorkingSet
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getId());
		String wsLabel = getWorkingSetLabel(iProject);
		if(StringUtils.isNotEmpty(wsLabel))
			property.setWorkingset(wsLabel);
			
		// Erstellungsdatum
		String stgDate = iProject.getName().substring(0,iProject.getName().indexOf('-'));
		Date date = new Date(NumberUtils.createLong(stgDate));
		String labelText = DateFormatUtils.format(date, "dd.MM.yyyy");
		property.setCreated(labelText);
				
		// dynamische Properties
		EList<DynPropertyItem>dynProperties = property.getProperties();
		
		// verfuegbare Factories aus dem Repository laden
		List<INtProjectPropertyFactory> projectPropertyFactories = ntProjektDataFactoryRepository
				.getAllProjektDataFactories();
		
		// Adapter erzeugen und abchecken
		for(INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
		{
			// koennen ueber den Adapter projectgekoppelte PropertyDaten geladen werden
			INtProjectProperty propertyAdapter = propertyFactory.createNtProjektData();
			propertyAdapter.setNtProjectID(ntProject.getId());
			if(propertyAdapter.getNtPropertyData() == null)
				continue;
			
			// DynPropertyItem - Attribut erzeugen
			EClass dynPropertyItemClass = ProjectPackage.eINSTANCE.getDynPropertyItem();
			DynPropertyItem dynPropertyItem = (DynPropertyItem) EcoreUtil.create(dynPropertyItemClass);
			
			// PropertyString 'to.String()' und Factoryklasse uebernehmen			
			dynPropertyItem.setName(propertyAdapter.toString());
			dynPropertyItem.setClassName(propertyAdapter.getClass().getName());
			
			// DynPropertyItem zu den NtProjectProperties hinzufuegen
			dynProperties.add(dynPropertyItem);
		}		
	}

	
	/*
	 * Die Properties ermitteln und in das Modell 'NtProperty' eintragen.
	 * Properties die aktuell nicht mehr gekoppelt sind werden in der Liste 
	 * 'unlinkedProperties' gesammelt und dann aus der PropertyDatei entfernt. 
	 */
	private List<INtProjectProperty>unlinkedProperties = new ArrayList<INtProjectProperty>();
	private void setPropertyDataOLD(NtProperty property, EObject eObject)
	{
		unlinkedProperties.clear();
		NtProject ntProject = (NtProject) eObject;
		
		// ProjekcID
		property.setId(ntProject.getId());
		
		// WorkingSet
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getId());
		String wsLabel = getWorkingSetLabel(iProject);
		if(StringUtils.isNotEmpty(wsLabel))
			property.setWorkingset(wsLabel);
			
		// Erstellungsdatum
		String stgDate = iProject.getName().substring(0,iProject.getName().indexOf('-'));
		Date date = new Date(NumberUtils.createLong(stgDate));
		String labelText = DateFormatUtils.format(date, "dd.MM.yyyy");
		property.setCreated(labelText);
		
		// dynamische Properties
		List<INtProjectProperty>projectProperties  = NtProjektPropertyUtils.getProjectProperties(
				ntProjektDataFactoryRepository, iProject);
				
		// alle ProjectProperty-Adapter bearbeiten
		EList<DynPropertyItem>dynProperties = property.getProperties();
		for(final INtProjectProperty projectProperty : projectProperties)
		{
			// ProjectPropertyAdapter initialisieren
			projectProperty.setNtProjectID(property.getId());
			
			if(projectProperty.getNtPropertyData() == null)
			{
				// diese Eigenschaft ist offensichtlich nicht mehr gekoppelt
				unlinkedProperties.add(projectProperty);
				continue;				
			}
			
			// DynPropertyItem - Attribut erzeugen
			EClass dynPropertyItemClass = ProjectPackage.eINSTANCE.getDynPropertyItem();
			DynPropertyItem dynPropertyItem = (DynPropertyItem) EcoreUtil.create(dynPropertyItemClass);
			
			// PropertyString 'to.String()' und Factoryklasse uebernehmen			
			dynPropertyItem.setName(projectProperty.toString());
			dynPropertyItem.setClassName(projectProperty.getClass().getName());
			
			// DynPropertyItem zu den NtProjectProperties hinzufuegen
			dynProperties.add(dynPropertyItem);
		}
		
		// ProjectPropertyDatei bereinigen (nichtgekoppelte Eigenschaften entfernen)
		if(!unlinkedProperties.isEmpty())
		{
			List<String> factoryNames = NtProjektPropertyUtils
					.getProjectPropertyFactoryNames(
							ntProjektDataFactoryRepository, iProject);
			for(INtProjectProperty projectProperty : unlinkedProperties)
			{
				String factoryName = projectProperty.getPropertyFactoryName();
				factoryNames.remove(factoryName);
			}
			
			String [] factoryNameArray = factoryNames.toArray(new String[factoryNames.size()]);
			NtProjektPropertyUtils.saveProjectPropertyFactories(iProject.getName(), factoryNameArray);
		}
	}
	
	/**
	 * Die dem Projekt zugeordneten WorkingSets in einem Label zusammenfassen.
	 * 
	 * @param selectedAdaptable - das selektierte Projekt
	 * @return
	 */
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	private String getWorkingSetLabel(IAdaptable selectedAdaptable)
	{
		StringBuilder wsLabel = null;
		assignedWorkingSets.clear();	
		
		IResourceNavigator navigator = Activator.findNavigator();
		IWorkingSet[] workingSets = navigator.getWindowWorkingSets();
		if (ArrayUtils.isNotEmpty(workingSets))
		{
			for (IWorkingSet workingSet : workingSets)
			{
				IAdaptable[] adaptables = workingSet.getElements();
				if (ArrayUtils.contains(adaptables, selectedAdaptable))
				{
					String wsName = workingSet.getName();
					if (!StringUtils.equals(wsName,
							IWorkingSetManager.OTHER_WORKINGSET_NAME))
					{
						assignedWorkingSets.add(workingSet);
						if (wsLabel == null)
							wsLabel = new StringBuilder(wsName);
						else
							wsLabel.append("," + wsName);
					}
				}
			}
		}
			
		return (wsLabel != null) ? wsLabel.toString() : null;
	}



	@Deactivate
	public void deActivate()
	{
		//System.out.println("NtProjectView: DEACTIVATE");
	}

	@PersistState
	public void save()
	{
		//System.out.println("NtProjectView: SAVE");
	}


	public EObject getSelectedNtProject()
	{
		return selectedNtProject;
	}

	// das editierte NtProjekt zurueckliefern @see SaveAction
	public NtProject getEditedNtProject()
	{
		return editedNtProject;
	}
	
	
	
	

}