package it.naturtalent.e4.project.expimp;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.expimp.dialogs.ImportExistProjects;
import it.naturtalent.e4.project.expimp.dialogs.ProjectImportDialog;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.datatransfer.CopyFilesAndFoldersOperation;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.JAXB;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

public class CopyOfImportHandler implements IImportHandler
{
	
	private Map<String, File>mapImportFiles;
	private List<String>lExistFiles;

	
	@Execute
	public void execute(Shell shell)
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
			detectExistProjects(importProjects);
			
			if(!lExistFiles.isEmpty())
			{
				// Ueber vorhandene Projekte informieren
				ImportExistProjects existProjectDialog = new ImportExistProjects(shell,lExistFiles);
				existProjectDialog.open();
			}

			// Abbruch, wenn alle zu importierenden Projekte vorhanden sind
			if(mapImportFiles.isEmpty())
				return;
			
			// ProjektId u. Aliasname mappen
			Map<String,String>mapProjectNames = getImportedAliasNames(importProjects);
			
			// die zuimportierenden Projekte erzeugen
			if((selectedWorkingSets != null) && (!selectedWorkingSets.isEmpty()))
				WorkbenchContentProvider.newAssignedWorkingSets = selectedWorkingSets.toArray(new IWorkingSet[selectedWorkingSets.size()]);					
			CreateNewProject.createProject(shell,mapProjectNames);
			WorkbenchContentProvider.newAssignedWorkingSets = null;

			// Die Projektdaten in die neuerzeugten Projekte importieren
			HashMap<String,String> newlyCreatedProjects = Activator.newlyCreatedProjectMap;
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

					CopyFilesAndFoldersOperation copyFileAndFolder = new CopyFilesAndFoldersOperation(shell);
					copyFileAndFolder.copyFiles(srcFiles, iProject);
				}
			}
		}
	}

	private void detectExistProjects(File [] importFiles)
	{		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot(); 
		
		for(File file : importFiles)
		{
			String projectId = file.getName();
			IProject iProject = workspaceRoot.getProject(projectId);
			if(iProject.exists())		
				lExistFiles.add(new NtProject(iProject).getName());		
			else
				mapImportFiles.put(projectId,file);			
		}
	}

	
	private Map<String,String>getImportedAliasNames(File [] importFiles)
	{
		Map<String, String> mapImportProjectNames = new HashMap<String, String>();

		for (File impFile : importFiles)
		{
			StringBuilder pathBuilder = new StringBuilder(impFile.getPath());
			pathBuilder.append(File.separator + IProjectData.PROJECTDATA_FOLDER);
			pathBuilder.append(File.separator + IProjectData.PROJECTDATAFILE);
			File projectDataFile = new File(pathBuilder.toString());

			try
			{
				FileInputStream in = new FileInputStream(projectDataFile);
				ProjectData projectDatas = JAXB
						.unmarshal(in, ProjectData.class);
				mapImportProjectNames.put(impFile.getName(),
						projectDatas.getName());

			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return mapImportProjectNames;
	}	
	

	@CanExecute
	public boolean canExecute()
	{
		// TODO Your code goes here
		return true;
	}



}