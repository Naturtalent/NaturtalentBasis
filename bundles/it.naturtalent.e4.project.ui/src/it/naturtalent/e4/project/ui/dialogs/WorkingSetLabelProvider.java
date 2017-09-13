package it.naturtalent.e4.project.ui.dialogs;

import java.io.File;

import it.naturtalent.e4.project.ui.Activator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.wb.swt.ResourceManager;

public class WorkingSetLabelProvider extends LabelProvider
{
	public Image getImage(Object object)
	{		
		Image icon = ResourceManager.getPluginImage(Activator.PLUGIN_ID, Activator.ICONS_RESOURCE_FOLDER+File.separator+"workingsets.gif");
		return icon;
	}

	public String getText(Object object)
	{
		Assert.isTrue(object instanceof IWorkingSet);
		IWorkingSet workingSet = (IWorkingSet) object;
		return workingSet.getLabel();
	}
}
