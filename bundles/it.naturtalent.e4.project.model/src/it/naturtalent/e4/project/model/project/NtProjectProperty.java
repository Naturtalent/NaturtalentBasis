/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Nt Project Property</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.NtProjectProperty#getNtproject <em>Ntproject</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.NtProjectProperty#getNtproperty <em>Ntproperty</em>}</li>
 * </ul>
 *
 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProjectProperty()
 * @model
 * @generated
 */
public interface NtProjectProperty extends EObject
{
	/**
	 * Returns the value of the '<em><b>Ntproject</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ntproject</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ntproject</em>' containment reference.
	 * @see #setNtproject(NtProject)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProjectProperty_Ntproject()
	 * @model containment="true"
	 * @generated
	 */
	NtProject getNtproject();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.NtProjectProperty#getNtproject <em>Ntproject</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ntproject</em>' containment reference.
	 * @see #getNtproject()
	 * @generated
	 */
	void setNtproject(NtProject value);

	/**
	 * Returns the value of the '<em><b>Ntproperty</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ntproperty</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ntproperty</em>' containment reference.
	 * @see #setNtproperty(NtProperty)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getNtProjectProperty_Ntproperty()
	 * @model containment="true"
	 * @generated
	 */
	NtProperty getNtproperty();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.NtProjectProperty#getNtproperty <em>Ntproperty</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ntproperty</em>' containment reference.
	 * @see #getNtproperty()
	 * @generated
	 */
	void setNtproperty(NtProperty value);

} // NtProjectProperty
