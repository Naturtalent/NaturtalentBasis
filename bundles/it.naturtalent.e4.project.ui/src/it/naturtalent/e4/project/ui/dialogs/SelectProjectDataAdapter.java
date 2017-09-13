package it.naturtalent.e4.project.ui.dialogs;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.ui.Activator;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;
import it.naturtalent.e4.project.ui.Messages;
import org.eclipse.swt.widgets.Label;

public class SelectProjectDataAdapter extends TitleAreaDialog
{
	
	public class AdapterFilter extends ViewerFilter
	{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{
			if (element instanceof IProjectDataAdapter)
			{
				IProjectDataAdapter adapter = (IProjectDataAdapter) element;
				if(adapter.getName().equals(IProjectDataAdapter.DEFAULTPROJECTADAPTERNAME))
					return false;
			}
			return true;
		}

	}

	private class TableLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			IProjectDataAdapter adapter = (IProjectDataAdapter) element;
			return adapter.getName();
		}
	}
	
	private static class ContentProvider implements IStructuredContentProvider
	{
		
		private IProjectDataAdapter[] adapters = null;
		
		
		public Object[] getElements(Object inputElement)
		{			
			return adapters;
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			adapters = null;
			if(newInput instanceof List)
			{
				List<IProjectDataAdapter>listAdapter = (List<IProjectDataAdapter>) newInput;
				adapters = listAdapter.toArray(new IProjectDataAdapter[listAdapter.size()]);
			}			
		}
	}
	
	
	
	
	private Table table;
	
	private CheckboxTableViewer checkboxTableViewer;
	
	private IProjectDataAdapter[] selectResult;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectProjectDataAdapter(Shell parentShell)
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
		setMessage(Messages.SelectProjectDataAdapter_this_message);
		setTitle(Messages.SelectProjectDataAdapter_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		
		Label lblAdapter = new Label(area, SWT.NONE);
		lblAdapter.setText(Messages.SelectProjectDataAdapter_lblAdapter_text);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = checkboxTableViewer.getTable();
		checkboxTableViewer.setLabelProvider(new TableLabelProvider());
		checkboxTableViewer.setContentProvider(new ContentProvider());
		
		ViewerFilter [] filters = new ViewerFilter[]{new AdapterFilter()};  
		checkboxTableViewer.setFilters(filters);
		
		checkboxTableViewer.setInput(Activator.projectDataAdapterRegister.getProjectDataAdapters());

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
		return new Point(450, 605);
	}

	@Override
	protected void okPressed()
	{
		Object[] result = checkboxTableViewer.getCheckedElements();	
		selectResult = new IProjectDataAdapter[result.length];
		System.arraycopy(result, 0, selectResult, 0,
				result.length);
		
		// sicherstellen, dass DefaultAdapter enthalten ist
		List <IProjectDataAdapter> adapters = (List<IProjectDataAdapter>) checkboxTableViewer.getInput();
		for(IProjectDataAdapter adapter : adapters)
		{
			if(adapter.getName().equals(IProjectDataAdapter.DEFAULTPROJECTADAPTERNAME))
			{
				if(!ArrayUtils.contains(selectResult, adapter))
					selectResult = ArrayUtils.add(selectResult, adapter);
				break;
			}
		}
		
		super.okPressed();
	}

	public void setSelectAdapters(IProjectDataAdapter[] adapters)
	{
		if((checkboxTableViewer != null) && (ArrayUtils.isNotEmpty(adapters)))		
			checkboxTableViewer.setCheckedElements(adapters);		
	}

	public IProjectDataAdapter[] getSelectResult()
	{
		return selectResult;
	}
	
	

}
