package it.naturtalent.e4.preferences;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.application.IPreferenceAdapter;

import org.eclipse.swt.layout.GridLayout;

public class PreferenceDialog extends Dialog
{

	@Inject @Optional private static Shell shell;
	@Inject @Optional IPreferenceRegistry preferenceRegistry;
	
	private PreferencesView preferenceView;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public PreferenceDialog()
	{
		super(shell);
	}
	
	public PreferenceDialog(Shell parentShell)
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
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		preferenceView = new PreferencesView();
		preferenceView.createControls(container);
		
		
		// Prefererenceregistry (alle Preferenzen an den View uebergeben)
		preferenceView.setPreferenceRegistry(preferenceRegistry);
		
		return container;
	}
	
	@Override
	protected void configureShell(Shell newShell)
	{	
		super.configureShell(newShell);
		newShell.setText(Messages.PreferenceDialog_WindowTitle);
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
	
	@Override
	protected void okPressed()
	{
		IPreferenceAdapter adapter = preferenceView.getPreferenceAdapter();
		if(adapter != null)
			adapter.okPressed();
		
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(861, 615);
	}

}
