package it.naturtalent.e4.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
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
	
	
	
	// Dialoglabels
	private static final String DEFAULT_DIALOG_TITLE = "Präferenz"; //$NON-NLS-N$
	protected String dialogTitle = DEFAULT_DIALOG_TITLE;
	private static final String DEFAULT_DIALOG_MESSAGE = "Präferenz editieren"; //$NON-NLS-N$
	protected String dialogMmessage = DEFAULT_DIALOG_MESSAGE;
	
	private static final String ADD_DIALOG_MESSAGE = "neu Präferenz hinzufügen"; //$NON-NLS-N$
	private static final String EDIT_DIALOG_MESSAGE = "Präferenz bearbeiten"; //$NON-NLS-N$
	
	
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
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				doEdit();
			}
		});
		
		
		table.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
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
				doEdit();
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
				checkboxTableViewer.setChecked(event.getElement(), true);
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

	/**
	 * UI's (Buttons) - Status aktualisieren
	 */
	protected void updateWidgets()
	{
		boolean selection = (table.getSelectionIndex() >= 0);
		btnEdit.setEnabled(selection);
		btnRemove.setEnabled(selection);
	}
	
	/**
	 * neuer Eintrag in der EditorListe mit Eingabedialog (simple Texteingabe mit einstellbaen Validator)
	 */
	protected void doAdd()
	{
		dialog = new EditorDialog(getShell(),dialogTitle, ADD_DIALOG_MESSAGE,"",validator);
		if(dialog.open() == InputDialog.OK)				
			checkboxTableViewer.add(dialog.getValue());				
	}
	
	protected void doEdit()
	{				
		int idx = table.getSelectionIndex();		
		String value = table.getItem(idx).getText(idx);
		InputDialog dialog = new InputDialog(getShell(),dialogTitle, EDIT_DIALOG_MESSAGE,value,validator);
		if(dialog.open() == InputDialog.OK)	
			table.getItem(idx).setText(dialog.getValue());
	}


	protected void doRemove()
	{
		int idx = table.getSelectionIndex();
		if(idx >= 0)
			table.remove(idx);
		
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

	

}
