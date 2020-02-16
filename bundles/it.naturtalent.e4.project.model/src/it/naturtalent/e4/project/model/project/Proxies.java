/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Proxies</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.Proxies#getProxies <em>Proxies</em>}</li>
 * </ul>
 *
 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxies()
 * @model
 * @generated
 */
public interface Proxies extends EObject
{
	/**
	 * Returns the value of the '<em><b>Proxies</b></em>' reference list.
	 * The list contents are of type {@link it.naturtalent.e4.project.model.project.Proxy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Proxies</em>' reference list.
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#getProxies_Proxies()
	 * @model
	 * @generated
	 */
	EList<Proxy> getProxies();

} // Proxies
