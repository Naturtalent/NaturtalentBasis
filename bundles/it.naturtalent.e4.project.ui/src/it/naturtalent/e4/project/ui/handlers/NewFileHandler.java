package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.ui.dialogs.NewFileDialog;
import it.naturtalent.e4.project.ui.dialogs.NewFolderDialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

public class NewFileHandler extends SelectedResourcesUtils
{

	@Execute
	public void execute(Shell shell, IEclipseContext context)
	{
		NewFileDialog dialog = ContextInjectionFactory.make(NewFileDialog.class, context);
		dialog.open();
	}
	
	@CanExecute
	public boolean canExecute(MPart part)
	{		
		IProject iProject = getSelectedProject(part);
		if(iProject != null && !iProject.isOpen())
			return false;

		return resourceIsType(getSelectedResource(part), IResource.PROJECT
				| IResource.FOLDER);
	}
}
