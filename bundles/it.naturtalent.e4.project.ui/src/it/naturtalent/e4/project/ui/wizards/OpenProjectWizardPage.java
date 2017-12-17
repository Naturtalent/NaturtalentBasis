package it.naturtalent.e4.project.ui.wizards;

import it.naturtalent.e4.project.ProjectData;

public class OpenProjectWizardPage extends DefaultProjectWizardPage
{

	@Override
	protected void initProperties()
	{
		if(iProject != null)
		{
			// mit den Properties des selektierten Projekts vorbelegen 
			if(ntProjectPropertyFactoryRepository != null)
			{
				if(projectProperty != null)
				{
					// Modell mit den ProjektDaten laden
					projectProperty.setNtProjectID(iProject.getName());
					//projectProperty.init();
					
					// die ProjectDaten explizit (wg. Databinding) 
					setProjectData((ProjectData) projectProperty.getNtPropertyData());
				}				
			}
		}
	}

	
	
	
}
