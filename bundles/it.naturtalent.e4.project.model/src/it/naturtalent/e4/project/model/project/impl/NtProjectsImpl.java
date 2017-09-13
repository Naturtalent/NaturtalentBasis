/**
 */
package it.naturtalent.e4.project.model.project.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.model.project.ProjectPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Nt Projects</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.NtProjectsImpl#getNtProject <em>Nt Project</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NtProjectsImpl extends MinimalEObjectImpl.Container implements NtProjects
{
	/**
	 * The cached value of the '{@link #getNtProject() <em>Nt Project</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNtProject()
	 * @generated
	 * @ordered
	 */
	protected EList<NtProject> ntProject;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NtProjectsImpl()
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
		return ProjectPackage.Literals.NT_PROJECTS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<NtProject> getNtProject()
	{
		if (ntProject == null)
		{
			ntProject = new EObjectContainmentEList<NtProject>(NtProject.class, this, ProjectPackage.NT_PROJECTS__NT_PROJECT);
		}
		return ntProject;
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
			case ProjectPackage.NT_PROJECTS__NT_PROJECT:
				return ((InternalEList<?>)getNtProject()).basicRemove(otherEnd, msgs);
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
			case ProjectPackage.NT_PROJECTS__NT_PROJECT:
				return getNtProject();
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
			case ProjectPackage.NT_PROJECTS__NT_PROJECT:
				getNtProject().clear();
				getNtProject().addAll((Collection<? extends NtProject>)newValue);
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
			case ProjectPackage.NT_PROJECTS__NT_PROJECT:
				getNtProject().clear();
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
			case ProjectPackage.NT_PROJECTS__NT_PROJECT:
				return ntProject != null && !ntProject.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //NtProjectsImpl
