package it.naturtalent.e4.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

/**
 * UI der Foldersuchseite.
 * 
 * Combo zur Eingabe eines Suchmusters fuer den Verzeichnisnamen.
 * Eine Gruppe in der die Auswahl der Projekte eingeschraenkt werden kann. 
 * 
 * @author dieter
 *
 */
public class FolderSearchComposite extends Composite
{
	
	// Name der Foldersetting Section
	private static final String FOLDER_SETTING_SECTION = "searchFolder"; //$NON-NLS-1$
	
	// SettingKeys
	public static final String SEARCH_FOLDERPATTERN_SETTINGS = "searchfolderpattern"; //$NON-NLS-1$
	
	// UIs
	private Combo comboFolderPattern;
	private Button btnCheckCaseSensitiv;
	private Button btnCheckRegularExpression;
	private DefaultNtProjectSearchComposite defaultNtProjectSearchComposite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FolderSearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label lblSpaceHeader = new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Label lblFolderPattern = new Label(this, SWT.NONE);
		lblFolderPattern.setText("Suchmuster des Verzeichnisses"); //$NON-NLS-1$
		new Label(this, SWT.NONE);
		
		comboFolderPattern = new Combo(this, SWT.NONE);
		comboFolderPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		btnCheckCaseSensitiv = new Button(composite, SWT.CHECK);
		btnCheckCaseSensitiv.setText("Groß-/Kleinschreibung beachten"); //$NON-NLS-1$
		
		btnCheckRegularExpression = new Button(composite, SWT.CHECK);
		btnCheckRegularExpression.setText("Regular expression");
		
		Label lblSpace1 = new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Label lblSpace2 = new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		// Gruppe 'auf Projekte einschraenken'
		Group groupProject = new Group(this, SWT.NONE);
		groupProject.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		groupProject.setText("die einbezogenen Projekte einschränken");
		
		// fuer die Einschraenkung der Projekte wird 'DefaultNtProjectSearchComposite' eingebunden
		defaultNtProjectSearchComposite = new DefaultNtProjectSearchComposite(groupProject, SWT.NONE);
		defaultNtProjectSearchComposite.setBounds(0, 0, 900, 221);

	}
	
	/*
	 * Die Foldersettings werden in einer Section von 'settings' gespeichert.
	 * 
	 */
	public void setDialogSettings(IDialogSettings settings)
	{
		IDialogSettings section = settings.getSection(FOLDER_SETTING_SECTION);
		if(section != null)
		{
			// SuchmusterArray laden
			String [] searchPattern = section.getArray(SEARCH_FOLDERPATTERN_SETTINGS);	
			if(searchPattern != null)
			{
				comboFolderPattern.setItems(searchPattern);
				
				// Index0 wird vorgegeben
				comboFolderPattern.setText(comboFolderPattern.getItem(0));
			}	
		}		
	}
	
	public void saveDialogSettings(IDialogSettings settings)
	{
		IDialogSettings section = settings.getSection(FOLDER_SETTING_SECTION);
		if (section == null)
		{
			section = new DialogSettings(FOLDER_SETTING_SECTION);
			settings.addSection(section);
		}
		
		// speichern des Suchpatterns
		String searchPattern = comboFolderPattern.getText();
		if (StringUtils.isNotEmpty(searchPattern))
		{
			String[] projectPattern = section.getArray(SEARCH_FOLDERPATTERN_SETTINGS);
			if (projectPattern == null)
			{
				// Setting wird angelegt
				projectPattern = ArrayUtils.add(null, searchPattern);
				section.put(SEARCH_FOLDERPATTERN_SETTINGS, projectPattern);
			}

			if (!ArrayUtils.contains(projectPattern, searchPattern))
			{
				projectPattern = ArrayUtils.insert(0, projectPattern,searchPattern);
				if (projectPattern.length > 9)
					projectPattern = ArrayUtils.remove(projectPattern, 9);
				section.put(SEARCH_FOLDERPATTERN_SETTINGS, projectPattern);
			}
		}

	}
	
	/**
	 * Gibt die Folder Search-Optionen in einer eigenen Klasse zurueck.
	 * 
	 * @return
	 */
	public SearchOptions getFolderSearchOptions()
	{
		SearchOptions searchOptions = new SearchOptions();
		searchOptions.setSearchPattern(comboFolderPattern.getText());
		searchOptions.setCaseSensitive(btnCheckCaseSensitiv.getSelection());
		searchOptions.setRegularExpression(btnCheckRegularExpression.getSelection());
		//searchOptions.setSearchItems(getFocusedAdaptables());
		
		return searchOptions;
	}
	
	/**
	 * Gibt die Project Search-Optionen in einer eigenen Klasse zurueck.
	 * 
	 * @return
	 */
	public SearchOptions getProjectSearchOptions()
	{
		return defaultNtProjectSearchComposite.getSearchOptions();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
