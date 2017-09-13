package it.naturtalent.e4.project.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetEditWizard;
import org.eclipse.wb.swt.ResourceManager;
import it.naturtalent.e4.project.ui.Messages;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.DoubleClickEvent;


/**
 * Festlegen, welche WorkingSets angezeigt werden sollen und Position im Navigator definieren.
 * 
 * @author dieter
 *
 */
public class ConfigureWorkingSetDialog extends TitleAreaDialog
{
	
	private WorkingSetManager workingSetManager = Activator.getWorkingSetManager();
	
	private List<IWorkingSet> fAllWorkingSets = new ArrayList<IWorkingSet>();
	private List<IWorkingSet> fAddedWorkingSets;
	private List<IWorkingSet> fRemovedWorkingSets;
	private List<IWorkingSet> fRemovedMRUWorkingSets;
	private Map <IWorkingSet, IWorkingSet> fEditedWorkingSets;
	
	// der Navigator
	protected IResourceNavigator navigator;
	
	private IWorkingSet[] selectResult;
	
	private IWorkingSet [] activeWorkingSets;
	
	// im Navigator vorhandene WS 
	//private IWorkingSet [] windowWorkingSets;
	
    private ILabelProvider labelProvider;

    private IStructuredContentProvider contentProvider;
    
    protected CheckboxTableViewer tableViewer;
	
	private Table table;
	
	protected Button btnEdit;
	
	protected Button btnDelete;
	
	protected Button btnNew;
	
	private Button btnUp;
	
	private Button btnDown;
	
	private Button btnSelectAll;
	
	private Button btnDeselectAll;

	
	
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public ConfigureWorkingSetDialog(Shell parentShell, IResourceNavigator navigator)
	{
		super(parentShell);	
		
		// alle verfuegbaren WorkingSets einlesen
		IWorkingSet [] allSets = workingSetManager.getWorkingSets();
		for(IWorkingSet iWorkingSet : allSets)
			fAllWorkingSets.add(iWorkingSet); 
			
		// die aktiven WorkingSets
		this.navigator = navigator;
		activeWorkingSets = navigator.getWindowWorkingSets();
		
        contentProvider = new ArrayContentProvider();
        labelProvider = new WorkingSetLabelProvider();

	}
	
	public ConfigureWorkingSetDialog(Shell parentShell, IWorkingSet [] activeWorkingSets)
	{
		super(parentShell);

		// alle verfuegbaren WorkingSets einlesen
		IWorkingSet [] allSets = workingSetManager.getWorkingSets();
		for(IWorkingSet iWorkingSet : allSets)
			fAllWorkingSets.add(iWorkingSet); 

		this.activeWorkingSets = activeWorkingSets;
		
		 contentProvider = new ArrayContentProvider();
		 labelProvider = new WorkingSetLabelProvider();
	}

	@Override
	public int open()
	{		
		fAddedWorkingSets = new ArrayList<IWorkingSet>();
		fRemovedWorkingSets = new ArrayList<IWorkingSet>();
		fEditedWorkingSets = new HashMap();
		fRemovedMRUWorkingSets = new ArrayList();
		
		return super.open();
	}



	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent)
	{
		setMessage(Messages.ConfigureWorkingSetDialog_this_message);
		setTitle(Messages.ConfigureWorkingSetDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				editSelectedWorkingSet();
			}
		});
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateWidgets();
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		btnUp = new Button(composite, SWT.NONE);
		btnUp.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				moveUp(((IStructuredSelection) tableViewer.getSelection())
						.toList());
			}
		});
		btnUp.setText(Messages.ConfigureWorkingSetDialog_btnUp);
		
		btnDown = new Button(composite, SWT.NONE);
		btnDown.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				moveDown(((IStructuredSelection) tableViewer.getSelection())
						.toList());
			}
		});
		btnDown.setText(Messages.ConfigureWorkingSetDialog_btnDown);
		
		btnSelectAll = new Button(composite, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(true);
			}
		});
		btnSelectAll.setText(Messages.ConfigureWorkingSetDialog_btnSelectAll_text);
		
		btnDeselectAll = new Button(composite, SWT.NONE);
		btnDeselectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(false);
			}
		});
		btnDeselectAll.setText(Messages.ConfigureWorkingSetDialog_btnDeselectAll);
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// neu
		btnNew = new Button(composite_1, SWT.NONE);		
		btnNew.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{				
				WorkingSetDialog dialog = new WorkingSetDialog(parent.getShell());
				if(dialog.open() == WorkingSetDialog.OK)
				{
					workingSetManager.restoreState();
					
					IWorkingSet workingSet = dialog.getWorkingSet();
					fAllWorkingSets.add(workingSet);
					tableViewer.add(workingSet);
					tableViewer.setSelection(new StructuredSelection(workingSet),
							true);
					tableViewer.setChecked(workingSet, true);
					workingSetManager.addWorkingSet(workingSet);
					fAddedWorkingSets.add(workingSet);
				}
			}
		});
		btnNew.setText(Messages.ConfigureWorkingSetDialog_btnNew_text);
		
		btnEdit = new Button(composite_1, SWT.NONE);
		btnEdit.setEnabled(false);
		btnEdit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				editSelectedWorkingSet();
			}
		});
		btnEdit.setText(Messages.ConfigureWorkingSetDialog_btnNewButton_text);
		
		// delete
		btnDelete = new Button(composite_1, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{				
				ISelection selection = tableViewer.getSelection();
				if (selection instanceof IStructuredSelection)
				{
					Iterator iter = ((IStructuredSelection) selection).iterator();
					while (iter.hasNext())
					{
						IWorkingSet workingSet = (IWorkingSet) iter.next();
						if (fAddedWorkingSets.contains(workingSet))
						{
							fAddedWorkingSets.remove(workingSet);
						}
						else
						{
							IWorkingSet[] recentWorkingSets = workingSetManager
									.getRecentWorkingSets();
							for (int i = 0; i < recentWorkingSets.length; i++)
							{
								if (workingSet.equals(recentWorkingSets[i]))
								{
									fRemovedMRUWorkingSets.add(workingSet);
									break;
								}
							}

							fRemovedWorkingSets.add(workingSet);
						}
						fAllWorkingSets.remove(workingSet);
						workingSetManager.removeWorkingSet(workingSet);
					}
					tableViewer.remove(((IStructuredSelection) selection).toArray());
				}
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.setText(Messages.ConfigureWorkingSetDialog_btnDelete_text);
		new Label(container, SWT.NONE);
		
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(labelProvider);
								
		init();
		return area;
	}
	
	protected void init()
	{
		// die Sortierung der aktiven WS nachziehen
		List<IWorkingSet> sortAll = new ArrayList<IWorkingSet>();				
				
		if (ArrayUtils.isNotEmpty(activeWorkingSets))
		{
			for (IWorkingSet workingSet : activeWorkingSets)
				sortAll.add(workingSet);
			for (IWorkingSet workingSet : fAllWorkingSets)
			{
				if (!sortAll.contains(workingSet))
					sortAll.add(workingSet);
			}
			fAllWorkingSets = sortAll;
		}
		
		// Tabelle mit den WorkingSets initialisieren
		tableViewer.setInput(fAllWorkingSets);

		// die aktiven WorkingSets checken				
		if(ArrayUtils.isNotEmpty(activeWorkingSets))
			tableViewer.setCheckedElements(activeWorkingSets);

				
		updateWidgets();		
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
	
	private boolean areAllGlobalWorkingSets(IStructuredSelection selection)
	{
		Set globals = new HashSet(Arrays.asList(workingSetManager.getWorkingSets()));
		for (Iterator iter = selection.iterator(); iter.hasNext();)
		{
			if (!globals.contains(iter.next()))
				return false;
		}
		return true;
	}
	
	protected void updateWidgets()
	{		
		IStructuredSelection selection = (IStructuredSelection) tableViewer
				.getSelection();
		boolean hasSelection = !selection.isEmpty();
		boolean hasSingleSelection = selection.size() == 1;
		
		btnDelete.setEnabled(hasSelection
				&& areAllGlobalWorkingSets(selection));
		
		btnEdit.setEnabled(hasSingleSelection
				&& areAllGlobalWorkingSets(selection));
		
		btnUp.setEnabled(canMoveUp());		
		btnDown.setEnabled(canMoveDown());
						
		btnSelectAll.setEnabled(hasSelection);
		btnDeselectAll.setEnabled(hasSelection);

		// Selektierter 'Others'-WorkingSet deaktiviert 'edit' und 'delete'
		if(hasSelection)
		{
			IWorkingSet workingSet = (IWorkingSet) selection.getFirstElement();
			if(StringUtils.equals(workingSet.getName(), IWorkingSetManager.OTHER_WORKINGSET_NAME))
			{
				btnEdit.setEnabled(false);
				btnDelete.setEnabled(false);
			}
		}
		
		
		
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 526);
	}
	
	private void editSelectedWorkingSet()
	{
		IWorkingSet editWorkingSet = (IWorkingSet) ((IStructuredSelection) tableViewer
				.getSelection()).getFirstElement();
				
		IWorkingSet originalWorkingSet = (IWorkingSet) fEditedWorkingSets
				.get(editWorkingSet);
		boolean firstEdit = originalWorkingSet == null;

		// save the original working set values for restoration when selection
		// dialog is cancelled.
		if (firstEdit)
		{
			originalWorkingSet = workingSetManager
					.createWorkingSet(editWorkingSet.getName(),
							editWorkingSet.getElements());
		}
		else
		{
			fEditedWorkingSets.remove(editWorkingSet);
		}
		
		WorkingSetDialog dialog = new WorkingSetDialog(getShell(),editWorkingSet);		
		dialog.create();
		if (dialog.open() == Window.OK)
		{
			editWorkingSet = dialog.getWorkingSet();
			tableViewer.update(editWorkingSet, null);			
			// make sure ok button is enabled when the selected working set
			// is edited. Fixes bug 33386.
			updateWidgets();
		}
		fEditedWorkingSets.put(editWorkingSet, originalWorkingSet);
	}

	@Override
	protected void okPressed()
	{
		Object[] result = tableViewer.getCheckedElements();	
		
		selectResult = new IWorkingSet[result.length];
		System.arraycopy(result, 0, selectResult, 0,
				result.length);
			
		if(navigator != null)
			navigator.setWorkingSets(selectResult);
		workingSetManager.updateOthers();
			
		super.okPressed();
	}
	
	@Override
	protected void cancelPressed()
	{
		restoreAddedWorkingSets();
		restoreChangedWorkingSets();
		restoreRemovedWorkingSets();
		super.cancelPressed();
	}

	/**
	 * Removes newly created working sets from the working set manager.
	 */
	private void restoreAddedWorkingSets()
	{
		Iterator iterator = fAddedWorkingSets.iterator();
		while (iterator.hasNext())
		{
			workingSetManager.removeWorkingSet(((IWorkingSet) iterator.next()));
		}
		fAddedWorkingSets.clear();
	}
	
	/**
	 * Rolls back changes to working sets.
	 */
	private void restoreChangedWorkingSets()
	{
		Iterator<IWorkingSet> iterator = fEditedWorkingSets.keySet().iterator();

		while (iterator.hasNext())
		{
			IWorkingSet editedWorkingSet = iterator.next();
			IWorkingSet originalWorkingSet = fEditedWorkingSets
					.get(editedWorkingSet);

			if (editedWorkingSet.getName().equals(originalWorkingSet.getName()) == false)
			{
				editedWorkingSet.setName(originalWorkingSet.getName());
			}
			if (editedWorkingSet.getElements().equals(
					originalWorkingSet.getElements()) == false)
			{
				editedWorkingSet.setElements(originalWorkingSet.getElements());
			}
		}
	}
	
	/**
	 * Adds back removed working sets to the working set manager.
	 */
	private void restoreRemovedWorkingSets()
	{
		for(IWorkingSet workingSet : fRemovedWorkingSets)
			workingSetManager.addWorkingSet(workingSet);
			
		for(IWorkingSet workingSet : fRemovedMRUWorkingSets)
			workingSetManager.addRecentWorkingSet(workingSet);			
	}


	private boolean canMoveUp()
	{
		int[] indc = tableViewer.getTable().getSelectionIndices();
		for (int i = 0; i < indc.length; i++)
		{
			if (indc[i] != i)
			{
				return true;
			}
		}
		return false;
	}

	private boolean canMoveDown()
	{
		int size = tableViewer.getTable().getItemCount();
		
		
		int[] indc = tableViewer.getTable().getSelectionIndices();
		int k = size - 1;
		for (int i = indc.length - 1; i >= 0; i--, k--)
		{
			if (indc[i] != k)
			{
				return true;
			}
		}
		return false;
	}
	
	private void moveDown(List toMoveDown)
	{
		if (toMoveDown.size() > 0)
		{
			setElements(reverse(moveUp(reverse(fAllWorkingSets), toMoveDown)));
			tableViewer.reveal(toMoveDown.get(toMoveDown.size() - 1));
		}
	}

	private void moveUp(List toMoveUp)
	{
		if (toMoveUp.size() > 0)
		{
			setElements(moveUp(fAllWorkingSets, toMoveUp));
			tableViewer.reveal(toMoveUp.get(0));
		}
	}
	
	private List moveUp(List elements, List move)
	{
		int nElements = elements.size();
		List res = new ArrayList(nElements);
		Object floating = null;
		for (int i = 0; i < nElements; i++)
		{
			Object curr = elements.get(i);
			if (move.contains(curr))
			{
				res.add(curr);
			}
			else
			{
				if (floating != null)
				{
					res.add(floating);
				}
				floating = curr;
			}
		}
		if (floating != null)
		{
			res.add(floating);
		}
		return res;
	}

	private void setElements(List elements)
	{
		fAllWorkingSets = elements;
		tableViewer.setInput(fAllWorkingSets);
		updateWidgets();
	}

	
	private List reverse(List p)
	{
		List reverse = new ArrayList(p.size());
		for (int i = p.size() - 1; i >= 0; i--)
		{
			reverse.add(p.get(i));
		}
		return reverse;
	}

	public IWorkingSet[] getConfigResult()
	{
		return selectResult;
	}

	public List<IWorkingSet> getAddedWorkingSets()
	{
		return fAddedWorkingSets;
	}

}
