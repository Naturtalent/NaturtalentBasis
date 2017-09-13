package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.utils.WorkspaceModifyOperation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

public class RenameResources
{

	/**
	 * Eine Ressource umbennen.
	 * 
	 * @param shell
	 * @param container
	 * @param folderName
	 */
	public static void rename(Shell shell, final IResource resource,
			final String newName)
	{
		Log log = LogFactory.getLog(RenameResources.class);
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask(
							Messages.RenameResourceAction_inputDialogTitle, 1);
					if (monitor.isCanceled())
					{
						throw new OperationCanceledException();
					}

					List<Object> resourcesAtDestination = new ArrayList<Object>();
					boolean pathIncludesName = true;
					List<String> reverseDestinations = new ArrayList<String>();

					IPath newPath = resource.getFullPath()
							.removeLastSegments(1).append(newName);

					MoveResourcesOperation.move((new IResource[]
						{ resource }), newPath, resourcesAtDestination,
							reverseDestinations, monitor, null,
							pathIncludesName);

				} finally
				{
					monitor.done();
				}
			}
		};
		try
		{
			// im Progressmonitor ausfuehren
			new ProgressMonitorDialog(shell).run(true, false, operation);

		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog
					.openError(shell,
							Messages.RenameResourceAction_interruptInfo,
							e.getMessage());
		} catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof CoreException)
			{
				log.error(e.getTargetException());
				ErrorDialog.openError(shell,
						Messages.RenameResourceAction_problemTitle, null, // no
																			// special
																			// message
						((CoreException) e.getTargetException()).getStatus());
			}
			else
			{
				// CoreExceptions are handled above, but unexpected runtime
				log.error(e.getTargetException());
				MessageDialog.openError(shell,
						Messages.RenameResourceAction_problemTitle, NLS.bind(
								Messages.RenameResourceAction_problemMessage, e
										.getTargetException().getMessage()));
			}
		}
	}
}
