package it.naturtalent.e4.preferences;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class EditorDialog extends InputDialog
{
	
	
	public EditorDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator)
	{
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		// TODO Auto-generated constructor stub
	}
	
}
