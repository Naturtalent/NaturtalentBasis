package it.naturtalent.e4.project.ui.wizards;


import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkingSet;

@Deprecated
public class ProjectWizard extends Wizard
{

	protected IResourceNavigator navigator = null;
	
	protected NtProject ntProject = null;
	
	protected ProjectWizardPage page;
	
	protected List<IProjectDataAdapter>lAdapters =  null;
	
	protected IEclipseContext context;
	//private EPartService ePartService;
	
	private IProjectDataFactory projectDataFactory;

	/*
	@Inject
	@Optional
	EPartService ePartService;
	*/
	
	public ProjectWizard()
	{		
		setWindowTitle("Project Wizard");			
	}
	
	/*
	public ProjectWizard(IResourceNavigator navigator)
	{
		super();
		this.navigator = navigator;
		
		if(navigator != null)
		{
			Object selObj = ((IStructuredSelection) navigator.getViewer().getSelection()).getFirstElement();
			if (selObj instanceof IProject)
			{
				ntProject = new NtProject((IProject) selObj);
				setWindowTitle(ntProject.getName());
			}
		}
	}
	*/
	
	@PostConstruct
	private void initWizard (IEclipseContext context, @Optional final
			IProjectDataFactory projectDataFactory, @Optional IEventBroker eventBroker)
	{
		this.context = context;
		this.projectDataFactory = projectDataFactory;
		
		page = new ProjectWizardPage(navigator);
		setNavigator(navigator);	
		page.setEventBroker(eventBroker);
	}

	public void setNavigator(IResourceNavigator navigator)
	{
		this.navigator = navigator;
		
		if(navigator != null)
		{
			page.setNavigator(navigator);
			
			Object selObj = ((IStructuredSelection) navigator.getViewer().getSelection()).getFirstElement();
			if (selObj instanceof IProject)
			{
				ntProject = new NtProject((IProject) selObj);
				
				if(ntProject.isOpen())				
					setWindowTitle(ntProject.getName());
				else
				{
					ProjectData projectData = projectDataFactory.getProjectData(ntProject);
					setWindowTitle(projectData.getName());
					page.setProjectDataFactory(projectDataFactory);
				}
			}
		}
	}

	@Override
	public void addPages()
	{		
		// ProjektWizardPage einfuegen
		addPage(page);
		
		// die vorgemerkten Adapter der ProjektWizardPage hinzufuegen
		if (lAdapters != null)
		{
			for (IProjectDataAdapter adapter : lAdapters)
			{				
				adapter.setProjectData(null);
				if (ntProject != null)
				{
					// Adapter mit den persistenten Projektdaten laden					
					IProjectData data = Activator.projectDataFactory
							.readProjectData(adapter, ntProject);
					adapter.setProject(ntProject);
					adapter.setProjectData(data);	
					
					// seit e4 Projektdaten im context speichern
					// Projektdaten ueber den Adapter lesen		
					/*
					if(data == null)
					{
						Object adapterData = adapter.getProjectData();
						context.set(adapter.getName(), adapterData);
					}
					*/
				}
				
				
				WizardPage wizardPage = adapter.getWizardPage();
				if(wizardPage != null)	
				{
					// die vom Adapter gelieferte WizardPage hinzufuegen 					
					ContextInjectionFactory.invoke(wizardPage, PostConstruct.class, context);
					addPage(wizardPage);
				}
				
				// den Adapter selbst hinzufuegen
				page.addAdapter(adapter);
			}
		}
	}
	
	
	
	/**
	 * die vom Wizard zuverwendeten Adapter vormerken 
	 * 
	 * @param adapters
	 */	
	public void setAdapters(IProjectDataAdapter [] adapters)
	{
		if (!ArrayUtils.isEmpty(adapters))
		{
			lAdapters = new ArrayList<IProjectDataAdapter>();
			for (IProjectDataAdapter adapter : adapters)
				lAdapters.add(adapter);
		}
	}

	@Override
	public boolean performFinish()
	{			
		if(!ntProject.isOpen())
			return true;		
		
		// die mit 'page' neuangelegten WorkingSets hinzufuegen 
		List<IWorkingSet>addedWorkingSets = page.getAddedWorkingSets();		
		if(addedWorkingSets != null)
		{
			IWorkingSet [] windowWorkingSets = navigator.getWindowWorkingSets();
			for(IWorkingSet addedWorkingSet : addedWorkingSets)
				windowWorkingSets = ArrayUtils.add(windowWorkingSets, addedWorkingSet);
			navigator.setWorkingSets(windowWorkingSets);			
		}

		// Die neue WorkingSetzuordnung dem ContentProvider mitteilen
		List<IWorkingSet>lNewAssignedWorkingSets = page.getAssignedWorkingSets();				
		WorkbenchContentProvider.newAssignedWorkingSets = lNewAssignedWorkingSets
				.toArray(new IWorkingSet[lNewAssignedWorkingSets.size()]);
		
		// Projektname geandert? - aktualisieren
		/*
		String name = page.getProjectAliasName();
		if(!ntProject.getName().equals(name))
			ntProject.setName(name);
			*/
					
		// Default Projektdaten im Adapter aktualisieren
		IProjectDataAdapter projectAdapter = ProjectDataAdapterRegistry
				.getProjectDataAdapter(ProjectData.PROP_PROJECTDATACLASS);
		if (projectAdapter != null)
		{
			ProjectData projectData = (ProjectData) projectAdapter.getProjectData();
			if(projectData == null)
			{
				projectData = new ProjectData();
				projectAdapter.setProjectData(projectData);
			}
			projectData.setName(page.getProjectAliasName());
		}
		
		// Projectdaten speichern - ContentProvider aktualisiert den Navigator
		//Activator.projectDataFactory.saveOrUpdateProjectData(getShell(), lAdapters, ntProject);		
		//WorkbenchContentProvider.newAssignedWorkingSets = null;
			
		// 'commit' in jedem Adapter 
		for(IProjectDataAdapter iAdapter : lAdapters)
			iAdapter.save();
		
		
		
		
		

		WorkbenchContentProvider.newAssignedWorkingSets = null;
		
		return true;
	}

}
