package it.naturtalent.e4.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

public class FolderSearchComposite extends Composite
{
	
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
		lblFolderPattern.setText("Suchmuster des Verzeichnisses");
		new Label(this, SWT.NONE);
		
		comboFolderPattern = new Combo(this, SWT.NONE);
		comboFolderPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		btnCheckCaseSensitiv = new Button(composite, SWT.CHECK);
		btnCheckCaseSensitiv.setText("Groß-/Kleinschreibung beachten");
		
		btnCheckRegularExpression = new Button(composite, SWT.CHECK);
		btnCheckRegularExpression.setText("Regular expression");
		
		Label lblSpace1 = new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Label lblSpace2 = new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Group groupProject = new Group(this, SWT.NONE);
		groupProject.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		groupProject.setText("Projekte");
		
		defaultNtProjectSearchComposite = new DefaultNtProjectSearchComposite(groupProject, SWT.NONE);
		defaultNtProjectSearchComposite.setBounds(0, 0, 900, 221);

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
