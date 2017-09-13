package it.naturtalent.e4.project.ui.filters;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ClosedProjectFilter extends ViewerFilter
{

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof IProject)
			return (((IProject) element).isOpen());

		return true;
	}

}
