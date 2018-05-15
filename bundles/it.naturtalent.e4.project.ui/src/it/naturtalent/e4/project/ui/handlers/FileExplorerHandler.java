package it.naturtalent.e4.project.ui.handlers;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

public class FileExplorerHandler extends SelectedResourcesUtils
{
	@Execute
	public void execute(MPart part)
	{
		ESelectionService selectionService = part.getContext()
				.get(ESelectionService.class);
		Object selObject = selectionService.getSelection();
		if (selObject instanceof IResource)
		{
			IResource iResource = (IResource) selObject;
			String filePath = iResource.getLocation().toOSString();
			System.out.println(filePath);

			if (StringUtils.isNotEmpty(filePath))
			{
				try
				{
					// os = System.getProperty("os.name");
					if (SystemUtils.IS_OS_LINUX)
						Runtime.getRuntime().exec("nautilus " + filePath);					
					else
						Runtime.getRuntime().exec("explorer " + filePath);

				} catch (Exception exp)
				{
					if (SystemUtils.IS_OS_LINUX)
						try
						{
							Runtime.getRuntime().exec("nemo " + filePath);
							return;
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					exp.printStackTrace();
				}
			}
		}
	}
	
	@CanExecute
	public boolean canExecute(MPart part)
	{
		ESelectionService selectionService = part.getContext()
				.get(ESelectionService.class);		
		Object selObject = selectionService.getSelection();
		if (selObject instanceof IResource)
		{
			IResource iResource = (IResource) selObject;
			return ((iResource != null && (iResource.getType() & (IResource.FILE)) == 0));
		}
		
		return false;
	}

}