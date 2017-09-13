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

import org.eclipse.e4.core.di.annotations.Execute;

public class SaveAsPerspectiveHandler 
{
	public class ResetPerspectiveHandler 
	{
		@Execute
		public void execute()
		{
			// set the selected perspective to previous item on perspective property
			// stack
			// set to be renderd = false
			System.out.println("SaveAs Perspective");
		}
	}
}
