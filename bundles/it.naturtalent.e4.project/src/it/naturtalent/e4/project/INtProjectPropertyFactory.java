package it.naturtalent.e4.project;

public interface INtProjectPropertyFactory
{
	
	/**
	 * Ueber diese Funktion wird eine ProjectProperty-Instanz zurueckgegeben
	 * 
	 * @return
	 */
	public INtProjectProperty createNtProjektData();
	
	/**
	 * Beschriftung ProjectPropertyFactory
	 * @return
	 */
	public String getLabel();
	
}
