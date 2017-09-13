package it.naturtalent.e4.project.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.filters.HiddenResourceFilter;
import it.naturtalent.e4.project.ui.filters.ResourceFilterProvider;
import it.naturtalent.e4.project.ui.navigator.ResourceComparator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;
import it.naturtalent.e4.project.ui.ws.WorkingSet;
import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.osgi.resource.Resource;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

/**
 * Dialog zur Erstellung/Aendern eines bestimmten WorkingSets. 
 * Textdialog WorkingSetName, und 2 Fenster (Projekte auswahlen und zum Set zusammenstellen).
 *  
 * @author dieter
 *
 */
public class WorkingSetDialog extends TitleAreaDialog
{
	private TreeViewer treeViewer;
	private TableViewer tableViewer;
	private Tree tree;
	private Table table;
	private Text text;
	private Button btnAdd;
	private Button btnRemove;
	private Button okButton;
	
	private IWorkingSet availResourcesWorkingSet;
	private List <String> availableWorkingSetNames = new ArrayList<String>();
	
	private IWorkingSet workingSet;	
	private Label lblAvailable;
	private Label lblSelected;
	private Label lblWorkingsetname;
	private ControlDecoration controlDecoration;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	
	public WorkingSetDialog(Shell parentShell)
	{
		super(parentShell);
	}

	
	public WorkingSetDialog(Shell parentShell, IWorkingSet workingSet)
	{
		super(parentShell);
		this.workingSet = workingSet;
	}


	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitle(Messages.WorkingSetDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lblWorkingsetname = new Label(container, SWT.NONE);
		lblWorkingsetname.setText(Messages.WorkingSetDialog_lblWorkingsetname_text);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		text = new Text(container, SWT.BORDER);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				updateWidgets();
			}
		});
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(WorkingSetDialog.class, "/org/eclipse/jface/fieldassist/images/error_ovr.gif")); //$NON-NLS-1$
		controlDecoration.setDescriptionText(Messages.WorkingSetDialog_error_missingName);
		
		lblAvailable = new Label(container, SWT.NONE);
		lblAvailable.setText(Messages.WorkingSetDialog_lblAvailable_text);
		new Label(container, SWT.NONE);
		
		lblSelected = new Label(container, SWT.NONE);
		lblSelected.setText(Messages.WorkingSetDialog_lblSelected_text);

		treeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
		tree = treeViewer.getTree();
		tree.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateWidgets();
			}
		});
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_tree.widthHint = 200;
		tree.setLayoutData(gd_tree);

		//treeViewer.setPreselection(false);
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setComparator(new ResourceComparator(ResourceComparator.NAME));

		ResourceFilterProvider filterProvider = new ResourceFilterProvider();
		filterProvider.addFilter(new HiddenResourceFilter());
		treeViewer.setFilters(filterProvider.getFilters());

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				
				// selektierte tree-Elemente nach table uebertragen
				tableViewer.add(selection.toArray());

				// reset Selection verhindert preselection incl. update im Tree
				treeViewer.setSelection(null);
				treeViewer.remove(selection.toArray());
				
				updateWidgets();
			}
		});
		btnAdd.setEnabled(false);
		btnAdd.setText(Messages.NewWorkingSetDialog_btnAdd_text);
		
		btnRemove = new Button(composite, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				
				// aus 'table' entfernen
				tableViewer.remove(selection.toArray());
				
				// wieder im 'tree' sichtbar machen
				Iterator<IAdaptable>it = selection.iterator();
				while(it.hasNext())	
				{
					IAdaptable adapt = it.next();
					IResource resource = (IResource) adapt
							.getAdapter(IResource.class);
					if (resource.getType() == IResource.PROJECT)
						treeViewer.add(availResourcesWorkingSet, adapt);
					else
					{
						IResource parent = resource.getParent();
						treeViewer.add(parent, adapt);
					}
				}
								
				updateWidgets();
			}
		});
		btnRemove.setEnabled(false);
		btnRemove.setText(Messages.WorkingSetDialog_btnNewButton_text);

		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table = tableViewer.getTable();
		table.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateWidgets();
			}
		});
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.widthHint = 200;
		table.setLayoutData(gd_table);

		tableViewer.setContentProvider(new WorkbenchContentProvider());
		tableViewer.setLabelProvider(new WorkbenchLabelProvider());
		
		initAvailableNames();
		initWidgets();
		updateWidgets();
		return area;
	}
	
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);		
	}
	
	private void initWidgets()
	{
		// 'tree' WorkingSet mit den verfuegbaren Resourcen erzeugen 
		availResourcesWorkingSet = Activator.getWorkingSetManager().availableResourceWorkingSet();	
		availResourcesWorkingSet.getElements();
		treeViewer.setInput(availResourcesWorkingSet);
				
		if (workingSet != null)
		{
			// Dialogtitel auf 'edit'
			setTitle(Messages.WorkingSetDialog_this_title_edit);
			text.setText(workingSet.getLabel());
			
			// 'table' initialisieren
			initTable();
		}
	}
	
	/*
	 * Uebertraegt die in 'workingSet' definierten Elemente aus dem dem
	 * WorkingSet 'availResourcesWorkingSet' in die Tabelle 'table'
	 */
	private void initTable()
	{				
		if((workingSet != null) && (availResourcesWorkingSet != null))
		{
			IAdaptable [] treeContent = availResourcesWorkingSet.getElements();
			
			IAdaptable [] adaptables = workingSet.getElements();
			for(IAdaptable adapt : adaptables)
			{
				IAdaptable contentAdapt = findElement(treeContent, adapt);
				if(contentAdapt != null)
				{
					tableViewer.add(contentAdapt);
					treeViewer.remove(contentAdapt);
				}
			}
		}
	}
	
	private void initAvailableNames()
	{
		IWorkingSet[] allSets = Activator.getWorkingSetManager()
				.getAllWorkingSets();
		
		for(IWorkingSet iWorkingSet : allSets)
			availableWorkingSetNames.add(iWorkingSet.getName());
	}
		
	/*
	 * Sucht eine Element im WorkingSet 'availResourcesWorkingSet'
	 * @param treeContent
	 * @param adapt
	 * @return
	 */
	private IAdaptable findElement(IAdaptable [] treeContent, IAdaptable adapt)
	{
		String nameContent, nameElement;
		IResource contentResource;
		IResource elementResource = (IResource) adapt.getAdapter(IResource.class);
		nameElement = elementResource.getName();
		for(IAdaptable contentAdapt : treeContent)
		{			
			contentResource = (IResource) contentAdapt.getAdapter(IResource.class);
			nameContent = contentResource.getName();
			if(StringUtils.equals(nameContent, nameElement))
			{
				return contentAdapt;
			}
		}
		
		return null;
	}
	
	
	private void updateWidgets()
	{
		controlDecoration.hide();
		if(okButton != null)
			okButton.setEnabled(true);
		
		btnAdd.setEnabled(!treeViewer.getSelection().isEmpty());
		btnRemove.setEnabled(!tableViewer.getSelection().isEmpty());
		
		String name = text.getText();
		if(StringUtils.isEmpty(name))
		{
			controlDecoration.show();
			controlDecoration.setDescriptionText(Messages.WorkingSetDialog_error_missingName);
			
			if(okButton != null)
				okButton.setEnabled(false);
			
			return;
		}
		
		if (availableWorkingSetNames.contains(name))
		{
			if ((workingSet != null) && (StringUtils.equals(workingSet.getName(), name)))
				return;
						
			controlDecoration.show();
			controlDecoration
					.setDescriptionText(Messages.WorkingSetDialog_error_name_exist);

			if (okButton != null)
				okButton.setEnabled(false);
			
			return;
		}
	}

	

	@Override
	protected void okPressed()
	{
		TableItem [] items = tableViewer.getTable().getItems();
		
		// die zugeordneten Elemente in einem Array zusammenfassen
		List<IAdaptable>lAdaptables = new ArrayList<IAdaptable>();		
		for(TableItem item : items)
		{
			IAdaptable adapt = (IAdaptable) item.getData();
			lAdaptables.add(adapt);			
		}		
		IAdaptable [] adapts = lAdaptables.toArray(new IAdaptable [lAdaptables.size()]);
		
		String name = text.getText();
		if(workingSet == null)
		{
			// ein neues WokingSet generieren
			workingSet = new WorkingSet(name, name, adapts);
			//Activator.getWorkingSetManager().addWorkingSet(workingSet);
		}
		else
		{
			// das bestehende WorkingsSet aktualisieren
			workingSet.setName(name);
			workingSet.setLabel(name);
			workingSet.setElements(adapts);
		}

		
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(834, 677);
	}


	public IWorkingSet getWorkingSet()
	{
		return workingSet;
	}
	
	

}
