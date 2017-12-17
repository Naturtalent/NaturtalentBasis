package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.expimp.ecp.ECPExportHandlerHelper;

/**
 * Exportiert die NtProjekteigenschaften
 *
 * @author dieter
 *
 */
public class ExportProjectPropertyOperation implements IRunnableWithProgress
{
	private final static String EXPORTOBJECTOPERATION_TITLE = "Export Eigenschaften"; 
	private int totalWork = IProgressMonitor.UNKNOWN;	
	
	// Map mit Key(FactoryClassName) Value(Liste mit den IProjectIDs)
	private Map<String,List<String>>mapProjectFactories = new HashMap<String, List<String>>();	
	
	// Repository mit allen verfuegbaren PropertyFactories
	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;
		
	private List<EObject>eObjectExportListe = new ArrayList<EObject>();
	
	private File exportDestDir;
	
	// Name des ECPProjects
	private String ecpProjectName = null;
	
	private Shell shell;
	
	
	/**
	 * Konstruktion
	 * 
	 * @param exportDestDir - in diesem Verzeichnis werden die Daten gespeichert
	 * @param projektDataFactoryRepository - Repository mit allen verfuegbaren Factories (Klassennamen der Factories)
	 * @param mapProjectFactories - die zuexportierenden Eigenschaften
	 */
	public ExportProjectPropertyOperation(Shell shell, File exportDestDir,
			INtProjectPropertyFactoryRepository projektDataFactoryRepository,
			Map<String, List<String>> mapProjectFactories)
	{	
		this.shell = shell;
		this.exportDestDir = exportDestDir;
		this.projektDataFactoryRepository = projektDataFactoryRepository;
		this.mapProjectFactories = mapProjectFactories;
	}



	@Override
	public void run(IProgressMonitor monitor)throws InvocationTargetException, InterruptedException
	{
		if((mapProjectFactories != null) && (!mapProjectFactories.isEmpty()))
		{
			totalWork = mapProjectFactories.size(); 
			monitor.beginTask(EXPORTOBJECTOPERATION_TITLE,totalWork);
			
			Set<String>propertyFatoryNames = mapProjectFactories.keySet();
			for(String propertyFatoryName : propertyFatoryNames)
			{
				INtProjectProperty ntProjectProperty = getProjectPropertyFactory(propertyFatoryName);
				if(ntProjectProperty != null)
				{
					List<EObject>exportListe = new ArrayList<EObject>();
					if(ecpProjectName != null)
					{
						// alle Propertydaten laden und in einer Liste sammeln 
						List<String>projectIDs = mapProjectFactories.get(propertyFatoryName);
						for(String projectID : projectIDs)
						{
							// Propertydaten ueber den Adapter laden 
							ntProjectProperty.setNtProjectID(projectID);
							Object obj = ntProjectProperty.getNtPropertyData();
							if (obj instanceof EObject)
							{
								EObject eObject = (EObject) obj;
								exportListe.add(eObject);
							}				
						}
						
						// Dateiname des Properties generieren und daten exportieren
						File exportFile = new File(exportDestDir,ecpProjectName+".xmi");
						ECPExportHandlerHelper.export(shell, exportListe, exportFile.getPath());			
					}
				}
			}
			monitor.worked(1);			
		}		
		monitor.done();		
	}
	
	/*
	 * Ueber den Namen 'factoryName' wird das entsprechende Factory aus dem Repository entnommen und der
	 * PropertyDataAdapter erzeugt und Zurueckgegeben. Gleichzeitig wird der ECPProjectname, der ebenfalls
	 * im Factory gespeichert ist modulglobal verfuegbar gemacht.
	 * 
	 */
	private INtProjectProperty getProjectPropertyFactory(String factoryName)
	{
		INtProjectProperty property = null; 
		
		List<INtProjectPropertyFactory>projectFactories = projektDataFactoryRepository.getAllProjektDataFactories();
		for(INtProjectPropertyFactory projectFactory : projectFactories)
		{
			if(StringUtils.equals(projectFactory.getClass().getName(),factoryName))
			{
				// ECPProjectname fuer spaetere Verwendung aus dem Factory laden
				ecpProjectName = projectFactory.getParentContainerName();
				
				// der eigentliche Adaprter durch das Factory erzeugen
				property = projectFactory.createNtProjektData();
				break;				
			}
		}

		return property;
	}

}
