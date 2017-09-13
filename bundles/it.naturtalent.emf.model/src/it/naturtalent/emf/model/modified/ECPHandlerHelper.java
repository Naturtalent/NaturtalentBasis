package it.naturtalent.emf.model.modified;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.emf.ecp.internal.wizards.FilterModelElementWizard;
import org.eclipse.emf.ecp.spi.common.ui.CheckedModelClassComposite;
import org.eclipse.emf.ecp.spi.common.ui.SelectModelElementWizard;
import org.eclipse.emf.ecp.spi.common.ui.composites.SelectionComposite;
import org.eclipse.emf.ecp.spi.core.InternalProject;
import org.eclipse.emf.ecp.ui.common.ECPCompositeFactory;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ECPHandlerHelper
{
	/**
	 * 
	 * eigene Klasse, wegen modifizierter CheckedSelectModelClassCompositeImpl()
	 * 
	 * This method allows the user to filter the visible packages and classes.
	 *
	 * @param ecpProject
	 *            the project to filter
	 * @param shell
	 *            the {@link Shell} to use for UI
	 */
	public static void filterProjectPackages(final ECPProject ecpProject,
			final Shell shell)
	{
		final Set<EPackage> ePackages = ECPUtil.getAllRegisteredEPackages();

		/*
		final CheckedModelClassComposite checkedModelComposite = ECPCompositeFactory
				.getCheckedModelClassComposite(ePackages);
				*/

		
		final CheckedModelClassComposite checkedModelComposite = new CheckedSelectModelClassCompositeImpl(new HashSet<EPackage>(), ePackages, new HashSet<EClass>());

		
		
		final Set<Object> initialSelectionSet = new HashSet<Object>();
		initialSelectionSet
				.addAll(((InternalProject) ecpProject).getVisiblePackages());
		initialSelectionSet
				.addAll(((InternalProject) ecpProject).getVisibleEClasses());
		checkedModelComposite
				.setInitialSelection(initialSelectionSet.toArray());

		final FilterModelElementWizard wizard = new FilterModelElementWizard();
		wizard.setCompositeProvider(checkedModelComposite);
		final WizardDialog wd = new WizardDialog(shell, wizard);

		final int wizardResult = wd.open();
		if (wizardResult == Window.OK)
		{
			final Object[] dialogSelection = checkedModelComposite.getChecked();
			final Set<EPackage> filtererdPackages = new HashSet<EPackage>();
			final Set<EClass> filtererdEClasses = new HashSet<EClass>();
			for (final Object object : dialogSelection)
			{
				if (object instanceof EPackage)
				{
					filtererdPackages.add((EPackage) object);
				}
				else if (object instanceof EClass)
				{
					final EClass eClass = (EClass) object;
					if (!filtererdPackages.contains(eClass.getEPackage()))
					{
						filtererdEClasses.add(eClass);
					}
				}
			}
			((InternalProject) ecpProject)
					.setVisiblePackages(filtererdPackages);
			((InternalProject) ecpProject)
					.setVisibleEClasses(filtererdEClasses);
		}
	}
	
	/**
	 * Add a new {@link EObject} to the root of an {@link ECPProject}.
	 *
	 * @param ecpProject the {@link ECPProject} to add the {@link EObject} to
	 * @param shell the {@link Shell} used to display the UI
	 * @param open whether to open the corresponding editor or not
	 * @return the created {@link EObject}
	 */
	public static EObject addModelElement(final ECPProject ecpProject, final Shell shell, boolean open) {
		final EClass newMEType = openSelectModelElementWizard(ecpProject, shell, open);
		if (ecpProject != null && newMEType != null) {
			// create ME
			final EObject newMEInstance = createModelElementInstance(newMEType);
			ecpProject.getEditingDomain().getCommandStack().execute(new ChangeCommand(newMEInstance) {

				@Override
				protected void doExecute() {
					ecpProject.getContents().add(newMEInstance);
				}
			});
			
			/*
			if (open) {
				openModelElement(newMEInstance, ecpProject);
			}
			*/
			
			return newMEInstance;
		}
		return null;
	}
	
	/**
	 * @param ecpProject
	 * @param shell
	 * @param open
	 * @return
	 */
	private static EClass openSelectModelElementWizard(final ECPProject ecpProject, final Shell shell, boolean open) {
		final SelectionComposite<TreeViewer> helper = ECPCompositeFactory.getSelectModelClassComposite(ecpProject);
		final SelectModelElementWizard wizard = new SelectModelElementWizard(
			
			"WindowPage","PageName","PageTitle","Description"	
				
			/*	
			Messages.NewModelElementWizardHandler_Title,
			Messages.NewModelElementWizard_WizardTitle_AddModelElement,
			Messages.NewModelElementWizard_PageTitle_AddModelElement,
			Messages.NewModelElementWizard_PageDescription_AddModelElement
			*/
			
			);
		
		
		wizard.setCompositeProvider(helper);
		final WizardDialog wd = new WizardDialog(shell, wizard);

		final int wizardResult = wd.open();
		if (wizardResult == Window.OK) {
			final Object[] selection = helper.getSelection();
			if (selection != null && selection.length > 0) {
				return (EClass) selection[0];
			}
		}
		return null;
	}
	
	/**
	 * @param newMEType
	 * @return
	 */
	private static EObject createModelElementInstance(final EClass newMEType)
	{
		final EPackage ePackage = newMEType.getEPackage();
		final EObject newMEInstance = ePackage.getEFactoryInstance()
				.create(newMEType);
		return newMEInstance;
	}

}
