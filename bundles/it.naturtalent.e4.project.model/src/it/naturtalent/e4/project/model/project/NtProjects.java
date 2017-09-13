/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Nt Projects</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.NtProjects#getNtProject <em>Nt Project</em>}</li>
 * </ul>
 *
 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProjects()
 * @model
 * @generated
 */
public interface NtProjects extends EObject
{
	/**
	 * Returns the value of the '<em><b>Nt Project</b></em>' containment reference list.
	 * The list contents are of type {@link it.naturtalent.e4.project.model.project.NtProject}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nt Project</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nt Project</em>' containment reference list.
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProjects_NtProject()
	 * @model containment="true"
	 * @generated
	 */
	EList<NtProject> getNtProject();

} // NtProjects
