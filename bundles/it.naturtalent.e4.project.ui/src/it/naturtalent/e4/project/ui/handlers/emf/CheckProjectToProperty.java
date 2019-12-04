 
package it.naturtalent.e4.project.ui.handlers.emf;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.common.util.EList;

import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.ui.Activator;

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