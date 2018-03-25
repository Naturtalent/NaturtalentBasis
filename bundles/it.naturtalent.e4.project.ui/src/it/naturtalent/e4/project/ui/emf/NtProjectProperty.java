package it.naturtalent.e4.project.ui.emf;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.wizard.IWizardPage;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.model.project.ProjectFactory;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.actions.emf.DeleteProjectAction;
import it.naturtalent.e4.project.ui.actions.emf.OpenProjectPathAction;
import it.naturtalent.e4.project.ui.actions.emf.UndoProjectAction;
import it.naturtalent.e4.project.ui.handlers.emf.SystemOpenHandler;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizardPage;

/**
 * ProjectProperty-Klasse des NtProjekts.
 * 
 * Diese Klasse ist obligatorisch fuer jedes NtProjekt und definiert die grundlegenden Eigenschaften eines
 * NtProjekts. 
 * 
 * @author dieter
 *
 */

public class NtProjectProperty implements INtProjectProperty
{
	
	// die eigentlichen Propertydaten
	private NtProject ntPropertyData = ProjectFactory.eINSTANCE.createNtProject();
	
	// ID des Projekts, auf das sich die Eigenschaft bezieht
	protected String ntProjectID;
	
	private IEclipseContext context;
	
	private IEventBroker eventBroker;
	
	private Log log = LogFactory.getLog(NtProjectProperty.class);

	
	@PostConstruct
	public void postConstruct(@Optional IEclipseContext context, @Optional IEventBroker eventBroker)
	{
		this.context = context;
		this.eventBroker = eventBroker;
	}
	
	@Override
	public void setNtProjectID(String ntProjectID)
	{
		this.ntProjectID = ntProjectID;		
		
		NtProject ntProject = Activator.findNtProject(ntProjectID);
		if(ntProject != null)
			ntPropertyData = ntProject;
	}


	@Override
	public String getNtProjectID()
	{
		return (ntPropertyData != null) ? ntPropertyData.getId() : null; 		
	}

	@Override
	public void setNtPropertyData(Object eObject)
	{
		if(eObject instanceof NtProject)
			ntPropertyData = (NtProject) eObject;
		
	}

	@Override
	public Object getNtPropertyData()
	{		
		return ntPropertyData;
	}

	@Override
	public Object getPropertyContainer()
	{		
		return Activator.getNtProjects();
	}

	@Override
	public void commit()
	{	
		if (ntPropertyData != null)
		{
			if (StringUtils.isEmpty(ntPropertyData.getId()))
			{
				// noch keine ID - ntProject wurde neu erzeugt
				ntPropertyData.setId(ntProjectID);
				Activator.getNtProjects().getNtProject().add(ntPropertyData);
			}

			try
			{
				// die Projekteigenschaft 'name' im Workspaceprojekt 'IProject'
				// persistent uebernehmen
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(ntPropertyData.getId());
				iProject.setPersistentProperty(
						INtProject.projectNameQualifiedName,
						ntPropertyData.getName());

			} catch (CoreException e)
			{
				log.error(e);
			}
		}
		
		// gesamte ECP-Projekt wird gespeichert
		ECPHandlerHelper.saveProject(Activator.getECPProject());
		eventBroker.send(UndoProjectAction.PROJECTCHANGED_MODELEVENT,"Model saved");
	}
	

	/* 
	 * WizardPage fuer die obligatorischen Projekteigenschaften erzeugen.
	 * Der WizardPage wird dieser Adapter uebergeben.
	 *  
	 * (non-Javadoc)
	 * @see it.naturtalent.e4.project.INtProjectProperty#createWizardPage()
	 */
	@Override
	public IWizardPage createWizardPage()
	{
		ProjectPropertyWizardPage projectWizardPage = ContextInjectionFactory.make(ProjectPropertyWizardPage.class, context);	
		projectWizardPage.setProjectProperty(this);		
		return projectWizardPage;
		
	}

	
	public String toString()
	{
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProjectID);
		if(iProject.exists())		
			return iProject.getLocation().toOSString();
		//return (StringUtils.isEmpty(ntProjectID) ? "NtProjekt undefiniert" : "erstellt am: "+getCreatedDate());
		return super.toString(); 
	}
	
	@Override
	public void undo()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete()
	{
	}

	@Override
	public String getLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action createAction()
	{
		StructuredViewer viewer = Activator.findNavigator().getViewer();		
		return new OpenProjectPathAction(viewer);
	}
	
	/*

	@Override
	public void setUndoEventKey(String undoEventKey)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDeleteEventKey(String deleteEventKey)
	{
		// TODO Auto-generated method stub
		
	}

*/
	
	@Override
	public Object init()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * aus der NtProjectID das Erstellungsdatum generieren
	 * 
	 * @return
	 */
	private String getCreatedDate()
	{		
		String stgDate = ntProjectID.substring(0, ntProjectID.indexOf('-'));
		Date date = new Date(NumberUtils.createLong(stgDate));
		return (DateFormatUtils.format(date, "dd.MM.yyyy")); 
	}

	@Override
	public String getPropertyFactoryName()
	{
		return this.getClass().getName()+NtProjectPropertyFactory.PROJECTPROPERTYFACTORY_EXTENSION;
	}

	@Override
	public boolean importProperty(Object importData)
	{		
		return false;
		
	}

	
}
