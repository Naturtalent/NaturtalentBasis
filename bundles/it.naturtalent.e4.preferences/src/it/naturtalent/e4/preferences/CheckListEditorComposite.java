package it.naturtalent.e4.preferences;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * Composite zur Realisierung eines Listeditors mit Checkfunktion durch Implementierung eines
 * CheckBoxTableViewers.
 * 
 * @author dieter
 *
 */
public class CheckListEditorComposite extends Composite
{
	
	protected CheckboxTableViewer checkboxTableViewer;
	private GridData gd_table;
	private int tableWidth;
	private int tableHeight;
	protected Table table;
	protected EditorDialog dialog;
	protected Button btnAdd;
	protected Button btnEdit;
	protected Button btnRemove;
	//private String [] checkedElements;
	protected String editedEntry;
	
	
	// Dialoglabels
	private static final String DEFAULT_DIALOG_TITLE = "Präferenz"; //$NON-NLS-N$
	protected String dialogTitle = DEFAULT_DIALOG_TITLE;
	private static final String DEFAULT_DIALOG_MESSAGE = "Präferenz editieren"; //$NON-NLS-N$
	protected String dialogMessage = DEFAULT_DIALOG_MESSAGE;
	
	private static final String ADD_DIALOG_MESSAGE = "neu Präferenz hinzufügen"; //$NON-NLS-N$
	private static final String EDIT_DIALOG_MESSAGE = "Präferenz bearbeiten"; //$NON-NLS-N$
	
	protected boolean dialogCancel;
	
	
	// Validator fuer InputDialog
	protected IInputValidator validator = new IInputValidator()
	{
		public String isValid(String string)
		{
			if (StringUtils.isEmpty(string))						
				return "leeres Eingabefeld"; //$NON-NLS-N$
			
			return null;
		}
	};

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CheckListEditorComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));

		checkboxTableViewer = CheckboxTableViewer.newCheckList(this, SWT.BORDER | SWT.FULL_SELECTION);		
		
		table = checkboxTableViewer.getTable();		
		
		gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		setTableSize(450,200);
		gd_table.heightHint = tableHeight;
		gd_table.widthHint = tableWidth;
		table.setLayoutData(gd_table);
		
		// kontrolliert den Checkmodus (Einzelchecking)
		addCheckStateListener();
		
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		checkboxTableViewer.addDoubleClickListener(new IDoubleClickListener()
		{			
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = checkboxTableViewer.getStructuredSelection();
				Object selObj = selection.getFirstElement();
				if (selObj instanceof String)
				{
					//selectedValue = (String) selObj;
					doEdit();
				}				
			}
		});
		
		checkboxTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{			
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateWidgets();				
			}
		});
		
		
		Composite btnComposite = new Composite(this, SWT.NONE);
		btnComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		btnAdd = new Button(btnComposite, SWT.NONE);
		btnAdd.setText("Add");//$NON-NLS-1$
		btnAdd.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doAdd();
			}
		});
		
		btnEdit = new Button(btnComposite, SWT.NONE);
		btnEdit.setText("Edit");//$NON-NLS-1$
		btnEdit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = checkboxTableViewer.getStructuredSelection();
				Object selObj = selection.getFirstElement();
				if (selObj instanceof String)
				{
					//selectedValue = (String) selObj;
					doEdit();
				}				
			}
		});
		
		btnRemove = new Button(btnComposite, SWT.NONE);
		btnRemove.setText("Remove");//$NON-NLS-1$
		btnRemove.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doRemove();
			}
		});
	}
	
	
	/*
	 * Listener fuer Einzelchecking (nur ein Eintrag kann gecheckt werden) 
	 */
	protected void addCheckStateListener()
	{
		checkboxTableViewer.addCheckStateListener(new ICheckStateListener()
		{			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event)
			{							
				checkboxTableViewer.setAllChecked(false);
				checkboxTableViewer.setChecked(event.getElement(), event.getChecked());
			}
		});
	}
	
	protected void setTableSize(int width, int height)
	{
		tableWidth = width;
		tableHeight = height;
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Den String 'values' splitten und in Tabelle uebernehmen.
	 * 
	 * @param values
	 */
	public void setValues(String values)
	{
		if(StringUtils.isNotEmpty(values))	
		{
			String [] items = StringUtils.split(values, ",");
			checkboxTableViewer.setInput(items);
		}
		updateWidgets();
	}
	
	/**
	 * Alle Tabelleneintraege, mit Komma getrennt, in einem String zusammenfassen und zurueckgeben. 
	 * @return
	 */
	public String getValues()
	{	
		// die moḿentan gecheckte Eintrage werden gesichert
		Object [] checkedElements = checkboxTableViewer.getCheckedElements();
		
		// !!! alle Tabelleneintraege weerden gecheckt
		checkboxTableViewer.setAllChecked(true);		
		Object [] result = checkboxTableViewer.getCheckedElements();
		String [] stg = new String[result.length];
		System.arraycopy(result, 0, stg, 0,result.length);
		
		// realen Checkstatus wieder herstellen
		checkboxTableViewer.setCheckedElements(checkedElements);
		
		return StringUtils.join(stg,",");
	}
	
	public boolean isEntryExist(String checkEntry)
	{
		String [] existValuesArray = StringUtils.split(getValues(), ",");
		return ArrayUtils.contains(existValuesArray, checkEntry);
	}

	/**
	 * UI's (Buttons) - Status aktualisieren
	 */
	protected void updateWidgets()
	{
		IStructuredSelection selection = checkboxTableViewer.getStructuredSelection();
		boolean selectionState = selection.getFirstElement() != null;
		btnEdit.setEnabled(selectionState);
		btnRemove.setEnabled(selectionState);
	}
	
	/**
	 * neuer Eintrag in der EditorListe mit Eingabedialog (simple Texteingabe mit einstellbaen Validator)
	 */
	protected void doAdd()
	{
		dialog = new EditorDialog(getShell(),dialogTitle, dialogMessage,"",validator);
		if(dialog.open() == InputDialog.OK)	
			addEntry(dialog.getValue());				
	}
	
	protected void addEntry(String newEntry)
	{
		checkboxTableViewer.add(newEntry);	
	}
	
	/**
	 * Der selektierte Eintrag kann mit einem InputDialog bearbeitet werden und wird dann anschliessend mit 
	 * 'updateEntry()' aktualisiert.
	 */
	protected void doEdit()
	{
		IStructuredSelection selection = checkboxTableViewer.getStructuredSelection();
		Object selObj = selection.getFirstElement();
		if (selObj instanceof String)
		{
			editedEntry = null;
			String oldEntry = (String) selObj; 
			InputDialog dialog = new InputDialog(getShell(), dialogTitle,
					dialogMessage, oldEntry, validator);
			if (dialog.open() == InputDialog.OK)
			{
				editedEntry = dialog.getValue();
				updateEntry(editedEntry);
				dialogCancel = false;
			}
			else dialogCancel = true;						
		}
	}
	
	/*
	 * den seektierten Tabelleneintrag durch 'updateEntry' ersetzen
	 */
	protected void updateEntry(String updateEntry)
	{
		IStructuredSelection selection = checkboxTableViewer.getStructuredSelection();
		Object selObj = selection.getFirstElement();
		if (selObj instanceof String)
		{
			boolean checkedState = checkboxTableViewer.getChecked(selObj);
			
			int idx = checkboxTableViewer.getTable().getSelectionIndex();

			// alten Eintrag loeschen
			checkboxTableViewer.remove(selObj);

			// den editierten Wert einfuegen
			checkboxTableViewer.insert(updateEntry, idx);
			
			/*
			String [] contentArray = (String[]) checkboxTableViewer.getInput();
			contentArray = ArrayUtils.remove(contentArray, idx);
			contentArray = ArrayUtils.insert(idx, contentArray, updateEntry);
			checkboxTableViewer.setInput(contentArray);
			*/
						
			// aktualisierten Wert wieder selektieren
			checkboxTableViewer.setSelection(new StructuredSelection(updateEntry), true);

			// CheckStatur wiederherstellen
			checkboxTableViewer.setChecked(updateEntry, checkedState);
		}
	}


	protected void doRemove()
	{
		IStructuredSelection selection = checkboxTableViewer.getStructuredSelection();
		Object selObj = selection.getFirstElement();
		if (selObj instanceof String)
			checkboxTableViewer.remove(selObj);				
		
		updateWidgets();				
	}


	public String[] getCheckedElements()
	{
		Object [] result = checkboxTableViewer.getCheckedElements();
		String [] stg = new String[result.length];
		System.arraycopy(result, 0, stg, 0,result.length);
				
		return stg;
	}


	public void setCheckedElements(String[] checkedElements)
	{
		//this.checkedElements = checkedElements;
		checkboxTableViewer.setCheckedElements(checkedElements);
	}

	/**
	 * Rückgabe aller Eintraege im Viewer
	 * 
	 * @return
	 */
	public String[] getTableEntries()
	{
		String[] names;
		String[] checkedNames = getCheckedElements();		
		checkboxTableViewer.setAllChecked(true);
		names = getCheckedElements();
		checkboxTableViewer.setCheckedElements(checkedNames);
		return names;
	}
	

}
