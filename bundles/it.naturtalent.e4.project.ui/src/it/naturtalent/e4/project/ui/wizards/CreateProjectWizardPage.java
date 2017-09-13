package it.naturtalent.e4.project.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.ProjectData;

@Deprecated
public class CreateProjectWizardPage extends DefaultProjectWizardPage
{

	/**
	 * Create the wizard.
	 */
	public CreateProjectWizardPage()
	{
		super();
		setTitle("Neues Projekt erzeugen");
		setDescription("Projekt definieren");
	}

	
	@Override
	protected void initProperties()
	{		
		ProjectData projectData = new ProjectData();
		if(iProject != null)
		{			
			try
			{
				// mit dem Namen des selektierten Projekts vorbelegen 
				projectData.setName(iProject.getPersistentProperty(
						INtProject.projectNameQualifiedName));
				txtProjectName.addFocusListener(new FocusAdapter()
				{
					@Override
					public void focusGained(FocusEvent e)
					{
						// vorgegebenen Projektnamen markieren
						txtProjectName.setSelection(0,
								txtProjectName.getText().length());
					}
				});
			} catch (CoreException e1)
			{
				
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//projectProperty.setNtPropertyData(projectData);
		
		// PropertyDaten an den Wizard uebergegen
		setProjectData(projectData);
		txtDescription.setText("");		
	}
	
}
