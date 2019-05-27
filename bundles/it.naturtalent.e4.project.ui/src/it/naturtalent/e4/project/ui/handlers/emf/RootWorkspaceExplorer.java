 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import it.naturtalent.e4.project.ui.Activator;

public class RootWorkspaceExplorer
{
	@Execute
	public void execute()
	{
		String workspacePath = null;
		
		//String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();		
		//Log log = LogFactory.getLog(this.getClass());
		//log.info("WorkspacePath: "+workspacePath);
		
		IProject[]iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if(ArrayUtils.isNotEmpty(iProjects))
		{
			for(IProject iProject : iProjects)
			{
				if(iProject.exists())
				{
					workspacePath = iProject.getParent().getLocation().toOSString();					
					break;
				}				
			}
		}
		
		if(StringUtils.isNotEmpty(workspacePath))
		{
			try
			{
				if (SystemUtils.IS_OS_LINUX)
					Runtime.getRuntime().exec("nautilus " + workspacePath);
				else
					Runtime.getRuntime().exec("explorer " + workspacePath);

			} catch (Exception exp)
			{
				if (SystemUtils.IS_OS_LINUX)
					try
					{
						Runtime.getRuntime().exec("nemo " + workspacePath);
						return;
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				exp.printStackTrace();
			}
		}
	}
	
	@CanExecute
	public boolean canExecute()
	{
		String workspacePath = null;
		IProject[]iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if(ArrayUtils.isNotEmpty(iProjects))
		{
			for(IProject iProject : iProjects)
			{
				if(iProject.exists())
				{
					workspacePath = iProject.getParent().getLocation().toOSString();					
					break;
				}				
			}
		}
		return (StringUtils.isNotEmpty(workspacePath));
	}
		
		
}