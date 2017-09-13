package it.naturtalent.e4.project.ui.ws;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;

public class WorkingSetRoot implements IAdaptable
{
	
	public WorkingSetRoot(IWorkingSet[] workingSets)
	{		
		this.workingSets = workingSets;
	}
	

	public WorkingSetRoot()
	{				
	}


	private IWorkingSet [] workingSets;
	

	
	
	public IWorkingSet[] getWorkingSets()
	{
		return workingSets;
	}



	public void setWorkingSets(IWorkingSet[] workingSets)
	{
		this.workingSets = workingSets;
	}



	@Override
	public Object getAdapter(Class adapter)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
