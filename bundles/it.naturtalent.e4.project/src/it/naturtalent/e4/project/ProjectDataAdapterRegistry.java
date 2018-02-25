package it.naturtalent.e4.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Creatable;

/**
 * Die Aufnahme aller ProjectDataAdapter
 * 
 * @author dieter
 *
 */
@Deprecated
// ersetzt durch NtProjectPropertyFactoryRepository

@Creatable
public class ProjectDataAdapterRegistry
{
	private static List<IProjectDataAdapter>adapterRegistry = new ArrayList<IProjectDataAdapter>();
	
	public static void addAdapter(IProjectDataAdapter adapter)
	{
		adapterRegistry.add(adapter);
	}

	public static List<IProjectDataAdapter> getProjectDataAdapters()
	{
		return adapterRegistry;
	}
	
	public static IProjectDataAdapter getProjectDataAdapter(String id)
	{
		for(IProjectDataAdapter adapter : adapterRegistry)
		{
			if(StringUtils.equals(adapter.getId(),id))
			return adapter;
				
		}
		return null;
	}

}
