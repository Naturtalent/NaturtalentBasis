package it.naturtalent.application.services;



import java.util.ArrayList;
import java.util.List;


/**
 * In diesem Repository werden alle definierten 'NewActionAdapter' gehalten.
 * Ueber diese Adapter kann die jeweilige Action zum Erstellen eines neuen Objects gestartet werden. 
 * Der Zugriff auf dieses Repository wird ueber den OSGI
 * 
 * @author dieter
 *
 */
public class NewActionAdapterRepository implements INewActionAdapterRepository
{
	private static final String GENERAL_CONTEXT = "General";
	
	private static List<INewActionAdapter>newActionAdapters = new ArrayList<INewActionAdapter>();

	@Override
	public List<INewActionAdapter> getNewWizardAdapters()
	{		
		return newActionAdapters;
	}
}
