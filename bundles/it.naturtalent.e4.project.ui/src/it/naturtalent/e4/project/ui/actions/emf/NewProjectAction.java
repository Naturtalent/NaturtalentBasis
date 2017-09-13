package it.naturtalent.e4.project.ui.actions.emf;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.ProjectPropertyDialog;
import it.naturtalent.e4.project.ui.emf.ProjectModelEventKey;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizard;

/**
 * Mit dieser Aktion wird ein neues Projekt erzeugt.
 * 
 * @author dieter
 *
 */
public class NewProjectAction extends Action
{

	@Inject @Optional private Shell shell;
	@Inject @Optional private IEclipseContext context;
	@Inject @Optional private MPart part;
	
	@Inject @Optional private ESelectionService selectionService;
	
	@Inject @Optional private EPartService partService;
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	// im 'context' eingetragen verursacht dies eine Selektion den Projektnamens im Eingabefeld des Wizards
	public static final String PREDIFINED_PROJECTNAME = "PREDIFINED_PROJECTNAME";
	
	
	@Override
	public void run()
	{
		// Dialog zum Definieren der gewuenschten Projekteigenschaften
		ProjectPropertyDialog propertyDialog = new ProjectPropertyDialog(shell);
		
		// alle verfuegbaren PropertyFactories an Dialog uebergeben
		if (ntProjektDataFactoryRepository != null)
			propertyDialog.setNtProjectPropertyFactories( ntProjektDataFactoryRepository
					.getAllProjektDataFactories());
			
		// Projekteigenschaften im Dialog auswaehlen
		if(propertyDialog.open() == ProjectPropertyDialog.OK)
		{
			// ProjectWizard erzeugen
			ProjectPropertyWizard projectPropertyWizard = ContextInjectionFactory.make(ProjectPropertyWizard.class, context);
			
			// evtl. selektiertes IProject zuruecksetzen - neues Projekt soll erzeugt werden
			projectPropertyWizard.setiProject(null);
			
			// die mit ProjectPropertyDialog ausgewaehlten Projekteigenschaften dem ProjectWizard mitteilen 	
			List<INtProjectPropertyFactory>checkedPropertiesFactories = propertyDialog.getCheckedPropertyFactories();
			projectPropertyWizard.setPropertyFactories(checkedPropertiesFactories);	
				
			// Name des neuen Projekts mit dem momentan selektierten vorbelegen
			ResourceNavigator navigator = (ResourceNavigator) Activator.findNavigator();
			IStructuredSelection selection = (IStructuredSelection) navigator.getViewer().getSelection();
			Object selObject = selection.getFirstElement();
			if (selObject instanceof IResource)
			{
				IResource iResource = (IResource) selObject;
				IProject iProject = iResource.getProject();
				try
				{
					// Projektname soll im Eingabefeld des Wizards selektiert werden
					String name = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
					projectPropertyWizard.setPredefinedProjectName(name);
					context.set(PREDIFINED_PROJECTNAME, name);
				} catch (CoreException e)
				{					
				}
			}
				
			// Daten der Eigenschaften mit den jeweiligen Wizards bearbeiten
			WizardDialog projectWizardDialog = new WizardDialog(shell, projectPropertyWizard);
			if(projectWizardDialog.open() == WizardDialog.OK)
			{
				// die Klassennamen der zugeordneten PropertyFactories auflisten
				String [] settingPropertyFactoryNames = null;
								
				for (INtProjectPropertyFactory propertyFactory : checkedPropertiesFactories)
				{
					settingPropertyFactoryNames = ArrayUtils.add(settingPropertyFactoryNames,
							propertyFactory.getClass().getName());
				}
				
				// PropertyFactories im Datenbereich des NtProjekts speichern 
				ProjectPropertyData projectPropertyData = new ProjectPropertyData();
				projectPropertyData.setPropertyFactories(settingPropertyFactoryNames);
				ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
				IProject iProject = projectPropertyWizard.getiProject();
				projectPropertySettings.put(iProject, projectPropertyData);
				
				//navigator = (ResourceNavigator) Activator.findNavigator();
				selection = new StructuredSelection(iProject);		
				navigator.selectReveal(selection);
				
			}			
		}

	}


	
	@Override
	public boolean isHandled()
	{		
		if(selectionService != null)
		{
			Object obj = selectionService.getSelection();
			if(obj instanceof IResource)
			{
				IResource iResource = (IResource) obj;				
				if(iResource.getType() == IProject.PROJECT)
					if(!((IProject)iResource).isOpen())
						return false;
			}				
		}
				
		return true;		
	}
	
	
}
