package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.IModelProviderDescriptor;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.resources.mapping.ModelProvider;
import org.eclipse.core.resources.mapping.ModelStatus;
import org.eclipse.core.resources.mapping.ResourceChangeValidator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;




/**
 * An AbstractWorkspaceOperation represents an undoable operation that affects
 * the workspace. It handles common workspace operation activities such as
 * tracking which resources are affected by an operation, prompting the user
 * when there are possible side effects of operations, building execution
 * exceptions from core exceptions, etc. Clients may call the public API from a
 * background thread.
 * <p>
 * This class is not intended to be subclassed by clients.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.3
 */
public abstract class AbstractWorkspaceOperation
{

	private static String ELLIPSIS = "..."; //$NON-NLS-1$

	protected static int EXECUTE = 1;

	protected static int UNDO = 2;

	protected static int REDO = 3;

	protected IResource[] resources;

	private boolean isValid = true;

	
	
	
	public AbstractWorkspaceOperation()
	{
		super();
		// TODO Auto-generated constructor stub
	}


	/*
	 * Specifies whether any user prompting is appropriate while computing
	 * status.
	 */
	protected boolean quietCompute = false;

	String[] modelProviderIds;

	private String label;

	/**
	 * Set the ids of any model providers for the resources involved.
	 * 
	 * @param ids
	 *            the array of String model provider ids that provide models
	 *            associated with the resources involved in this operation
	 */
	public void setModelProviderIds(String[] ids)
	{
		modelProviderIds = ids;
	}

	/**
	 * Set the resources which are affected by this operation
	 * 
	 * @param resources
	 *            an array of resources
	 */
	protected void setTargetResources(IResource[] resources)
	{
		this.resources = resources;
	}

	/**
	 * Return the workspace manipulated by this operation.
	 * 
	 * @return the IWorkspace used by this operation.
	 */
	protected IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Return the workspace rule factory associated with this operation.
	 * 
	 * @return the IResourceRuleFactory associated with this operation.
	 */
	protected IResourceRuleFactory getWorkspaceRuleFactory()
	{
		return getWorkspace().getRuleFactory();
	}

	/**
	 * Mark this operation invalid due to some external change. May be used by
	 * subclasses.
	 * 
	 */
	protected void markInvalid()
	{
		isValid = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation checks a validity flag.
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#canExecute()
	 */
	public boolean canExecute()
	{
		return isValid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation checks a validity flag.
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#canUndo()
	 */
	public boolean canUndo()
	{
		return isValid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation checks a validity flag.
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#canRedo()
	 */
	public boolean canRedo()
	{
		return isValid();
	}

	/**
	 * Execute the specified operation. This implementation executes the
	 * operation in a workspace runnable and catches any CoreExceptions
	 * resulting from the operation. Unhandled CoreExceptions are propagated as
	 * ExecutionExceptions.
	 * 
	 * @param monitor
	 *            the progress monitor to use for the operation
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @return the IStatus of the execution. The status severity should be set
	 *         to <code>OK</code> if the operation was successful, and
	 *         <code>ERROR</code> if it was not. Any other status is assumed to
	 *         represent an incompletion of the execution.
	 * @throws ExecutionException
	 *             if an exception occurred during execution.
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */

	/*
	public IStatus execute(IProgressMonitor monitor, final IAdaptable uiInfo)
			throws ExecutionException
	{
		try
		{
			getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor) throws CoreException
				{
					doExecute(monitor, uiInfo);
				}
			}, getExecuteSchedulingRule(), IWorkspace.AVOID_UPDATE, monitor);
		} catch (final CoreException e)
		{
			throw new ExecutionException(
					NLS.bind(
							DataTransferMessages.AbstractWorkspaceOperation_ExecuteErrorTitle,
							getLabel()), e);
		}
		isValid = true;
		return Status.OK_STATUS;
	}
	*/

	/**
	 * Redo the specified operation. This implementation redoes the operation in
	 * a workspace runnable and catches any CoreExceptions resulting from the
	 * operation. Unhandled CoreExceptions are propagated as
	 * ExecutionExceptions.
	 * 
	 * @param monitor
	 *            the progress monitor to use for the operation
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @return the IStatus of the redo. The status severity should be set to
	 *         <code>OK</code> if the operation was successful, and
	 *         <code>ERROR</code> if it was not. Any other status is assumed to
	 *         represent an incompletion of the redo.
	 * @throws ExecutionException
	 *             if an exception occurred during execution.
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#redo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	/*
	public IStatus redo(IProgressMonitor monitor, final IAdaptable uiInfo)
			throws ExecutionException
	{
		try
		{
			getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor) throws CoreException
				{
					doExecute(monitor, uiInfo);
				}
			}, getRedoSchedulingRule(), IWorkspace.AVOID_UPDATE, monitor);
		} catch (final CoreException e)
		{
			throw new ExecutionException(
					NLS.bind(
							DataTransferMessages.AbstractWorkspaceOperation_RedoErrorTitle,
							getLabel()), e);

		}
		isValid = true;
		return Status.OK_STATUS;
	}
	*/

	/**
	 * Undo the specified operation. This implementation undoes the operation in
	 * a workspace runnable and catches any CoreExceptions resulting from the
	 * operation. Unhandled CoreExceptions are propagated as
	 * ExecutionExceptions.
	 * 
	 * @param monitor
	 *            the progress monitor to use for the operation
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @return the IStatus of the undo. The status severity should be set to
	 *         <code>OK</code> if the operation was successful, and
	 *         <code>ERROR</code> if it was not. Any other status is assumed to
	 *         represent an incompletion of the undo. *
	 * @throws ExecutionException
	 *             if an exception occurred during execution.
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	/*
	public IStatus undo(IProgressMonitor monitor, final IAdaptable uiInfo)
			throws ExecutionException
	{
		try
		{
			getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor) throws CoreException
				{
					doUndo(monitor, uiInfo);
				}
			}, getUndoSchedulingRule(), IWorkspace.AVOID_UPDATE, monitor);
		} catch (final CoreException e)
		{
			throw new ExecutionException(
					NLS.bind(
							DataTransferMessages.AbstractWorkspaceOperation_UndoErrorTitle,
							getLabel()), e);

		}
		isValid = true;
		return Status.OK_STATUS;
	}
	*/


	/**
	 * Return whether the proposed operation is valid. The default
	 * implementation simply checks to see if the flag has been marked as
	 * invalid, relying on subclasses to mark the flag invalid when appropriate.
	 * 
	 * @return the validity flag
	 */
	protected boolean isValid()
	{
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.operations.IAdvancedUndoableOperation#aboutToNotify
	 * (org.eclipse.core.commands.operations.OperationHistoryEvent)
	 */
	public void aboutToNotify(OperationHistoryEvent event)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.IAdvancedUndoableOperation#
	 * getAffectedObjects()
	 */
	public Object[] getAffectedObjects()
	{
		return resources;
	}

	/**
	 * Update the provided resource change description factory so it can
	 * generate a resource delta describing the result of an undo or redo.
	 * Return a boolean indicating whether any update was done. The default
	 * implementation does not update the factory. Subclasses are expected to
	 * override this method to more specifically describe their modifications to
	 * the workspace.
	 * 
	 * @param factory
	 *            the factory to update
	 * @param operation
	 *            an integer indicating whether the change is part of an
	 *            execute, undo, or redo
	 * @return a boolean indicating whether the factory was updated.
	 */
	protected boolean updateResourceChangeDescriptionFactory(
			IResourceChangeDescriptionFactory factory, int operation)
	{
		return false;
	}

	/**
	 * Return an error status describing an invalid operation using the provided
	 * message.
	 * 
	 * @param message
	 *            the message to be used in the status, or <code>null</code> if
	 *            a generic message should be used
	 * @return the error status
	 */
	protected IStatus getErrorStatus(String message)
	{
		String statusMessage = message;
		if (statusMessage == null)
		{
			statusMessage = NLS
					.bind(DataTransferMessages.AbstractWorkspaceOperation_ErrorInvalidMessage,
							getLabel());
		}
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				OperationStatus.OPERATION_INVALID, statusMessage, null);
	}

	/**
	 * Return a warning status describing the warning state of an operation
	 * using the provided message and code.
	 * 
	 * @param message
	 *            the message to be used in the status, or <code>null</code> if
	 *            a generic message should be used
	 * @param code
	 *            the integer code to be assigned to the status
	 * @return the warning status
	 */
	protected IStatus getWarningStatus(String message, int code)
	{
		String statusMessage = message;
		if (statusMessage == null)
		{
			statusMessage = NLS
					.bind(DataTransferMessages.AbstractWorkspaceOperation_GenericWarningMessage,
							getLabel());
		}
		return new Status(IStatus.WARNING, Activator.PLUGIN_ID, code,
				statusMessage, null);
	}

	/**
	 * Return whether the resources known by this operation currently exist.
	 * 
	 * @return <code>true</code> if there are existing resources and
	 *         <code>false</code> if there are no known resources or any one of
	 *         them does not exist
	 */
	protected boolean resourcesExist()
	{
		if (resources == null || resources.length == 0)
		{
			return false;
		}
		for (int i = 0; i < resources.length; i++)
		{
			if (resources[i] == null || !resources[i].exists())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Return whether the resources known by this operation contain any
	 * projects.
	 * 
	 * @return <code>true</code> if there is one or more projects known by this
	 *         operation and false if there are no projects.
	 */
	protected boolean resourcesIncludesProjects()
	{
		if (resources == null || resources.length == 0)
		{
			return false;
		}
		for (int i = 0; i < resources.length; i++)
		{
			if (resources[i].getType() == IResource.PROJECT)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a scheduling rule appropriate for executing this operation.
	 * 
	 * The default implementation is to return a rule that locks out the entire
	 * workspace. Subclasses are encouraged to provide more specific rules that
	 * affect only their resources.
	 * 
	 * @return the scheduling rule to use when executing this operation, or
	 *         <code>null</code> if there are no scheduling restrictions for
	 *         this operation.
	 * 
	 * @see IWorkspace#run(IWorkspaceRunnable, ISchedulingRule, int,
	 *      IProgressMonitor)
	 */
	protected ISchedulingRule getExecuteSchedulingRule()
	{
		return getWorkspace().getRoot();
	}

	/**
	 * Return a scheduling rule appropriate for undoing this operation.
	 * 
	 * The default implementation is to return a rule that locks out the entire
	 * workspace. Subclasses are encouraged to provide more specific rules that
	 * affect only their resources.
	 * 
	 * @return the scheduling rule to use when undoing this operation, or
	 *         <code>null</code> if there are no scheduling restrictions for
	 *         this operation.
	 * 
	 * @see IWorkspace#run(IWorkspaceRunnable, ISchedulingRule, int,
	 *      IProgressMonitor)
	 */
	protected ISchedulingRule getUndoSchedulingRule()
	{
		return getWorkspace().getRoot();
	}

	/**
	 * Return a scheduling rule appropriate for redoing this operation.
	 * 
	 * The default implementation considers the redo scheduling rule the same as
	 * the original execution scheduling rule.
	 * 
	 * @return the scheduling rule to use when redoing this operation, or
	 *         <code>null</code> if there are no scheduling restrictions for
	 *         this operation.
	 * 
	 * @see IWorkspace#run(IWorkspaceRunnable, ISchedulingRule, int,
	 *      IProgressMonitor)
	 */
	protected ISchedulingRule getRedoSchedulingRule()
	{
		return getExecuteSchedulingRule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.IAdvancedUndoableOperation2#
	 * setQuietCompute(boolean)
	 */
	public void setQuietCompute(boolean quiet)
	{
		quietCompute = quiet;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer text = new StringBuffer(super.toString());
		text.append("\n"); //$NON-NLS-1$
		text.append(this.getClass().getName());
		appendDescriptiveText(text);
		return text.toString();
	}

	/**
	 * Append any descriptive text to the specified string buffer to be shown in
	 * the receiver's {@link #toString()} text.
	 * <p>
	 * Note that this method is not intend to be subclassed by clients.
	 * 
	 * @param text
	 *            the StringBuffer on which to append the text
	 */
	protected void appendDescriptiveText(StringBuffer text)
	{
		text.append(" resources: "); //$NON-NLS-1$
		text.append(resources);
		text.append('\'');
	}

	/**
	 * Return the shell described by the specified adaptable, or the active
	 * shell if no shell has been specified in the adaptable.
	 * 
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * 
	 * @return the shell specified in the adaptable, or the active shell if no
	 *         shell has been specified
	 * 
	 */
	protected Shell getShell(IAdaptable uiInfo)
	{
		if (uiInfo != null)
		{
			Shell shell = (Shell) uiInfo.getAdapter(Shell.class);
			if (shell != null)
			{
				return shell;
			}
		}
		return Display.getDefault().getActiveShell();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.IAdvancedUndoableOperation2#
	 * runInBackground()
	 */
	public boolean runInBackground()
	{
		return true;
	}

	/**
	 * Set the label of the operation to the specified name.
	 * 
	 * @param name
	 *            the string to be used for the label. Should never be
	 *            <code>null</code>.
	 */
	public void setLabel(String name)
	{
		label = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#getLabel()
	 * <p> Default implementation. Subclasses may override this method. </p>
	 */
	public String getLabel()
	{
		return label;
	}
	
	/**
	 * Return a status indicating the projected outcome of undoing the receiver.
	 * This method is not called by the operation history, but instead is used
	 * by clients (such as implementers of
	 * {@link org.eclipse.core.commands.operations.IOperationApprover2}) who
	 * wish to perform advanced validation of an operation before attempting to
	 * undo it.
	 * 
	 * If an ERROR status is returned, the undo will not proceed and the user
	 * notified if deemed necessary by the caller. The validity flag on the
	 * operation should be marked as invalid. If an OK status is returned, the
	 * undo will proceed. The caller must interpret any other returned status
	 * severity, and may choose to prompt the user as to how to proceed.
	 * 
	 * If there are multiple conditions that result in an ambiguous status
	 * severity, it is best for the implementor of this method to consult the
	 * user as to how to proceed for each one, and return an OK or ERROR status
	 * that accurately reflects the user's wishes, or to return a multi-status
	 * that accurately describes all of the issues at hand, so that the caller
	 * may potentially consult the user. (Note that the user should not be
	 * consulted at all if a client has called {@link #setQuietCompute(boolean)}
	 * with a value of <code>true</code>.)
	 * 
	 * This implementation computes the validity of undo by computing the
	 * resource delta that would be generated on undo, and checking whether any
	 * registered model providers are affected by the operation.
	 * 
	 * @param monitor
	 *            the progress monitor to be used for computing the status
	 * @return the status indicating the projected outcome of undoing the
	 *         receiver
	 * 
	 * @see org.eclipse.core.commands.operations.IAdvancedUndoableOperation#computeUndoableStatus(org.eclipse.core.runtime.IProgressMonitor)
	 * @see #setQuietCompute(boolean)
	 */
	public IStatus computeUndoableStatus(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		// If we are not to prompt the user, nothing to do.
		if (quietCompute) {
			return status;
		}

		IResourceChangeDescriptionFactory factory = ResourceChangeValidator
				.getValidator().createDeltaFactory();
		if (updateResourceChangeDescriptionFactory(factory, UNDO)) {
			boolean proceed = promptToConfirm(getShell(null),"side effects","label",null,new String[]{""}, true /* syncExec */);
			if (!proceed) {
				status = Status.CANCEL_STATUS;
			}
		}
		return status;

	}
	
	/**
	 * Prompt the user to inform them of the possible side effects of an
	 * operation on resources. Do not prompt for side effects from ignored model
	 * providers. A model provider can be ignored if it is the client calling
	 * this API. Any message from the provided model provider id or any model
	 * providers it extends will be ignored.
	 * 
	 * @param shell
	 *            the shell to parent the prompt dialog
	 * @param title
	 *            the title of the dialog
	 * @param message
	 *            the message for the dialog
	 * @param delta
	 *            a delta built using an
	 *            {@link IResourceChangeDescriptionFactory}
	 * @param ignoreModelProviderIds
	 *            model providers to be ignored
	 * @param syncExec
	 *            prompt in a sync exec (required when called from a non-UI
	 *            thread)
	 * @return whether the user chose to continue
	 * @since 3.2
	 */
	public static boolean promptToConfirm(final Shell shell,
			final String title, String message, IResourceDelta delta,
			String[] ignoreModelProviderIds, boolean syncExec) {
		IStatus status = ResourceChangeValidator.getValidator().validateChange(
				delta, null);
		if (status.isOK()) {
			return true;
		}
		final IStatus displayStatus;
		if (status.isMultiStatus()) {
			List result = new ArrayList();
			IStatus[] children = status.getChildren();
			for (int i = 0; i < children.length; i++) {
				IStatus child = children[i];
				if (!isIgnoredStatus(child, ignoreModelProviderIds)) {
					result.add(child);
				}
			}
			if (result.isEmpty()) {
				return true;
			}
			if (result.size() == 1) {
				displayStatus = (IStatus) result.get(0);
			} else {
				displayStatus = new MultiStatus(status.getPlugin(), status
						.getCode(), (IStatus[]) result
						.toArray(new IStatus[result.size()]), status
						.getMessage(), status.getException());
			}
		} else {
			if (isIgnoredStatus(status, ignoreModelProviderIds)) {
				return true;
			}
			displayStatus = status;
		}

		if (message == null) {
			message = "Potential side effects have been identified";
		}
		final String dialogMessage = NLS.bind(Messages.IDE_areYouSure, message);

		final boolean[] result = new boolean[] { false };
		Runnable runnable = new Runnable() {
			public void run() {
				ErrorDialog dialog = new ErrorDialog(shell, title,
						dialogMessage, displayStatus, IStatus.ERROR
								| IStatus.WARNING | IStatus.INFO) {
					protected void createButtonsForButtonBar(Composite parent) {
						createButton(parent, IDialogConstants.YES_ID,
								IDialogConstants.YES_LABEL, false);
						createButton(parent, IDialogConstants.NO_ID,
								IDialogConstants.NO_LABEL, true);
						createDetailsButton(parent);
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.jface.dialogs.ErrorDialog#buttonPressed(int)
					 */
					protected void buttonPressed(int id) {
						if (id == IDialogConstants.YES_ID) {
							super.buttonPressed(IDialogConstants.OK_ID);
						} else if (id == IDialogConstants.NO_ID) {
							super.buttonPressed(IDialogConstants.CANCEL_ID);
						}
						super.buttonPressed(id);
					}
					protected int getShellStyle() {
						return super.getShellStyle() | SWT.SHEET;
					}
				};
				int code = dialog.open();
				result[0] = code == 0;
			}
		};
		if (syncExec) {
			shell.getDisplay().syncExec(runnable);
		} else {
			runnable.run();
		}
		return result[0];
	}

	private static boolean isIgnoredStatus(IStatus status,
			String[] ignoreModelProviderIds) {
		if (ignoreModelProviderIds == null) {
			return false;
		}
		if (status instanceof ModelStatus) {
			ModelStatus ms = (ModelStatus) status;
			for (int i = 0; i < ignoreModelProviderIds.length; i++) {
				String id = ignoreModelProviderIds[i];
				if (ms.getModelProviderId().equals(id)) {
					return true;
				}
				IModelProviderDescriptor desc = ModelProvider
						.getModelProviderDescriptor(id);
				String[] extended = desc.getExtendedModels();
				if (isIgnoredStatus(status, extended)) {
					return true;
				}
			}
		}
		return false;
	}


}
