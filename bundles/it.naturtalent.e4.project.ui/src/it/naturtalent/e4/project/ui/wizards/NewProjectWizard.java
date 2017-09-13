package it.naturtalent.e4.project.ui.wizards;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

public class NewProjectWizard extends ProjectWizard
{
	
	public NewProjectWizard()
	{
		super();			
	}

	/*
	public NewProjectWizard(IResourceNavigator navigator)
	{
		super(navigator);		
	}
	*/
	
	@PostConstruct
	private void initWizard (EPartService ePartService, @Optional IEventBroker eventBroker)
	{
		
		page = new NewProjectWizardPage(navigator);
		page.setEventBroker(eventBroker);
		
		ntProject = null;
		setWindowTitle("");	
		
		/*
		MPart mPart = ePartService
				.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);		
		navigator = (IResourceNavigator) mPart.getObject();
		
		page = new NewProjectWizardPage(navigator);
		
		if(navigator != null)
		{
			Object selObj = ((IStructuredSelection) navigator.getViewer().getSelection()).getFirstElement();
			if (selObj instanceof IProject)
			{
				ntProject = new NtProject((IProject) selObj);
				setWindowTitle(ntProject.getName());				
			}
		}
		*/
		
	}


	@Override
	public boolean performFinish()
	{		
		// die im Wizard dem neuen Projekt zugeordneten WorkingSets in 'processDelta()' beruecksichtigen
		List<IWorkingSet>lNewAssignedWorkingSets = page.getAssignedWorkingSets();
		if ((lNewAssignedWorkingSets != null)
				&& (!lNewAssignedWorkingSets.isEmpty()))
			WorkbenchContentProvider.newAssignedWorkingSets = lNewAssignedWorkingSets
					.toArray(new IWorkingSet[lNewAssignedWorkingSets.size()]);
		
		// CreateNewProject provoziert ueber den Listener 'processDelta()'-Aufruf in WorkbenchContentProvider
		CreateNewProject.createProject(getShell(),new String [] {page.getProjectAliasName()});
		WorkbenchContentProvider.newAssignedWorkingSets = null;
		
		// (test) sicherstellen dass 'Other' - Workingset aktuell bleibt
		Activator.getWorkingSetManager().updateOthers();
			
		// zum Speichern der Projektdaten auf das neue Projekt zugreifen
		String id = (String) Activator.newlyCreatedProjectMap.keySet().toArray()[0];
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(id);

		// Default Projektdaten speichern
		ntProject = new NtProject(newProject);
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

		// die konfigurierten Projectdaten speichern (ueber die Adapter)
		Activator.projectDataFactory.saveOrUpdateProjectData(getShell(), lAdapters, ntProject);
		
		// 'commit' in jedem Adapter 
		for(IProjectDataAdapter iAdapter : lAdapters)
		{
			iAdapter.setProject(ntProject);
			iAdapter.save();
		}

		// das neue Projekt im Navigator selektieren
		final ISelection selection = new StructuredSelection(ntProject.getIProject());		
		((ResourceNavigator) navigator).selectReveal(selection);

		
		// Initialisierung des Navigators wenn erstes Projekt im Workspace erzeugt wurde
		IWorkingSet ws = navigator.getAggregateWorkingSet();
		if(ws.isEmpty())	
			((ResourceNavigator) navigator).initDefaultResources();
				
		return true;
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
				// die Adapter sollen keinen Bezug zu einem selektierten Projekt haben 
				adapter.setProjectData(null);
				adapter.setProject(null);
				
				if(adapter.getWizardPage() != null)	
				{
					// die vom Adapter gelieferte WizardPage hinzufuegen 
					WizardPage wizardPage = adapter.getWizardPage();
					ContextInjectionFactory.invoke(wizardPage, PostConstruct.class, context);
					addPage(wizardPage);
				}

				// den Adapter selbst hinzufuegen
				page.addAdapter(adapter);
			}
		}

	}

	
}
