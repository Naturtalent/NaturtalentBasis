package it.naturtalent.e4.preferences;



import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.swt.widgets.List;

public class ListEditorComposite extends Composite
{

	protected List list;
	protected EditorDialog dialog;
	private Button btnAdd;
	protected Button btnEdit;
	private Button btnRemove;
	
	private String dialogTitle = Messages.ListPreferenceComposite_InputLabelTitel;
	private String messageTitle = Messages.ListPreferenceComposite_InputLabel;
	
	// Validator fuer InputDialog
	protected IInputValidator validator = new IInputValidator()
	{
		public String isValid(String string)
		{
			if (StringUtils.isEmpty(string))						
				return Messages.ListPreferenceComposite_InputErrorEmptyField;
			
			return null;
		}
	};
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ListEditorComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
				
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
		btnAdd.setText("Add"); //$NON-NLS-1$
		
		btnEdit = new Button(composite, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doEdit();
			}
		});
		btnEdit.setText(Messages.ListPreferenceComposite_Edit);
		
		btnRemove = new Button(composite, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doRemove();
			}
		});
		btnRemove.setText(Messages.ListPreferenceComposite_Remove);
		updateWidgets();
	}

	protected void doAdd()
	{
		dialog = new EditorDialog(getShell(),dialogTitle, messageTitle,"",validator);
		if(dialog.open() == InputDialog.OK)			
			list.add(dialog.getValue());	
	}

	protected void doEdit()
	{
		int idx = list.getSelectionIndex();
		InputDialog dialog = new InputDialog(getShell(),dialogTitle, messageTitle,list.getItem(idx),validator);
		if(dialog.open() == InputDialog.OK)			
			list.setItem(idx, dialog.getValue());				
	}
	
	protected void doRemove()
	{
		list.remove(list.getSelectionIndex());
		updateWidgets();				
	}
	
	private void updateWidgets()
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

	public void setMessageTitle(String messageTitle)
	{
		this.messageTitle = messageTitle;
	}

	
	
	
	
}
