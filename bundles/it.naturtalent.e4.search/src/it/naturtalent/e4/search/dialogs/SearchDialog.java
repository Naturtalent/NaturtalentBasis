package it.naturtalent.e4.search.dialogs;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.search.IPage;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.search.Activator;
import it.naturtalent.e4.search.ProjectSearchPage;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;

/**
 * Zentraler Such-Dialog
 * 
 * @author apel.dieter
 * 
 */
public class SearchDialog extends TitleAreaDialog
{

	//private IResourceNavigator resourceNavigator;
	
	private ProgressMonitorPart progressMonitorPart;

	private int PREFERRED_CONTENT_HEIGHT = 450;

	private int PREFERRED_CONTENT_WIDTH = 650;

	private ISearchInEclipsePage page;

	private TabFolder tabSearchFolder;

	private Text textMask;
	private Composite composite;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	
	public SearchDialog()
	{
		super(Display.getDefault().getActiveShell());		
	}
	
	public SearchDialog(Shell parentShell)
	{
		super(parentShell);		
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		GridData containerLayoutData = new GridData(GridData.FILL_BOTH);
		containerLayoutData.heightHint = PREFERRED_CONTENT_HEIGHT;
		containerLayoutData.widthHint = PREFERRED_CONTENT_WIDTH;
		container.setLayoutData(containerLayoutData);

		tabSearchFolder = new TabFolder(container, SWT.NONE);
		GridData gd_tabSearchFolder = new GridData(GridData.FILL_BOTH);
		gd_tabSearchFolder.grabExcessVerticalSpace = false;
		gd_tabSearchFolder.heightHint = 216;
		tabSearchFolder.setLayoutData(gd_tabSearchFolder);

		if (Activator.searchPageregistry != null)
		{
			ISearchInEclipsePage[] pages = Activator.searchPageregistry
					.getSearchPages();
			
			for (int i = 0; i < pages.length; i++)
			{
				ISearchInEclipsePage page = pages[i]; 
						
				TabItem tabItem = new TabItem(tabSearchFolder, SWT.NONE);
				tabItem.setText(page.getLabel());

				Control partControl = page.createControl(tabSearchFolder);
				tabItem.setControl(partControl);
			}
		}
		
		composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		
		progressMonitorPart = new ProgressMonitorPart(container,
				new GridLayout(), true);
		progressMonitorPart
				.setLayoutData(new GridData(GridData.FILL_BOTH));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
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
		return new Point(450, 545);
	}


	@Override
	protected void okPressed()
	{
		startSearch();
		
		ResourceSearchResult result = (ResourceSearchResult) page.getResult();
		if(result.getHitCount() > 0)
		{
			// Treffer! - Dialog beenden
			super.okPressed();
		}
	}
	
	public ISearchInEclipsePage getSearchPage()
	{
		return page;
	}

	/*
	 * die 'performSearch' - Funktion der Suchseite aufrufen
	 */
	public void startSearch()
	{
		page = getSelectedPage();
		page.performSearch(progressMonitorPart);		
	}


	private ISearchInEclipsePage getSelectedPage()
	{
		int index = tabSearchFolder.getSelectionIndex();
		if (index == -1)
		{
			// ???
			return Activator.searchPageregistry.getSearchPages()[0];
		}
		return Activator.searchPageregistry.getSearchPages()[index];
	}

}
