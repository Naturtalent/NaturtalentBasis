package it.naturtalent.e4.project.ui.dialogs;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.ui.utils.FileModifiedComposite;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

/**
 * Dialog zur Ueberpruefung der Aenderungszeitpunkte an Project, Folder, File - Resourcen.
 * Wann wurde eine Resource erstellt bzw. zuletzt geandert.
 * Das Erstellungsdatum ist in Linux identisch mit lastModify
 * 
 * @author dieter
 *
 */
public class ResourcePropertyDialog extends TitleAreaDialog
{
	
	// Composite zur Darstellung der Eigenschaften
	private FileModifiedComposite fileModifiedComposite;
	
	private Button btnClipboard;
	
	private IResource resource;
	
	// der benutzte Zwischenspeicher
	private Clipboard clipboard;

	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ResourcePropertyDialog(Shell parentShell)
	{
		super(parentShell);
		
		// Clipboard aktivieren
		clipboard = new Clipboard(Display.getDefault());
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitle("Dateieigenschaften");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// eingenes Composite zur Darstellung der Modifyeigenschaften
		fileModifiedComposite = new FileModifiedComposite(container, SWT.NONE);
		fileModifiedComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		btnClipboard = new Button(composite, SWT.NONE);
		btnClipboard.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				clipboard.setContents(new Object[]
						{resource.getLocation().toOSString()}, new Transfer[]
								{ TextTransfer.getInstance() });
			}
		});
		btnClipboard.setText("");
		btnClipboard.setImage(Icon.COMMAND_COPY.getImage(IconSize._16x16_DefaultIconSize));
		new Label(container, SWT.NONE);
				
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
		return new Point(627, 300);
	}
	
	public void setResource(IResource resource)
	{
		if(fileModifiedComposite != null)
		{
			this.resource = resource;
			
			// Files koennen nicht in den Clipboard kopiert werden
			if(resource.getType() == IResource.FILE)	
				btnClipboard.setEnabled(false);
				
			fileModifiedComposite.setResource(resource);
		}
	}
	
	@Override
	public boolean close()
	{
		if (clipboard != null)
		{
			clipboard.dispose();
			clipboard = null;
		}

		return super.close();
	}

}
