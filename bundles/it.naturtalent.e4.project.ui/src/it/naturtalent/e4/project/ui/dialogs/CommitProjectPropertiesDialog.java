package it.naturtalent.e4.project.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;

/**
 * Zustimmung zum endgueltigen Entfernen von Eigenschaften einholen.
 * 
 * @author dieter
 *
 */
public class CommitProjectPropertiesDialog extends TitleAreaDialog
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
			return element.toString();
		}
	}
	
	private TableViewer tableViewer;
	private Table table;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public CommitProjectPropertiesDialog(Shell parentShell)
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
		setMessage("Sollen die angezeigten Eigenschaften unwideruflich gelöscht werden ?");
		setTitle("Entfernen bestehender Eigenschaften");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		Button button_1 = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		button_1.setText("Ja");
		Button button = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button.setText("Nein");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(373, 481);
	}

	/**
	 * 
	 * @param propertyFactories
	 */
	public void setPropertyFactories(String ntProjectID, List<INtProjectPropertyFactory> propertyFactories)
	{
		if(propertyFactories != null)
		{
			List<String>propertyNames = new ArrayList<String>();
			
			for(INtProjectPropertyFactory propertyFactory : propertyFactories)
			{
				INtProjectProperty property = propertyFactory.createNtProjektData();
				property.setNtProjectID(ntProjectID);				
				if(property.getNtProjectID() != null)
				{
					property.getNtPropertyData();
					propertyNames.add(property.toString());
				}
			}
			tableViewer.setInput(propertyNames);
		}
	}
	
	
}
