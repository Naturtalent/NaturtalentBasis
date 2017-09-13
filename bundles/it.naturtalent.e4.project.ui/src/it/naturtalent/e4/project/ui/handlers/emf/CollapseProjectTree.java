 
package it.naturtalent.e4.project.ui.handlers.emf;

import javax.inject.Named;

import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

import it.naturtalent.e4.project.IResourceNavigator;

/**
 * @author dieter
 * 
 * Collapse Projekttreeviewer
 *
 */
public class CollapseProjectTree
{
	@Execute
	public void execute(MPart part, @Named(IServiceConstants.ACTIVE_SELECTION) @Optional IResource iResource)
	{
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			IResourceNavigator navigator = (IResourceNavigator) obj;
			navigator.getViewer().collapseAll();
		}
	}

	@CanExecute
	public boolean canExecute()
	{
		return true;
	}
		
}