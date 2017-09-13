package it.naturtalent.e4.project.ui;

import it.naturtalent.e4.project.AbstractNewActionAdapter;
import it.naturtalent.e4.project.ui.actions.NewFileAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

@Deprecated
public class NewFileActionAdapter extends AbstractNewActionAdapter
{

	{ 
		newActionClass = NewFileAction.class;
	}
	
	@Override
	public String getLabel()
	{				
		return Activator.properties.getProperty(Messages.Explorer_newFileLabel);
	}

	@Override
	public Image getImage()
	{				
		return Icon.DIALOG_NEW_FILE.getImage(IconSize._16x16_DefaultIconSize);
	}
	
	@Override
	public String getMessage()
	{		
		return Messages.NewFileActionAdapter_Message;
	}

}
