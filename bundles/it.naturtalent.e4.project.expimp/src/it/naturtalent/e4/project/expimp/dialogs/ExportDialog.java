package it.naturtalent.e4.project.expimp.dialogs;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import it.naturtalent.e4.project.IExportAdapter;
import it.naturtalent.e4.project.expimp.ExpImpProcessor;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.DoubleClickEvent;

import it.naturtalent.e4.project.expimp.Messages;

/**
 * Zentraler Import Dialog
 * 
 * @author apel.dieter
 *
 */
public class ExportDialog extends TitleAreaDialog
{
	
	private TreeViewer treeViewer;
	
	
	private static class TreeContentProvider implements ITreeContentProvider
	{
		Map<String, List<IExportAdapter>>exportAdapters;
		
		
		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			exportAdapters = null;
			
			if(newInput instanceof Map)
				exportAdapters = (Map<String, List<IExportAdapter>>) newInput;
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object inputElement)
		{		
			if(exportAdapters != null)				
				return exportAdapters.keySet().toArray(new String[0]);
			
			return new Object[0];
		}

		public Object[] getChildren(Object parentElement)
		{			
			if (parentElement instanceof String)
			{
				List<IExportAdapter>adapters = exportAdapters.get(parentElement);						
				return adapters.toArray(new IExportAdapter[adapters.size()]); 
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

	private static class ViewerLabelProvider extends LabelProvider
	{
		public Image getImage(Object element)
		{
			if (element instanceof String)
				return SWTResourceManager.getImage(this.getClass(), "/icons/full/obj16/fldr_obj.gif"); //$NON-NLS-1$

			
			if (element instanceof IExportAdapter)
			{
				IExportAdapter adapter = (IExportAdapter) element;
				return adapter.getImage();				
			}

			return super.getImage(element);
		}

		public String getText(Object element)
		{
			if (element instanceof String)
				return (String) element;
			
			if (element instanceof IExportAdapter)
			{
				IExportAdapter adapter = (IExportAdapter) element;
				return adapter.getLabel();				
			}
			
			return super.getText(element);
		}
	}
	
	@Inject @Optional static Shell shell;
	@Inject @Optional private IEclipseContext context;


	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public ExportDialog()
	{
		super(shell);
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ExportDialog(Shell parentShell)
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
		setMessage(Messages.ExportDialog_this_message);
		setTitle(Messages.ExportDialog_this_title);
		setTitleImage(SWTResourceManager.getImage(ExportDialog.class, "/icons/full/wizban/export_wiz.png"));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		treeViewer = new TreeViewer(container, SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				Object selObj = selection.getFirstElement();
				if(selObj instanceof IExportAdapter)
					setMessage(((IExportAdapter)selObj).getMessage());
				else setMessage(Messages.ExportDialog_this_message);					
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				okPressed();
			}
		});
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new ViewerLabelProvider());
		
		Map<String, List<IExportAdapter>>adapters =  ExpImpProcessor.exportAdapterRepository.getExportAdaptersMap();
		treeViewer.setInput(adapters);

		return area;
	}
	
	private void executeHandler()
	{
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		Object selObj = selection.getFirstElement();
		if (selObj instanceof IExportAdapter)
		{
			IExportAdapter adapter = (IExportAdapter) selObj;		
			if(context != null)
				ContextInjectionFactory.make(adapter.getExportAction().getClass(), context).run();
			else
				adapter.getExportAction().run();
		}
	}
	
	

	@Override
	protected void okPressed()
	{
		executeHandler();		
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
		return new Point(450, 538);
	}

}
