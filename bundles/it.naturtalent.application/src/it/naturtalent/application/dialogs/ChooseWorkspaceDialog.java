package it.naturtalent.application.dialogs;



import java.io.File;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.log.Logger;
//import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.application.ChooseWorkspaceData;


/**
 * @author dieter
 *
 */
public class ChooseWorkspaceDialog extends TitleAreaDialog
{		
	private ChooseWorkspaceData workspaceData = new ChooseWorkspaceData();
	
	public static final String WORKSPACE_SETTINGS = "workspacesettings"; //$NON-NLS-N$
	
	
	private String title, areaTitle, message;		
	private Image titleImage;
	private CCombo formCombo;
	private Button formButton;
	private String selectedURL = null;
	
	@Inject
	static Shell parentShell;
	
	@Inject
	static Logger logger;
	
	/**
	 * @wbp.parser.constructor
	 */
	public ChooseWorkspaceDialog()
	{
		super(parentShell);
	}
	
	/**
	 * Konstruktion
	 * @param parentShell
	 */
	public ChooseWorkspaceDialog(Shell parentShell)
	{
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		if (titleImage != null)
			setTitleImage(titleImage);

		title = "Arbeitsverzeichis öffnen";
		getShell().setText(title);
		
		areaTitle = "Arbeitsverzeichnis auswählen";
		setTitle(areaTitle);
		
		message = "Naturtalent speichert die Projektdaten in einem speziellen Verzeichnis.\n";
		message = message + "Mit diesem Dialog kann ein Verzeichnis ausgewählt werden";
		setMessage(message);

		Composite comp = (Composite) super.createDialogArea(parent);
		Composite editorArea = new Composite(comp, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		gridLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		gridLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		gridLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);		
		editorArea.setLayout(gridLayout);		
		editorArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Arbeitsverzeichnis manuell eingeben
		formCombo = new CCombo(editorArea, SWT.BORDER);
		formCombo.setLayoutData(new GridData(400, SWT.DEFAULT));
		
		// den aktuellen Workspace im Combo eintragen
		String currentWorkspace = workspaceData.getCurrentWorkspaceLocation();
		formCombo.setText(currentWorkspace);
				
		formCombo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				// in Abhaengigkeit der Texteigabe Ok-Button de-/aktivieren
				Button okButton = getButton(Window.OK);
				if (okButton != null && !okButton.isDisposed())
				{
					boolean nonWhitespaceFound = false;
					String characters = formCombo.getText();
					for (int i = 0; !nonWhitespaceFound
							&& i < characters.length(); i++)
					{
						if (!Character.isWhitespace(characters.charAt(i)))						
							nonWhitespaceFound = true;						
					}
					okButton.setEnabled(nonWhitespaceFound);
				}
			}			
		});
		
		
		// Arbeitsverzeichnis im Dateiexplorer auswaehlen
		formButton = new Button(editorArea, SWT.PUSH);
		formButton.setText("auswählen");
        setButtonLayoutData(formButton);
        GridData data = (GridData) formButton.getLayoutData();
        data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        formButton.setLayoutData(data);
        formButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Auswahl Arbeitsverzeichnis");
				dialog.setMessage("Das zuverwendende Verzeichnis auswählen.");
				dialog.setFilterPath(getInitialBrowsePath());
				String dir = dialog.open();
				if (dir != null)
				{
					formCombo.setText(TextProcessor.process(dir));					
				}
			}
		});
		
		return comp;
	} 
	 
    private String getInitialBrowsePath()
	{
		File dir = new File(formCombo.getText());
		
		while (dir != null && !dir.exists())
		{
			dir = dir.getParentFile();
		}

		return dir != null ? dir.getAbsolutePath() : System
				.getProperty("user.dir"); //$NON-NLS-1$
	}
    
    @Override
	protected void okPressed()
	{		
		selectedURL = formCombo.getText();
		
		try
		{
			// speichert Workspace Lecation unter CMD_DATA im LauchInifile
			new ChooseWorkspaceData().setCommandData(selectedURL);
			
			//new ChooseWorkspaceData().setWorkspaceLocations(workspaces);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.okPressed();
	}

	/**
	 * Get the workspace location from the widget.
	 * @return String
	 */
	public String getSelectedWorkspaceLocation()
	{
		return selectedURL;
	}

	/**
	 * uebergibt die Praeferenzen in einem String und initialisiert hiermit die Combo
	 * 
	 * @param preferences
	 */
	public void setComboItems(String preferences)
	{		
		if (formCombo != null)
		{
			if (StringUtils.isNotEmpty(preferences))
			{
				String[] prefs = StringUtils.split(preferences, ",");
				formCombo.setItems(prefs);
			}
		}
	}
	
	
	
}
