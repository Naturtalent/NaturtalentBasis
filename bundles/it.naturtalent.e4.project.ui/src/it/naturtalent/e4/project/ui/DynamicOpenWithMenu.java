 
package it.naturtalent.e4.project.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import it.naturtalent.application.services.IOpenWithEditorAdapter;
import it.naturtalent.application.services.IOpenWithEditorAdapterRepository;



/**
 * Diese Klasse ist im 'fragment.e4xmi' im DynamicMenuContribution eingetragen.
 * @AboutToShow wird jedesmal aufgerufen, wenn der Menupunkt geoeffnet wird.
 *
 * DirectMenuItem funktionert nicht - unklar warum
 * 
 * Menu's mit DynamicMenuContribution-Items muessen zusaetzlich mindestens ein DirectMenuItem haben,
 * ansonsten wird aus irgendwelchen Gründen der Menupunkt beim zweiten Aufruf disabled.
 * 
 * @author dieter
 *
 */
public class DynamicOpenWithMenu 
{

	@Inject @Optional
	private IOpenWithEditorAdapterRepository openwithAdapterRepository;
	
	@Inject @Optional
	private MApplication application;
	

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
	
	// in diesem Map werden alle Factories gespeichert die die MMenuItem zum Zeitpunkt
	// @AboutToShow erzeugen	
	private static Map<Integer,DynamicMenuFactory> menuFactoryMap = new HashMap<Integer,DynamicMenuFactory>();
	
	// wird aufgerufen, wenn der MenuePunkt'Oeffnen mit' selektiert wird
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) 
	{
		if(menuFactoryMap.size() == 0)
		{
			// mit den registrierten Adaptern die FactoryListe initialisieren
			DynamicOpenWithMenu openWithMenu = new DynamicOpenWithMenu();	
			List<IOpenWithEditorAdapter> menueAdapters = openwithAdapterRepository.getOpenWithAdapters();
			for(IOpenWithEditorAdapter menueAdapter : menueAdapters)
			{
				// mit Index < 0 wird der Adapter nicht als dyn. Menue Adapter interpretiert
				if (menueAdapter.getIndex() < 0)
					continue;
					
				if (menueAdapter.getType())
				{
					MCommand command = application.getCommand(menueAdapter.getCommandID());
					openWithMenu.addHandledDynamicItem(
							menueAdapter.getMenuID(),
							menueAdapter.getMenuLabel(), command, menueAdapter.getIndex());
				}
				else
				{
					openWithMenu.addHandledDynamicItem(
							menueAdapter.getMenuID(),
							menueAdapter.getMenuLabel(),
							menueAdapter.getContribURI(), menueAdapter.getIndex());
				}
				
			}

		}
			
		
		// mit Hilfe der gelisteten Factories werden die realen dynamischen Menuepunkte erzeugt
		Set<Integer>indices = menuFactoryMap.keySet();
		for(int index : indices)
			items.add(menuFactoryMap.get(index).createDynamicMenu());
	}
	
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
	// !!! funktioniert nicht
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