 
package it.naturtalent.e4.project.ui.handlers.emf;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

/**
 * Gegen den Uhrzeigersinn ein Project aus dem ProjectQueue selektieren.
 *  
 * @author dieter
 *
 */
public class BackPrevProjectHandler
{
	@Execute
	public void execute()
	{
		String lastItem = Activator.projectQueue.getLast();
		Activator.projectQueue.removeLast();
		Activator.projectQueue.addFirst(lastItem);
		String projectID = Activator.projectQueue.getLast();
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