/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Proxy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxy#isInUse <em>In Use</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxy#getSchemata <em>Schemata</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxy#getHost <em>Host</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxy#getPort <em>Port</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxy#isAuthentification <em>Authentification</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxy#getUser <em>User</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxy#getPassword <em>Password</em>}</li>
 * </ul>
 *
 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy()
 * @model
 * @generated
 */
public interface Proxy extends EObject
{
	/**
	 * Returns the value of the '<em><b>Schemata</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schemata</em>' attribute.
	 * @see #setSchemata(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy_Schemata()
	 * @model
	 * @generated
	 */
	String getSchemata();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxy#getSchemata <em>Schemata</em>}' attribute.
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
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy_Host()
	 * @model
	 * @generated
	 */
	String getHost();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxy#getHost <em>Host</em>}' attribute.
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
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy_Port()
	 * @model
	 * @generated
	 */
	String getPort();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxy#getPort <em>Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Port</em>' attribute.
	 * @see #getPort()
	 * @generated
	 */
	void setPort(String value);

	/**
	 * Returns the value of the '<em><b>Authentification</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Authentification</em>' attribute.
	 * @see #setAuthentification(boolean)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy_Authentification()
	 * @model
	 * @generated
	 */
	boolean isAuthentification();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxy#isAuthentification <em>Authentification</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Authentification</em>' attribute.
	 * @see #isAuthentification()
	 * @generated
	 */
	void setAuthentification(boolean value);

	/**
	 * Returns the value of the '<em><b>User</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User</em>' attribute.
	 * @see #setUser(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy_User()
	 * @model
	 * @generated
	 */
	String getUser();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxy#getUser <em>User</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User</em>' attribute.
	 * @see #getUser()
	 * @generated
	 */
	void setUser(String value);

	/**
	 * Returns the value of the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Password</em>' attribute.
	 * @see #setPassword(String)
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy_Password()
	 * @model
	 * @generated
	 */
	String getPassword();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxy#getPassword <em>Password</em>}' attribute.
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
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxy_InUse()
	 * @model
	 * @generated
	 */
	boolean isInUse();

	/**
	 * Sets the value of the '{@link it.naturtalent.e4.project.model.project.Proxy#isInUse <em>In Use</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>In Use</em>' attribute.
	 * @see #isInUse()
	 * @generated
	 */
	void setInUse(boolean value);

} // Proxy
