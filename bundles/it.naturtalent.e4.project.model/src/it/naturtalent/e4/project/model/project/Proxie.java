/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Proxie</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxie#getSchemata <em>Schemata</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxie#getHost <em>Host</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxie#getPort <em>Port</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxie#getAuth <em>Auth</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxie#getPassword <em>Password</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxie#isInUse <em>In Use</em>}</li>
 * </ul>
 *
 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxie()
 * @model
 * @generated
 */
public interface Proxie extends EObject
{
	/**
	 * Returns the value of the '<em><b>Schemata</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schemata</em>' attribute.
	 * @see #setSchemata(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxie_Schemata()
	 * @model
	 * @generated
	 */
	String getSchemata();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxie#getSchemata <em>Schemata</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schemata</em>' attribute.
	 * @see #getSchemata()
	 * @generated
	 */
	void setSchemata(String value);

	/**
	 * Returns the value of the '<em><b>Host</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Host</em>' attribute.
	 * @see #setHost(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxie_Host()
	 * @model
	 * @generated
	 */
	String getHost();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxie#getHost <em>Host</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Host</em>' attribute.
	 * @see #getHost()
	 * @generated
	 */
	void setHost(String value);

	/**
	 * Returns the value of the '<em><b>Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Port</em>' attribute.
	 * @see #setPort(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxie_Port()
	 * @model
	 * @generated
	 */
	String getPort();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxie#getPort <em>Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Port</em>' attribute.
	 * @see #getPort()
	 * @generated
	 */
	void setPort(String value);

	/**
	 * Returns the value of the '<em><b>Auth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Auth</em>' attribute.
	 * @see #setAuth(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxie_Auth()
	 * @model
	 * @generated
	 */
	String getAuth();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxie#getAuth <em>Auth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Auth</em>' attribute.
	 * @see #getAuth()
	 * @generated
	 */
	void setAuth(String value);

	/**
	 * Returns the value of the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Password</em>' attribute.
	 * @see #setPassword(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxie_Password()
	 * @model
	 * @generated
	 */
	String getPassword();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxie#getPassword <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Password</em>' attribute.
	 * @see #getPassword()
	 * @generated
	 */
	void setPassword(String value);

	/**
	 * Returns the value of the '<em><b>In Use</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>In Use</em>' attribute.
	 * @see #setInUse(boolean)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxie_InUse()
	 * @model
	 * @generated
	 */
	boolean isInUse();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxie#isInUse <em>In Use</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>In Use</em>' attribute.
	 * @see #isInUse()
	 * @generated
	 */
	void setInUse(boolean value);

} // Proxie
