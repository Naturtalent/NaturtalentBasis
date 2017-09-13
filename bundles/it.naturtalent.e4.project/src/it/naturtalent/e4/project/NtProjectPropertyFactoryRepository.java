package it.naturtalent.e4.project;

import java.util.ArrayList;
import java.util.List;

/**
 * Realisierung des Speichers aller PropertyFactories.
 * Der Zugriff wird ueber den OSGI-Service zur Verfuegung gestellt.
 * 
 * @author dieter
 *
 */
public class NtProjectPropertyFactoryRepository implements INtProjectPropertyFactoryRepository
{
	
	private static List<INtProjectPropertyFactory> ntProjektPropertyFactories = new ArrayList<INtProjectPropertyFactory>();

	@Override
	public List<INtProjectPropertyFactory> getAllProjektDataFactories()
	{		
		return ntProjektPropertyFactories;
	}

	@Override
	public INtProjectPropertyFactory getFactory(
			Class<? extends INtProjectPropertyFactory> factoryClass)
	{
		for(INtProjectPropertyFactory factory : ntProjektPropertyFactories)
		{
			if(factory.getClass().equals(factoryClass))
				return factory;
		}

		return null;
	}

	@Override
	public INtProjectProperty createNtProjectData(
			Class<? extends INtProjectPropertyFactory> factoryClass)
	{
		for(INtProjectPropertyFactory factory : ntProjektPropertyFactories)
		{
			if(factory.getClass().equals(factoryClass))
				return factory.createNtProjektData();
			
		}
		return null;
	}

}
