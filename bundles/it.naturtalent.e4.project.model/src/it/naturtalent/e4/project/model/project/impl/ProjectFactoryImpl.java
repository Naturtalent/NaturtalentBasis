/**
 */
package it.naturtalent.e4.project.model.project.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import it.naturtalent.e4.project.model.project.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ProjectFactoryImpl extends EFactoryImpl implements ProjectFactory
{
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ProjectFactory init()
	{
		try
		{
			ProjectFactory theProjectFactory = (ProjectFactory)EPackage.Registry.INSTANCE.getEFactory(ProjectPackage.eNS_URI);
			if (theProjectFactory != null)
			{
				return theProjectFactory;
			}
		}
		catch (Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ProjectFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProjectFactoryImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass)
	{
		switch (eClass.getClassifierID())
		{
			case ProjectPackage.NT_PROJECT: return createNtProject();
			case ProjectPackage.NT_PROJECTS: return createNtProjects();
			case ProjectPackage.DYN_PROPERTY_ITEM: return createDynPropertyItem();
			case ProjectPackage.NT_PROPERTY: return createNtProperty();
			case ProjectPackage.PROXY: return createProxy();
			case ProjectPackage.PROXIES: return createProxies();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NtProject createNtProject()
	{
		NtProjectImpl ntProject = new NtProjectImpl();
		return ntProject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NtProjects createNtProjects()
	{
		NtProjectsImpl ntProjects = new NtProjectsImpl();
		return ntProjects;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DynPropertyItem createDynPropertyItem()
	{
		DynPropertyItemImpl dynPropertyItem = new DynPropertyItemImpl();
		return dynPropertyItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NtProperty createNtProperty()
	{
		NtPropertyImpl ntProperty = new NtPropertyImpl();
		return ntProperty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Proxy createProxy()
	{
		ProxyImpl proxy = new ProxyImpl();
		return proxy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Proxies createProxies()
	{
		ProxiesImpl proxies = new ProxiesImpl();
		return proxies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProjectPackage getProjectPackage()
	{
		return (ProjectPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ProjectPackage getPackage()
	{
		return ProjectPackage.eINSTANCE;
	}

} //ProjectFactoryImpl
