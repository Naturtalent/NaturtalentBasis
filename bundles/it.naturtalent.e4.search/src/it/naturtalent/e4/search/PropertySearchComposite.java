package it.naturtalent.e4.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
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
	
	private CDateTime dateTime;
	private Button btnRadiodate;
	private Button btnRadioSince;
	private CDateTime dateSince;
	private Button btnRadioBefore;
	private CDateTime dateBefore;
	private Button btnRadioBetween;
	private CDateTime dateUp;
	private CDateTime dateTo;
	private Label lblSearchMask;
	
	private Date defaultDate; 
	
	
	public enum DateEnum
	{
		DATE,
		DATE_BEFORE,
		DATE_AFTER,
		DATE_BETWEEN
	}
	private DateEnum selectedDateEnum;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PropertySearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0); 	
		defaultDate = today.getTime();
		
		lblSearchMask = new Label(this, SWT.NONE);
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
		dateTime = new CDateTime(groupDateGroup, SWT.BORDER);		
		dateTime.setSelection(defaultDate);				
		dateTime.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		new Label(groupDateGroup, SWT.NONE);
		btnRadiodate.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{		
				selectedDateEnum = DateEnum.DATE;
				//setDateDefinition(DateEnum.DATE, dateTime, null);
			}
		});
		
		// Date since
		btnRadioSince = new Button(groupDateGroup, SWT.RADIO);
		btnRadioSince.setText(Messages.PropertySearchComposite_btnRadioSince_text);		
		dateSince = new CDateTime(groupDateGroup, SWT.BORDER);
		dateSince.setSelection(defaultDate);
		dateSince.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		new Label(groupDateGroup, SWT.NONE);
		btnRadioSince.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectedDateEnum = DateEnum.DATE_AFTER;
				//setDateDefinition(DateEnum.DATE_AFTER, dateSince, null);
			}
		});
				
		// Date before (Defaultselection)
		btnRadioBefore = new Button(groupDateGroup, SWT.RADIO);		
		btnRadioBefore.setSelection(true);
		btnRadioBefore.setText(Messages.PropertySearchComposite_btnRadioBefore_text);		
		dateBefore = new CDateTime(groupDateGroup, SWT.BORDER);
		dateBefore.setSelection(defaultDate);
		dateBefore.setPattern(Messages.PropertySearchComposite_dateTime_pattern);		
		new Label(groupDateGroup, SWT.NONE);
		selectedDateEnum = DateEnum.DATE_BEFORE;
		
		btnRadioBefore.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectedDateEnum = DateEnum.DATE_BEFORE;
				//setDateDefinition(DateEnum.DATE_BEFORE, dateBefore, null);
			}
		});
				
		// Date between
		btnRadioBetween = new Button(groupDateGroup, SWT.RADIO);
		btnRadioBetween.setText(Messages.PropertySearchComposite_btnRadioBetween_text);
		dateUp = new CDateTime(groupDateGroup, SWT.BORDER);		
		dateUp.setSelection(defaultDate);
		dateUp.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		dateTo = new CDateTime(groupDateGroup, SWT.BORDER);
		dateTo.setSelection(defaultDate);
		dateTo.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		btnRadioBetween.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectedDateEnum = DateEnum.DATE_BETWEEN;
				//setDateDefinition(DateEnum.DATE_BETWEEN, dateUp, dateTo);
			}
		});
		
		initDataBindings();	
		textSearchMask.setText("*");
	}
	
	public void disposePatternEditor()
	{
		textSearchMask.dispose();	
		lblSearchMask.dispose();
	}

	/**
	 * Gibt die Property-Search-Optionen in einer eigenen Klasse zurueck.
	 * 
	 * Im Zuge diesen Funktion wird die Liste der uebergebenen Projekte, oder bei 'null' alle existierenden Projekte
	 * nach ihrem Erstellungsdatum gefiltert. Die gefilterte Menge wird in die Struktur SearchPattern eingetragen.
	 * 
	 * Die Filterfunktion wird ausgeschaltet wenn der RadioButton 'btnRadioBefore' und das heutige Datum im zugeordneten
	 * DateTime Widget das heutige Datum eingetragen ist.
	 * 
	 * @return
	 */
	public SearchOptions getPropertySearchOptions(List<IAdaptable>srcProjects)
	{
		IProject [] iProjects = null;
		
		Date fromDate = null;
		Date toDate = null;

		// Ergebnisliste mit den gefilterten Projekten vorbereiten
		List<IAdaptable>filteredProjectList = new ArrayList<IAdaptable>();

		// die ungefilterte Projekt-Menge in einem Array zur Verfuegung stellen
		if(srcProjects == null)
			iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		else
		{
			for(IAdaptable srcProject : srcProjects)
				iProjects = (IProject[]) ArrayUtils.add(iProjects, srcProject);
		}

		// SearchOptions-Struktur vorbereiten
		SearchOptions searchOptions = new SearchOptions();
				
		// die editierte Patternmaske eintragen
		searchOptions.setSearchPattern(searchPattern.getPattern());
		
		// sonstige SearchPption Defaultwerte
		searchOptions.setCaseSensitive(false);
		searchOptions.setRegularExpression(false);
		
		// eine leere Liste und Abbruch der Funkktion, wenn die Eingangsliste leer war
		if(srcProjects.isEmpty())
		{
			searchOptions.setSearchItems(filteredProjectList);		
			return searchOptions;
		}
	
		// Filterprozess vorbereiten (abhaengig vom selektierten RadioButton)
		switch (selectedDateEnum)
		{
			case DATE:
	
				fromDate = dateTime.getSelection();
				break;
	
			case DATE_AFTER:
	
				fromDate = dateSince.getSelection();
				break;
				
			case DATE_BEFORE:
			
				fromDate = dateBefore.getSelection();

				// aus dem Filterprozess aussteigen wenn das heutige Datum erkannt wird (filtern sinnlos)
				if(checkEqualsDate(fromDate, new Date()))
				{
					// alle Projekte in die filterProjektListe uebernehmen
					for(IProject iProject : iProjects)
					{
						if(iProject.exists())
							filteredProjectList.add(iProject);
					}

					searchOptions.setSearchItems(filteredProjectList);		
					return searchOptions;
				}
				break;
	
			case DATE_BETWEEN:
				
				fromDate = dateUp.getSelection();				
				toDate = dateTo.getSelection();
				
				break;
		}
		
		for(IProject iProject : iProjects)
		{
			if(iProject.exists())
			{
				if(dateFilter(iProject, fromDate, toDate))
					filteredProjectList.add(iProject);
			}
		}
			
		// List der gefilterten Projekte in SearchOption uebernehmen
		searchOptions.setSearchItems(filteredProjectList);		
		return searchOptions;
	}

	/*
	 * 
	 */
	private boolean dateFilter(IProject iProject, Date fromDate, Date toDate)
	{
		// Erstellungsdatum des Projects aus ID extrahieren
		String projectID = iProject.getName();
		String stgDate = projectID.substring(0, projectID.indexOf('-'));		
		Date projDate = new Date(NumberUtils.createLong(stgDate));
		
		//System.out.println("Projekt:"+projDate+"  |   "+ fromDate);
						
		switch (selectedDateEnum)
			{
				case DATE:
					
					if(checkEqualsDate(projDate, fromDate))
						return true;
					break;

				case DATE_AFTER:

					if(checkBeforeDate(projDate, fromDate))
						return true;
					break;
					
				case DATE_BEFORE:

					if(checkBeforeDate(fromDate, projDate))
						return true;
					break;

				case DATE_BETWEEN:

					if(checkBetweenDate(projDate, fromDate, toDate))
						return true;
			}
		
		return false;
	}
	
	// prueft auf gleiches Datum
	private boolean checkEqualsDate(Date projDate, Date checkDate)
	{
		Calendar projCal = Calendar.getInstance();
		projCal.setTimeInMillis(projDate.getTime());
		
		Calendar checkCal = Calendar.getInstance();
		checkCal.setTimeInMillis(checkDate.getTime());
		
		return (projCal.get(Calendar.YEAR) == checkCal.get(Calendar.YEAR)
				&& projCal.get(Calendar.DAY_OF_YEAR) == checkCal.get(Calendar.DAY_OF_YEAR));
	}
	
	// prueft Projektdatum aelter als Checkdatum
	private boolean checkBeforeDate(Date projDate, Date checkDate)
	{
		Calendar projCal = Calendar.getInstance();
		projCal.setTimeInMillis(projDate.getTime());
		
		Calendar checkCal = Calendar.getInstance();
		checkCal.setTimeInMillis(checkDate.getTime());
		
		return projCal.after(checkCal);
	}

	// prueft Projektdatum zwischen from und to
	private boolean checkBetweenDate(Date projDate, Date fromDate, Date toDate)
	{
		Calendar projCal = Calendar.getInstance();
		projCal.setTimeInMillis(projDate.getTime());
		
		Calendar fromCal = Calendar.getInstance();
		fromCal.setTimeInMillis(fromDate.getTime());

		Calendar toCal = Calendar.getInstance();
		toCal.setTimeInMillis(toDate.getTime());

		return ((projCal.after(fromCal) && (projCal.before(toCal))));
	}

	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
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
