package it.naturtalent.e4.project;

import java.beans.PropertyChangeEvent;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author dieter
 * 
 * ProjectPropertyData repraesentiert die PropertyDaten die pro Projekt im
 * jeweiligen Projektdatenordner abgelegt werden. Die Klasse beinhaltet  
 * ein Array mit den Klassennamen der ProjectFactories die momentan dem
 * jeweiligen NtProjekt zugeordnet sind.
 * 
 */

/**
 * Modell zur Speicherung der PropertyFactoryNames (Klassenname) in Datenbereich des NtProjekts.
 * 
 * @author dieter
 *
 */
@XmlRootElement(name="propertyData")
public class ProjectPropertyData extends BaseBean
{
	// Properties
	public static final String PROP_FACTORY = "factory"; //$NON-NLS-N$
	
	// XML-Classname
	public static final String PROP_PROPERTYDATACLASS = "propertyData";
	
	// Datenfelder
	private String[] propertyFactories;	// alle dem Projekt zugeordnetern Propertyfactories
	
	
	/**
	 * Rueckgabe aller PropertyFactories (jeweilige Klassenamen) die einem
	 * NtProjekt momentan zugeordnet sind.
	 * 
	 * @return
	 */
	public String[] getPropertyFactories()
	{
		return propertyFactories;
	}

	public void setPropertyFactories(String[] propertyFactories)
	{
		firePropertyChange(new PropertyChangeEvent(this, PROP_PROPERTYDATACLASS, this.propertyFactories,
				this.propertyFactories = propertyFactories));
	}
	
	
	
}
