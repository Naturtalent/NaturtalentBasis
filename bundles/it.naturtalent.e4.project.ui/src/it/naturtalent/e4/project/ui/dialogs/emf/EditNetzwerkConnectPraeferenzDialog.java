package it.naturtalent.e4.project.ui.dialogs.emf;

import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.model.project.Proxy;

public class EditNetzwerkConnectPraeferenzDialog extends TitleAreaDialog
{
	
	// das zueditierende Objekt 
	private Proxy proxy;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public EditNetzwerkConnectPraeferenzDialog(Shell parentShell)
	{
		super(parentShell);
	}
	
	public EditNetzwerkConnectPraeferenzDialog(Shell parentShell, Proxy proxy)
	{
		super(parentShell);
		this.proxy = proxy;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setMessage("Proxies für Netzwerkverbindungen definieren");
		setTitle("Netzwerkproxies");
		Composite area = (Composite) super.createDialogArea(parent);		

		try
		{
			// FootNote im Dialog bearbeiten
			ECPSWTViewRenderer.INSTANCE.render(area, proxy);			
			
		} catch (ECPRendererException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
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
		return new Point(500, 500);
	}

}
