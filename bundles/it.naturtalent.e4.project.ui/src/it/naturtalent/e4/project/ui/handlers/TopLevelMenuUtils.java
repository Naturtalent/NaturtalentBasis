package it.naturtalent.e4.project.ui.handlers;



import it.naturtalent.e4.project.NaturtalentConstants;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.viewers.TreeViewer;
import org.osgi.service.prefs.BackingStoreException;

public class TopLevelMenuUtils
{
	
	private static final String WORKINGSET_IDENTIFIER = "workingset"; //$NON-NLS-1$
	private static final String PROJECT_IDENTIFIER = "project"; //$NON-NLS-1$
	
	/**
	 * Generiert ein TopLevel-Menue im uebergebenen View
	 *  
	 * @param part - der uebergegene View
	 * @param handlerURI
	 * @return
	 */
	public static List<MDirectMenuItem> createTopLevelMenus(MPart part, List<String>handlerURI)
	{
		List<MDirectMenuItem> lMenus = null;
		
		// Liste der Menues ermitteln
		List<MMenu> menus = part.getMenus();
		if ((menus != null) && (!menus.isEmpty()))
		{
			// ViewMenu des ExplorerViews
			MMenu mainMenu = menus.get(0);
			
			// TopLevel parent-menu
			MMenu toplevelMenu = MMenuFactory.INSTANCE.createMenu();
			toplevelMenu.setLabel("Top Level Elements");
			mainMenu.getChildren().add(toplevelMenu);
			List<MMenuElement> children = toplevelMenu.getChildren();

			// Project child-menu
			MDirectMenuItem projectHandleMenu = MMenuFactory.INSTANCE
					.createDirectMenuItem();
			projectHandleMenu.setLabel("Projects");
			projectHandleMenu.setType(ItemType.CHECK);
			projectHandleMenu.setElementId(PROJECT_IDENTIFIER);			
			projectHandleMenu.setContributionURI(handlerURI.get(0));						
			children.add((MDirectMenuItem) projectHandleMenu);

			// WorkingSet child-menu
			MDirectMenuItem workingSetHandleMenu = MMenuFactory.INSTANCE
					.createDirectMenuItem();
			workingSetHandleMenu.setLabel("WorkingSets");
			workingSetHandleMenu.setType(ItemType.CHECK);
			workingSetHandleMenu.setElementId(WORKINGSET_IDENTIFIER);
			workingSetHandleMenu.setContributionURI(handlerURI.get(1));
			children.add((MDirectMenuItem) workingSetHandleMenu);

			MElementContainer container = projectHandleMenu.getParent();
			lMenus = container.getChildren();
		}
		
		return lMenus;
	}

	/**
	 * Markiert Menue als selektiert in Abhaengigkeit vom WorkingSet-Flag
	 *  
	 * @param menus
	 * @param workingSetLevel
	 */
	public static void updateTopLevelMenus(List<MDirectMenuItem>menus, boolean workingSetLevel)
	{
		if (menus != null)
		{
			for (MDirectMenuItem menu : menus)
			{
				if (workingSetLevel)
				{
					if (StringUtils.equals(menu.getElementId(),
							WORKINGSET_IDENTIFIER))
						menu.setSelected(true);
					else
						menu.setSelected(false);
				}
				else
				{
					if (StringUtils.equals(menu.getElementId(),
							WORKINGSET_IDENTIFIER))
						menu.setSelected(false);
					else
						menu.setSelected(true);
				}
			}
		}
	}

	/**
	 * Setzt 'Workingset-Flag' und aktualisiert den Preferenzspeicher
	 * 
	 * @param preferences
	 * @param directHandleMenu
	 * @param workingSetLevel
	 */
	public static void setTopLevelMenus(IEclipsePreferences preferences,
			MDirectMenuItem directHandleMenu, boolean workingSetLevel)
	{
		MElementContainer container = directHandleMenu.getParent();
		List<MDirectMenuItem> menus = container.getChildren();
		
		updateTopLevelMenus(menus, workingSetLevel);
		preferences.putBoolean(NaturtalentConstants.WORKINGSET_AS_TOPLEVEL, workingSetLevel);
		try
		{
			preferences.flush();
		} catch (BackingStoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static MPopupMenu createExplorerPopup(EMenuService service, MPart part, TreeViewer treeViewer)
	{
		service.registerContextMenu(treeViewer.getTree(), NaturtalentConstants.EXPLORER_POPUPMENU_ID);
		
		List<MMenu> menus = part.getMenus();
		if (menus != null)
		{
			for(MMenu menu : menus)
			{
				if(StringUtils.equals(menu.getElementId(), NaturtalentConstants.EXPLORER_POPUPMENU_ID))
				{
					if (menu instanceof MPopupMenu)
						return (MPopupMenu) menu;
				}
			}
		}

		return null;
	}
	
	

}
