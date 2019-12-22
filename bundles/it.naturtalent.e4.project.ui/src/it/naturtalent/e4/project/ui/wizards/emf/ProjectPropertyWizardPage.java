package it.naturtalent.e4.project.ui.wizards.emf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.actions.emf.NewProjectAction;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.emf.ProjectModelEventKey;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

/**
 * Standardseite des ProjektPropertyWizards.
 *  
 * @author dieter
 *
 */
public class ProjectPropertyWizardPage extends WizardPage
{
	public static final String NT_WIZARDPAGE = "ntWizardPage";
	
	private NtProjectProperty projectProperty;
	//private ProjectData projectData;
	
	private IEventBroker eventBroker;
	
	// momentan vom Navigator selektiertes Projekt
	protected IProject selectedIProject;
	
	protected INtProjectPropertyFactoryRepository ntProjectPropertyFactoryRepository;
	
	protected Text txtProjectName;
	//protected Text txtDescription;
	
	private Button btnCheckWorkingset;
	private Button btnSelectWs;
	private CCombo comboWorkingsets;
	//private static ControlDecoration controlDecoration;
	
	//private String defaultProjectName = null;
	//private String projectName = null;
	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	// Liste der neuangelegten WorkingSets
	private List<IWorkingSet>addedWorkingSets = null;
	
	// Haendler ueberwacht Modelvalidation
	private EventHandler modelValidationEventHandler = new EventHandler()
	{		
		@Override
		public void handleEvent(Event event)
		{
			// ist kein Projektname definiert - ist die Seite nicht komplett
			Object obj = event.getProperty(IEventBroker.DATA);
			ProjectPropertyWizardPage.this.setPageComplete((Boolean) obj);
		}
	};
	
	/**
	 * Create the wizard.
	 */
	public ProjectPropertyWizardPage()
	{
		super(NT_WIZARDPAGE);
		setTitle("Project Wizard"); //$NON-NLS-N$
		setDescription("Projekt bearbeiten"); //$NON-NLS-N$
	}
	
	@PostConstruct
	private void postConstruct(
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject iProject,
			INtProjectPropertyFactoryRepository ntProjectPropertyFactoryRepository, @Optional IEventBroker eventBroker)
	{
		this.selectedIProject = iProject;
		this.ntProjectPropertyFactoryRepository = ntProjectPropertyFactoryRepository;
		this.eventBroker = eventBroker;		
		eventBroker.subscribe(ProjectModelEventKey.PROJECT_VALIDATION_MODELEVENT, modelValidationEventHandler);
		
		// im E4Context hinterlegter Name ist Indiz fuer 'neues' - Projekt
		if ((StringUtils.isNotEmpty((String) E4Workbench.getServiceContext()
				.get(NewProjectAction.PREDIFINED_PROJECTNAME)))
				|| (iProject == null))
		{
			setDescription("neues Projekt erzeugen"); //$NON-NLS-N$
			setImageDescriptor(Icon.WIZBAN_NEW.getImageDescriptor(IconSize._75x66_TitleDialogIconSize));
		}
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
		
		try
		{			
			ECPSWTViewRenderer.INSTANCE.render(container, (EObject) projectProperty.getNtPropertyData());
			
			if(txtProjectName != null)
				txtProjectName.selectAll();
			
		} catch (ECPRendererException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
				IWorkingSet[] activeWorkingSets = assignedWorkingSets
						.toArray(new IWorkingSet[assignedWorkingSets.size()]);
				addedWorkingSets = null;

				SelectWorkingSetDialog dialog = new SelectWorkingSetDialog(
						getShell(), activeWorkingSets);
				if (dialog.open() == SelectWorkingSetDialog.OK)
				{
					// die neuangelegten WokingSets
					addedWorkingSets = dialog.getAddedWorkingSets();

					// die ausgewaehlten WorkingSets in Combo uebernehmen
					IWorkingSet[] configResults = dialog.getConfigResult();
					assignedWorkingSets.clear();
					StringBuilder buildName = new StringBuilder(5);
					for (IWorkingSet workingSet : configResults)
					{
						String wsName = workingSet.getName();
						if (!StringUtils.equals(wsName,
								IWorkingSetManager.OTHER_WORKINGSET_NAME))
						{
							if (assignedWorkingSets.size() > 0)
								buildName.append("," + wsName); // $NON-NLS-N$
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
		
	
		
		// Eigenschaften und Workingsetinfo's des selektierten Projekts uebernehmen
		//initProperties();
		initWorkingSets();
		
	

		
	}
	
	
	
	@Override
	public void dispose()
	{
		eventBroker.unsubscribe(modelValidationEventHandler);
		super.dispose();
	}
	
	/*
	 * die Eigenschaften des selektierten Projekts uebernehmen
	 */
	/*
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
					projectProperty.init();
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
	*/

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
				comboWorkingsets.setText(name);
				comboWorkingsets.add(name);				
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
						comboWorkingsets.setText(wsName);
						comboWorkingsets.add(wsName);
						comboWorkingsets.setData(wsName,
								assignedWorkingSets.clone());
					}
				}
			}
		}
	}

	/*
	public ProjectData getProjectData()
	{
		return projectData;
	}
	*/
	
	

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
	
	/*
	public String getProjectName()
	{
		return projectName;
	}
	*/
	
	public NtProjectProperty getProjectProperty()
	{
		return projectProperty;
	}

	/**
	 * Uebergabe der Adapterseite. 
	 * Ermoeglicht der WizardPage den Zugriff auf die konkreten Projekteigenschaften.
	 * 
	 * @param projectProperty
	 */
	public void setProjectProperty(NtProjectProperty projectProperty)
	{
		this.projectProperty = projectProperty;
	}
	
	/*
	@Inject
	@Optional
	public void handleModelChangedEvent(@UIEventTopic("PROJECTNAMEFIELD_CREATED") Text text)
	{
		txtProjectName = text;
	}
	*/
	
}
