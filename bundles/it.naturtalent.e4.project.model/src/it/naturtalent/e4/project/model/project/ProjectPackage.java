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
	 * The meta object id for the '{@link it.naturtalent.e4.project.model.project.impl.DynPropertyItemImpl <em>Dyn Property Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.naturtalent.e4.project.model.project.impl.DynPropertyItemImpl
	 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getDynPropertyItem()
	 * @generated
	 */
	int DYN_PROPERTY_ITEM = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DYN_PROPERTY_ITEM__NAME = 0;

	/**
	 * The feature id for the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DYN_PROPERTY_ITEM__CLASS_NAME = 1;

	/**
	 * The number of structural features of the '<em>Dyn Property Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DYN_PROPERTY_ITEM_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Dyn Property Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DYN_PROPERTY_ITEM_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link it.naturtalent.e4.project.model.project.impl.NtPropertyImpl <em>Nt Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.naturtalent.e4.project.model.project.impl.NtPropertyImpl
	 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getNtProperty()
	 * @generated
	 */
	int NT_PROPERTY = 3;

	/**
	 * The feature id for the '<em><b>Workingset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROPERTY__WORKINGSET = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROPERTY__ID = 1;

	/**
	 * The feature id for the '<em><b>Created</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROPERTY__CREATED = 2;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROPERTY__PROPERTIES = 3;

	/**
	 * The number of structural features of the '<em>Nt Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROPERTY_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Nt Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NT_PROPERTY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl <em>Proxy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.naturtalent.e4.project.model.project.impl.ProxyImpl
	 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getProxy()
	 * @generated
	 */
	int PROXY = 4;

	/**
	 * The feature id for the '<em><b>In Use</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY__IN_USE = 0;

	/**
	 * The feature id for the '<em><b>Schemata</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY__SCHEMATA = 1;

	/**
	 * The feature id for the '<em><b>Host</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY__HOST = 2;

	/**
	 * The feature id for the '<em><b>Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY__PORT = 3;

	/**
	 * The feature id for the '<em><b>Authentification</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY__AUTHENTIFICATION = 4;

	/**
	 * The feature id for the '<em><b>User</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY__USER = 5;

	/**
	 * The feature id for the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY__PASSWORD = 6;

	/**
	 * The number of structural features of the '<em>Proxy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Proxy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link it.naturtalent.e4.project.model.project.impl.ProxiesImpl <em>Proxies</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.naturtalent.e4.project.model.project.impl.ProxiesImpl
	 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getProxies()
	 * @generated
	 */
	int PROXIES = 5;

	/**
	 * The feature id for the '<em><b>Proxies</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXIES__PROXIES = 0;

	/**
	 * The number of structural features of the '<em>Proxies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXIES_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Proxies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROXIES_OPERATION_COUNT = 0;

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
	 * Returns the meta object for class '{@link it.naturtalent.e4.project.model.project.DynPropertyItem <em>Dyn Property Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dyn Property Item</em>'.
	 * @see it.naturtalent.e4.project.model.project.DynPropertyItem
	 * @generated
	 */
	EClass getDynPropertyItem();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.DynPropertyItem#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see it.naturtalent.e4.project.model.project.DynPropertyItem#getName()
	 * @see #getDynPropertyItem()
	 * @generated
	 */
	EAttribute getDynPropertyItem_Name();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.DynPropertyItem#getClassName <em>Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class Name</em>'.
	 * @see it.naturtalent.e4.project.model.project.DynPropertyItem#getClassName()
	 * @see #getDynPropertyItem()
	 * @generated
	 */
	EAttribute getDynPropertyItem_ClassName();

	/**
	 * Returns the meta object for class '{@link it.naturtalent.e4.project.model.project.NtProperty <em>Nt Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nt Property</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProperty
	 * @generated
	 */
	EClass getNtProperty();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.NtProperty#getWorkingset <em>Workingset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Workingset</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProperty#getWorkingset()
	 * @see #getNtProperty()
	 * @generated
	 */
	EAttribute getNtProperty_Workingset();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.NtProperty#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProperty#getId()
	 * @see #getNtProperty()
	 * @generated
	 */
	EAttribute getNtProperty_Id();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.NtProperty#getCreated <em>Created</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Created</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProperty#getCreated()
	 * @see #getNtProperty()
	 * @generated
	 */
	EAttribute getNtProperty_Created();

	/**
	 * Returns the meta object for the containment reference list '{@link it.naturtalent.e4.project.model.project.NtProperty#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Properties</em>'.
	 * @see it.naturtalent.e4.project.model.project.NtProperty#getProperties()
	 * @see #getNtProperty()
	 * @generated
	 */
	EReference getNtProperty_Properties();

	/**
	 * Returns the meta object for class '{@link it.naturtalent.e4.project.model.project.Proxy <em>Proxy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Proxy</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy
	 * @generated
	 */
	EClass getProxy();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.Proxy#getSchemata <em>Schemata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schemata</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy#getSchemata()
	 * @see #getProxy()
	 * @generated
	 */
	EAttribute getProxy_Schemata();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.Proxy#getHost <em>Host</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Host</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy#getHost()
	 * @see #getProxy()
	 * @generated
	 */
	EAttribute getProxy_Host();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.Proxy#getPort <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Port</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy#getPort()
	 * @see #getProxy()
	 * @generated
	 */
	EAttribute getProxy_Port();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.Proxy#isAuthentification <em>Authentification</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Authentification</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy#isAuthentification()
	 * @see #getProxy()
	 * @generated
	 */
	EAttribute getProxy_Authentification();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.Proxy#getUser <em>User</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>User</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy#getUser()
	 * @see #getProxy()
	 * @generated
	 */
	EAttribute getProxy_User();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.Proxy#getPassword <em>Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Password</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy#getPassword()
	 * @see #getProxy()
	 * @generated
	 */
	EAttribute getProxy_Password();

	/**
	 * Returns the meta object for the attribute '{@link it.naturtalent.e4.project.model.project.Proxy#isInUse <em>In Use</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>In Use</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxy#isInUse()
	 * @see #getProxy()
	 * @generated
	 */
	EAttribute getProxy_InUse();

	/**
	 * Returns the meta object for class '{@link it.naturtalent.e4.project.model.project.Proxies <em>Proxies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Proxies</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxies
	 * @generated
	 */
	EClass getProxies();

	/**
	 * Returns the meta object for the reference list '{@link it.naturtalent.e4.project.model.project.Proxies#getProxies <em>Proxies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Proxies</em>'.
	 * @see it.naturtalent.e4.project.model.project.Proxies#getProxies()
	 * @see #getProxies()
	 * @generated
	 */
	EReference getProxies_Proxies();

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

		/**
		 * The meta object literal for the '{@link it.naturtalent.e4.project.model.project.impl.DynPropertyItemImpl <em>Dyn Property Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.naturtalent.e4.project.model.project.impl.DynPropertyItemImpl
		 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getDynPropertyItem()
		 * @generated
		 */
		EClass DYN_PROPERTY_ITEM = eINSTANCE.getDynPropertyItem();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DYN_PROPERTY_ITEM__NAME = eINSTANCE.getDynPropertyItem_Name();

		/**
		 * The meta object literal for the '<em><b>Class Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DYN_PROPERTY_ITEM__CLASS_NAME = eINSTANCE.getDynPropertyItem_ClassName();

		/**
		 * The meta object literal for the '{@link it.naturtalent.e4.project.model.project.impl.NtPropertyImpl <em>Nt Property</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.naturtalent.e4.project.model.project.impl.NtPropertyImpl
		 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getNtProperty()
		 * @generated
		 */
		EClass NT_PROPERTY = eINSTANCE.getNtProperty();

		/**
		 * The meta object literal for the '<em><b>Workingset</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NT_PROPERTY__WORKINGSET = eINSTANCE.getNtProperty_Workingset();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NT_PROPERTY__ID = eINSTANCE.getNtProperty_Id();

		/**
		 * The meta object literal for the '<em><b>Created</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NT_PROPERTY__CREATED = eINSTANCE.getNtProperty_Created();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NT_PROPERTY__PROPERTIES = eINSTANCE.getNtProperty_Properties();

		/**
		 * The meta object literal for the '{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl <em>Proxy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.naturtalent.e4.project.model.project.impl.ProxyImpl
		 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getProxy()
		 * @generated
		 */
		EClass PROXY = eINSTANCE.getProxy();

		/**
		 * The meta object literal for the '<em><b>Schemata</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROXY__SCHEMATA = eINSTANCE.getProxy_Schemata();

		/**
		 * The meta object literal for the '<em><b>Host</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROXY__HOST = eINSTANCE.getProxy_Host();

		/**
		 * The meta object literal for the '<em><b>Port</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROXY__PORT = eINSTANCE.getProxy_Port();

		/**
		 * The meta object literal for the '<em><b>Authentification</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROXY__AUTHENTIFICATION = eINSTANCE.getProxy_Authentification();

		/**
		 * The meta object literal for the '<em><b>User</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROXY__USER = eINSTANCE.getProxy_User();

		/**
		 * The meta object literal for the '<em><b>Password</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROXY__PASSWORD = eINSTANCE.getProxy_Password();

		/**
		 * The meta object literal for the '<em><b>In Use</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROXY__IN_USE = eINSTANCE.getProxy_InUse();

		/**
		 * The meta object literal for the '{@link it.naturtalent.e4.project.model.project.impl.ProxiesImpl <em>Proxies</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.naturtalent.e4.project.model.project.impl.ProxiesImpl
		 * @see it.naturtalent.e4.project.model.project.impl.ProjectPackageImpl#getProxies()
		 * @generated
		 */
		EClass PROXIES = eINSTANCE.getProxies();

		/**
		 * The meta object literal for the '<em><b>Proxies</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROXIES__PROXIES = eINSTANCE.getProxies_Proxies();

	}

} //ProjectPackage
