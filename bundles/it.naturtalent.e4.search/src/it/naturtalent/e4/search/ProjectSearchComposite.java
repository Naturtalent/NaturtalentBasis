package it.naturtalent.e4.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class ProjectSearchComposite extends Composite
{

	private DefaultNtProjectSearchComposite projectSearchComposite; 
	
	private PropertySearchComposite propertySearchComposite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectSearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		projectSearchComposite = new DefaultNtProjectSearchComposite(this, SWT.NONE);
		projectSearchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Datumsfilter einfuegen
		propertySearchComposite = new PropertySearchComposite(this, SWT.NONE);
		propertySearchComposite.disposePatternEditor();

	}
	
	public void setDialogSettings(IDialogSettings settings)
	{
		projectSearchComposite.setDialogSettings(settings);		
	}
	
	public void saveDialogSettings(IDialogSettings settings)
	{
		projectSearchComposite.saveDialogSettings(settings);		
	}
	
	public SearchOptions getSearchOptions()
	{
		// SearchOptions vom Standard-ProjektSeachComposite abfragen 
		SearchOptions searchOptions = projectSearchComposite.getSearchOptions();
				
		// SearchOptions vom PropertySeachComposite abfragen (Datumsfilter)
		SearchOptions propertyOptions = propertySearchComposite.getPropertySearchOptions(searchOptions.getSearchItems());
		
		// mit den gefilterten Daten aus dem PropertyComposite weiterarbeiten
		searchOptions.setSearchItems(propertyOptions.getSearchItems());
		
		return searchOptions;
	}


	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
