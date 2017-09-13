package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ui.Activator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkingSet;


public class SelectedResourcesUtils
{
	//protected List<IResource> selectedResources = new ArrayList<IResource>();
	
	
	private List<IResource>resources;
	private List nonResources;

	protected IWorkingSet getSelectedWorkingSet(MPart part)
	{					
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			IResourceNavigator resourceNavigator = (IResourceNavigator) obj;
			IStructuredSelection selection = (IStructuredSelection) resourceNavigator
			.getViewer().getSelection();
			Object selObject = selection.getFirstElement();
			if (selObject instanceof IWorkingSet)
				return (IWorkingSet) selObject;
		}
		
		return null;
	}

	protected IStructuredSelection getSelection(MPart part)
	{					
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			IResourceNavigator resourceNavigator = (IResourceNavigator) obj;
			return (IStructuredSelection) resourceNavigator
					.getViewer().getSelection();
		}
		
		return null;
	}

	protected IResource [] getSelectedResources(MPart part)
	{					
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			IResourceNavigator resourceNavigator = (IResourceNavigator) obj;
			IStructuredSelection selection = (IStructuredSelection) resourceNavigator
					.getViewer().getSelection();

			return getSelectedResources(selection);			
		}
		
		return null;
	}
	
	protected IResource getSelectedResource(MPart part)
	{
		IResource [] resources = getSelectedResources(part);
		if(ArrayUtils.isNotEmpty(resources))
			return resources[0];
		
		return null;
	}
	
	protected IResource [] getSelectedResources(IStructuredSelection selection)
	{
		computeResources(selection);

		if (resources != null)
			return resources.toArray(new IResource[resources.size()]);

		return null;
	}
	
	protected IResource getSelectedResource(IStructuredSelection selection)
	{
		IResource [] resources = getSelectedResources(selection);
		if(ArrayUtils.isNotEmpty(resources))
			return resources[0];
		
		return null;
	}
	
	protected IProject getSelectedProject (MPart part)
	{
		IResource [] resources = getSelectedResources(part);		
		if(ArrayUtils.isNotEmpty(resources))
		{
			IResource resource = resources[0];			
			return (resource instanceof IProject) ? (IProject) resource
					: resource.getProject();
		}
		
		return null;
	}
	
	/**
	 * Ermittelt die Datenadapter des momentan selektierten Projekts.
	 * 
	 * @param part
	 * @return
	 */
	protected IProjectDataAdapter[] getProjectDataAdapters(MPart part)
	{
		IProjectDataAdapter[] inUseAdapters = null;
		IProject iProject = getSelectedProject(part);
		if (iProject != null)
		{
			NtProject ntProject = new NtProject(iProject);
			List<IProjectDataAdapter> lAdapters = ProjectDataAdapterRegistry
					.getProjectDataAdapters();
			List<IProjectDataAdapter> lProjectAdapters = new ArrayList<IProjectDataAdapter>();
			for (IProjectDataAdapter adapter : lAdapters)
			{
				
				Object obj = adapter.getProjectData(iProject.getName());
				if(obj != null)
				{
					lProjectAdapters.add(adapter);
					continue;
				}
				
				
				IProjectData projectData = Activator.projectDataFactory
						.readProjectData(adapter, ntProject);
				if (projectData != null)
					lProjectAdapters.add(adapter);
			}

			inUseAdapters = lProjectAdapters
					.toArray(new IProjectDataAdapter[lProjectAdapters.size()]);
		}

		return inUseAdapters;
	}
	
	/**
	 * Ermittelt die Datenadapter des momentan selektierten Projekts.
	 * 
	 * @param part
	 * @return
	 */
	public IProjectDataAdapter[] getProjectDataAdapters(IProject iProject)
	{
		IProjectDataAdapter[] inUseAdapters = null;
		if (iProject != null)
		{
			NtProject ntProject = new NtProject(iProject);
			List<IProjectDataAdapter> lAdapters = ProjectDataAdapterRegistry
					.getProjectDataAdapters();
			List<IProjectDataAdapter> lProjectAdapters = new ArrayList<IProjectDataAdapter>();
			for (IProjectDataAdapter adapter : lAdapters)
			{
				// Projektdaten sind in einer Datei gespeichert
				IProjectData projectData = Activator.projectDataFactory
						.readProjectData(adapter, ntProject);
				if (projectData != null)
					lProjectAdapters.add(adapter);
				else
				{
					// Projektdaten irgendwo sonst (z.B. Datenbank)
					Object projectDataObject = adapter.getProjectData(ntProject.getId());
					if(projectDataObject != null)
						lProjectAdapters.add(adapter);
				}
			}

			inUseAdapters = lProjectAdapters
					.toArray(new IProjectDataAdapter[lProjectAdapters.size()]);
		}
		return inUseAdapters;
	}	
	
	/**
	 * Returns whether the current selection consists entirely of resources
	 * whose types are among those in the given resource type mask.
	 * 
	 * @param resourceMask
	 *            a bitwise OR of resource types: <code>IResource</code>.{<code>FILE</code>,
	 *            <code>FOLDER</code>, <code>PROJECT</code>,
	 *            <code>ROOT</code>}
	 * @return <code>true</code> if all resources in the current selection are
	 *         of the specified types or if the current selection is empty, and
	 *         <code>false</code> if some elements are resources of a
	 *         different type or not resources
	 * @see IResource
	 */
	protected boolean selectionIsOfType(int resourceMask)
	{
		if (resources != null)
		{
			for (IResource resource : resources)
			{
				if (!resourceIsType(resource, resourceMask))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	protected boolean resourceIsType(IResource resource, int resourceMask)
	{
		return (resource.getType() & resourceMask) != 0;
	}
	
	/**
	 * Extracts <code>IResource</code>s from the current selection and adds
	 * them to the resources list, and the rest into the non-resources list.
	 */
	private final void computeResources(IStructuredSelection selection)
	{
		resources = null;
		nonResources = null;

		for (Iterator e = selection.iterator(); e.hasNext();)
		{
			Object next = e.next();
			if (next instanceof IResource)
			{
				if (resources == null)
				{
					// assume selection contains mostly resources most times
					resources = new ArrayList(selection.size());
				}
				resources.add((IResource) next);
				continue;
			}
			else if (next instanceof IAdaptable)
			{
				Object resource = ((IAdaptable) next)
						.getAdapter(IResource.class);
				if (resource != null)
				{
					if (resources == null)
					{
						// assume selection contains mostly resources most times
						resources = new ArrayList(selection.size());
					}
					resources.add((IResource) resource);
					continue;
				}
			}
			else
			{

				boolean resourcesFoundForThisSelection = false;

				IAdapterManager adapterManager = Platform.getAdapterManager();
				ResourceMapping mapping = (ResourceMapping) adapterManager
						.getAdapter(next, ResourceMapping.class);

				if (mapping != null)
				{

					ResourceTraversal[] traversals = null;
					try
					{
						traversals = mapping.getTraversals(
								ResourceMappingContext.LOCAL_CONTEXT,
								new NullProgressMonitor());
					} catch (CoreException exception)
					{
						exception.printStackTrace();
					}

					if (traversals != null)
					{

						for (int i = 0; i < traversals.length; i++)
						{

							IResource[] traversalResources = traversals[i]
									.getResources();

							if (traversalResources != null)
							{

								resourcesFoundForThisSelection = true;

								if (resources == null)
								{
									resources = new ArrayList(
											selection.size());
								}

								for (int j = 0; j < traversalResources.length; j++)
								{
									resources.add(traversalResources[j]);
								}// for

							}// if

						}// for

					}// if

				}// if

				if (resourcesFoundForThisSelection)
				{
					continue;
				}
			}

			if (nonResources == null)
			{
				// assume selection contains mostly resources most times
				nonResources = new ArrayList(1);
			}
			nonResources.add(next);
		}
	}

}
