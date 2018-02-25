package it.naturtalent.e4.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Utilities in Verbindung mit dem neuen ProjectProperty Mechanismus auf Basis der EMF-Model Komponenten.
 * 
 * Im Datenbereich des NtProjekts (.projectdata) wird in der Datei (propertyData.xml) die PropertyFactory-Klassenamen
 * der dem NtProjekt zugeordneten Eigenschaft gespeichert. 
 * Die Eigenschaft 'it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory' ist obligatorisch jedem NtProjekt
 * zugeordnet und beinhaltet die grundlegenden Daten (Name, Description,...)
 * 
 * Diese Utilities unterstuetzen das Handling.
 * 
 * @author dieter
 *
 */
public class NtProjektPropertyUtils
{
	//private static String obligateProjectFactoryName = "it.naturtalent.e4.project.ui.NtProjectPropertyFactory";
	
	/**
	 * Speichert eine Liste mit PropertyFatories im Datenbereich des Projekts 'projectID'.
	 * Die Liste beinhaltet alle PropertyFactories die dem Projekt mit der ID 'projectID' 
	 * zugeordnet sind.
	 * @param projectID
	 * @param propertyFactories
	 */
	public static void saveProjectPropertyFactories(String projectID, List<INtProjectPropertyFactory>propertyFactories)
	{
		String [] settingPropertyFactoryNames = null;
		for (INtProjectPropertyFactory propertyFactory : propertyFactories)
		{
			settingPropertyFactoryNames = ArrayUtils.add(settingPropertyFactoryNames,
					propertyFactory.getClass().getName());
		}
		saveProjectPropertyFactories(projectID, settingPropertyFactoryNames);
	}
	
	/**
	 * Speichert ein Array mit PropertyFatoryNames im Datenbereich des Projekts 'projectID'.
	 * Das Array beinhaltet alle PropertyFactory Namen die dem Projekt mit der ID 'projectID'
	 * zugeordnet sind. 
	 * 
	 * @param projectID
	 * @param settingPropertyFactoryNames
	 */
	public static void saveProjectPropertyFactories(String projectID, String [] settingPropertyFactoryNames)
	{
		/*
		if(settingPropertyFactoryNames == null)
			settingPropertyFactoryNames = new String [] {obligateProjectFactoryName};
			*/
					
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectID);
		if(iProject != null)
		{		
			/*
			if(!ArrayUtils.contains(settingPropertyFactoryNames, obligateProjectFactoryName))
				settingPropertyFactoryNames = ArrayUtils.add(settingPropertyFactoryNames, obligateProjectFactoryName);
				*/		
			
			ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
			ProjectPropertyData projectPropertyData = new ProjectPropertyData();
			projectPropertyData.setPropertyFactories(settingPropertyFactoryNames);
			projectPropertySettings.put(iProject, projectPropertyData);
		}
	}
	
	/**
	 * Die ProjectPropertyInformationen (PropertyFactories) werden in einem Datenmodell 'ProjectPropertyData'
	 * gespeichert das selbst wiederum Grundlage fuer das Speichern via ProjectPropertySettings 
	 * im Property-File des Projekts dient.
	 * 
	 * @param iProject
	 * @param propertyFactories
	 */
	public static void setProjectPropertyFactories(IProject iProject,
			List<INtProjectPropertyFactory> propertyFactories) 
	{
		// Namen der dem Projekt zugeordneten PropertyFactories
		String [] settingPropertyFactoryNames = null;
		
		for(INtProjectPropertyFactory propertyFactory : propertyFactories)
		{
			// alle FactoryNamen in einem Array zusammenfassen
			settingPropertyFactoryNames = ArrayUtils.add(settingPropertyFactoryNames,propertyFactory.getClass().getName());
		}
		
		// Die Namen in eine 'ProjectPropertyData' Klasse uebernehmen und die
		if(ArrayUtils.isNotEmpty(settingPropertyFactoryNames))
		{
			ProjectPropertyData projectPropertyData = new ProjectPropertyData();
			projectPropertyData.setPropertyFactories(settingPropertyFactoryNames);
			ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
			projectPropertySettings.put(iProject, projectPropertyData);
		}		
	}
	
	/**
	 * Alle dem Project zugeordnete PropertyFactories ermitteln und zurueckgeben.
	 * Ausgehend von den im Projekt gespeicherten FactoryNamen wird die entsprechende
	 * Factoryklasse aus dem Repository geladen und dann in der Rueckgabeliste speichern.
	 * 
	 * @param ntProjektDataFactoryRepository
	 * @param iProject
	 * @return
	 */
	public static List<INtProjectPropertyFactory> getProjectPropertyFactories(INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository, IProject iProject)
	{		
		if ((ntProjektDataFactoryRepository != null) && (iProject != null))
		{
			// alle insgesamt verfuegbaren PropertyFactories auflisten
			List<INtProjectPropertyFactory> allFactories = ntProjektDataFactoryRepository.getAllProjektDataFactories();
			if((allFactories != null) && (!allFactories.isEmpty()))
			{
				// alle dem Project zugeordneten PropertyFactoryNamen aus dem Datenbereich des Projekts lesen
				String [] settingFactories = null;
				ProjectPropertyData propertyData = new ProjectPropertySettings().get(iProject);
				if(propertyData != null)
					settingFactories = propertyData.getPropertyFactories();
				
				if(ArrayUtils.isNotEmpty(settingFactories))			
				{					
					// Rueckgabeliste vorbereiten
					List<INtProjectPropertyFactory> projektPropertyFactories = new ArrayList<INtProjectPropertyFactory>();
					
					for(String factoryName : settingFactories)
					{
						for(INtProjectPropertyFactory projectFactory : allFactories)
						{							
							if(StringUtils.equals(projectFactory.getClass().getName(),factoryName))
							{
								projektPropertyFactories.add(projectFactory);
								break;
							}
						}
					}
					
					return projektPropertyFactories;
				}
			}
		}

		return null;
	}
	
	/**
	 * Alle dem Project zugeordnete Properties ermitteln und zurueckgeben.
	 * 
	 * @param ntProjektDataFactoryRepository
	 * @param iProject
	 * @return
	 */
	public static List<INtProjectProperty> getProjectProperties(INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository, IProject iProject)
	{	
		List<INtProjectProperty>projectProperties = new ArrayList<INtProjectProperty>();
		List<INtProjectPropertyFactory>propertyFactories = getProjectPropertyFactories(ntProjektDataFactoryRepository, iProject);
		if((propertyFactories != null) && (!propertyFactories.isEmpty()))
		{			
			if(!propertyFactories.isEmpty())
			{
				for(INtProjectPropertyFactory propertyFactory : propertyFactories)
				{
					// ProjectProperty ueber die Factory - 'create' - Funktion erzeugen 
					INtProjectProperty projectProperty = propertyFactory.createNtProjektData();
					projectProperty.setNtProjectID(iProject.getName());
					projectProperties.add(projectProperty);				
				}
			}	
		}
		
		return projectProperties;
	}
	

	
}
