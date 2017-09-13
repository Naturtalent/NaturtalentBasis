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
package it.naturtalent.icons.urihandler;

import it.naturtalent.icons.Activator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.FileLocator;

import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class IconURLConnection extends URLConnection
{

	String iconName;

	protected IconURLConnection(URL url)
	{
		super(url);
		iconName = url.getAuthority();
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		try
		{
			Icon selectedIcon = Icon.valueOf(iconName);
			InputStream is = selectedIcon
					.getImageAsInputStream(IconSize._16x16_DefaultIconSize);
			return is;
		} catch (Exception e)
		{
			System.out
					.println("[ERROR] " + IconURLConnection.class.getName()
							+ " " + iconName
							+ " not found, replacing with empty icon.");
			return FileLocator.find(Activator.getPluginPath("icons/empty.png"))
					.openStream();
		}
	}

	@Override
	public void connect() throws IOException
	{
	}
}
