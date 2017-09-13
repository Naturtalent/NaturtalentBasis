package it.naturtalent.e4.project.expimp.dialogs;

import it.naturtalent.e4.project.expimp.Messages;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wb.swt.SWTResourceManager;

public class ImportExistProjects extends TitleAreaDialog
{
	private Table table;

	private List<String>existProjects;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ImportExistProjects(Shell parentShell, List<String>existFiles)
	{				
		super(parentShell);
		this.existProjects = existFiles;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitleImage(SWTResourceManager.getImage(ImportExistProjects.class, "/icons/full/wizban/import_wiz.png"));
		setMessage(Messages.ImportExistProjects_this_message);
		setTitle(Messages.ImportExistProjects_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblExistingProjects = new Label(container, SWT.NONE);
		lblExistingProjects.setText(Messages.ImportExistProjects_lblExistingProjects_text);
		
		TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider(){

			@Override
			public Image getImage(Object element)
			{
				// TODO Auto-generated method stub
				return SWTResourceManager.getImage(ImportExistProjects.class,
						"/icons/projekt.png");
			}
			
		});
		tableViewer.setInput(existProjects);
		
		

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
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 609);
	}

}
