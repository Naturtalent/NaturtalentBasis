package it.naturtalent.e4.search.registry;



import it.naturtalent.e4.project.search.ISearchInEclipsePageDescriptor;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Markus Gebhard
 */
public class PageSorter
{

	@SuppressWarnings("unchecked")
	public static void sortPages(ISearchInEclipsePageDescriptor[] pages)
	{
		Arrays.sort(pages, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				ISearchInEclipsePageDescriptor page1 = (ISearchInEclipsePageDescriptor) o1;
				ISearchInEclipsePageDescriptor page2 = (ISearchInEclipsePageDescriptor) o2;
				Integer id1 = page1.getTabPosition();
				Integer id2 = page2.getTabPosition();
				if (id1 != null && id2 != null)
				{
					if (id1.equals(id2))
					{
						return page1.getLabel().compareTo(page2.getLabel());
					}
					return id1.compareTo(id2);
				}
				else if (id1 != null && id2 == null)
				{
					return -1;
				}
				else if (id1 == null && id2 != null)
				{
					return 1;
				}
				else
				{
					return page1.getLabel().compareTo(page2.getLabel());
				}
			}
		});
	}
}