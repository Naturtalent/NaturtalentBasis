package it.naturtalent.e4.project.expimp;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.IOverwriteQuery;

import it.naturtalent.e4.project.ui.datatransfer.FileSystemExportOperation;
import it.naturtalent.e4.project.ui.utils.DeleteResources;

public class ExportResources implements IOverwriteQuery
{
	private Shell shell;
	
		
	public ExportResources(Shell shell)
	{
		super();
		this.shell = shell;
	}

	/**
	 * Exportiert die Resourcen 'rsources' in das Zielverzeichnis 'destDir'.
	 * Ist das Archivflag gesetzt, werden die Resourcen anschliessend im Workspace gelöscht.
	 * 
	 * @param shell
	 * @param resources
	 * @param destDir
	 * @param archiv
	 */
	public void export(Shell shell, List <IResource> resources, String destDir, boolean archiv)
	{		
		// Resource exportieren
		FileSystemExportOperation op = new FileSystemExportOperation(null,
				resources, destDir, this);
		
		try
		{
			// Export starten
			new ProgressMonitorDialog(shell).run(true, false, op);
			
			if(archiv)
			{
				// nach dem Exportieren loeschen
				IResource [] resourceArray = resources.toArray(new IResource [resources.size()]);
				DeleteResources.deleteResources(shell, resourceArray);				
			}
			
			
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, Messages.ExportResources_Error, realException.getMessage());
		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, Messages.ExportResources_Cancel, e.getMessage());
		}
		
	}


	@Override
	public String queryOverwrite(String pathString)
	{
		Path path = new Path(pathString);

		String messageString;
		// Break the message up if there is a file name and a directory
		// and there are at least 2 segments.
		if (path.getFileExtension() == null || path.segmentCount() < 2)
		{
			messageString = NLS.bind(
					Messages.WizardDataTransfer_existsQuestion,
					pathString);
		}
		else
		{
			messageString = NLS
					.bind(Messages.WizardDataTransfer_overwriteNameAndPathQuestion,
							path.lastSegment(), path.removeLastSegments(1)
									.toOSString());
		}

		final MessageDialog dialog = new MessageDialog(shell, Messages.Question, null, messageString,
				MessageDialog.QUESTION, new String[]
					{ IDialogConstants.YES_LABEL,
							IDialogConstants.YES_TO_ALL_LABEL,
							IDialogConstants.NO_LABEL,
							IDialogConstants.NO_TO_ALL_LABEL,
							IDialogConstants.CANCEL_LABEL }, 0)
		{
			protected int getShellStyle()
			{
				return super.getShellStyle() | SWT.SHEET;
			}
		};
		String[] response = new String[]
			{ YES, ALL, NO, NO_ALL, CANCEL };
		// run in syncExec because callback is from an operation,
		// which is probably not running in the UI thread.
		shell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				dialog.open();
			}
		});
		return dialog.getReturnCode() < 0 ? CANCEL : response[dialog
				.getReturnCode()];

	}

}
