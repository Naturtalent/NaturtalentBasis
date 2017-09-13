package it.naturtalent.emf.model;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DefaultReferenceDialog extends TitleAreaDialog
{
	
	protected EObject element = null;
	protected ViewModelContext modelContext = null;
	protected String title = "Element Dialog"; //$NON-NLS-N$
	
	private Map<String, Object>attributeMap;
	
	public DefaultReferenceDialog(Shell parentShell)
	{
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{		
		attributeMap = EMFModelUtils.saveAttributes(element);		
		this.setTitle(title);
				
		Composite area = null;
		try
		{
			area = (Composite) super.createDialogArea(parent);
			Composite container = new Composite(area, SWT.NONE);			
			container.setLayout(new GridLayout(1, false));
			container.setLayoutData(new GridData(GridData.FILL_BOTH));	
				
			if(modelContext != null)
				ECPSWTViewRenderer.INSTANCE.render(container, modelContext);
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return area;
	}

	@Override
	protected void cancelPressed()
	{		
		EMFModelUtils.reloadAttributes(element, attributeMap);
		super.cancelPressed();
	}
	


}
