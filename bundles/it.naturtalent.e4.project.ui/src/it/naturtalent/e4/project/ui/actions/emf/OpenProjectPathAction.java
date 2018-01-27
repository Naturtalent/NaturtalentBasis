package it.naturtalent.e4.project.ui.actions.emf;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import it.naturtalent.emf.model.actions.DefaultModelAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;


/**
 * Selektierten Pfad mit dem System-Dateiexplorer oeffnen
 * 
 * @author dieter
 *
 */
public class OpenProjectPathAction extends DefaultModelAction
{

	public OpenProjectPathAction(StructuredViewer viewer)
	{
		super(viewer);
		setImageDescriptor(Icon.ICON_FOLDER.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		setEnabled(false);
	}

	@Override
	public void run()
	{		
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object selObject = selection.getFirstElement();
		if (selObject instanceof IResource)
		{
			try
			{
				IResource iResource = (IResource) selObject;
				String destPath = iResource.getLocation().toOSString();
				
				//os = System.getProperty("os.name");
				if (SystemUtils.IS_OS_LINUX)
					Runtime.getRuntime().exec("nautilus " + destPath);
				else
					Runtime.getRuntime().exec("explorer " + destPath);

			} catch (Exception exp)
			{
				exp.printStackTrace();
			}
		}
		
		
		System.out.println("Aktion Open");
	}

	@Override
	public boolean canRun()
	{
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object selObject = selection.getFirstElement();
		if (selObject instanceof IResource)
		{
			IResource iResource = (IResource) selObject;			
			if((iResource.getType() & (IResource.FOLDER) | (IResource.PROJECT) | (IResource.FILE)) != 0)
				return true;
		}
		
		return false;
	}
	
	

}
