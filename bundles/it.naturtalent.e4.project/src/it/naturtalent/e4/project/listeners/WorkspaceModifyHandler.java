package it.naturtalent.e4.project.listeners;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;

public class WorkspaceModifyHandler implements IResourceChangeListener
{

	// Speicher der Resourceaenderungen	
	private static ArrayList<IResourceDelta>resourceDeltas = new ArrayList<IResourceDelta>();
	
	@Inject
	@Optional
	private static IEventBroker eventBroker;

	@Execute
	void init() 
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);		
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event)
	{
		IResourceDelta rootDelta;
		
		if (event.getType() != IResourceChangeEvent.POST_CHANGE)
			return;

		rootDelta = event.getDelta();
		if (rootDelta == null)
			return;

		// einen ResourceVisitor erzeugen
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor()
		{
			public boolean visit(IResourceDelta delta)
			{
				// nur interessiert an ADD oder REMOVE Änderungen
				int kind = delta.getKind();
				if ((kind == IResourceDelta.ADDED)
						|| (kind == IResourceDelta.REMOVED))
						//|| (kind == IResourceDelta.CHANGED))
				{
					IResource resource = delta.getResource();
					switch (resource.getType())
						{
							case IResource.FILE:
							case IResource.FOLDER:
							case IResource.PROJECT:
								resourceDeltas.add(delta);
								break;

							default:
								break;
						}
				}
				return true;
			}
		};
		try
		{
			// den ResourceVisitor 'visitor' aufrufen
			rootDelta.accept(visitor);
		} catch (CoreException e)
		{
			//logger.error(e);
		}
	}
	
	public static void postWorkspaceModifyEvent()
	{
		Map<String, Object> map;
		
		map = new HashMap<String, Object>();
		for(IResourceDelta resourceDelta : resourceDeltas)
		{
			int kind = resourceDelta.getKind();

			if (kind == IResourceDelta.ADDED)
				map.put(WorkspaceEventConstants.WORKSPACEMODIFY_NEW_RESOURCES,
						resourceDelta.getResource());
			else
			{
				if (kind == IResourceDelta.REMOVED)
					map.put(WorkspaceEventConstants.WORKSPACEMODIFY_DELETE_RESOURCES,
							resourceDelta.getResource());
			}
		}
		
		if(!map.isEmpty())
			eventBroker.post(WorkspaceEventConstants.WORKSPACEMODIFY, map);
		
		resourceDeltas.clear();
	}

}
