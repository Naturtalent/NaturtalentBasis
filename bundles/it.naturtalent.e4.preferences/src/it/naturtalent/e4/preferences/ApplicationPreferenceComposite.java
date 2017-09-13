package it.naturtalent.e4.preferences;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.application.Messages;
import org.eclipse.swt.layout.GridData;

/**
 * UI der Applicationreferences
 * 
 * @author dieter
 *
 */
public class ApplicationPreferenceComposite extends Composite
{
	// Textfeld temporaeres Verzeichnis
	private Text txtTemp;
	
	// Liste der verfuegbaen Workspaces
	private WorkspaceEditorComposite listWorkspaces;
	private Text textLoggerFile;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ApplicationPreferenceComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(null);
		
		// temporaeres Verzeichnis
		Label lblTemp = new Label(this, SWT.NONE);
		lblTemp.setBounds(5, 5, 253, 17);
		lblTemp.setText(Messages.ApplicationPreferenceComposite_lblTemp_text);
				
		txtTemp = new Text(this, SWT.BORDER);
		txtTemp.setBounds(5, 28, 480, 27);
		txtTemp.setEditable(false);
		
		Button btnSelect = new Button(this, SWT.NONE);
		btnSelect.setBounds(490, 27, 86, 29);
		btnSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				dlg.setText("Verzeichnis auswählen");
				String dir = dlg.open();
				if(StringUtils.isNotEmpty(dir))
					txtTemp.setText(dir);
			}
		});
		btnSelect.setText(Messages.ApplicationPreferenceComposite_btnSelect_text);
		
		// Logger
		Label lblLogger = new Label(this, SWT.NONE);
		lblLogger.setText(Messages.ApplicationPreferenceComposite_lblLogger_text);
		lblLogger.setBounds(5, 94, 83, 17);
		
		textLoggerFile = new Text(this, SWT.BORDER);
		textLoggerFile.setEditable(false);
		textLoggerFile.setBounds(5, 117, 480, 27);
		
		Button btnOpenLogger = new Button(this, SWT.NONE);
		btnOpenLogger.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String logFilePath = textLoggerFile.getText();
				if(StringUtils.isNotEmpty(logFilePath))
				{
					File logFile = new File(textLoggerFile.getText());
					if(logFile.exists() && logFile.isFile())
					{
						String ext = FilenameUtils.getExtension(logFilePath);
						Program prog = Program.findProgram(ext);
						if (prog != null)
						{
							try
							{
								prog.execute(logFile.getPath());
								return;
							} catch (Exception excp)
							{
								//log.error(e);
							}
						}	
					}
				}
			}
		});
		btnOpenLogger.setBounds(490, 117, 81, 27);
		btnOpenLogger.setText(Messages.ApplicationPreferenceComposite_btnNewButton_text);
		
		// Workspaces
		Label lblWorkspace = new Label(this, SWT.NONE);
		lblWorkspace.setBounds(5, 184, 83, 17);
		lblWorkspace.setText(Messages.ApplicationPreferenceComposite_lblWorkspace_text);
		
		listWorkspaces = new WorkspaceEditorComposite(this, SWT.NONE);
		((GridData) listWorkspaces.getList().getLayoutData()).heightHint = 261;
		listWorkspaces.setBounds(5, 207, 560, 357);
		

		
	}
	
	public Text getTempDirEditor()
	{
		return txtTemp;
	}
	
	
	
	public Text getTextLoggerFile()
	{
		return textLoggerFile;
	}

	public ListEditorComposite getListWorkspaces()
	{
		return listWorkspaces;
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
