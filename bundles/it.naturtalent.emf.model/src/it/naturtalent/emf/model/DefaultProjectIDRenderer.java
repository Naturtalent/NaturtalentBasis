package it.naturtalent.emf.model;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.core.swt.renderer.TextControlSWTRenderer;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.template.model.VTViewTemplateProvider;
import org.eclipse.emfforms.spi.common.report.ReportService;
import org.eclipse.emfforms.spi.core.services.databinding.EMFFormsDatabinding;
import org.eclipse.emfforms.spi.core.services.editsupport.EMFFormsEditSupport;
import org.eclipse.emfforms.spi.core.services.label.EMFFormsLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;


public class DefaultProjectIDRenderer extends TextControlSWTRenderer
{

	// Markierung zur Unterdrückung der Selektionsfunktion
	public static boolean enableProjectIDSelection = false;
	
	protected Label projectLabelValue = null;	
	
	private Button buttonSelect;
	private Button btnUnset;
	
	protected String selectedID;
	
	protected String propertyFactoryName;
	
	@Optional @Inject INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	@Inject
	public DefaultProjectIDRenderer(VControl vElement,
			ViewModelContext viewContext,
			ReportService reportService,
			EMFFormsDatabinding emfFormsDatabinding, EMFFormsLabelProvider emfFormsLabelProvider,
			VTViewTemplateProvider vtViewTemplateProvider, EMFFormsEditSupport emfFormsEditSupport)
	{
		super(vElement, viewContext, reportService, emfFormsDatabinding,
				emfFormsLabelProvider, vtViewTemplateProvider, emfFormsEditSupport);		
	}
	
	@Override
	protected Control createSWTControl(Composite parent)
	{
		enableProjectIDSelection = false;
		
		final Composite composite = new Composite(parent, SWT.NONE);
		
		// ist dem ausgewaehlten Projekt eine PropertyFactory zugeordnet 
		if(StringUtils.isNotEmpty(selectedID))
		{
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selectedID);				
			List<INtProjectPropertyFactory>propertyFactories = NtProjektPropertyUtils.getProjectPropertyFactories(ntProjektDataFactoryRepository, iProject);
			if(propertyFactories != null)
			{
				for(INtProjectPropertyFactory propertyFactory : propertyFactories)
				{
					String factoryName = propertyFactory.getClass().getName();					
					if(StringUtils.equals(factoryName, propertyFactoryName))
					{
						enableProjectIDSelection = true;
						break;
					}					
				}
			}		
		}
		
		// abbruch, wenn kein PropertyFactory dem ausgewaehlten Projekt zugeordnet wurde
		Text dmy = new Text(composite, SWT.NONE);
		if (enableProjectIDSelection == false)
		{
			// ProjectID - Element ausblenden			
			getVElement().setVisible(false);
			return composite;
		}
				
		GridLayoutFactory.fillDefaults().numColumns(5).applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, false)
				.align(SWT.FILL, SWT.BEGINNING).applyTo(composite);
		
		// Original ProjektID Renderer enablen		
		getVElement().setEnabled(false);

		// Label Projektname hinzufuegen
		projectLabelValue = new Label(composite, SWT.BORDER);		
		setProjectName(null);
		GridDataFactory.fillDefaults().grab(true, false)
				.align(SWT.FILL, SWT.CENTER).applyTo(projectLabelValue);

		// Button Projektzuordnung hinzufuegen
		buttonSelect = new Button(composite, SWT.PUSH);
		buttonSelect.setImage(
				Icon.ICON_LINK.getImage(IconSize._16x16_DefaultIconSize));
		// buttonSelect.setText("select"); //$NON-NLS-1$
		buttonSelect.addSelectionListener(new SelectionAdapter()
		{			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Projekt, zu dem ein Link hergestellt werden, soll auswaehlen
				SelectProjectsDialog dialog = new SelectProjectsDialog(
						Display.getDefault().getActiveShell());

				if (StringUtils.isNotEmpty(selectedID))
				{
					// Projekt mit bestehender Zuordnung im Dialog selektieren
					dialog.create();
					dialog.setSelectedProjectId(selectedID);
				}

				if (dialog.open() == SelectProjectsDialog.OK)
				{
					// Projekt wurde ausgewaehlt
					selectedID = dialog.getSelectedProjectId();					
					setProjectName(selectedID);
					updateWidgets();
					updateElement();
				}
			}
		});

		// Button Projektzuordnung zuruecknehmen
		btnUnset = new Button(composite, SWT.PUSH);
		btnUnset.setImage(
				Icon.ICON_LINK_BREAK.getImage(IconSize._16x16_DefaultIconSize));
		btnUnset.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// keine Zuordnung mehr vorhanden
				selectedID = null;				
				setProjectName(null);
				updateWidgets();
				updateElement();
			}
		});

		// init
		// projectLabelValue.setText(getProjectName(selectedID));
		setProjectName(selectedID);
		updateWidgets();

		return composite;
	}
	
	protected void updateWidgets()
	{
		boolean isSelected = StringUtils.isNotEmpty(selectedID);
				
		buttonSelect.setEnabled(!isSelected);
		btnUnset.setEnabled(isSelected);
	}
	
	protected void updateElement()
	{
		
	}
	
	/**
	 * Aktualisiert den Projektnamenlabel mit dem Namen des Projekts mit der ID 'projectID'.
	 * 
	 * @param projectID
	 */
	protected void setProjectName(String projectID)
	{
		projectLabelValue.setText(getProjectName(projectID));
		projectLabelValue.setToolTipText(getProjectName(projectID));
	}
	
	private String getProjectName(String projectID)
	{
		String projectName = "kein Projekt zugeordnet";
		
		if(StringUtils.isNotEmpty(projectID))
		{			
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for(IProject project : projects)
			{
				try
				{
					if (project.isOpen() && StringUtils.equals(projectID, project.getName()))
					{
						projectName = project
								.getPersistentProperty(INtProject.projectNameQualifiedName);
					}

				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return projectName;
	}
	
}
