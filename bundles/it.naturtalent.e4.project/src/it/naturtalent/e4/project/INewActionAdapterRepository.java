package it.naturtalent.e4.project;

import java.util.List;

public interface INewActionAdapterRepository
{
	public List<INewActionAdapter>getNewWizardAdapters();
	public void addNewActionAdapter(INewActionAdapter newWizardAdapter);
}
