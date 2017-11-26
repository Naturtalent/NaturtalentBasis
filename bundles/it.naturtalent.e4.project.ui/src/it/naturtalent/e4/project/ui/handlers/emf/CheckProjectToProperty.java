 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXB;

import org.apache.commons.lang3.StringUtils;
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
 * Prueft, ob zu jedem PropertyID ein IProject existiert
 * 
 * @author dieter
 *
 */
public class CheckProjectToProperty
{	
	@Execute
	public void execute()
	{
		int failCounter = 0;
		NtProjects ntProjects = Activator.getNtProjects();
		EList<NtProject>projects = ntProjects.getNtProject();
		for(NtProject project : projects)
		{
			String id = project.getId();
			String name = project.getName();
			
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(id);	
			if(!iProject.exists())
			{
				System.out.println(id+" : "+name+" : "+iProject.getName());
				failCounter++;
			}
		}
		
		System.out.println(projects.size()+" : fehlende Projekte: "+failCounter);
	}
	
	@CanExecute
	public boolean canExecute()
	{
		NtProjects ntProjects = Activator.getNtProjects();
		if(ntProjects == null)
			return false;
		
		EList<NtProject>projects = ntProjects.getNtProject();
		IProject[] iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		return ((projects.size() > 0) && (iProjects.length > 0));
	}
}