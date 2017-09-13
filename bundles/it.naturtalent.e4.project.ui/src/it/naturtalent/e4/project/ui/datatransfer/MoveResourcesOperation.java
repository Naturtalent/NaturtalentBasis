package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * A MoveResourcesOperation represents an undoable operation for moving one or
 * more resources in the workspace. Clients may call the public API from a
 * background thread.
 * <p>
 * This operation can track any overwritten resources and restore them when the
 * move is undone. It is up to clients to determine whether overwrites are
 * allowed. If a resource should not be overwritten, it should not be included
 * in this operation. In addition to checking for overwrites, the target
 * location for the move is assumed to have already been validated by the
 * client. It will not be revalidated on undo and redo.
 * </p>
 * <p>
 * This class is intended to be instantiated and used by clients. It is not
 * intended to be subclassed by clients.
 * <p>
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.3
 * 
 */
public class MoveResourcesOperation extends
		AbstractCopyOrMoveResourcesOperation {

	IResource[] originalResources;

	IPath originalDestination;

	IPath[] originalDestinationPaths;
	
	private static Shell shell;

	/**
	 * Create a MoveResourcesOperation that moves all of the specified resources
	 * to the same target location, using their existing names.
	 * 
	 * @param resources
	 *            the resources to be moved
	 * @param destinationPath
	 *            the destination path for the resources, not including the name
	 *            of the moved resource.
	 * @param label
	 *            the label of the operation
	 */
	public MoveResourcesOperation(IResource[] resources, IPath destinationPath,
			String label) {
		super(resources, destinationPath, label);
		originalResources = this.resources;
		originalDestination = this.destination;
		originalDestinationPaths = this.destinationPaths;
	}

	/**
	 * Create a MoveResourcesOperation that moves a single resource to a new
	 * location. The new location includes the name of the resource, so this may
	 * be used for a move/rename operation or a simple move.
	 * 
	 * @param resource
	 *            the resource to be moved
	 * @param newPath
	 *            the new path for the resource, including its desired name.
	 * @param label
	 *            the label of the operation
	 */
	public MoveResourcesOperation(IResource resource, IPath newPath,
			String label) {
		super(new IResource[] { resource }, new IPath[] { newPath }, label);
		originalResources = this.resources;
		originalDestination = this.destination;
		originalDestinationPaths = this.destinationPaths;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Map execute to moving the resources
	 * 
	 * @see org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#doExecute(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	protected void doExecute(IProgressMonitor monitor, IAdaptable uiInfo)
			throws CoreException {
		move(monitor, uiInfo);
	}

	/**
	 * Move any known resources according to the destination parameters known by
	 * this operation. Store enough information to undo and redo the operation.
	 * 
	 * @param monitor
	 *            the progress monitor to use for the operation
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the
	 *            caller in order to supply UI information for prompting the
	 *            user if necessary. When this parameter is not
	 *            <code>null</code>, it contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	protected void move(IProgressMonitor monitor, IAdaptable uiInfo)
			throws CoreException {

		monitor.beginTask("", 2000); //$NON-NLS-1$
		monitor
				.setTaskName(DataTransferMessages.AbstractResourcesOperation_MovingResources);
		List resourcesAtDestination = new ArrayList();
		List undoDestinationPaths = new ArrayList();
		List overwrittenResources = new ArrayList();

		for (int i = 0; i < resources.length; i++) {
			// Move the resources and record the overwrites that would
			// be restored if this operation were reversed		
			move(
					new IResource[] { resources[i] }, getDestinationPath(
							resources[i], i), resourcesAtDestination,
					undoDestinationPaths, new SubProgressMonitor(monitor,
							1000 / resources.length), uiInfo, true);
		}

		// Reset the target resources to refer to the resources in their new
		// location.
		setTargetResources((IResource[]) resourcesAtDestination
				.toArray(new IResource[resourcesAtDestination.size()]));
		// Reset the destination paths that correspond to these resources
		destinationPaths = (IPath[]) undoDestinationPaths
				.toArray(new IPath[undoDestinationPaths.size()]);
		destination = null;

		monitor.done();
	}
	
	/**
	 * Moves the resources to the given destination. This method can be called
	 * recursively to merge folders during folder move.
	 * 
	 * @param resources
	 *            the resources to be moved
	 * @param destination
	 *            the destination path for the resources, relative to the
	 *            workspace
	 * @param resourcesAtDestination
	 *            A list used to record each moved resource.
	 * @param reverseDestinations
	 *            A list used to record each moved resource's original location
	 * @param monitor
	 *            the progress monitor used to show progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the
	 *            caller in order to supply UI information for prompting the
	 *            user if necessary. When this parameter is not
	 *            <code>null</code>, it contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @param pathIncludesName
	 *            a boolean that indicates whether the specified path includes
	 *            the resource's name at the destination. If this value is
	 *            <code>true</code>, the destination will contain the desired
	 *            name of the resource (usually only desired when only one
	 *            resource is being moved). If this value is <code>false</code>,
	 *            each resource's name will be appended to the destination.
	 * @return an array of ResourceDescriptions describing any resources that
	 *         were overwritten by the move operation
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static void move(IResource[] resources, IPath destination,
			List resourcesAtDestination, List reverseDestinations,
			IProgressMonitor monitor, IAdaptable uiInfo,
			boolean pathIncludesName) throws CoreException {

		monitor.beginTask("", resources.length); //$NON-NLS-1$
		monitor
				.setTaskName(DataTransferMessages.AbstractResourcesOperation_MovingResources);
		List overwrittenResources = new ArrayList();
		for (int i = 0; i < resources.length; i++) {
			IResource source = resources[i];
			IPath destinationPath;
			if (pathIncludesName) {
				destinationPath = destination;
			} else {
				destinationPath = destination.append(source.getName());
			}
			IWorkspaceRoot workspaceRoot = getWorkspaceRoot();
			IResource existing = workspaceRoot.findMember(destinationPath);
			if (source.getType() == IResource.FOLDER && existing != null) {
				// The resource is a folder and it exists in the destination.
				// Move its children to the existing destination.
				if (source.isLinked() == existing.isLinked()) {
						IResource[] children = ((IContainer) source).members();
						// move only linked resource children (267173)
						if (source.isLinked() && source.getLocation().equals(existing.getLocation()))
							children = filterNonLinkedResources(children);
						move(children,
								destinationPath, resourcesAtDestination,
								reverseDestinations, new SubProgressMonitor(
										monitor, 1), uiInfo, false);
					// Delete the source. No need to record it since it
					// will get moved back.
					delete(source, monitor, uiInfo, false, false);
				} else {
					// delete the destination folder, moving a linked folder
					// over an unlinked one or vice versa. Fixes bug 28772.
					delete(
							new IResource[] { existing },
							new SubProgressMonitor(monitor, 0), uiInfo, false);
					// Record the original path
					reverseDestinations.add(source.getFullPath());
					source.move(destinationPath, IResource.SHALLOW
							| IResource.KEEP_HISTORY, new SubProgressMonitor(
							monitor, 1));
					// Record the resource at its destination
					resourcesAtDestination.add(getWorkspaceRoot()
							.findMember(destinationPath));					
				}
			} else {
				if (existing != null) {
					if (source.isLinked() == existing.isLinked()) {
						// Record the original path
						reverseDestinations.add(source.getFullPath());
						
						/*
						overwrittenResources.add(copyOverExistingResource(
								source, existing, new SubProgressMonitor(
										monitor, 1), uiInfo, true));
										*/
						
						resourcesAtDestination.add(existing);
					} else {
						// Moving a linked resource over unlinked or vice
						// versa. Can't use setContents here. Fixes bug 28772.
						delete(
								new IResource[] { existing },
								new SubProgressMonitor(monitor, 0), uiInfo,
								false);
						reverseDestinations.add(source.getFullPath());
						source.move(destinationPath, IResource.SHALLOW
								| IResource.KEEP_HISTORY,
								new SubProgressMonitor(monitor, 1));
						// Record the resource at its destination
						resourcesAtDestination.add(getWorkspaceRoot()
								.findMember(destinationPath));
						
					}
				} else {
					// No resources are being overwritten.
					// First record the source path
					reverseDestinations.add(source.getFullPath());
					// ensure the destination path exists
					IPath parentPath = destination;
					if (pathIncludesName) {
						parentPath = destination.removeLastSegments(1);
					}

					IContainer generatedParent = generateContainers(parentPath);
					source.move(destinationPath, IResource.SHALLOW
							| IResource.KEEP_HISTORY, new SubProgressMonitor(
							monitor, 1));
					// Record the move. If we had to generate a parent
					// folder, that should be recorded as part of the copy
					if (generatedParent == null) {
						resourcesAtDestination.add(getWorkspaceRoot()
								.findMember(destinationPath));
					} else {
						resourcesAtDestination.add(generatedParent);
					}
				}

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		}
		monitor.done();		
	}
	
	/*
	 * Check for existence of the specified path and generate any containers
	 * that do not yet exist. Return any generated containers, or null if no
	 * container had to be generated.
	 */
	private static IContainer generateContainers(IPath path)
			throws CoreException {
		IResource container;
		if (path.segmentCount() == 0) {
			// nothing to generate
			return null;
		}
		container = getWorkspaceRoot().findMember(path);
		// Nothing to generate because container exists
		if (container != null) {
			return null;
		}

		// Now make a non-existent handle representing the desired container
		if (path.segmentCount() == 1) {
			container = ResourcesPlugin.getWorkspace().getRoot().getProject(
					path.segment(0));
		} else {
			container = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(path);
		}
		return (IContainer) container;
	}

	/**
	 * Returns only the linked resources out of an array of resources
	 * @param resources The resources to filter
	 * @return The linked resources
	 */
	private static IResource[] filterNonLinkedResources(IResource[] resources)
	{
		List result = new ArrayList();
		for (int i = 0; i < resources.length; i++)
		{
			if (resources[i].isLinked())
				result.add(resources[i]);
		}
		return (IResource[]) result.toArray(new IResource[0]);
	}
	
	/**
	 * Delete the specified resources
	 * 
	 * @param resourceToDelete
	 *            the resource to be deleted
	 * @param monitor
	 *            the progress monitor to use to show the operation's progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @param forceOutOfSyncDelete
	 *            a boolean indicating whether a resource should be deleted even
	 *            if it is out of sync with the file system
	 * @param deleteContent
	 *            a boolean indicating whether project content should be deleted
	 *            when a project resource is to be deleted
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static void delete(IResource resourceToDelete, IProgressMonitor monitor,
			IAdaptable uiInfo, boolean forceOutOfSyncDelete,
			boolean deleteContent) throws CoreException
	{
		if (resourceToDelete.getType() == IResource.PROJECT)
		{
			// it is a project
			monitor.setTaskName("delete ...");
			IProject project = (IProject) resourceToDelete;
			project.delete(deleteContent, forceOutOfSyncDelete, monitor);
		}
		else
		{
			// if it's not a project, just delete it
			monitor.beginTask("", 2); //$NON-NLS-1$
			monitor.setTaskName("delete ...");
			int updateFlags;
			if (forceOutOfSyncDelete)
			{
				updateFlags = IResource.KEEP_HISTORY | IResource.FORCE;
			}
			else
			{
				updateFlags = IResource.KEEP_HISTORY;
			}
			resourceToDelete.delete(updateFlags, new SubProgressMonitor(
					monitor, 1));
			monitor.done();
		}
	}

	/**
	 * Delete all of the specified resources, returning resource descriptions
	 * that can be used to restore them.
	 * 
	 * @param resourcesToDelete
	 *            an array of resources to be deleted
	 * @param monitor
	 *            the progress monitor to use to show the operation's progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * 
	 * @param deleteContent
	 *            a boolean indicating whether project content should be deleted
	 *            when a project resource is to be deleted
	 * @return an array of ResourceDescriptions that can be used to restore the
	 *         deleted resources.
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static void delete(IResource[] resourcesToDelete, IProgressMonitor monitor,
			IAdaptable uiInfo, boolean deleteContent) throws CoreException
	{
		final List exceptions = new ArrayList();
		boolean forceOutOfSyncDelete = false;
		monitor.beginTask("", resourcesToDelete.length); //$NON-NLS-1$
		monitor.setTaskName("delete ...");
		try
		{
			for (int i = 0; i < resourcesToDelete.length; ++i)
			{
				if (monitor.isCanceled())
				{
					throw new OperationCanceledException();
				}
				IResource resource = resourcesToDelete[i];
				try
				{
					delete(resource, new SubProgressMonitor(monitor, 1),
							uiInfo, forceOutOfSyncDelete, deleteContent);
				} catch (CoreException e)
				{
					if (resource.getType() == IResource.FILE)
					{
						IStatus[] children = e.getStatus().getChildren();
						if (children.length == 1
								&& children[0].getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL)
						{
							int result = queryDeleteOutOfSync(resource, uiInfo);

							if (result == IDialogConstants.YES_ID)
							{
								// retry the delete with a force out of sync
								delete(resource, new SubProgressMonitor(
										monitor, 1), uiInfo, true,
										deleteContent);
							}
							else if (result == IDialogConstants.YES_TO_ALL_ID)
							{
								// all future attempts should force out of
								// sync
								forceOutOfSyncDelete = true;
								delete(resource, new SubProgressMonitor(
										monitor, 1), uiInfo,
										forceOutOfSyncDelete, deleteContent);
							}
							else if (result == IDialogConstants.CANCEL_ID)
							{
								throw new OperationCanceledException();
							}
							else
							{
								exceptions.add(e);
							}
						}
						else
						{
							exceptions.add(e);
						}
					}
					else
					{
						exceptions.add(e);
					}
				}
			}
			IStatus result = createResult(exceptions);
			if (!result.isOK())
			{
				throw new CoreException(result);
			}
		} finally
		{
			monitor.done();
		}
	}

	/*
	 * Creates and return a result status appropriate for the given list of
	 * exceptions.
	 */
	private static IStatus createResult(List exceptions)
	{
		if (exceptions.isEmpty())
		{
			return Status.OK_STATUS;
		}
		final int exceptionCount = exceptions.size();
		if (exceptionCount == 1)
		{
			return ((CoreException) exceptions.get(0)).getStatus();
		}
		CoreException[] children = (CoreException[]) exceptions
				.toArray(new CoreException[exceptionCount]);
		boolean outOfSync = false;
		for (int i = 0; i < children.length; i++)
		{
			if (children[i].getStatus().getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL)
			{
				outOfSync = true;
				break;
			}
		}
		String title = outOfSync ? Messages.AbstractResourcesOperation_outOfSyncError
				: Messages.AbstractResourcesOperation_deletionExceptionMessage;
		final MultiStatus multi = new MultiStatus(Activator.PLUGIN_ID, 0,
				title, null);
		for (int i = 0; i < exceptionCount; i++)
		{
			CoreException exception = children[i];
			IStatus status = exception.getStatus();
			multi.add(new Status(status.getSeverity(), status.getPlugin(),
					status.getCode(), status.getMessage(), exception));
		}
		return multi;
	}

	
	/*
	 * Return the workspace root.
	 */
	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#updateResourceChangeDescriptionFactory(org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory,
	 *      int)
	 */
	protected boolean updateResourceChangeDescriptionFactory(
			IResourceChangeDescriptionFactory factory, int operation) {
		for (int i = 0; i < resources.length; i++) {
			IResource resource = resources[i];
			factory.move(resource, getDestinationPath(resource, i));
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Map undo to move status.
	 * 
	 * @see org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#computeUndoableStatus(org.eclipse.core.runtime.IProgressMonitor)
	 */
	/*
	public IStatus computeUndoableStatus(IProgressMonitor monitor) {
		IStatus status = super.computeUndoableStatus(monitor);
		if (status.isOK()) {
			status = computeMoveOrCopyStatus();
		}
		return status;
	}
	*/
	
	/*
	 * Ask the user whether the given resource should be deleted despite being
	 * out of sync with the file system.
	 * 
	 * Return one of the IDialogConstants constants indicating which of the Yes,
	 * Yes to All, No, Cancel options has been selected by the user.
	 */
	private static int queryDeleteOutOfSync(IResource resource,
			IAdaptable uiInfo)
	{
		final MessageDialog dialog = new MessageDialog(shell,
				Messages.AbstractResourcesOperation_deletionMessageTitle, null,
				NLS.bind(Messages.AbstractResourcesOperation_outOfSyncQuestion,
						resource.getName()), MessageDialog.QUESTION,
				new String[]
					{ IDialogConstants.YES_LABEL,
							IDialogConstants.YES_TO_ALL_LABEL,
							IDialogConstants.NO_LABEL,
							IDialogConstants.CANCEL_LABEL }, 0)
		{
			protected int getShellStyle()
			{
				return super.getShellStyle() | SWT.SHEET;
			}
		};
		shell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				dialog.open();
			}
		});
		int result = dialog.getReturnCode();
		if (result == 0)
		{
			return IDialogConstants.YES_ID;
		}
		if (result == 1)
		{
			return IDialogConstants.YES_TO_ALL_ID;
		}
		if (result == 2)
		{
			return IDialogConstants.NO_ID;
		}
		return IDialogConstants.CANCEL_ID;
	}

}
