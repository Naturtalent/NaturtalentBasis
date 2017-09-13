package it.naturtalent.e4.update.dialogs;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.dialogs.IUDetailsGroup;
import org.eclipse.equinox.internal.p2.ui.model.AvailableIUElement;
import org.eclipse.equinox.internal.p2.ui.model.AvailableUpdateElement;
import org.eclipse.equinox.internal.p2.ui.model.ElementUtils;
import org.eclipse.equinox.internal.p2.ui.model.IUElementListRoot;
import org.eclipse.equinox.internal.p2.ui.viewers.IUDetailsLabelProvider;
import org.eclipse.equinox.internal.p2.ui.viewers.ProvElementContentProvider;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProfileChangeOperation;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class SelectableIUsPage extends TitleAreaDialog
{

	private IUElementListRoot root;	
	private CheckboxTableViewer tableViewer;
	private ProfileChangeOperation  resolvedOperation;
	private IUDetailsGroup iuDetailsGroup;
	private IStatus couldNotResolveStatus = Status.OK_STATUS; // we haven't tried and failed
	private ProfileChangeOperation operation;
	
	private SashForm sashForm;
	private Table table;
	private ProvElementContentProvider contentProvider;
	private IUDetailsLabelProvider labelProvider;
	private Object [] initialSelections;
	private Object [] resultSelections;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectableIUsPage(Shell parentShell)
	{
		super(parentShell);
		root = new IUElementListRoot();
		initialSelections = new IInstallableUnit[0];
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);		
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		contentProvider = new ProvElementContentProvider();
		tableViewer.setContentProvider(contentProvider);
		labelProvider = new IUDetailsLabelProvider(null, ProvUI.getIUColumnConfig(), getShell());
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setInput(root);
		//setInitialCheckState();
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 300);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.internal.p2.ui.dialogs.ResolutionStatusPage#updateCaches(org.eclipse.equinox.internal.p2.ui.model.IUElementListRoot, org.eclipse.equinox.p2.operations.ProfileChangeOperation)
	 */
	public void updateCaches(IUElementListRoot newRoot)
	{
		if (newRoot != null && root != newRoot)
		{
			root = newRoot;
			if (tableViewer != null)
				tableViewer.setInput(newRoot);
		}
	}
	


	protected void setInitialCheckState()
	{
		if (initialSelections == null)
		{
			return;
		}

		ArrayList<Object> selections = new ArrayList<Object>(
				initialSelections.length);

		for (int i = 0; i < initialSelections.length; i++)
		{
			if (initialSelections[i] instanceof AvailableUpdateElement)
			{
				AvailableUpdateElement element = (AvailableUpdateElement) initialSelections[i];
				if (element.isLockedForUpdate())
				{
					continue;
				}
			}
			selections.add(initialSelections[i]);
		}		
	}

	private void setDetailText(ProfileChangeOperation resolvedOperation) {
		String detail = null;
		Object selectedElement = getSelectedElement();
		IInstallableUnit selectedIU = ElementUtils.elementToIU(selectedElement);
		IUDetailsGroup detailsGroup = getDetailsGroup();

		// We either haven't resolved, or we failed to resolve and reported some error
		// while doing so.  
		if (resolvedOperation == null || !resolvedOperation.hasResolved() || statusOverridesOperation()) {
			// See if the wizard status knows something more about it.
			IStatus currentStatus = getCurrentStatus();
			if (!currentStatus.isOK()) {
				detail = currentStatus.getMessage();
				detailsGroup.enablePropertyLink(false);
			} else if (selectedIU != null) {
				detail = getIUDescription(selectedElement);
				detailsGroup.enablePropertyLink(true);
			} else {
				detail = ""; //$NON-NLS-1$
				detailsGroup.enablePropertyLink(false);
			}
			detailsGroup.setDetailText(detail);
			return;
		}

		// An IU is selected and we have resolved.  Look for information about the specific IU.
		if (selectedIU != null) {
			detail = resolvedOperation.getResolutionDetails(selectedIU);
			if (detail != null) {
				detailsGroup.enablePropertyLink(false);
				detailsGroup.setDetailText(detail);
				return;
			}
			// No specific error about this IU.  Show the overall error if it is in error.
			if (resolvedOperation.getResolutionResult().getSeverity() == IStatus.ERROR) {
				detail = resolvedOperation.getResolutionDetails();
				if (detail != null) {
					detailsGroup.enablePropertyLink(false);
					detailsGroup.setDetailText(detail);
					return;
				}
			}

			// The overall status is not an error, or else there was no explanatory text for an error.
			// We may as well just show info about this iu.
			detailsGroup.enablePropertyLink(true);
			detailsGroup.setDetailText(getIUDescription(selectedElement));
			return;
		}

		//No IU is selected, give the overall report
		detail = resolvedOperation.getResolutionDetails();
		detailsGroup.enablePropertyLink(false);
		if (detail == null)
			detail = ""; //$NON-NLS-1$
		detailsGroup.setDetailText(detail);
	}
	
	protected String getIUDescription(Object element)
	{
		if (element instanceof AvailableIUElement)
		{
			return getIUDescription((AvailableIUElement) element);
		}
		IInstallableUnit selectedIU = ElementUtils.elementToIU(element);
		if (selectedIU != null)
		{
			return getIUDescription(selectedIU);
		}
		return ""; //$NON-NLS-1$
	}

	protected IUDetailsGroup getDetailsGroup()
	{
		return iuDetailsGroup;
	}
	
	private Object getSelectedElement()
	{
		Object[] elements = getSelectedElements();
		if (elements.length == 0)
			return null;
		return elements[0];
	}

	public Object[] getSelectedElements()
	{		
		return resultSelections;
	}
	
	
	
	@Override
	protected void okPressed()
	{
		resultSelections = tableViewer.getCheckedElements();		
		super.okPressed();
	}

	public IStatus getCurrentStatus()
	{
		if (statusOverridesOperation())
			return couldNotResolveStatus;
		if (operation != null && operation.getResolutionResult() != null)
			return operation.getResolutionResult();
		return couldNotResolveStatus;
	}
	
	/*
	 * Return a boolean indicating whether the wizard's current status should override any detail
	 * reported by the operation.
	 */
	public boolean statusOverridesOperation()
	{
		return false;
	}

}
