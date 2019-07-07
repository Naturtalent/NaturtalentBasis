package it.naturtalent.application;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import it.naturtalent.application.services.IOpenWithEditorAdapterRepository;

public class CenterWindowAddon
{

	public static final String MAINWINDOW_ID = "it.naturtalent.application.mainwindow";
	
	@Inject @Optional IOpenWithEditorAdapterRepository openwithAdapterRepository;
	
	@PostConstruct
	public void init(final IEventBroker eventBroker)
	{
		EventHandler handler = new EventHandler()
		{			
			@Override
			public void handleEvent(Event event)
			{
				if(!UIEvents.isSET(event))
					return;
				
				Object objElement = event.getProperty(UIEvents.EventTags.ELEMENT);
				if(!(objElement instanceof MTrimmedWindow))
					return;
				
				MTrimmedWindow windowModel = (MTrimmedWindow)objElement;
				Shell shell = (Shell)windowModel.getWidget();
				if(shell == null)
					return;
				
				// Hauptfenster anpassen und zentrieren
				if(windowModel.getElementId() != null)
				{
					if(!windowModel.getElementId().equals(MAINWINDOW_ID))
						return;
				}
				
				Monitor primary = shell.getMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = shell.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				shell.setLocation(x, y);				
			}
		};
		
		eventBroker.subscribe(UIEvents.UIElement.TOPIC_WIDGET, handler);
	}
	
}
