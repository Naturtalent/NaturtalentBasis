package it.naturtalent.e4.project.service;



import it.naturtalent.e4.project.INewActionAdapter;
import it.naturtalent.e4.project.INewActionAdapterRepository;

import java.util.ArrayList;
import java.util.List;

public class NewActionAdapterRepository implements INewActionAdapterRepository
{
	private static final String GENERAL_CONTEXT = "General";
	
	private static List<INewActionAdapter>newActionAdapters = new ArrayList<INewActionAdapter>();

	@Override
	public List<INewActionAdapter> getNewWizardAdapters()
	{		
		return newActionAdapters;
	}

	@Override
	public void addNewActionAdapter(INewActionAdapter newWizardAdapter)
	{
		if(!newActionAdapters.contains(newWizardAdapter))
			newActionAdapters.add(newWizardAdapter);		
	}


	
}
