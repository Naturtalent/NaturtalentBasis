package it.naturtalent.e4.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

/**
 * Composite zum Einblenden der zur Projekt-Suchfunktion erforderlichen Eingabefeldder.
 * 
 * @author dieter
 *
 */
public class PropertySearchComposite extends Composite
{

	// Setting-Section fuer DateFilterComposite 
	private static final String PROJECTDATEFILTER_SETTINGSECTION = "projectdatesettingsection"; //$NON-NLS-1$
	
	// Datumsfilter 
	private DateFilterComposite dateFilterComposite;
	private Text textProjectID;
	
	//private PropertySearchComposite propertySearchComposite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PropertySearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Label lblProjectID = new Label(this, SWT.NONE);
		lblProjectID.setText("Project ID (nicht Namen) eingeben");
		
		textProjectID = new Text(this, SWT.BORDER);
		textProjectID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Datumsfilter einfuegen
		dateFilterComposite = new DateFilterComposite(this, SWT.NONE);
	}
	
	// Dialogsettings laden
	public void setDialogSettings(IDialogSettings settings)
	{		 
		// da 'DateFilterComposite' auch von anderen Such-Composites verwendet wird muss ein SettingSection verwendet werdem
		IDialogSettings section = settings.getSection(PROJECTDATEFILTER_SETTINGSECTION);
		if(section == null)
		{
			section = new DialogSettings(PROJECTDATEFILTER_SETTINGSECTION);			
			settings.addSection(section);
		}
		dateFilterComposite.setDialogSettings(section);
	}
	
	// Dialogsettings speichern
	public void saveDialogSettings(IDialogSettings settings)
	{
		// da 'DateFilterComposite' auch von anderen Such-Composites verwendet wird muss ein SettingSection verwendet werdem
		IDialogSettings section = settings.getSection(PROJECTDATEFILTER_SETTINGSECTION);
		if(section == null)
		{
			section = new DialogSettings(PROJECTDATEFILTER_SETTINGSECTION);
			settings.addSection(section);
		}
		dateFilterComposite.saveDialogSettings(section);
	}

	public SearchOptions getSearchOptions()
	{
		SearchOptions searchOptions = new SearchOptions();
		
		// den eingegebenen Pattern uebernehmen
		searchOptions.setSearchPattern(textProjectID.getText());
		
		// die anderen Parameter werden nicht unterstuetzt
		searchOptions.setCaseSensitive(false);
		searchOptions.setRegularExpression(false);
		
		// mit leerer Liste initialisieren
		searchOptions.setSearchItems(new ArrayList<IAdaptable>());
		
		return searchOptions;
	}
	
	public DateFilterOptions getFilterOptions()
	{
		return dateFilterComposite.getFilterOptions();
	}


	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
