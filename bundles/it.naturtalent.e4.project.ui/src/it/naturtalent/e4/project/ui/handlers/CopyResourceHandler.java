package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.utils.CopyResourcesAction;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;



public class CopyResourceHandler extends SelectedResourcesUtils
{
	
	private IResourceNavigator navigator;
	
	@Execute
	public void execute(Shell shell, MPart part)
	{
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			navigator = (IResourceNavigator) obj;

			IResource[] selectedResources = getSelectedResources(part);
			if (ArrayUtils.isNotEmpty(selectedResources))
			{
				CopyResourcesAction copyResources = new CopyResourcesAction(
						shell, navigator.getClipboard());
				copyResources.copyResources(selectedResources);
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
						if(!(((IProject)iResource).isOpen()))
							return false;
					}
				}
			}

			boolean fileFoldersSelected = selectionIsOfType(IResource.FILE
					| IResource.FOLDER);
			if (!projSelected && !fileFoldersSelected)
				return false;
			
			return true;
		}

		return false;
	}	

}
