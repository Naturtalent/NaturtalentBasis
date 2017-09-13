package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

public class WorkingSetConfigurationHandler
{
	@Inject 
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	
	private boolean isExecutable = true;
	
	@Execute
	public void execute(MPart part, ESelectionService selectionService)
	{
		if (part.getObject() instanceof IResourceNavigator)
		{
			IResourceNavigator navigator = (IResourceNavigator) part.getObject();
			ConfigureWorkingSetDialog dialog = new ConfigureWorkingSetDialog(shell,navigator);
			dialog.open();
		}
	}
	
	@CanExecute
	public boolean canExecute()
	{
		return isExecutable;
	}
	
	public void setExecutable(boolean isExecutable)
	{
		this.isExecutable = isExecutable;
	}

	

}