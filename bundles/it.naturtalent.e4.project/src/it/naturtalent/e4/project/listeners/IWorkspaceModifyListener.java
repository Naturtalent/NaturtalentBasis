/**
 * IViewListener.java
 */
package it.naturtalent.e4.project.listeners;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;


/**
 * 
 * 
 * @see ViewRestoreRegistration
 *  
 * 
 */
public interface IWorkspaceModifyListener
{
	/**
	 * Über diese Methode können alle registrierten Views aktualisiert werden.
	 * 
	 */
	public void updateDeltaResources(ArrayList<IResourceDelta> resourceDeltas);
	
	/**
	 * Eine bestimmte Ressource ueber diesen Listener verbreiten
	 * 
	 */
	public void propagateResource(IResource iResource);

	/**
	 * Gibt den registrierten Listenern eine Ressource vor, die ggf.
	 * nach dem Aktualisieren selektiert werden sollte 
	 * 
	 */
	public void selectDeltaResources(IResource selectIResource);
	
	/**
	 * 
	 */
	public void selectProject(IProject iProject);

	/**
	 * 
	 */
	public void modifiedProject(IProject iProject);

}
