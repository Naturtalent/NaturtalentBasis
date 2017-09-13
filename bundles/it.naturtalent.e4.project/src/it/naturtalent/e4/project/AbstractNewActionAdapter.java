package it.naturtalent.e4.project;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

@Deprecated
public abstract class AbstractNewActionAdapter implements INewActionAdapter
{

	protected Action newAction;
	
	protected Class<? extends Action> newActionClass;
	
	@Override
	public abstract String getLabel();
	

	@Override
	public Image getImage()
	{		
		return null;
	}

	@Override
	public String getCategory()
	{		
		return null;
	}
	
	@Override
	public Class<? extends Action> getActionClass()
	{
		return newActionClass;
	}

	@Override
	public String getMessage()
	{		
		return null;
	}
	
	@Override
	public Action getAction()
	{		
		if(newAction == null)
			try
			{
				newAction = newActionClass.newInstance();
			} catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return newAction;
	}

	@Override
	public void setAction(Action newAction)
	{
		this.newAction = newAction;
	}
	

}
