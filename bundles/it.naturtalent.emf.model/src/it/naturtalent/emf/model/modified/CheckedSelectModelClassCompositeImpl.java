package it.naturtalent.emf.model.modified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecp.internal.common.ui.MessageKeys;
import org.eclipse.emf.ecp.spi.common.ui.CheckedModelClassComposite;
import org.eclipse.emf.ecp.spi.common.ui.composites.AbstractEClassTreeSelectionComposite;
import org.eclipse.emfforms.spi.localization.LocalizationServiceHelper;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Modifiziert 'org.eclipse.emf.ecp.spi.common.ui.composites.CheckedSelectModelClassCompositeImpl)
 * 
 * 
 * This class provides a CheckedTree that allows the user to select {@link EPackage EPackages} and {@link EClass
 * EClasses}.
 *
 * @author Eugen Neufeld
 *
 */
public class CheckedSelectModelClassCompositeImpl extends AbstractEClassTreeSelectionComposite implements
	CheckedModelClassComposite {
	private Object[] checked;

	private Object[] initialSelection;

	/**
	 * Constructor setting the necessary data for selecting the {@link EClass EClasses}.
	 *
	 * @param unsupportedEPackages {@link EPackage EPackages} that are not supported
	 * @param filteredEPackages {@link EPackage EPackages} selected by the user
	 * @param filteredEClasses {@link EClass EClasses} selected by the user
	 */
	public CheckedSelectModelClassCompositeImpl(Collection<EPackage> unsupportedEPackages,
		Collection<EPackage> filteredEPackages, Collection<EClass> filteredEClasses) {
		super(unsupportedEPackages, filteredEPackages, filteredEClasses);
	}

	@Override
	protected boolean isCheckedTree() {
		return true;
	}

	@Override
	public Composite createUI(Composite parent) {
		final Composite composite = super.createUI(parent);
		final Composite buttons = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).applyTo(buttons);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(2, 1).applyTo(buttons);

		final Button buttonAll = new Button(buttons, SWT.PUSH);
		buttonAll.setText(LocalizationServiceHelper.getString(getClass(),
			MessageKeys.CheckedModelElementHelper_SelectAllLabel));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(buttonAll);
		buttonAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getViewer().setCheckedElements(
					((IStructuredContentProvider) getViewer().getContentProvider()).getElements(new Object()));
				setChecked();
			}
		});
		final Button buttonNone = new Button(buttons, SWT.PUSH);
		buttonNone.setText(LocalizationServiceHelper.getString(getClass(),
			MessageKeys.CheckedModelElementHelper_DeselectAllLabel));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(buttonNone);
		buttonNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getViewer().setCheckedElements(new Object[0]);
				setChecked();
			}
		});
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setChecked();
			}
		});

		if (initialSelection != null) {
			getViewer().setCheckedElements(initialSelection);
			
			//
			// !!! Modifikation
			// verhindert 'getChecked() = null', passiert wenn Dialog aufgerufen wird
			// und ohne aktive CheckAktion mit Ok beendet wird
			//
			checked = initialSelection;
		}
		return composite;
	}

	/**
	 * @return the treeViewer
	 */
	@Override
	public CheckboxTreeViewer getViewer() {
		return (CheckboxTreeViewer) super.getViewer();
	}

	private void setChecked() {
		final List<Object> objects = new ArrayList<Object>(Arrays.asList(getViewer().getCheckedElements()));
		objects.removeAll(Arrays.asList(getViewer().getGrayedElements()));
		checked = objects.toArray();
	}

	/**
	 * Returns the checked Elements.
	 *
	 * @return an array containing the checked elements
	 */
	@Override
	public Object[] getChecked() {
		return checked;

	}

	/**
	 * Initialize the selection by setting the checked elements.
	 *
	 * @param selection the objects to check
	 */
	@Override
	public void setInitialSelection(Object[] selection) {
		if (getViewer() != null) {
			getViewer().setCheckedElements(selection);
		} else {
			initialSelection = selection;
		}
	}
}
