package it.naturtalent.e4.search.handlers;

import javax.inject.Inject;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.search.IProjectSearchPageRegistry;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.search.Activator;
import it.naturtalent.e4.search.SearchResultView;
import it.naturtalent.e4.search.dialogs.SearchDialog;
import it.naturtalent.e4.search.parts.SearchView;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartService;

public class SearchHandler 
{

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

	@Inject
	EPartService partService;
	
	private static final String ResourceNavigatorViewID = "it.naturtalent.e4.project.ui.part.explorer";
	

	@Execute
	public void execute(Shell shell, MPart part, IEclipseContext context)
	{
		// SearchView aktivieren (sichtbar machen)
		partService.showPart(SearchView.SEARCHVIEW_ID, PartState.ACTIVATE);
		
		SearchDialog searchDialog = ContextInjectionFactory.make(SearchDialog.class, context);		
		if(searchDialog.open() == SearchDialog.OK)
		{
			/*
			ISearchInEclipsePage page = searchDialog.getSearchPage();
			ResourceSearchResult result = (ResourceSearchResult) page.getResult();
			IAdaptable [] resources = result.getResourceResult();
			if (ArrayUtils.isNotEmpty(resources))
			{		
				// Fenster mit den Suchergebnissen zeigen			
				MPart mPart = partService
						.findPart(SearchResultView.SEARCHRESULT_VIEW_ID);
				mPart.setVisible(true);				
				partService.showPart(mPart, PartState.ACTIVATE);
				SearchResultView resultView = (SearchResultView) mPart.getObject();
				
				// den Resourecenavigator im Applicationmodel suchen
				Object obj = modelService.find(ResourceNavigatorViewID,application);
				if((obj instanceof PartImpl) && (resultView != null))
				{
					resultView.setResourceNavigator((MPart) obj);						
					resultView.setInput(result);
				}
			}
			*/
		}		
	}

	@CanExecute
	public boolean canExecute()
	{
		return true;
	}

}