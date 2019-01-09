package it.naturtalent.e4.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DirectoryEditorComposite extends Composite
{
	private Text textDirectory;
	private Label lblDirName;
	private Button btnSelect;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DirectoryEditorComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		lblDirName = new Label(this, SWT.NONE);
		lblDirName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblDirName.setText("ein Verzeichnis auswählen");
		new Label(this, SWT.NONE);
		
		textDirectory = new Text(this, SWT.BORDER);
		textDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnSelect = new Button(this, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Dialog Verzeichnis vorbereiten
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				dlg.setText("Verzeichnis auswählen");
				
				// falls bereits definiert, in das Verzeichnis wechseln
				if(StringUtils.isNotEmpty(textDirectory.getText()))
					dlg.setFilterPath(textDirectory.getText());
			
				// Dialog oeffnen				
				String dir = dlg.open();
				if(StringUtils.isNotEmpty(dir))	
				{
					textDirectory.setText(dir);
					preSelection();
				}
			}
		});
		btnSelect.setText("auswählen");
	}
	
	protected void preSelection()
	{
	}
	
	public String getDirectory()
	{
		return textDirectory.getText();
	}

	public void setDirectory(String directory)
	{
		textDirectory.setText(directory);
	}

	public String getLabel()
	{
		return lblDirName.getText();
	}

	public void setLabel(String label)
	{
		lblDirName.setText(label);
	}
	
	public void setEnable(boolean enable)
	{
		textDirectory.setEditable(enable);
		btnSelect.setEnabled(enable);
	}
	

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
