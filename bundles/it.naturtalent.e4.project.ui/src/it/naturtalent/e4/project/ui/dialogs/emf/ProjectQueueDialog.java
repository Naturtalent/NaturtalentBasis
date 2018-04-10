package it.naturtalent.e4.project.ui.dialogs.emf;



import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.utils.ProjectQueue;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

/**
 * Mit dem Dialog kann ein Project aus dem ProjectQueue ausgewaehlt und selekiert werden.
 * @see it.naturtalent.e4.project.ui.handlers.emf.SelectInUseProjectHandler
 * @see it.naturtalent.e4.project.ui.utils.ProjectQueue
 * @author dieter
 *
 */
public class ProjectQueueDialog extends TitleAreaDialog
{
	// filtert nach Projektnamen
	private class NameFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{					
			if (element instanceof String)
			{	
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject((String)element);
				if(iProject.exists())
				{
					try
					{
						String projectName = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);						
						if(StringUtils.isNotEmpty(stgFilter))					
							return StringUtils.containsIgnoreCase(projectName, stgFilter);					

					} catch (CoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			return true;
		}
	}

	
	// LabelProvider liefert Aliasnamen der Projekte
	private class TableLabelProvider extends LabelProvider
			implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			try
			{
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject((String) element);
				if(iProject.exists())
					return iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
				
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return element.toString();
		}
	}

	private TableViewer tableViewer;
	
	private Table table;
	
	private Object [] selections;		
	
	private Button btnDelete;
	private Text textFilter;
	private String stgFilter;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ProjectQueueDialog(Shell parentShell)
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
		setMessage(Messages.ProjectQueueDialog_this_message);
		setTitle(Messages.ProjectQueueDialog_this_title);
		setTitleImage(Icon.WIZBAN_SMILEY.getImage(IconSize._75x66_TitleDialogIconSize));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		textFilter = new Text(container, SWT.BORDER);
		textFilter.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{				
				stgFilter = textFilter.getText();
				tableViewer.refresh();
			}
		});
		
		//textFilter.setText(Messages.ProjectQueueDialog_text_text);
		textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				okPressed();
			}
		});
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				selections = selection.toArray();			
				updateWidget();
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tableViewer.setFilters(new ViewerFilter []{new NameFilter()});
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnDelete = new Button(composite, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// aus dem Dialog entfernen
				StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
				Object [] selections = selection.toArray();				
				for(Object selObject : selections)
					tableViewer.remove(selObject);
				
				// aus dem ProjektQueue entfernen
				ProjectQueue projectQueue = Activator.projectQueue;
				for(Object selObject : selections)
					projectQueue.remove(selObject);				
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.setText(Messages.ProjectQueueDialog_btnNewButton_text_1);
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(Activator.projectQueue);

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
		return new Point(707, 613);
	}

	public Object[] getSelections()
	{
		return selections;
	}

	private void updateWidget()
	{
		btnDelete.setEnabled(table.getSelectionCount() > 0);
	}

}
