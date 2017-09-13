package it.naturtalent.e4.project.ui.wizards;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Messages;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;

public class NewProjectWizardPage extends ProjectWizardPage
{
	
	public NewProjectWizardPage(IResourceNavigator navigator)
	{
		super(navigator);
		setTitle(Messages.NewProjectWizardPage_title);
		setDescription(Messages.NewProjectWizardPage_description);
	}
	
	@Override
	public void createControl(Composite parent)
	{		
		super.createControl(parent);		
	}

	@Override
	protected void init()
	{
		super.init();
		
		txtProjectName.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				// vorgegebenen Projektnamen markieren
				txtProjectName.setSelection(0, txtProjectName.getText().length());
			}
		});		
	}


	
	

}
