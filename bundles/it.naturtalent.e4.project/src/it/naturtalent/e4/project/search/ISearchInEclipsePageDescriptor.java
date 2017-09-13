package it.naturtalent.e4.project.search;



import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Markus Gebhard
 */
public interface ISearchInEclipsePageDescriptor
{
	public ISearchInEclipsePage createPage() throws CoreException;

	public String getId();

	public String getLabel();

	public Integer getTabPosition();

	public ImageDescriptor getImageDescriptor();
}