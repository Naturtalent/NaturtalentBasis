package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.xml.bind.JAXB;

import org.apache.commons.lang3.ArrayUtils;
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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.expimp.dialogs.ProjectImportDialog;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.datatransfer.CopyFilesAndFoldersOperation;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;

/**
 * @author dieter
 *
 * Importiert ausgewaehlte NtProjekte die in das 'ImportVerzeichnis' exportiert wurden.
 * 
 */
public class ImportAction extends Action
{
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
		// mit dem Dialog das Quellverzeichnis der Importdaten auswaehlen		
		ProjectImportDialog dialog = new ProjectImportDialog(shell);
		if(dialog.open() == ProjectImportDialog.OK)
		{
			// die im Dialog selektierten ImportProjekte abfragen 
			EObject [] selectedImportObjects = dialog.getResultImportSource();
			
			// Verzeichnis indem die zu importierenden Projekte exportiert wurden
			final File importDir = new File(dialog.getImportSourceDirectory());
			
			// Mapped die selektierten ImportProjektIDs mit den in den Projekten gespeicherten Resourcen
			Map<String, String[]>mapImportFiles = prepareProjectResourceMap(importDir, selectedImportObjects);
						
			//Mapped ProjektId u. Name der seletierten ImportProjekte
			Map<String,String>createProjectMap = new HashMap<String, String>();
			for(EObject eObject : selectedImportObjects)
			{
				if (eObject instanceof NtProject)
				{
					NtProject ntProject = (NtProject) eObject;
					String projectID = ntProject.getId();
					String projectName = ntProject.getName();
					createProjectMap.put(projectID, projectName);
				}
			}
			final Set<String>selectedImportProjectIDs = createProjectMap.keySet();
			
			// die zuimportierenden NtProjekte erzeugen
			List<IWorkingSet> selectedWorkingSets = dialog.getAssignedWorkingSets();
			if((selectedWorkingSets != null) && (!selectedWorkingSets.isEmpty()))
				WorkbenchContentProvider.newAssignedWorkingSets = selectedWorkingSets.toArray(new IWorkingSet[selectedWorkingSets.size()]);					
			CreateNewProject.createProject(shell, createProjectMap);
			WorkbenchContentProvider.newAssignedWorkingSets = null;
			
			// IProject und zugehoerige Resourcen in einer Map zusammenfassen
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			final Map<IProject,String[]>importProjectMap = new HashMap<IProject, String[]>();
			for(EObject eObject : selectedImportObjects)
			{
				NtProject ntProject = (NtProject) eObject;
				IProject iProject = workspaceRoot.getProject(ntProject.getId());
				String [] importResources = mapImportFiles.get(ntProject.getId());
				importProjectMap.put(iProject, importResources);
			}
			
			BusyIndicator.showWhile(shell.getDisplay(), new Runnable()
			{
				@Override
				public void run()
				{

					// Die Resourcen in das Project kopieren
					CopyFilesAndFoldersOperation copyFileAndFolder = new CopyFilesAndFoldersOperation(shell);
					copyFileAndFolder.copyFileStores(shell, importProjectMap);

					// die Properties importieren
					Map<String, List<String>> propertyFactoryMap = getPropertyFactoriesMap(
							importDir, selectedImportProjectIDs);
					for (String factoryName : propertyFactoryMap.keySet())
						importProjectProperty(importDir, factoryName,
								propertyFactoryMap.get(factoryName));
				}
			});
		}
	}
	
	/*
	 * In einer Map werden alle zu einem NtProjekt (key = ProjectID) gehoerenden Resourcen (value = Dateien und Verzeichnisse)
	 * zusammengefasst.
	 */
	private Map<String, String[]> prepareProjectResourceMap(File sourceImportDir, EObject [] importObjects)
	{
		// zu importierende und vorhandene Projekte separieren
		Map<String, String[]>mapImportFiles = new HashMap<String, String[]>();
		
		for(EObject eObject : importObjects)
		{
			if (eObject instanceof NtProject)
			{
				String projectID = ((NtProject) eObject).getId();
				File importProjectFile = new File(sourceImportDir, projectID);
				if (importProjectFile.exists())
				{
					String [] srcFiles = importProjectFile.list(new FilenameFilter()
					{						
						@Override
						public boolean accept(File dir, String name)
						{							
							return !name.equals(".project");
						}
					});
					
					// das Projektverzeichnis wird vorangestellt
					for(int i = 0;i < srcFiles.length;i++)
						srcFiles[i] = importProjectFile.getPath()+File.separator+srcFiles[i];
					
					mapImportFiles.put(projectID, srcFiles);
				}				
			}
		}
		return mapImportFiles;
	}
	
	/*
	 * In einer Map werden alle PropertyFactoryNamen als Key und die zugehoerigen NtProjekte als ValueListe fuer die
	 * ausgewaehlten ImportProjekte zusammengefasst.
	 * 
	 */
	private Map<String,List<String>> getPropertyFactoriesMap(File importDir, final Set<String>selectedImportProjectIDs)
	{
		Map<String,List<String>>propertyFactories = new HashMap<String, List<String>>();
		
		// Filtert die fuer den Import ausgewahlten NtProjekt-Verzeichnisse
		File [] ntProjects = importDir.listFiles(new FilenameFilter()
		{						
			@Override
			public boolean accept(File dir, String name)
			{	
				if(!selectedImportProjectIDs.contains(name))
					return false;
					
				return new File(dir, name).isDirectory();				
			}
		});
		
		if(ArrayUtils.isNotEmpty(ntProjects))
		{
			for(File ntProject : ntProjects)
			{
				File projectDataDir = new File(ntProject,IProjectData.PROJECTDATA_FOLDER);
				File propertyFile = new File(projectDataDir, ProjectPropertyData.PROP_PROPERTYDATACLASS + ".xml");
				if(propertyFile.exists())
				{
					try
					{
						// die PropertyFactoryNamen aus der PropertyData-Datei lesen
						InputStream in = new FileInputStream(propertyFile);
						ProjectPropertyData projectPropertyData = JAXB.unmarshal(in, ProjectPropertyData.class);
						String [] factoryNames = projectPropertyData.getPropertyFactories();
						for(String factoryName : factoryNames)
						{
							if(!propertyFactories.containsKey(factoryName))
							{
								// Key bisher noch nicht vorhanden - Value Liste neue anlegen
								List<String> eObjectList = new ArrayList<String>();
								eObjectList.add(ntProject.getName());
								propertyFactories.put(factoryName, eObjectList);
							}
							else
							{
								List<String> eObjectList = propertyFactories.get(factoryName); 
								eObjectList.add(ntProject.getName());
							}
						}						
					} catch (FileNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
			}				
		}
		
		return propertyFactories;
	}
	
	/*
	 * Die exportierten Properties mit Hilfe der EMFProperty-Datei '.xmi' importieren.   
	 * Mit der ProjectPropertyFactory 'projectPropertyFactory' kann der jeweilige ProjectPropertyAdapter generiert werden.
	 * Mit dem ProjectPropertyAdapter koennen die fuer den Import selektierten NtProjekte('selectedImportProjectID')
	 * gefiltert und im jeweiligen Container gespeichert werden.
	 * 
	 */
	private void importProjectProperty(File importDir, String projectPropertyFactory, List<String>selectedImportProjectIDs)
	{
		INtProjectPropertyFactory factory = projektDataFactoryRepository.getFactoryByName(projectPropertyFactory);
		if(factory != null)
		{
			String factoryName = factory.getParentContainerName();
			File importEMFPropertyFile = new File(importDir,factoryName + ".xmi");
			if (importEMFPropertyFile.exists())
			{
				// alle exportierten Properties laden
				URI fileURI = URI.createFileURI(importEMFPropertyFile.getPath());
				ResourceSet resourceSet = new ResourceSetImpl();
				Resource resource = resourceSet.getResource(fileURI, true);
				EList<EObject> importObjects = resource.getContents();

				// mit dem zugeoerigen Adapter werden die selektierten Projekte gefiltert und gespeichet
				INtProjectProperty adapter =  factory.createNtProjektData();
				Object parentObject = adapter.getPropertyContainer();
								
				for(EObject importObject : importObjects)
				{					
					adapter.setNtPropertyData(importObject);
					String id = adapter.getNtProjectID();
					if(selectedImportProjectIDs.contains(id))
					{
						// nur die Objecte deren ProjektID zu den Selektierten gehoert werden importiert
						importEObject(parentObject, importObject);
						
						// pesistent speichern
						adapter.commit();		
					}
				}
			}
		}
	}
	
	/*
	 * Ein einzelnes ImportObject im Containerobjekt speichern.
	 * 
	 */
	private void importEObject(final Object parentObject, final EObject eObjectImport)
	{
		if (parentObject instanceof EObject)
		{	
			// Referenz auf diesen Object suchen
			for (final EReference ref : ((EObject) parentObject).eClass().getEAllContainments())
			{
				if (ref.getEReferenceType().isInstance(eObjectImport))
				{
					// im ParentObjekt zur Referenz hinzufuegen-/ bzw. diese ersetzen
					final EditingDomain editingDomain = AdapterFactoryEditingDomain
							.getEditingDomainFor(parentObject);
					if (ref.isMany())
					{
						editingDomain.getCommandStack().execute(
								new AddCommand(editingDomain,
										(EObject) parentObject, ref,
										EcoreUtil.copy(eObjectImport)));
					}
					else
					{
						editingDomain.getCommandStack().execute(
								new SetCommand(editingDomain,
										(EObject) parentObject, ref,
										EcoreUtil.copy(eObjectImport)));
					}
					break;
				}
			}
		}				
		else if (parentObject instanceof ECPProject) 
		{
			// Parent selbst ist ECPProject, importObjekt hinzufuegen
			final EditingDomain editingDomain = ((ECPProject) parentObject).getEditingDomain();
			editingDomain.getCommandStack().execute(new ChangeCommand(eObjectImport) 
			{
				@Override
				protected void doExecute() 
				{
					((ECPProject) parentObject).getContents().add(EcoreUtil.copy(eObjectImport));
				}
			});
		}
	}
	
	/*
	 * Alle PropertyFactoryNamen der zuimportierenden Objecte in einer Liste aufsammeln.
	 */
	private Map<String,List<EObject>> pickUpPropertyFactories(File importDir, EObject [] importObjects)
	{
		Map<String,List<EObject>>propertyFactories = new HashMap<String, List<EObject>>();
		
		for(EObject importObject : importObjects)
		{
			if (importObject instanceof NtProject)
			{
				String projectID = ((NtProject) importObject).getId();
				File importProjectFile = new File(importDir, projectID);
				if (importProjectFile.exists())
				{
					File projectDataDir = new File(importProjectFile,IProjectData.PROJECTDATA_FOLDER);
					File propertyFile = new File(projectDataDir, ProjectPropertyData.PROP_PROPERTYDATACLASS + ".xml");
					try
					{
						InputStream in = new FileInputStream(propertyFile);
						ProjectPropertyData projectPropertyData = JAXB.unmarshal(in, ProjectPropertyData.class);
						String [] factoryNames = projectPropertyData.getPropertyFactories();
						for(String factoryName : factoryNames)
						{
							if(!propertyFactories.containsKey(factoryName))
							{
								// Key bisher noch nicht vorhanden - Value Liste neue anlegen
								List<EObject> eObjectList = new ArrayList<EObject>();
								eObjectList.add(importObject);
								propertyFactories.put(factoryName, eObjectList);
							}
							else
							{
								List<EObject> eObjectList = propertyFactories.get(factoryName); 
								eObjectList.add(importObject);
							}
						}						
					} catch (FileNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}			
		}		
		
		return propertyFactories;
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
