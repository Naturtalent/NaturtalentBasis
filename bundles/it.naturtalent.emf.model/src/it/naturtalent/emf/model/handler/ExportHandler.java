 
package it.naturtalent.emf.model.handler;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.internal.ui.util.ECPExportHandlerHelper;
import org.eclipse.swt.widgets.Shell;

public class ExportHandler
{
	/*
	@Execute
	public void execute(Shell shell, @Named(IServiceConstants.ACTIVE_SELECTION) EObject eObject)
	{
		final List<EObject> eObjects = new LinkedList<EObject>();
		eObjects.add(EcoreUtil.copy(eObject));
		ECPExportHandlerHelper.export(shell, eObjects);
	}
	*/

	@Execute
	public void execute(Shell shell)
	{
		System.out.println("Export");
		
		
		
		
		
	}

}