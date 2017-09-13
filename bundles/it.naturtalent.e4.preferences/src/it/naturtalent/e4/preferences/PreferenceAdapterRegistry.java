package it.naturtalent.e4.preferences;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.application.IPreferenceAdapter;

public class PreferenceAdapterRegistry implements IPreferenceRegistry
{
	private static List<IPreferenceAdapter>registry = new ArrayList<IPreferenceAdapter>();
	
	@Override
	public List<IPreferenceAdapter> getPreferenceAdapters()
	{		
		return registry;	
	}
}
