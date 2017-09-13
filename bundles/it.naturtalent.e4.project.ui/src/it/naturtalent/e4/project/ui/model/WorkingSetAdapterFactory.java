package it.naturtalent.e4.project.ui.model;

import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Adapter factory which provides a ResourceMapping for a working set
 */
public class WorkingSetAdapterFactory implements IAdapterFactory
{
	
	class WorkingSetRootAdapter implements IWorkbenchAdapter
	{

		public Object[] getChildren(Object o)
		{
			if (o instanceof WorkingSetRoot)
			{
				WorkingSetRoot set = (WorkingSetRoot) o;
				return set.getWorkingSets();
			}
			return null;
		}

		public ImageDescriptor getImageDescriptor(Object o)
		{
			return null;
		}

		public String getLabel(Object o)
		{
			return null;
		}

		public Object getParent(Object o)
		{
			return null;
		}

	}


	/*
	 * Adapter for converting a working set to a resource mapping for use by
	 * object contributions.
	 */
	class ContributorResourceAdapter implements IContributorResourceAdapter2
	{

		public ResourceMapping getAdaptedResourceMapping(IAdaptable adaptable)
		{
			if (adaptable instanceof IWorkingSet)
			{
				IWorkingSet workingSet = (IWorkingSet) adaptable;
				IAdaptable[] elements = workingSet.getElements();
				List result = new ArrayList();
				for (int i = 0; i < elements.length; i++)
				{
					IAdaptable element = elements[i];
					ResourceMapping mapping = getContributedResourceMapping(element);
					if (mapping == null)
					{
						mapping = getResourceMapping(element);
					}
					if (mapping != null)
					{
						result.add(mapping);
					}
				}
				if (!result.isEmpty())
				{
					return new WorkingSetResourceMapping(workingSet);
				}
			}
			return null;
		}

		public IResource getAdaptedResource(IAdaptable adaptable)
		{
			// Working sets don't adapt to IResource
			return null;
		}

	}

	class WorkbenchAdapter implements IWorkbenchAdapter
	{

		public Object[] getChildren(Object o)
		{
			if (o instanceof IWorkingSet)
			{
				IWorkingSet set = (IWorkingSet) o;
				return set.getElements();
			}
			return null;
		}

		public ImageDescriptor getImageDescriptor(Object o)
		{
			if (o instanceof IWorkingSet)
			{
				IWorkingSet set = (IWorkingSet) o;
				return set.getImageDescriptor();
			}
			return null;
		}

		public String getLabel(Object o)
		{
			if (o instanceof IWorkingSet)
			{
				IWorkingSet set = (IWorkingSet) o;
				return set.getLabel();
			}
			return null;
		}

		public Object getParent(Object o)
		{
			return null;
		}

	}

	private IContributorResourceAdapter2 contributorResourceAdapter = new ContributorResourceAdapter();

	private IWorkbenchAdapter workbenchAdapter = new WorkbenchAdapter();
	
	private WorkingSetRootAdapter workingSetRootAdapter = new WorkingSetRootAdapter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 * java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		
		if (adaptableObject instanceof WorkingSetRoot)
		{
			if (adapterType == IWorkbenchAdapter.class)
			{
				return workingSetRootAdapter;
			}
		}
		
		if (adaptableObject instanceof IWorkingSet)
		{			
			if (adapterType == IWorkbenchAdapter.class)
			{
				return workbenchAdapter;
			}
			
			if (adapterType == IContributorResourceAdapter.class)
			{
				return contributorResourceAdapter;
			}
			if (adapterType == ResourceMapping.class)
			{
				IWorkingSet workingSet = (IWorkingSet) adaptableObject;
				IAdaptable[] elements = workingSet.getElements();
				List result = new ArrayList();
				for (int i = 0; i < elements.length; i++)
				{
					IAdaptable element = elements[i];
					ResourceMapping mapping = getResourceMapping(element);
					if (mapping != null)
					{
						result.add(mapping);
					}
				}
				if (!result.isEmpty())
				{
					return new WorkingSetResourceMapping(workingSet);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList()
	{
		return new Class[]
			{ IContributorResourceAdapter2.class, IWorkbenchAdapter.class,
					ResourceMapping.class };
	}

	static ResourceMapping getResourceMapping(Object o)
	{
		// First, ask the object directly for a resource mapping
		Object mapping = internalGetAdapter(o, ResourceMapping.class);
		if (mapping instanceof ResourceMapping)
		{
			return (ResourceMapping) mapping;
		}
		// If this fails, ask for a resource and convert to a resource mapping
		Object resource = internalGetAdapter(o, IResource.class);
		if (resource != null)
		{
			mapping = internalGetAdapter(resource, ResourceMapping.class);
			if (mapping instanceof ResourceMapping)
			{
				return (ResourceMapping) mapping;
			}
		}
		return null;
	}

	static ResourceMapping getContributedResourceMapping(IAdaptable element)
	{
		Object resourceAdapter = internalGetAdapter(element,
				IContributorResourceAdapter.class);
		if (resourceAdapter != null)
		{
			if (resourceAdapter instanceof IContributorResourceAdapter2)
			{
				// First, use the mapping contributor adapter to get the mapping
				IContributorResourceAdapter2 mappingAdapter = (IContributorResourceAdapter2) resourceAdapter;
				ResourceMapping mapping = mappingAdapter
						.getAdaptedResourceMapping(element);
				if (mapping != null)
				{
					return mapping;
				}
			}
			if (resourceAdapter instanceof IContributorResourceAdapter)
			{
				// Next, use the resource adapter to get a resource and then get
				// the mapping for that resource
				IResource resource = ((IContributorResourceAdapter) resourceAdapter)
						.getAdaptedResource(element);
				if (resource != null)
				{
					Object mapping = internalGetAdapter(resource,
							ResourceMapping.class);
					if (mapping instanceof ResourceMapping)
					{
						return (ResourceMapping) mapping;
					}
				}
			}
		}
		return null;
	}

	static Object internalGetAdapter(Object o, Class adapter)
	{
		if (o instanceof IAdaptable)
		{
			IAdaptable element = (IAdaptable) o;
			Object adapted = element.getAdapter(adapter);
			if (adapted != null)
			{
				return adapted;
			}
		}
		// Fallback to the adapter manager in case the object doesn't
		// implement getAdapter or in the case where the implementation
		// doesn't consult the manager
		return Platform.getAdapterManager().getAdapter(o, adapter);
	}

}
