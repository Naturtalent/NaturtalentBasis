/*******************************************************************************
 * Copyright (c) 2012 Joseph Carroll and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Joseph Carroll <jdsalingerjr@gmail.com> - initial API and implementation
 ******************************************************************************/ 
package it.naturtalent.e4.perspectiveswitcher.handlers;

import it.naturtalent.e4.perspectiveswitcher.PerspectiveActivator;
import it.naturtalent.e4.perspectiveswitcher.tools.E4PerspectiveSwitcherPreferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ShowTextPerspectiveHandler 
{
	@Execute
	public void execute(	
			@Preference(nodePath = E4PerspectiveSwitcherPreferences.ROOT_PREFERENCES_NODE) IEclipsePreferences prefs)
	{
		Boolean showShortcutText = prefs.getBoolean(
				E4PerspectiveSwitcherPreferences.SHOW_TEXT, true);
		prefs.putBoolean(E4PerspectiveSwitcherPreferences.SHOW_TEXT,
				!showShortcutText);
	}
}
