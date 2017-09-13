 
package it.naturtalent.e4.project.ui.handlers.emf;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.ui.actions.emf.NewProjectAction;

/**
 * @author A682055
 *
 * Handler zum Erzeugen eines neuen Projekts.
 * 
 */
public class CreateProjectHandler
{
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	@Inject @Optional public IEclipseContext context;
	
	@Execute
	public void execute(Shell shell)
	{

		NewProjectAction newProjectAction = ContextInjectionFactory.make(NewProjectAction.class, context);
		newProjectAction.run();
	}
		
}