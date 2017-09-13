package it.naturtalent.e4.project.ui.navigator;

import it.naturtalent.e4.project.ui.WorkbenchImages;
import it.naturtalent.e4.project.ui.registry.OverlayIcon;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class WorkBenchLabelDecorator implements ILabelDecorator
{

	private static final ImageDescriptor LINK;	
	private static final ImageDescriptor LINK_WARNING;

	private ResourceManager resourceManager;
	
	static
	{
		LINK = WorkbenchImages.getImage(WorkbenchImages.IMG_LINK_OVR);
		LINK_WARNING = WorkbenchImages.getImage(WorkbenchImages.IMG_LINKWARN_OVR);
	}

	@Override
	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose()
	{
		if (resourceManager != null)
			resourceManager.dispose();
		resourceManager = null;		
	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Image decorateImage(Image image, Object element)
	{
		ImageDescriptor overlayDescriptor;
		
		if (element instanceof IResource == false)
		{
			return null;
		}
		
		IResource resource = (IResource) element;
		if (resource.isLinked() && !resource.isVirtual())
		{
			IFileInfo fileInfo = null;
			URI location = resource.getLocationURI();
			if (location != null)
			{
				fileInfo = getFileInfo(location);
			}
			if (fileInfo != null && fileInfo.exists())
			{							
				overlayDescriptor = new DecorationOverlayIcon(
						image, LINK, IDecoration.BOTTOM_LEFT);				
				return (Image) getResourceManager().get(overlayDescriptor);
			}
			else
			{
				overlayDescriptor = new DecorationOverlayIcon(
						image, LINK_WARNING, IDecoration.BOTTOM_LEFT);						
				return (Image) getResourceManager().get(overlayDescriptor);				
			}
		}
		
		return null;
	}
	
	/**
	 * Lazy load the resource manager
	 * 
	 * @return The resource manager, create one if necessary
	 */
	private ResourceManager getResourceManager()
	{
		if (resourceManager == null)
		{
			resourceManager = new LocalResourceManager(
					JFaceResources.getResources());
		}

		return resourceManager;
	}

	private IFileInfo getFileInfo(URI location)
	{
		if (location.getScheme() == null)
			return null;
		
		IFileStore store = null;
		try
		{
			store = EFS.getStore(location);
			if(store == null)
				return null;
				
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return store.fetchInfo();
	}
	
	@Override
	public String decorateText(String text, Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
