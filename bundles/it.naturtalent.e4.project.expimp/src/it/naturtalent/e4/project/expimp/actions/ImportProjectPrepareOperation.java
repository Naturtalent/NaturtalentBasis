package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXB;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;

/**
 * Das Importieren vorbereiten (erzeugt die zu importierenden NtProjekte und erstellt Hilfsmaps fuer das Importieren
 * der Resourcen und Eigenschaften).
 * 
 * Die zu importierenden NtProjekte im aktuellen Workspace erzeugen.
 * 
 * Eine Map erzeugen in der zu jedem NtProjekt die zu importierenden Resourcen aufgelisteten sind.
 * Wird benoetigt um die Resourcen (Verzeichnisse und Dateien) zu importieren
 * 
 * Eine Map erzeugen in der zu jedem ProjectPropertyFactorynamen die zuegordneten NtProjekte aufgelistet sind.
 * Wird benoetigt um die Properties der NrProjekte zu importieren  
 * 
 * @author dieter
 *
 */
public class ImportProjectPrepareOperation implements IRunnableWithProgress
{
	private final static String PREPAREPROPERTY_OPERATION_TITLE = "Import vorbereiten";
	
	private int totalWork = IProgressMonitor.UNKNOWN;
	
	private Shell shell;
	
	private File importDir;
	
	// alle zu importierenden NtProjekte
	private EObject [] importObjects;
	
	// es soll in WorkingSets importiert werden
	private List<IWorkingSet> selectedWorkingSets;
	
	private IProgressMonitor monitor;
	
	// Map indem jedem IProject (key) eine Liste (value) mit den Pfadangaben der zugehoerigen zu importierenden Resourcen
	private Map<IProject,String[]>importProjectMap = new HashMap<IProject, String[]>();
	
	// Map mit den PropertyFactorynamen und zugehoerigen IProjekten
	private Map<String, List<String>> propertyFactoryMap;
	
	
	
	public ImportProjectPrepareOperation(Shell shell, File importDir, EObject[] importObjects, List<IWorkingSet> selectedWorkingSets)
	{
		super();
		this.shell = shell;
		this.importDir = importDir;
		this.importObjects = importObjects;
		this.selectedWorkingSets = selectedWorkingSets;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		if (ArrayUtils.isNotEmpty(importObjects))
		{
			this.monitor = monitor;
			totalWork = importObjects.length * 2;
			monitor.beginTask(PREPAREPROPERTY_OPERATION_TITLE, totalWork);

			// Pro NtProjekt (key = ProjektID) die zu importierenden Resourcen (value = List<Path der Resourcen>) 
			Map<String, String[]> mapImportFiles = prepareProjectResourceMap(importDir, importObjects);

			//Mapped ProjektId u. Name der selektierten ImportProjekte
			final Map<String,String>createProjectMap = new HashMap<String, String>();
			for(EObject eObject : importObjects)
			{
				if (eObject instanceof NtProject)
				{
					NtProject ntProject = (NtProject) eObject;
					String projectID = ntProject.getId();
					String projectName = ntProject.getName();
					createProjectMap.put(projectID, projectName);
					monitor.worked(1);
				}
			}
			final Set<String>selectedImportProjectIDs = createProjectMap.keySet();

			// die zuimportierenden NtProjekte im Workspace erzeugen	
			
			shell.getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					if((selectedWorkingSets != null) && (!selectedWorkingSets.isEmpty()))
						WorkbenchContentProvider.newAssignedWorkingSets = selectedWorkingSets.toArray(new IWorkingSet[selectedWorkingSets.size()]);					
					CreateNewProject.createProject(shell, createProjectMap);
					WorkbenchContentProvider.newAssignedWorkingSets = null;
				}
			});
			
			

			// IProject und zugehoerige Resourcen in einer Map zusammenfassen
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();			
			for(EObject eObject : importObjects)
			{
				NtProject ntProject = (NtProject) eObject;
				IProject iProject = workspaceRoot.getProject(ntProject.getId());
				String [] importResources = mapImportFiles.get(ntProject.getId());
				importProjectMap.put(iProject, importResources);
			}

			// generiert eine Map mit FactoryNamen (key) und den zugeoerigen IProjekten (value) als Liste
			propertyFactoryMap = getPropertyFactoriesMap(importDir, createProjectMap.keySet());
			
			monitor.done();
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
			monitor.worked(1);
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


	/**
	 * Rueckgabe einer Map indem jedem IProject (key) eine Liste (value) mit den Pfadangaben der zugehoerigen
	 * Resourcen gespeichert ist.
	 * 
	 * @return
	 */
	public Map<IProject, String[]> getImportProjectMap()
	{
		return importProjectMap;
	}

	/**
	 * Rueckgabe einer Map mit PropertyFactorynamen und zugehoerigen IProjekten
	 *  
	 * @return
	 */
	public Map<String, List<String>> getPropertyFactoryMap()
	{
		return propertyFactoryMap;
	}
	
	
	



}
