package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.datatransfer.MailTransfer;
import it.naturtalent.e4.project.ui.datatransfer.PasteResourceAction;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;



public class PasteResourceHandler extends SelectedResourcesUtils
{
	
	private IResourceNavigator navigator;
	
	@Execute
	public void execute(Shell shell, MPart part)
	{
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			navigator = (IResourceNavigator) obj;

			PasteResourceAction pasteActon = new PasteResourceAction(shell, navigator.getClipboard());
			IStructuredSelection selection = (IStructuredSelection) navigator.getViewer().getSelection();
			pasteActon.pasteResources(selection);
			
		}
	}

	@CanExecute
	public boolean canExecute(MPart part)
	{
		if(getSelectedProject(part) == null)
			return false;
				
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			navigator = (IResourceNavigator) obj;
			Clipboard clipboard = navigator.getClipboard();
		
			if (!clipboard.isDisposed())
			{
				String[] clipboardFileContent = (String[]) clipboard.getContents(FileTransfer
						.getInstance());
				
				if(clipboardFileContent != null)
					return true;
				
				obj = MailTransfer.getInstance();
				if (obj != null)
					return (clipboard.getContents((Transfer) obj) != null);
			}
		}

		return false;
	}	

}
