package it.naturtalent.e4.project.ui.actions;

import it.naturtalent.e4.project.ui.dialogs.NewFolderDialog;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

public class NewFolderAction extends Action
{

	@Inject @Optional private Shell shell;
	@Inject @Optional private IEclipseContext context;	
	@Inject @Optional private ESelectionService selectionService;
	
	private static IResource iResource = null;
	
	@Override
	public void run()
	{
		NewFolderDialog dialog =  ContextInjectionFactory.make(NewFolderDialog.class, context);
		dialog.open();
	}

	/* 
	 * Action wird freigegeben, wenn die selektierte Resource ein Projekt (offenes Projekt) oder selbst ein Folder ist.
	 * 
	 * @see org.eclipse.jface.action.Action#isHandled()
	 */
	@Override
	public boolean isHandled()
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
			return false;	
		}
		
		return super.isHandled();		
	}
}
