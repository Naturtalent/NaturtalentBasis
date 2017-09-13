package it.naturtalent.e4.project;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

public class ProcessProject
{
	public static @Inject @Optional IProjectDataFactory projectDataFactory;
	public static ProjectDataAdapterRegistry registry;
	
	@Execute
	public void init(IEclipseContext context)
	{		
		registry = ContextInjectionFactory.make(ProjectDataAdapterRegistry.class, context);		
		ProjectDataAdapter projectDataAdapter = new ProjectDataAdapter();
		projectDataAdapter.setProjectDataFactory(projectDataFactory);
		registry.addAdapter(projectDataAdapter);		
	}
}
