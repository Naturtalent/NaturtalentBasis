package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.expimp.ExpImpProcessor;
import it.naturtalent.e4.project.expimp.ExportResources;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.expimp.dialogs.BackupNtProjektDialog;
import it.naturtalent.e4.project.expimp.dialogs.ExportNtProjectDialog;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.datatransfer.RefreshResourcesOperation;
import it.naturtalent.e4.project.ui.emf.ExportProjectPropertiesOperation;

/**
 * Mit dieser Action wird der Backup von Projekten ausgefuehrt.
 * 
 * @author dieter
 *
 */

public class BackupAction extends Action
{

	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;

	private Log log = LogFactory.getLog(this.getClass());

	private Shell shell;

	protected File exportDestDir;

	private Map<String, List<String>> mapProjectFactories = new HashMap<String, List<String>>();
		
	// Mapped IProjects zu den zugeordneten WorkingSets
	private Map<String, List<IProject>> mapProjectWS = new HashMap<String, List<IProject>>();

	@PostConstruct
	private void postConstruct(
			@Optional INtProjectPropertyFactoryRepository projektDataFactoryRepository,
			@Named(IServiceConstants.ACTIVE_SHELL) @Optional Shell shell)
	{
		this.projektDataFactoryRepository = projektDataFactoryRepository;
		this.shell = shell;
	}

	@Override
	public void run()
	{
		final BackupNtProjektDialog projectExportDialog = new BackupNtProjektDialog(ExpImpProcessor.shell);
		
		// Exportmodalitaeten im Dialog festlegen
		if (projectExportDialog.open() == ExportNtProjectDialog.OK)
		{	
			
			File destZipFile = new File(projectExportDialog.getResultDestDir(),"backup.zip");
			
			
			// die zum Export ausgewaehlten Resourcen (Projekte) in einer Liste zusammenfassen
			IResource[] resources = projectExportDialog.getResultExportSource();
			if (ArrayUtils.isEmpty(resources))
				return;
			
			// die Projekteigenschaften in speziellen Dateien speichern
			backupProperties(resources);
			
			// die ExportResource-Funktion instanziieren
			ExportResources exportResources = new ExportResources(shell);
			
			// tmporaeres Parentverzeichnis der Backupdaten vorbereiten
			File backupDir = createBackUpDir();

			// fuer jedes WS die zugeordneten Projekte zusammenstellen
			Map<String, List<IProject>> mappedWSProjects = mapWSProjects(resources);
			
			// WS-weise die Projekte exportieren
			for(String wsName : mappedWSProjects.keySet())
			{
				// vom WS-Namen abgeleitetes Unterverzeichnis erstellen
				String destPath = createWorkingSetSubDir(backupDir, wsName);
				
				// die Projekte des WS in 'projectResources' auflisten 
				List <IProject> iProjects = mappedWSProjects.get(wsName);
				List <IResource> projectResources = new ArrayList<IResource>();
				for(IProject iProject : iProjects)
					projectResources.add(iProject);

				// Projekte in Unterverzeichnis exportieren
				exportResources.export(shell, projectResources, destPath, false);
			}
			
			// BackupVerzeichnis zippen			
			zipFiles(backupDir.getPath(), destZipFile.getPath());

		}
	}
	
	/*
	 * Die Projekteigenschaften in speziellen Dateien im jeweiligen Projektbereich speichern.
	 * Die jeweilige Projekteigenschaft werden ueber die zugeordneten Adapter ermittelt. 
	 */
	private void backupProperties(IResource[] resources)
	{
		// alle im Adapterrepository gespeicherten Factories auflisten 
		List<INtProjectPropertyFactory> projectPropertyFactories = projektDataFactoryRepository.getAllProjektDataFactories();

		// dann die Adapter selbst erzeugen und auflisten
		List<INtProjectProperty> projectPropertyAdapters = new ArrayList<INtProjectProperty>();
		for (INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
			projectPropertyAdapters.add(propertyFactory.createNtProjektData());
		
		// die Resourcen in eine Liste ueberfuehren
		List<IResource> iResources = Arrays.asList(resources);
		
		// ExportOperation instanziieren
		ExportProjectPropertiesOperation exportPropertiesOperation = new ExportProjectPropertiesOperation(
				iResources, projectPropertyAdapters);
		
		try
		{
			// Projekteigenschaften im langlaufenden Prozess exportieren 
			new ProgressMonitorDialog(shell).run(true, false,exportPropertiesOperation);
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, Messages.ExportResources_Error,realException.getMessage());
		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, Messages.ExportResources_Cancel,e.getMessage());
			return;
		}
		
		/*
		 *  da die Eigenschaften in separaten Dateien gespeichert wurden ist ein Refresh erforderlich
		 */
		RefreshResourcesOperation refreshOperation = new RefreshResourcesOperation(iResources);
		try
		{
			new ProgressMonitorDialog(shell).run(true, false,refreshOperation);
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, Messages.ExportResources_Error,
					realException.getMessage());
		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, Messages.ExportResources_Cancel,e.getMessage());
			return;
		}

		/*
		
		// abschliessend alle zuexportierenden NtProjekte exportieren (kopieren)
		if (shell != null)
		{
			ExportResources exportResource = new ExportResources(shell);
			exportResource.export(shell, iResources,exportDestDir.getPath(),false);					
		}

		MessageDialog.openInformation(null, "Export", //$NON-NLS-1$
				"Projekte wurden exportiert in das Verzeichnis: " //$NON-NLS-1$
						+ exportDestDir);
						*/
	}

	/*
	 * Ein vom Namen des Workingsets abgeleitetes Unterverzeichnis erstellen.
	 */
	private String createWorkingSetSubDir(File backupDir, String workingSetName)
	{
		
		File wsDir = new File(backupDir, workingSetName);
		if(wsDir.exists())
		{
			try
			{
				FileUtils.deleteDirectory(wsDir);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(wsDir.mkdir())
			return wsDir.getPath();			

		return null;
	}
	
	// das Haupt-Backupverzeichnis erzeugen
	private File createBackUpDir()
	{
		// temporaeres Verzeichnis
		File tempDir = FileUtils.getTempDirectory();
		
		// BackUp-Verzeichnis in das die Projekte exportiert werden
		File backupDir = new File(FileUtils.getTempDirectoryPath(), "backUp");
		if(backupDir.exists())
		{
			try
			{
				// Backup-Verzeichnis vorher loeschen
				FileUtils.deleteDirectory(backupDir);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

		// BackUp-Verzeichnis neu erzeugen
		return (backupDir.mkdir()) ? backupDir : null; 
	}
	
	/*
	 * Die Projekte nach WorkingSets sortieren.
	 */
	private Map<String, List<IProject>> mapWSProjects(IResource[] resources)
	{
		Map<String, List<IProject>> mapProjectWS = new HashMap<String, List<IProject>>();
		
		IResourceNavigator navigator = Activator.findNavigator();
		IWorkingSet[] workingSets = navigator.getWindowWorkingSets();
		
		List<IProject>mappedProjects;
		for(IResource resource : resources)
		{
			if (resource instanceof IProject)
			{	
				IProject iProject = (IProject) resource;
				if(iProject.exists())
				{
					String workingSetName = getWorkingSetName(workingSets, iProject);
					
					if(!mapProjectWS.containsKey(workingSetName))
					{
						mappedProjects = new ArrayList<IProject>();
						mappedProjects.add(iProject);
						mapProjectWS.put(workingSetName, mappedProjects);
					}
					else
					{
						mappedProjects = mapProjectWS.get(workingSetName);
						mappedProjects.add(iProject);
					}
				}					
			}
		}
		
		return mapProjectWS;
	}

	/*
	 * Workingsetname des Projekts zurueckgeben.
	 * Ist das Projekt mehreren WS zugeordnet wird das erste zuruckgegeben.
	 */
	private String getWorkingSetName(IWorkingSet[] workingSets, IProject iProject)
	{
		if (ArrayUtils.isNotEmpty(workingSets))
		{
			for (IWorkingSet workingSet : workingSets)
			{
				IAdaptable[] adaptables = workingSet.getElements();
				if (ArrayUtils.contains(adaptables, iProject))
					return workingSet.getName();
			}
		}
		
		return null;
	}

	/*
	private String getWorkingSetNameOLD(IAdaptable adaptable)
	{
		IResourceNavigator navigator = Activator.findNavigator();
		IWorkingSet[] workingSets = navigator.getWindowWorkingSets();
		if (ArrayUtils.isNotEmpty(workingSets))
		{
			for (IWorkingSet workingSet : workingSets)
			{
				IAdaptable[] adaptables = workingSet.getElements();
				if (ArrayUtils.contains(adaptables, adaptable))
					return workingSet.getName();
			}
		}
		
		return null;
	}
	*/
	
	
	
	public void runExport()
	{
		final ExportNtProjectDialog projectExportDialog = new ExportNtProjectDialog(
				ExpImpProcessor.shell);

		// BusyIndicator - das Einlesen der vorhandenen NtProjecte kann dauern
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable()
		{
			@Override
			public void run()
			{
				// projectExportDialog.createDialogArea(Composite parent) via Busyindicator aufrufen
				projectExportDialog.create();
			}
		});

		// Exportmodalitaeten im Dialog festlegen
		if (projectExportDialog.open() == ExportNtProjectDialog.OK)
		{
			// die zum Export ausgewaehlten Resourcen in einer Liste zusammenfassen
			IResource[] resources = projectExportDialog.getResultExportSource();
			if (ArrayUtils.isEmpty(resources))
				return;

			// das ausgewaelte Zielverzeichnis (hierhin werden die Projekte exportiert)
			exportDestDir = projectExportDialog.getResultDestDir();
			if(exportDestDir == null)
				return;

			// die Resourcen in eine Liste ueberfuehren
			List<IResource> iResources = Arrays.asList(resources);

			// die Eigenschaften des Projekts werden ueber die
			// Eigenschaftsadapter ermittelt und
			// in einer fuer jeder Eigenschaft spezifische Date im
			// Projektbereich gespeichet
			// zuerst alle definierten AdapterFactories aus dem Repository laden
			List<INtProjectPropertyFactory> projectPropertyFactories = projektDataFactoryRepository
					.getAllProjektDataFactories();

			// dann die Adapter selbst erzeugen und auflisten
			List<INtProjectProperty> projectPropertyAdapters = new ArrayList<INtProjectProperty>();
			for (INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
				projectPropertyAdapters
						.add(propertyFactory.createNtProjektData());
			
			ExportProjectPropertiesOperation exportPropertiesOperation = new ExportProjectPropertiesOperation(
					iResources, projectPropertyAdapters);
			try
			{
				// Projekteigenschaften im langlaufenden Prozess exportieren 
				new ProgressMonitorDialog(shell).run(true, false,exportPropertiesOperation);
			} catch (InvocationTargetException e)
			{
				// Error
				Throwable realException = e.getTargetException();
				MessageDialog.openError(shell, Messages.ExportResources_Error,realException.getMessage());
			} catch (InterruptedException e)
			{
				// Abbruch
				MessageDialog.openError(shell, Messages.ExportResources_Cancel,e.getMessage());
				return;
			}

			/*
			 *  da die Eigenschaften in separaten Dateien gespeichert wurden ist ein Refresh erforderlich
			 */
			RefreshResourcesOperation refreshOperation = new RefreshResourcesOperation(iResources);
			try
			{
				new ProgressMonitorDialog(shell).run(true, false,refreshOperation);
			} catch (InvocationTargetException e)
			{
				// Error
				Throwable realException = e.getTargetException();
				MessageDialog.openError(shell, Messages.ExportResources_Error,
						realException.getMessage());
			} catch (InterruptedException e)
			{
				// Abbruch
				MessageDialog.openError(shell, Messages.ExportResources_Cancel,e.getMessage());
				return;
			}

			// abschliessend alle zuexportierenden NtProjekte exportieren (kopieren)
			if (shell != null)
			{
				ExportResources exportResource = new ExportResources(shell);
				exportResource.export(shell, iResources,
						exportDestDir.getPath(),
						projectExportDialog.isArchivState());
			}

			MessageDialog.openInformation(null, "Export", //$NON-NLS-1$
					"Projekte wurden exportiert in das Verzeichnis: " //$NON-NLS-1$
							+ exportDestDir);
		}
	}

	/**
     * Ein Archiv neu erstellen.
     * 
     * @param srcFolder
     * @param destZipFile
     * @return
     */
	public static boolean zipFiles(String srcFolder, String destZipFile)
	{
		boolean result = false;
		try
		{
			System.out.println("Program Start zipping the given files");
			/*
			 * send to the zip procedure
			 */
			zipFolder(srcFolder, destZipFile);
			result = true;
			System.out.println("Given files are successfully zipped");
		} catch (Exception e)
		{
			System.out.println("Some Errors happned during the zip process");
		} finally
		{
			return result;
		}
	}
    
    /*
     * zip the folders
     */
    private static void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        /*
         * create the output stream to zip file result
         */
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        /*
         * add the folder to the zip
         */
        addFolderToZip("", srcFolder, zip);
        /*
         * close the zip objects
         */
        zip.flush();
        zip.close();
    }
    
    /*
	 * add folder to the zip file
	 */
	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception
	{
		File folder = new File(srcFolder);
	
		/*
		 * check the empty folder
		 */
		if (folder.list().length == 0)
		{
			System.out.println("leeres Verzeichnis: "+folder.getName());
			addFileToZip(path, srcFolder, zip, true);
		}
		else
		{
			/*
			 * list the files in the folder
			 */
			for (String fileName : folder.list())
			{
				if (path.equals(""))
				{
					addFileToZip(folder.getName(), srcFolder + "/" + fileName,zip, false);
				}
				else
				{
					addFileToZip(path + "/" + folder.getName(),srcFolder + "/" + fileName, zip, false);
				}
			}
		}
	}
	
	/*
     * recursively add files to the zip files
     */
	private static void addFileToZip(String path, String srcFile,
			ZipOutputStream zip, boolean flag) throws Exception
	{
		/*
		 * create the file object for inputs
		 */
		File folder = new File(srcFile);

		/*
		 * if the folder is empty add empty folder to the Zip file
		 */
		if (flag == true)
		{
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
		}
		else
		{ /*
			 * if the current name is directory, recursively traverse it to get
			 * the files
			 */
			if (folder.isDirectory())
			{
				/*
				 * if folder is not empty
				 */
				addFolderToZip(path, srcFile, zip);
			}
			else
			{
				/*
				 * write the file to the output
				 */
				byte[] buf = new byte[1024];
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				while ((len = in.read(buf)) > 0)
				{
					/*
					 * Write the Result
					 */
					zip.write(buf, 0, len);
				}
			}
		}
	}
	

}
