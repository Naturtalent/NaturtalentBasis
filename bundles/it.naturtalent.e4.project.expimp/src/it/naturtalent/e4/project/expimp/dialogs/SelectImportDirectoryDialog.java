package it.naturtalent.e4.project.expimp.dialogs;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;

import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Wurde beim Projektimport ein Verzeichnis ausgewaehlt, dass mehrere Sub-ExpImportVerzeichnisse enthaelt kann mit diesem
 * Dialog eins selektiert werden.
 *   
 * @author dieter
 *
 */
public class SelectImportDirectoryDialog extends TitleAreaDialog
{
	private class TableLabelProvider extends LabelProvider
			implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof File)
			{
				File file = (File) element;
				return file.getPath();
			}
			return element.toString();
		}
	}
	
	private Table table;
	private TableViewer tableViewer;
	private File selectedDir;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectImportDirectoryDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitleImage(Icon.WIZBAN_IMPORT.getImage(IconSize._75x66_TitleDialogIconSize));
		setMessage("Von mehreren Importverzeichnissen muss eins ausgewählt werden.");
		setTitle("Projekte importieren");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
				Object selObj = selection.getFirstElement();
				if (selObj instanceof File)				
					selectedDir = (File) selObj;
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());

		return area;
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
		return new Point(450, 500);
	}
	
	public void setImportDirectories(List<File>importDirectories)
	{
		tableViewer.setInput(importDirectories);
	}

	public File getSelectedDir()
	{
		return selectedDir;
	}

	

}
