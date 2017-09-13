package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class CopyResourcesOperation extends
AbstractCopyOrMoveResourcesOperation
{

	// Used when all resources are going to the same container (no name changes)
	protected IPath destination = null;

	//protected IResource[] resources;

	private static Shell shell;
	
	IResource[] originalResources;

	/**
	 * Create a CopyResourcesOperation that copies each of the specified
	 * resources to its corresponding destination path in the destination path
	 * array. The resource name for the target is included in the corresponding
	 * destination path.
	 * 
	 * @param resources
	 *            the resources to be copied. Must not contain null resources.
	 * @param destinationPaths
	 *            a workspace-relative destination path for each copied
	 *            resource, which includes the name of the resource at the new
	 *            destination. Must be the same length as the resources array,
	 *            and may not contain null paths.
	 * @param label
	 *            the label of the operation
	 */
	public CopyResourcesOperation(IResource[] resources,
			IPath[] destinationPaths, String label) {
		super(resources, destinationPaths, label);
		setOriginalResources(this.resources);
	}
	
	
	/**
	 * Move or copy any known resources according to the destination parameters
	 * known by this operation. Store enough information to undo and redo the
	 * operation.
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
	protected void copy(IProgressMonitor monitor)
			throws CoreException
	{

		monitor.beginTask("", 2000); //$NON-NLS-1$
		monitor.setTaskName(Messages.AbstractResourcesOperation_CopyingResourcesProgress);
		List resourcesAtDestination = new ArrayList();
		List overwrittenResources = new ArrayList();

		for (int i = 0; i < resources.length; i++)
		{
			// Copy the resources and record the overwrites that would
			// be restored if this operation were reversed
			copy(new IResource[]
				{ resources[i] }, getDestinationPath(resources[i], i),
					resourcesAtDestination, new SubProgressMonitor(monitor,
							1000 / resources.length), true,
					fCreateGroups, fCreateLinks, fRelativeToVariable);
		}

		// Reset the target resources to refer to the resources in their new
		// location.
		setTargetResources((IResource[]) resourcesAtDestination
				.toArray(new IResource[resourcesAtDestination.size()]));
		monitor.done();
	}
	
	/**
	 * Create a CopyResourcesOperation that copies all of the specified
	 * resources to a single target location. The original resource name will be
	 * used when copied to the new location.
	 * 
	 * @param resources
	 *            the resources to be copied
	 * @param destinationPath
	 *            the workspace-relative destination path for the copied
	 *            resource.
	 * @param label
	 *            the label of the operation
	 */
	public CopyResourcesOperation(IResource[] resources, IPath destinationPath,
			String label) 
	{
		super(resources, destinationPath, label);
		setOriginalResources(this.resources);
				
		this.destination = destination;
		setTargetResources(resources);
		this.originalResources = this.resources;
	}


	/*
	 * Return true if the specified subResource is a descendant of the specified
	 * super resource. Used to remove descendants from the resource array when
	 * an operation is requested on a parent and its descendant.
	 */
	private static boolean isDescendantOf(IResource subResource,
			IResource superResource)
	{
		return !subResource.equals(superResource)
				&& superResource.getFullPath().isPrefixOf(
						subResource.getFullPath());
	}
	
	/**
	 * Copies the resources to the given destination. This method can be called
	 * recursively to merge folders during folder copy.
	 * 
	 * @param resources
	 *            the resources to be copied
	 * @param destination
	 *            the destination path for the resources, relative to the
	 *            workspace
	 * @param resourcesAtDestination
	 *            A list used to record the new copies.
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
	 *            resource is being copied). If this value is <code>false</code>,
	 *            each resource's name will be appended to the destination.
	 * @return an array of ResourceDescriptions describing any resources that
	 *         were overwritten by the copy operation
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static void copy(IResource[] resources, IPath destination,
			List resourcesAtDestination, IProgressMonitor monitor,
			IAdaptable uiInfo, boolean pathIncludesName) throws CoreException
	{
		copy(resources, destination, resourcesAtDestination, monitor, pathIncludesName, false, false, null);
	}


	/**
	 * Copies the resources to the given destination. This method can be called
	 * recursively to merge folders during folder copy.
	 * 
	 * @param resources
	 *            the resources to be copied
	 * @param destination
	 *            the destination path for the resources, relative to the
	 *            workspace
	 * @param resourcesAtDestination
	 *            A list used to record the new copies.
	 * @param monitor
	 *            the progress monitor used to show progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @param pathIncludesName
	 *            a boolean that indicates whether the specified path includes
	 *            the resource's name at the destination. If this value is
	 *            <code>true</code>, the destination will contain the desired
	 *            name of the resource (usually only desired when only one
	 *            resource is being copied). If this value is <code>false</code>
	 *            , each resource's name will be appended to the destination.
	 * @param createVirtual
	 *            a boolean that indicates whether virtual folders should be
	 *            created instead of folders when a hierarchy of files is
	 *            copied.
	 * @param createLinks
	 *            a boolean that indicates whether linked resources should be
	 *            created instead of files and folders (if createGroups is
	 *            false) when copied.
	 * @param relativeToVariable
	 *            a String that indicates relative to which variable linked
	 *            resources should be created, if createLinks is set to true.
	 *            Absolute linked resources will be created if null is passed
	 *            otherwise (and createLinks is set to true).
	 * @return an array of ResourceDescriptions describing any resources that
	 *         were overwritten by the copy operation
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static void copy(IResource[] resources, IPath destination,
			List resourcesAtDestination, IProgressMonitor monitor,
			boolean pathIncludesName, boolean createVirtual,
			boolean createLinks, String relativeToVariable)
			throws CoreException
	{

		monitor.beginTask("", resources.length); //$NON-NLS-1$
		monitor.setTaskName(Messages.AbstractResourcesOperation_CopyingResourcesProgress);
		List overwrittenResources = new ArrayList();
		for (int i = 0; i < resources.length; i++)
		{
			IResource source = resources[i];
			IPath destinationPath;
			if (pathIncludesName)
			{
				destinationPath = destination;
			}
			else
			{
				destinationPath = destination.append(source.getName());
			}
			IWorkspaceRoot workspaceRoot = getWorkspaceRoot();
			IResource existing = workspaceRoot.findMember(destinationPath);
			if (source.getType() == IResource.FOLDER && existing != null)
			{
				// The resource is a folder and it exists in the destination.
				// Copy its children to the existing destination.
				if ((source.isLinked() && existing.isLinked())
						|| (source.isVirtual() && existing.isVirtual())
						|| (!source.isLinked() && !existing.isLinked()
								&& !source.isVirtual() && !existing.isVirtual()))
				{
					IResource[] children = ((IContainer) source).members();
					// copy only linked resource children (267173)
					if (source.isLinked()
							&& source.getLocation().equals(
									existing.getLocation()))
						children = filterNonLinkedResources(children);
					copy(children, destinationPath, resourcesAtDestination,
							new SubProgressMonitor(monitor, 1), false,
							createVirtual, createLinks, relativeToVariable);

				}
				else
				{
					// delete the destination folder, copying a linked folder
					// over an unlinked one or vice versa. Fixes bug 28772.
					delete(new IResource[]
						{ existing }, new SubProgressMonitor(monitor, 0),
							false);
					if ((createLinks || createVirtual)
							&& (source.isLinked() == false)
							&& (source.isVirtual() == false))
					{
						IFolder folder = workspaceRoot
								.getFolder(destinationPath);
						if (createVirtual)
						{
							folder.create(IResource.VIRTUAL, true,
									new SubProgressMonitor(monitor, 1));
							IResource[] members = ((IContainer) source)
									.members();

						}
						else
							folder.createLink(
									createRelativePath(source.getLocationURI(),
											relativeToVariable, folder), 0,
									new SubProgressMonitor(monitor, 1));
					}
					else
						source.copy(destinationPath, IResource.SHALLOW,
								new SubProgressMonitor(monitor, 1));
				}
			}
			else
			{
				if (existing != null)
				{
					// source is a FILE and destination EXISTS
					if ((createLinks || createVirtual)
							&& (source.isLinked() == false))
					{
						// we create a linked file, and overwrite the
						// destination
						
						delete(new IResource[]
							{ existing }, new SubProgressMonitor(monitor, 0),
								false);
					
						if (source.getType() == IResource.FILE)
						{
							IFile file = workspaceRoot.getFile(destinationPath);
							file.createLink(
									createRelativePath(source.getLocationURI(),
											relativeToVariable, file), 0,
									new SubProgressMonitor(monitor, 1));
						}
						else
						{
							IFolder folder = workspaceRoot
									.getFolder(destinationPath);
							if (createVirtual)
							{
								folder.create(IResource.VIRTUAL, true,
										new SubProgressMonitor(monitor, 1));
								IResource[] members = ((IContainer) source)
										.members();

							}
							else
								folder.createLink(
										createRelativePath(
												source.getLocationURI(),
												relativeToVariable, folder), 0,
										new SubProgressMonitor(monitor, 1));
						}
						resourcesAtDestination.add(getWorkspaceRoot()
								.findMember(destinationPath));

					}
					else
					{
						if (source.isLinked() == existing.isLinked())
						{
							// Record the "copy"
							resourcesAtDestination.add(existing);
						}
						else
						{
							// Copying a linked resource over unlinked or vice
							// versa. Can't use setContents here. Fixes bug
							// 28772.
							delete(new IResource[]
								{ existing },
									new SubProgressMonitor(monitor, 0), false);
							source.copy(destinationPath, IResource.SHALLOW,
									new SubProgressMonitor(monitor, 1));
							// Record the copy
							resourcesAtDestination.add(getWorkspaceRoot()
									.findMember(destinationPath));

						}
					}
				}
				else
				{
					// source is a FILE or FOLDER
					// no resources are being overwritten
					// ensure the destination path exists
					IPath parentPath = destination;
					if (pathIncludesName)
					{
						parentPath = destination.removeLastSegments(1);
					}
					IContainer generatedParent = generateContainers(parentPath);
					if ((createLinks || createVirtual)
							&& (source.isLinked() == false))
					{
						if (source.getType() == IResource.FILE)
						{
							IFile file = workspaceRoot.getFile(destinationPath);
							file.createLink(
									createRelativePath(source.getLocationURI(),
											relativeToVariable, file), 0,
									new SubProgressMonitor(monitor, 1));
						}
						else
						{
							IFolder folder = workspaceRoot
									.getFolder(destinationPath);
							if (createVirtual)
							{
								folder.create(IResource.VIRTUAL, true,
										new SubProgressMonitor(monitor, 1));
								IResource[] members = ((IContainer) source)
										.members();

							}
							else
								folder.createLink(
										createRelativePath(
												source.getLocationURI(),
												relativeToVariable, folder), 0,
										new SubProgressMonitor(monitor, 1));
						}
					}
					else
						source.copy(destinationPath, IResource.SHALLOW,
								new SubProgressMonitor(monitor, 1));
					// Record the copy. If we had to generate a parent
					// folder, that should be recorded as part of the copy
					if (generatedParent == null)
					{
						resourcesAtDestination.add(getWorkspaceRoot()
								.findMember(destinationPath));
					}
					else
					{
						resourcesAtDestination.add(generatedParent);
					}
				}

				if (monitor.isCanceled())
				{
					throw new OperationCanceledException();
				}
			}
		}
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
			List resourcesAtDestination, 
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
								new SubProgressMonitor(
										monitor, 1), uiInfo, false);
		
					// Delete the source. No need to record it since it
					// will get moved back.
					delete(source, monitor, false, false);
				} else {
					// delete the destination folder, moving a linked folder
					// over an unlinked one or vice versa. Fixes bug 28772.
					delete(	new IResource[] { existing },
							new SubProgressMonitor(monitor, 0), false);
					// Record the original path					
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
						resourcesAtDestination.add(existing);
					} else {
						// Moving a linked resource over unlinked or vice
						// versa. Can't use setContents here. Fixes bug 28772.
						delete(
								new IResource[] { existing },
								new SubProgressMonitor(monitor, 0), false);						
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
			throws CoreException
	{
		IResource container;
		if (path.segmentCount() == 0)
		{
			// nothing to generate
			return null;
		}
		container = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		// Nothing to generate because container exists
		if (container != null)
		{
			return null;
		}

		// Now make a non-existent handle representing the desired container
		if (path.segmentCount() == 1)
		{
			container = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(path.segment(0));
		}
		else
		{
			container = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(path);
		}
		return (IContainer) container;
	}

	/*
	 * Copy the content of the specified resource to the existing resource,
	 * returning a ResourceDescription that can be used to restore the original
	 * content. Do nothing if the resources are not files.
	 */
	private static void copyOverExistingResource(IResource source,
			IResource existing, IProgressMonitor monitor, IAdaptable uiInfo,
			boolean deleteSourceFile) throws CoreException
	{
		if (!(source instanceof IFile && existing instanceof IFile))
		{
			return;
		}
		IFile file = (IFile) source;
		IFile existingFile = (IFile) existing;
		monitor.beginTask(
				Messages.AbstractResourcesOperation_CopyingResourcesProgress, 3);
		if (file != null && existingFile != null)
		{
			if (validateEdit(file, existingFile, shell))
			{
				// Now delete the source file if requested
				// We don't need to remember anything about it, because
				// any undo involving this operation will move the original
				// content back to it.
				if (deleteSourceFile)
				{
					file.delete(IResource.KEEP_HISTORY, new SubProgressMonitor(
							monitor, 1));
				}
				monitor.done();
			}
		}
		monitor.done();
	}

	/*
	 * Validate the destination file if it is read-only and additionally the
	 * source file if both are read-only. Returns true if both files could be
	 * made writeable.
	 */
	private static boolean validateEdit(IFile source, IFile destination,
			Shell shell)
	{
		if (destination.isReadOnly())
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IStatus status;
			if (source.isReadOnly())
			{
				status = workspace.validateEdit(new IFile[]
					{ source, destination }, shell);
			}
			else
			{
				status = workspace.validateEdit(new IFile[]
					{ destination }, shell);
			}
			return status.isOK();
		}
		return true;
	}

	/**
	 * Transform an absolute path URI to a relative path one (i.e. from
	 * "C:\foo\bar\file.txt" to "VAR\file.txt" granted that the relativeVariable
	 * is "VAR" and points to "C:\foo\bar\").
	 * 
	 * @param locationURI
	 * @param resource
	 * @return an URI that was made relative to a variable
	 */
	static private URI createRelativePath(URI locationURI,
			String relativeVariable, IResource resource)
	{
		if (relativeVariable == null)
			return locationURI;
		IPath location = URIUtil.toPath(locationURI);
		IPath result;
		try
		{
			result = URIUtil.toPath(resource.getPathVariableManager()
					.convertToRelative(URIUtil.toURI(location), true,
							relativeVariable));
		} catch (CoreException e)
		{
			return locationURI;
		}
		return URIUtil.toURI(result);
	}

	/**
	 * Returns only the linked resources out of an array of resources
	 * 
	 * @param resources
	 *            The resources to filter
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
			boolean deleteContent) throws CoreException
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
							forceOutOfSyncDelete, deleteContent);
				} catch (CoreException e)
				{
					if (resource.getType() == IResource.FILE)
					{
						IStatus[] children = e.getStatus().getChildren();
						if (children.length == 1
								&& children[0].getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL)
						{
							int result = queryDeleteOutOfSync(resource);

							if (result == IDialogConstants.YES_ID)
							{
								// retry the delete with a force out of sync
								delete(resource, new SubProgressMonitor(
										monitor, 1), true,
										deleteContent);
							}
							else if (result == IDialogConstants.YES_TO_ALL_ID)
							{
								// all future attempts should force out of
								// sync
								forceOutOfSyncDelete = true;
								delete(resource, new SubProgressMonitor(
										monitor, 1), forceOutOfSyncDelete, deleteContent);
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
	 * Ask the user whether the given resource should be deleted despite being
	 * out of sync with the file system.
	 * 
	 * Return one of the IDialogConstants constants indicating which of the Yes,
	 * Yes to All, No, Cancel options has been selected by the user.
	 */
	private static int queryDeleteOutOfSync(IResource resource)
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
			boolean forceOutOfSyncDelete,
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

	/*
	 * Return the workspace.
	 */
	/*
	private static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}
	*/

	/*
	 * Return the workspace root.
	 */
	private static IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/*
	 * Record the original resources, including a resource description to
	 * describe it. This is so we can make sure the original resources and their
	 * subtrees are intact before allowing a copy to be undone.
	 */
	private void setOriginalResources(IResource[] originals)
	{
		originalResources = originals;
	}

	/*
	@Override
	protected void doUndo(IProgressMonitor monitor, IAdaptable uiInfo)
			throws CoreException
	{
		// TODO Auto-generated method stub
		
	}
	*/


	/*
	@Override
	protected void doExecute(IProgressMonitor monitor, IAdaptable uiInfo)
			throws CoreException
	{
		// TODO Auto-generated method stub
		
	}
	*/

/*
	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException
	{
		// TODO Auto-generated method stub
		copy(monitor);
	}
*/


}
