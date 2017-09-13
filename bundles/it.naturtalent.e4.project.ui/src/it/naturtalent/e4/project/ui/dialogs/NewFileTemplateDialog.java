package it.naturtalent.e4.project.ui.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.naturtalent.e4.project.ui.NtPreferences;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;


public class NewFileTemplateDialog extends Dialog
{
	private Table table;
	private TableViewer tableViewer;
	private String selectedTemplate;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public NewFileTemplateDialog(Shell parentShell)
	{
		super(parentShell);
	}
	
	@Override
	protected void configureShell(Shell newShell)
	{	
		super.configureShell(newShell);
		newShell.setText("Dateivorlagen");
	}


	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());

		return container;
	}
	
	public void setTemplates(Set<String> values)
	{
		tableViewer.setInput(values);
	}
	
	public String getSelectedTemplate()
	{
		return selectedTemplate;
	}
	
	@Override
	protected void okPressed()
	{
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		selectedTemplate = (String) selection.getFirstElement();		
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(306, 531);
	}

}
