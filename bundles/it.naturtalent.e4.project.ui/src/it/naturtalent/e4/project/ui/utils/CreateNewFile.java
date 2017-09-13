package it.naturtalent.e4.project.ui.utils;

import it.naturtalent.e4.project.ui.Messages;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class CreateNewFile
{
	
	private static InputStream fileContents = new ByteArrayInputStream(new byte[0]);
	
	/**
	 * Eine neue Datei erzeugen
	 * 
	 * @param shell
	 * @param container
	 * @param folderName
	 * @param linkTarget
	 */
	public static void createFile(Shell shell, IContainer container,
			String fileName, InputStream contents, final URI linkTarget)
	{			
		final IFile fileHandle = createFileHandle(container, fileName);
		
		if (contents != null)
			fileContents = contents;

		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask("neue Datei", 2000);
					if (monitor.isCanceled())
					{
						throw new OperationCanceledException();
					}
					if (linkTarget == null)
					{
						fileHandle.create(fileContents, IResource.FORCE, monitor);
					}
					else
					{
						fileHandle.createLink(linkTarget,
								IResource.ALLOW_MISSING_LOCAL, monitor);
					}
					if (monitor.isCanceled())
					{
						throw new OperationCanceledException();
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
			MessageDialog.openError(shell, "Abbruch", e.getMessage());
		} catch (InvocationTargetException e)
		{			
			if (e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(shell,
						Messages.NewFolderDialog_errorTitle, null, // no special message
						((CoreException) e.getTargetException())
								.getStatus());
			} else {
				// CoreExceptions are handled above, but unexpected runtime exceptions and errors may still occur.
				
				/* Logger muss noch realisiert werden
				IDEWorkbenchPlugin.log(getClass(),
						"createNewFolder", exception.getTargetException()); //$NON-NLS-1$
						*/
				
				MessageDialog
						.openError(
								shell,
								Messages.NewFolderDialog_errorTitle,
								NLS
										.bind(
												Messages.NewFolderDialog_internalError,
												e.getTargetException()
														.getMessage()));
			}
		}
	}

	/**
	 * Creates a folder resource handle for the folder with the given name.
	 * The folder handle is created relative to the container specified during 
	 * object creation. 
	 *
	 * @param folderName the name of the folder resource to create a handle for
	 * @return the new folder resource handle
	 */
	private static IFile createFileHandle(IContainer container, String fileName)
	{
		IWorkspaceRoot workspaceRoot = container.getWorkspace().getRoot();
		IPath filePath = container.getFullPath().append(fileName);
		IFile fileHandle = workspaceRoot.getFile(filePath);

		return fileHandle;
	}

}
