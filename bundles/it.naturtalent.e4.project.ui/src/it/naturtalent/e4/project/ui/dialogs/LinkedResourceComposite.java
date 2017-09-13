package it.naturtalent.e4.project.ui.dialogs;

import it.naturtalent.e4.project.ui.Messages;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class LinkedResourceComposite extends Composite
{
	private Text linkTargetField;
	
	private int resourceType = IResource.FOLDER;

	//private String linkTarget = ""; //$NON-NLS-1$
	
	private Button radioLink;
	
	private Button btnBrowseLink;
	
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	public static final String LINKTARGETPROPERTY = "LINKTARGETPROPERTY"; 
	private String linkTargetSelection;
		
	public String getLinkTargetSelection()
	{
		return linkTargetSelection;
	}
	public void setLinkTargetSelection(String linkTargetSelection)
	{
		propertyChangeSupport.firePropertyChange(LINKTARGETPROPERTY,				
				this.linkTargetSelection,this.linkTargetSelection = linkTargetSelection);
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LinkedResourceComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Button radioDefault = new Button(this, SWT.RADIO);
		radioDefault.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				btnBrowseLink.setEnabled(false);
			}
		});
		radioDefault.setSelection(true);
		radioDefault.setText("Default Filesystem (Naturtalent Workspace)");
		new Label(this, SWT.NONE);
		
		radioLink = new Button(this, SWT.RADIO);
		radioLink.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				btnBrowseLink.setEnabled(true);
			}
		});
		radioLink.setText("link auf alternatives Filesystem (Link Ressource)");
		new Label(this, SWT.NONE);
		
		linkTargetField = new Text(this, SWT.BORDER);
		linkTargetField.setEnabled(false);
		linkTargetField.setEditable(false);
		linkTargetField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnBrowseLink = new Button(this, SWT.NONE);
		btnBrowseLink.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				handleLinkTargetBrowseButtonPressed();
			}
		});
		btnBrowseLink.setText("Browse");
		btnBrowseLink.setEnabled(false);
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Opens a file or directory browser depending on the link type.
	 */
	private void handleLinkTargetBrowseButtonPressed()
	{
		if (resourceType == IResource.FILE)
		{
			FileDialog dialog = new FileDialog(linkTargetField.getShell(),
					SWT.SHEET);
			dialog.setText(Messages.LinkedResourceComposite_targetSelectionLabel);
			setLinkTargetSelection(dialog.open());
		}
		else
		{
			DirectoryDialog dialog = new DirectoryDialog(
					linkTargetField.getShell(), SWT.SHEET);
			dialog.setMessage(Messages.LinkedResourceComposite_targetSelectionLabel);
			String filePath = linkTargetField.getText(); 
			if(StringUtils.isNotEmpty(filePath))
				dialog.setFilterPath(filePath);
			
			setLinkTargetSelection(dialog.open());
		}

		if (linkTargetSelection != null)
		{
			linkTargetField.setText(linkTargetSelection);
		}
	}
	

	public String getLinkTarget()
	{
		if (radioLink.getSelection())
		{
			if (linkTargetField != null
					&& linkTargetField.isDisposed() == false)
				return linkTargetField.getText();
		}
		
		return "";
	}

	public void setLinkTarget(String linkTarget)
	{		
		if (linkTargetField != null && linkTargetField.isDisposed() == false)
			linkTargetField.setText(linkTarget);
	}
	public void setType(int resourceType)
	{
		this.resourceType = resourceType;
	} 
	
	

}
