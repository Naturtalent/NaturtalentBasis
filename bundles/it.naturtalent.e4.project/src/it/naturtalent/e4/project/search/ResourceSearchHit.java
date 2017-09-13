package it.naturtalent.e4.project.search;

import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.search.textcomponents.ITextComponent;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;


public class ResourceSearchHit extends SearchHit
{
	public IResource iResource;
	
	public ResourceSearchHit(IResource iResource, ITextComponent textComponent,
			HitRange hitRange)
	{
		super(iResource.getName(), textComponent, hitRange);
		this.iResource = iResource; 
		
		if(iResource.getType() == IResource.PROJECT)	
			label = new NtProject((IProject)iResource).getName();
	}
}
