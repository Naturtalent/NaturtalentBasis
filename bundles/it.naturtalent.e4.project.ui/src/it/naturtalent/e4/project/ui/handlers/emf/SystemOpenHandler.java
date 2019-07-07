 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

/**
 * 
 * 
 * @author dieter
 *
 */
@Deprecated //@see SystenOpenEditorAction
public class SystemOpenHandler
{
	@Inject @Optional private ESelectionService selectionService;
	
	@Execute
	public void execute()
	{
		Object selObject = selectionService.getSelection(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if (selObject instanceof IResource)
		{
			IResource iResource = (IResource) selObject;
			String destPath = iResource.getLocation().toOSString();
			try
			{				
				if (SystemUtils.IS_OS_LINUX)
					Runtime.getRuntime().exec("nautilus " + destPath);
				else
					Runtime.getRuntime().exec("explorer " + destPath);

			} catch (Exception exp)
			{
				if (SystemUtils.IS_OS_LINUX)
					try
					{
						Runtime.getRuntime().exec("nemo " + destPath);
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
	
	@CanExecute
	public boolean canExecute()
	{
		Object selObject = selectionService.getSelection(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if(selObject instanceof IResource)
		{			
			IResource iResource = (IResource) selObject;
			return ((iResource != null && (iResource.getType() & (IResource.FILE)) == 0));
			
		}		
		return false;
	}
		
}