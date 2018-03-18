package it.naturtalent.e4.project.ui.actions.emf;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;

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
			// alle Adapter aufrufen
			List<INtProjectPropertyFactory> projectPropertyFactories = projektDataFactoryRepository
					.getAllProjektDataFactories();
			for(INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
			{
				// Adapter erzeugen
				INtProjectProperty propertyAdapter = propertyFactory.createNtProjektData();
				
				// Importfunktion des Adapters aufrufen
				propertyAdapter.setNtProjectID(iProject.getName());
				propertyAdapter.delete();							
			}

			// alle dem iProject zugeordneten PropertyFactories auflisten und loeschen
			/*
			List<INtProjectProperty> projectProperties = NtProjektPropertyUtils
					.getProjectProperties(projektDataFactoryRepository,iProject);
			if (projectProperties != null)
			{
				for (INtProjectProperty projectProperty : projectProperties)
					projectProperty.delete();
			}
			*/
			
			//eventBroker.post(NtProjectView.UPDATE_PROJECTVIEW_REQUEST, null);
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
