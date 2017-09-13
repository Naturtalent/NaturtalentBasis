package it.naturtalent.emf.model.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.DeleteAction;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.action.UndoAction;
import org.eclipse.jface.action.Action;

import it.naturtalent.emf.model.parts.DefaultViewModelPart.ViewActionID;

public class DomainActions extends EditingDomainActionBarContributor
{

	@Override
	public UndoAction createUndoAction()
	{
		AdapterFactoryEditingDomain a;
		
		
		//EClass newMEType = Archive
				
		EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(null);
		
		return super.createUndoAction();
	}


	

	
	
}
