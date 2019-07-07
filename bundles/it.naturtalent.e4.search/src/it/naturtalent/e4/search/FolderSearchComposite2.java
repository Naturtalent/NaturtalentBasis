package it.naturtalent.e4.search;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IWorkingSet;

public class FolderSearchComposite2 extends Composite
{
	// DialogSettings 
	private static final String SEARCH_FOCUS_SETTINGS = "searchfocus"; //$NON-NLS-1$
	private static final String SEARCH_WORKINGSET_SETTINGS = "searchworkingset"; //$NON-NLS-1$
	private static final String SEARCH_PATTERN_SETTINGS = "searchpattern"; //$NON-NLS-1$
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	private DataBindingContext m_bindingContext;
	
	private IResourceNavigator resourceNavigator;
	
	//private IWorkingSet [] workingSets;
	
	// Liste der zugeordneten WorkingSets	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();

	
	public class SearchPattern
	{
		String pattern;

		public String getPattern()
		{
			return pattern;
		}

		public void setPattern(String pattern)
		{
			this.pattern = pattern;
		}
	}
	
	private SearchPattern searchPattern = new SearchPattern();
	
	private Text textSearchMask;
	
	private Button btnRadioAllProjects;
	
	private Button btnRadioWorkingSets;
	
	private Combo comboWorkingSet;
	
	private Button btnBrowseWS;
	
	private Button btnCaseSensitive;
	
	private IAdaptable [] resultAdaptables;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FolderSearchComposite2(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label lblSearchMask = new Label(this, SWT.NONE);
		lblSearchMask.setText(Messages.SearchDialog_lblNewLabel_text);
		new Label(this, SWT.NONE);
		
		textSearchMask = new Text(this, SWT.BORDER);
		textSearchMask.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnCaseSensitive = new Button(this, SWT.CHECK);
		btnCaseSensitive.setText("Check Button");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Group grpFocus = new Group(this, SWT.NONE);
		grpFocus.setLayout(new GridLayout(3, false));
		grpFocus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		grpFocus.setText(Messages.SearchDialog_group_text);
		
		btnRadioAllProjects = new Button(grpFocus, SWT.RADIO);
		btnRadioAllProjects.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				comboWorkingSet.setEnabled(false);
				btnBrowseWS.setEnabled(false);
				setResultAdaptables();
			}
		});
		btnRadioAllProjects.setText(Messages.SearchDialog_btnRadioWorkspace_text);
		new Label(grpFocus, SWT.NONE);
		new Label(grpFocus, SWT.NONE);
		
		btnRadioWorkingSets = new Button(grpFocus, SWT.RADIO);
		btnRadioWorkingSets.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				comboWorkingSet.setEnabled(true);
				btnBrowseWS.setEnabled(true);
				setResultAdaptables();
			}
		});
		btnRadioWorkingSets.setText(Messages.SearchDialog_btnRadioWorkingSet_text);
		
		comboWorkingSet = new Combo(grpFocus, SWT.NONE);
		comboWorkingSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnBrowseWS = new Button(grpFocus, SWT.NONE);
		btnBrowseWS.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				//workingSets = null;
				SelectWorkingSetDialog dialog = new SelectWorkingSetDialog(
						getShell(), assignedWorkingSets
								.toArray(new IWorkingSet[assignedWorkingSets
										.size()]));				
				if(dialog.open() == ConfigureWorkingSetDialog.OK)
				{
					// die ausgewaehlten WorkingSets in Combo uebernehmen 
					IWorkingSet [] workingSets = dialog.getConfigResult();
					assignedWorkingSets.clear();				
					StringBuilder buildName = new StringBuilder(5);
					for(IWorkingSet workingSet : workingSets)
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
					comboWorkingSet.add(name);
					comboWorkingSet.setText(name);
					comboWorkingSet.setData(name, assignedWorkingSets.clone());
					setResultAdaptables();
				}	
			}
		});
		btnBrowseWS.setText(Messages.SearchDialog_btnBrowse_text);
		m_bindingContext = initDataBindings();
		init();
	}
	
	private void setPattern(String pattern)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();
		
		searchPattern.pattern = StringUtils.isNotBlank(pattern) ? pattern : "";
		m_bindingContext = initDataBindings();
	}
	
	private void init()
	{
		Boolean focus = settings.getBoolean(SEARCH_FOCUS_SETTINGS);		
		focus = focus != null ? focus : true;

		btnRadioAllProjects.setSelection(focus);
		btnRadioWorkingSets.setSelection(!focus);
		
		String wsSettingNames = settings.get(SEARCH_WORKINGSET_SETTINGS);
		if(StringUtils.isNotEmpty(wsSettingNames))
		{
			assignedWorkingSets.clear();	
			StringBuilder buildName = new StringBuilder(5);
			WorkingSetManager wsManager = it.naturtalent.e4.project.ui.Activator
					.getWorkingSetManager();
			String wsNames [] = StringUtils.split(wsSettingNames, ",");
			for(String name : wsNames)
			{
				IWorkingSet workingSet = wsManager.getWorkingSet(name);
				if(workingSet != null)
				{
					if (assignedWorkingSets.size() > 0)
						buildName.append("," + name); //$NON-NLS-N$
					else
						buildName.append(name);								
					assignedWorkingSets.add(workingSet);
				}	
			}
			String name = buildName.toString();
			comboWorkingSet.add(name);
			comboWorkingSet.setText(name);
			comboWorkingSet.setData(name, assignedWorkingSets.clone());
		}
		
		setResultAdaptables();
		setPattern(settings.get(SEARCH_PATTERN_SETTINGS));		
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public String getResultSearchPattern()
	{
		if(StringUtils.isNotEmpty(searchPattern.pattern))
			settings.put(SEARCH_PATTERN_SETTINGS, searchPattern.pattern);
		return searchPattern.pattern;
	}
	
	public boolean getCaseSensitve()
	{
		return btnCaseSensitive.getSelection();
	}

	private void setResultAdaptables()
	{
		resultAdaptables = null;
		
		if (btnRadioAllProjects.getSelection())
		{
			// Focus auf 'alle Projekte'
			if(resourceNavigator != null)
				resultAdaptables = resourceNavigator.getAggregateWorkingSet()
					.getElements();
		}

		else
		{
			// Focus auf 'Workingsets'
			List<IAdaptable> wsAdaptables = new ArrayList<IAdaptable>();
			List<IWorkingSet> workingSets = (List<IWorkingSet>) comboWorkingSet
					.getData(comboWorkingSet.getText());
			if (workingSets != null)
			{
				for (IWorkingSet workingSet : workingSets)
				{
					IAdaptable[] adaptables = workingSet.getElements();
					for (IAdaptable adaptable : adaptables)
						wsAdaptables.add(adaptable);
				}
				resultAdaptables = wsAdaptables
						.toArray(new IAdaptable[wsAdaptables.size()]);
			}
		}
	}
	
	public IAdaptable [] getResultAdaptables()
	{
		if(ArrayUtils.isNotEmpty(resultAdaptables))
			settings.put(SEARCH_FOCUS_SETTINGS, btnRadioAllProjects.getSelection());
		if(btnRadioWorkingSets.getSelection())
			settings.put(SEARCH_WORKINGSET_SETTINGS, comboWorkingSet.getText());
		return resultAdaptables;
	}
	
	public void setResourceNavigator(IResourceNavigator resourceNavigator)
	{
		this.resourceNavigator = resourceNavigator;
		init();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextSearchMaskObserveWidget = WidgetProperties.text(SWT.Modify).observe(textSearchMask);
		IObservableValue patternSearchPatternObserveValue = PojoProperties.value("pattern").observe(searchPattern);
		bindingContext.bindValue(observeTextTextSearchMaskObserveWidget, patternSearchPatternObserveValue, null, null);
		//
		return bindingContext;
	}
}
