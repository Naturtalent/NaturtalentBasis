package it.naturtalent.e4.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class DateFilterOptions
{
	// Definierte Filterbezug
	public enum BaseFilterEnum
	{
		NO_FILTER,		// kein Filter definiert
		CREATE_BASED,	// der Filter bezieht sich auf das Erstellungsdatum der Resource
		MODIFY_BASED;	// der Filter bezieht sich auf die letzte Aenderung der Resource		
	}

	// Definierte Datumsfilter
	public enum DateFilterEnum
	{
		DATE,			// alle Resourcen, die mit diesem Datum ueberinstimmen
		DATE_BEFORE,	// alle Resourcen, die vor diesem Datum liegen (inklusiv)
		DATE_SINCE,		// alle Resourcen, die nach diesem Datum liegen (inklusiv)
		DATE_BETWEEN;	// alle Resourcen, die zwischen diesen Zeitpunkten liegen
	}
	
	// die im Composite ausgewaehlte Filterbasis
	private BaseFilterEnum filterBase;
	
	// der  im Composite ausgewaehlte Filtertyp
	private DateFilterEnum filterType;
	
	// Tabelle speichert die im Composite getaetigten Datumseingaben
	private Map<DateFilterEnum,Date[]>filterMap = new HashMap<DateFilterOptions.DateFilterEnum, Date[]>();
	
	// Filtertyp und Date in Tabelle eintragen
	public void setFilterMap(DateFilterEnum filterType, Date...date)
	{
		filterMap.put(filterType, date);		
	}
		
	public BaseFilterEnum getFilterBase()
	{
		return filterBase;
	}
	public void setFilterBase(BaseFilterEnum filterBase)
	{
		this.filterBase = filterBase;
	}
	public DateFilterEnum getFilterType()
	{
		return filterType;
	}
	public void setFilterType(DateFilterEnum filterType)
	{
		this.filterType = filterType;
	}
	
	public List<IAdaptable> filterResources(List<IAdaptable>adaptables)
	{
		if(filterBase.equals(BaseFilterEnum.NO_FILTER))
			return null;

		// Liste aller gefilterten Resourcen (Ergebnisliste)
		List<IAdaptable>filteredResources = new ArrayList<IAdaptable>(); 

		// der aktuell eingestellte Filterzeitpunkt im Calendarformat
		Calendar filterCal0 = Calendar.getInstance();
		Date [] date = filterMap.get(filterType);
		if(date[0] == null)
		{
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"Datumsfilter", "Datumsangabe unklar");// $NON-NLS-N$
			return null;
		}
				
		filterCal0.setTimeInMillis(filterMap.get(filterType)[0].getTime());
		
		// der Typ - 'Between' benoetigt einen zweiten Zeitpunkt
		Calendar filterCal1 = null; 
		if(date.length == 2)
		{			
			// den zweiten Zeitpunkt ermitteln 
			filterCal1 = Calendar.getInstance();
			date = filterMap.get(filterType);
			if(date[1] == null)
			{
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"Datumsfilter", "Datumsangabe unklar");// $NON-NLS-N$
				return null;
			}
			filterCal1.setTimeInMillis(filterMap.get(filterType)[1].getTime());
			
			if(filterCal0.after(filterCal1))
			{
				long change = filterCal0.getTimeInMillis();
				filterCal0.setTimeInMillis(filterCal1.getTimeInMillis());
				filterCal1.setTimeInMillis(change);
			}
			
		}
		
		// Datumsfilter auf jede Resource anwenden 
		for(IAdaptable adabtable : adaptables)
		{			
			boolean filterd = filterResource((IResource) adabtable, filterCal0, filterCal1);
			if(filterd)
				filteredResources.add(adabtable);			
		}

		return filteredResources;
	}
	
	/*
	 * Eine einzelne Resource auf Datum (Erstellung oder Letzte Aenderung) pruefen.
	 * 'true' wird zurueckgegeben, wenn die Resource in der Zeitvorgabe liegt.   
	 */

	private boolean filterResource(IResource resource, Calendar filterCal1, Calendar filterCal2)
	{
		long resourceDate = getResourceDate(resource);
		
		if(resourceDate > 0)
		{
			Calendar resourceCal = Calendar.getInstance();
			resourceCal.setTimeInMillis(resourceDate);
			
			switch (filterType)
				{
					case DATE:
						return isCalendarEquals(resourceCal, filterCal1);
						
					case DATE_SINCE:						
						return (resourceCal.after(filterCal1) || isCalendarEquals(resourceCal, filterCal1));
						
					case DATE_BEFORE:
						return (filterCal1.after(resourceCal) || isCalendarEquals(resourceCal, filterCal1));
						
					case DATE_BETWEEN:
						return ((resourceCal.after(filterCal1) && (resourceCal.before(filterCal2))) || 
								isCalendarEquals(resourceCal, filterCal1) ||
								isCalendarEquals(resourceCal, filterCal2));
								
					default:
						break;
				}
		}
		
		return false;
	}

	/*
	 * Hilfsfunktion prueft auf Gleichzeitigkeit zweier Zeitpunkte
	 */
	private boolean isCalendarEquals(Calendar cal1, Calendar cal2)
	{
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));

	}
	
	private void printCalendars(Calendar cal1, Calendar cal2)
	{
		SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");//$NON-NLS-N$
		String s1 = (format1.format(cal1.getTime()));
		String s2 = (format1.format(cal2.getTime()));
		System.out.println(s1+"  :  "+s2);
	}
	
	/*
	 *  Entsprechend den Optionen Erstellungs-,Aenderungsdatum der Resource ermitteln 
	 */
	private long getResourceDate(IResource resource)
	{
		long resourceDate = 0L;		
		
		// letzte Aenderung an der Resource checken
		if(filterBase == BaseFilterEnum.MODIFY_BASED)
		{
			File file =  new File(resource.getLocation().toOSString());
			if((resource.getType() == IResource.PROJECT) || (resource.getType() == IResource.FOLDER))
			{
				// bei Project und Folder wird die Leitzte Aenderung im Verzeichnis ermittelt 
				resourceDate = getLatestModifiedDate(file);
			}
			else
			{
				// bei Dateien kann die letzte Aenderung direkt abgefragt werden
				resourceDate = file.lastModified();
			}
		}
		else // Erstellungsdatum der Resource checken
		{
			if(resource.getType() == IResource.PROJECT)
			{
				// Erstellungsdatum des Projekts aus der ID extrahieren
				IProject iProject = (IProject) resource; 
				String stgDate = iProject.getName().substring(0, iProject.getName().indexOf('-'));			
				resourceDate = NumberUtils.createLong(stgDate).longValue();
			}
			else
			{
				try
				{
					// Erstellungsdatum File (in Linux identisch mit lastModify)
					File file = new File(resource.getLocation().toOSString());
					BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
					resourceDate = attributes.creationTime().to(TimeUnit.MILLISECONDS);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return resourceDate;
	}
	
	/*
	 * letzte Aenderung im Verzeichnis ermitteln
	 */
	private long getLatestModifiedDate(File dir)
	{
		File[] files = dir.listFiles();
		long latestDate = 0;
		for (File file : files)
		{
			long fileModifiedDate = file.isDirectory()
					? getLatestModifiedDate(file)
					: file.lastModified();
			if (fileModifiedDate > latestDate)
			{
				latestDate = fileModifiedDate;
			}
		}
		return Math.max(latestDate, dir.lastModified());
	}


}
