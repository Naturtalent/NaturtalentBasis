package it.naturtalent.e4.project.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;

@Deprecated
public class OpenProjectWizard extends Wizard
{
	
	private OpenProjectWizardPage openWizardPage;
	
	private List<INtProjectProperty>projectProperties;
	
	protected IProject iProject;
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;

	public OpenProjectWizard()
	{
		super();
		setWindowTitle("OpenProjectWizard");				
	}
	
	@PostConstruct
	private void postConstruct(@Optional IEclipseContext context,
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject)
	{
		if(context != null)
		{
			openWizardPage = ContextInjectionFactory.make(OpenProjectWizardPage.class, context);
			addPage(openWizardPage);
			
			projectProperties = NtProjektPropertyUtils.getProjectProperties(
					ntProjektDataFactoryRepository, iProject);
			
			//System.out.println(factoryRepository);
		}
		
		this.iProject = iProject;
	}
	
	/**
	 * Die projekteigenen PropertyFactoryNames werden vom OpenHandler uebergeben.
	 * 
	 * @param propertyFactories
	 */

	public void setPropertyFactories(List<INtProjectPropertyFactory> propertyFactories)
	{		
		projectProperties = new ArrayList<INtProjectProperty>();
		if(!propertyFactories.isEmpty())
		{
			for(INtProjectPropertyFactory propertyFactory : propertyFactories)
			{
				INtProjectProperty projectProperty = propertyFactory.createNtProjektData();
				projectProperty.setNtProjectID(iProject.getName());
				projectProperties.add(projectProperty);				
			}
		}
	}
	
	
	/**
	 * Die projekteigenen PropertyFactoryNames werden vom OpenHandler uebergeben.
	 * 
	 * @param propertyFactories
	 */
	/*
	public void setPropertyFactories(INtProjectPropertyFactory[] propertyFactories)
	{		
		projectProperties = new ArrayList<INtProjectProperty>();
		if(ArrayUtils.isNotEmpty(propertyFactories))
		{
			for(INtProjectPropertyFactory propertyFactory : propertyFactories)
			{
				INtProjectProperty projectProperty = propertyFactory.createNtProjektData();
				projectProperties.add(projectProperty);				
			}
		}
	}
	*/
	
	/*
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
						openWizardPage.setProjectProperty((NtProjectProperty) projectProperty);	
				}
			}
		}
	}
	*/

	
	@Override
	public void addPages()
	{
		if((projectProperties != null) && (!projectProperties.isEmpty()))
		{
			// Propertydaten laden, zugehoerige WizardPage erzeugen und hinzufuegen
			for(INtProjectProperty projectProperty : projectProperties)
			{
				if (projectProperty.init() != null)
				{
					IWizardPage page = projectProperty.createWizardPage();
					if (page != null)
						addPage(page);
					else
					{
						// NtProjectProperty der Page 'OpenProjectWizardPage'
						// separat zuordnen
						if (StringUtils.equals(
								projectProperty.getClass().getName(),
								NtProjectProperty.class.getName()))
							openWizardPage.setProjectProperty(
									(NtProjectProperty) projectProperty);
					}
				}
				else
				{
					// Fehler: PropertyFactory vorhanden aber kein Modellelement gespeichert 
					// Fazit: PropertyFactory ebenfalls loeschen						
					String propertyName = projectProperty.getClass().getName();
					List<INtProjectPropertyFactory>factories = ntProjektDataFactoryRepository.getAllProjektDataFactories();
					for(INtProjectPropertyFactory factory : factories)
					{
						INtProjectProperty property = factory.createNtProjektData();
						if(StringUtils.equals(propertyName, property.getClass().getName()))
						{
							new ProjectPropertySettings().removePropertyFactory(iProject,factory.getClass().getName());
							break;
						}						
					}					
				}
			}
		}
	}
	

	@Override
	public boolean performFinish()
	{
		if (iProject.isOpen())
		{
			IResourceNavigator navigator = Activator.findNavigator();

			// die mit 'page' neuangelegten WorkingSets hinzufuegen
			List<IWorkingSet> addedWorkingSets = openWizardPage.getAddedWorkingSets();
			if (addedWorkingSets != null)
			{
				IWorkingSet[] windowWorkingSets = navigator
						.getWindowWorkingSets();
				for (IWorkingSet addedWorkingSet : addedWorkingSets)
					windowWorkingSets = ArrayUtils.add(windowWorkingSets,
							addedWorkingSet);
				navigator.setWorkingSets(windowWorkingSets);
			}

			// Die neue WorkingSetzuordnung dem ContentProvider mitteilen
			List<IWorkingSet> lNewAssignedWorkingSets = openWizardPage
					.getAssignedWorkingSets();
			WorkbenchContentProvider.newAssignedWorkingSets = lNewAssignedWorkingSets
					.toArray(new IWorkingSet[lNewAssignedWorkingSets.size()]);

			// ProjectPropertydaten abspeichern
			if ((projectProperties != null) && (!projectProperties.isEmpty()))
			{
				// ProjectProperties via 'INtProjectProperty' abspeichern
				for (INtProjectProperty projectProperty : projectProperties)
					projectProperty.commit();
			}

			WorkbenchContentProvider.newAssignedWorkingSets = null;
		}
		return true;
	}

	@Override
	public boolean performCancel()
	{
		if((projectProperties != null) && (!projectProperties.isEmpty()))
		{
			for(INtProjectProperty projectProperty : projectProperties)		
				projectProperty.undo();		
		}
		return super.performCancel();
	}

	public void setProjectProperties(List<INtProjectProperty> projectProperties) {
		this.projectProperties = projectProperties;
	}
	
	

}
