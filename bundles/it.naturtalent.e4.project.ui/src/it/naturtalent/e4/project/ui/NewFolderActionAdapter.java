package it.naturtalent.e4.project.ui;

import it.naturtalent.e4.project.AbstractNewActionAdapter;
import it.naturtalent.e4.project.ui.actions.NewFolderAction;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

@Deprecated
public class NewFolderActionAdapter extends AbstractNewActionAdapter
{

	{ 
		newActionClass = NewFolderAction.class;
	}
	
	@Override
	public String getLabel()
	{				
		return Activator.properties.getProperty(Messages.Explorer_newFolderLabel);
	}

	@Override
	public Image getImage()
	{		
		return ResourceManager.getPluginImage(Activator.PLUGIN_ID, "icons/etools/newfolder_wiz.gif"); //$NON-NLS-N$ //$NON-NLS-1$
	}
	
	@Override
	public String getMessage()
	{		
		return Messages.NewFolderActionAdapter_Message;
	}

}
