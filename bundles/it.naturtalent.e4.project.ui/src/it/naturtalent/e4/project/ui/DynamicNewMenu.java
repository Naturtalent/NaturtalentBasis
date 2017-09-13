 
package it.naturtalent.e4.project.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

/**
 * Hilfskonstruktion zur Implementiertung dynamischer Menuepunkte.
 * @see ProjectUIProcessor
 * 
 * @author dieter
 *
 */
public class DynamicNewMenu 
{	
	private class DynamicMenuFactory 
	{
		public boolean type = false;
		public String id;
		public String label;
		public String contributeURI;
		public MCommand command;
		
		public MMenuItem createDynamicMenu()
		{
			MMenuItem dynamicItem;
								
			dynamicItem = (type) ? MMenuFactory.INSTANCE.createHandledMenuItem() : MMenuFactory.INSTANCE
		            .createDirectMenuItem();
			dynamicItem.setElementId(id);
			dynamicItem.setLabel(label);
			
			if(type)
			{
				((MHandledMenuItem)dynamicItem).setCommand(command);				
			}
			else
			{
				dynamicItem.setContributorURI(contributeURI);				
			}

			return dynamicItem;
		}
	}
	
	private static Map<Integer,DynamicMenuFactory> menuFactoryMap = new HashMap<Integer,DynamicMenuFactory>();

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) 
	{		
		// mit Hilfe der gelisteten Factories werden die realen Menuepunkte erzeugt
		int n = menuFactoryMap.size();
		for(int i = 0;i < n;i++)
			items.add(menuFactoryMap.get(i).createDynamicMenu());		
	}
	
	/*
	 * 
	 * Baustelle, die Nummerierung der Menuepunke soll Luecken zulassen
	 * 
	 * 
	 */
	
	// der Zielhandler wird ueber 'command' adressiert
	public void addHandledDynamicItem(String id, String label, MCommand command, int index)
	{
		DynamicMenuFactory menueFactory = new DynamicMenuFactory();
		menueFactory.type = true;
		menueFactory.id = id;
		menueFactory.label = label;
		menueFactory.command = command;		
		menuFactoryMap.put(index, menueFactory);
	}
	
	// der Zielhandler wird direkt ueber die Adresse 'contributeUR' (bundleclass://path)   adressiert
	public void addHandledDynamicItem(String id, String label, String contributeURI, int index)
	{
		DynamicMenuFactory menueFactory = new DynamicMenuFactory();
		menueFactory.type = false;
		menueFactory.id = id;
		menueFactory.label = label;
		menueFactory.contributeURI = contributeURI;
		menuFactoryMap.put(index, menueFactory);
	}
	
}