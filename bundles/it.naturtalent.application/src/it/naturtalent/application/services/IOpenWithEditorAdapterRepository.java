package it.naturtalent.application.services;

import java.util.List;

public interface IOpenWithEditorAdapterRepository
{
	/**
	 * Rueckgabe einer Liste aller definierten Adapter
	 * 
	 * @return
	 */
	public List<IOpenWithEditorAdapter>getOpenWithAdapters();	
}
