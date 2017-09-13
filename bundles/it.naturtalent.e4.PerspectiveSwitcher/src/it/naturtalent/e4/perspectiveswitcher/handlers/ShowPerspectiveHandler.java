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
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.ToolItem;

import it.naturtalent.e4.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import it.naturtalent.e4.perspectiveswitcher.internal.dialogs.SelectPerspectiveDialog;
import it.naturtalent.e4.perspectiveswitcher.tools.PerspectiveSwitcherSwtTrim;

public final class ShowPerspectiveHandler
{

	@Execute
	public void execute(IEclipseContext context, @Optional
	@Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_ID)
	String perspectiveID, @Optional
	@Named(E4WorkbenchParameterConstants.COMMAND_PERSPECTIVE_NEW_WINDOW)
	String newWindow) throws InvocationTargetException, InterruptedException
	{

		if (perspectiveID == null || perspectiveID.equals(""))
			openSelectionDialog(context);
		else if (Boolean.parseBoolean(newWindow))
			openNewWindowPerspective(context, perspectiveID);
		else
			openPerspective(context, perspectiveID);
	}

	/**
	 * Opens the specified perspective in a new window.
	 * 
	 * @param perspectiveId
	 *            The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private void openNewWindowPerspective(IEclipseContext context,
			String perspectiveID)
	{
		MApplication application = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);
		EModelService modelService = context.get(EModelService.class);

		List<MPerspective> perspectives = modelService.findElements(
				application, perspectiveID, MPerspective.class, null);
		partService.switchPerspective(perspectives.get(0));
	}

	/**
	 * Opens a view selection dialog, allowing the user to chose a view.
	 * 
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private final void openSelectionDialog(IEclipseContext context)
	{
		SelectPerspectiveDialog dialog = ContextInjectionFactory.make(
				SelectPerspectiveDialog.class, context);
		dialog.open();
	}

	/**
	 * Opens the perspective with the given identifier.
	 * 
	 * @param perspectiveId
	 *            The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private final void openPerspective(IEclipseContext context,
			String perspectiveID)
	{
		MApplication application = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);
		EModelService modelService = context.get(EModelService.class);

		List<MPerspective> perspectives = modelService.findElements(
				application, perspectiveID, MPerspective.class, null);
		
		// Perspektive mit der ID 'perspectiveID' suchen
		MPerspective perspectiveToShow = null;
		for(MPerspective perspective : perspectives)
		{
			if(perspective.getElementId().equals(perspectiveID))
			{
				perspectiveToShow = perspective;
				break;
			}
		}
		
		if(perspectiveToShow != null)
		{
			// Prespektive zeigen
			partService.switchPerspective(perspectiveToShow);

			// in der Toolbar zeigen
			List<MToolBar> mToolBars = modelService.findElements(application,
					null, MToolBar.class, null);
			for (MToolBar mToolBar : mToolBars)
			{
				if (StringUtils.equals(
						"it.naturtalent.e4.application.toolbar.perspectives",
						mToolBar.getElementId()))
				{
					List<MToolBarElement> list = mToolBar.getChildren();
					for (MToolBarElement element : list)
					{
						if (element instanceof MToolControl)
						{
							MToolControl control = (MToolControl) element;
							Object obj = control.getObject();
							if (obj instanceof PerspectiveSwitcherSwtTrim)
							{
								// PerspectiveSwitcherSwtTrim ermittelt								
								PerspectiveSwitcherSwtTrim perspectiveTrim = (PerspectiveSwitcherSwtTrim) obj;

								// ToolItem neu hinzufuegen
								ToolItem item = perspectiveTrim.getToolItemFor(perspectiveToShow);
								if (item == null)
									perspectiveTrim.addPerspectiveShortcut(perspectiveToShow);
							}
						}
					}
				}
			}
		}
	}
}
