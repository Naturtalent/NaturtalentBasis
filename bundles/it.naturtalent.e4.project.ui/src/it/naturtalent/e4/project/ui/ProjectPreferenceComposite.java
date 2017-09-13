package it.naturtalent.e4.project.ui;

import it.naturtalent.e4.project.ui.FileTemplateDialog.PreferenceData;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class ProjectPreferenceComposite extends Composite
{
	private Map<String,String>templateMap;
	
	private Map<String,String>defaultMap;
	
	private Table table;
	private Button btnDelete;
	private Button btnAdd;
	private Button btnEdit;
	private TableViewer tableViewer;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectPreferenceComposite(Composite parent, int style)
	{
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblNewLabel.widthHint = 200;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText(Messages.ProjectPreferenceComposite_lblNewLabel_text);
		new Label(this, SWT.NONE);
		
		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 300;
		gd_composite.heightHint = 400;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new TableColumnLayout());
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateWidgets();
			}
		});
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		Composite compositeButton = new Composite(this, SWT.NONE);
		compositeButton.setLayout(new FillLayout(SWT.VERTICAL));
		
		btnAdd = new Button(compositeButton, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileTemplateDialog dialog = new FileTemplateDialog(getShell());
				dialog.create();
				PreferenceData preferenceData = dialog.createPreferenceData(); 
				dialog.setPreferenceData(preferenceData);				
				if(dialog.open() == FileTemplateDialog.OK)
				{
					preferenceData = dialog.getPreferenceData();
					templateMap.put(preferenceData.name, preferenceData.url);					
					tableViewer.refresh();					
				}
			}
		});
		btnAdd.setText(Messages.ProjectPreferenceComposite_btnAdd_text);
		
		btnEdit = new Button(compositeButton, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doEdit();
			}
		});
		btnEdit.setText(Messages.ProjectPreferenceComposite_btnEdit_text);
		
		btnDelete = new Button(compositeButton, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
				String key = (String) selection.getFirstElement();
				templateMap.remove(key);
				tableViewer.refresh();
			}
		});
		btnDelete.setText(Messages.ProjectPreferenceComposite_btnDelete_text);

		// Defaultdaten zusammenfassen
		defaultMap = new HashMap<String, String>();
		IEclipsePreferences defaultNode = DefaultScope.INSTANCE
				.getNode(NtPreferences.ROOT_PREFERENCES_NODE);
		String defaultValue = defaultNode.get(NtPreferences.FILE_TEMPLATE_PREFERENCE, null);
		String[] templateArray = StringUtils.split(defaultValue, ",");
		for (int i = 0; (i + 1) < templateArray.length; i++)
			defaultMap.put(templateArray[i], templateArray[++i]);
		
		updateWidgets();		
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void doEdit()
	{	
		StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
		String key = (String) selection.getFirstElement();
		
		FileTemplateDialog dialog = new FileTemplateDialog(getShell());
		dialog.create();
		
		PreferenceData preferenceData = dialog.createPreferenceData(); 
		preferenceData.name = key;
		preferenceData.url = templateMap.get(key);
		dialog.setPreferenceData(preferenceData);
		
		if(dialog.open() == FileTemplateDialog.OK)
		{
			preferenceData = dialog.getPreferenceData();
			templateMap.put(preferenceData.name, preferenceData.url);
		}
		
	}
	
	public void setPreferenceValue(String preferenceValue)
	{
		templateMap = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(preferenceValue))
		{
			String[] templateArray = StringUtils.split(preferenceValue, ",");
			for (int i = 0; (i + 1) < templateArray.length; i++)
				templateMap.put(templateArray[i], templateArray[++i]);
			tableViewer.setInput(templateMap.keySet());
		}	
	}
	
	public String getPreferenceValue()
	{
		StringBuilder prefValue = new StringBuilder();
		for(String key : templateMap.keySet())
		{
			String value = templateMap.get(key);			
			if(StringUtils.isEmpty(prefValue))			
				prefValue.append(key+","+value);
			else
				prefValue.append(","+key+","+value);
		}
		
		return prefValue.toString();
	}
	
	private void updateWidgets()
	{
		StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
		String key = (String) selection.getFirstElement();
		btnEdit.setEnabled(true);
		btnDelete.setEnabled(true);
		if(defaultMap.containsKey(key) || (key == null))
		{
			btnEdit.setEnabled(false);
			btnDelete.setEnabled(false);
		}
	}

}
