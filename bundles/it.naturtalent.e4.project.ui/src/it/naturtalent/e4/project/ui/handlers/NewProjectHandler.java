package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.ui.actions.NewProjectAction;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;


@Deprecated
public class NewProjectHandler  extends SelectedResourcesUtils
{
	@Inject @Optional public IEclipseContext context;
	
	@Execute
	public void execute(Shell shell, MPart part)
	{		
		NewProjectAction newProjectAction = ContextInjectionFactory.make(NewProjectAction.class, context);
				
		if (part != null)
		{
			Object obj = part.getObject();
			if (obj instanceof ResourceNavigator)
				newProjectAction.setResourceNavigator((ResourceNavigator) obj);				
		}
		
		newProjectAction.run();		
	}
	
	@CanExecute
	public boolean canExecute(MPart part)
	{		
		if(part != null)
		{
			Object obj = part.getObject();
			return (obj instanceof ResourceNavigator);			
		}
		
		return false;
	}
	
	
}
