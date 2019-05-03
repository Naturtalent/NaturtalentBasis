/*******************************************************************************
 * Copyright (c) 2012 Marco Descher.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marco Descher - initial API and implementation
 ******************************************************************************/
package it.naturtalent.icons.core;

import it.naturtalent.icons.Activator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

public enum Icon
{
	// @formatter:off
	DIALOG_MOVE_UP, 
	DIALOG_MOVE_DOWN, 
	DIALOG_MOVE_LEFT, 
	DIALOG_MOVE_RIGHT,
	DIALOG_NEW_FILE,
	DIALOG_NEW_FOLDER,
	DIALOG_NEW_PROJECT,
	DIALOG_NEW_CONTACT,
	COMMAND_ADD, 
	COMMAND_EDIT, 
	COMMAND_DELETE,
	COMMAND_REFRESH,
	COMMAND_FILTER,
	COMMAND_SAVE,
	COMMAND_SEARCH,
	COMMAND_UNDO,
	COMMAND_REDO,
	COMMAND_COPY,
	COMMAND_CONNECT,
	COMMAND_DISCONNECT, 
	COMMAND_EXPORT,
	COMMAND_IMPORT,
	COMMAND_CLONE_GIT,
	COMMAND_REFRESH_GIT,
	COMMAND_CREATE_REPOSITORY,
	MENU_SAVE,
	MENU_COPY,
	MENU_PASTE,
	MENU_OPEN,
	MENU_NEW,
	MENU_NEW_PROJECT,
	MENU_NEW_FOLDER,
	MENU_NEW_FILE,
	ICON_BACKWARD_NAV,
	ICON_COLLAPSEALL,
	ICON_EMPTY,
	ICON_DATABASE,
	ICON_DATABASE_GET,
	ICON_DATABASE_ADD,
	ICON_DESIGN,
	ICON_DESIGNS,
	ICON_SAMPLE, 
	ICON_SAVE_EDIT,
	ICON_SYNC,
	ICON_FILE,
	ICON_FOLDER,
	ICON_FORWARD_NAV,
	ICON_FILTER,
	ICON_PROJECT,
	ICON_PROJECT_OPEN,
	ICON_PROJECT_CLOSE,
	ICON_JOURNAL,
	ICON_PAGE,
	ICON_PUSH,
	ICON_PULL,
	ICON_MESSEN,
	ICON_MASSSTAB,
	ICON_PAINTBRUSH,
	ICON_DATE,
	ICON_LAST_EDIT_POS,
	ICON_LAYER,
	ICON_LAYERS,
	ICON_LINK,
	ICON_LINK_ADD,
	ICON_LINK_EDIT,
	ICON_LINK_DELETE,
	ICON_LINK_BREAK,
	ICON_LOCK,
	ICON_LOCKS,
	ICON_SOCKET,
	ICON_STAMP_MODE,
	ICON_ADD_SOCKET,
	ICON_EDIT_SOCKET,
	ICON_DELETE_SOCKET,
	ICON_SPOT,
	PART_CONTACT,
	PART_BROWSE_EXIST,
	OVERLAY_ERROR,
	OVERLAY_PROJECT_CO,
	WIZBAN_NEW,
	WIZBAN_SYNCHRONIZE_GIT,
	WIZBAN_PULL_GIT,
	WIZBAN_EXPORT,
	WIZBAN_IMPORT,
	WIZBAN_SMILEY;		
	// @formatter:on

	private Icon()
	{
	}

	/**
	 * Returns an image. Clients do not need to dispose the image, it will be
	 * disposed automatically.
	 * 
	 * @return an {@link Image}
	 */
	public Image getImage(IconSize is)
	{
		Image image = JFaceResources.getImageRegistry().get(this.name());
		if (image == null)
		{
			addIconImageDescriptor(this.name(), is);
			image = JFaceResources.getImageRegistry().get(this.name());
		}
		return image;
	}

	/**
	 * @return {@link ImageDescriptor} for the current image
	 */
	public ImageDescriptor getImageDescriptor(IconSize is)
	{
		ImageDescriptor id = null;
		id = JFaceResources.getImageRegistry().getDescriptor(this.name());
		if (id == null)
		{
			addIconImageDescriptor(this.name(), is);
			id = JFaceResources.getImageRegistry().getDescriptor(this.name());
		}
		return id;
	}

	/**
	 * @return a string to be embedded as iconURI, see beta plugin process for
	 *         an example
	 */
	public String getIconURI()
	{
		return "icon://" + name();
	}

	/**
	 * Get the Icon as {@link InputStream}; used by the
	 * {@link IconURLConnection}
	 * 
	 * @param is
	 * @return <code>null</code> if any error in resolving the image
	 * @throws IOException
	 */
	public InputStream getImageAsInputStream(IconSize is) throws IOException
	{
		InputStream ret = null;

		ResourceBundle iconsetProperties = ResourceBundle.getBundle("iconset");
		String fileName = iconsetProperties.getString(this.name());
		URL url = Activator.getPluginPath("icons/" + is.name + "/" + fileName);
		ret = url.openConnection().getInputStream();

		return ret;
	}

	/**
	 * Add an image descriptor for a specific key and {@link IconSize} to the
	 * global {@link ImageRegistry}
	 * 
	 * @param name
	 * @param is
	 * @return <code>true</code> if successfully added, else <code>false</code>
	 */
	private static boolean addIconImageDescriptor(String name, IconSize is)
	{
		try
		{
			ResourceBundle iconsetProperties = ResourceBundle
					.getBundle("iconset");
			String fileName = iconsetProperties.getString(name);
			URL fileLocation = Activator.getPluginPath("icons/" + is.name + "/"
					+ fileName);
			ImageDescriptor id = ImageDescriptor.createFromURL(fileLocation);
			JFaceResources.getImageRegistry().put(name, id);
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}
