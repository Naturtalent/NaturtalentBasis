/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see it.naturtalent.e4.project.model.project.ProjectPackage
 * @generated
 */
public interface ProjectFactory extends EFactory
{
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ProjectFactory eINSTANCE = it.naturtalent.e4.project.model.project.impl.ProjectFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Nt Project</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nt Project</em>'.
	 * @generated
	 */
	NtProject createNtProject();

	/**
	 * Returns a new object of class '<em>Nt Projects</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nt Projects</em>'.
	 * @generated
	 */
	NtProjects createNtProjects();

	/**
	 * Returns a new object of class '<em>Dyn Property Item</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Dyn Property Item</em>'.
	 * @generated
	 */
	DynPropertyItem createDynPropertyItem();

	/**
	 * Returns a new object of class '<em>Nt Property</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nt Property</em>'.
	 * @generated
	 */
	NtProperty createNtProperty();

	/**
	 * Returns a new object of class '<em>Proxy</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Proxy</em>'.
	 * @generated
	 */
	Proxy createProxy();

	/**
	 * Returns a new object of class '<em>Proxies</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Proxies</em>'.
	 * @generated
	 */
	Proxies createProxies();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ProjectPackage getProjectPackage();

} //ProjectFactory
