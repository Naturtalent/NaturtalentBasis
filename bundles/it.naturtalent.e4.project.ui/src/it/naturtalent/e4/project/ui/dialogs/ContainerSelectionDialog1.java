package it.naturtalent.e4.project.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.e4.project.ui.filters.ClosedProjectFilter;
import it.naturtalent.e4.project.ui.filters.HiddenResourceFilter;
import it.naturtalent.e4.project.ui.filters.ResourceFilterProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.DoubleClickEvent;

public class ContainerSelectionDialog1 extends TitleAreaDialog
{
	
	private TreeViewer treeViewer;
	
	// Last selection made by user
	private IContainer selectedContainer;


	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ContainerSelectionDialog1(Shell parentShell)
	{
		super(parentShell);
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
		
		treeViewer = new TreeViewer(container, SWT.BORDER);
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();				
				selectedContainer = (IContainer) selection
						.getFirstElement();
				okPressed();
			}
		});
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());		
		ResourceFilterProvider filterProvider = new ResourceFilterProvider();
		filterProvider.addFilter(new ClosedProjectFilter());
		filterProvider.addFilter(new HiddenResourceFilter());
		filterProvider.addFilter(new ViewerFilter()
		{			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{
				if (element instanceof IResource)
				{
					IResource resource = (IResource) element;
					if((resource.getType() & (IResource.PROJECT | IResource.FOLDER)) != 0)
						return true;			
				}
				return false;
			}
		});
		treeViewer.setFilters(filterProvider.getFilters());
		treeViewer.setInput(ResourcesPlugin.getWorkspace());
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();				
				selectedContainer = (IContainer) selection
						.getFirstElement();
			}
		});
		
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
		return new Point(450, 779);
	}
	
	/**
	 * Sets the selected existing container.
	 * 
	 * @param container
	 */
	public void setSelectedContainer(IContainer container)
	{
		selectedContainer = container;

		// expand to and select the specified container
		List itemsToExpand = new ArrayList();
		IContainer parent = container.getParent();
		while (parent != null)
		{
			itemsToExpand.add(0, parent);
			parent = parent.getParent();
		}
		treeViewer.setExpandedElements(itemsToExpand.toArray());
		treeViewer.setSelection(new StructuredSelection(container), true);
	}

	public IContainer getSelectedContainer()
	{
		return selectedContainer;
	}
	
	

}
