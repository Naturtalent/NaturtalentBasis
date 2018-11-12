package it.naturtalent.e4.project.ui.emf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;


public class ExpImpUtils
{
	
	/**
	 * Die Projekteigenschaft wird aus dem Contenfile 'emfFileName' gelesen. Diese Datei muss sich im 
	 * Projektbereich des IProject (identifiziert durch 'projectID') befinden.
	 *   
	 * @param emfFileName
	 * @param projectID
	 * @return
	 */
	public static EObject importNtPropertyData(String emfFileName, String projectID)
	{
		if(StringUtils.isNotEmpty(emfFileName) && StringUtils.isNotEmpty(projectID))
		{
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectID);
			if(iProject.exists())
			{
				IFile iFile = iProject.getFile(emfFileName);
				File contentFile = iFile.getLocation().toFile();	
				
				// ist die Datei 'emfFileName' wirklich vorhanden
				if(contentFile.exists() && !contentFile.isDirectory())
				{				
					EList<EObject>contents = loadEObjectFromResource(contentFile);
					return contents.get(0);
				}
			}	
		}
		return null;
	}
	
	/**
	 * @param projectID
	 * @param ntPropertyData
	 * @param baseFileName
	 */
	public static void exportNtPropertyData(String projectID, EObject ntPropertyData, String baseFileName)
	{
		// sind Propertydaten vorhanden @see setNtProjectID(String ntProjectID)
		if(ntPropertyData != null)
		{			
			if(StringUtils.isNotEmpty(projectID))
			{
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectID);
				if(iProject.exists())
				{					
					IFile iFile = iProject.getFile(baseFileName);
					File file = iFile.getLocation().toFile();					
					String filePath = file.getPath();					
					List<EObject> eObjects = new ArrayList<EObject>();
					eObjects.add(ntPropertyData);
					URI resourceURI = URI.createFileURI(filePath);
										
					saveEObjectToResource(ntPropertyData, resourceURI);
				}
			}
		}	
	}
	
	/**
	 * Eine einzelnes EObject wird in der Resource 'resourceURI' gespeichert.
	 * !!! das Object wird vor dem Speichern dupliziert, ansonsten würde Originalobject geloescht
	 * 
	 * @param eObjects
	 * @param resourceURI
	 */

	public static void saveEObjectToResource(EObject eObject, URI resourceURI)
	{
		List<EObject> eObjects = new ArrayList<EObject>();
		EObject copyObject = EcoreUtil.copy(eObject);
		eObjects.add(copyObject);
		saveEObjectToResource(eObjects, resourceURI);
	}
	
	/**
	 * Eine Liste EObjects wird in der Resource 'resourceURI' gespeichert.
	 * !!! wenn verhindert werden soll, dass die Objekte geloescht werden, sollte die Liste Kopien enthalten
	 * 
	 * @param eObjects
	 * @param resourceURI
	 */

	public static void saveEObjectToResource(List<EObject> eObjects, URI resourceURI)
	{
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(resourceURI);
		final EList<EObject> contents = resource.getContents();
		
		for (final EObject eObject : eObjects)
		{
			contents.add(eObject);
		}
			
		contents.addAll(eObjects);
		try
		{
			resource.save(null);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Die in einer Datei gespeicherten Daten von EObjects importieren und in einer Liste zurueckgeben.
	 * 
	 * @param resourceFile
	 * @return
	 */
	public static EList<EObject>loadEObjectFromResource(File resourceFile)
	{
		URI resourceURI = URI.createFileURI(resourceFile.getPath());
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(resourceURI, true);
		return resource.getContents();
	}
	
}
