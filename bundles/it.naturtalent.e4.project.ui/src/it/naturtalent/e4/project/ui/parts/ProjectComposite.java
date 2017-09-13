package it.naturtalent.e4.project.ui.parts;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class ProjectComposite extends Composite
{
	
	private DataBindingContext m_bindingContext;

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text textProjectName;

	//private NtProject ntProject;
	private IProjectData projectData;
	private StyledText styledText;
	private Label lblNewLabel;
	private static ControlDecoration controlDecoration;
	
	private IEventBroker eventBroker;
	
	/**
	 * Interne Klasse zum ueberpruefen des Textfeldes 'applicationText'
	 * 
	 * @author dieter
	 * 
	 */
	public class EmptyStringValidator implements IValidator
	{
		public EmptyStringValidator()
		{
			super();
		}

		@Override
		public IStatus validate(Object value)
		{
			IStatus status = Status.OK_STATUS;
			
			controlDecoration.hide();
			if (StringUtils.isEmpty((String) value))	
			{				
				controlDecoration.show();
				status = ValidationStatus.error("Empty Input");
			}
			else
			{
				if(eventBroker != null)
					eventBroker.post(IProjectData.PROJECT_EVENT_MODIFY_PROJECTDATA, projectData);
			}
			
			return status;
		}
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectComposite(Composite parent, int style)
	{
		super(parent, style);
		addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(2, false));
		//new Label(this, SWT.NONE);
		
		textProjectName = new Text(this, SWT.BORDER);		
		textProjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolkit.adapt(textProjectName, true, true);
		textProjectName.setEnabled(false);
		
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		styledText = new StyledText(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		styledText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				if(eventBroker != null)
					eventBroker.post(IProjectData.PROJECT_EVENT_MODIFY_PROJECTDATA, projectData);
			}
		});
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		styledText.setEnabled(false);
		
		controlDecoration = new ControlDecoration(this, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(Icon.OVERLAY_ERROR.getImage(IconSize._7x8_OverlayIconSize));

		toolkit.adapt(styledText);
		toolkit.paintBordersFor(styledText);
		m_bindingContext = initDataBindings();

	}
	
	public void setNtProject(IProjectData projectData)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();
				
		this.projectData = projectData;
		textProjectName.setEnabled(projectData != null);
		styledText.setEnabled(projectData != null);
		
		m_bindingContext = initDataBindings();
	}
	
	public void setEventBroker(IEventBroker eventBroker)
	{
		this.eventBroker = eventBroker;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextStyledTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(styledText);
		IObservableValue descriptionProjectDataObserveValue = BeanProperties.value("description").observe(projectData);
		bindingContext.bindValue(observeTextStyledTextObserveWidget, descriptionProjectDataObserveValue, null, null);
		//
		IObservableValue observeTextTextProjectNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(textProjectName);
		IObservableValue nameProjectDataObserveValue = BeanProperties.value("name").observe(projectData);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(new EmptyStringValidator());
		bindingContext.bindValue(observeTextTextProjectNameObserveWidget, nameProjectDataObserveValue, strategy, null);
		//
		return bindingContext;
	}
}
