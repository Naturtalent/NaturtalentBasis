package it.naturtalent.e4.project.ui.dialogs;


import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.jface.viewers.ViewerSorter;

public class SelectProjectsDialog extends TitleAreaDialog
{
	private static class Sorter extends ViewerSorter
	{
		Collator collator = Collator.getInstance(Locale.GERMAN);
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			String name1 = mapProjects.get((String) e1);
			String name2 = mapProjects.get((String) e2);
			if (StringUtils.isNotEmpty(name1)
					&& StringUtils.isNotEmpty(name2))
				return collator.compare(name1, name2);	
			
			return 0;
		}
	}
		
	private class NameFilter extends ViewerFilter
	{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{
			if (element instanceof String)
			{
				String projectName = mapProjects.get((String) element);
				String filter = textflter.getText();	
				if(StringUtils.isNotEmpty(projectName) && StringUtils.isNotEmpty(filter))	
					return StringUtils.containsIgnoreCase(projectName, filter);
			}

			return true;
		}

	}

	private class TableLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return Icon.ICON_PROJECT.getImage(IconSize._16x16_DefaultIconSize);	
		}

		public String getColumnText(Object element, int columnIndex)
		{			
			return mapProjects.get(element);
		}
	}
	
	private static class ContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			if(inputElement instanceof Map)
			{
				mapProjects = (Map<String, String>) inputElement;
				return mapProjects.keySet().toArray(new String[mapProjects.keySet().size()]);
			}
			
			return new Object [0];
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
	
	private static Map<String, String>mapProjects;
	
	
	private Text textflter;
	private TableViewer tableViewer;
	private Table table;
	
	private String selectedProjectId;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectProjectsDialog(Shell parentShell)
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
		
		Label lblUnarchived = new Label(container, SWT.NONE);
		lblUnarchived.setText("Projekte");
		
		textflter = new Text(container, SWT.BORDER);
		textflter.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				tableViewer.refresh();
			}
		});
		textflter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setSorter(new Sorter());
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.addFilter(new NameFilter());
		
		tableViewer.setInput(getAllProjects());

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
		return new Point(450, 634);
	}

	@Override
	protected void okPressed()
	{
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		selectedProjectId = (String) selection.getFirstElement();
		super.okPressed();
	}

	public String getSelectedProjectId()
	{
		return selectedProjectId;
	}
	
	public void setSelectedProjectId(String selectedProjectId)
	{
		this.selectedProjectId = selectedProjectId;		
		
		// im Viewer selektieren
		tableViewer.setSelection(new StructuredSelection(selectedProjectId), true);
	}

	public String getProjectName(String projectId)
	{
		return mapProjects.get(projectId);
	}	
	
	/**
	 * Alle ProjektIDs in einer Map mit dem Key Projektname zusammenfassen
	 * 
	 * @return
	 */
	private Map<String, String> getAllProjects()
	{
		Map<String, String>allProjects = new HashMap<String, String>();
				
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();		
		for(IProject project : projects)
		{
			try
			{
				if (project.isOpen())
				{
					String name = project
							.getPersistentProperty(INtProject.projectNameQualifiedName);
					if (StringUtils.isNotEmpty(name))
						allProjects.put(project.getName(), name);
				}

			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return allProjects;
	}

}
