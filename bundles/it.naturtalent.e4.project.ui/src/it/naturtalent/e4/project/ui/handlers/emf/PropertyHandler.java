 
package it.naturtalent.e4.project.ui.handlers.emf;

import javax.inject.Inject;

import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.ui.dialogs.ResourcePropertyDialog;
import it.naturtalent.e4.project.ui.handlers.SelectedResourcesUtils;

public class PropertyHandler extends SelectedResourcesUtils
{
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	@Execute
	public void execute(MPart part, Shell shell, IEclipseContext context)
	{	
		IResource resource = getSelectedResource(part);
		
		ResourcePropertyDialog proertyDialog = new ResourcePropertyDialog(shell);
		proertyDialog.create();
		proertyDialog.setResource(resource);
		proertyDialog.open();
	}

	@CanExecute
	public boolean canExecute(MPart part)
	{
		return resourceIsType(getSelectedResource(part), IResource.PROJECT
				| IResource.FOLDER | IResource.FILE);
	}
	
		
}