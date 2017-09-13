package it.naturtalent.e4.preferences;

import java.util.List;

import it.naturtalent.application.IPreferenceAdapter;

public interface IPreferenceRegistry
{
	/**
	 * Liefert die registrierten Preferaenzadapter
	 * 
	 * @return
	 */
	public List<IPreferenceAdapter> getPreferenceAdapters();
}
