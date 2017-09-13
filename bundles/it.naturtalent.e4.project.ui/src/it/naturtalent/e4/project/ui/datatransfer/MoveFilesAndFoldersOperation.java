package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.utils.WorkspaceModifyOperation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/**
 * Moves files and folders.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.1
 * @noextend This class is not intended to be subclassed by clients.
 */
public class MoveFilesAndFoldersOperation extends CopyFilesAndFoldersOperation
{

	/**
	 * Creates a new operation initialized with a shell.
	 * 
	 * @param shell
	 *            parent shell for error dialogs
	 */
	public MoveFilesAndFoldersOperation(Shell shell)
	{
		super(shell);
	}

	/**
	 * Returns whether this operation is able to perform on-the-fly
	 * auto-renaming of resources with name collisions.
	 * 
	 * @return <code>true</code> if auto-rename is supported, and
	 *         <code>false</code> otherwise
	 */
	protected boolean canPerformAutoRename()
	{
		return false;
	}

	/**
	 * Moves the resources to the given destination. This method is called
	 * recursively to merge folders during folder move.
	 * 
	 * @param resources
	 *            the resources to move
	 * @param destination
	 *            destination to which resources will be moved
	 * @param subMonitor
	 *            a progress monitor for showing progress and for cancelation
	 * 
	 */
	protected void copy(IResource[] resources, IPath destination,
			IProgressMonitor subMonitor) throws CoreException
	{
		for (int i = 0; i < resources.length; i++)
		{
			IResource source = resources[i];
			IPath destinationPath = destination.append(source.getName());
			IWorkspace workspace = source.getWorkspace();
			IWorkspaceRoot workspaceRoot = workspace.getRoot();
			IResource existing = workspaceRoot.findMember(destinationPath);
			if (source.getType() == IResource.FOLDER && existing != null)
			{
				// the resource is a folder and it exists in the destination,
				// move the children of the folder.
				if (homogenousResources(source, existing))
				{
					IResource[] children = ((IContainer) source).members();
					copy(children, destinationPath, subMonitor);
					delete(source, subMonitor);
				}
				else
				{
					// delete the destination folder, moving a linked folder
					// over an unlinked one or vice versa. Fixes bug 28772.
					delete(existing, new SubProgressMonitor(subMonitor, 0));
					source.move(destinationPath, IResource.SHALLOW
							| IResource.KEEP_HISTORY, new SubProgressMonitor(
							subMonitor, 0));
				}
			}
			else
			{
				// if we're merging folders, we could be overwriting an existing
				// file
				if (existing != null)
				{
					if (homogenousResources(source, existing))
					{
						moveExisting(source, existing, subMonitor);
					}
					else
					{
						// Moving a linked resource over unlinked or vice versa.
						// Can't use setContents here. Fixes bug 28772.
						delete(existing, new SubProgressMonitor(subMonitor, 0));
						source.move(destinationPath, IResource.SHALLOW
								| IResource.KEEP_HISTORY,
								new SubProgressMonitor(subMonitor, 0));
					}
				}
				else
				{
					source.move(destinationPath, IResource.SHALLOW
							| IResource.KEEP_HISTORY, new SubProgressMonitor(
							subMonitor, 0));
				}
				subMonitor.worked(1);
				if (subMonitor.isCanceled())
				{
					throw new OperationCanceledException();
				}
			}
		}
	}

	/**
	 * Returns the message for querying deep copy/move of a linked resource.
	 * 
	 * @param source
	 *            resource the query is made for
	 * @return the deep query message
	 */
	protected String getDeepCheckQuestion(IResource source)
	{
		return NLS
				.bind(DataTransferMessages.CopyFilesAndFoldersOperation_deepMoveQuestion,
						source.getFullPath().makeRelative());
	}

	/**
	 * Returns the task title for this operation's progress dialog.
	 * 
	 * @return the task title
	 */
	protected String getOperationTitle()
	{
		return DataTransferMessages.MoveFilesAndFoldersOperation_operationTitle;
	}

	/**
	 * Returns the message for this operation's problems dialog.
	 * 
	 * @return the problems message
	 */
	protected String getProblemsMessage()
	{
		return DataTransferMessages.MoveFilesAndFoldersOperation_problemMessage;
	}

	/**
	 * Returns the title for this operation's problems dialog.
	 * 
	 * @return the problems dialog title
	 */
	protected String getProblemsTitle()
	{
		return DataTransferMessages.MoveFilesAndFoldersOperation_moveFailedTitle;
	}

	/**
	 * Returns whether the source file in a destination collision will be
	 * validateEdited together with the collision itself. Returns true.
	 * 
	 * @return boolean <code>true</code>, the source file in a destination
	 *         collision should be validateEdited.
	 */
	protected boolean getValidateConflictSource()
	{
		return true;
	}

	/**
	 * Sets the content of the existing file to the source file content. Deletes
	 * the source file.
	 * 
	 * @param source
	 *            source file to move
	 * @param existing
	 *            existing file to set the source content in
	 * @param subMonitor
	 *            a progress monitor for showing progress and for cancelation
	 * @throws CoreException
	 *             setContents failed
	 * @deprecated As of 3.3, this method is not called.
	 */
	private void moveExisting(IResource source, IResource existing,
			IProgressMonitor subMonitor) throws CoreException
	{
		IFile existingFile = getFile(existing);

		if (existingFile != null)
		{
			IFile sourceFile = getFile(source);

			if (sourceFile != null)
			{
				existingFile.setContents(sourceFile.getContents(),
						IResource.KEEP_HISTORY, new SubProgressMonitor(
								subMonitor, 0));
				delete(sourceFile, subMonitor);
			}
		}
	}
	
	public IResource[] moveResources(final IResource[] resources,
			IContainer destination)
	{
		return moveResources(resources, destination, true);
	}

	
	/**
	 * Copies the given resources to the destination.
	 * 
	 * @param resources
	 *            the resources to copy
	 * @param destination
	 *            destination to which resources will be copied
	 * @return IResource[] the resulting {@link IResource}[]
	 */
	private IResource[] moveResources(final IResource[] resources,
			IContainer destination, boolean fork)
	{
		final IPath destinationPath = destination.getFullPath();
		final IResource[][] copiedResources = new IResource[1][0];

		// test resources for existence separate from validate API.
		// Validate is performance critical and resource exists
		// check is potentially slow. Fixes bugs 16129/28602.
		IStatus resourceStatus = checkExist(resources);
		if (resourceStatus.getSeverity() != IStatus.OK)
		{
			displayError(resourceStatus);
			return copiedResources[0];
		}
		String errorMsg = validateDestination(destination, resources);
		if (errorMsg != null)
		{
			displayError(errorMsg); 
			return copiedResources[0];
		}

		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor)
			{
				moveResources(resources, destinationPath, copiedResources,
						monitor);
			}
		};

		try
		{			
			new ProgressMonitorDialog(messageShell).run(true, false, operation);
			
		} catch (InterruptedException e)
		{
			return copiedResources[0];
		} catch (InvocationTargetException e)
		{
			display(e);
		}

		// If errors occurred, open an Error dialog
		if (errorStatus != null)
		{
			displayError(errorStatus);
			errorStatus = null;
		}

		return copiedResources[0];
	}
	
	void moveResources(final IResource[] resources,
			final IPath destinationPath, final IResource[][] copiedResources,
			IProgressMonitor monitor)
	{
		IResource[] copyResources = resources;

		// Fix for bug 31116. Do not provide a task name when
		// creating the task.
		monitor.beginTask("", 100); //$NON-NLS-1$
		monitor.setTaskName(DataTransferMessages.AbstractResourcesOperation_MovingResources);
		monitor.worked(10); // show some initial progress

		// Checks only required if this is an exisiting container path.
		boolean copyWithAutoRename = false;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if (root.exists(destinationPath))
		{
			IContainer container = (IContainer) root
					.findMember(destinationPath);
			// If we're copying to the source container then perform
			// auto-renames on all resources to avoid name collisions.
			if (isDestinationSameAsSource(copyResources, container)
					&& canPerformAutoRename())
			{
				copyWithAutoRename = true;
			}
			else
			{
				// If no auto-renaming will be happening, check for
				// potential name collisions at the target resource
				copyResources = validateNoNameCollisions(container,
						copyResources);
				if (copyResources == null)
				{
					if (canceled)
					{
						return;
					}
					displayError(Messages.CopyFilesAndFoldersOperation_nameCollision);
					return;
				}
				if (validateEdit(container, copyResources) == false)
				{
					return;
				}
			}
		}

		errorStatus = null;
		if (copyResources.length > 0)
		{
			if (copyWithAutoRename)
			{
				/*
				performCopyWithAutoRename(copyResources, destinationPath,
						new SubProgressMonitor(monitor, 90));
						*/
			}
			else
			{
				performMove(copyResources, destinationPath,
						new SubProgressMonitor(monitor, 90));
			}
		}
		monitor.done();
		copiedResources[0] = copyResources;
	}

	private boolean performMove(IResource[] resources, IPath destination,
			IProgressMonitor monitor)
	{
		try
		{
			List<Object> resourcesAtDestination = new ArrayList<Object>();
			boolean createVirtual = false;
			boolean pathIncludesName = false;
			String relativeToVariable = null;

			CopyResourcesOperation.move(resources, destination,
					resourcesAtDestination, monitor, null, pathIncludesName);

		} catch (Exception e)
		{

			if (e.getCause() instanceof CoreException)
			{
				recordError((CoreException) e.getCause());
			}
			else
			{
				// IDEWorkbenchPlugin.log(e.getMessage(), e);
				displayError(e.getMessage());
			}
			return false;

		}
		return true;
	}


	/*
	 * (non-Javadoc) Overrides method in CopyFilesAndFoldersOperation
	 * 
	 * Note this method is for internal use only. It is not API.
	 */
	public String validateDestination(IContainer destination,
			IResource[] sourceResources)
	{
		IPath destinationLocation = destination.getLocation();

		for (int i = 0; i < sourceResources.length; i++)
		{
			IResource sourceResource = sourceResources[i];

			// is the source being copied onto itself?
			if (sourceResource.getParent().equals(destination))
			{
				return NLS
						.bind(DataTransferMessages.MoveFilesAndFoldersOperation_sameSourceAndDest,
								sourceResource.getName());
			}
			// test if linked source is copied onto itself. Fixes bug 29913.
			if (destinationLocation != null)
			{
				IPath sourceLocation = sourceResource.getLocation();
				IPath destinationResource = destinationLocation
						.append(sourceResource.getName());
				if (sourceLocation != null
						&& sourceLocation.isPrefixOf(destinationResource))
				{
					return NLS
							.bind(DataTransferMessages.MoveFilesAndFoldersOperation_sameSourceAndDest,
									sourceResource.getName());
				}
			}
		}
		return super.validateDestination(destination, sourceResources);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.CopyFilesAndFoldersOperation#isMove()
	 */
	protected boolean isMove()
	{
		return true;
	}

	/**
	 * Returns an AbstractWorkspaceOperation suitable for performing the move or
	 * copy operation that will move or copy the given resources to the given
	 * destination path.
	 * 
	 * @param resources
	 *            the resources to be moved or copied
	 * @param destinationPath
	 *            the destination path to which the resources should be moved
	 * @return the operation that should be used to perform the move or copy
	 * @since 3.3
	 */
	protected AbstractWorkspaceOperation getUndoableCopyOrMoveOperation(
			IResource[] resources, IPath destinationPath) {
		return new MoveResourcesOperation(resources, destinationPath,
				DataTransferMessages.CopyFilesAndFoldersOperation_moveTitle);

	}
}
