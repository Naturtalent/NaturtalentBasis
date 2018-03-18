package it.naturtalent.e4.project.ui.wizards.emf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

/**
 * Zentraler Wizard zum Erstellen und Aendern von Projekten.
 * 
 * @author dieter
 *
 */
public class ProjectPropertyWizard extends Wizard
{
	
	private ProjectPropertyWizardPage propertyWizardPage;
		
	private List<INtProjectProperty>projectPropertyAdapters;
	
	protected IProject iProject;
	
	private IEclipseContext context;
	
	// vordefinierter Projektname
	private String predefinedProjectName = null;

	/**
	 * Konstruktion
	 */
	public ProjectPropertyWizard()
	{
		super();
		setWindowTitle("OpenProjectWizard");				
	}
	
	@PostConstruct
	private void postConstruct(@Optional IEclipseContext context,
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject)
	{
		this.context = context;
		this.iProject = iProject;
		
		/*
		if(context != null)
		{
			this.context = context;
			
	
			propertyWizardPage = ContextInjectionFactory.make(ProjectPropertyWizardPage.class, context);
			addPage(propertyWizardPage);
			
		
			projectProperties = NtProjektPropertyUtils.getProjectProperties(
					ntProjektDataFactoryRepository, iProject);
		
		}
		*/
		
		
	}
	

	
	/**
	 * Mit den uebergeben PropertyFactories werden die zuzuordnenden ProjectProperties erzeugt und in der Liste
	 * 'projectProperties' zusammengefasst.
	 * 
	 * @param propertyFactories
	 */

	public void setPropertyFactories(List<INtProjectPropertyFactory> propertyFactories)
	{		
		projectPropertyAdapters = new ArrayList<INtProjectProperty>();
		if(!propertyFactories.isEmpty())
		{
			for(INtProjectPropertyFactory propertyFactory : propertyFactories)
			{
				INtProjectProperty projectProperty = propertyFactory.createNtProjektData();
				projectProperty.setNtProjectID((iProject != null) ? iProject.getName() : null);	
				
				// das obligatorische Property soll als erste Page im Wizard gezeigt werden
				if(projectProperty instanceof NtProjectProperty)
					projectPropertyAdapters.add(0,projectProperty);
				else
					projectPropertyAdapters.add(projectProperty);				
			}
		}
	}
	
	/* 
	 * Es werden alle Property-WizardPages hinzugefuegt, die ueber die ProjectPropertyAdapter 
	 * definiert sind und erzeugt werden.
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		if((projectPropertyAdapters != null) && (!projectPropertyAdapters.isEmpty()))
		{
			// spezifische WizardPages erzeugen und dem Wizard hinzufuegen
			for(INtProjectProperty projectPropertyAdapter : projectPropertyAdapters)
			{
				IWizardPage page = projectPropertyAdapter.createWizardPage();
				if (page != null)
				{
					addPage(page);
										
					if(page instanceof ProjectPropertyWizardPage)
					{
						// die ProjektWizardPage wird noch benoetigt (in performFinish())
						propertyWizardPage = (ProjectPropertyWizardPage) page;
						
						if(StringUtils.isNotEmpty(predefinedProjectName))
						{
							// gibt es einen vordefinierten Projektnamen, wird dieser uebernommen
							NtProject ntProject = (NtProject)projectPropertyAdapter.getNtPropertyData();
							ntProject.setName(predefinedProjectName);
						}						
					}
							
				}
				
				/*
				else
				{
					// NtProjectProperty der Page 'OpenProjectWizardPage'
					// separat zuordnen
					if (StringUtils.equals(
							projectProperty.getClass().getName(),
							NtProjectProperty.class.getName()))
						propertyWizardPage.setProjectProperty(
								(NtProjectProperty) projectProperty);
				}
				*/

			}
		}
	}


	@Override
	public boolean performFinish()
	{
		String ntProjectID = null;
		
		if(iProject == null)
		{
			// neues Projekt
			
			// die im Wizard dem neuen Projekt zugeordneten WorkingSets in 'processDelta()' beruecksichtigen
			List<IWorkingSet>lNewAssignedWorkingSets = propertyWizardPage.getAssignedWorkingSets();
			if ((lNewAssignedWorkingSets != null) && (!lNewAssignedWorkingSets.isEmpty()))
				WorkbenchContentProvider.newAssignedWorkingSets = lNewAssignedWorkingSets
						.toArray(new IWorkingSet[lNewAssignedWorkingSets.size()]);
			
			
			String projectName = null;
			if((projectPropertyAdapters != null) && (!projectPropertyAdapters.isEmpty()))
			{
				// ProjectProperties via 'INtProjectProperty' abspeichern
				String [] settingPropertyFactoryNames = null;
				for(INtProjectProperty projectProperty : projectPropertyAdapters)
				{
					EObject propertyData = (EObject) projectProperty.getNtPropertyData();					
					if (propertyData instanceof NtProject)
					{
						NtProject ntProject = (NtProject) propertyData;
						projectName = ntProject.getName();
						break;
					}					
				}
			}
					
			// CreateNewProject provoziert ueber den Listener 'processDelta()'-Aufruf in WorkbenchContentProvider
			CreateNewProject.createProject(getShell(),new String [] {projectName});
			WorkbenchContentProvider.newAssignedWorkingSets = null;
			
			// (test) sicherstellen dass 'Other' - Workingset aktuell bleibt
			Activator.getWorkingSetManager().updateOthers();
			
			// ID des neuen Projekts 
			ntProjectID = (String) Activator.newlyCreatedProjectMap.keySet().toArray()[0];
			iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProjectID);

			// Initialisierung des Navigators wenn erstes Projekt im Workspace erzeugt wurde
			ResourceNavigator navigator = (ResourceNavigator) Activator.findNavigator();
			IWorkingSet ws = navigator.getAggregateWorkingSet();
			if(ws.isEmpty())	
				((ResourceNavigator) navigator).initDefaultResources();
		}
		else
		{
			// bestehendes Projekt
			
			if (iProject.isOpen())
			{
				ntProjectID = iProject.getName();
				
				IResourceNavigator navigator = Activator.findNavigator();
				
				// zunaechst Projekt aus allen WorkingSets entfernen
				WorkingSetManager workingSetManager = Activator.getWorkingSetManager();
				IAdaptable [] adaptables = {iProject};
				workingSetManager.removeWorkingSetsElements(adaptables);

				List<IWorkingSet> assignedWorkingSets = propertyWizardPage.getAssignedWorkingSets();
				for(IWorkingSet workingSet : assignedWorkingSets)
				{
					IAdaptable[] wsAdaptables = workingSet.getElements();
					wsAdaptables = ArrayUtils.add(wsAdaptables, iProject);
					workingSet.setElements(wsAdaptables);					
				}

				// ResourceNavigator aktualisieren
				navigator.getViewer().refresh();
			}
			
		}

		// ProjectPropertydata abspeichern
		if((projectPropertyAdapters != null) && (!projectPropertyAdapters.isEmpty()))
		{
			for(INtProjectProperty projectProperty : projectPropertyAdapters)
			{
				projectProperty.setNtProjectID(iProject.getName());
				projectProperty.commit();
			}
		}
		
		/*
		if((projectPropertyAdapters != null) && (!projectPropertyAdapters.isEmpty()))
		{
			// ProjectProperties via 'INtProjectProperty' abspeichern
			String [] settingPropertyFactoryNames = null;
			for(INtProjectProperty projectProperty : projectPropertyAdapters)
			{				
				projectProperty.setNtProjectID(ntProjectID);
				projectProperty.commit();
				
				// in die Liste der FactoryNamen eintragen
				settingPropertyFactoryNames = ArrayUtils.add(settingPropertyFactoryNames,
						projectProperty.getClass().getName()
								+ INtProjectPropertyFactory.PROJECTPROPERTYFACTORY_EXTENSION);
			}
		
			// PropertyFactoryNames in Datei speichern und UpdateRequest an ProjectView
			//NtProjektPropertyUtils.saveProjectPropertyFactories(ntProjectID, settingPropertyFactoryNames);
			
			// Updateanforderung an ProjectView senden	
			MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
			IEventBroker eventBroker = currentApplication.getContext().get(IEventBroker.class);
			NtProject ntProject = Activator.findNtProject(ntProjectID);
			eventBroker.post(NtProjectView.UPDATE_PROJECTVIEW_REQUEST, ntProject);
	
		}
		*/
		
		// IProject im Navigator selektieren
		IResourceNavigator navigator = Activator.findNavigator();
		navigator.getViewer().setSelection(new StructuredSelection(iProject), true);
		
		return true;
	}

	@Override
	public boolean performCancel()
	{
		if((projectPropertyAdapters != null) && (!projectPropertyAdapters.isEmpty()))
		{
			for(INtProjectProperty projectProperty : projectPropertyAdapters)		
				projectProperty.undo();		
		}
		return super.performCancel();
	}

	/*
	public void setProjectProperties(List<INtProjectProperty> projectProperties) {
		this.projectProperties = projectProperties;
	}
	*/
	
	

	public IProject getiProject()
	{
		return iProject;
	}

	public void setPredefinedProjectName(String predefinedProjectName)
	{
		this.predefinedProjectName = predefinedProjectName;
	}

	public void setiProject(IProject iProject)
	{
		this.iProject = iProject;
	}
	
	
	

}
