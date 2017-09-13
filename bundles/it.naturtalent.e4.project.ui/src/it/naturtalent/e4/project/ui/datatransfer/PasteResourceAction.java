package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.model.IMailData;
import it.naturtalent.e4.project.ui.handlers.SelectedResourcesUtils;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;

public class PasteResourceAction extends SelectedResourcesUtils
{

    /**
     * The shell in which to show any dialogs.
     */
    private Shell shell;

    /**
     * System clipboard
     */
    private Clipboard clipboard;
    
    private Log log = LogFactory.getLog(this.getClass());    

	public PasteResourceAction(Shell shell, Clipboard clipboard)
	{
		super();
		this.shell = shell;
		this.clipboard = clipboard;
	}

	public void pasteResources(IStructuredSelection selection)
	{
		// try a resource transfer
		ResourceTransfer resTransfer = ResourceTransfer.getInstance();
		IResource[] resourceData = (IResource[]) clipboard
				.getContents(resTransfer);

		if (resourceData != null && resourceData.length > 0)
		{
			if (resourceData[0].getType() == IResource.PROJECT)
			{
				/*
				
				// enablement checks for all projects
				for (int i = 0; i < resourceData.length; i++)
				{
					CopyProjectOperation operation = new CopyProjectOperation(
							this.shell);
					operation.copyProject((IProject) resourceData[i]);
				}
				
				*/
				
			}
			else
			{
				// enablement should ensure that we always have access to a
				// container
				IContainer container = getContainer(selection);
				
				CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(
						this.shell);
				operation.copyResources(resourceData, container);
			}
			return;
		}

		// try a file transfer
		FileTransfer fileTransfer = FileTransfer.getInstance();
		String[] fileData = (String[]) clipboard.getContents(fileTransfer);

		if (fileData != null)
		{
			// enablement should ensure that we always have access to a
			// container
			IContainer container = getContainer(selection);

			CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(
					this.shell);
			operation.copyFiles(fileData, container);
			
			return;
		}
		
		// try MS Outlook Mail
		Transfer msMail = MailTransfer.getInstance();
		if(msMail != null)
		{			
			
			log.info("get clipboard content");
			
			Object mailData = clipboard.getContents(msMail);
			
			log.info("get clipboard content success. MailData:  "+mailData.toString());
			
			
			if (mailData instanceof IMailData)				
			{
				fileData = ((IMailData)mailData).getMailFiles();				
				if(fileData != null)
				{
					IContainer container = getContainer(selection);
					CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(
							this.shell);
					operation.copyFiles(fileData, container);
				}
			}
		}
	}
	
	/**
	 * Returns the container to hold the pasted resources.
	 */
	private IContainer getContainer(IStructuredSelection selection)
	{
		IResource[] resources = getSelectedResources(selection);
		if (resources[0] instanceof IFile)
		{
			return ((IFile) resources[0]).getParent();
		}
		else
		{
			return (IContainer) resources[0];
		}
	}
    
    
}
