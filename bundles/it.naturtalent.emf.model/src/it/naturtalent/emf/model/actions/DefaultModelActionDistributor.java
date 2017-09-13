package it.naturtalent.emf.model.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;

import it.naturtalent.emf.model.parts.DefaultViewModelPart;



public class DefaultModelActionDistributor
{
	// universelle ActionKeys
	public static final String ADD_ACTION_ID = "addAction";
	public static final String EDIT_ACTION_ID = "editAction";
	public static final String DELETE_ACTION_ID = "deleteAction";
	public static final String SAVE_ACTION_ID = "saveAction";
	public static final String UNDO_ACTION_ID = "undoAction";
	
	private Map<String, DefaultModelAction>actionRegistry = new HashMap<String, DefaultModelAction>();
	
	private Composite toolbarComposite;
	
	private ToolBarManager toolBarManager;
	
	private EditingDomain domain;
	
	protected EObject eObject;
	
	
	public DefaultModelActionDistributor(Composite toolbarComposite)
	{
		super();
		this.toolbarComposite = toolbarComposite;
	}


	public void addAction(String actionID, DefaultModelAction action)
	{
		actionRegistry.put(actionID, action);
		action.setActionDestributor(this);
	}
	
	public ToolBar createActionToolbar()
	{
		toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(toolbarComposite);
		
		final Cursor handCursor = new Cursor(Display.getCurrent(),SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (handCursor.isDisposed() == false)				
					handCursor.dispose();				
			}
		});
		
		Set<String>keys = actionRegistry.keySet();
		for(String key : keys)
			toolBarManager.add(actionRegistry.get(key));
			
		toolBarManager.update(true);	
		return toolbar;
	}
	
	/**
	 * Das ausgewaehlte EObject an alle Aktionen ubergeben.
	 * EditingDomain des selektierten Objects ermitteln.
	 *  
	 * @param eObject
	 */
	public void setSelectedObject(EObject eObject)
	{		
		this.eObject = eObject;
		domain = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
		
		Set<String>keys = actionRegistry.keySet();
		for(String key : keys)
		{
			DefaultModelAction defaultAction = actionRegistry.get(key);
			defaultAction.seteObject(eObject);
			//defaultAction.setEnabled(true);
		}

	}

	public boolean canUndo()
	{
		EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
		if(domain != null)
			return domain.getCommandStack().canUndo();
		
		return false;
	}
	
	public void undo(EObject eObject)
	{
		EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);				
		if(domain != null)			
			while(domain.getCommandStack().canUndo())
				domain.getCommandStack().undo();
	}
	
	public void	run(String actionKey)
	{
		DefaultModelAction action = actionRegistry.get(actionKey);
		if(action != null)
			action.run();
	}


	public void setEnable(String key, boolean enable)
	{
		Action action = actionRegistry.get(key);
		if(action != null)
			action.setEnabled(enable);
	}


	public Map<String, DefaultModelAction> getActionRegistry()
	{
		return actionRegistry;
	}


	public EditingDomain getDomain()
	{
		return domain;
	}

	
	
	
}
