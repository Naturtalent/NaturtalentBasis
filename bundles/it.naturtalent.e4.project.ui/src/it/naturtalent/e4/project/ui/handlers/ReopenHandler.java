 
package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.utils.OpenProjects;

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
import org.eclipse.swt.widgets.Shell;

public class ReopenHandler extends SelectedResourcesUtils
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
				// Liste mit den selektierten Projekten
				List<IProject> iProjects = new ArrayList<IProject>();
				for (IResource iResource : selectedResources)
				{					
					if (iResource instanceof IProject)
					{
						IProject iProject = (IProject) iResource;
						iProjects.add(iProject);	
					}
				}
				
				if(!iProjects.isEmpty())
				{
					// die Projekte wiedereroeffnen
					OpenProjects openProject = new OpenProjects();
					IProject[]projects = iProjects.toArray(new IProject[iProjects.size()]);
					openProject.open(shell,projects);
							
					// Navigator aktualisieren
					Object obj = part.getObject();
					if (obj instanceof IResourceNavigator)
					{
						ResourceNavigator navigator = (ResourceNavigator) obj;
						navigator.updateAfterReOpen(projects);
						navigator.refreshViewer();						
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
							return false;
					}
				}
			}
			return true;
		}

		return false;
	}
		
}