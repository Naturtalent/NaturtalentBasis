package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.ui.dialogs.NewFolderDialog;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Shell;



public class NewFolderHandler
{
	//IContainer selectedContainer;	
	
	@Execute
	public void execute(IEclipseContext context)
	{

		NewFolderDialog dialog = ContextInjectionFactory.make(NewFolderDialog.class, context);
		dialog.open();

		/*
		if(selectedContainer != null)
		{
			NewFolderDialog dialog = ContextInjectionFactory.make(NewFolderDialog.class, context);
			dialog.open();
		}
		*/
	}

	@CanExecute
	public boolean canExecute(ESelectionService selectionService)
	{
		
		if(selectionService != null)
		{
			Object obj = selectionService.getSelection();
			if(obj instanceof IResource)
			{
				IResource iResource = (IResource) obj;
				
				if(iResource.getType() == IProject.PROJECT)
					if(!((IProject)iResource).isOpen())
						return false;
				
				return (iResource != null && (iResource.getType() & (IResource.FOLDER) | (IResource.PROJECT)) != 0);
			}				
		}

		
		/*
		selectedContainer = null;
		
		Object obj = selectionService.getSelection();
		if (obj instanceof IProject)
		{
			if(!((IProject)obj).isOpen())
				return false;
							
			// das selektierte Projekt ist Ziel-Container
			selectedContainer = (IProject)obj;			
			return true;
		}
		else
		{
			if (obj instanceof IResource)
			{				
				IResource iResource = (IResource) obj;
				if(iResource != null && (iResource.getType() & (IResource.FOLDER)) != 0)
				{
					selectedContainer = (IContainer) iResource;
					return true;
				}
			}
		}
		*/
		
		return false;
	}
	
	
}
