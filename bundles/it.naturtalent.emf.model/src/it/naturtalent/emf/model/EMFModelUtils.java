package it.naturtalent.emf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProvider;
import org.eclipse.emf.ecp.core.exceptions.ECPProjectWithNameExistsException;
import org.eclipse.emf.ecp.core.util.ECPUtil;

public class EMFModelUtils
{

	private static Log log = LogFactory.getLog(EMFModelUtils.class);
	
	/**
	 * Ein neues Projekt erzeugen.
	 * 
	 * @param projectName
	 * @return
	 */
	public static ECPProject createProject(String projectName)
	{
		ECPProject project = null;
		
		final List<ECPProvider> providers = new ArrayList<ECPProvider>();
		
		for (final ECPProvider provider : ECPUtil.getECPProviderRegistry().getProviders())
		{
			if (provider.hasCreateProjectWithoutRepositorySupport())			
				providers.add(provider);			
		}
		
		if (providers.size() == 0)
		{
			log.error("kein Provider installiert"); //$NON-NLS-N$
			return null;
		}

		try
		{
			project = ECPUtil.getECPProjectManager()
					.createProject(providers.get(0), projectName, ECPUtil.createProperties());
		} catch (ECPProjectWithNameExistsException e)
		{
			log.error("es wurde kein Project erzeugt"); //$NON-NLS-N$
		}
		
		return project;
	}
			
			
			
	public static void unSet(EObject eObject)
	{
		if (eObject != null)
		{
			EList<EStructuralFeature> features = eObject.eClass()
					.getEStructuralFeatures();
			for (EStructuralFeature feature : features)
			{
				if (!feature.isMany())
					eObject.eUnset(feature);
			}
		}
	}
	
	/**
	 * @param eObject
	 * @return
	 */
	public static Map<String, Object> saveAttributes(EObject eObject)
	{		
		Map<String, Object>attributeMap = new HashMap<String, Object>();
	
		if (eObject != null)
		{
			EList<EAttribute> attributes = eObject.eClass().getEAllAttributes();
			for (EAttribute attribute : attributes)
			{
				String name = attribute.getName();
				Object value = eObject.eGet(attribute);
				attributeMap.put(name, value);
			}
		}
		
		return attributeMap;
	}
	
	/**
	 * @param eObject
	 * @param attributeMap
	 */
	public static void reloadAttributes(EObject eObject, Map<String, Object>attributeMap)
	{
		if (eObject != null)
		{
			EList<EAttribute> attributes = eObject.eClass().getEAllAttributes();
			for (EAttribute attribute : attributes)
			{
				String attributeName = attribute.getName();
				Object value = attributeMap.get(attributeName);
				eObject.eSet(attribute, value);
			}
		}
	}
	
	/**
	 * @param eObject
	 * @param attributeMap
	 */
	public static void printAttributes(EObject eObject, Map<String, Object>attributeMap)
	{
		EList<EAttribute>attributes  = eObject.eClass().getEAllAttributes();
		for(EAttribute attribute : attributes)
		{	
			String attributeName = attribute.getName();
			Object value = attributeMap.get(attributeName);			
			System.out.println("<EMFModelUtil> sAttributname: "+attributeName+"  Value: "+value);
		}

	}
}