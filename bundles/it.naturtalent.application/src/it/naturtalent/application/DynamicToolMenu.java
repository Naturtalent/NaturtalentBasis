 
package it.naturtalent.application;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.internal.workbench.URIHelper;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.osgi.framework.Bundle;

import it.naturtalent.application.handlers.DynamicToolMenuHandler;

public class DynamicToolMenu {
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		MDirectMenuItem directMenuItem = MMenuFactory.INSTANCE.createDirectMenuItem();
		directMenuItem.setLabel("DirectMenuItemTool");
		//directMenuItem.setContributionURI("bundleclass://it.naturtalent.project.ui/it.naturtalent.e4.project.ui.handlers.NewFolderHandler");
		
		
		directMenuItem.setContributorURI("platform:/plugin/it.naturtalent.application");		
		directMenuItem.setContributionURI("bundleclass://it.naturtalent.application/it.naturtalent.application.handlers.DynamicToolMenuHandler");	
		
		
		items.add(directMenuItem);
	}
	
	
	/**
	 * Returns the platform resource URI for the provided class
	 * 
	 * @param clazz
	 *            the class to get the resource URI for
	 * @return the platform resource URI
	 */
	
	public static final String RESOURCE_SCHEMA = "bundleclass://"; //$NON-NLS-1$

	public static final String RESOURCE_SEPARATOR = "/"; //$NON-NLS-1$
	public String getResourceURI(Class<?> clazz)
	{
		return RESOURCE_SCHEMA + Activator.PLUGIN_ID + RESOURCE_SEPARATOR
				+ clazz.getCanonicalName();
	}
	
		
}