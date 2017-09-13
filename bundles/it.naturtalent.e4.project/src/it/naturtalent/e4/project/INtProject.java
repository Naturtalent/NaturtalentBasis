package it.naturtalent.e4.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;

public interface INtProject
{
	// Qualifier
	public static final String QUALIFIER = "it.naturtalent.projekt";

	// der Projektname wird persistent im WorkspaceProject gespeichert
	public static final QualifiedName projectNameQualifiedName = new QualifiedName(
			QUALIFIER, "projectName");
	
	// die ProjektID werden redundant im WorkspaceProject gespeichert
	public static final QualifiedName projectIdQualifiedName = new QualifiedName(
			QUALIFIER, "projectID");

	public String getId();
	
	public String getName();
	
	public void setName(String name);
	
	public boolean isOpen();
	
	public IResource [] getResources();
	
	public IProject getIProject();

}
