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
		return projectSearchComposite.getSearchOptions();
	}


	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
