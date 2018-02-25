/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Nt Property</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.NtProperty#getWorkingset <em>Workingset</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.NtProperty#getId <em>Id</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.NtProperty#getCreated <em>Created</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.NtProperty#getProperties <em>Properties</em>}</li>
 * </ul>
 *
 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProperty()
 * @model
 * @generated
 */
public interface NtProperty extends EObject
{
	/**
	 * Returns the value of the '<em><b>Workingset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Workingset</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Workingset</em>' attribute.
	 * @see #setWorkingset(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProperty_Workingset()
	 * @model
	 * @generated
	 */
	String getWorkingset();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.NtProperty#getWorkingset <em>Workingset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Workingset</em>' attribute.
	 * @see #getWorkingset()
	 * @generated
	 */
	void setWorkingset(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProperty_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.NtProperty#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Created</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Created</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Created</em>' attribute.
	 * @see #setCreated(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProperty_Created()
	 * @model
	 * @generated
	 */
	String getCreated();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.NtProperty#getCreated <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Created</em>' attribute.
	 * @see #getCreated()
	 * @generated
	 */
	void setCreated(String value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
	 * The list contents are of type {@link it.naturtalent.e4.project.model.project.DynPropertyItem}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Properties</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' containment reference list.
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProperty_Properties()
	 * @model containment="true"
	 * @generated
	 */
	EList<DynPropertyItem> getProperties();

} // NtProperty
