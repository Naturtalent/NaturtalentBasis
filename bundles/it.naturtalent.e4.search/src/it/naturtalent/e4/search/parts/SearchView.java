 
package it.naturtalent.e4.search.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class SearchView
{
	/*
	 * Labelprovider der Projektsuche
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof IProject)
			{
				IProject iProject = (IProject) element;
				try
				{
					return iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
				} catch (CoreException e)
				{					
				}
				
			}
			return element.toString();
		}
	}
	
	public static final String SEARCHVIEW_ID = "it.naturtalent.search.resultpart";
	
	
	private Table table;
	
	// UI
	private TableViewer tableViewer;
	
	@Inject
	public SearchView()
	{

	}

	@PostConstruct
	public void postConstruct(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));
		
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectResourceNavigatorEntry();
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
	}
	
	/*
	 * das im Suchfenster selektierte Projekt auch im ResourceNavigator selektiern
	 */
	private void selectResourceNavigatorEntry()
	{
		ResourceNavigator resourceNavigator = (ResourceNavigator) Activator.findNavigator();
		
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

				// gesuchtes Projekt selektieren, oeffnen und Focus auf ResourceNavigator
				Object obj = adaptable.getAdapter(Project.class);
				if(obj != null)
					treeViewer.expandToLevel(obj, 1);
				treeViewer.setSelection(new StructuredSelection(adaptable), true);
				treeViewer.getTree().setFocus();
			}
		}
	}


	@Inject
	@Optional
	public void handleStartSearchEvent(@UIEventTopic(ISearchInEclipsePage.START_SEARCH_EVENT) String startMessage)
	{
		// zum Beginn der Suche wird die Tabelle geloescht
		tableViewer.setInput(new IAdaptable[] {});
		tableViewer.add(startMessage);
	}

	@Inject
	@Optional
	public void handleMatchPatternEvent(@UIEventTopic(ISearchInEclipsePage.MATCH_PATTERN_EVENT) Object searchItem)
	{
		// einen weiteren Matchpattern in den TableViewer uebernehmen
		tableViewer.add(searchItem);
	}

	

}
