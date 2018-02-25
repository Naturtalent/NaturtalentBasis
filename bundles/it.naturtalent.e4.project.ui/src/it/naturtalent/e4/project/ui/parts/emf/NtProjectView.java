
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
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.DefaultReferenceService;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTView;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContextFactory;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.commands.EMFStoreBasicCommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.osgi.service.component.annotations.Deactivate;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.model.project.DynPropertyItem;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjectProperty;
import it.naturtalent.e4.project.model.project.NtProperty;
import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.model.project.impl.NtPropertyImpl;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.actions.emf.SaveAction;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;

public class NtProjectView
{	
	// im 'fragment.e4xmi' definierte ID's
	public static final String NTPROJECT_VIEW_ID = "iit.naturtalent.e4.project.ui.part.emf.NtProjectView";
	public static final String SAVE_TOOLBAR_ID = "it.naturtalent.e4.project.ui.directtoolitem.speichern";
	public static final String UNDO_TOOLBAR_ID = "it.naturtalent.e4.project.ui.directtoolitem.undo";
	
	public final static String UPDATE_PROJECTVIEW_REQUEST = "updateprojectviewrequest";
	
	private ScrolledComposite projectComposite;
	private ScrolledComposite propertyComposite;
	
	private EObject selectedNtProject;
	
	private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
		
		
	@PostConstruct
	public void postConstruct(Composite composite, Shell shell,
			final EPartService partService, final EModelService modelService,
			@Optional INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository)
	{
		this.ntProjektDataFactoryRepository = ntProjektDataFactoryRepository;
		
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
				} 				
			}
		});
		
		
	}
				
	
	/**
	 * Die Selektion einer Resource im ResourceNavigator wurde empfangen.
	 * Aenderungen den dem letzten selektierten NtProject werden festgeschrieben
	 * 
	 * @param selectedResource
	 */
	@Inject
	@Optional
	public void setNtSelection(
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IResource selectedResource, 
			EPartService partService, EModelService modelService)
	{
		if (selectedResource instanceof IProject)
		{
			commit((IProject)selectedResource, partService, modelService);
			
			EObject eObject = Activator.findNtProject(((IProject)selectedResource).getName());
			if(eObject != null)
			{
				selectedNtProject = eObject; 
				showDetails(selectedNtProject);
			}
		}		
	}
	
	@Inject
	@Optional
	public void handleModelChangedEvent(@UIEventTopic(UPDATE_PROJECTVIEW_REQUEST) EObject eObject)
	{
		System.out.println(selectedNtProject);
		showDetails(selectedNtProject);
	}
	
	/*
	 * Modellaenderungen festschreiben
	 * 
	 */
	private void commit(IProject iProject, EPartService partService, EModelService modelService)
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
					iProject = ResourcesPlugin.getWorkspace().getRoot()
							.getProject(ntProject.getId());
					if (iProject.exists())
					{
						try
						{
							// Name des 'alten' selektierten Projects in
							// IProject schreiben und im Navigator updaten
							iProject.setPersistentProperty(INtProject.projectNameQualifiedName,ntProject.getName());
							MPart mPart = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
							ResourceNavigator navigator = (ResourceNavigator) mPart.getObject();
							navigator.getTreeViewer().update(iProject, null);

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
	 * Das Object 'eObject' (NtProject) anzeigen
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
				ViewModelContext vmc = ViewModelContextFactory.INSTANCE.createViewModelContext(
						ViewProviderHelper.getView(eObject, null),
						eObject, new DefaultReferenceService());
				
				render = ECPSWTViewRenderer.INSTANCE.render(projectComposite,vmc);
				
				projectComposite.setExpandHorizontal(true);
				projectComposite.setExpandVertical(true);
				projectComposite.setContent(render.getSWTControl());
				//projectComposite.setMinSize(render.getSWTControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));	
				
				
				//Properties
				EClass propertyClass = ProjectPackage.eINSTANCE.getNtProperty();
				NtProperty property = (NtProperty) EcoreUtil.create(propertyClass);
				setPropertyData(property, eObject);
				
				render = ECPSWTViewRenderer.INSTANCE.render(propertyComposite,property);
				
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
	}
	
	/*
	 * Die Properties ermitteln und in das Modell 'NtProperty' eintragen.
	 */
	private void setPropertyData(NtProperty property, EObject eObject)
	{
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
			
			// DynPropertyItem - Attribut erzeugen
			EClass dynPropertyItemClass = ProjectPackage.eINSTANCE.getDynPropertyItem();
			DynPropertyItem dynPropertyItem = (DynPropertyItem) EcoreUtil.create(dynPropertyItemClass);
			
			// PropertyString 'to.String()' und Factoryklasse uebernehmen			
			dynPropertyItem.setName(projectProperty.toString());
			dynPropertyItem.setClassName(projectProperty.getClass().getName());
			
			// DynPropertyItem zu den NtProjectProperties hinzufuegen
			dynProperties.add(dynPropertyItem);
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
		System.out.println("NtProjectView: DEACTIVATE");
	}

	@PersistState
	public void save()
	{
		System.out.println("NtProjectView: SAVE");
	}


	public EObject getSelectedNtProject()
	{
		return selectedNtProject;
	}
	
	

}