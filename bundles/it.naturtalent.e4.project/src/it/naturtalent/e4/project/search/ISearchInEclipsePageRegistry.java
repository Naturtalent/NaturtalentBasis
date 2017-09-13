package it.naturtalent.e4.project.search;


/**
 * @author Markus Gebhard
 */
public interface ISearchInEclipsePageRegistry
{

	public void add(ISearchInEclipsePageDescriptor desc);

	public ISearchInEclipsePageDescriptor[] getPages();

}