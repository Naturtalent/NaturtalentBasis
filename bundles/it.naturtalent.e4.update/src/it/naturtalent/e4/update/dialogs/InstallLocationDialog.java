package it.naturtalent.e4.update.dialogs;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.update.Messages;

public class InstallLocationDialog extends TitleAreaDialog
{

	// DialogSettings
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	public static final String INSTALL_LOCATION_SETTINGS = "installlocationsettings"; //$NON-NLS-1$
	
	private String installLocation = null;
	
	private Combo comboLocation;
	
	private Button btnLocal;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public InstallLocationDialog(Shell parentShell)
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
		setMessage(Messages.InstallLocationDialog_this_message);
		setTitle(Messages.InstallLocationDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblLocation = new Label(container, SWT.NONE);
		lblLocation.setText(Messages.InstallLocationDialog_lblNewLabel_text);
		new Label(container, SWT.NONE);
		
		comboLocation = new Combo(container, SWT.NONE);
		comboLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboLocation.addModifyListener( new ModifyListener()
	        {
	            public void modifyText( ModifyEvent e )
	            {
					try
					{
						URI installLocationURI = new URI(comboLocation.getText());
						if(getButton( IDialogConstants.OK_ID) != null)
							getButton( IDialogConstants.OK_ID ).setEnabled( true );
					} catch (URISyntaxException e1)
					{	
						installLocation = null;
						if(getButton( IDialogConstants.OK_ID) != null)
							getButton( IDialogConstants.OK_ID ).setEnabled( false );
					}	            	
	            }
	        } );
		
		String[]locations = settings.getArray(INSTALL_LOCATION_SETTINGS);
		if(ArrayUtils.isEmpty(locations))
			comboLocation.setText("http://");
		else
			comboLocation.setText(locations[0]);
			
	
		btnLocal = new Button(container, SWT.NONE);
		btnLocal.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				 DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
				 comboLocation.setText(fileDialog.open());
			}
		});
		btnLocal.setText(Messages.InstallLocationDialog_btnNewButton_text);

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 300);
	}

	@Override
	protected void okPressed()
	{		
		installLocation = comboLocation.getText();		
		super.okPressed();
	}

	public String getInstallLocation()
	{
		return installLocation;
	}

	
	

}
