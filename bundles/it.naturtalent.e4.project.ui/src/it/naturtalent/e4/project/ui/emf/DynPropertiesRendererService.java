package it.naturtalent.e4.project.ui.emf;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VElement;
import org.eclipse.emfforms.spi.common.report.ReportService;
import org.eclipse.emfforms.spi.core.services.databinding.DatabindingFailedException;
import org.eclipse.emfforms.spi.core.services.databinding.DatabindingFailedReport;
import org.eclipse.emfforms.spi.core.services.databinding.EMFFormsDatabinding;
import org.eclipse.emfforms.spi.swt.core.AbstractSWTRenderer;
import org.eclipse.emfforms.spi.swt.core.di.EMFFormsDIRendererService;

import it.naturtalent.e4.project.model.project.ProjectPackage;

/**
 * @author A682055
 *
 *         Implementiert das Interface EMFFormsDIRendererService und wird als
 *         OSGI-Service zur Verfuegung gestellt (@see taskreferenceservice.aml)
 * 
 *         Fuer das Rendering der Reference Tasks im Element Schedule soll der
 *         'it.naturtalent.emf.model.DefaultReferenceRenderer' verwendet werden.
 *         Dieser Service priorisiert diesen Renderer ueber die Funktion
 *         'isApplicable(VElement vElement' und gibt die gewuenschte
 *         Rendererklasse ueber 'getRendererClass()' zurueck.
 * 
 */
public class DynPropertiesRendererService
		implements EMFFormsDIRendererService<VControl>
{

	private EMFFormsDatabinding databindingService;

	private ReportService reportService;

	protected void setEMFFormsDatabinding(
			EMFFormsDatabinding databindingService)
	{
		this.databindingService = databindingService;
	}

	/**
	 * Called by the initializer to set the ReportService.
	 *
	 * @param reportService
	 *            The ReportService
	 */
	protected void setReportService(ReportService reportService)
	{
		this.reportService = reportService;
	}

	@Override
	public double isApplicable(VElement vElement,
			ViewModelContext viewModelContext)
	{
		if (!VControl.class.isInstance(vElement))
		{
			return NOT_APPLICABLE;
		}
		final VControl control = (VControl) vElement;
		if (control.getDomainModelReference() == null)
		{
			return NOT_APPLICABLE;
		}
		
		IValueProperty valueProperty;
		try
		{
			valueProperty = databindingService.getValueProperty(
					control.getDomainModelReference(),
					viewModelContext.getDomainModel());
		} catch (final DatabindingFailedException ex)
		{
			reportService.report(new DatabindingFailedReport(ex));
			return NOT_APPLICABLE;
		}
		final EStructuralFeature eStructuralFeature = (EStructuralFeature) valueProperty.getValueType();
		
		if(eStructuralFeature instanceof EReferenceImpl)
		{
			EReferenceImpl reference = (EReferenceImpl) eStructuralFeature;
			if(ProjectPackage.eINSTANCE.getDynPropertyItem().equals(reference.getEReferenceType()))
				return 10;
		}

		return NOT_APPLICABLE;
	}

	@Override
	public Class<? extends AbstractSWTRenderer<VControl>> getRendererClass()
	{
		return DynPropertiesRenderer.class;
	}

}
