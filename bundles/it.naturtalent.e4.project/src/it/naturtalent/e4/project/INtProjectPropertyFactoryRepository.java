package it.naturtalent.e4.project;

import java.util.List;

/**
 * Definition des Speichers, indem alle verfuegbaren PropertyFactories gespeichert werden.
 * 
 * @author dieter
 *
 */
public interface INtProjectPropertyFactoryRepository
{	
	/**
	 * Alle verfuegbaren Factories gelistet zurueckgeben.
	 * 
	 * @return
	 */
	public List<INtProjectPropertyFactory> getAllProjektDataFactories();
	
	/**
	 * Factory ueber den Klassennamen im Repository suchen und zureuckgeben:
	 *   
	 * @param factoryClass
	 * @return
	 */
	public INtProjectPropertyFactory getFactory(Class<? extends INtProjectPropertyFactory>factoryClass);
	
	/**
	 * Zunaechst Factory Ueber den Klassennamen suchen und dann den 'NtProjectProperty' ueber die 
	 * 'create' - Funktion erzeugen und zureuckgeben.
	 *  
	 * @param factoryClass
	 * @return
	 */
	public INtProjectProperty createNtProjectData(Class<? extends INtProjectPropertyFactory>factoryClass);
}
