package it.naturtalent.e4.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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

/**
 * Default UI zur Suche innerhalb der NtProjects. Ueber den Focus koennen alle Projekte des Workspace oder WorkingSets als Grundlage
 * der Suche ausgewaehlt werden.
 *  
 * @author dieter
 *
 */
public class DefaultNtProjectSearchComposite extends Composite
{
	// Key Definitionen der DialogSettings 
	public static final String SEARCH_PROJECTPATTERN_SETTINGS = "searchprojektpattern"; //$NON-NLS-1$
	private static final String SEARCH_FOCUS_SETTINGS = "searchfocus"; //$NON-NLS-1$
	private static final String SEARCH_WORKINGSET_SETTINGS = "searchworkingset"; //$NON-NLS-1$
	
	// Liste der ausgewaehlten WorkingSets	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	
	// UIs
	private Combo comboPattern;
	private Button btnCheckCaseSensitiv; 
	private Button btnCheckRegularExpression;
	private Button btnRadioWorkingSets;
	private Button btnRadioAllProjects;
	private Button btnBrowseWS;
	private Combo comboWorkingSet;
	private Label lblMatchFilter;
	
	// Dialogsettings
	//private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();

	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DefaultNtProjectSearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		// Label Suchpattern
		Label lblPattern = new Label(this, SWT.NONE);
		lblPattern.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblPattern.setText(Messages.SearchDialog_lblNewLabel_text);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		// Combo Suchpattern
		comboPattern = new Combo(this, SWT.BORDER);
		comboPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Composite Checkbuttons
		Composite compositeCaseSensitiy = new Composite(this, SWT.NONE);
		compositeCaseSensitiy.setLayout(new FillLayout(SWT.VERTICAL));
				
		btnCheckCaseSensitiv = new Button(compositeCaseSensitiy, SWT.CHECK);
		btnCheckCaseSensitiv.setText(Messages.ProjectSearchComposite_checkCaseSensitiv);
		
		btnCheckRegularExpression = new Button(compositeCaseSensitiy, SWT.CHECK);
		btnCheckRegularExpression.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				lblMatchFilter.setVisible(!btnCheckRegularExpression.getSelection());
			}
		});
		btnCheckRegularExpression.setText(Messages.ProjectSearchComposite_checkRegularExpression);
		new Label(this, SWT.NONE);
		
		// Lbel Matchfilter 
		lblMatchFilter = new Label(this, SWT.NONE);
		lblMatchFilter.setText("(*=beliebiger Text, ?=ein Buchstabe,\\=escape für: * ?");
		new Label(this, SWT.NONE);
		
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
				
		// Group Focus
		Group groupFocus = new Group(this, SWT.NONE);
		groupFocus.setLayout(new GridLayout(3, false));
		groupFocus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		groupFocus.setText(Messages.SearchDialog_group_text);
		
		// Radiobutton Projekte
		btnRadioAllProjects = new Button(groupFocus, SWT.RADIO);
		btnRadioAllProjects.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				btnBrowseWS.setEnabled(!btnRadioAllProjects.getSelection());
			}
		});
		btnRadioAllProjects.setText(Messages.SearchDialog_btnRadioWorkspace_text);
		new Label(groupFocus, SWT.NONE);
		new Label(groupFocus, SWT.NONE);
		
		// Radiobutton Workingsets
		btnRadioWorkingSets = new Button(groupFocus, SWT.RADIO);		
		btnRadioWorkingSets.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				
				//comboWorkingSet.setEnabled(true);
				//btnBrowseWS.setEnabled(true);
				//setResultAdaptables();
				
			}
		});
		btnRadioWorkingSets.setText(Messages.SearchDialog_btnRadioWorkingSet_text);
		
		// Combo WorkingSet
		comboWorkingSet = new Combo(groupFocus, SWT.READ_ONLY);
		comboWorkingSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// Radiobutton WorkingSet Browse
		btnBrowseWS = new Button(groupFocus, SWT.NONE);
		btnBrowseWS.setText(Messages.SearchDialog_btnBrowse_text);
		new Label(groupFocus, SWT.NONE);
		new Label(groupFocus, SWT.NONE);
		new Label(groupFocus, SWT.NONE);
		
		// WorkingSet selektieren
		btnBrowseWS.addSelectionListener(new SelectionAdapter()
		{			
			@Override
			public void widgetSelected(SelectionEvent e)
			{	
				// WorkingSet Auswahldialog (initialisiert mit einem Array der vorhandenen WorkingSets)
				SelectWorkingSetDialog dialog = new SelectWorkingSetDialog(getShell(), 
						assignedWorkingSets.toArray(new IWorkingSet[assignedWorkingSets.size()]));		
				dialog.create();
				dialog.setMessage("WorkingSets für die Suche auswählen");
				if(dialog.open() == ConfigureWorkingSetDialog.OK)
				{
					// die im Dialog ausgewaehlten WorkingSets abfragen 
					IWorkingSet [] workingSets = dialog.getConfigResult();
					assignedWorkingSets.clear();				
					StringBuilder buildComboText = new StringBuilder(5);
					for(IWorkingSet workingSet : workingSets)
					{
						// die Ws-Namen (durch Komma getrannt) zusammenfassen
						String wsName = workingSet.getName();
						if (assignedWorkingSets.size() > 0)
							buildComboText.append("," + wsName); // $NON-NLS-N$
						else
							buildComboText.append(wsName);

						// ausgewaehltes WorkingSet zur Liste hinzufuegen
						assignedWorkingSets.add(workingSet);
					}
					
					// Text zur Combo hinzufuegen 
					String comboText = buildComboText.toString();
					if(comboWorkingSet.indexOf(comboText) < 0)
						comboWorkingSet.add(comboText);
										
					comboWorkingSet.setText(comboText);							
				}				
			}
		});
	}
	
	/**
	 * DialogSettings in den UIs initialisieren.
	 * 
	 * @param settings
	 */
	public void setDialogSettings(IDialogSettings settings)
	{
		// Suchmuster vorgeben
		String [] searchPattern = settings.getArray(SEARCH_PROJECTPATTERN_SETTINGS);	
		if(searchPattern != null)
		{
			comboPattern.setItems(searchPattern);
			comboPattern.setText(comboPattern.getItem(0));
		}
		
		// Focus settings (Projekte oder Workspaces)
		Boolean focus = settings.getBoolean(SEARCH_FOCUS_SETTINGS);	
		focus = (focus != null) ? focus : true;
		btnRadioAllProjects.setSelection(focus);
		btnRadioWorkingSets.setSelection(!focus);
		
		// WorkingSet settings
		assignedWorkingSets.clear();	
		String wsSettingName = settings.get(SEARCH_WORKINGSET_SETTINGS);
		if(StringUtils.isNotEmpty(wsSettingName))
		{	
			comboWorkingSet.add(wsSettingName);			
			comboWorkingSet.setText(wsSettingName);			
		}
	}
	
	/**
	 * DialogSettings speichern
	 * 
	 * @param settings
	 */
	public void saveDialogSettings(IDialogSettings settings)
	{
		// speichern des Suchpatterns
		String searchPattern = comboPattern.getText();
		if (StringUtils.isNotEmpty(searchPattern))
		{
			String[] projectPattern = settings.getArray(SEARCH_PROJECTPATTERN_SETTINGS);
			if (projectPattern == null)
			{
				// Setting wird initialisiert
				projectPattern = ArrayUtils.add(null, searchPattern);
				settings.put(SEARCH_PROJECTPATTERN_SETTINGS,projectPattern);
			}
				
			if (!ArrayUtils.contains(projectPattern, searchPattern))
			{
				projectPattern = ArrayUtils.insert(0, projectPattern,searchPattern);
				if (projectPattern.length > 9)
					projectPattern = ArrayUtils.remove(projectPattern, 9);
				settings.put(SEARCH_PROJECTPATTERN_SETTINGS,projectPattern);
			}			
		}
		
		// der Selektionstatus des 'btnRadioAllProjects' wird als Setting gespeichert
		settings.put(SEARCH_FOCUS_SETTINGS, btnRadioAllProjects.getSelection());
		
		// Textfeldinhalt der WorkingSet-Combo wird als Setting gespeichert
		if(!btnRadioAllProjects.getSelection())		
			settings.put(SEARCH_WORKINGSET_SETTINGS, comboWorkingSet.getText());
	}
	
	/**
	 * Rueckgabe der Adaptables, auf die in die Suche einbezogen werden. 
	 *  
	 * @return
	 */
	private List<IAdaptable> getFocusedAdaptables()
	{
		ArrayList<IAdaptable>focusedAdaptablesList = new ArrayList<IAdaptable>();
				
		if(!btnRadioAllProjects.isDisposed() && btnRadioAllProjects.getSelection())
		{
			// alle Projekte sind im Focus
			IResourceNavigator resourceNavigator = it.naturtalent.e4.project.ui.Activator.findNavigator();
			IAdaptable [] allAdaptables = resourceNavigator.getAggregateWorkingSet().getElements();
			if(ArrayUtils.isNotEmpty(allAdaptables))
				return (Arrays.asList(allAdaptables));
		}
		else
		{		
			// alle Projekte der ausgewaehlten WorkingSets
			for(IWorkingSet workingSet : assignedWorkingSets)
			{
				IAdaptable[] adaptables = workingSet.getElements();
				for (IAdaptable adaptable : adaptables)
					focusedAdaptablesList.add(adaptable);
			}
		}
				
		return focusedAdaptablesList;
	}
	
	/**
	 * Gibt die Search-Optionen in einer eigenen Klasse zurueck.
	 * 
	 * @return
	 */
	public SearchOptions getSearchOptions()
	{
		SearchOptions searchOptions = new SearchOptions();
		searchOptions.setSearchPattern(comboPattern.getText());
		searchOptions.setCaseSensitive(btnCheckCaseSensitiv.getSelection());
		searchOptions.setRegularExpression(btnCheckRegularExpression.getSelection());
		searchOptions.setSearchItems(getFocusedAdaptables());
		
		return searchOptions;
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
