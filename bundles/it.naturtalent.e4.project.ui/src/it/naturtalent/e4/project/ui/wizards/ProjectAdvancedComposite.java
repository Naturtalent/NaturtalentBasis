package it.naturtalent.e4.project.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.beans.BeanProperties;

public class ProjectAdvancedComposite extends Composite
{
	private DataBindingContext m_bindingContext;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());

	
	private ProjectData projectData;
	
	private Text txtNotizen;

	private CCombo comboWorkingSets;

	private Button btnSelect;

	private Button btnAddWorkingSet;

	private IResourceNavigator navigator = null;

	private NtProject ntProject = null;

	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	// Liste der neuangelegten WorkingSets
	private List<IWorkingSet>addedWorkingSets = null;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ProjectAdvancedComposite(Composite parent, int style, final IResourceNavigator navigator)
	{
		super(parent, style);
		this.navigator = navigator;
		
		if(navigator != null)
		{
			IStructuredSelection selection = (IStructuredSelection) navigator
					.getViewer().getSelection();
			Object selObj = selection.getFirstElement();
			
			if (selObj instanceof IResource)
			{
				IProject project = ((IResource)selObj).getProject();
				ntProject = new NtProject(project);
			}
		}
				
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new GridLayout(1, false));
		
		Section sctnAdvanced = formToolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);
		sctnAdvanced.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		sctnAdvanced.setBounds(0, 0, 117, 23);
		formToolkit.paintBordersFor(sctnAdvanced);
		sctnAdvanced.setText(Messages.ProjectAdvancedComposite_extended);
		sctnAdvanced.setExpanded(true);
		
		Composite composite = formToolkit.createComposite(sctnAdvanced, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		sctnAdvanced.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblNotice = formToolkit.createLabel(composite, Messages.ProjectAdvancedComposite_notes, SWT.NONE);
		lblNotice.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtNotizen = formToolkit.createText(composite, "", SWT.MULTI);
		GridData gd_txtNotizen = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtNotizen.heightHint = 110;
		txtNotizen.setLayoutData(gd_txtNotizen);
		
		Label lblSpace = new Label(composite, SWT.NONE);
		formToolkit.adapt(lblSpace, true, true);
		new Label(composite, SWT.NONE);
		
		Group grpWorkingSets = new Group(composite, SWT.NONE);
		grpWorkingSets.setLayout(new GridLayout(3, false));
		grpWorkingSets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		grpWorkingSets.setText(Messages.ProjectAdvancedComposite_workingsets);
		formToolkit.adapt(grpWorkingSets);
		formToolkit.paintBordersFor(grpWorkingSets);
		
		btnAddWorkingSet = new Button(grpWorkingSets, SWT.CHECK);
		btnAddWorkingSet.setSelection(true);
		btnAddWorkingSet.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				btnSelect.setEnabled(btnAddWorkingSet.getSelection());
				comboWorkingSets.setEnabled(btnAddWorkingSet.getSelection());
			}
		});
		GridData gd_btnAddWorkingSet = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_btnAddWorkingSet.widthHint = 406;
		btnAddWorkingSet.setLayoutData(gd_btnAddWorkingSet);
		formToolkit.adapt(btnAddWorkingSet, true, true);
		btnAddWorkingSet.setText(Messages.ProjectAdvancedComposite_assign);
		
		Label lblWorkingSets = formToolkit.createLabel(grpWorkingSets, Messages.ProjectAdvancedComposite_workingSetLaels, SWT.NONE);
		
		comboWorkingSets = new CCombo(grpWorkingSets, SWT.BORDER);
		comboWorkingSets.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String key = comboWorkingSets.getItem(comboWorkingSets.getSelectionIndex());				
				assignedWorkingSets = (ArrayList<IWorkingSet>) comboWorkingSets.getData(key);
			}
		});
		comboWorkingSets.setEditable(false);
		comboWorkingSets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(comboWorkingSets);
		formToolkit.paintBordersFor(comboWorkingSets);
		
		// Button 'select' - WorkingSets auswaehlen
		btnSelect = formToolkit.createButton(grpWorkingSets, Messages.ProjectAdvancedComposite_select, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{			
				// SelectDialog oeffnen
				IWorkingSet [] activeWorkingSets = assignedWorkingSets.toArray(new IWorkingSet[assignedWorkingSets.size()]);
				addedWorkingSets = null;
				SelectWorkingSetDialog dialog = new SelectWorkingSetDialog(getShell(), activeWorkingSets);				
				if(dialog.open() == ConfigureWorkingSetDialog.OK)
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
					comboWorkingSets.add(name);
					comboWorkingSets.setText(buildName.toString());
					comboWorkingSets.setData(name, assignedWorkingSets.clone());					
				}
			}
		});
		btnSelect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		init();
		

	}

	private void init()
	{
		if (ntProject != null)
		{
			// alle WorkingSets denen das Projekt zugeordnet ist in Combo uebernehmen
			StringBuilder buildName = new StringBuilder(5);
			IProject project = ntProject.getIProject();
			IWorkingSet[] workingSets = navigator.getWorkingSets();
			for (IWorkingSet workingSet : workingSets)
			{
				IAdaptable[] adaptables = workingSet.getElements();
				if (ArrayUtils.contains(adaptables, project))
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
				comboWorkingSets.add(name);
				comboWorkingSets.setText(name);
				comboWorkingSets.setData(name, assignedWorkingSets.clone());			
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
						comboWorkingSets.add(wsName);
						comboWorkingSets.setText(wsName);
						comboWorkingSets.setData(wsName,
								assignedWorkingSets.clone());
					}
				}
			}
		}
		
		IProjectDataAdapter projectAdapter = ProjectDataAdapterRegistry
				.getProjectDataAdapter(ProjectData.PROP_PROJECTDATACLASS);
		if (projectAdapter != null)
		{
			projectData = (ProjectData) projectAdapter.getProjectData();
			if(projectData != null)
			{
				String notice = projectData.getDescription();
				txtNotizen.setText(StringUtils.isNotEmpty(notice) ? notice : "");				
				m_bindingContext = initDataBindings();
			}
		}
	}
	
	
	public List<IWorkingSet> getAssignedWorkingSets()
	{
		if(!btnAddWorkingSet.getSelection())
			assignedWorkingSets.clear();
		return assignedWorkingSets;
	}
	
	public List<IWorkingSet> getAddedWorkingSets()
	{
		return addedWorkingSets;
	}
	

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtNotizenObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtNotizen);
		IObservableValue descriptionProjectDataObserveValue = BeanProperties.value("description").observe(projectData);
		bindingContext.bindValue(observeTextTxtNotizenObserveWidget, descriptionProjectDataObserveValue, null, null);
		//
		return bindingContext;
	}
}
