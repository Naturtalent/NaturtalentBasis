package it.naturtalent.e4.project.ui.actions.emf;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import it.naturtalent.e4.project.ui.dialogs.ProjectPropertyDialog;

public class SelectProjectPropertyAction extends Action
{

	@Override
	public void run()
	{
		IEclipseContext context = E4Workbench.getServiceContext();
		
		ProjectPropertyDialog propertyDialog = ContextInjectionFactory.make(ProjectPropertyDialog.class, context);	
		
		// Dialog buchbarer Projekteigenschaften
		/*
		ProjectPropertyDialog propertyDialog = new ProjectPropertyDialog(
				Display.getDefault().getActiveShell());
				*/
		
		if(propertyDialog.open() == ProjectPropertyDialog.OK)
		{
			
		}
		
		

	}

}
