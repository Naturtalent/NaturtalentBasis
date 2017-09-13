package it.naturtalent.e4.search;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;

public class SearchResultView
{
	private TableViewer tableViewer;
	private Table table;

	// mit dieser ID ist 'SearchResultView' im ApplicatonModel eingetragen
	public static final String SEARCHRESULT_VIEW_ID = "it.naturtalent.search.resultpart";
	
	//private ResourceNavigator resourceNavigator;
	
	private MPart resourceNavigatorPart; 
	
	private IEclipseContext context;

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent, final IEclipseContext context)
	{		
		this.context = context;
		
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectResourceNavigatorEntry();
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				// ResourceNavigator ermitteln und Projekt dort selektieren				
				selectResourceNavigatorEntry();

				// Project oeffnen
				if (resourceNavigatorPart != null)
				{
					/*
					OpenResourceHandler openHandler = ContextInjectionFactory.make(OpenResourceHandler.class, resourceNavigatorPart.getContext());
					ContextInjectionFactory.invoke(openHandler, Execute.class, resourceNavigatorPart.getContext());
					
					
					IEclipseContext contextNavigator = navigatorPart.getContext();
					if(contextNavigator != null)
					{
						OpenResourceHandler openHandler = ContextInjectionFactory.make(OpenResourceHandler.class, contextNavigator);
						ContextInjectionFactory.invoke(openHandler, Execute.class, contextNavigator);
					}
					*/
					
				}
			}
		});
		table = tableViewer.getTable();
		
		tableViewer.setContentProvider(new WorkbenchContentProvider());
		tableViewer.setLabelProvider(new WorkbenchLabelProvider());
		tableViewer.setComparator(new ViewerComparator());
	}

	private void selectResourceNavigatorEntry()
	{
		ResourceNavigator resourceNavigator = (ResourceNavigator) resourceNavigatorPart.getObject();
		
		if(resourceNavigator != null)
		{
			IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
			final IAdaptable adaptable = (IAdaptable) selection.getFirstElement();

			if (adaptable != null)
			{
				TreeViewer treeViewer = resourceNavigator.getViewer();
				if (resourceNavigator.getTopLevelStatus())
				{
					IWorkingSet[] workingSets = resourceNavigator.getWindowWorkingSets();
					for (IWorkingSet workingSet : workingSets)
					{
						IAdaptable[] elements = workingSet.getElements();
						for (IAdaptable element : elements)
						{
							if (element.equals(adaptable))
							{
								treeViewer.expandToLevel(workingSet, 1);
								break;
							}
						}
					}
				}
				
				// ResourceNavigator aktivieren und dort selektieren
				EPartService partService = resourceNavigatorPart.getContext().get(EPartService.class);	
				partService.activate(resourceNavigatorPart);
				
				Object obj = adaptable.getAdapter(Project.class);
				if(obj != null)
					treeViewer.expandToLevel(obj, 1);
				treeViewer.setSelection(new StructuredSelection(adaptable), true);	
			}
		}
	}
	
	private MPart selectResourceNavigatorEntryOLD()
	{
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		final IAdaptable adaptable = (IAdaptable) selection
				.getFirstElement();

		if (adaptable != null)
		{
			MPart mPart = Activator.ePartService
					.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);

			final IResourceNavigator resourceNavigator = (IResourceNavigator) mPart
					.getObject();

			final StructuredSelection revealSelection = new StructuredSelection(
					adaptable);

			TreeViewer treeViewer = resourceNavigator.getViewer();
			if (resourceNavigator.getTopLevelStatus())
			{
				IWorkingSet[] workingSets = resourceNavigator
						.getWindowWorkingSets();
				for (IWorkingSet workingSet : workingSets)
				{
					IAdaptable[] elements = workingSet.getElements();
					for (IAdaptable element : elements)
					{
						if (element.equals(adaptable))
						{
							treeViewer.expandToLevel(workingSet, 1);
							break;
						}
					}
				}
			}

			treeViewer.setSelection(revealSelection, true);
			return mPart;
		}	
		
		return null;
	}
	
	public void setResourceNavigator(MPart resourceNavigatorPart)
	{
		this.resourceNavigatorPart = resourceNavigatorPart;
	}

	public void setInput(ResourceSearchResult searchResult)
	{
		IResource[]result = searchResult.getResourceResult();
		if(ArrayUtils.isNotEmpty(result))
		{
			if(tableViewer.getInput() != null)		
				tableViewer.remove(tableViewer.getInput());
			
			for(int i = 0;i < result.length;i++)
			{
				if(i == 0)
					tableViewer.setInput(new IAdaptable[]{result[0]});
				
				tableViewer.add(result[i]);
			}			
		}
	}
	
	public void addResource(IAdaptable adaptable)
	{		
		if(tableViewer.getInput() == null)		
			tableViewer.setInput(new IAdaptable[]{adaptable});		
		else tableViewer.add(adaptable);			
	}
	
	public void clear()
	{
		if(tableViewer.getInput() != null)		
			tableViewer.remove(tableViewer.getInput());
	}

	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		// TODO	Set the focus to control
	}

}
