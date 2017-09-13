package it.naturtalent.e4.project.ui.filters;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class HiddenResourceFilter extends ViewerFilter
{
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof IResource)
		{
			IResource iResource = (IResource) element;
			if ((iResource.getType() & (IResource.FOLDER | IResource.FILE)) != 0)
				if (iResource.getName().startsWith("."))
					return false;
		}

		return true;
	}
}
