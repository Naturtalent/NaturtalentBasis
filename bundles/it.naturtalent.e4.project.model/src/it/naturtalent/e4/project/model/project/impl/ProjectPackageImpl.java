/**
 */
package it.naturtalent.e4.project.model.project.impl;

import it.naturtalent.e4.project.model.project.DynPropertyItem;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.model.project.NtProperty;
import it.naturtalent.e4.project.model.project.ProjectFactory;
import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.model.project.util.ProjectValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ProjectPackageImpl extends EPackageImpl implements ProjectPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ntProjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ntProjectsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dynPropertyItemEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ntPropertyEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see it.naturtalent.e4.project.model.project.ProjectPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ProjectPackageImpl()
	{
		super(eNS_URI, ProjectFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link ProjectPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ProjectPackage init()
	{
		if (isInited) return (ProjectPackage)EPackage.Registry.INSTANCE.getEPackage(ProjectPackage.eNS_URI);

		// Obtain or create and register package
		ProjectPackageImpl theProjectPackage = (ProjectPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ProjectPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ProjectPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theProjectPackage.createPackageContents();

		// Initialize created meta-data
		theProjectPackage.initializePackageContents();

		// Register package validator
		EValidator.Registry.INSTANCE.put
			(theProjectPackage, 
			 new EValidator.Descriptor()
			 {
				 public EValidator getEValidator()
				 {
					 return ProjectValidator.INSTANCE;
				 }
			 });

		// Mark meta-data to indicate it can't be changed
		theProjectPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ProjectPackage.eNS_URI, theProjectPackage);
		return theProjectPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNtProject()
	{
		return ntProjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNtProject_Id()
	{
		return (EAttribute)ntProjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNtProject_Name()
	{
		return (EAttribute)ntProjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNtProject_Description()
	{
		return (EAttribute)ntProjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EOperation getNtProject__HasName__DiagnosticChain_Map()
	{
		return ntProjectEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNtProjects()
	{
		return ntProjectsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNtProjects_NtProject()
	{
		return (EReference)ntProjectsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDynPropertyItem()
	{
		return dynPropertyItemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDynPropertyItem_Name()
	{
		return (EAttribute)dynPropertyItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDynPropertyItem_ClassName()
	{
		return (EAttribute)dynPropertyItemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNtProperty()
	{
		return ntPropertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNtProperty_Workingset()
	{
		return (EAttribute)ntPropertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNtProperty_Id()
	{
		return (EAttribute)ntPropertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNtProperty_Created()
	{
		return (EAttribute)ntPropertyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNtProperty_Properties()
	{
		return (EReference)ntPropertyEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProjectFactory getProjectFactory()
	{
		return (ProjectFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents()
	{
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		ntProjectEClass = createEClass(NT_PROJECT);
		createEAttribute(ntProjectEClass, NT_PROJECT__ID);
		createEAttribute(ntProjectEClass, NT_PROJECT__NAME);
		createEAttribute(ntProjectEClass, NT_PROJECT__DESCRIPTION);
		createEOperation(ntProjectEClass, NT_PROJECT___HAS_NAME__DIAGNOSTICCHAIN_MAP);

		ntProjectsEClass = createEClass(NT_PROJECTS);
		createEReference(ntProjectsEClass, NT_PROJECTS__NT_PROJECT);

		dynPropertyItemEClass = createEClass(DYN_PROPERTY_ITEM);
		createEAttribute(dynPropertyItemEClass, DYN_PROPERTY_ITEM__NAME);
		createEAttribute(dynPropertyItemEClass, DYN_PROPERTY_ITEM__CLASS_NAME);

		ntPropertyEClass = createEClass(NT_PROPERTY);
		createEAttribute(ntPropertyEClass, NT_PROPERTY__WORKINGSET);
		createEAttribute(ntPropertyEClass, NT_PROPERTY__ID);
		createEAttribute(ntPropertyEClass, NT_PROPERTY__CREATED);
		createEReference(ntPropertyEClass, NT_PROPERTY__PROPERTIES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents()
	{
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(ntProjectEClass, NtProject.class, "NtProject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNtProject_Id(), ecorePackage.getEString(), "id", "", 0, 1, NtProject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNtProject_Name(), ecorePackage.getEString(), "name", "", 0, 1, NtProject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNtProject_Description(), ecorePackage.getEString(), "description", null, 0, 1, NtProject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = initEOperation(getNtProject__HasName__DiagnosticChain_Map(), ecorePackage.getEBoolean(), "hasName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEDiagnosticChain(), "chain", 0, 1, IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(ecorePackage.getEMap());
		EGenericType g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(ntProjectsEClass, NtProjects.class, "NtProjects", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNtProjects_NtProject(), this.getNtProject(), null, "NtProject", null, 0, -1, NtProjects.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dynPropertyItemEClass, DynPropertyItem.class, "DynPropertyItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDynPropertyItem_Name(), ecorePackage.getEString(), "name", null, 0, 1, DynPropertyItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDynPropertyItem_ClassName(), ecorePackage.getEString(), "className", null, 0, 1, DynPropertyItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ntPropertyEClass, NtProperty.class, "NtProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNtProperty_Workingset(), ecorePackage.getEString(), "workingset", null, 0, 1, NtProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNtProperty_Id(), ecorePackage.getEString(), "id", null, 0, 1, NtProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNtProperty_Created(), ecorePackage.getEString(), "created", null, 0, 1, NtProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNtProperty_Properties(), this.getDynPropertyItem(), null, "properties", null, 0, -1, NtProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //ProjectPackageImpl
