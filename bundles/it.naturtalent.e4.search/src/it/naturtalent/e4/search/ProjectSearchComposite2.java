package it.naturtalent.e4.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.swt.layout.FillLayout;

/**
 * UI fuer die Projekt SearchPage
 * @author dieter
 *
 */
public class ProjectSearchComposite2 extends Composite
{
	// DialogSettings 
	private static final String SEARCH_FOCUS_SETTINGS = "searchfocus"; //$NON-NLS-1$
	private static final String SEARCH_WORKINGSET_SETTINGS = "searchworkingset"; //$NON-NLS-1$
	public static final String SEARCH_PROJECT_SETTING = "searchpattern"; //$NON-NLS-1$
	public static final String SEARCH_PROJECT_SETTINGS = "searchprojectpattern"; //$NON-NLS-1$
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
	
	private Button btnRadioAllProjects;
	
	private Button btnRadioWorkingSets;
	
	private Combo comboWorkingSet;
	
	private Button btnBrowseWS;
	
	private IAdaptable [] resultAdaptables;
	private CCombo textSearchMask;
	private Composite compositeCaseSensitive;
	private Button checkCaseSensitiv;
	private Button checkRegularExpression;
	private Label lblMatchtFilter;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectSearchComposite2(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		// Label Suchpattern
		Label lblSearchMask = new Label(this, SWT.NONE);
		lblSearchMask.setText(Messages.SearchDialog_lblNewLabel_text);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		// Combo Suchpattern
		textSearchMask = new CCombo(this, SWT.BORDER);
		textSearchMask.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Items aus dem DialogSetting in Combo uebernehmen		
		String [] projectSearchTexte= settings.getArray(SEARCH_PROJECT_SETTINGS);		
		if(projectSearchTexte != null)
			textSearchMask.setItems(projectSearchTexte);
		
		// Composite Checkbuttons
		compositeCaseSensitive = new Composite(this, SWT.NONE);
		compositeCaseSensitive.setLayout(new FillLayout(SWT.VERTICAL));
		
		// Composite Checkbuttons
		checkCaseSensitiv = new Button(compositeCaseSensitive, SWT.CHECK);
		checkCaseSensitiv.setText(Messages.ProjectSearchComposite_checkCaseSensitiv);
		
		checkRegularExpression = new Button(compositeCaseSensitive, SWT.CHECK);
		checkRegularExpression.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				lblMatchtFilter.setVisible(!checkRegularExpression.getSelection());
			}
		});
		checkRegularExpression.setText(Messages.ProjectSearchComposite_checkRegularExpression);
		new Label(this, SWT.NONE);
		
		lblMatchtFilter = new Label(this, SWT.NONE);
		lblMatchtFilter.setText(Messages.ProjectSearchComposite_lblNewLabel_text);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Group grpFocus = new Group(this, SWT.NONE);
		grpFocus.setLayout(new GridLayout(3, false));
		grpFocus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
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
	
	/*
	 * der uebergebene String wird im Model 'SearchPattern' uebernommen und gleichzeitig via 'Databinding'
	 * im Textfeld dargestellt
	 */
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
		
		// Pattern aus dem DialogSetting in das Modell und Textfeld(CCombo) uebernehmen 
		setPattern(settings.get(SEARCH_PROJECT_SETTING));		
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	/*
	 * Rueckgabe der SearchPattern
	 */
	public String getSearchPattern()
	{		
		return searchPattern.pattern;
	}
	
	public boolean getCaseSensitve()
	{
		return checkCaseSensitiv.getSelection();
	}

	private void setResultAdaptables()
	{
		resultAdaptables = null;
		
		if (btnRadioAllProjects.getSelection())
		{
			if(resourceNavigator != null)
				resultAdaptables = resourceNavigator.getAggregateWorkingSet()
					.getElements();
		}

		else
		{
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
		IObservableValue observeTextTextSearchMaskObserveWidget = WidgetProperties.text().observe(textSearchMask);
		IObservableValue patternSearchPatternObserveValue = PojoProperties.value("pattern").observe(searchPattern);
		bindingContext.bindValue(observeTextTextSearchMaskObserveWidget, patternSearchPatternObserveValue, null, null);
		//
		return bindingContext;
	}
}
