package it.naturtalent.e4.project.ui.model;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.WorkbenchImages;
import it.naturtalent.e4.project.ui.registry.OverlayIcon;

/**
 * An IWorkbenchAdapter that represents IProject.
 */
public class WorkbenchProject extends WorkbenchResource implements
		IProjectActionFilter
{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	HashMap<String, ImageDescriptor> imageCache = new HashMap(11);

	/**
	 * Answer the appropriate base image to use for the passed resource,
	 * optionally considering the passed open status as well iff appropriate for
	 * the type of passed resource
	 */	
	@Override
	protected ImageDescriptor getBaseImage(IResource resource)
	{
		IProject project = (IProject) resource;
		boolean isOpen = project.isOpen();
		
		 String baseKey = isOpen ? WorkbenchImages.IMG_PROJECT_OPEN
	                : WorkbenchImages.IMG_PROJECT_CLOSED;
		
		if(isOpen)
		{
			try
			{
				String[] natureIds = project.getDescription().getNatureIds();
				
				  for (int i = 0; i < natureIds.length; ++i)
				  {
					  // Have to use a cache because OverlayIcon does not define its own equality criteria,
	                    // so WorkbenchLabelProvider would always create a new image otherwise.
					  String imageKey = natureIds[i];

					ImageDescriptor overlayImage = (ImageDescriptor) imageCache
							.get(imageKey);
					if (overlayImage != null)
					{
						return overlayImage;
					}
					  
					  ImageDescriptor natureImage = Activator.getProjectImageRegistry()
	                            .getNatureImage(natureIds[i]);
					  
					if (natureImage != null)
					{
						ImageDescriptor baseImage = WorkbenchImages.getImage(baseKey);
						
						overlayImage = new OverlayIcon(baseImage,
								new ImageDescriptor[][]
									{
										{ natureImage } }, new Point(16, 16));
						imageCache.put(imageKey, overlayImage);
						return overlayImage;
					}
				}
			} catch (CoreException e)
			{
				
				e.printStackTrace();
			}
		}
		
		
		return WorkbenchImages.getImage(WorkbenchImages.IMG_PROJECT_OPEN);
	}

	@Override
	public String getLabel(Object o)
	{
		IProject project = (IProject) o;
		
		try
		{
			String name = project.getPersistentProperty(INtProject.projectNameQualifiedName);
			if (StringUtils.isNotEmpty(name))
				return name;
			
			// Projektname wird nicht mehr redundant in 'IProjectData.PROJECTDATAFILE' gespeichert
			log.info("Projekt:"+project.getName()+" -Projektname fehlerhaft in PersistentProperty gespeichert");
						
			/*
			else
			{
				// Projektname wurde nicht korrekt in PersistentProperty gespeichert
				
				try
				{
					//log.info("Projektname fehlerhaft in PersistentProperty gespeichert, Versuch der Korrektur");
					System.err.println("Projektname fehlerhaft in PersistentProperty gespeichert, Versuch der Korrektur");
					
					// Name aus dem Projektdatefile lesen
					StringBuilder pathBuilder = new StringBuilder(project
							.getLocation().toOSString());
					pathBuilder.append(File.separator
							+ IProjectData.PROJECTDATA_FOLDER);
					pathBuilder.append(File.separator
							+ IProjectData.PROJECTDATAFILE);
					File projectDataFile = new File(pathBuilder.toString());

					FileInputStream in = new FileInputStream(projectDataFile);
					ProjectData projectData = JAXB.unmarshal(in,
							ProjectData.class);
					name = projectData.getName();
					if (StringUtils.isNotEmpty(name))
					{
						// Projektname erneut in PersistentProperty speichern
						project.setPersistentProperty(
								INtProject.projectNameQualifiedName, name);
						return name;
					}

				} catch (Exception e)
				{					
					e.printStackTrace();
				}
			}
			*/
			
			
		} catch (CoreException e)
		{
		}
				
		return super.getLabel(o);
	}



	/**
	 * Returns the children of this container.
	 */
	public Object[] getChildren(Object o)
	{
		IProject project = (IProject) o;
		if (project.isOpen())
		{
			try
			{
				return project.members();
			} catch (CoreException e)
			{
				// don't get the children if there are problems with the project
			}
		}
		return NO_CHILDREN;
	}

	/**
	 * Returns whether the specific attribute matches the state of the target
	 * object.
	 * 
	 * @param target
	 *            the target object
	 * @param name
	 *            the attribute name
	 * @param value
	 *            the attriute value
	 * @return <code>true</code> if the attribute matches; <code>false</code>
	 *         otherwise
	 */
	public boolean testAttribute(Object target, String name, String value)
	{
		if (!(target instanceof IProject))
		{
			return false;
		}
		IProject proj = (IProject) target;
		if (name.equals(NATURE))
		{
			try
			{
				return proj.isAccessible() && proj.hasNature(value);
			} catch (CoreException e)
			{
				return false;
			}
		}
		else if (name.equals(OPEN))
		{
			value = value.toLowerCase();
			return (proj.isOpen() == value.equals("true"));//$NON-NLS-1$
		}
		return super.testAttribute(target, name, value);
	}
}
