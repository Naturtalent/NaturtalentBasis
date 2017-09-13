package it.naturtalent.e4.project.ui.utils;

import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.datatransfer.ResourceTransfer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;

/**
 * Kopiert die aktuell selektierten Resourcen in die Zwischenablage
 * 
 * @author dieter
 *
 */
public class CopyResourcesAction
{
    /**
     * System clipboard
     */
    private Clipboard clipboard;
    
    /**
     * The shell in which to show any dialogs.
     */
    private Shell shell;

    
    
    public CopyResourcesAction(Shell shell,Clipboard clipboard)
	{
		super();
		this.shell = shell;
		this.clipboard = clipboard;
	}

	public void copyResources(IResource [] resources)
	{    	
		final int length = resources.length;
		int actualLength = 0;
		String[] fileNames = new String[length];
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			IPath location = resources[i].getLocation();
			if (location != null)
			{
				fileNames[actualLength++] = location.toOSString();
			}
			if (i > 0)
			{
				buf.append("\n"); //$NON-NLS-1$
			}
			buf.append(resources[i].getName());
		}

		// was one or more of the locations null?
		if (actualLength < length)
		{
			String[] tempFileNames = fileNames;
			fileNames = new String[actualLength];
			for (int i = 0; i < actualLength; i++)
			{
				fileNames[i] = tempFileNames[i];
			}
		}

		setClipboard(resources, fileNames, buf.toString());
		
        // update the enablement of the paste action
        // workaround since the clipboard does not suppot callbacks
		
		/*
        if (pasteAction != null && pasteAction.getStructuredSelection() != null) {
			pasteAction.selectionChanged(pasteAction.getStructuredSelection());
		}
		*/
	}
    
    /**
     * Set the clipboard contents. Prompt to retry if clipboard is busy.
     * 
     * @param resources the resources to copy to the clipboard
     * @param fileNames file names of the resources to copy to the clipboard
     * @param names string representation of all names
     */
	private void setClipboard(IResource[] resources, String[] fileNames,
			String names)
	{
		try
		{
			// set the clipboard contents
			if (fileNames.length > 0)
			{
				clipboard.setContents(
						new Object[]
							{ resources, fileNames, names },
						new Transfer[]
							{ ResourceTransfer.getInstance(),
									FileTransfer.getInstance(),
									TextTransfer.getInstance() });
			}
			else
			{
				clipboard.setContents(
						new Object[]
							{ resources, names },
						new Transfer[]
							{ ResourceTransfer.getInstance(),
									TextTransfer.getInstance() });
			}
		} catch (SWTError e)
		{
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD)
			{
				throw e;
			}
			if (MessageDialog.openQuestion(shell,
					Messages.CopyToClipboardProblemDialog_title,
					Messages.CopyToClipboardProblemDialog_message))
			{
				setClipboard(resources, fileNames, names);
			}
		}
	}

}
