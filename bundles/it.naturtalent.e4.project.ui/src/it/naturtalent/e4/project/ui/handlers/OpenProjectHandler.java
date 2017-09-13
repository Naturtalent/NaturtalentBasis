 
package it.naturtalent.e4.project.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.ui.actions.OpenProjectAction;

public class OpenProjectHandler
{
	@Inject @Optional public IEclipseContext context;
	
	@Execute
	public void execute(Shell shell)
	{
		OpenProjectAction openProject = ContextInjectionFactory.make(OpenProjectAction.class, context);
		openProject.run();
	}
	

}