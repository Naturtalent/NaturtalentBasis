 
package it.naturtalent.e4.project.ui.handlers.emf;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.common.util.EList;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.ui.Activator;

/**
 * Prueft, ob zu jedem IProjectID ein PropertyID existiert
 * 
 * @author dieter
 *
 */
public class CheckPropertyToProject
{	
	@Execute
	public void execute()
	{
		IProject[] iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ProjectData projectData;
		String name = null;		
		int counter = 0;
		for (IProject iProject : iProjects)
		{
			String id = iProject.getName();
			try
			{
				name = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			NtProject ntProject = Activator.findNtProject(id);			
			if(ntProject == null)
			{
				System.out.println(id+" : "+name+" : "+iProject.getName());
				counter++;
			}
		}
		
		System.out.println(iProjects.length+" : fehlende Properties: "+counter);
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