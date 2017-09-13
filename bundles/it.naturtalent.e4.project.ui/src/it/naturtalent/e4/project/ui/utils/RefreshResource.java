package it.naturtalent.e4.project.ui.utils;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.datatransfer.CopyFilesAndFoldersOperation;
import it.naturtalent.e4.project.ui.utils.WorkspaceModifyOperation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class RefreshResource
{

	/**
	 * Eine Ressource umbennen.
	 * 
	 * @param shell
	 * @param container
	 * @param folderName
	 */
	public static void refresh(final Shell shell, final IResource resource)
	{
		Log log = LogFactory.getLog(RefreshResource.class);
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask(Messages.RefreshAction_progressMessage, 1);
					if (monitor.isCanceled())
					{
						throw new OperationCanceledException();
					}

					if (resource.getType() == IResource.PROJECT)
					{
						checkLocationDeleted(shell, (IProject) resource);
					}
					else if (resource.getType() == IResource.ROOT)
					{
						IProject[] projects = ((IWorkspaceRoot) resource)
								.getProjects();
						for (int i = 0; i < projects.length; i++)
						{
							checkLocationDeleted(shell, projects[i]);
						}
					}
					resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);

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
			MessageDialog.openError(shell,
					Messages.RefreshAction_interruptInfo, e.getMessage());
		} catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof CoreException)
			{
				log.error(e.getTargetException());
				ErrorDialog.openError(shell,
						Messages.RefreshAction_problemTitle, null, // no special
																	// message
						((CoreException) e.getTargetException()).getStatus());
			}
			else
			{
				// CoreExceptions are handled above, but unexpected runtime
				// exceptions and errors may still occur.
				log.error(e.getTargetException());
				MessageDialog.openError(shell,
						Messages.RefreshAction_problemTitle, NLS.bind(
								Messages.RefreshAction_problemMessage, e
										.getTargetException().getMessage()));
			}
		}
	}

	/**
	 * Checks whether the given project's location has been deleted. If so,
	 * prompts the user with whether to delete the project or not.
	 */
	private static void checkLocationDeleted(Shell shell, IProject project)
			throws CoreException
	{
		if (!project.exists())
		{
			return;
		}
		IFileInfo location = ResourceInfoUtils.getFileInfo(project
				.getLocationURI());
		if (!location.exists())
		{
			String message = NLS.bind(
					Messages.RefreshAction_locationDeletedMessage,
					project.getName(), location.toString());

			final MessageDialog dialog = new MessageDialog(
					shell,
					Messages.RefreshAction_dialogTitle, // dialog
					// title
					null, // use default window icon
					message,
					MessageDialog.QUESTION,
					new String[]
						{ IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
					0)
			{
				protected int getShellStyle()
				{
					return super.getShellStyle() | SWT.SHEET;
				}
			}; // yes is the
				// default

			// Must prompt user in UI thread (we're in the operation thread
			// here).
			shell.getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					dialog.open();
				}
			});

			// Do the deletion back in the operation thread
			if (dialog.getReturnCode() == 0)
			{ // yes was chosen
				project.delete(true, true, null);
			}
		}
	}
}
