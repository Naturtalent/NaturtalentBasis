 
package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.utils.CloseProjects;
import it.naturtalent.e4.project.ui.utils.DeleteResources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;

public class CloseHandler extends SelectedResourcesUtils
{
	@Execute
	public void execute(MPart part, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
		IResource[] selectedResources = getSelectedResources(part);
		if(ArrayUtils.isNotEmpty(selectedResources))	
		{
			boolean projSelected = selectionIsOfType(IResource.PROJECT);
			if (projSelected)
			{
				List<IProject> iProjects = new ArrayList<IProject>();
				for (IResource iResource : selectedResources)
				{					
					if (iResource instanceof IProject)
					{
						// aus WorkingSets entfernen
						IProject iProject = (IProject) iResource;
						iProjects.add(iProject);
						DeleteResources.removeFromWorkingSets(iProject);
					}
				}
				
				if(!iProjects.isEmpty())
				{
					// Projekte schliessen
					CloseProjects closeProject = new CloseProjects();
					closeProject.close(shell,
							iProjects.toArray(new IProject[iProjects.size()]));

					// Navigator aktualisieren
					Object obj = part.getObject();
					if (obj instanceof IResourceNavigator)
					{
						ResourceNavigator resourceNavigator = (ResourceNavigator) obj;
						resourceNavigator.refreshViewer();
					}
				}
			}
		}
	}

	@CanExecute
	public boolean canExecute(MPart part)
	{
		IResource[] selectedResources = getSelectedResources(part);
		if (ArrayUtils.isNotEmpty(selectedResources))
		{
			boolean projSelected = selectionIsOfType(IResource.PROJECT);
			if(projSelected)
			{
				for(IResource iResource : selectedResources)
				{
					if(iResource instanceof IProject)
					{
						if((((IProject)iResource).isOpen()))
							return true;
					}
				}
			}
		}

		return false;
	}
	
}