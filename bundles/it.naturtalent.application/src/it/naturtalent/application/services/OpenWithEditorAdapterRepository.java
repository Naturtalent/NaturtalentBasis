package it.naturtalent.application.services;



import java.util.ArrayList;
import java.util.List;


/**
 * In diesem Repository werden alle definierten 'OpenWithEditorAdapter' gehalten.
 * Mit diesem Adapter wird ein spezieller Editor zum oeffnen und editieren von bestimmten Datei zur Verfuegung gestellt.
 * Der Zugriff auf dieses Repository erfolgt ueber OSGI
 * 
 * @author dieter
 *
 */
public class OpenWithEditorAdapterRepository implements IOpenWithEditorAdapterRepository
{	
	
	private static List<IOpenWithEditorAdapter>openWithAdapters = new ArrayList<IOpenWithEditorAdapter>();

	@Override
	public List<IOpenWithEditorAdapter> getOpenWithAdapters()
	{		
		return openWithAdapters;
	}

	
}
