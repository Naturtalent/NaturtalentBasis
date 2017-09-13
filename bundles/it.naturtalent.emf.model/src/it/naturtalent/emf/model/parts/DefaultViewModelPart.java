package it.naturtalent.emf.model.parts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import it.naturtalent.emf.model.actions.DefaultModelAction;
import it.naturtalent.emf.model.actions.DomainActions;




public class DefaultViewModelPart
{

	protected final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	protected StructuredViewer viewer;
	
	protected ToolBarManager toolBarManager;
	
	public enum ViewActionID
	{
		ADD_TASK,
		EDIT_TASK,
		DELETE_TASK,
		SAVE_TASK,
	}
	protected Map<ViewActionID, Action>actionRegistry = new HashMap<ViewActionID, Action>();

	
	protected void createSectionToolbar(Section section)
	{
		toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);		
		section.setTextClient(toolbar);
		
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
	
		
		createActions();

		DomainActions defaultActions = new DomainActions();		
		Action action = defaultActions.createUndoAction();
		toolBarManager.add(action);


		
		toolBarManager.update(true);		
	}
	
	protected void createActions()
	{

		Action action;
		
		action = new DefaultModelAction(viewer);
		toolBarManager.add(action);
		actionRegistry.put(ViewActionID.ADD_TASK, action);

	}
	
	protected void preDestroy()
	{
		formToolkit.dispose();		
	}


}
