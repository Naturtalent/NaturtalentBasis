/**
 */
package it.naturtalent.e4.project.model.project;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see it.naturtalent.e4.project.model.project.ProjectFactory
 * @model kind="package"
 * @generated
 */
public interface ProjectPackage extends EPackage
{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "project";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.example.org/project";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "project";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ProjectPackage eINSTANCE = it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl.init();

	/**
	 * The meta object id for the '{@link it.naturtalent.e4.project.model.project.impl.NtProjectImpl <em>Nt Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.naturtalent.e4.project.model.project.impl.NtProjectImpl
	 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getNtProject()
	 * @generated
	 */
	int NT_PROJECT = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECT__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECT__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECT__DESCRIPTION = 2;

	/**
	 * The number of structural features of the '<em>Nt Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECT_FEATURE_COUNT = 3;

	/**
	 * The operation id for the '<em>Has Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECT___HAS_NAME__DIAGNOSTICCHAIN_MAP = 0;

	/**
	 * The number of operations of the '<em>Nt Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECT_OPERATION_COUNT = 1;


	/**
	 * The meta object id for the '{@link it.naturtalent.e4.project.model.project.impl.NtProjectsImpl <em>Nt Projects</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.naturtalent.e4.project.model.project.impl.NtProjectsImpl
	 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getNtProjects()
	 * @generated
	 */
	int NT_PROJECTS = 1;

	/**
	 * The feature id for the '<em><b>Nt Project</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECTS__NT_PROJECT = 0;

	/**
	 * The number of structural features of the '<em>Nt Projects</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECTS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Nt Projects</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROJECTS_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link it.naturtalent.e4.project.model.project.NtProject <em>Nt Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nt Project</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProject
	 * @generated
	 */
	EClass getNtProject();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.NtProject#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProject#getId()
	 * @see #getNtProject()
	 * @generated
	 */
	EAttribute getNtProject_Id();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.NtProject#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProject#getName()
	 * @see #getNtProject()
	 * @generated
	 */
	EAttribute getNtProject_Name();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.NtProject#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProject#getDescription()
	 * @see #getNtProject()
	 * @generated
	 */
	EAttribute getNtProject_Description();

	/**
	 * Returns the meta object for the '{@link it.naturtalent.e4.project.model.project.NtProject#hasName(org.eclipse.emf.common.util.DiagnosticChain, java.util.Map) <em>Has Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Has Name</em>' operation.
	 * @see it.naturtalent.e4.project.model.project.NtProject#hasName(org.eclipse.emf.common.util.DiagnosticChain, java.util.Map)
	 * @generated
	 */
	EOperation getNtProject__HasName__DiagnosticChain_Map();

	/**
	 * Returns the meta object for class '{@link it.naturtalent.e4.project.model.project.NtProjects <em>Nt Projects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nt Projects</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProjects
	 * @generated
	 */
	EClass getNtProjects();

	/**
	 * Returns the meta object for the containment reference list '{@link it.naturtalent.e4.project.model.project.NtProjects#getNtProject <em>Nt Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nt Project</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProjects#getNtProject()
	 * @see #getNtProjects()
	 * @generated
	 */
	EReference getNtProjects_NtProject();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ProjectFactory getProjectFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals
	{
		/**
		 * The meta object literal for the '{@link it.naturtalent.e4.project.model.project.impl.NtProjectImpl <em>Nt Project</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.naturtalent.e4.project.model.project.impl.NtProjectImpl
		 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getNtProject()
		 * @generated
		 */
		EClass NT_PROJECT = eINSTANCE.getNtProject();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NT_PROJECT__ID = eINSTANCE.getNtProject_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NT_PROJECT__NAME = eINSTANCE.getNtProject_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NT_PROJECT__DESCRIPTION = eINSTANCE.getNtProject_Description();

		/**
		 * The meta object literal for the '<em><b>Has Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation NT_PROJECT___HAS_NAME__DIAGNOSTICCHAIN_MAP = eINSTANCE.getNtProject__HasName__DiagnosticChain_Map();

		/**
		 * The meta object literal for the '{@link it.naturtalent.e4.project.model.project.impl.NtProjectsImpl <em>Nt Projects</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.naturtalent.e4.project.model.project.impl.NtProjectsImpl
		 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getNtProjects()
		 * @generated
		 */
		EClass NT_PROJECTS = eINSTANCE.getNtProjects();

		/**
		 * The meta object literal for the '<em><b>Nt Project</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NT_PROJECTS__NT_PROJECT = eINSTANCE.getNtProjects_NtProject();

	}

} //ProjectPackage
