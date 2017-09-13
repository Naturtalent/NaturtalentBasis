package it.naturtalent.e4.project.ui.actions.emf;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;

/**
 * Loescht alle Eigenschaften des dem momentan im Navigator selektierten
 * Projekts.
 * 
 * @author dieter
 *
 */
public class DeleteProjectAction extends Action
{
	private IProject iProject;

	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;
	
	private IEventBroker eventBroker;
	
	public static final String DELETE_PROJECT_EVENT = "deleteprojectevent";

	@Override
	public void run()
	{
		if ((iProject != null) && (projektDataFactoryRepository != null))
		{
			// alle dem iProject zugeordneten PropertyFactories auflisten und loeschen
			List<INtProjectProperty> projectProperties = NtProjektPropertyUtils
					.getProjectProperties(projektDataFactoryRepository,iProject);
			if (projectProperties != null)
			{
				for (INtProjectProperty projectProperty : projectProperties)
					projectProperty.delete();
				
				/*
				String msg = null;
				try
				{
					msg = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				eventBroker.send(DELETE_PROJECT_EVENT, msg);
				*/
			}
		}
	}

	@PostConstruct
	private void postConstruct(
			@Optional INtProjectPropertyFactoryRepository projektDataFactoryRepository,
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject,
			@Optional IEventBroker eventBroker)
	{
		this.projektDataFactoryRepository = projektDataFactoryRepository;
		this.iProject = iProject;
		this.eventBroker = eventBroker;
	}

}
