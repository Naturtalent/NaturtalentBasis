package it.naturtalent.e4.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

/**
 * Composite zum Einblenden der zur Projekt-Suchfunktion erforderlichen Eingabefeldder.
 * 
 * @author dieter
 *
 */
public class ProjectSearchComposite extends Composite
{

	// Setting-Section fuer DateFilterComposite 
	private static final String PROJECTDATEFILTER_SETTINGSECTION = "projectdatesettingsection"; //$NON-NLS-1$
		
	private DefaultNtProjectSearchComposite projectSearchComposite; 
	
	// Datumsfilter 
	private DateFilterComposite dateFilterComposite;
	
	//private PropertySearchComposite propertySearchComposite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectSearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		// Grundcomposite der Projektsuche
		projectSearchComposite = new DefaultNtProjectSearchComposite(this, SWT.NONE);
		projectSearchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Datumsfilter einfuegen
		dateFilterComposite = new DateFilterComposite(this, SWT.NONE);
		
		projectSearchComposite.setFocus();
	}
	
	// Dialogsettings laden
	public void setDialogSettings(IDialogSettings settings)
	{		 
		projectSearchComposite.setDialogSettings(settings);
		
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

		projectSearchComposite.saveDialogSettings(settings);
	}
	
	public SearchOptions getSearchOptions()
	{
		// SearchOptions vom Standard-ProjektSeachComposite abfragen 
		SearchOptions searchOptions = projectSearchComposite.getSearchOptions();
				
		// mit den gefilterten Daten aus dem PropertyComposite weiterarbeiten
		//searchOptions.setSearchItems(propertyOptions.getSearchItems());
		
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
