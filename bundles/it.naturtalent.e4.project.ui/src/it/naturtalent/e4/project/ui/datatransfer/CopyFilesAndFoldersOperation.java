package it.naturtalent.e4.project.ui.datatransfer;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.actions.SystenOpenEditorAction;
import it.naturtalent.e4.project.ui.utils.WorkspaceModifyOperation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;

public class CopyFilesAndFoldersOperation
{

	/**
	 * Status containing the errors detected when running the operation or
	 * <code>null</code> if no errors detected.
	 */
	protected MultiStatus errorStatus;

	/**
	 * The parent shell used to show any dialogs.
	 */
	protected Shell messageShell;

	/**
	 * Whether or not the copy has been canceled by the user.
	 */
	protected boolean canceled = false;

	/**
	 * Whether or not the operation creates links instead of folders and files.
	 */
	protected boolean createLinks = false;

	/**
	 * Whether or not the operation creates virtual folders and links instead of
	 * folders and files.
	 */
	private boolean createVirtualFoldersAndLinks = false;
	
	/**
	 * Overwrite all flag.
	 */
	private boolean alwaysOverwrite = false;

	private String relativeVariable = null;
	
	private Log log = LogFactory.getLog(CopyFilesAndFoldersOperation.class);

	/**
	 * Creates a new operation initialized with a shell.
	 * 
	 * @param shell
	 *            parent shell for error dialogs
	 */
	public CopyFilesAndFoldersOperation(Shell shell)
	{
		messageShell = shell;
	}

	/**
	 * Returns a new name for a copy of the resource at the given path in the
	 * given workspace. This name is determined automatically.
	 * 
	 * @param originalName
	 *            the full path of the resource
	 * @param workspace
	 *            the workspace
	 * @return the new full path for the copy
	 */
	static IPath getAutoNewNameFor(IPath originalName, IWorkspace workspace)
	{
		int counter = 1;
		String resourceName = originalName.lastSegment();
		IPath leadupSegment = originalName.removeLastSegments(1);

		while (true)
		{
			String nameSegment;

			if (counter > 1)
			{
				nameSegment = NLS.bind(
						Messages.CopyFilesAndFoldersOperation_copyNameTwoArgs,
						new Integer(counter), resourceName);
			}
			else
			{
				nameSegment = NLS.bind(
						Messages.CopyFilesAndFoldersOperation_copyNameOneArg,
						resourceName);
			}

			IPath pathToTry = leadupSegment.append(nameSegment);

			if (!workspace.getRoot().exists(pathToTry))
			{
				return pathToTry;
			}

			counter++;
		}
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
		return true;
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
		return NLS.bind(Messages.CopyFilesAndFoldersOperation_deepCopyQuestion,
				source.getFullPath().makeRelative());
	}

	/**
	 * Checks whether the infos exist.
	 * 
	 * @param stores
	 *            the file infos to test
	 * @return Multi status with one error message for each missing file.
	 */
	IStatus checkExist(IFileStore[] stores)
	{
		MultiStatus multiStatus = new MultiStatus(PlatformUI.PLUGIN_ID,
				IStatus.OK, getProblemsMessage(), null);

		for (int i = 0; i < stores.length; i++)
		{
			if (stores[i].fetchInfo().exists() == false)
			{
				String message = NLS.bind(
						Messages.CopyFilesAndFoldersOperation_resourceDeleted,
						stores[i].getName());
				IStatus status = new Status(IStatus.ERROR,
						PlatformUI.PLUGIN_ID, IStatus.OK, message, null);
				multiStatus.add(status);
			}
		}
		return multiStatus;
	}

	/**
	 * Checks whether the resources with the given names exist.
	 * 
	 * @param resources
	 *            IResources to checl
	 * @return Multi status with one error message for each missing file.
	 */
	IStatus checkExist(IResource[] resources)
	{
		MultiStatus multiStatus = new MultiStatus(PlatformUI.PLUGIN_ID,
				IStatus.OK, getProblemsMessage(), null);

		for (int i = 0; i < resources.length; i++)
		{
			IResource resource = resources[i];
			if (resource != null && !resource.isVirtual())
			{
				URI location = resource.getLocationURI();
				String message = null;
				if (location != null)
				{
					IFileInfo info = getFileInfo(location);
					if (info == null || info.exists() == false)
					{
						if (resource.isLinked())
						{
							message = NLS
									.bind(Messages.CopyFilesAndFoldersOperation_missingLinkTarget,
											resource.getName());
						}
						else
						{
							message = NLS
									.bind(Messages.CopyFilesAndFoldersOperation_resourceDeleted,
											resource.getName());
						}
					}
				}
				if (message != null)
				{
					IStatus status = new Status(IStatus.ERROR,
							PlatformUI.PLUGIN_ID, IStatus.OK, message, null);
					multiStatus.add(status);
				}
			}
		}
		return multiStatus;
	}

	/**
	 * Return the fileInfo for location. Return <code>null</code> if there is a
	 * CoreException looking it up
	 * 
	 * @param location
	 * @return String or <code>null</code>
	 */
	public static IFileInfo getFileInfo(URI location)
	{
		if (location.getScheme() == null)
			return null;
		IFileStore store = getFileStore(location);
		if (store == null)
		{
			return null;
		}
		return store.fetchInfo();
	}

	/**
	 * Get the file store for the URI.
	 * 
	 * @param uri
	 * @return IFileStore or <code>null</code> if there is a
	 *         {@link CoreException}.
	 */
	public static IFileStore getFileStore(URI uri)
	{
		try
		{
			return EFS.getStore(uri);
		} catch (CoreException e)
		{
			e.printStackTrace();
			// log(e);
			return null;
		}
	}

	/**
	 * Returns the message for this operation's problems dialog.
	 * 
	 * @return the problems message
	 */
	protected String getProblemsMessage()
	{
		return Messages.CopyFilesAndFoldersOperation_problemMessage;
	}

	/**
	 * Returns the title for this operation's problems dialog.
	 * 
	 * @return the problems dialog title
	 */
	protected String getProblemsTitle()
	{
		return Messages.CopyFilesAndFoldersOperation_copyFailedTitle;
	}

	/**
	 * Returns whether the source file in a destination collision will be
	 * validateEdited together with the collision itself. Returns false. Should
	 * return true if the source file is to be deleted after the operation.
	 * 
	 * @return boolean <code>true</code> if the source file in a destination
	 *         collision should be validateEdited. <code>false</code> if only
	 *         the destination should be validated.
	 */
	protected boolean getValidateConflictSource()
	{
		return false;
	}

	/**
	 * Returns whether the given resources are either both linked or both
	 * unlinked.
	 * 
	 * @param source
	 *            source resource
	 * @param destination
	 *            destination resource
	 * @return boolean <code>true</code> if both resources are either linked or
	 *         unlinked. <code>false</code> otherwise.
	 */
	protected boolean homogenousResources(IResource source,
			IResource destination)
	{
		boolean isSourceLinked = source.isLinked();
		boolean isDestinationLinked = destination.isLinked();

		return (isSourceLinked && isDestinationLinked || isSourceLinked == false
				&& isDestinationLinked == false);
	}

	/**
	 * Returns whether the given resource is accessible. Files and folders are
	 * always considered accessible and a project is accessible if it is open.
	 * 
	 * @param resource
	 *            the resource
	 * @return <code>true</code> if the resource is accessible, and
	 *         <code>false</code> if it is not
	 */
	private boolean isAccessible(IResource resource)
	{
		switch (resource.getType())
			{
				case IResource.FILE:
					return true;
				case IResource.FOLDER:
					return true;
				case IResource.PROJECT:
					return ((IProject) resource).isOpen();
				default:
					return false;
			}
	}

	/**
	 * Returns whether any of the given source resources are being recopied to
	 * their current container.
	 * 
	 * @param sourceResources
	 *            the source resources
	 * @param destination
	 *            the destination container
	 * @return <code>true</code> if at least one of the given source resource's
	 *         parent container is the same as the destination
	 */
	boolean isDestinationSameAsSource(IResource[] sourceResources,
			IContainer destination)
	{
		IPath destinationLocation = destination.getLocation();

		for (int i = 0; i < sourceResources.length; i++)
		{
			IResource sourceResource = sourceResources[i];
			if (sourceResource.getParent().equals(destination))
			{
				return true;
			}
			else if (destinationLocation != null)
			{
				// do thorough check to catch linked resources. Fixes bug 29913.
				IPath sourceLocation = sourceResource.getLocation();
				IPath destinationResource = destinationLocation
						.append(sourceResource.getName());
				if (sourceLocation != null
						&& sourceLocation.isPrefixOf(destinationResource))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Copies the given resources to the destination. The current Thread is
	 * halted while the resources are copied using a WorkspaceModifyOperation.
	 * This method should be called from the UIThread.
	 * 
	 * @param resources
	 *            the resources to copy
	 * @param destination
	 *            destination to which resources will be copied
	 * @return IResource[] the resulting {@link IResource}[]
	 * @see WorkspaceModifyOperation
	 * @see Display#getThread()
	 * @see Thread#currentThread()
	 */
	public IResource[] copyResources(final IResource[] resources,
			IContainer destination)
	{
		return copyResources(resources, destination, true);
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
	private IResource[] copyResources(final IResource[] resources,
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
				copyResources(resources, destinationPath, copiedResources,
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

	void copyResources(final IResource[] resources,
			final IPath destinationPath, final IResource[][] copiedResources,
			IProgressMonitor monitor)
	{
		IResource[] copyResources = resources;

		// Fix for bug 31116. Do not provide a task name when
		// creating the task.
		monitor.beginTask("", 100); //$NON-NLS-1$
		monitor.setTaskName(Messages.CopyFilesAndFoldersOperation_operationTitle);
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
				performCopyWithAutoRename(copyResources, destinationPath,
						new SubProgressMonitor(monitor, 90));
			}
			else
			{
				performCopy(copyResources, destinationPath,
						new SubProgressMonitor(monitor, 90));
			}
		}
		monitor.done();
		copiedResources[0] = copyResources;
	}
	
	/**
	 * Copies the given resources to the destination container with the given
	 * name.
	 * <p>
	 * Note: the destination container may need to be created prior to copying
	 * the resources.
	 * </p>
	 * 
	 * @param resources
	 *            the resources to copy
	 * @param destination
	 *            the path of the destination container
	 * @param monitor
	 *            a progress monitor for showing progress and for cancelation
	 * @return <code>true</code> if the copy operation completed without errors
	 */
	private boolean performCopy(IResource[] resources, IPath destination,
			IProgressMonitor monitor)
	{
		try
		{

			List<Object> resourcesAtDestination = new ArrayList<Object>();
			boolean createVirtual = false;
			boolean pathIncludesName = false;
			String relativeToVariable = null;

			AbstractWorkspaceOperation op = getUndoableCopyOrMoveOperation(
					resources, destination);

			if (op instanceof CopyResourcesOperation)
			{				
				CopyResourcesOperation.copy(resources, destination,
						resourcesAtDestination, monitor, pathIncludesName,
						createVirtual, createLinks, relativeToVariable);								
			}
			else
			{
				if (op instanceof MoveResourcesOperation)
				{
					List reverseDestinations = new ArrayList();
					MoveResourcesOperation.move(resources, destination,
							resourcesAtDestination, reverseDestinations,monitor, null,pathIncludesName);				
				}				
			}
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

	/**
	 * Individually copies the given resources to the specified destination
	 * container checking for name collisions. If a collision is detected, it is
	 * saved with a new name.
	 * <p>
	 * Note: the destination container may need to be created prior to copying
	 * the resources.
	 * </p>
	 * 
	 * @param resources
	 *            the resources to copy
	 * @param destination
	 *            the path of the destination container
	 * @return <code>true</code> if the copy operation completed without errors.
	 */
	private boolean performCopyWithAutoRename(IResource[] resources,
			IPath destination, IProgressMonitor monitor)
	{
		IWorkspace workspace = resources[0].getWorkspace();
		IPath[] destinationPaths = new IPath[resources.length];
		try
		{
			for (int i = 0; i < resources.length; i++)
			{
				IResource source = resources[i];
				destinationPaths[i] = destination.append(source.getName());

				if (workspace.getRoot().exists(destinationPaths[i]))
				{
					destinationPaths[i] = getNewNameFor(destinationPaths[i],
							workspace);
				}
			}

			
			
			final CopyResourcesOperation op = new CopyResourcesOperation(resources,
					destinationPaths,
					Messages.CopyFilesAndFoldersOperation_copyTitle);
			//op.setModelProviderIds(getModelProviderIds());
			
			// im Progressmonitor ausfuehren
			op.copy(monitor);
			
			
			
			//System.out.println("Progress OK");
						
						
			/*			
			List<Object> resourcesAtDestination = new ArrayList<Object>();
			boolean createVirtual = false;
			boolean pathIncludesName = false;
			String relativeToVariable = null;

			AbstractWorkspaceOperation op = getUndoableCopyOrMoveOperation(
					resources, destination);

			if (op instanceof CopyResourcesOperation)
			{
				CopyResourcesOperation.copy(resources, destination,
						resourcesAtDestination, monitor, null, pathIncludesName,
						createVirtual, createLinks, relativeToVariable);				
			}
			else
			{
				if (op instanceof MoveResourcesOperation)
				{
					List reverseDestinations = new ArrayList();
					MoveResourcesOperation.move(resources, destination,
							resourcesAtDestination, reverseDestinations,monitor, null,pathIncludesName);				
				}				
			}
			*/
	
			

		} catch (Exception e)
		{
			log.error(e);
			
			recordError((CoreException) e.getCause());
			
			if (e.getCause() instanceof CoreException)
			{
				recordError((CoreException) e.getCause());
			}
			else
			{
				// getNewNameFor() wurde mit Abbruch 'throw new OperationCanceledException()' beendet
				//IDEWorkbenchPlugin.log(e.getMessage(), e);
				displayError(DataTransferMessages.CopyFilesAndFoldersOperation_interruptInfo);
			}
			return false;
			
			 
		}
		return true;
	}
	
	/**
	 * Make an <code>IAdaptable</code> that adapts to the specified shell,
	 * suitable for passing for passing to any
	 * {@link org.eclipse.core.commands.operations.IUndoableOperation} or
	 * {@link org.eclipse.core.commands.operations.IOperationHistory} method
	 * that requires an {@link org.eclipse.core.runtime.IAdaptable}
	 * <code>uiInfo</code> parameter.
	 * 
	 * @param shell
	 *            the shell that should be returned by the IAdaptable when asked
	 *            to adapt a shell. If this parameter is <code>null</code>,
	 *            the returned shell will also be <code>null</code>.
	 * 
	 * @return an IAdaptable that will return the specified shell.
	 */
	public static IAdaptable getUIInfoAdapter(final Shell shell) {
		return new IAdaptable() {
			public Object getAdapter(Class clazz) {
				if (clazz == Shell.class) {
					return shell;
				}
				return null;
			}
		};
	}
	
	/**
	 * Copies the given files and folders to the destination. The current Thread is halted while the
	 * resources are copied using a WorkspaceModifyOperation. This method should be called from the
	 * UI Thread.
	 * 
	 * @param fileNames names of the files to copy
	 * @param destination destination to which files will be copied
	 * @see WorkspaceModifyOperation
	 * @see Display#getThread()
	 * @see Thread#currentThread()
	 * @since 3.2
	 */
	public void copyFiles(final String[] fileNames, IContainer destination)
	{
		IFileStore[] stores = buildFileStores(fileNames);
		if (stores == null)
		{
			return;
		}

		copyFileStores(destination, stores, true, null);
	}
	
	public void copyFileStores(Shell shell, final Map<IProject, String[]>sourceMap)
	{
		WorkspaceModifyOperation op = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor)
			{
				Set<IProject>iProjects = sourceMap.keySet();		
				monitor.beginTask("import data", iProjects.size()); //$NON-NLS-1$
				
				for(IProject iProject : iProjects)
				{
					IFileStore[] stores = buildFileStores(sourceMap.get(iProject));
					if (stores == null)
						continue;
					
					IStatus fileStatus = checkExist(stores);
					if (fileStatus.getSeverity() != IStatus.OK)
					{
						displayError(fileStatus);
						return;
					}
					String errorMsg = validateImportDestinationInternal(iProject, stores);
					if (errorMsg != null)
					{
						displayError(errorMsg);
						return;
					}
					final IPath destinationPath = iProject.getFullPath();

					copyFileStores(stores, destinationPath, monitor);
					
					monitor.worked(1);					
				}
				monitor.done();
				
			}
		};
		try
		{
			new ProgressMonitorDialog(messageShell).run(true, false, op);
			
		} catch (InterruptedException e)
		{
			return;
		} catch (InvocationTargetException exception)
		{
			display(exception);
		}

	}
	
	/**
	 * Copies the given files and folders to the destination.
	 * 
	 * @param stores
	 *            the file stores to copy
	 * @param destination
	 *            destination to which files will be copied
	 */
	private void copyFileStores(IContainer destination,
			final IFileStore[] stores, boolean fork, IProgressMonitor monitor)
	{
		// test files for existence separate from validate API
		// because an external file may not exist until the copy actually
		// takes place (e.g., WinZip contents).
		IStatus fileStatus = checkExist(stores);
		if (fileStatus.getSeverity() != IStatus.OK)
		{
			displayError(fileStatus);
			return;
		}
		String errorMsg = validateImportDestinationInternal(destination, stores);
		if (errorMsg != null)
		{
			displayError(errorMsg);
			return;
		}
		final IPath destinationPath = destination.getFullPath();

		if (fork)
		{
			WorkspaceModifyOperation op = new WorkspaceModifyOperation()
			{
				public void execute(IProgressMonitor monitor)
				{
					copyFileStores(stores, destinationPath, monitor);
				}
			};
			try
			{
				new ProgressMonitorDialog(messageShell).run(true, false, op);
				
			} catch (InterruptedException e)
			{
				return;
			} catch (InvocationTargetException exception)
			{
				display(exception);
			}
		}
		else
		{
			copyFileStores(stores, destinationPath, monitor);
		}

		// If errors occurred, open an Error dialog
		if (errorStatus != null)
		{
			displayError(errorStatus);
			errorStatus = null;
		}
	}
	
	private void copyFileStores(final IFileStore[] stores,
			final IPath destinationPath, IProgressMonitor monitor)
	{
		// Checks only required if this is an exisiting container path.
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if (root.exists(destinationPath))
		{
			IContainer container = (IContainer) root
					.findMember(destinationPath);

			performFileImport(stores, container, monitor);
		}
	}

	/**
	 * Depending on the 'Linked Resources' preferences it copies the given files and folders to the
	 * destination or creates links or shows a dialog that lets the user choose. The current thread
	 * is halted while the resources are copied using a {@link WorkspaceModifyOperation}. This
	 * method should be called from the UI Thread.
	 * 
	 * @param fileNames names of the files to copy
	 * @param destination destination to which files will be copied
	 * @param dropOperation the drop operation ({@link DND#DROP_NONE}, {@link DND#DROP_MOVE}
	 *            {@link DND#DROP_COPY}, {@link DND#DROP_LINK}, {@link DND#DROP_DEFAULT})
	 * @see WorkspaceModifyOperation
	 * @see Display#getThread()
	 * @see Thread#currentThread()
	 * @since 3.6
	 */
	public void copyOrLinkFiles(final String[] fileNames, IContainer destination, int dropOperation) 
	{
		// mode = ImportTypeDialog.IMPORT_COPY - festeingestellt
		copyFiles(fileNames, destination);
		
		// ToDo 
		
		
		/*
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault()
				.getPreferenceStore();
		boolean targetIsVirtual = destination.isVirtual();
		String dndPreference = store
				.getString(targetIsVirtual ? IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_VIRTUAL_FOLDER_MODE
						: IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_MODE);

		int mode = ImportTypeDialog.IMPORT_NONE;
		String variable = null;

		// check if resource linking is disabled
		if (ResourcesPlugin.getPlugin().getPluginPreferences()
				.getBoolean(ResourcesPlugin.PREF_DISABLE_LINKING))
			mode = ImportTypeDialog.IMPORT_COPY;
		else
		{
			if (dndPreference
					.equals(IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_MODE_PROMPT))
			{
				ImportTypeDialog dialog = new ImportTypeDialog(messageShell,
						dropOperation, fileNames, destination);
				dialog.setResource(destination);
				if (dialog.open() == Window.OK)
				{
					mode = dialog.getSelection();
					variable = dialog.getVariable();
				}
			}
			else if (dndPreference
					.equals(IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_MODE_MOVE_COPY))
			{
				mode = ImportTypeDialog.IMPORT_COPY;
			}
			else if (dndPreference
					.equals(IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_MODE_LINK))
			{
				mode = ImportTypeDialog.IMPORT_LINK;
			}
			else if (dndPreference
					.equals(IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_MODE_LINK_AND_VIRTUAL_FOLDER))
			{
				mode = ImportTypeDialog.IMPORT_VIRTUAL_FOLDERS_AND_LINKS;
			}
		}

		switch (mode)
			{
				case ImportTypeDialog.IMPORT_COPY:
					copyFiles(fileNames, destination);
					break;
				case ImportTypeDialog.IMPORT_VIRTUAL_FOLDERS_AND_LINKS:
					if (variable != null)
						setRelativeVariable(variable);
					createVirtualFoldersAndLinks(fileNames, destination);
					break;
				case ImportTypeDialog.IMPORT_LINK:
					if (variable != null)
						setRelativeVariable(variable);
					linkFiles(fileNames, destination);
					break;
				case ImportTypeDialog.IMPORT_NONE:
					break;
			}
			*/

	}
	
	/**
	 * Removes the given resource from the workspace.
	 * 
	 * @param resource
	 *            resource to remove from the workspace
	 * @param monitor
	 *            a progress monitor for showing progress and for cancelation
	 * @return true the resource was deleted successfully false the resource was
	 *         not deleted because a CoreException occurred
	 */
	boolean delete(IResource resource, IProgressMonitor monitor)
	{
		boolean force = false; // don't force deletion of out-of-sync resources

		if (resource.getType() == IResource.PROJECT)
		{
			// if it's a project, ask whether content should be deleted too
			IProject project = (IProject) resource;
			try
			{
				project.delete(true, force, monitor);
			} catch (CoreException e)
			{
				recordError(e); // log error
				return false;
			}
		}
		else
		{
			// if it's not a project, just delete it
			int flags = IResource.KEEP_HISTORY;
			if (force)
			{
				flags = flags | IResource.FORCE;
			}
			try
			{
				resource.delete(flags, monitor);
			} catch (CoreException e)
			{
				recordError(e); // log error
				return false;
			}
		}
		return true;
	}

	
	/**
	 * Checks whether the destination is valid for copying the source file
	 * stores.
	 * <p>
	 * Note this method is for internal use only. It is not API.
	 * </p>
	 * <p>
	 * TODO Bug 117804. This method has been renamed to avoid a bug in the
	 * Eclipse compiler with regards to visibility and type resolution when
	 * linking.
	 * </p>
	 * 
	 * @param destination
	 *            the destination container
	 * @param sourceStores
	 *            the source IFileStore
	 * @return an error message, or <code>null</code> if the path is valid
	 */
	private String validateImportDestinationInternal(IContainer destination,
			IFileStore[] sourceStores)
	{
		if (!isAccessible(destination))
			return Messages.CopyFilesAndFoldersOperation_destinationAccessError;

		if (!destination.isVirtual())
		{
			IFileStore destinationStore;
			try
			{
				destinationStore = EFS.getStore(destination.getLocationURI());
			} catch (CoreException exception)
			{

				/*
				 * ToDo log
				 */

				exception.printStackTrace();

				// IDEWorkbenchPlugin.log(exception.getLocalizedMessage(),
				// exception);

				return NLS.bind(
						Messages.CopyFilesAndFoldersOperation_internalError,
						exception.getLocalizedMessage());
			}
			for (int i = 0; i < sourceStores.length; i++)
			{
				IFileStore sourceStore = sourceStores[i];
				IFileStore sourceParentStore = sourceStore.getParent();

				if (sourceStore != null)
				{
					if (destinationStore.equals(sourceStore)
							|| (sourceParentStore != null && destinationStore
									.equals(sourceParentStore)))
					{
						return NLS
								.bind(Messages.CopyFilesAndFoldersOperation_importSameSourceAndDest,
										sourceStore.getName());
					}
					// work around bug 16202. replacement for
					// sourcePath.isPrefixOf(destinationPath)
					if (sourceStore.isParentOf(destinationStore))
					{
						return Messages.CopyFilesAndFoldersOperation_destinationDescendentError;
					}
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Performs an import of the given stores into the provided container.
	 * Returns a status indicating if the import was successful.
	 * 
	 * @param stores
	 *            stores that are to be imported
	 * @param target
	 *            container to which the import will be done
	 * @param monitor
	 *            a progress monitor for showing progress and for cancelation
	 */
	private void performFileImport(IFileStore[] stores, IContainer target,
			IProgressMonitor monitor)
	{
		IOverwriteQuery query = new IOverwriteQuery()
		{
			public String queryOverwrite(String pathString)
			{
				if (alwaysOverwrite)
				{
					return ALL;
				}

				final String returnCode[] =
					{ CANCEL };
				final String msg = NLS
						.bind(Messages.CopyFilesAndFoldersOperation_overwriteQuestion,
								pathString);
				final String[] options =
					{ IDialogConstants.YES_LABEL,
							IDialogConstants.YES_TO_ALL_LABEL,
							IDialogConstants.NO_LABEL,
							IDialogConstants.CANCEL_LABEL };
				messageShell.getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						MessageDialog dialog = new MessageDialog(messageShell,
								Messages.CopyFilesAndFoldersOperation_question,
								null, msg, MessageDialog.QUESTION, options, 0)
						{
							protected int getShellStyle()
							{
								return super.getShellStyle() | SWT.SHEET;
							}
						};
						dialog.open();
						int returnVal = dialog.getReturnCode();
						String[] returnCodes =
							{ YES, ALL, NO, CANCEL };
						returnCode[0] = returnVal == -1 ? CANCEL
								: returnCodes[returnVal];
					}
				});
				if (returnCode[0] == ALL)
				{
					alwaysOverwrite = true;
				}
				else if (returnCode[0] == CANCEL)
				{
					canceled = true;
				}
				return returnCode[0];
			}
		};

		ImportOperation op = new ImportOperation(target.getFullPath(),
				stores[0].getParent(), FileStoreStructureProvider.INSTANCE,
				query, Arrays.asList(stores));
		op.setContext(messageShell);
		op.setCreateContainerStructure(false);
		op.setVirtualFolders(createVirtualFoldersAndLinks);
		op.setCreateLinks(createLinks);
		op.setRelativeVariable(relativeVariable);
		try
		{
			op.run(monitor);
		} catch (InterruptedException e)
		{
			return;
		} catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof CoreException)
			{
				displayError(((CoreException) e.getTargetException())
						.getStatus());
			}
			else
			{
				display(e);
			}
			return;
		}
		// Special case since ImportOperation doesn't throw a CoreException on
		// failure.
		IStatus status = op.getStatus();
		if (!status.isOK())
		{
			if (errorStatus == null)
			{
				errorStatus = new MultiStatus(PlatformUI.PLUGIN_ID,
						IStatus.ERROR, getProblemsMessage(), null);
			}
			errorStatus.merge(status);
		}
	}

	
	/**
	 * Build the collection of fileStores that map to fileNames. If any of them
	 * cannot be found then match then return null.
	 * 
	 * @param fileNames
	 * @return IFileStore[]
	 */
	private IFileStore[] buildFileStores(final String[] fileNames)
	{
		IFileStore[] stores = new IFileStore[fileNames.length];
		for (int i = 0; i < fileNames.length; i++)
		{
			IFileStore store = getFileStore(fileNames[i]);
			if (store == null)
			{
				reportFileInfoNotFound(fileNames[i]);
				return null;
			}
			stores[i] = store;
		}
		return stores;
	}
	
	/**
	 * Report that a file info could not be found.
	 * 
	 * @param fileName
	 */
	private void reportFileInfoNotFound(final String fileName)
	{
		messageShell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				ErrorDialog.openError(
						messageShell,
						getProblemsTitle(),
						NLS.bind(
								Messages.CopyFilesAndFoldersOperation_infoNotFound,
								fileName), null);
			}
		});
	}


	/**
	 * Returns a new name for a copy of the resource at the given path in the
	 * given workspace.
	 * 
	 * @param originalName
	 *            the full path of the resource
	 * @param workspace
	 *            the workspace
	 * @return the new full path for the copy, or <code>null</code> if the
	 *         resource should not be copied
	 */
	private IPath getNewNameFor(final IPath originalName,
			final IWorkspace workspace)
	{
		final IResource resource = workspace.getRoot().findMember(originalName);
		final IPath prefix = resource.getFullPath().removeLastSegments(1);
		final String returnValue[] =
			{ "" }; //$NON-NLS-1$

		messageShell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				IInputValidator validator = new IInputValidator()
				{
					public String isValid(String string)
					{
						if (resource.getName().equals(string))
						{
							return Messages.CopyFilesAndFoldersOperation_nameMustBeDifferent;
						}
						IStatus status = workspace.validateName(string,
								resource.getType());
						if (!status.isOK())
						{
							return status.getMessage();
						}
						if (workspace.getRoot().exists(prefix.append(string)))
						{
							return Messages.CopyFilesAndFoldersOperation_nameExists;
						}
						return null;
					}
				};

				InputDialog dialog = new InputDialog(
						messageShell,
						Messages.CopyFilesAndFoldersOperation_inputDialogTitle,
						NLS.bind(
								Messages.CopyFilesAndFoldersOperation_inputDialogMessage,
								resource.getName()), getAutoNewNameFor(
								originalName, workspace).lastSegment()
								.toString(), validator);
				dialog.setBlockOnOpen(true);
				dialog.open();
				if (dialog.getReturnCode() == Window.CANCEL)
				{
					returnValue[0] = null;
				}
				else
				{
					returnValue[0] = dialog.getValue();
				}
			}
		});
		if (returnValue[0] == null)
		{
			throw new OperationCanceledException();
		}
		return prefix.append(returnValue[0]);
	}

	/**
	 * Sets the content of the existing file to the source file content.
	 * 
	 * @param source
	 *            source file to copy
	 * @param existing
	 *            existing file to set the source content in
	 * @param subMonitor
	 *            a progress monitor for showing progress and for cancelation
	 * @throws CoreException
	 *             setContents failed
	 */
	private void copyExisting(IResource source, IResource existing,
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
			}
		}
	}

	/**
	 * Checks whether the destination is valid for copying the source resources.
	 * <p>
	 * Note this method is for internal use only. It is not API.
	 * </p>
	 * 
	 * @param destination
	 *            the destination container
	 * @param sourceResources
	 *            the source resources
	 * @return an error message, or <code>null</code> if the path is valid
	 */
	public String validateDestination(IContainer destination,
			IResource[] sourceResources)
	{
		if (!isAccessible(destination))
		{
			return Messages.CopyFilesAndFoldersOperation_destinationAccessError;
		}
		IContainer firstParent = null;
		URI destinationLocation = destination.getLocationURI();
		for (int i = 0; i < sourceResources.length; i++)
		{
			IResource sourceResource = sourceResources[i];
			if (firstParent == null)
			{
				firstParent = sourceResource.getParent();
			}
			else if (firstParent.equals(sourceResource.getParent()) == false)
			{
				// Resources must have common parent. Fixes bug 33398.
				return Messages.CopyFilesAndFoldersOperation_parentNotEqual;
			}

			// verify that if the destination is a virtual folder, the resource
			// must be
			// either a link or another virtual folder
			if (destination.isVirtual())
			{
				if (!sourceResource.isLinked() && !sourceResource.isVirtual()
						&& !createLinks && !createVirtualFoldersAndLinks)
				{
					return NLS
							.bind(Messages.CopyFilesAndFoldersOperation_sourceCannotBeCopiedIntoAVirtualFolder,
									sourceResource.getName());
				}
			}
			URI sourceLocation = sourceResource.getLocationURI();
			if (sourceLocation == null)
			{
				if (sourceResource.isLinked())
				{
					// Don't allow copying linked resources with undefined path
					// variables. See bug 28754.
					return NLS
							.bind(Messages.CopyFilesAndFoldersOperation_missingPathVariable,
									sourceResource.getName());
				}
				return NLS.bind(
						Messages.CopyFilesAndFoldersOperation_resourceDeleted,
						sourceResource.getName());

			}
			if (!destination.isVirtual())
			{
				if (sourceLocation.equals(destinationLocation))
				{
					return NLS
							.bind(Messages.CopyFilesAndFoldersOperation_sameSourceAndDest,
									sourceResource.getName());
				}
				// is the source a parent of the destination?
				if (new Path(sourceLocation.toString()).isPrefixOf(new Path(
						destinationLocation.toString())))
				{
					return Messages.CopyFilesAndFoldersOperation_destinationDescendentError;
				}
			}

			String linkedResourceMessage = validateLinkedResource(destination,
					sourceResource);
			if (linkedResourceMessage != null)
			{
				return linkedResourceMessage;
			}
		}
		return null;
	}

	/**
	 * Check if the destination is valid for the given source resource.
	 * 
	 * @param destination
	 *            destination container of the operation
	 * @param source
	 *            source resource
	 * @return String error message or null if the destination is valid
	 */
	private String validateLinkedResource(IContainer destination,
			IResource source)
	{
		if ((source.isLinked() == false) || source.isVirtual())
		{
			return null;
		}
		IWorkspace workspace = destination.getWorkspace();
		IResource linkHandle = createLinkedResourceHandle(destination, source);
		IStatus locationStatus = workspace.validateLinkLocationURI(linkHandle,
				source.getRawLocationURI());

		if (locationStatus.getSeverity() == IStatus.ERROR)
		{
			return locationStatus.getMessage();
		}
		IPath sourceLocation = source.getLocation();
		if (source.getProject().equals(destination.getProject()) == false
				&& source.getType() == IResource.FOLDER
				&& sourceLocation != null)
		{
			// prevent merging linked folders that point to the same
			// file system folder
			try
			{
				IResource[] members = destination.members();
				for (int j = 0; j < members.length; j++)
				{
					if (sourceLocation.equals(members[j].getLocation())
							&& source.getName().equals(members[j].getName()))
					{
						return NLS
								.bind(Messages.CopyFilesAndFoldersOperation_sameSourceAndDest,
										source.getName());
					}
				}
			} catch (CoreException exception)
			{
				displayError(NLS.bind(
						Messages.CopyFilesAndFoldersOperation_internalError,
						exception.getMessage()));
			}
		}
		return null;
	}

	/**
	 * Returns whether moving all of the given source resources to the given
	 * destination container could be done without causing name collisions.
	 * 
	 * @param destination
	 *            the destination container
	 * @param sourceResources
	 *            the list of resources
	 * @return <code>true</code> if there would be no name collisions, and
	 *         <code>false</code> if there would
	 */
	protected IResource[] validateNoNameCollisions(IContainer destination,
			IResource[] sourceResources)
	{
		List copyItems = new ArrayList();
		IWorkspaceRoot workspaceRoot = destination.getWorkspace().getRoot();
		int overwrite = IDialogConstants.NO_ID;

		// Check to see if we would be overwriting a parent folder.
		// Cancel entire copy operation if we do.
		for (int i = 0; i < sourceResources.length; i++)
		{
			final IResource sourceResource = sourceResources[i];
			final IPath destinationPath = destination.getFullPath().append(
					sourceResource.getName());
			final IPath sourcePath = sourceResource.getFullPath();

			IResource newResource = workspaceRoot.findMember(destinationPath);
			if (newResource != null && destinationPath.isPrefixOf(sourcePath))
			{
				displayError(NLS.bind(
						Messages.CopyFilesAndFoldersOperation_overwriteProblem,
						destinationPath, sourcePath));

				canceled = true;
				return null;
			}
		}
		// Check for overwrite conflicts
		for (int i = 0; i < sourceResources.length; i++)
		{
			final IResource source = sourceResources[i];
			final IPath destinationPath = destination.getFullPath().append(
					source.getName());

			IResource newResource = workspaceRoot.findMember(destinationPath);
			if (newResource != null)
			{
				if (overwrite != IDialogConstants.YES_TO_ALL_ID
						|| (newResource.getType() == IResource.FOLDER && homogenousResources(
								source, destination) == false))
				{
					overwrite = checkOverwrite(source, newResource);
				}
				if (overwrite == IDialogConstants.YES_ID
						|| overwrite == IDialogConstants.YES_TO_ALL_ID)
				{
					copyItems.add(source);
				}
				else if (overwrite == IDialogConstants.CANCEL_ID)
				{
					canceled = true;
					return null;
				}
			}
			else
			{
				copyItems.add(source);
			}
		}
		return (IResource[]) copyItems.toArray(new IResource[copyItems.size()]);
	}

	/**
	 * Validates that the given source resources can be copied to the
	 * destination as decided by the VCM provider.
	 * 
	 * @param destination
	 *            copy destination
	 * @param sourceResources
	 *            source resources
	 * @return <code>true</code> all files passed validation or there were no
	 *         files to validate. <code>false</code> one or more files did not
	 *         pass validation.
	 */
	protected boolean validateEdit(IContainer destination,
			IResource[] sourceResources)
	{
		ArrayList copyFiles = new ArrayList();

		collectExistingReadonlyFiles(destination.getFullPath(),
				sourceResources, copyFiles);
		if (copyFiles.size() > 0)
		{
			IFile[] files = (IFile[]) copyFiles.toArray(new IFile[copyFiles
					.size()]);
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IStatus status = workspace.validateEdit(files, messageShell);

			canceled = status.isOK() == false;
			return status.isOK();
		}
		return true;
	}
	
	/**
	 * Checks whether the destination is valid for copying the source files.
	 * <p>
	 * Note this method is for internal use only. It is not API.
	 * </p>
	 * 
	 * @param destination
	 *            the destination container
	 * @param sourceNames
	 *            the source file names
	 * @return an error message, or <code>null</code> if the path is valid
	 */
	public String validateImportDestination(IContainer destination,
			String[] sourceNames) {

		IFileStore[] stores = new IFileStore[sourceNames.length];
		for (int i = 0; i < sourceNames.length; i++) {
			IFileStore store = getFileStore(sourceNames[i]);
			if (store == null) {
				return NLS
						.bind(
								DataTransferMessages.CopyFilesAndFoldersOperation_infoNotFound,
								sourceNames[i]);
			}
			stores[i] = store;
		}
		return validateImportDestinationInternal(destination, stores);

	}

	/**
	 * Recursively collects existing files in the specified destination path.
	 * 
	 * @param destinationPath
	 *            destination path to check for existing files
	 * @param copyResources
	 *            resources that may exist in the destination
	 * @param existing
	 *            holds the collected existing files
	 */
	private void collectExistingReadonlyFiles(IPath destinationPath,
			IResource[] copyResources, ArrayList existing)
	{
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		for (int i = 0; i < copyResources.length; i++)
		{
			IResource source = copyResources[i];
			IPath newDestinationPath = destinationPath.append(source.getName());
			IResource newDestination = workspaceRoot
					.findMember(newDestinationPath);
			IFolder folder;

			if (newDestination == null)
			{
				continue;
			}
			folder = getFolder(newDestination);
			if (folder != null)
			{
				IFolder sourceFolder = getFolder(source);

				if (sourceFolder != null)
				{
					try
					{
						collectExistingReadonlyFiles(newDestinationPath,
								sourceFolder.members(), existing);
					} catch (CoreException exception)
					{
						recordError(exception);
					}
				}
			}
			else
			{
				IFile file = getFile(newDestination);

				if (file != null)
				{
					if (file.isReadOnly())
					{
						existing.add(file);
					}
					if (getValidateConflictSource())
					{
						IFile sourceFile = getFile(source);
						if (sourceFile != null)
						{
							existing.add(sourceFile);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the resource either casted to or adapted to an IFolder.
	 * 
	 * @param resource
	 *            resource to cast/adapt
	 * @return the resource either casted to or adapted to an IFolder.
	 *         <code>null</code> if the resource does not adapt to IFolder
	 */
	protected IFolder getFolder(IResource resource)
	{
		if (resource instanceof IFolder)
		{
			return (IFolder) resource;
		}
		return (IFolder) ((IAdaptable) resource).getAdapter(IFolder.class);
	}

	/**
	 * Check if the user wishes to overwrite the supplied resource or all
	 * resources.
	 * 
	 * @param source
	 *            the source resource
	 * @param destination
	 *            the resource to be overwritten
	 * @return one of IDialogConstants.YES_ID, IDialogConstants.YES_TO_ALL_ID,
	 *         IDialogConstants.NO_ID, IDialogConstants.CANCEL_ID indicating
	 *         whether the current resource or all resources can be overwritten,
	 *         or if the operation should be canceled.
	 */
	private int checkOverwrite(final IResource source,
			final IResource destination)
	{
		final int[] result = new int[1];

		// Dialogs need to be created and opened in the UI thread
		Runnable query = new Runnable()
		{
			public void run()
			{
				String message;
				int resultId[] =
					{ IDialogConstants.YES_ID, IDialogConstants.YES_TO_ALL_ID,
							IDialogConstants.NO_ID, IDialogConstants.CANCEL_ID };
				String labels[] = new String[]
					{ IDialogConstants.YES_LABEL,
							IDialogConstants.YES_TO_ALL_LABEL,
							IDialogConstants.NO_LABEL,
							IDialogConstants.CANCEL_LABEL };

				if (destination.getType() == IResource.FOLDER)
				{
					if (homogenousResources(source, destination))
					{
						message = NLS
								.bind(Messages.CopyFilesAndFoldersOperation_overwriteMergeQuestion,
										destination.getFullPath()
												.makeRelative());
					}
					else
					{
						if (destination.isLinked())
						{
							message = NLS
									.bind(Messages.CopyFilesAndFoldersOperation_overwriteNoMergeLinkQuestion,
											destination.getFullPath()
													.makeRelative());
						}
						else
						{
							message = NLS
									.bind(Messages.CopyFilesAndFoldersOperation_overwriteNoMergeNoLinkQuestion,
											destination.getFullPath()
													.makeRelative());
						}
						resultId = new int[]
							{ IDialogConstants.YES_ID, IDialogConstants.NO_ID,
									IDialogConstants.CANCEL_ID };
						labels = new String[]
							{ IDialogConstants.YES_LABEL,
									IDialogConstants.NO_LABEL,
									IDialogConstants.CANCEL_LABEL };
					}
				}
				else
				{
					String[] bindings = new String[]
						{ getLocationText(destination),
								getDateStringValue(destination),
								getLocationText(source),
								getDateStringValue(source) };
					message = NLS
							.bind(Messages.CopyFilesAndFoldersOperation_overwriteWithDetailsQuestion,
									bindings);
				}
				MessageDialog dialog = new MessageDialog(messageShell,
						Messages.CopyFilesAndFoldersOperation_resourceExists,
						null, message, MessageDialog.QUESTION, labels, 0)
				{
					protected int getShellStyle()
					{
						return super.getShellStyle() | SWT.SHEET;
					}
				};
				dialog.open();
				if (dialog.getReturnCode() == SWT.DEFAULT)
				{
					// A window close returns SWT.DEFAULT, which has to be
					// mapped to a cancel
					result[0] = IDialogConstants.CANCEL_ID;
				}
				else
				{
					result[0] = resultId[dialog.getReturnCode()];
				}
			}
		};
		messageShell.getDisplay().syncExec(query);
		return result[0];
	}

	/**
	 * Creates a file or folder handle for the source resource as if it were to
	 * be created in the destination container.
	 * 
	 * @param destination
	 *            destination container
	 * @param source
	 *            source resource
	 * @return IResource file or folder handle, depending on the source type.
	 */
	IResource createLinkedResourceHandle(IContainer destination,
			IResource source)
	{
		IWorkspace workspace = destination.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IPath linkPath = destination.getFullPath().append(source.getName());
		IResource linkHandle;

		if (source.getType() == IResource.FOLDER)
		{
			linkHandle = workspaceRoot.getFolder(linkPath);
		}
		else
		{
			linkHandle = workspaceRoot.getFile(linkPath);
		}
		return linkHandle;
	}

	/**
	 * Returns the resource either casted to or adapted to an IFile.
	 * 
	 * @param resource
	 *            resource to cast/adapt
	 * @return the resource either casted to or adapted to an IFile.
	 *         <code>null</code> if the resource does not adapt to IFile
	 */
	protected IFile getFile(IResource resource)
	{
		if (resource instanceof IFile)
		{
			return (IFile) resource;
		}
		return (IFile) ((IAdaptable) resource).getAdapter(IFile.class);
	}
	
	

	private static String NOT_LOCAL_TEXT = Messages.ResourceInfo_notLocal;

	private static String UNKNOWN_LABEL = Messages.ResourceInfo_unknown;

	private static String MISSING_PATH_VARIABLE_TEXT = Messages.ResourceInfo_undefinedPathVariable;

	private static String NOT_EXIST_TEXT = Messages.ResourceInfo_notExist;

	private static String VIRTUAL_FOLDER_TEXT = Messages.ResourceInfo_isVirtualFolder;

	private static String FILE_NOT_EXIST_TEXT = Messages.ResourceInfo_fileNotExist;

	/**
	 * Return the value for the date String for the timestamp of the supplied
	 * resource.
	 * 
	 * @param resource
	 *            The resource to query
	 * @return String
	 */
	public static String getDateStringValue(IResource resource)
	{
		if (!resource.isLocal(IResource.DEPTH_ZERO))
		{
			return NOT_LOCAL_TEXT;
		}

		// don't access the file system for closed projects (bug 151089)
		if (!isProjectAccessible(resource))
		{
			return UNKNOWN_LABEL;
		}

		URI location = resource.getLocationURI();
		if (location == null)
		{
			if (resource.isLinked())
			{
				return MISSING_PATH_VARIABLE_TEXT;
			}
			return NOT_EXIST_TEXT;
		}

		IFileInfo info = getFileInfo(location);
		if (info == null)
		{
			return UNKNOWN_LABEL;
		}

		if (info.exists())
		{
			DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG,
					DateFormat.MEDIUM);
			return format.format(new Date(info.getLastModified()));
		}
		return NOT_EXIST_TEXT;
	}

	/**
	 * Get the location of a resource
	 * 
	 * @param resource
	 * @return String the text to display the location
	 */
	public static String getLocationText(IResource resource)
	{
		if (resource.isVirtual())
			return VIRTUAL_FOLDER_TEXT;
		if (!resource.isLocal(IResource.DEPTH_ZERO))
		{
			return NOT_LOCAL_TEXT;
		}

		URI resolvedLocation = resource.getLocationURI();
		URI location = resolvedLocation;
		boolean isLinked = resource.isLinked();
		if (isLinked)
		{
			location = resource.getRawLocationURI();
		}
		if (location == null)
		{
			return NOT_EXIST_TEXT;
		}

		if (resolvedLocation.getScheme() == null)
			return location.toString();

		IFileStore store = getFileStore(resolvedLocation);
		// don't access the file system for closed projects (bug 151089)
		boolean isPathVariable = isPathVariable(resource);
		if (isProjectAccessible(resource) && resolvedLocation != null
				&& !isPathVariable)
		{
			// No path variable used. Display the file not exist message
			// in the location. Fixes bug 33318.
			if (store == null)
			{
				return UNKNOWN_LABEL;
			}
			if (!store.fetchInfo().exists())
			{
				return NLS.bind(FILE_NOT_EXIST_TEXT, store.toString());
			}
		}
		if (isLinked && isPathVariable)
		{
			String tmp = URIUtil.toPath(resource.getRawLocationURI())
					.toOSString();
			return resource.getPathVariableManager()
					.convertToUserEditableFormat(tmp, true);
		}
		if (store != null)
		{
			return store.toString();
		}
		return location.toString();
	}

	/**
	 * Returns whether the given resource is a linked resource bound to a path
	 * variable.
	 * 
	 * @param resource
	 *            resource to test
	 * @return boolean <code>true</code> the given resource is a linked resource
	 *         bound to a path variable. <code>false</code> the given resource
	 *         is either not a linked resource or it is not using a path
	 *         variable.
	 */
	private static boolean isPathVariable(IResource resource)
	{
		if (!resource.isLinked())
		{
			return false;
		}

		URI resolvedLocation = resource.getLocationURI();
		if (resolvedLocation == null)
		{
			// missing path variable
			return true;
		}
		URI rawLocation = resource.getRawLocationURI();
		if (resolvedLocation.equals(rawLocation))
		{
			return false;
		}

		return true;
	}

	/**
	 * Returns whether the resource's project is available
	 */
	private static boolean isProjectAccessible(IResource resource)
	{
		IProject project = resource.getProject();
		return project != null && project.isAccessible();
	}

	protected void display(InvocationTargetException e)
	{
		// CoreExceptions are collected above, but unexpected runtime
		// exceptions and errors may still occur.

		/*
		 * IDEWorkbenchPlugin.getDefault().getLog().log(
		 * StatusUtil.newStatus(IStatus.ERROR, MessageFormat.format(
		 * "Exception in {0}.performCopy(): {1}", //$NON-NLS-1$ new Object[] {
		 * getClass().getName(), e.getTargetException() }), null));
		 */
		displayError(NLS.bind(
				Messages.CopyFilesAndFoldersOperation_internalError, e
						.getTargetException().getMessage()));
	}

	/**
	 * Display the supplied status in an error dialog.
	 * 
	 * @param status
	 *            The status to display
	 */
	protected void displayError(final IStatus status)
	{
		messageShell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				ErrorDialog.openError(messageShell, getProblemsTitle(), null,
						status);
			}
		});
	}

	/**
	 * Opens an error dialog to display the given message.
	 * 
	 * @param message
	 *            the error message to show
	 */
	protected void displayError(final String message)
	{
		messageShell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				MessageDialog.openError(messageShell, getProblemsTitle(),
						message);
			}
		});
	}

	/**
	 * Records the core exception to be displayed to the user once the action is
	 * finished.
	 * 
	 * @param error
	 *            a <code>CoreException</code>
	 */
	protected void recordError(CoreException error)
	{
		if (errorStatus == null)
		{
			errorStatus = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.ERROR,
					getProblemsMessage(), error);
		}

		errorStatus.merge(error.getStatus());
	}
	
	/**
	 * Get the file store for the local file system path.
	 * 
	 * @param string
	 * @return IFileStore or <code>null</code> if there is a
	 *         {@link CoreException}.
	 */
	public static IFileStore getFileStore(String string)
	{
		Path location = new Path(string);
		// see if there is an existing resource at that location that might have
		// a different file store
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(location);
		if (file != null)
		{
			return getFileStore(file.getLocationURI());
		}
		return getFileStore(location.toFile().toURI());
	}
	
	/**
	 * Set a variable relative to which the links are created
	 * 
	 * @param variable
	 * @since 3.6
	 */
	public void setRelativeVariable(String variable)
	{
		relativeVariable = variable;
	}
	
	/**
	 * Set whether or not links will be created under the destination container.
	 * 
	 * @param value
	 * @since 3.6
	 */
	public void setCreateLinks(boolean value)
	{
		createLinks = value;
	}
	
	/**
	 * Set whether or not virtual folders and links will be created under the destination
	 * container.
	 * 
	 * @param value
	 * @since 3.6
	 */
	public void setVirtualFolders(boolean value)
	{
		createVirtualFoldersAndLinks = value;
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
	 * @return the operation that should be used to perform the move or cop
	 * @since 3.3
	 */
	protected AbstractWorkspaceOperation getUndoableCopyOrMoveOperation(
			IResource[] resources, IPath destinationPath) {
		return new CopyResourcesOperation(resources, destinationPath,
				Messages.CopyFilesAndFoldersOperation_copyTitle);

	}
}
