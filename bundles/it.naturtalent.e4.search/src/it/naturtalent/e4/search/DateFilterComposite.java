package it.naturtalent.e4.search;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import it.naturtalent.e4.search.DateFilterOptions.BaseFilterEnum;
import it.naturtalent.e4.search.DateFilterOptions.DateFilterEnum;


/**
 * Composite zum Einblenden der Datum Filtereinstellungen.
 * 
 * @author dieter
 *
 */
public class DateFilterComposite extends Composite
{
	// sammelt alle Daten der Composite
	private DateFilterOptions filterOptions;
	
	private CDateTime dateTime;
	private Button btnRadiodate;
	private Button btnRadioSince;
	private CDateTime dateSince;
	private Button btnRadioBefore;
	private CDateTime dateBefore;
	private Button btnRadioBetween;
	private CDateTime dateUp;
	private CDateTime dateTo;
	private Group groupDateGroup;
	
	//private DateEnum selectedDateEnum;
	private Group groupModify;
	private Label lblUND;
	private Button btnNoFilter;
	
	// Setting BaseButton
	private final static String SETTING_BASE_KEY = "filterbasekey";
	private Map<String, Button>buttonBaseMap = new HashMap<String, Button>();
	
	// Setting TypeButton
	private final static String SETTING_TYPE_KEY = "filtertypekey";
	private Map<String, Button>buttonTypeMap = new HashMap<String, Button>();

	// Setting Date
	private final static String SETTING_DATE_KEY = "datekey";
	private final static String BETWEEN_TYPE_UP = "dateup";
	private final static String BETWEEN_TYPE_TO = "dateto";
	private Map<String, CDateTime>dateTypeMap = new HashMap<String, CDateTime>();

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DateFilterComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		filterOptions = new DateFilterOptions();
		filterOptions.setFilterBase(BaseFilterEnum.NO_FILTER);
		
		groupModify = new Group(this, SWT.NONE);
		groupModify.setLayout(new GridLayout(1, false));
		GridData gd_groupModify = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_groupModify.heightHint = 70;
		gd_groupModify.widthHint = 159;
		groupModify.setLayoutData(gd_groupModify);
		groupModify.setText(Messages.PropertySearchComposite_modifygroup_text);
		
		
		// 'No' Filter
		btnNoFilter = new Button(groupModify, SWT.RADIO);
		btnNoFilter.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Click auf RadioNoFilter setzt Base in Options
				filterOptions.setFilterBase(BaseFilterEnum.NO_FILTER);
			}
		});
		btnNoFilter.setSelection(true);
		btnNoFilter.setText(Messages.DateFilterComposite_btnRadioNoFilter_text);
		buttonBaseMap.put(BaseFilterEnum.NO_FILTER.toString(),btnNoFilter); 
		
		
		// 'Create_Based' Filter
		Button btnCreate = new Button(groupModify, SWT.RADIO);
		btnCreate.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Click auf RadioCreate setzt Base in Options
				filterOptions.setFilterBase(BaseFilterEnum.CREATE_BASED);
			}
		});
		btnCreate.setToolTipText(Messages.DateFilterComposite_btnCreate_toolTipText);
		btnCreate.setText(Messages.PropertySearchComposite_btnRadioCreate_text);
		buttonBaseMap.put(BaseFilterEnum.CREATE_BASED.toString(),btnCreate);
		
		// 'Modify_Based' Filter
		Button btnRadioButton = new Button(groupModify, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Click auf RadioModify setzt Base in Options
				filterOptions.setFilterBase(BaseFilterEnum.MODIFY_BASED);
			}
		});
		btnRadioButton.setToolTipText(Messages.DateFilterComposite_btnRadioButton_toolTipText);
		btnRadioButton.setText(Messages.PropertySearchComposite_btnRadioModify_text);
		buttonBaseMap.put(BaseFilterEnum.MODIFY_BASED.toString(),btnRadioButton);		
		
		// Gruppe mit den Zeitpunkten
		groupDateGroup = new Group(this, SWT.NONE);
		GridData gd_groupDateGroup = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_groupDateGroup.widthHint = 220;
		groupDateGroup.setLayoutData(gd_groupDateGroup);
		groupDateGroup.setText(Messages.PropertySearchComposite_group_text);
		groupDateGroup.setLayout(new GridLayout(2, false));
		
		// Date		
		btnRadiodate = new Button(groupDateGroup, SWT.RADIO);
		btnRadiodate.setText(Messages.PropertySearchComposite_btnRadioDate_text);	
		buttonTypeMap.put(DateFilterEnum.DATE.toString(),btnRadiodate);
		dateTime = new CDateTime(groupDateGroup, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dateTypeMap.put(DateFilterEnum.DATE.toString(),dateTime);
		dateTime.setSelection(new Date());		
		dateTime.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{		
				// Aenderung vom Zeitpunkt 'date' speichert diesen in Options
				filterOptions.setFilterMap(DateFilterEnum.DATE, dateTime.getSelection());
			}
		});				
		dateTime.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		btnRadiodate.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{		
				// Click auf RadioDate setzt den Typ und den Zeitpunkt in den Options
				filterOptions.setFilterType(DateFilterEnum.DATE);	
				filterOptions.setFilterMap(DateFilterEnum.DATE, dateTime.getSelection());
			}
		});
		
		// Date since
		btnRadioSince = new Button(groupDateGroup, SWT.RADIO);
		buttonTypeMap.put(DateFilterEnum.DATE_SINCE.toString(),btnRadioSince);
		btnRadioSince.setText(Messages.PropertySearchComposite_btnRadioSince_text);		
		dateSince = new CDateTime(groupDateGroup, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dateSince.setSelection(new Date());
		dateTypeMap.put(DateFilterEnum.DATE_SINCE.toString(),dateSince);
		dateSince.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		dateSince.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				// Aenderung vom Zeitpunkt 'since' speichert diesen in Options
				filterOptions.setFilterMap(DateFilterEnum.DATE_SINCE, dateSince.getSelection());
			}
		});				
		btnRadioSince.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Click auf RadioSince setzt den Typ und den Zeitpunkt in den Options
				filterOptions.setFilterType(DateFilterEnum.DATE_SINCE);	
				filterOptions.setFilterMap(DateFilterEnum.DATE_SINCE, dateSince.getSelection());
			}
		});
		
		// Date before (Defaultfilter)
		btnRadioBefore = new Button(groupDateGroup, SWT.RADIO);				
		btnRadioBefore.setText(Messages.PropertySearchComposite_btnRadioBefore_text);	
		buttonTypeMap.put(DateFilterEnum.DATE_BEFORE.toString(),btnRadioBefore);
		dateBefore = new CDateTime(groupDateGroup, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dateBefore.setSelection(new Date());
		dateTypeMap.put(DateFilterEnum.DATE_BEFORE.toString(),dateBefore);
		dateBefore.setPattern(Messages.PropertySearchComposite_dateTime_pattern);

		// wird standardmaessig selektiert
		btnRadioBefore.setSelection(true);
		filterOptions.setFilterType(DateFilterEnum.DATE_BEFORE);
		filterOptions.setFilterMap(DateFilterEnum.DATE_BEFORE, dateBefore.getSelection());
		
		dateBefore.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				// Aenderung vom Zeitpunkt 'before' speichert diesen in Options
				filterOptions.setFilterMap(DateFilterEnum.DATE_BEFORE, dateBefore.getSelection());
			}
		});				

		btnRadioBefore.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Click auf RadioBefore setzt den Typ und den Zeitpunkt in den Options
				filterOptions.setFilterType(DateFilterEnum.DATE_BEFORE);
				filterOptions.setFilterMap(DateFilterEnum.DATE_BEFORE, dateBefore.getSelection());
			}
		});
		
		// Date between
		btnRadioBetween = new Button(groupDateGroup, SWT.RADIO);
		btnRadioBetween.setText(Messages.PropertySearchComposite_btnRadioBetween_text);
		buttonTypeMap.put(DateFilterEnum.DATE_BETWEEN.toString(),btnRadioBetween);
		dateUp = new CDateTime(groupDateGroup, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);		
		dateUp.setSelection(new Date());
		dateTypeMap.put(BETWEEN_TYPE_UP,dateUp);
		dateUp.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		dateUp.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{	
				// Aenderung vom Zeitpunkt 'dateUp' speichert diesen und 'dateTo' in Options
				filterOptions.setFilterMap(DateFilterEnum.DATE_BETWEEN, dateUp.getSelection(),dateTo.getSelection());
			}
		});				

		lblUND = new Label(groupDateGroup, SWT.NONE);
		lblUND.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUND.setText(Messages.DateFilterComposite_lblNewLabel_text);
		dateTo = new CDateTime(groupDateGroup, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dateTo.setSelection(new Date());
		dateTypeMap.put(BETWEEN_TYPE_TO,dateTo);
		dateTo.setPattern(Messages.PropertySearchComposite_dateTime_pattern);
		dateTo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{				
				// Aenderung vom Zeitpunkt 'dateTo' speichert diesen und 'dateUp' in Options
				filterOptions.setFilterMap(DateFilterEnum.DATE_BETWEEN, dateUp.getSelection(),dateTo.getSelection());
			}
		});				

		btnRadioBetween.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Click auf RadioBetween setzt den Typ und die Zeitpunkte 'dateUp' und 'dateTo'  in den Options
				filterOptions.setFilterType(DateFilterEnum.DATE_BETWEEN);
				filterOptions.setFilterMap(DateFilterEnum.DATE_BETWEEN, dateUp.getSelection(),dateTo.getSelection());
			}
		});	
	}

	public DateFilterOptions getFilterOptions()
	{
		return filterOptions;
	}
	
	/*
	 * Widgets mit DialogSettings einstellen
	 */
	public void setDialogSettings(IDialogSettings settings)
	{
		// FilterBase Buttons
		String baseKey = settings.get(SETTING_BASE_KEY);
		Button settingButton = buttonBaseMap.get(baseKey);
		if(settingButton != null)
		{			
			// vorhandene Selektionen loeschen			
			for(Button baseButton : buttonBaseMap.values())
			{
				if(baseButton.equals(settingButton))
				{
					// Radio mit dem SettingKey selektieren und Base in Options setzen
					baseButton.setSelection(true);	
					filterOptions.setFilterBase(BaseFilterEnum.valueOf(baseKey));
				}
				else
					baseButton.setSelection(false);
			}
		}
		
		// FilterType Buttons
		String typeKey = settings.get(SETTING_TYPE_KEY);
		Button settingTypeButton = buttonTypeMap.get(typeKey);
		if(settingTypeButton != null)
		{
			// vorhandene Selektionen loeschen			
			for(Button typeButton : buttonTypeMap.values())
			{
				if(typeButton.equals(settingTypeButton))
				{
					// Radio mit dem SettingKey selektieren und Type in Options setzen
					typeButton.setSelection(true);
					filterOptions.setFilterType(DateFilterEnum.valueOf(typeKey));	
					filterOptions.setFilterMap(DateFilterEnum.valueOf(typeKey));
				}
				else
					typeButton.setSelection(false);
			}
			
			// Zeitpunkte uebernehmen
			Long dateTime;
			if(StringUtils.equals(typeKey, DateFilterEnum.DATE_BETWEEN.toString()))
			{
				// beim Typ 'Between' werden zwei Zeitpunkte uebernommen
				String [] date = settings.getArray(SETTING_DATE_KEY);
				if(ArrayUtils.isNotEmpty(date))
				{
					dateTime = Long.decode(date[0]);	
					dateTypeMap.get(BETWEEN_TYPE_UP).setSelection(new Date(dateTime.longValue()));
					dateTime = Long.decode(date[1]);	
					dateTypeMap.get(BETWEEN_TYPE_TO).setSelection(new Date(dateTime.longValue()));
				}
			}
			else
			{
				String date = settings.get(SETTING_DATE_KEY);
				if(StringUtils.isNotEmpty(date))
				{
					dateTime = Long.decode(date);	
					dateTypeMap.get(typeKey).setSelection(new Date(dateTime.longValue()));
				}
			}			
		}

	}

	/*
	 * DialogSettings speichern 
	 */
	public void saveDialogSettings(IDialogSettings settings)
	{	
		// FilterBase Buttons
		for(String key : buttonBaseMap.keySet())
		{
			if(buttonBaseMap.get(key).getSelection())
			{
				// ID des selekierten Base-RadioButtos speichern
				settings.put(SETTING_BASE_KEY, key);
				break;
			}
		}
		
		// TypeBase Buttons
		for(String key : buttonTypeMap.keySet())
		{
			Long dateTime;
			
			if(buttonTypeMap.get(key).getSelection())
			{
				// ID des selekierten Type-RadioButtos speichern
				settings.put(SETTING_TYPE_KEY, key);
				
				// Zeitpunkte speichern
				if(StringUtils.equals(key, DateFilterEnum.DATE_BETWEEN.toString()))
				{
					// als Array bei Between
					dateTime = dateTypeMap.get(BETWEEN_TYPE_UP).getSelection().getTime();
					String [] dates = ArrayUtils.add(null, Long.toString(dateTime));
					dateTime = dateTypeMap.get(BETWEEN_TYPE_TO).getSelection().getTime();
					dates = ArrayUtils.add(dates, Long.toString(dateTime));
					settings.put(SETTING_DATE_KEY, dates);
				}
				else
				{
					// als String bei allen anderen
					dateTime = dateTypeMap.get(key).getSelection().getTime();
					settings.put(SETTING_DATE_KEY, Long.toString(dateTime));
				}
				break;
			}
		}
	}

	

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
