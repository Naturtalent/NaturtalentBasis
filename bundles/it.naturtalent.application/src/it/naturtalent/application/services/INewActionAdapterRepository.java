package it.naturtalent.application.services;

import java.util.List;

public interface INewActionAdapterRepository
{
	public static final String GENERAL_CATEGORY = "General";
		
	/**
	 * Rueckgabe eine Liste aller definierten Adapter
	 * 
	 * @return
	 */
	public List<INewActionAdapter>getNewWizardAdapters();	
}
