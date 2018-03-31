 
package it.naturtalent.e4.project.ui.handlers.emf;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.emf.ProjectQueueDialog;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

public class SelectInUseProjectHandler
{
	@Execute
	public void execute()
	{
		ProjectQueueDialog dialog = new ProjectQueueDialog(Display.getDefault().getActiveShell());
		if(dialog.open() == ProjectQueueDialog.OK)
		{
			String projectID = dialog.getSelectedProjectID();
			if(StringUtils.isNotEmpty(projectID))
			{
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectID);
				ResourceNavigator resourceNavigator = (ResourceNavigator) Activator.findNavigator();
				resourceNavigator.setSelection(iProject);
			}
		}
	}

	@CanExecute
	public boolean canExecute()
	{
		return Activator.projectQueue.size() > 0;		
	}

}