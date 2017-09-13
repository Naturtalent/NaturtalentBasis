package it.naturtalent.e4.project.ui.wizards;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ui.Messages;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

@Deprecated
public class ProjectWizardPage extends WizardPage
{
	
	/**
	 * interne Klasse Projektname
	 * @author apel.dieter
	 *
	 */
	public class ProjectName
	{
		String name;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}
	}
	
	/**
	 * Interne Klasse zum ueberpruefenn des Textfeldes 'applicationText'
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
			if (StringUtils.isNotEmpty((String) value))
			{
				controlDecoration.hide();	
				ProjectWizardPage.this.setPageComplete(true);	
				
				if(eventBroker != null)
					eventBroker.post(IProjectData.PROJECT_EVENT_MODIFY_PROJECTNAME, value);
				
				return Status.OK_STATUS;
			}
			else
			{
				controlDecoration.show();
				ProjectWizardPage.this.setPageComplete(false);				
				return ValidationStatus.error("Empty Projectname");
			}
		}		
	}

	
	private ProjectName projectName = new ProjectName();

	private DataBindingContext m_bindingContext;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected Text txtProjectName;
	private static ControlDecoration controlDecoration;

	private IResourceNavigator navigator = null;
	private IProjectDataFactory projectDataFactory;
	
	
	private ProjectAdvancedComposite projectAdvancedComposite;	
	private List<IProjectDataAdapter>adapters = new ArrayList<IProjectDataAdapter>();
	
	// alle definierten Adaptercomposites auflisten
	private List<Composite>adapterComposites = new ArrayList<Composite>();
	
	private IEventBroker eventBroker;
	
	/**
	 * Create the wizard.
	 */
	public ProjectWizardPage(IResourceNavigator navigator)
	{
		super("wizardPage"); //$NON-NLS-1$
		setTitle(Messages.ProjectWizardPage_title);
		setDescription(Messages.ProjectWizardPage_modify);
		this.navigator = navigator;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Composite container = new Composite(parent, SWT.NULL);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblProjectName = formToolkit.createLabel(container, Messages.ProjectWizardPage_name, SWT.NONE);
		
		// Projectname
		txtProjectName = formToolkit.createText(container, "", SWT.BORDER);		 //$NON-NLS-1$
		txtProjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		controlDecoration = new ControlDecoration(txtProjectName, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(ProjectWizardPage.class, "/org/eclipse/jface/fieldassist/images/error_ovr.gif"));
		controlDecoration.setDescriptionText("Some description");
		
		// Notizen und Workingsets
		projectAdvancedComposite = new ProjectAdvancedComposite(container, SWT.NONE, navigator);
		GridData gd_projectAdvancedComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_projectAdvancedComposite.widthHint = 581;
		gd_projectAdvancedComposite.heightHint = 260;
		projectAdvancedComposite.setLayoutData(gd_projectAdvancedComposite);
		
		Composite dynComposite = new Composite(container, SWT.NONE);
		dynComposite.setLayout(new FillLayout(SWT.VERTICAL));
		dynComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 2, 1));
		formToolkit.adapt(dynComposite);
		formToolkit.paintBordersFor(dynComposite);
		
		// hat der Adapter ein Composite definiert wird dieses dynamisch hinzufuegen		
		for(IProjectDataAdapter	adapter : adapters)
		{
			Composite adapterComposite = adapter.createComposite(dynComposite);			
			if(adapterComposite != null)		
				adapterComposites.add(adapterComposite);
		}
		
		m_bindingContext = initDataBindings();
		init();		
	}
	
	
	public List<IWorkingSet> getAssignedWorkingSets()
	{
		return projectAdvancedComposite.getAssignedWorkingSets();
	}
	
	protected void init()
	{
		if(navigator != null)
		{
			Object selObj = ((IStructuredSelection) navigator.getViewer().getSelection()).getFirstElement();
			if (selObj instanceof IProject)
			{
				NtProject ntProject = new NtProject((IProject) selObj);
				setProjectAliasName(ntProject.getName());		
				
				// bei geschlossenen Projekten Name aus Projektdaten holen
				if (!ntProject.isOpen() && (projectDataFactory != null))
				{
					setProjectAliasName(projectDataFactory.getProjectData(
							ntProject).getName());
					projectAdvancedComposite.setEnabled(false);
				}
			}
		}
	}
	
	
	
	public void setNavigator(IResourceNavigator navigator)
	{
		this.navigator = navigator;
	}

	public void setProjectAliasName (String projectName)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();
		
		this.projectName.setName(StringUtils.isNotEmpty(projectName) ? projectName : "");
		m_bindingContext = initDataBindings();
	}
	
	public String getProjectAliasName()
	{
		return projectName.getName();
	}
	
	public List<IWorkingSet> getAddedWorkingSets()
	{
		return projectAdvancedComposite.getAddedWorkingSets();
	}
	
	public void setProjectDataFactory(IProjectDataFactory projectDataFactory)
	{
		this.projectDataFactory = projectDataFactory;
	}

	public void addAdapter(IProjectDataAdapter adapter)
	{
		adapters.add(adapter);
	}
	
	public void setEventBroker(IEventBroker eventBroker)
	{
		this.eventBroker = eventBroker;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtProjectNameObserveWidget_1 = WidgetProperties.text(SWT.Modify).observe(txtProjectName);
		IObservableValue nameProjectNameObserveValue = PojoProperties.value("name").observe(projectName);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(new EmptyStringValidator());
		bindingContext.bindValue(observeTextTxtProjectNameObserveWidget_1, nameProjectNameObserveValue, strategy, null);
		//
		return bindingContext;
	}
	
	
	
}
