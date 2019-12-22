package it.naturtalent.e4.project.ui.emf;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.core.swt.renderer.TextControlSWTRenderer;
import org.eclipse.emf.ecp.view.spi.model.ModelChangeListener;
import org.eclipse.emf.ecp.view.spi.model.ModelChangeNotification;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.impl.VViewImpl;
import org.eclipse.emf.ecp.view.template.model.VTViewTemplateProvider;
import org.eclipse.emfforms.spi.common.report.ReportService;
import org.eclipse.emfforms.spi.core.services.databinding.EMFFormsDatabinding;
import org.eclipse.emfforms.spi.core.services.editsupport.EMFFormsEditSupport;
import org.eclipse.emfforms.spi.core.services.label.EMFFormsLabelProvider;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.util.ProjectValidator;
import it.naturtalent.e4.project.ui.actions.emf.NewProjectAction;

/**
 * Angepasster Renderer fuer den Projektnamen. 
 * @author dieter
 *
 */
public class NtProjectNameRendering extends TextControlSWTRenderer
{	
	private IEventBroker eventBroker;	
	private NtProject ntObject;
		
	private Text text;
	
	// Listener selektiert den Projektnamen, wenn ein vordefinierter Name benutzt werden soll
	private ModifyListener firstTimeSelection = new ModifyListener()
	{		
		@Override
		public void modifyText(ModifyEvent e)
		{
			text.selectAll();
			
			// Textselektion soll nur einmal erfolgen (Listener und E4Contexteintrag entfernen)			
			text.removeModifyListener(firstTimeSelection);
			E4Workbench.getServiceContext().remove(NewProjectAction.PREDIFINED_PROJECTNAME);
		}
	};
	
	
	
	/**
	 * Konstruktion
	 * 
	 * @param vElement
	 * @param viewContext
	 * @param reportService
	 * @param emfFormsDatabinding
	 * @param emfFormsLabelProvider
	 * @param vtViewTemplateProvider
	 * @param emfFormsEditSupport
	 */
	@Inject
	public NtProjectNameRendering(VControl vElement,
			ViewModelContext viewContext, ReportService reportService,
			EMFFormsDatabinding emfFormsDatabinding,
			EMFFormsLabelProvider emfFormsLabelProvider,
			VTViewTemplateProvider vtViewTemplateProvider,
			EMFFormsEditSupport emfFormsEditSupport)
	{
		super(vElement, viewContext, reportService, emfFormsDatabinding,
				emfFormsLabelProvider, vtViewTemplateProvider, emfFormsEditSupport);
		
		// das wiederzugebende Object 
		ntObject = (NtProject) getViewModelContext().getDomainModel();

		
		// Zugriff auf EventBroker
		eventBroker = E4Workbench.getServiceContext().get(IEventBroker.class);
		
		// Listener reagiert auf Aenderungen am Modell	
		viewContext.registerDomainChangeListener(new ModelChangeListener()
		{			
			@Override
			public void notifyChange(ModelChangeNotification notification)
			{				
				EObject obj = notification.getNotifier();
				if (obj instanceof NtProject)
				{
					// Validierungsergebnis bekanntgeben
					boolean validationStatus = new ProjectValidator().validateNtProject(ntObject, new BasicDiagnostic(), null);
					eventBroker.send(ProjectModelEventKey.PROJECT_VALIDATION_MODELEVENT, validationStatus);					
				}
			}
		});

	}
	
	
	@Override
	protected Control createSWTControl(Composite parent)
	{
		// Validation bei der Eroeffnung
		boolean validationStatus = new ProjectValidator().validateNtProject(ntObject, new BasicDiagnostic(), null);		
		eventBroker.send(ProjectModelEventKey.PROJECT_VALIDATION_MODELEVENT, validationStatus);		

		Control control = super.createSWTControl(parent);		
		Composite composite = (Composite) control;
		Control [] controls = composite.getChildren();
		for(Control child : controls)
		{
			if(child instanceof Text)	
			{
				text = (Text)child;				
				eventBroker.send(ProjectModelEventKey.PROJECTNAME_WIZARDTEXTFIELD, text);
				break;
			}
		}
		
		// ist ein vordefinierter Name hinterlegt wird der Projektname selektiert
		if(StringUtils.isNotEmpty((String)E4Workbench.getServiceContext().get(NewProjectAction.PREDIFINED_PROJECTNAME)))
			text.addModifyListener(firstTimeSelection);				
		
		return control;		
	}

	/*
	 * Durch 'WidgetProperties.text(SWT.Modify)' findet eine Validierung nach jeder Eingabe statt.
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecp.view.spi.core.swt.renderer.TextControlSWTRenderer#bindValue(org.eclipse.swt.widgets.Control, org.eclipse.core.databinding.observable.value.IObservableValue, org.eclipse.core.databinding.DataBindingContext, org.eclipse.core.databinding.UpdateValueStrategy, org.eclipse.core.databinding.UpdateValueStrategy)
	 */
	protected Binding bindValue(Control text, IObservableValue modelValue,
			DataBindingContext dataBindingContext,
			UpdateValueStrategy targetToModel,
			UpdateValueStrategy modelToTarget)
	{
		final IObservableValue value = WidgetProperties.text(SWT.Modify)
				.observe(Composite.class.cast(text).getChildren()[0]);
		final Binding binding = dataBindingContext.bindValue(value, modelValue,
				targetToModel, modelToTarget);
		return binding;
	}

}
