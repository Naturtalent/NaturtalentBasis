package it.naturtalent.e4.project.ui;

import it.naturtalent.e4.project.AbstractNewActionAdapter;
import it.naturtalent.e4.project.ui.actions.NewProjectAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

@Deprecated
public class NewProjectActionAdapter extends AbstractNewActionAdapter
{

	{ 
		newActionClass = NewProjectAction.class;
	}
	
	@Override
	public String getLabel()
	{				
		return Activator.properties.getProperty(Messages.Explorer_newProjectLabel);
	}
	
	@Override
	public Image getImage()
	{		
		return Icon.DIALOG_NEW_PROJECT.getImage(IconSize._16x16_DefaultIconSize);		
	}
	
	@Override
	public String getMessage()
	{		
		return Messages.NewProjectActionAdapter_Message;
	}

}
