package it.naturtalent.e4.project;


import java.util.Date;

//import javax.xml.bind.annotation.XmlRootElement;



//@XmlRootElement(name="projectData")
public class ProjectData extends AbstractProjectData implements Cloneable
{

	// XML-Classname
	public static final String PROP_PROJECTDATACLASS = "projectData";
	

	/**
	 * Konstruktion
	 * 
	 */
	public ProjectData()
	{
		// ID bei der Konstruktion festlegen
		id = makeIdentifier();
	}
	
	/**
	 * einen datumsbasierenden Key erzeugen
	 */

	private String date;

	private long identifierCounter;

	/**
	 * Einen eindeutigen, datumsbasierenden key erzeugen
	 * 
	 * @return
	 */
	public String makeIdentifier()
	{
		if (date == null)
			date = Long.toString((new Date().getTime())) + "-";
		return date + Long.toString(++identifierCounter);
	}

}
