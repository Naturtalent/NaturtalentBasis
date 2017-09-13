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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.services.EContextService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import it.naturtalent.e4.perspectiveswitcher.commands.E4WorkbenchCommandConstants;
import it.naturtalent.e4.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import it.naturtalent.e4.perspectiveswitcher.tools.E4PerspectiveSwitcherPreferences;
import it.naturtalent.e4.perspectiveswitcher.tools.E4Util;
import it.naturtalent.e4.perspectiveswitcher.tools.EPerspectiveSwitcher;
import it.naturtalent.e4.perspectiveswitcher.tools.PerspectiveSwitcherSwtTrim;

public class ClosePerspectiveHandler
{
	@Execute
	public void execute(IEclipseContext context)
	{
		MApplication application = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);
		EModelService modelService = context.get(EModelService.class);
		//ECommandService commandService = context.get(ECommandService.class);
		MWindow window = context.get(MWindow.class);
	
		// die aktive Perspektive
		MPerspective activePerspective = modelService.getActivePerspective(window);
	
		List<MToolBar> mToolBars = modelService.findElements(application, null,
				MToolBar.class, null);

		for (MToolBar mToolBar : mToolBars)
		{
			if (StringUtils.equals(
					"it.naturtalent.e4.application.toolbar.perspectives",
					mToolBar.getElementId()))
			{
				List<MToolBarElement>list = mToolBar.getChildren();
				for(MToolBarElement element : list)
				{
					if (element instanceof MToolControl)
					{
						MToolControl control = (MToolControl) element;
						Object obj = control.getObject();
						if (obj instanceof PerspectiveSwitcherSwtTrim)
						{
							PerspectiveSwitcherSwtTrim perspectiveTrim = (PerspectiveSwitcherSwtTrim) obj;
							//MPerspective perspective = perspectiveTrim.getFirstShortcut();
							MPerspective perspective = perspectiveTrim.getNextShortcut(activePerspective);
							if(perspective != null)
							{
									perspectiveTrim.removePerspectiveShortcut(activePerspective);
									perspectiveTrim.setSelectedElement(perspective);
									partService.switchPerspective(perspective);

							}
						}
					}
				}
			}
		}	
	}


}
