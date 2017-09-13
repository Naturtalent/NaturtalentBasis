package it.naturtalent.e4.update.dialogs;

import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.ProvUIProvisioningListener;
import org.eclipse.equinox.internal.p2.ui.model.ProfileElement;
import org.eclipse.equinox.internal.p2.ui.viewers.DeferredQueryContentProvider;
import org.eclipse.equinox.internal.p2.ui.viewers.IUColumnConfig;
import org.eclipse.equinox.internal.p2.ui.viewers.IUComparator;
import org.eclipse.equinox.internal.p2.ui.viewers.IUDetailsLabelProvider;
import org.eclipse.equinox.internal.p2.ui.viewers.ProvElementComparer;
import org.eclipse.equinox.internal.p2.ui.viewers.StructuredViewerProvisioningListener;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;

public class SelectInstalledIUDialog extends TitleAreaDialog
{

	private static final String PROFILE_ID = "DefaultProfile";
	
	private IUColumnConfig[] columnConfig = ProvUI.getIUColumnConfig();
	
	private ProvisioningUI ui = ProvisioningUI.getDefaultUI();

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectInstalledIUDialog(Shell parentShell, ProvisioningUI ui)
	{
		super(parentShell);
		
		if(ui != null)
			this.ui = ui;
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
		
		TreeViewer installedIUViewer = new TreeViewer(container, SWT.BORDER);
		Tree tree = installedIUViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		// Filters and sorters before establishing content, so we don't refresh unnecessarily.
		IUComparator comparator = new IUComparator(IUComparator.IU_NAME);
		comparator.useColumnConfig(columnConfig);
		installedIUViewer.setComparator(comparator);
		installedIUViewer.setComparer(new ProvElementComparer());
		
		// Now the content.
		installedIUViewer.setContentProvider(new DeferredQueryContentProvider());

		// Now the visuals, columns before labels.
		setTreeColumns(installedIUViewer.getTree());
		installedIUViewer.setLabelProvider(new IUDetailsLabelProvider(null, columnConfig, null));

		// Input last.
		installedIUViewer.setInput(getInput());

		final StructuredViewerProvisioningListener listener = new StructuredViewerProvisioningListener(
				getClass().getName(), installedIUViewer,
				ProvUIProvisioningListener.PROV_EVENT_IU
						| ProvUIProvisioningListener.PROV_EVENT_PROFILE,
				ui.getOperationRunner());
		ProvUI.getProvisioningEventBus(ui.getSession()).addListener(listener);
		installedIUViewer.getControl().addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				ProvUI.getProvisioningEventBus(ui.getSession()).removeListener(
						listener);
			}
		});

		return area;
	}
	
	Object getInput()
	{
		ProfileElement element = new ProfileElement(null, PROFILE_ID);
		return element;
	}
	
	private void setTreeColumns(Tree tree)
	{
		IUColumnConfig[] columns = columnConfig;
		tree.setHeaderVisible(true);

		for (int i = 0; i < columns.length; i++)
		{
			TreeColumn tc = new TreeColumn(tree, SWT.NONE, i);
			tc.setResizable(true);
			tc.setText(columns[i].getColumnTitle());
			tc.setWidth(columns[i].getWidthInPixels(tree));
		}
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
	
	

}
