package it.naturtalent.e4.project.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;

@Deprecated
public class CreateProjectWizard extends Wizard
{
	
	private CreateProjectWizardPage createWizardPage;
	
	private IEclipseContext context;
	
	// Namen der dem Projekt zugeordneten PropertyFactories
	private String [] settingPropertyFactoryNames = null;
	
	// optionale Projekteigenschaften
	private List<INtProjectProperty>projectProperties;
	
	// obligatorische Projekteigenschaft
	private NtProjectProperty ntProjectProperty = null;
	

	public CreateProjectWizard()
	{
		super();
		setWindowTitle("Project Wizard");				
	}
	
	@PostConstruct
	private void postConstruct(@Optional IEclipseContext context)
	{
		if(context != null)
		{
			createWizardPage = ContextInjectionFactory.make(CreateProjectWizardPage.class, context);
			addPage(createWizardPage);
		}		
	}
	
	
	
	/**
	 * Mit den uebergebenen Factories werden die ProjektProperties erzeugt und aufgelistet.
	 * Die Klassennamen der benutzten Factories werden zusaetzlich aufgelistet. 
	 * 
	 * @param propertyFactories
	 */
	public void setPropertyFactories(INtProjectPropertyFactory[] propertyFactories)
	{
		//this.propertyFactories = propertyFactories;
		projectProperties = new ArrayList<INtProjectProperty>();
		if(ArrayUtils.isNotEmpty(propertyFactories))
		{
			for(INtProjectPropertyFactory propertyFactory : propertyFactories)
			{
				INtProjectProperty projectProperty = propertyFactory.createNtProjektData();
				projectProperties.add(projectProperty);
				
				// Zugriff auf obligatorische Projekteigenschaft 'NtProjectProperty' sicherstellen			
				if(StringUtils.equals(projectProperty.getClass().getName(), NtProjectProperty.class.getName()))
					ntProjectProperty = (NtProjectProperty) projectProperty;
							
				// Factoryname sichern
				settingPropertyFactoryNames = ArrayUtils.add(
						settingPropertyFactoryNames,
						propertyFactory.getClass().getName());
			}
			
			// sicherstellen, dass die obligatorische PropertyFactory 'NtProjectProperty' uebernommen werden
			INtProjectProperty checkNtPprojectProperty = null;
			for(INtProjectProperty projectProperty : projectProperties)
			{
				if(StringUtils.equals(projectProperty.getClass().getName(),NtProjectProperty.class.getName()))
				{
					ntProjectProperty = (NtProjectProperty) projectProperty;
					break;
				}						
			}
			if(checkNtPprojectProperty == null)
			{
				ntProjectProperty = new NtProjectProperty();
				projectProperties.add(ntProjectProperty);
			}
			
			// sicherstellen, dass der obligatorische PropertyFactoryName uebernommen wird
			if(!ArrayUtils.contains(settingPropertyFactoryNames, NtProjectPropertyFactory.class.getName()))
				settingPropertyFactoryNames = ArrayUtils.add(settingPropertyFactoryNames, NtProjectPropertyFactory.class.getName());
			
		}
	}

	@Override
	public void addPages()
	{
		if((projectProperties != null) && (!projectProperties.isEmpty()))
		{
			for(INtProjectProperty projectProperty : projectProperties)
			{
				IWizardPage page = projectProperty.createWizardPage();
				if(page != null)
					addPage(page);	
				else
				{
					// NtProjectProperty der Page 'CreatProjectWizardPage' separat zuordnen 
					if(StringUtils.equals(projectProperty.getClass().getName(),NtProjectProperty.class.getName()))					
						createWizardPage.setProjectProperty((NtProjectProperty) projectProperty);	
				}
			}
		}
	}

	@Override
	public boolean performFinish()
	{
		// die im Wizard dem neuen Projekt zugeordneten WorkingSets in 'processDelta()' beruecksichtigen
		List<IWorkingSet>lNewAssignedWorkingSets = createWizardPage.getAssignedWorkingSets();
		if ((lNewAssignedWorkingSets != null)
				&& (!lNewAssignedWorkingSets.isEmpty()))
			WorkbenchContentProvider.newAssignedWorkingSets = lNewAssignedWorkingSets
					.toArray(new IWorkingSet[lNewAssignedWorkingSets.size()]);
		
		// die bearbeiteten ProjectProperyDaten holen 
		ProjectData projectData = (ProjectData) ntProjectProperty.getNtPropertyData(); 
				
		// CreateNewProject provoziert ueber den Listener 'processDelta()'-Aufruf in WorkbenchContentProvider
		String projectName = projectData.getName();
		CreateNewProject.createProject(getShell(),new String [] {projectName});
		WorkbenchContentProvider.newAssignedWorkingSets = null;
		
		// (test) sicherstellen dass 'Other' - Workingset aktuell bleibt
		Activator.getWorkingSetManager().updateOthers();
			
		// ID des neuen Projekts (an alle ProjectProperties uebergeben)
		String ntProjectID = (String) Activator.newlyCreatedProjectMap.keySet().toArray()[0];
		
		// ProjectProperties und Factorynames abspeichern
		if((projectProperties != null) && (!projectProperties.isEmpty()))
		{
			// ProjectProperties via 'INtProjectProperty' abspeichern
			for(INtProjectProperty projectProperty : projectProperties)
			{				
				projectProperty.setNtProjectID(ntProjectID);
				projectProperty.commit();
			}
			
			// Die Factorynamen im Projektdatenbereich speichern
			ProjectPropertyData projectPropertyData = new ProjectPropertyData();
			projectPropertyData.setPropertyFactories(settingPropertyFactoryNames);
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProjectID);
			ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
			projectPropertySettings.put(iProject, projectPropertyData);

		}
		
		return true;		
	}

	@Override
	public boolean performCancel()
	{
		if((projectProperties != null) && (!projectProperties.isEmpty()))
		{
			for(INtProjectProperty projectProperty : projectProperties)
			{
				projectProperty.undo();
			}
		}

		return super.performCancel();
	}
	
	

}
