 
package it.naturtalent.e4.project.ui.handlers.emf;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;

/**
 * Im den Uhrzeigersinn ein Projekt aus dem ProjectQueue selektieren.
 * 
 * @author dieter
 *
 */
public class ForwardNextProjectHandler
{
	@Execute
	public void execute(@Optional EPartService partService)
	{
		String projectID = Activator.projectQueue.getFirst();
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectID);
		ResourceNavigator resourceNavigator = (ResourceNavigator) Activator.findNavigator();
		resourceNavigator.setSelection(iProject);
		
		// Focus auf den Navigator
		MPart part = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);			
		partService.activate(part);		
	}

	@CanExecute
	public boolean canExecute()
	{
		return Activator.projectQueue.size() > 0;		
	}
		
}