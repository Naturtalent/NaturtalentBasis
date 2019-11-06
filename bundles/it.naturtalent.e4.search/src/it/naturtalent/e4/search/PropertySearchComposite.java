package it.naturtalent.e4.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.internal.dtree.DataDeltaNode;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;

public class PropertySearchComposite extends Composite
{
	// Liste der zugeordneten WorkingSets	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();

	/*
	 * 
	 * Struktur SearchPattern (per Databinding verbunden mit Widget textSearchMask
	 * 
	 */	 
	private class SearchPattern
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
	private Group groupDateGroup;
	private DateTime date;
	private Button btnRadiodate;
	private Button btnRadioSince;
	private DateTime dateSince;
	private Button btnRadioBefore;
	private DateTime dateBefore;
	private Button btnRadioBetween;
	private DateTime dateUp;
	private DateTime dateTo;
	
	public enum DateEnum
	{
		DATE,
		DATE_BEFORE,
		DATE_AFTER,
		DATE_BETWEEN
	}
	
	/*
	 * 
	 * In dieser Struktur wird selektierte (RadioButton) Definition gespeichert
	 * 
	 */	 
	private class DateDefinition
	{
		private DateEnum dateEnum;
		Calendar from;
		Calendar to;
	}
	private DateDefinition dateDefinition;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PropertySearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Label lblSearchMask = new Label(this, SWT.NONE);
		lblSearchMask.setText(Messages.SearchDialog_lblNewLabel_text);
		
		textSearchMask = new Text(this, SWT.BORDER);
		GridData gd_textSearchMask = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textSearchMask.widthHint = 140;
		textSearchMask.setLayoutData(gd_textSearchMask);
		new Label(this, SWT.NONE);
		//new Label(this, SWT.NONE);
		
		groupDateGroup = new Group(this, SWT.NONE);
		groupDateGroup.setText(Messages.PropertySearchComposite_group_text);
		groupDateGroup.setLayout(new GridLayout(3, false));
		
		// Date
		btnRadiodate = new Button(groupDateGroup, SWT.RADIO);
		btnRadiodate.setText(Messages.PropertySearchComposite_btnRadioDate_text);
		date = new DateTime(groupDateGroup, SWT.BORDER);
		new Label(groupDateGroup, SWT.NONE);
		btnRadiodate.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateDateDefoinition(DateEnum.DATE, date, null);
			}
		});
		
		// Date since
		btnRadioSince = new Button(groupDateGroup, SWT.RADIO);
		btnRadioSince.setText(Messages.PropertySearchComposite_btnRadioSince_text);		
		dateSince = new DateTime(groupDateGroup, SWT.BORDER);
		new Label(groupDateGroup, SWT.NONE);
		btnRadioSince.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateDateDefoinition(DateEnum.DATE_AFTER, dateSince, null);
			}
		});
				
		// Date before
		btnRadioBefore = new Button(groupDateGroup, SWT.RADIO);
		btnRadioBefore.setSelection(true);
		btnRadioBefore.setText(Messages.PropertySearchComposite_btnRadioBefore_text);		
		dateBefore = new DateTime(groupDateGroup, SWT.BORDER);
		new Label(groupDateGroup, SWT.NONE);
		btnRadioBefore.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateDateDefoinition(DateEnum.DATE_BEFORE, dateBefore, null);
			}
		});
				
		// Date between
		btnRadioBetween = new Button(groupDateGroup, SWT.RADIO);
		btnRadioBetween.setText(Messages.PropertySearchComposite_btnRadioBetween_text);
		dateUp = new DateTime(groupDateGroup, SWT.BORDER);
		dateTo = new DateTime(groupDateGroup, SWT.BORDER);
		btnRadioBetween.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateDateDefoinition(DateEnum.DATE_BEFORE, dateUp, dateTo);
			}
		});
		
		initDataBindings();	
		textSearchMask.setText("*");
		dateDefinition = new DateDefinition();
		dateDefinition.dateEnum = DateEnum.DATE_BEFORE; 
		dateDefinition.from = Calendar.getInstance();
		dateDefinition.to = Calendar.getInstance();

	}
	
	private void updateDateDefoinition(DateEnum dateEnum, DateTime fromDateTime, DateTime toDateTime)
	{
		switch (dateEnum)
			{
				case DATE_AFTER:
				case DATE_BEFORE:
				case DATE:
					dateDefinition.dateEnum = dateEnum;
					dateDefinition.from.set(Calendar.DAY_OF_MONTH, fromDateTime.getDay());
					dateDefinition.from.set(Calendar.MONTH, fromDateTime.getMonth());
					dateDefinition.from.set(Calendar.YEAR, fromDateTime.getYear());					
					break;
					
				case DATE_BETWEEN:

					dateDefinition.from.set(Calendar.DAY_OF_MONTH, fromDateTime.getDay());
					dateDefinition.from.set(Calendar.MONTH, fromDateTime.getMonth());
					dateDefinition.from.set(Calendar.YEAR, fromDateTime.getYear());
					
					dateDefinition.to.set(Calendar.DAY_OF_MONTH, toDateTime.getDay());
					dateDefinition.to.set(Calendar.MONTH, toDateTime.getMonth());
					dateDefinition.to.set(Calendar.YEAR, toDateTime.getYear());	
					
					break;

				default:
					break;
			}
		
	}
	
	/**
	 * Gibt die Property-Search-Optionen in einer eigenen Klasse zurueck.
	 * 
	 * @return
	 */
	public SearchOptions getPropertySearchOptions()
	{
		SearchOptions searchOptions = new SearchOptions();
		
		// die eingegebene Pattermaske
		searchOptions.setSearchPattern(searchPattern.getPattern());
		
		searchOptions.setCaseSensitive(false);
		searchOptions.setRegularExpression(false);
		
		IProject [] iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		List<IAdaptable>iProjectList = new ArrayList<IAdaptable>();		
		for(IProject iProject : iProjects)
		{
			if(dateFilter(iProject) != null)
				iProjectList.add(iProject);
		}
			
		
		searchOptions.setSearchItems(iProjectList);
		
		return searchOptions;
	}
	
	private IProject dateFilter(IProject iProject)
	{
		// Erstellungsdatum des Projects
		String projectID = iProject.getName();
		String stgDate = projectID.substring(0, projectID.indexOf('-'));		
		Date projDate = new Date(NumberUtils.createLong(stgDate));
		Calendar projCalendar = Calendar.getInstance(); 
		projCalendar.setTime(projDate);
		
		switch (dateDefinition.dateEnum)
			{
				case DATE:

					if(
							(projCalendar.get(Calendar.YEAR) == dateDefinition.from.get(Calendar.YEAR)) &&
							(projCalendar.get(Calendar.MONTH) == dateDefinition.from.get(Calendar.MONTH)) &&
							(projCalendar.get(Calendar.DAY_OF_MONTH) == dateDefinition.from.get(Calendar.DAY_OF_MONTH))
						)
					return iProject;

				case DATE_AFTER:

					if(
							(projCalendar.get(Calendar.YEAR) >= dateDefinition.from.get(Calendar.YEAR)) &&
							(projCalendar.get(Calendar.MONTH) >= dateDefinition.from.get(Calendar.MONTH)) &&
							(projCalendar.get(Calendar.DAY_OF_MONTH) >= dateDefinition.from.get(Calendar.DAY_OF_MONTH))
						)
					return iProject;

					

				default:
					break;
			}
		
		return null;
	}


	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	// Rueckgabe der eingegebenen Pattern
	public String getResultSearchPattern()
	{
		return searchPattern.pattern;
	}
	
	// verbindet Text 'textSearchMask' mit Struktur value 'pattern' der Struktur searchPattern
	protected DataBindingContext initDataBindings() 
	{
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextSearchMaskObserveWidget = WidgetProperties.text(SWT.Modify).observe(textSearchMask);
		IObservableValue patternSearchPatternObserveValue = PojoProperties.value("pattern").observe(searchPattern);
		bindingContext.bindValue(observeTextTextSearchMaskObserveWidget, patternSearchPatternObserveValue, null, null);
		//
		return bindingContext;
	}
}
