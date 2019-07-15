package it.naturtalent.application.dialogs;

import it.naturtalent.application.Activator;
import it.naturtalent.application.IShowViewAdapter;
import it.naturtalent.application.IShowViewAdapterRepository;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * Mit einem entsprechenden Adapter kann die ViewID der ausgewaehlten View ermittelt und angezeigt werden.
 * Diese Adapter (IShowViewAdapter) werden in einem zentralen Repository gespeichert.
 * 
 * @author dieter
 *
 */
public class ShowViewDialog extends Dialog
{
	private static class ViewerLabelProvider extends LabelProvider
	{
		public Image getImage(Object element)
		{
			if (element instanceof IShowViewAdapter)
			{
				IShowViewAdapter adapter = (IShowViewAdapter) element;
				return adapter.getImage();				
			}
			
			if(element instanceof String)
				return Icon.DIALOG_NEW_FOLDER.getImage(IconSize._16x16_DefaultIconSize);				

			return super.getImage(element);
		}

		public String getText(Object element)
		{
			if (element instanceof String)
				return (String) element;
			
			if (element instanceof IShowViewAdapter)
			{
				IShowViewAdapter adapter = (IShowViewAdapter) element;
				return adapter.getLabel();				
			}
			
			return super.getText(element);

		}
	}
	private static class TreeContentProvider implements ITreeContentProvider
	{
		Map<String, List<IShowViewAdapter>>showViewAdapters;
		
		
		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			showViewAdapters = null;
			
			if(newInput instanceof Map)
				showViewAdapters = (Map<String, List<IShowViewAdapter>>) newInput;
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			if(showViewAdapters != null)				
				return showViewAdapters.keySet().toArray(new String[0]);
			
			return new Object[0];

		}

		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof String)
			{
				List<IShowViewAdapter>adapters = showViewAdapters.get(parentElement);						
				return adapters.toArray(new IShowViewAdapter[adapters.size()]); 
			}
			
			return new Object[0];
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			return getChildren(element).length > 0;
		}
	}
	
	private Text txtSeek;
	private TreeViewer treeViewer;	
	private Button button;
	
	private String selectectViewID;
	
	// Repository mit den ShowView - Adapter
	@Inject @Optional private IShowViewAdapterRepository showViewAdapterRepository;


	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	@Inject
	public ShowViewDialog(Shell parentShell)
	{
		super(parentShell);		
	}
	
	@Override
	protected void configureShell(Shell newShell)
	{	
		super.configureShell(newShell);
		newShell.setText(Activator.properties.getProperty(Activator.PROPERTY_SHOWVIEW));
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		txtSeek = new Text(container, SWT.BORDER);
		txtSeek.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new TreeColumnLayout());
		
		treeViewer = new TreeViewer(composite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		treeViewer.setLabelProvider(new ViewerLabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				button.setEnabled(false);
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selObj = selection.getFirstElement();
				if (selObj instanceof IShowViewAdapter)
					button.setEnabled(true);
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{				
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selObj = selection.getFirstElement();
				if (selObj instanceof String)
				{
					TreePath tp = ((TreeSelection) treeViewer.getSelection())
							.getPaths()[0];
					if (treeViewer.getExpandedState(tp))
						treeViewer.collapseToLevel(tp, 1);
					else
						treeViewer.expandToLevel(tp, 1);
				}
				else
				{
					if (selObj instanceof IShowViewAdapter)
					{
						okPressed();
					}
				}
			}
		});

		if (showViewAdapterRepository != null)
		{
			Map<String, List<IShowViewAdapter>> adapters = showViewAdapterRepository
					.getShowViewAdaptersMap();
			treeViewer.setInput(adapters);
		}

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		button.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(288, 474);
	}

	@Override
	protected void okPressed()
	{
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if((selection != null) && (selection.getFirstElement() instanceof IShowViewAdapter))
		{
			IShowViewAdapter adapter = (IShowViewAdapter) selection.getFirstElement();
			selectectViewID = adapter.partID();			
		}
		
		super.okPressed();
	}
	
	public String getSelectectViewID()
	{
		return selectectViewID;
	}

}
