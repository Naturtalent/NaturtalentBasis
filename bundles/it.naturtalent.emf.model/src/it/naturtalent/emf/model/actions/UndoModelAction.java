package it.naturtalent.emf.model.actions;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.StructuredViewer;

import it.naturtalent.emf.model.ModelEventKeys;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class UndoModelAction extends DefaultModelAction
{
	private static EditingDomain domain;
	
	public UndoModelAction(StructuredViewer viewer)
	{
		super(viewer);	
		setImageDescriptor(Icon.COMMAND_UNDO.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		setToolTipText("undo");		
	}

	@Override
	public void seteObject(EObject eObject)
	{		
		super.seteObject(eObject);
		domain = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
	}

	@Override
	public void run()
	{
		if (domain != null)
		{
			Command command = domain.getCommandStack().getUndoCommand();			
			//Class cmdClass = command.getClass();
			//if(cmdClass.equals(AddOrdnerAction.))
			
			
			domain.getCommandStack().undo();
			setEnabled(domain.getCommandStack().canUndo());
		}
	}
	
	

}
