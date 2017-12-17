package it.naturtalent.e4.project.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.core.databinding.beans.BeanProperties;

/**
 * Standardseite als Einstieg in die ProjektPropertyWizards.
 *  
 * @author dieter
 *
 */
public class ProjectPropertyWizardPage2 extends WizardPage
{
	private DataBindingContext m_bindingContext;

	protected NtProjectProperty projectProperty;
	private ProjectData projectData;
	
	// momentan vom Navigator selektiertes Projekt
	protected IProject selectedIProject;
	
	protected INtProjectPropertyFactoryRepository ntProjectPropertyFactoryRepository;
	
	protected Text txtProjectName;
	protected Text txtDescription;	
	private Button btnCheckWorkingset;
	private Button btnSelectWs;
	private CCombo comboWorkingsets;
	private static ControlDecoration controlDecoration;
	
	private String defaultProjectName = null;
	private String projectName = null;
	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	// Liste der neuangelegten WorkingSets
	private List<IWorkingSet>addedWorkingSets = null;
	
	
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
				ProjectPropertyWizardPage2.this.setPageComplete(true);	
				
				return Status.OK_STATUS;
			}
			else
			{
				controlDecoration.show();
				ProjectPropertyWizardPage2.this.setPageComplete(false);				
				return ValidationStatus.error("Empty Projectname");
			}
		}		
	}



	/**
	 * Create the wizard.
	 */
	public ProjectPropertyWizardPage2()
	{
		super("wizardPage");
		setTitle("Project Wizard");
		setDescription("Projekt definieren");
	}
	
	@PostConstruct
	private void postConstruct(
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject,
			INtProjectPropertyFactoryRepository ntProjectPropertyFactoryRepository)
	{
		this.selectedIProject = iProject;
		this.ntProjectPropertyFactoryRepository = ntProjectPropertyFactoryRepository;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		// Projektname
		Label lblNewPlabel = new Label(container, SWT.NONE);
		lblNewPlabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewPlabel.setText("Projekt");
		
		txtProjectName = new Text(container, SWT.BORDER);
		txtProjectName.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				projectName = txtProjectName.getText();
			}
		});
		txtProjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		controlDecoration = new ControlDecoration(txtProjectName, SWT.LEFT | SWT.TOP);		
		controlDecoration.setImage(Icon.OVERLAY_ERROR.getImage(IconSize._7x8_OverlayIconSize));
		controlDecoration.setDescriptionText("undefinierter Projektname");
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		// Notizen
		Label lblDescription = new Label(container, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDescription.setText("Notizen");
		
		txtDescription = new Text(container, SWT.BORDER | SWT.WRAP);
		GridData gd_txtDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_txtDescription.heightHint = 224;
		txtDescription.setLayoutData(gd_txtDescription);
		new Label(container, SWT.NONE);
		
		Label label = new Label(container, SWT.NONE);
		
		// Workingsets
		Group grpWorkingsets = new Group(container, SWT.NONE);
		grpWorkingsets.setLayout(new GridLayout(3, false));
		grpWorkingsets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpWorkingsets.setText("Workingsets");
		
		btnCheckWorkingset = new Button(grpWorkingsets, SWT.CHECK);
		btnCheckWorkingset.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				btnSelectWs.setEnabled(btnCheckWorkingset.getSelection());
				//comboWorkingsets.setEditable(btnCheckWorkingset.getSelection());
				comboWorkingsets.setEnabled(btnCheckWorkingset.getSelection());
			}
		});
		btnCheckWorkingset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnCheckWorkingset.setText("Projekt Workingset(s) zuordnen ");
		btnCheckWorkingset.setSelection(true);
		new Label(grpWorkingsets, SWT.NONE);
		
		Label lblWorkingsets = new Label(grpWorkingsets, SWT.NONE);
		lblWorkingsets.setText("Workingsets");
		
		comboWorkingsets = new CCombo(grpWorkingsets, SWT.BORDER);
		comboWorkingsets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboWorkingsets.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String key = comboWorkingsets.getItem(comboWorkingsets.getSelectionIndex());				
				assignedWorkingSets = (ArrayList<IWorkingSet>) comboWorkingsets.getData(key);
			}
		});
		comboWorkingsets.setEditable(false);
		
		btnSelectWs = new Button(grpWorkingsets, SWT.NONE);
		btnSelectWs.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// SelectDialog oeffnen
				IWorkingSet [] activeWorkingSets = assignedWorkingSets.toArray(new IWorkingSet[assignedWorkingSets.size()]);
				addedWorkingSets = null;
				SelectWorkingSetDialog dialog = new SelectWorkingSetDialog(getShell(), activeWorkingSets);				
				if(dialog.open() == SelectWorkingSetDialog.OK)
				{
					// die neuangelegten WokingSets
					addedWorkingSets = dialog.getAddedWorkingSets();
					
					// die ausgewaehlten WorkingSets in Combo uebernehmen 
					IWorkingSet [] configResults = dialog.getConfigResult();
					assignedWorkingSets.clear();				
					StringBuilder buildName = new StringBuilder(5);
					for(IWorkingSet workingSet : configResults)
					{
						String wsName = workingSet.getName();
						if (!StringUtils.equals(wsName,
								IWorkingSetManager.OTHER_WORKINGSET_NAME))
						{
							if (assignedWorkingSets.size() > 0)
								buildName.append("," + wsName); //$NON-NLS-N$
							else
								buildName.append(wsName);								
							assignedWorkingSets.add(workingSet);
						}	
					}
					String name = buildName.toString();
					comboWorkingsets.add(name);
					comboWorkingsets.setText(buildName.toString());
					comboWorkingsets.setData(name, assignedWorkingSets.clone());					
				}	
			}
		});
		btnSelectWs.setText("select");
		
		m_bindingContext = initDataBindings();
		
		// Eigenschaften und Workingsetinfo's des selektierten Projekts uebernehmen
		initProperties();
		initWorkingSets();
		
		
		
	}
	
	/*
	 * die Eigenschaften des selektierten Projekts uebernehmen
	 */
	protected void initProperties()
	{
		if(selectedIProject != null)
		{
			
			NtProject ntProject = new NtProject(selectedIProject);
			String name = ntProject.getName();
			
			
			// mit den Properties des momentan selektierten Projekts vorbelegen 
			if(ntProjectPropertyFactoryRepository != null)
			{
				NtProjectProperty projectProperty = (NtProjectProperty) ntProjectPropertyFactoryRepository
						.createNtProjectData(NtProjectPropertyFactory.class);
				
				if(projectProperty != null)
				{
					// die Projektdaten des selektierten Projekts laden				
					projectProperty.setNtProjectID(selectedIProject.getName());
					//projectProperty.init();
					projectData = (ProjectData) projectProperty.getNtPropertyData();
					
					// Projektname und Notizen ausgeben
					txtProjectName.setText((projectData.getName() != null) ? projectData.getName() : "");
					txtProjectName.addFocusListener(new FocusAdapter()
					{
						@Override
						public void focusGained(FocusEvent e)
						{
							// vorgegebenen Projektnamen markieren
							txtProjectName.setSelection(0, txtProjectName.getText().length());
						}
					});		
					
					txtDescription.setText((projectData.getDescription() != null) ? projectData.getDescription() : "");					
				}				
			}
		}
		else
		{
			// neues Projekt - ggf. Defaultwerte vorgeben
			if(StringUtils.isNotEmpty(defaultProjectName))
			{
				txtProjectName.setText(defaultProjectName);
				txtProjectName.setSelection(0);
			}
		}		
	}

	/*
	 * Workingsetinfo's des selektierten Projekts uebernehmen
	 */
	private void initWorkingSets()
	{
		IResourceNavigator navigator = Activator.findNavigator();		
		if((selectedIProject != null) && (navigator != null))
		{
			// alle WorkingSets denen das Projekt zugeordnet ist in Combo uebernehmen
			StringBuilder buildName = new StringBuilder(5);			
			IWorkingSet[] workingSets = navigator.getWorkingSets();
			for (IWorkingSet workingSet : workingSets)
			{
				IAdaptable[] adaptables = workingSet.getElements();
				if (ArrayUtils.contains(adaptables, selectedIProject))
				{
					String wsName = workingSet.getName();
					if (!StringUtils.equals(wsName,
							IWorkingSetManager.OTHER_WORKINGSET_NAME))
					{
						if (assignedWorkingSets.size() > 0)
							buildName.append("," + wsName); //$NON-NLS-N$
						else
							buildName.append(wsName);						
						assignedWorkingSets.add(workingSet);
					}
				}
			}						
			String name = buildName.toString();
			if (StringUtils.isNotEmpty(name))
			{
				comboWorkingsets.add(name);
				comboWorkingsets.setText(name);
				comboWorkingsets.setData(name, assignedWorkingSets.clone());			
			}			
		}
		else
		{
			if (navigator != null)
			{
				// ein WorkingSet ist im Navigator selektiert
				Object selObj = ((IStructuredSelection) navigator.getViewer()
						.getSelection()).getFirstElement();
				if (selObj instanceof IWorkingSet)
				{
					IWorkingSet selectedWorkingSet = (IWorkingSet) selObj;
					assignedWorkingSets.add(selectedWorkingSet);
					String wsName = selectedWorkingSet.getName();
					if (!StringUtils.equals(wsName,
							IWorkingSetManager.OTHER_WORKINGSET_NAME))
					{
						comboWorkingsets.add(wsName);
						comboWorkingsets.setText(wsName);
						comboWorkingsets.setData(wsName,
								assignedWorkingSets.clone());
					}
				}
			}
		}
	}

	public ProjectData getProjectData()
	{
		return projectData;
	}

	public void setProjectData(ProjectData projectData)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();
		this.projectData = projectData;
		m_bindingContext = initDataBindings();
	}

	public void setDefaultProjectName(String defaultProjectName)
	{
		this.defaultProjectName = defaultProjectName;
	}
	
	public List<IWorkingSet> getAssignedWorkingSets()
	{
		if(!btnCheckWorkingset.getSelection())
			assignedWorkingSets.clear();
		return assignedWorkingSets;
	}
	
	public List<IWorkingSet> getAddedWorkingSets()
	{
		return addedWorkingSets;
	}
	
	public String getProjectName()
	{
		return projectName;
	}
	
	
	
	public NtProjectProperty getProjectProperty()
	{
		return projectProperty;
	}

	public void setProjectProperty(NtProjectProperty projectProperty)
	{
		this.projectProperty = projectProperty;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtProjectNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtProjectName);
		IObservableValue nameProjectDataObserveValue = BeanProperties.value("name").observe(projectData);
		bindingContext.bindValue(observeTextTxtProjectNameObserveWidget, nameProjectDataObserveValue, null, null);
		//
		IObservableValue observeTextTxtDescriptionObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtDescription);
		IObservableValue descriptionProjectDataObserveValue = BeanProperties.value("description").observe(projectData);
		bindingContext.bindValue(observeTextTxtDescriptionObserveWidget, descriptionProjectDataObserveValue, null, null);
		//
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(new EmptyStringValidator());
		bindingContext.bindValue(observeTextTxtProjectNameObserveWidget, nameProjectDataObserveValue, strategy, null);
		return bindingContext;
	}
}
