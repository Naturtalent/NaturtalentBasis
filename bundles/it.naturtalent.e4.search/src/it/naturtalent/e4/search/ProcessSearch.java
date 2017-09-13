package it.naturtalent.e4.search;

import javax.inject.Inject;

import it.naturtalent.e4.project.search.IProjectSearchPageRegistry;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class ProcessSearch
{

	@Inject
	@Optional
	MApplication application;
	
	@Inject
	@Optional
	EPartService ePartService;

	@Inject
	@Optional
	IProjectSearchPageRegistry searchPageregistry;


	@Execute
	public void init()
	{
		if (searchPageregistry != null)
		{
			Activator.searchPageregistry = searchPageregistry;
			searchPageregistry.addSearchPage(ProjectSearchPage.PROJECTSEARCHPAGE_ID,new ProjectSearchPage());
			searchPageregistry.addSearchPage(FolderSearchPage.FOLDERSEARCHPAGE_ID,new FolderSearchPage());
			searchPageregistry.addSearchPage(FileSearchPage.FILESEARCHPAGE_ID,new FileSearchPage());
		}
		
		Activator.ePartService = ePartService;
		Activator.application = application;
	}

	
}
