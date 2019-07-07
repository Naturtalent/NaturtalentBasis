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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.wizard.IWizardPage;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.ProjectFactory;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.actions.OpenProjectAction;
import it.naturtalent.e4.project.ui.actions.SystenOpenEditorAction;
import it.naturtalent.e4.project.ui.actions.emf.UndoProjectAction;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizardPage;


/**
 * Dieser Adapter implementiert die Eigenschaft des NtProjekts und ist obligatorisch fuer jedes NtProjekt.
 *  
 * @see it.naturtalent.e4.project.expimp.actions.ExportAction 
 * @see it.naturtalent.e4.project.expimp.actions.ExportAction
 * 
 * @author dieter
 *
 */

public class NtProjectProperty implements INtProjectProperty
{
	
	// EMF-Modell der Projekteigenschaft
	private NtProject ntPropertyData = ProjectFactory.eINSTANCE.createNtProject();
	
	// in diese Datei werden die Projekteigenschaften waehrend des Exports gespeichert
	public static final String EXPIMP_NTPROJECTDATA_FILE = ".ntProjectData.xmi";
	
	// ID des NtProjekts auf den sich der Adapter bezieht
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
		
		// fuer jedes ProjektID(Workspaceprojekt) muss ein NtProject (EMF-Modell) existieren
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

	/**
	 * ein Click auf die Projekteigenschaft oeffnet den eigenen Projekteigenschaftsdialog.
	 */
	@Override
	public Action createAction()
	{
		OpenProjectAction openProject = ContextInjectionFactory.make(OpenProjectAction.class, context);		
		return openProject;
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
	public void importProperty()
	{		
		// Eigenschaft (NtProject) wird aus der Propertydatei generiert
		EObject niProjectData = ExpImpUtils.importNtPropertyData(EXPIMP_NTPROJECTDATA_FILE, ntProjectID);
		if (niProjectData instanceof NtProject)
		{
			NtProject ntProject = (NtProject) niProjectData;
			
			// nochmal pruefen, ob die IDs uebereinstimmen
			if(StringUtils.equals(ntProjectID, ntProject.getId()))
				Activator.getNtProjects().getNtProject().add(ntProject);
		}
	}

	/* 
	 * Beim Exportieren wird das zuexportierende Objekt geloescht (warum ist unklar). Als Workaroung wird vor dem
	 * exportieren eine Kopie gezogen und nachher wieder eigefuegt. 
	 */
	@Override
	public void exportProperty()
	{
		// sind Propertydaten vorhanden @see setNtProjectID(String ntProjectID)
		if(ntPropertyData != null)
		{
			String projectID = ntPropertyData.getId();
			if(StringUtils.isNotEmpty(projectID))
			{
				// wenn die Propertydaten und projektid uebereinstimmen wird exportiert 
				if(StringUtils.equals(projectID, ntProjectID))
					ExpImpUtils.exportNtPropertyData(projectID, ntPropertyData, EXPIMP_NTPROJECTDATA_FILE);
			}
		}
	}
	
}
