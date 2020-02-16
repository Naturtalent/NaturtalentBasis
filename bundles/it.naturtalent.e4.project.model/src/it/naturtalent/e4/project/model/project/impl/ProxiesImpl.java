/**
 */
package it.naturtalent.e4.project.model.project.impl;

import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.model.project.Proxies;

import it.naturtalent.e4.project.model.project.Proxy;
import java.util.Collection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Proxies</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxiesImpl#getProxies <em>Proxies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProxiesImpl extends MinimalEObjectImpl.Container implements Proxies
{
	/**
	 * The cached value of the '{@link #getProxies() <em>Proxies</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProxies()
	 * @generated
	 * @ordered
	 */
	protected EList<Proxy> proxies;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProxiesImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return ProjectPackage.Literals.PROXIES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Proxy> getProxies()
	{
		if (proxies == null)
		{
			proxies = new EObjectResolvingEList<Proxy>(Proxy.class, this, ProjectPackage.PROXIES__PROXIES);
		}
		return proxies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case ProjectPackage.PROXIES__PROXIES:
				return getProxies();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case ProjectPackage.PROXIES__PROXIES:
				getProxies().clear();
				getProxies().addAll((Collection<? extends Proxy>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case ProjectPackage.PROXIES__PROXIES:
				getProxies().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case ProjectPackage.PROXIES__PROXIES:
				return proxies != null && !proxies.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ProxiesImpl
