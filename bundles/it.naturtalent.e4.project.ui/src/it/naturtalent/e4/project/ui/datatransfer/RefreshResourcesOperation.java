package it.naturtalent.e4.project.ui.datatransfer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

public class RefreshResourcesOperation implements IRunnableWithProgress
{
	private List<IResource>iResources;
	private final static String REFRESHOPERATION_TITLE = "Refreshing"; 
	private int totalWork = IProgressMonitor.UNKNOWN;
	private List<IStatus> errorTable = new ArrayList<IStatus>(1);
	
	public RefreshResourcesOperation(List<IResource> iResources)
	{
		super();
		this.iResources = iResources;
	}



	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		if((iResources != null) && (!iResources.isEmpty()))
		{
			totalWork = iResources.size(); 
			monitor.beginTask(REFRESHOPERATION_TITLE,totalWork);
			for(IResource iResource : iResources)
			{
				try
				{
					iResource.refreshLocal(IResource.DEPTH_INFINITE, null);
					monitor.worked(1);
				} catch (CoreException e)
				{							
					errorTable.add(new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0,"Resourcename",e));
				}				
			}
		}
		monitor.done();	
	}

}
