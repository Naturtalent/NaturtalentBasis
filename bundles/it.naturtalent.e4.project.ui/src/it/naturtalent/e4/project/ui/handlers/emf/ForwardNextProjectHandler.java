 
package it.naturtalent.e4.project.ui.handlers.emf;

import org.eclipse.e4.core.di.annotations.Execute;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;

/**
 * Im den Uhrzeigersinn ein Project aus dem ProjectQueue selektieren.
 * 
 * @author dieter
 *
 */
public class ForwardNextProjectHandler
{
	@Execute
	public void execute()
	{
		String projectID = Activator.projectQueue.getFirst();
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectID);
		ResourceNavigator resourceNavigator = (ResourceNavigator) Activator.findNavigator();
		resourceNavigator.setSelection(iProject);
	}

	@CanExecute
	public boolean canExecute()
	{
		return Activator.projectQueue.size() > 0;		
	}
		
}