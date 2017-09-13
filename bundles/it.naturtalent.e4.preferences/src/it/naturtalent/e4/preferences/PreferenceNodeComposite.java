package it.naturtalent.e4.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import it.naturtalent.application.IPreferenceAdapter;
import it.naturtalent.application.IPreferenceNode;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * Bildet den Rahmen in den die individuelle Detailseite eingestellt wird.
 *  
 * @author dieter
 *
 */
public class PreferenceNodeComposite extends Composite implements IPreferenceNode
{
	private Composite nodeComposite;
	private Label titleNode;
	private Composite parentNode;	
	private IPreferenceAdapter preferenceAdapter;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreferenceNodeComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		titleNode = new Label(this, SWT.NONE);
		titleNode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		titleNode.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.BOLD));
		titleNode.setText("Title");
		
		Label lblHeader = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblHeader.setText("Header");
		
		parentNode = new Composite(this, SWT.NONE);
		parentNode.setLayout(new GridLayout(1, false));
		parentNode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		nodeComposite = new Composite(parentNode, SWT.NONE);
		nodeComposite.setLayout(new GridLayout(1, false));
		nodeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeButton = new Composite(this, SWT.NONE);
		compositeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeButton.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button btnDefault = new Button(compositeButton, SWT.NONE);
		btnDefault.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(preferenceAdapter != null)
					preferenceAdapter.restoreDefaultPressed();
			}
		});
		btnDefault.setText("Restore Default");
		
		Button btnApply = new Button(compositeButton, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(preferenceAdapter != null)
					preferenceAdapter.appliedPressed();
			}
		});
		btnApply.setText("Apply");
		
		Label lblFooter = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblFooter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblFooter.setText("footer");

	}
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}	
	
	public IPreferenceAdapter getPreferenceAdapter()
	{
		return preferenceAdapter;
	}

	public void setPreferenceAdapter(
			IPreferenceAdapter preferenceAdapter)
	{
		this.preferenceAdapter = preferenceAdapter;
	}

	public void refresh()
	{
		nodeComposite.pack(true);
	}

	public Composite getNodeComposite()
	{
		return nodeComposite;
	}

	public void setNodeComposite(Composite nodeComposite)
	{
		this.nodeComposite = nodeComposite;
	}

	public String getTitle()
	{
		return titleNode.getText();
	}

	public void setTitle(String title)
	{
		titleNode.setText(title);
	}

	public void setGroupNode(Group groupNode)
	{
		this.parentNode = groupNode;
	}

	public Composite getParentNode()
	{
		return parentNode;
	}
}
