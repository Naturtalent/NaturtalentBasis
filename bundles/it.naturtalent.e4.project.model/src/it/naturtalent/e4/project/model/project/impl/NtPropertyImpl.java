/**
 */
package it.naturtalent.e4.project.model.project.impl;

import it.naturtalent.e4.project.model.project.DynPropertyItem;
import it.naturtalent.e4.project.model.project.NtProperty;
import it.naturtalent.e4.project.model.project.ProjectPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Nt Property</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.NtPropertyImpl#getWorkingset <em>Workingset</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.NtPropertyImpl#getId <em>Id</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.NtPropertyImpl#getCreated <em>Created</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.NtPropertyImpl#getProperties <em>Properties</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NtPropertyImpl extends MinimalEObjectImpl.Container implements NtProperty
{
	/**
	 * The default value of the '{@link #getWorkingset() <em>Workingset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkingset()
	 * @generated
	 * @ordered
	 */
	protected static final String WORKINGSET_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWorkingset() <em>Workingset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkingset()
	 * @generated
	 * @ordered
	 */
	protected String workingset = WORKINGSET_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getCreated() <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreated()
	 * @generated
	 * @ordered
	 */
	protected static final String CREATED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreated() <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreated()
	 * @generated
	 * @ordered
	 */
	protected String created = CREATED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<DynPropertyItem> properties;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NtPropertyImpl()
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
		return ProjectPackage.Literals.NT_PROPERTY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getWorkingset()
	{
		return workingset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWorkingset(String newWorkingset)
	{
		String oldWorkingset = workingset;
		workingset = newWorkingset;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.NT_PROPERTY__WORKINGSET, oldWorkingset, workingset));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId)
	{
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.NT_PROPERTY__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCreated()
	{
		return created;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCreated(String newCreated)
	{
		String oldCreated = created;
		created = newCreated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.NT_PROPERTY__CREATED, oldCreated, created));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DynPropertyItem> getProperties()
	{
		if (properties == null)
		{
			properties = new EObjectContainmentEList<DynPropertyItem>(DynPropertyItem.class, this, ProjectPackage.NT_PROPERTY__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case ProjectPackage.NT_PROPERTY__PROPERTIES:
				return ((InternalEList<?>)getProperties()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
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
			case ProjectPackage.NT_PROPERTY__WORKINGSET:
				return getWorkingset();
			case ProjectPackage.NT_PROPERTY__ID:
				return getId();
			case ProjectPackage.NT_PROPERTY__CREATED:
				return getCreated();
			case ProjectPackage.NT_PROPERTY__PROPERTIES:
				return getProperties();
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
			case ProjectPackage.NT_PROPERTY__WORKINGSET:
				setWorkingset((String)newValue);
				return;
			case ProjectPackage.NT_PROPERTY__ID:
				setId((String)newValue);
				return;
			case ProjectPackage.NT_PROPERTY__CREATED:
				setCreated((String)newValue);
				return;
			case ProjectPackage.NT_PROPERTY__PROPERTIES:
				getProperties().clear();
				getProperties().addAll((Collection<? extends DynPropertyItem>)newValue);
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
			case ProjectPackage.NT_PROPERTY__WORKINGSET:
				setWorkingset(WORKINGSET_EDEFAULT);
				return;
			case ProjectPackage.NT_PROPERTY__ID:
				setId(ID_EDEFAULT);
				return;
			case ProjectPackage.NT_PROPERTY__CREATED:
				setCreated(CREATED_EDEFAULT);
				return;
			case ProjectPackage.NT_PROPERTY__PROPERTIES:
				getProperties().clear();
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
			case ProjectPackage.NT_PROPERTY__WORKINGSET:
				return WORKINGSET_EDEFAULT == null ? workingset != null : !WORKINGSET_EDEFAULT.equals(workingset);
			case ProjectPackage.NT_PROPERTY__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ProjectPackage.NT_PROPERTY__CREATED:
				return CREATED_EDEFAULT == null ? created != null : !CREATED_EDEFAULT.equals(created);
			case ProjectPackage.NT_PROPERTY__PROPERTIES:
				return properties != null && !properties.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (workingset: ");
		result.append(workingset);
		result.append(", id: ");
		result.append(id);
		result.append(", created: ");
		result.append(created);
		result.append(')');
		return result.toString();
	}

} //NtPropertyImpl
