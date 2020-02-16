/**
 */
package it.naturtalent.e4.project.model.project.impl;

import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.model.project.Proxy;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Proxy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl#isInUse <em>In Use</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl#getSchemata <em>Schemata</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl#getHost <em>Host</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl#getPort <em>Port</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl#isAuthentification <em>Authentification</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl#getUser <em>User</em>}</li>
 *   <li>{@link it.naturtalent.e4.project.model.project.impl.ProxyImpl#getPassword <em>Password</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProxyImpl extends MinimalEObjectImpl.Container implements Proxy
{
	/**
	 * The default value of the '{@link #isInUse() <em>In Use</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInUse()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IN_USE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isInUse() <em>In Use</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInUse()
	 * @generated
	 * @ordered
	 */
	protected boolean inUse = IN_USE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSchemata() <em>Schemata</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchemata()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEMATA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSchemata() <em>Schemata</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchemata()
	 * @generated
	 * @ordered
	 */
	protected String schemata = SCHEMATA_EDEFAULT;

	/**
	 * The default value of the '{@link #getHost() <em>Host</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHost()
	 * @generated
	 * @ordered
	 */
	protected static final String HOST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHost() <em>Host</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHost()
	 * @generated
	 * @ordered
	 */
	protected String host = HOST_EDEFAULT;

	/**
	 * The default value of the '{@link #getPort() <em>Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPort()
	 * @generated
	 * @ordered
	 */
	protected static final String PORT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPort() <em>Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPort()
	 * @generated
	 * @ordered
	 */
	protected String port = PORT_EDEFAULT;

	/**
	 * The default value of the '{@link #isAuthentification() <em>Authentification</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAuthentification()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AUTHENTIFICATION_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAuthentification() <em>Authentification</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAuthentification()
	 * @generated
	 * @ordered
	 */
	protected boolean authentification = AUTHENTIFICATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getUser() <em>User</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUser()
	 * @generated
	 * @ordered
	 */
	protected static final String USER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUser() <em>User</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUser()
	 * @generated
	 * @ordered
	 */
	protected String user = USER_EDEFAULT;

	/**
	 * The default value of the '{@link #getPassword() <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPassword()
	 * @generated
	 * @ordered
	 */
	protected static final String PASSWORD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPassword()
	 * @generated
	 * @ordered
	 */
	protected String password = PASSWORD_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProxyImpl()
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
		return ProjectPackage.Literals.PROXY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSchemata()
	{
		return schemata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSchemata(String newSchemata)
	{
		String oldSchemata = schemata;
		schemata = newSchemata;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROXY__SCHEMATA, oldSchemata, schemata));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getHost()
	{
		return host;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHost(String newHost)
	{
		String oldHost = host;
		host = newHost;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROXY__HOST, oldHost, host));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPort()
	{
		return port;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPort(String newPort)
	{
		String oldPort = port;
		port = newPort;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROXY__PORT, oldPort, port));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isAuthentification()
	{
		return authentification;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAuthentification(boolean newAuthentification)
	{
		boolean oldAuthentification = authentification;
		authentification = newAuthentification;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROXY__AUTHENTIFICATION, oldAuthentification, authentification));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getUser()
	{
		return user;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUser(String newUser)
	{
		String oldUser = user;
		user = newUser;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROXY__USER, oldUser, user));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPassword()
	{
		return password;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPassword(String newPassword)
	{
		String oldPassword = password;
		password = newPassword;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROXY__PASSWORD, oldPassword, password));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isInUse()
	{
		return inUse;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInUse(boolean newInUse)
	{
		boolean oldInUse = inUse;
		inUse = newInUse;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROXY__IN_USE, oldInUse, inUse));
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
			case ProjectPackage.PROXY__IN_USE:
				return isInUse();
			case ProjectPackage.PROXY__SCHEMATA:
				return getSchemata();
			case ProjectPackage.PROXY__HOST:
				return getHost();
			case ProjectPackage.PROXY__PORT:
				return getPort();
			case ProjectPackage.PROXY__AUTHENTIFICATION:
				return isAuthentification();
			case ProjectPackage.PROXY__USER:
				return getUser();
			case ProjectPackage.PROXY__PASSWORD:
				return getPassword();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case ProjectPackage.PROXY__IN_USE:
				setInUse((Boolean)newValue);
				return;
			case ProjectPackage.PROXY__SCHEMATA:
				setSchemata((String)newValue);
				return;
			case ProjectPackage.PROXY__HOST:
				setHost((String)newValue);
				return;
			case ProjectPackage.PROXY__PORT:
				setPort((String)newValue);
				return;
			case ProjectPackage.PROXY__AUTHENTIFICATION:
				setAuthentification((Boolean)newValue);
				return;
			case ProjectPackage.PROXY__USER:
				setUser((String)newValue);
				return;
			case ProjectPackage.PROXY__PASSWORD:
				setPassword((String)newValue);
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
			case ProjectPackage.PROXY__IN_USE:
				setInUse(IN_USE_EDEFAULT);
				return;
			case ProjectPackage.PROXY__SCHEMATA:
				setSchemata(SCHEMATA_EDEFAULT);
				return;
			case ProjectPackage.PROXY__HOST:
				setHost(HOST_EDEFAULT);
				return;
			case ProjectPackage.PROXY__PORT:
				setPort(PORT_EDEFAULT);
				return;
			case ProjectPackage.PROXY__AUTHENTIFICATION:
				setAuthentification(AUTHENTIFICATION_EDEFAULT);
				return;
			case ProjectPackage.PROXY__USER:
				setUser(USER_EDEFAULT);
				return;
			case ProjectPackage.PROXY__PASSWORD:
				setPassword(PASSWORD_EDEFAULT);
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
			case ProjectPackage.PROXY__IN_USE:
				return inUse != IN_USE_EDEFAULT;
			case ProjectPackage.PROXY__SCHEMATA:
				return SCHEMATA_EDEFAULT == null ? schemata != null : !SCHEMATA_EDEFAULT.equals(schemata);
			case ProjectPackage.PROXY__HOST:
				return HOST_EDEFAULT == null ? host != null : !HOST_EDEFAULT.equals(host);
			case ProjectPackage.PROXY__PORT:
				return PORT_EDEFAULT == null ? port != null : !PORT_EDEFAULT.equals(port);
			case ProjectPackage.PROXY__AUTHENTIFICATION:
				return authentification != AUTHENTIFICATION_EDEFAULT;
			case ProjectPackage.PROXY__USER:
				return USER_EDEFAULT == null ? user != null : !USER_EDEFAULT.equals(user);
			case ProjectPackage.PROXY__PASSWORD:
				return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
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
		result.append(" (inUse: ");
		result.append(inUse);
		result.append(", schemata: ");
		result.append(schemata);
		result.append(", host: ");
		result.append(host);
		result.append(", port: ");
		result.append(port);
		result.append(", authentification: ");
		result.append(authentification);
		result.append(", user: ");
		result.append(user);
		result.append(", password: ");
		result.append(password);
		result.append(')');
		return result.toString();
	}

} //ProxyImpl
