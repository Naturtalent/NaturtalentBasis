package it.naturtalent.e4.project;

public interface INtProjectPropertyFactory
{
	
	public static String PROJECTPROPERTYFACTORY_EXTENSION = "Factory";
	
	/**
	 * Ueber diese Funktion wird eine ProjectProperty-Instanz zurueckgegeben
	 * !!! Klassenname ist immer ProjectProperty + "Factory"
	 * 
	 * @return
	 */
	public INtProjectProperty createNtProjektData();
	
	/**
	 * Beschriftung ProjectPropertyFactory
	 * @return
	 */
	public String getLabel();
	
	/**
	 * Gibt den Namen des ECPProjects zurueck, indem diese Eigenschaft gespeichert ist.
	 * 
	 * @return
	 */
	public String getParentContainerName();

	
}
