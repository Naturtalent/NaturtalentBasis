package it.naturtalent.e4.preferences;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.e4.preferences.EditorDialog;


/**
 * Composite zur Bearbeitung von Preaferenzen,
 * Besteht aus einem Textfeld und einer Liste. mit Add-,Edit- und Remove-Buttons 
 * 
 * @author dieter
 *
 */
public class TextListEditorComposite extends Composite
{

	protected Text text;
	protected List list;
	protected EditorDialog dialog;
	protected Button btnAdd;
	protected Button btnEdit;
	protected Button btnRemove;
	
	// Textmassage
	private static final String DEFAULT_TEXT_MESSAGE = "aktuelle Präferenz"; //$NON-NLS-N$	
	protected String textMessage = DEFAULT_TEXT_MESSAGE;
	
	// Dialoglabels
	private static final String DEFAULT_DIALOG_TITLE = "Präferenz"; //$NON-NLS-N$
	protected String dialogTitle = DEFAULT_DIALOG_TITLE;
	private static final String DEFAULT_DIALOG_MESSAGE = "Präferenz editieren"; //$NON-NLS-N$
	protected String dialogMmessage = DEFAULT_DIALOG_MESSAGE;
	
	// Tooltips
	private static final String ADD_DIALOG_TOOLTIP = "eine Präferenz hinzufügen"; //$NON-NLS-N$
	protected String addDialogToolTip = ADD_DIALOG_TOOLTIP;
	private static final String EDIT_DIALOG_TOOLTIP = "eine Präferenz editieren"; //$NON-NLS-N$
	protected String editDialogToolTip = EDIT_DIALOG_TOOLTIP;
	private static final String REMOVE_DIALOG_TOOLTIP = "selektierte Präferenz entfernen"; //$NON-NLS-N$
	protected String removeDialogToolTip = REMOVE_DIALOG_TOOLTIP;

	
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
	 * @param parent
	 * @param style
	 */
	public TextListEditorComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		// Text mit Message
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText(textMessage);
		new Label(this, SWT.NONE);
				
		text = new Text(this, SWT.BORDER);
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);
		
		// Liste		
		list = new List(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				doEdit();
			}
		});
		list.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateWidgets();
			}
		});
		GridData gd_list = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gd_list.heightHint = 340;
		list.setLayoutData(gd_list);		
		list.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				setSize(560, 400);
			}
		});
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doAdd();
			}
		});
		btnAdd.setText("Add");	//$NON-NLS-1$
		btnAdd.setToolTipText(ADD_DIALOG_TOOLTIP);
		
		
		btnEdit = new Button(composite, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doEdit();
			}
		});
		btnEdit.setText("Edit");	//$NON-NLS-1$
		btnEdit.setToolTipText(EDIT_DIALOG_TOOLTIP);
		
		btnRemove = new Button(composite, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doRemove();
			}
		});
		btnRemove.setText("Remove");	//$NON-NLS-1$
		btnRemove.setToolTipText(REMOVE_DIALOG_TOOLTIP);
		
		
		
		updateWidgets();
	}

	protected void doAdd()
	{
		dialog = new EditorDialog(getShell(),dialogTitle, dialogMmessage,"",validator);
		if(dialog.open() == InputDialog.OK)			
			list.add(dialog.getValue());	
	}

	protected void doEdit()
	{
		int idx = list.getSelectionIndex();
		InputDialog dialog = new InputDialog(getShell(),dialogTitle, dialogMmessage,list.getItem(idx),validator);
		if(dialog.open() == InputDialog.OK)			
			list.setItem(idx, dialog.getValue());				
	}
	
	protected void doRemove()
	{
		list.remove(list.getSelectionIndex());
		updateWidgets();				
	}
	
	protected void updateWidgets()
	{
		boolean selection = list.getSelection().length > 0;
		btnEdit.setEnabled(selection);
		btnRemove.setEnabled(selection);
	}
	
	public void setValues(String values)
	{
		if(StringUtils.isNotEmpty(values))	
		{
			String [] items = StringUtils.split(values, ",");
			list.setItems(items);
		}
		updateWidgets();
	}
	
	public String getValues()
	{
		String value = "";
		String [] values = list.getItems();
		if(ArrayUtils.isNotEmpty(values))
			value = StringUtils.join(values,",");
		return value;
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public List getList()
	{
		return list;
	}

	public void setDialogTitle(String dialogTitle)
	{
		this.dialogTitle = dialogTitle;
	}


	
	
	
}
