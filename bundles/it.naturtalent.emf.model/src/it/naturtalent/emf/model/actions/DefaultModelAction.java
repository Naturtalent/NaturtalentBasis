package it.naturtalent.emf.model.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;




public class DefaultModelAction extends Action
{
	protected StructuredViewer viewer;
	
	protected DefaultModelActionDistributor actionDestributor;
	
	protected EObject eObject;
	
	//protected IEventBroker eventBroker = Activator.getEventBroker();
	

	public DefaultModelAction(StructuredViewer viewer)
	{
		super();
		this.viewer = viewer;		
	}


	public DefaultModelActionDistributor getActionDestributor()
	{
		return actionDestributor;
	}


	public void setActionDestributor(DefaultModelActionDistributor actionDestributor)
	{
		this.actionDestributor = actionDestributor;
	}
	
	public void undo()
	{
		if(actionDestributor != null)
			actionDestributor.undo(eObject); 
	}


	public EObject geteObject()
	{
		return eObject;
	}


	public void seteObject(EObject eObject)
	{
		this.eObject = eObject;
	}
	
	public boolean canRun()
	{
		return true;
	}
}
