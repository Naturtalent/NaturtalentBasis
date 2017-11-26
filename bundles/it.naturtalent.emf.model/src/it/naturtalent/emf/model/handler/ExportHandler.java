 
package it.naturtalent.emf.model.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Shell;

public class ExportHandler
{
	
	@Execute
	public void execute(Shell shell, @Named(IServiceConstants.ACTIVE_SELECTION) EObject eObject)
	{
		System.out.println("Export");
		
		/*
		final List<EObject> eObjects = new LinkedList<EObject>();
		eObjects.add(EcoreUtil.copy(eObject));
		ECPExportHandlerHelper.export(shell, eObjects);
		*/
	}

	/*
	@Execute
	public void execute(Shell shell)
	{
		System.out.println("Export");
		
	}
	*/


}