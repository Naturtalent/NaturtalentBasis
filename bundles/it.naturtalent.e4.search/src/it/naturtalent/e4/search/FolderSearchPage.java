package it.naturtalent.e4.search;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.internal.resources.Container;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.search.HitRange;
import it.naturtalent.e4.project.search.ResourceSearchHit;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.project.search.SearchOptions;
import it.naturtalent.e4.project.search.SearchResult;
import it.naturtalent.e4.project.search.TextSearcher;
import it.naturtalent.e4.project.search.textcomponents.ComponentPath;
import it.naturtalent.e4.project.search.textcomponents.TextComponent;
import it.naturtalent.e4.project.search.textcomponents.TextComponentType;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

public class FolderSearchPage extends ProjectSearchPage
{
	public static final String FOLDERSEARCHPAGE_ID = "02foldersearch";
	
	private FolderSearchComposite folderSeachComposite;
	
	private FileFilter directoryFilter = new FileFilter()
	{
		public boolean accept(File file)
		{
			return file.isDirectory();
		}
	};
	
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		MPart mPart = Activator.ePartService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if(mPart != null)
			resourceNavigator = (ResourceNavigator) mPart.getObject();
		
		// Ergebnisview anzeigen
		mPart = Activator.ePartService.findPart(SearchResultView.SEARCHRESULT_VIEW_ID);
		resultView = (SearchResultView) mPart.getObject();
		
		
		folderSeachComposite = new FolderSearchComposite(parent, SWT.NONE); 
		folderSeachComposite.setResourceNavigator(resourceNavigator);
		return folderSeachComposite;
	}
	
	
	
	
	@Override
	public void performSearch(IProgressMonitor progressMonitor)
	{
		final SearchOptions searchOptions = new SearchOptions();
		
		searchOptions.setSearchPattern(folderSeachComposite.getResultSearchPattern());		
		searchOptions.setCaseSensitive(folderSeachComposite.getCaseSensitve());
		searchOptions.setWholeWordOnly(isWholeWordOnly);
					
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException
			{				
				SearchResult result = performSearch(searchOptions, monitor);
				monitor.done();
			}
		};

		try
		{
			ModalContext.run(runnable, false, progressMonitor, shell.getDisplay());
			
		} catch (InvocationTargetException e)
		{
			MessageDialog.openError(shell,"Fehler beim suchen", e.getMessage());
			
		} catch (InterruptedException e)
		{
			
			//clearResultCountLabel();
			//setSearchResult(null);
			
			/*
			MessageDialog.openError(getShell(),
					Messages.TelekomProjektSearchPage_TitleSearch,
					Messages.TelekomProjektSearchPage_CancelSearching);
					*/
		}
	}




	protected SearchResult performSearch(final SearchOptions searchOptions,
			IProgressMonitor monitor) throws InterruptedException
	{
		
		IAdaptable[] adaptables = folderSeachComposite.getResultAdaptables();
	
		if (ArrayUtils.isNotEmpty(adaptables) && (resourceNavigator != null))
		{		
			String resourceName;
			IResource resource;
			
			searchResult = new ResourceSearchResult();						
			monitor.beginTask("Suche in  Projekten", adaptables.length);			
			for (IAdaptable adaptable : adaptables)
			{				
				if (monitor.isCanceled())
				{
					throw new InterruptedException();
				}
 
				resource = null;
				if(adaptable instanceof IResource)
					resource = (IResource) adaptable;
				
				if(resource == null)
					continue;
				
				resourceName = resource.getName();				
				if(adaptable instanceof IProject)
				{
					IProject iProject = (IProject)adaptable;
					
					try
					{
						IResource[] members = iProject.members();
						if(ArrayUtils.isNotEmpty(members))
						{
							for (IResource member : members)
							{
								//searchFolderInPath(member.getLocation());
							}
						}						
						
					} catch (CoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			
				// Name des Zielprojekts im Monitor anzeigen				
				monitor.subTask(Messages.bind("Projekt: ", resourceName));
				
				// Such nach Projektnamen
				if (StringUtils.isNotEmpty(searchOptions.getSearchPattern()))
				{
					textComponent = new TextComponent(
							TextComponentType.TEXT,
							"Text component", //$NON-NLS-1$
							resourceName, new ComponentPath());

					HitRange hitRange = TextSearcher.getFirstHit(textComponent,
							searchOptions);
					if (hitRange != null)
					{
						if (!isSearchResultContains(resource))
						{
							ResourceSearchHit hit = new ResourceSearchHit(
									resource, textComponent, hitRange);
							searchResult.addHit(hit);
						}
					}
				}
			
						

				monitor.worked(1);
			}
		}
		
		return searchResult;
	}
	
	
	private void searchFolderInPath(IPath ipath)
	{
		final File level = ipath.toFile();
				
		File[] files = level.listFiles(directoryFilter);
		if (ArrayUtils.isNotEmpty(files))
		{
			for (File file : files)
				System.out.println(file.getName());
		}
		
	}
	
	@Override
	public String getLabel()
	{
		return "Verzeichnis";
	}
	
	
}
	
