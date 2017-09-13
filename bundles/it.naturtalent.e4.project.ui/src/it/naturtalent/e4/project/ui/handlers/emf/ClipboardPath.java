
package it.naturtalent.e4.project.ui.handlers.emf;

import javax.inject.Named;

import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import it.naturtalent.e4.project.IResourceNavigator;

/**
 * @author dieter
 * 
 * Uebertraegt den Pfad der selektierten Resource in die Zwischenablage. 
 *
 */
public class ClipboardPath
{
	private IResourceNavigator navigator;

	@Execute
	public void execute(MPart part,
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IResource iResource)
	{
		if (((iResource != null
				&& (iResource.getType() & (IResource.FILE)) == 0)))
		{
			Object obj = part.getObject();
			if (obj instanceof IResourceNavigator)
			{
				navigator = (IResourceNavigator) obj;
				Clipboard clipboard = navigator.getClipboard();

				clipboard.setContents(new Object[]
					{ iResource.getLocation().toOSString() }, new Transfer[]
					{ TextTransfer.getInstance() });
			}
		}
	}

	@CanExecute
	public boolean canExecute(
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IResource iResource)
	{
		return ((iResource != null
				&& (iResource.getType() & (IResource.FILE)) == 0));
	}

}