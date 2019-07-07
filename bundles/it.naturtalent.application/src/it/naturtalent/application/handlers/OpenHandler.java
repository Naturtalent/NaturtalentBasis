/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package it.naturtalent.application.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.application.services.IOpenWithEditorAdapter;

public class OpenHandler
{

	@Execute
	public void execute(IEclipseContext context,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
			throws InvocationTargetException, InterruptedException
	{
		
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		EPartService partService = currentApplication.getContext().get(EPartService.class);
		MPart part = partService.findPart("iit.naturtalent.e4.project.ui.part.emf.NtProjectView");
		if(part != null)
		{
			ESelectionService selectionService = part.getContext().get(ESelectionService.class);
			Object selObject = selectionService.getSelection();
			if((selObject != null) && (selObject instanceof Resource))
			{
				if(((IResource)selObject).getType() ==  IResource.FILE)
				{
					IFile ifile = (IFile) selObject;
					String fileName = ifile.getName();
					String ext = FilenameUtils.getExtension(fileName);
					
					Program prog = Program.findProgram(ext);			
					if (prog != null)
					{
						try
						{
							File file = FileUtils.toFile(ifile.getLocationURI().toURL());
							prog.execute(file.getPath());
							
							return;
						} catch (MalformedURLException e)
						{
							e.printStackTrace();
						}
					}				
			}					
		}
			
		FileDialog dialog = new FileDialog(shell);
		dialog.open();
		}
	}
}
