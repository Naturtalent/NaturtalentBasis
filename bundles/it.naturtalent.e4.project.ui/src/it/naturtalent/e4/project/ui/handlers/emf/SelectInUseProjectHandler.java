 
package it.naturtalent.e4.project.ui.handlers.emf;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Display;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.emf.ProjectQueueDialog;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

/**
 * Handler steuert den ProjectQueueDialog.
 * @see it.naturtalent.e4.project.ui.dialogs.emf.ProjectQueueDialog
 * @see it.naturtalent.e4.project.ui.utils.ProjectQueue
 * 
 * @author dieter
 *
 */
public class SelectInUseProjectHandler
{
	@Execute
	public void execute(@Optional EPartService partService)
	{
		ProjectQueueDialog dialog = new ProjectQueueDialog(Display.getDefault().getActiveShell());
		if(dialog.open() == ProjectQueueDialog.OK)
		{
			Object [] selections = dialog.getSelections();
			for(Object selection : selections)
			{
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject((String) selection);
				ResourceNavigator resourceNavigator = (ResourceNavigator) Activator.findNavigator();
				resourceNavigator.setSelection(iProject);	
				
				// Focus auf den Navigator
				MPart part = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);			
				partService.activate(part);		
			}
			
			/*
			if(StringUtils.isNotEmpty(projectID))
			{
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectID);
				ResourceNavigator resourceNavigator = (ResourceNavigator) Activator.findNavigator();
				resourceNavigator.setSelection(iProject);
			}
			*/
		}
	}

	@CanExecute
	public boolean canExecute()
	{
		return Activator.projectQueue.size() > 0;		
	}

}