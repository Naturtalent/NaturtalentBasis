package it.naturtalent.e4.project.ui.handlers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class FileExplorerHandler extends SelectedResourcesUtils
{
	@Execute
	public void execute(MPart part)
	{		
		IResource resource = getSelectedResource(part);
		String filePath = resource.getLocation().toOSString();
		
		
		/*
		switch (resource.getType())
			{
				case IResource.PROJECT:
					filePath = resource.getLocation().toOSString();
					break;
					
				case IResource.FOLDER:
					break;

				case IResource.FILE:
					break;

				default: break;
			}
			*/
		
		if(StringUtils.isNotEmpty(filePath))
		{
			try
			{
				//os = System.getProperty("os.name");
				if (SystemUtils.IS_OS_LINUX)
					Runtime.getRuntime().exec("nautilus " + filePath);
				else
					Runtime.getRuntime().exec("explorer " + filePath);

			} catch (Exception exp)
			{
				exp.printStackTrace();
			}	
		}
	}
	
	@CanExecute
	public boolean canExecute(MPart part)
	{
		return resourceIsType(getSelectedResource(part), IResource.PROJECT
				| IResource.FOLDER | IResource.FILE);
	}

}