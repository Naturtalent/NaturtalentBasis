package it.naturtalent.e4.project.ui.emf;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.view.internal.control.multireference.MultiReferenceSWTRenderer;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.renderer.NoPropertyDescriptorFoundExeption;
import org.eclipse.emf.ecp.view.spi.renderer.NoRendererFoundException;
import org.eclipse.emf.ecp.view.spi.util.swt.ImageRegistryService;
import org.eclipse.emf.ecp.view.template.model.VTViewTemplateProvider;
import org.eclipse.emfforms.spi.common.report.ReportService;
import org.eclipse.emfforms.spi.core.services.databinding.DatabindingFailedException;
import org.eclipse.emfforms.spi.core.services.databinding.EMFFormsDatabinding;
import org.eclipse.emfforms.spi.core.services.label.EMFFormsLabelProvider;
import org.eclipse.emfforms.spi.swt.core.layout.SWTGridCell;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.model.project.DynPropertyItem;
import it.naturtalent.e4.project.model.project.NtProperty;




/**
 * Referenzrenderer dynamische NtProjektProperties modifiziern.
 * Ein Doppelclick auf DynPropertyItem ruft die im PropertyAdapter definierte 'run'-Funktion auf. 
 * 
 * @author dieter
 *
 */
public class DynPropertiesRenderer extends MultiReferenceSWTRenderer

{
	
	// das zentrale ProjectPropertyRepository
	private @Inject INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	@Inject
	public DynPropertiesRenderer(VControl vElement, ViewModelContext viewContext, ReportService reportService,
			EMFFormsDatabinding emfFormsDatabinding, EMFFormsLabelProvider emfFormsLabelProvider,
			VTViewTemplateProvider vtViewTemplateProvider, ImageRegistryService imageRegistryService)
	{
		super(vElement, viewContext, reportService, emfFormsDatabinding,
				emfFormsLabelProvider, vtViewTemplateProvider, imageRegistryService);		
	}

	@Override
	protected boolean showAddExistingButton()
	{		
		return false;
	}

	@Override
	protected boolean showAddNewButton()
	{
		return false;
	}

	@Override
	protected boolean showDeleteButton()
	{
		return false;
	}

	/* 
	 * Doppelclickfunktion anpassen
	 * 
	 */
	@Override
	protected void handleDoubleClick(EObject selectedObject)
	{
		if (selectedObject instanceof DynPropertyItem)
		{
			DynPropertyItem dynProperty = (DynPropertyItem) selectedObject;
						
			NtProperty ntProperty = (NtProperty) dynProperty.eContainer();
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProperty.getId());
			if(iProject.exists())
			{
				List<INtProjectProperty> projectProperties = NtProjektPropertyUtils
						.getProjectProperties(ntProjektDataFactoryRepository,iProject);
				
				for(INtProjectProperty projectProperty : projectProperties)
				{
					if(StringUtils.equals(projectProperty.getClass().getName(),dynProperty.getClassName()))
					{						
						Action propertyAction = projectProperty.createAction();
						try
						{
							propertyAction.run();
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}
	}


	
}
