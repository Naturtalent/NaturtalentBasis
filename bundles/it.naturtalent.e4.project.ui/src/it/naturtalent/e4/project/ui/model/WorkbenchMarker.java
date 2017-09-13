package it.naturtalent.e4.project.ui.model;

import it.naturtalent.e4.project.ui.utils.IMarkerActionFilter;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * Model object for adapting IMarker objects to the IWorkbenchAdapter interface.
 */
public class WorkbenchMarker extends WorkbenchAdapter implements
		IMarkerActionFilter
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object
	 * )
	 */
	public ImageDescriptor getImageDescriptor(Object o)
	{
		if (!(o instanceof IMarker))
		{
			return null;
		}
		/*
		return IDEWorkbenchPlugin.getDefault().getMarkerImageProviderRegistry()
				.getImageDescriptor((IMarker) o);
				*/
		
		/* nicht implementiert */
		System.out.println("WorkbenchMarker Adapter nicht ist nicht implementiert");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object o)
	{
		IMarker marker = (IMarker) o;
		return marker.getAttribute(IMarker.MESSAGE, "");//$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o)
	{
		return ((IMarker) o).getResource();
	}

	/**
	 * Returns whether the specific attribute matches the state of the target
	 * object.
	 * 
	 * @param target
	 *            the target object
	 * @param name
	 *            the attribute name
	 * @param value
	 *            the attriute value
	 * @return <code>true</code> if the attribute matches; <code>false</code>
	 *         otherwise
	 */
	
	
	public boolean testAttribute(Object target, String name, String value)
	{
		//return MarkerPropertyTester.test((IMarker) target, name, value);
		return false;
	}
	
}
