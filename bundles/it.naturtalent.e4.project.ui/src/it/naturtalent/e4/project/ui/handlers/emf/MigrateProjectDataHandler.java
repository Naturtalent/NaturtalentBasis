 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXB;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.model.project.ProjectFactory;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;

/**
 * Hilfsklasse dient der einmaligen Umstellung von ProjectData auf PropertyData
 * 
 * @author dieter
 *
 */
public class MigrateProjectDataHandler
{	
	
	private final static String PROJECTPROPERTYFILE = "propertyData.xml";
	private final static String PROJECTDATAFILE = "projectData.xml";
	
	@Execute
	public void execute()
	{
		doMigrate();
	}
	
	@CanExecute
	public boolean canExecute()
	{
		NtProjects ntProjects = Activator.getNtProjects();
		EList<NtProject>projects = ntProjects.getNtProject();
		return (projects.size() == 0);
	}
	
	
	private void doMigrate()
	{		
		// Funktion gesperrt, wenn bereits Propertys existieren
		NtProjects ntProjects = Activator.getNtProjects();
		EList<NtProject>projects = ntProjects.getNtProject();
		if(projects.size() > 0)
		{
			System.out.println("Migration gesperrt - es exisieren bereits Properties");
			return;
		}
		
		System.out.println("Migration");
		
		IProject[] iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ProjectData projectData;
		NtProject ntProject;
		String id;
		String name;
		String desc;
				
		for (IProject iProject : iProjects)
		{
			// Property (alt ProjektData) aus der Datendatei lesen
			Object obj = getProjectPropertyData(iProject);
			if (obj instanceof ProjectData)
			{
				projectData = (ProjectData) obj;
				id = iProject.getName();
				desc = projectData.getDescription();
				try
				{
					name = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
				} catch (CoreException e)
				{
					name = projectData.getName();
				}
				
				// ProjektProperty erstellen
				ntProject = ProjectFactory.eINSTANCE.createNtProject();
				ntProject.setId(id);
				ntProject.setName(name);
				ntProject.setDescription(desc);
				Activator.getNtProjects().getNtProject().add(ntProject);
				
				// Property in Datei speichern
				storeProjectProperty(iProject);

			}
		}
		
		// gesamte ECP-Projekt wird gespeichert
		ECPHandlerHelper.saveProject(Activator.getECPProject());	
	}
	
	private void storeProjectProperty(IProject iProject)
	{
		if ((iProject != null) && (iProject.isOpen()))
		{
			IFolder folder = iProject.getFolder(IProjectData.PROJECTDATA_FOLDER);
			if (folder.exists())
			{
				String name = ProjectPropertyData.PROP_PROPERTYDATACLASS + ".xml";
				IFile iFile = folder.getFile(name);
				
				String [] settingPropertyFactoryNames = new String[]{NtProjectPropertyFactory.class.getName()};
				ProjectPropertyData projectPropertyData = new ProjectPropertyData();
				projectPropertyData.setPropertyFactories(settingPropertyFactoryNames);							
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXB.marshal(projectPropertyData, out);
				ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			
				try
				{
					if (iFile.exists())
						iFile.setContents(in, IFile.FORCE, null);
					else
						iFile.create(in, IFile.FORCE, null);
					
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private File projectPropertyFile(IProject iProject)
	{
		ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
		String projectDataDirPath = projectPropertySettings.getProjectDataPath(iProject);			
		if (projectDataDirPath != null)
		{
			File projectDataDir = new File(projectDataDirPath);
			File propertyFile = new File(projectDataDir,PROJECTPROPERTYFILE);
			return propertyFile;
		}
		return null;
	}
	
	/**
	 * Rueckgabe der fuer dieses Projekt gespeicherten ProjektDaten
	 * 
	 * @param iProject
	 * @return
	 */
	protected Object getProjectPropertyData(IProject iProject)
	{
		InputStream in = getProjectDataInputStream(iProject);
		if (in != null)
			return (ProjectData) JAXB.unmarshal(in, ProjectData.class);
		
		return null;
	}
	
	private InputStream getProjectDataInputStream(IProject iProject)
	{		
		if ((iProject != null) && (iProject.isOpen()))
		{
			IFolder folder = iProject.getFolder(IProjectData.PROJECTDATA_FOLDER);
			if (folder.exists())
			{
				IFile iFile = folder.getFile(PROJECTDATAFILE);
				if (iFile.exists())
					try
					{						
						return iFile.getContents(true);
					} catch (CoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}

		return null;
	}
	
	private void deletePropertyFile()
	{
		ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
		IProject[] iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : iProjects)
		{
			String projectDataDirPath = projectPropertySettings.getProjectDataPath(iProject);			
			if (projectDataDirPath != null)
			{
				File projectDataDir = new File(projectDataDirPath);
				File propertyFile = new File(projectDataDir,PROJECTPROPERTYFILE);
				propertyFile.delete();
			}
		}
		
		System.out.println("erledigt");
	}
	



}