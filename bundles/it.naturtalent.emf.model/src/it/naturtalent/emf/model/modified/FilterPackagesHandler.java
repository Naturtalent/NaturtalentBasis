
package it.naturtalent.emf.model.modified;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.swt.widgets.Shell;

/**
 * eigener Handler, wegen modifizierter CheckedSelectModelClassCompositeImpl()
 * 
 * @author A682055
 *
 */
public class FilterPackagesHandler
{
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional ECPProject ecpProject, Shell shell)
	{
		ECPHandlerHelper.filterProjectPackages(ecpProject, shell);
	}

}