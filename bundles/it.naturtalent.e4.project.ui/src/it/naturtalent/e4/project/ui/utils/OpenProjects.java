package it.naturtalent.e4.project.ui.utils;

import it.naturtalent.e4.project.ui.Messages;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

public class OpenProjects
{

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Eine Ressource umbennen.
	 * 
	 * @param shell
	 * @param container
	 * @param folderName
	 */
	public void open(Shell shell, final IProject [] projects)
	{
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask("open", projects.length); //$NON-NLS-1$
					if (monitor.isCanceled())
					{
						throw new OperationCanceledException();						
					}

					for(IProject project : projects)
					{
						project.open(monitor);
						monitor.worked(1);
					}
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
							Messages.OpenProjects_Cancel,
							e.getMessage());
		} catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof CoreException)
			{
				log.error(e); 
				ErrorDialog.openError(shell,Messages.OpenProjects_OpenError, null, // no special message
						((CoreException) e.getTargetException()).getStatus());
			}
			else
			{
				// CoreExceptions are handled above, but unexpected runtime
				log.error(e);
				MessageDialog.openError(shell,
						Messages.OpenProjectAction_problemTitle, NLS.bind(
								Messages.OpenProjectAction_problemMessage, e
										.getTargetException().getMessage()));
			}
		}
	}
}
