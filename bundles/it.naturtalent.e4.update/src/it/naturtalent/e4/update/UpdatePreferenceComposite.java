package it.naturtalent.e4.update;

import org.eclipse.swt.widgets.Composite;

import it.naturtalent.e4.preferences.ListEditorComposite;

public class UpdatePreferenceComposite extends ListEditorComposite
{

	public UpdatePreferenceComposite(Composite parent, int style)
	{
		super(parent, style);
		
		setDialogTitle("UpdateSite");
		setMessageTitle("eine URL zur UpdateSite");
	}	

}
