package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.expimp.dialogs.ImportExistProjects;
import it.naturtalent.e4.project.expimp.dialogs.ProjectImportDialog;
import it.naturtalent.e4.project.expimp.ecp.ECPExportHandlerHelper;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.datatransfer.CopyFilesAndFoldersOperation;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

public class ImportAction extends Action
{

	private Map<String, File>mapImportFiles;
	
	// bereits existierende Projekte
	private List<IProject>existProjects;
	private List<String>lExistFiles;
	
	private Shell shell;
	private IEventBroker eventBroker;
	
	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;
	
	private static Log log = LogFactory.getLog(ImportAction.class);

	@PostConstruct
	public void postConstruct(UISynchronize sync,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			@Optional IEventBroker eventBroker,
			@Optional INtProjectPropertyFactoryRepository projektDataFactoryRepository)
	{
		this.shell = shell;
		this.eventBroker = eventBroker;
		this.projektDataFactoryRepository = projektDataFactoryRepository;
	}
		
	@Override
	public void run()
	{
		ProjectImportDialog dialog = new ProjectImportDialog(shell);
		if(dialog.open() == ProjectImportDialog.OK)
		{
			// Importdaten aus dem Dialog holen
			File [] importProjects = dialog.getResultImportSource();
			List<IWorkingSet> selectedWorkingSets = dialog.getAssignedWorkingSets();
			
			// zu importierende und vorhandene Projekte separieren
			mapImportFiles = new HashMap<String, File>();
			lExistFiles = new ArrayList<String>();
			existProjects = new ArrayList<IProject>();
			detectExistProjects(importProjects);
			
			if(!lExistFiles.isEmpty())
			{
				// Ueber vorhandene Projekte informieren
				ImportExistProjects existProjectDialog = new ImportExistProjects(shell,lExistFiles);
				existProjectDialog.open();
				
				// vorhandene Projekte den ausgewaehlten WorkingSets zuordnen				
				if(!selectedWorkingSets.isEmpty())
				{
					IWorkingSet[] workingSets = selectedWorkingSets
							.toArray(new IWorkingSet[selectedWorkingSets.size()]);
					WorkingSetManager workingSetManager = Activator
							.getWorkingSetManager();					
					for(IProject project : existProjects)
						workingSetManager.addToWorkingSets(project,workingSets);					
				}				
			}

			// Abbruch, wenn alle zu importierenden Projekte bereits vorhanden sind
			if(mapImportFiles.isEmpty())
				return;
			
			// mit den effective zu importierenden Projekten weiterarbeiten
			Collection<File>colFiles = mapImportFiles.values();
			importProjects = colFiles.toArray(new File[colFiles.size()]);
			
			// ProjektId u. Aliasname mappen
			Map<String,String>mapProjectNames = getImportedAliasNames(importProjects);
			
			// die zuimportierenden Projekte erzeugen
			if((selectedWorkingSets != null) && (!selectedWorkingSets.isEmpty()))
				WorkbenchContentProvider.newAssignedWorkingSets = selectedWorkingSets.toArray(new IWorkingSet[selectedWorkingSets.size()]);					
			CreateNewProject.createProject(shell,mapProjectNames);
			WorkbenchContentProvider.newAssignedWorkingSets = null;

			// Dateien fuer jedes einzene Projekt in einer Map zusammenfassen
			final HashMap<String,String> newlyCreatedProjects = Activator.newlyCreatedProjectMap;
			Map<IProject, String[]>sourceMap = new HashMap<IProject, String[]>();
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			for (String key : newlyCreatedProjects.keySet())
			{
				IProject iProject = workspaceRoot.getProject(key);
				File impFile = mapImportFiles.get(key);
				
				if((impFile != null) && (iProject.exists()))
				{
					String [] srcFiles = impFile.list(new FilenameFilter()
					{						
						@Override
						public boolean accept(File dir, String name)
						{							
							return !name.equals(".project");
						}
					});
										
					for(int i = 0;i < srcFiles.length;i++)
						srcFiles[i] = impFile.getPath()+File.separator+srcFiles[i];
					
					sourceMap.put(iProject, srcFiles);
				}
				
				
				importProperties(impFile);
				
			}
			
			// Projektdateine kopieren
			CopyFilesAndFoldersOperation copyFileAndFolder = new CopyFilesAndFoldersOperation(shell);
			copyFileAndFolder.copyFileStores(shell, sourceMap);
			
			// Initialisierung des Navigators wenn erstes Projekt im Workspace erzeugt wurde
			if (eventBroker != null)
			{
				if (eventBroker != null)
					eventBroker.post(IResourceNavigator.NAVIGATOR_EVENT_IMPORTED, newlyCreatedProjects.keySet());
			}
		}
	}
	
	private void importProperties(File impFile)
	{
			
		List<EObject>propertiesData = getImportProperty(impFile);		
		List<INtProjectPropertyFactory>allPropertyFactoris = projektDataFactoryRepository.getAllProjektDataFactories();
		for(INtProjectPropertyFactory propertyFactory : allPropertyFactoris)
		{
			INtProjectProperty ntProjectProperty = propertyFactory.createNtProjektData();
			
			for(EObject eObject : propertiesData)
				if(ntProjectProperty.importProperty(eObject))
					break;
		}
		
		
		
		/*
		List<INtProjectProperty> projectProperties = NtProjektPropertyUtils
				.getProjectProperties(projektDataFactoryRepository,iProject);
		
		// List sammelt alle dem Projekt zugeordneten PropertyDaten 
		final List<EObject> projectPropertyData = new LinkedList<EObject>();
		
		// die konkreten Daten der jeweiligen Eigenschaft auflisten
		if (projectProperties != null)
		{
			for (INtProjectProperty projectProperty : projectProperties)
			{
				Object obj = projectProperty.getNtPropertyData();
				if (obj instanceof EObject)
					projectProperty.importProperty(EcoreUtil.copy((EObject) obj));
			}
		}
		*/
		
	}
	
	private void detectExistProjects(File [] importFiles)
	{		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot(); 
		
		for (File file : importFiles)
		{
			// ueberspringen, wenn keine gueltige ImportExpost-Datei vorhanden ist
			String projectName = getProjektName(file);
			if (StringUtils.isNotEmpty(projectName))
			{
				String projectId = file.getName();
				IProject iProject = workspaceRoot.getProject(projectId);

				if (iProject.exists())
				{
					lExistFiles.add(projectName);
					existProjects.add(iProject);
				}
				else
					mapImportFiles.put(projectId, file);
			}			
			else log.error("ungueltige Importdatei " + file.getName());
		}
	}
	
	private Map<String,String>getImportedAliasNames(File [] importFiles)
	{
		Map<String, String> mapImportProjectNames = new HashMap<String, String>();

		for (File impFile : importFiles)
		{			
			String projectName = getProjektName(impFile);
			if(StringUtils.isNotEmpty(projectName))
				mapImportProjectNames.put(impFile.getName(),projectName);
			else
				log.error("ungueltige Importdatei " + impFile.getName());
		}

		return mapImportProjectNames;
	}
	
	private List<EObject> getImportProperty(File file)
	{
		// Path zur ImportExport-Datei vervollstaendigen 
		File projectFile = new File(file, IProjectData.PROJECTDATA_FOLDER);
		projectFile = new File(projectFile, ExportAction.IMPEXPORTFILE_NAME);

		// Resource laden
		URI fileURI = URI.createFileURI(projectFile.getPath());
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(fileURI, true);

		return resource.getContents();
	}
	
	/**
	 * Ermittelt ueber die ImportExport Datei den Namen des Projekts.
	 * Filtert die PopertyDaten von NtProject und gibt den dort gespeicherten Namen zurueck.
	 *  
	 * @param file - Verzeichnis des IProjects im Importverzeichnis 
	 * @return - Name des Projekts
	 */
	private String getProjektName(File file)
	{
		// Path zur ImportExport-Datei vervollstaendigen 
		File projectFile = new File(file, IProjectData.PROJECTDATA_FOLDER);
		projectFile = new File(projectFile, ExportAction.IMPEXPORTFILE_NAME);

		// Resource laden
		URI fileURI = URI.createFileURI(projectFile.getPath());
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(fileURI, true);

		// NtProject filtern
		for(EObject eObject : resource.getContents())
		{
			if (eObject instanceof it.naturtalent.e4.project.model.project.NtProject)
				return ((it.naturtalent.e4.project.model.project.NtProject) eObject).getName();	
		}
		
		return null;
	}
	
	
}
