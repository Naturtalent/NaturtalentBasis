package it.naturtalent.e4.project.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;

public class WorkbenchImages
{

	//public final static String IMG_PROJECT = "IMG_PROJECT"; //$NON-NLS-1$
	
	/**
	 * Identifies a project image.
	 */
	//public final static String IMG_OBJ_PROJECT = "IMG_OBJ_PROJECT"; //$NON-NLS-1$

	/**
	 * Identifies a closed project image.
	 */
	//public final static String IMG_OBJ_PROJECT_CLOSED = "IMG_OBJ_PROJECT_CLOSED"; //$NON-NLS-1$
	
	public final static String IMG_PROJECT_OPEN = "project_open.gif"; //$NON-NLS-1$
	public final static String IMG_PROJECT_CLOSED = "project_close.gif"; //$NON-NLS-1$
	public final static String IMG_PROJECT_FOLDER = "folder.png"; //$NON-NLS-1$
	public final static String IMG_PROJECT_FILE = "file_obj.gif"; //$NON-NLS-1$
	
	public final static String IMG_PROJECT_OVR = "native_co.gif"; //$NON-NLS-1$
	public final static String IMG_LINK_OVR = "ovr/link_ovr.gif"; //$NON-NLS-1$
	public final static String IMG_LINKWARN_OVR = "ovr/linkwarn_ovr.gif"; //$NON-NLS-1$
	
	public final static String IMG_WORKINGSETS = "workingsets.gif"; //$NON-NLS-1$
	
	
	
	public static ImageDescriptor getImage(String imageKey)
	{
		String iconURI = Activator.getPlatformURI()+Activator.ICONS_RESOURCE_FOLDER+"/"+imageKey;
		URI uri = URI.createURI(iconURI);
		try
		{
			return  ImageDescriptor.createFromURL(new URL(uri.toString()));
			
		} catch (MalformedURLException e)
		{
			System.err.println("iconURI \"" + uri.toString()
					+ "\" is invalid, no image will be shown");
		}

		return null;
	}




	public static ImageDescriptor getImageDescriptor(String imgObjWorkingSets)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
