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

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class ResetPerspectiveHandler 
{
	private static final String MAIN_PERSPECTIVE_STACK_ID = "it.naturtalent.trimwindow.perspectivestack";
	
	@Execute
	public void execute(IEclipseContext context, EModelService modelService, MApplication application)
	{
		// set the selected perspective to previous item on perspective property
		// stack
		// set to be renderd = false
		System.out.println("Reset Perspective");
		
		 MPerspectiveStack perspectiveStack = (MPerspectiveStack) modelService.find(MAIN_PERSPECTIVE_STACK_ID, application);

		 MWindow window = context.get(MWindow.class);
		 MPerspective perspective = modelService.getActivePerspective(window);
		 
		 
		 modelService.resetPerspectiveModel(perspective, window);
		 
         //PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().resetPerspective();
         //perspectiveStack.getSelectedElement().setVisible(true);
         //perspectiveStack.setVisible(true);

	}
		
}
